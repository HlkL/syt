package com.hg.syt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hg.syt.model.hosp.Schedule;
import com.hg.syt.repository.ScheduleRepository;
import com.hg.syt.service.DepartmentService;
import com.hg.syt.service.HospitalService;
import com.hg.syt.service.ScheduleService;
import com.hg.syt.vo.hosp.BookingScheduleRuleVo;
import com.hg.syt.vo.hosp.ScheduleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-13 01:50
 */
@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private HospitalService hospitalService;

    @Resource
    private ScheduleRepository scheduleRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private DepartmentService departmentService;


    @Override
    public void saveSchedule( HttpServletRequest request ) {
        Map<String, Object> map = hospitalService.signatureVerification( request );

        String mapStr = JSONObject.toJSONString( map );
        Schedule schedule = JSONObject.parseObject( mapStr, Schedule.class );

        Schedule departmentExist = scheduleRepository
                .getScheduleByHoscodeAndHosScheduleId( schedule.getHoscode(),schedule.getHosScheduleId() );

        if ( departmentExist != null ) {
            schedule.setId( departmentExist.getId() );
            schedule.setCreateTime( departmentExist.getCreateTime() );
        } else {
            schedule.setCreateTime( new Date() );
        }
        schedule.setUpdateTime( new Date() );
        schedule.setIsDeleted( 0 );
        scheduleRepository.save( schedule );
    }

    @Override
    public Page findPageSchedule( HttpServletRequest request ) {

        Map<String, Object> map = hospitalService.signatureVerification( request );

        //???????????????,??????????????????
        int page = StringUtils.isEmpty( map.get( "page" ) ) ? 1 : Integer.parseInt( ( String ) map.get( "page" ) );
        int limit = StringUtils.isEmpty( map.get( "limit" ) ) ? 5 : Integer.parseInt( ( String ) map.get( "limit" ) );

        //TODO ?????? DepartmentQueryVo ?????????????????? Department ??
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode( ( String ) map.get( "hoscode" ) );
        // DepartmentQueryVo ??? Department ??
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties( scheduleQueryVo,schedule );
        schedule.setIsDeleted( 0 );

        //???????????????
        PageRequest pageRequest = PageRequest.of( page - 1, limit );
        //??????ExampleMatcher??????
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
                .withIgnoreCase( true );

        Example<Schedule> example = Example.of( schedule, exampleMatcher );

        return scheduleRepository.findAll( example, pageRequest );
    }

    @Override
    public void removeSchedule( HttpServletRequest request ) {
        Map<String, Object> map = hospitalService.signatureVerification( request );
        String hoscode = ( String ) map.get( "hoscode" );
        String hosScheduleId = ( String ) map.get( "hosScheduleId" );

        Schedule schedule = scheduleRepository
                .getScheduleByHoscodeAndHosScheduleId( hoscode,hosScheduleId );
        if ( schedule != null ) {
            scheduleRepository.deleteById( schedule.getId() );
        }
    }

    @Override
    public Map<String, Object> getRuleSchedule( Integer page, Integer limit,
                                                String hoscode, String depcode ) {

        // ?????????????????? ??? ???????????? ??????
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // ???????????????workDate???????????????
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),//????????????
                Aggregation.group("workDate")//????????????
                        .first("workDate").as("workDate") //???????????????workDate???????????????workDate
                        // ??????????????????
                        .count().as("docCount")  //????????????????????????????????????????????????docCount
                        .sum("reservedNumber").as("reservedNumber")  //"reservedNumber"???"availableNumber"???????????????
                        .sum("availableNumber").as("availableNumber"),
                //??????
                Aggregation.sort( Sort.Direction.ASC,"workDate"),
                //4 ????????????
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //???????????????????????????
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        //???????????????????????????
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        //???????????????????????????
        for(BookingScheduleRuleVo bookingScheduleRuleVo:bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //?????????????????????????????????
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);

        //??????????????????
        String hosName = hospitalService.getHospName(hoscode);
        //??????????????????
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);

        return result;
    }

    @Override
    public List<Schedule> getDetailSchedule( String hoscode, String depcode, String workDate ) {

        //??????????????????mongodb
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //?????????list????????????????????????????????????????????????????????????????????????????????????
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    private Schedule packageSchedule(Schedule schedule) {
        //??????????????????
        schedule.getParam().put("hosname",hospitalService.getHospName(schedule.getHoscode()));
        //??????????????????
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //????????????????????????
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }

    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "??????";
            default:
                break;
        }
        return dayOfWeek;
    }

}


