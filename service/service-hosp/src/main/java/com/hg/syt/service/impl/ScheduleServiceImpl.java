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

        //获取当前页,和每页记录数
        int page = StringUtils.isEmpty( map.get( "page" ) ) ? 1 : Integer.parseInt( ( String ) map.get( "page" ) );
        int limit = StringUtils.isEmpty( map.get( "limit" ) ) ? 5 : Integer.parseInt( ( String ) map.get( "limit" ) );

        //TODO 使用 DepartmentQueryVo 而不直接使用 Department ??
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode( ( String ) map.get( "hoscode" ) );
        // DepartmentQueryVo 转 Department ??
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties( scheduleQueryVo,schedule );
        schedule.setIsDeleted( 0 );

        //分页构造器
        PageRequest pageRequest = PageRequest.of( page - 1, limit );
        //创建ExampleMatcher对象
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

        // 根据医院编号 和 科室编号 查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // 根据工作日workDate期进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate") //对分组后的workDate字段起别名workDate
                        // 统计号源数量
                        .count().as("docCount")  //查出来数量后给这个字段起个别名叫docCount
                        .sum("reservedNumber").as("reservedNumber")  //"reservedNumber"和"availableNumber"字段值相加
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort( Sort.Direction.ASC,"workDate"),
                //4 实现分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //调用方法，最终执行
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        //把日期对应星期获取
        for(BookingScheduleRuleVo bookingScheduleRuleVo:bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //设置最终数据，进行返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);

        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);

        return result;
    }

    @Override
    public List<Schedule> getDetailSchedule( String hoscode, String depcode, String workDate ) {

        //根据参数查询mongodb
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //把得到list集合遍历，向设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    private Schedule packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }

    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}


