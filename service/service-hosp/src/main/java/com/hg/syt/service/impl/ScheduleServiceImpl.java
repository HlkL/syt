package com.hg.syt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hg.syt.model.hosp.Department;
import com.hg.syt.model.hosp.Schedule;
import com.hg.syt.repository.ScheduleRepository;
import com.hg.syt.service.HospitalService;
import com.hg.syt.service.ScheduleService;
import com.hg.syt.vo.hosp.DepartmentQueryVo;
import com.hg.syt.vo.hosp.ScheduleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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
}


