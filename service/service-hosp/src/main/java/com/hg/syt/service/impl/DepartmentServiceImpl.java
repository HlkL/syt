package com.hg.syt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hg.syt.model.hosp.Department;
import com.hg.syt.model.hosp.Schedule;
import com.hg.syt.repository.DepartmentRepository;
import com.hg.syt.repository.ScheduleRepository;
import com.hg.syt.service.DepartmentService;
import com.hg.syt.service.HospitalService;
import com.hg.syt.vo.hosp.DepartmentQueryVo;
import com.hg.syt.vo.hosp.DepartmentVo;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-12 23:39
 */
@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentRepository departmentRepository;

    @Resource
    private HospitalService hospitalService;

    @Resource
    private ScheduleRepository scheduleRepository;

    @Override
    public void saveDepartment( HttpServletRequest request ) {
        Map<String, Object> map = hospitalService.signatureVerification( request );

        String mapStr = JSONObject.toJSONString( map );
        Department department = JSONObject.parseObject( mapStr, Department.class );

        Department departmentExist = departmentRepository
                .getDepartmentByHoscodeAndDepcode( department.getHoscode(),department.getDepcode() );

        if ( departmentExist != null ) {
            department.setId( departmentExist.getId() );
            department.setCreateTime( departmentExist.getCreateTime() );
        } else {
            department.setCreateTime( new Date() );
        }
        department.setUpdateTime( new Date() );
        department.setIsDeleted( 0 );
        departmentRepository.save( department );
    }

    @Override
    public Page findPageDepartment( HttpServletRequest request ) {
        Map<String, Object> map = hospitalService.signatureVerification( request );

        //获取当前页,和每页记录数
        int page = StringUtils.isEmpty( map.get( "page" ) ) ? 1 : Integer.parseInt( ( String ) map.get( "page" ) );
        int limit = StringUtils.isEmpty( map.get( "limit" ) ) ? 5 : Integer.parseInt( ( String ) map.get( "limit" ) );

        //TODO 使用 DepartmentQueryVo 而不直接使用 Department ??
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode( ( String ) map.get( "hoscode" ) );
        // DepartmentQueryVo 转 Department ??
        Department department = new Department();
        BeanUtils.copyProperties( departmentQueryVo,department );
        department.setIsDeleted( 0 );

        //分页构造器
        PageRequest pageRequest = PageRequest.of( page - 1  , limit );
        //创建ExampleMatcher对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
                .withIgnoreCase( true );

        Example<Department> example = Example.of( department, exampleMatcher );

        return departmentRepository.findAll( example, pageRequest );
    }

    @Override
    public void removeDepartment( HttpServletRequest request ) {
        Map<String, Object> map = hospitalService.signatureVerification( request );
        String hoscode = ( String ) map.get( "hoscode" );
        String depcode = ( String ) map.get( "depcode" );
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode( hoscode, depcode );
        if ( department != null ) {
            departmentRepository.deleteById( department.getId() );

            //删除科室医生排班信息 本业务所有的 hoscode 都一样
//            Schedule schedule = scheduleRepository
//                    .getScheduleByHoscode( hoscode );
//            if ( schedule != null ) {
//                scheduleRepository.deleteById( schedule.getId() );
//            }
        }
    }

    @Override
    public List<DepartmentVo> queryAllDeptByHospCode( String hoscode ) {
        //封装数据
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号查询医院所有科室信息
//        Department departmentQuery = new Department();
//        departmentQuery.setHoscode( hoscode );
//        Example<Department> example = Example.of( departmentQuery );
//        //查询科室列表
//        List<Department> departmentAll = departmentRepository.findAll( example );

        List<Department> departmentAll = departmentRepository.getDepartmentByHoscode( hoscode );

        //根据大科室编号 bigcode 分组,获取每个大科室里面的下级科室
        Map<String, List<Department>> departmentMap =
                departmentAll.stream().collect( Collectors.groupingBy( Department::getBigcode ) );

        //遍历map
        for ( Map.Entry<String, List<Department>> entry: departmentMap.entrySet() ){
            //获取大科室编号
            String bigCode = entry.getKey();
            //大科室编号对应的全部小科室信息
            List<Department> departments = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode( bigCode );
            departmentVo.setDepname( departments.get( 0 ).getDepname() );

            //封装小科室
            List<DepartmentVo> departmentChild = new ArrayList<>();
            departments.forEach( child->{
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode( child.getDepcode() );
                departmentVo1.setDepname( child.getDepname() );
                //封装到集合中
                departmentChild.add( departmentVo1 );
            } );

            //将小科室数据封装到大科室中
            departmentVo.setChildren( departmentChild );

            //将所有数据放入集合中返回
            result.add( departmentVo );
        }

        return result;
    }
}


