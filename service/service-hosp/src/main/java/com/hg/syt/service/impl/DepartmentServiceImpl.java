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

        //???????????????,??????????????????
        int page = StringUtils.isEmpty( map.get( "page" ) ) ? 1 : Integer.parseInt( ( String ) map.get( "page" ) );
        int limit = StringUtils.isEmpty( map.get( "limit" ) ) ? 5 : Integer.parseInt( ( String ) map.get( "limit" ) );

        //TODO ?????? DepartmentQueryVo ?????????????????? Department ??
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode( ( String ) map.get( "hoscode" ) );
        // DepartmentQueryVo ??? Department ??
        Department department = new Department();
        BeanUtils.copyProperties( departmentQueryVo,department );
        department.setIsDeleted( 0 );

        //???????????????
        PageRequest pageRequest = PageRequest.of( page - 1  , limit );
        //??????ExampleMatcher??????
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

            //?????????????????????????????? ?????????????????? hoscode ?????????
//            Schedule schedule = scheduleRepository
//                    .getScheduleByHoscode( hoscode );
//            if ( schedule != null ) {
//                scheduleRepository.deleteById( schedule.getId() );
//            }
        }
    }

    @Override
    public List<DepartmentVo> queryAllDeptByHospCode( String hoscode ) {
        //????????????
        List<DepartmentVo> result = new ArrayList<>();

        //????????????????????????????????????????????????
//        Department departmentQuery = new Department();
//        departmentQuery.setHoscode( hoscode );
//        Example<Department> example = Example.of( departmentQuery );
//        //??????????????????
//        List<Department> departmentAll = departmentRepository.findAll( example );

        List<Department> departmentAll = departmentRepository.getDepartmentByHoscode( hoscode );

        //????????????????????? bigcode ??????,??????????????????????????????????????????
        Map<String, List<Department>> departmentMap =
                departmentAll.stream().collect( Collectors.groupingBy( Department::getBigcode ) );

        //??????map
        for ( Map.Entry<String, List<Department>> entry: departmentMap.entrySet() ){
            //?????????????????????
            String bigCode = entry.getKey();
            //?????????????????????????????????????????????
            List<Department> departments = entry.getValue();

            //???????????????
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode( bigCode );
            departmentVo.setDepname( departments.get( 0 ).getDepname() );

            //???????????????
            List<DepartmentVo> departmentChild = new ArrayList<>();
            departments.forEach( child->{
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode( child.getDepcode() );
                departmentVo1.setDepname( child.getDepname() );
                //??????????????????
                departmentChild.add( departmentVo1 );
            } );

            //???????????????????????????????????????
            departmentVo.setChildren( departmentChild );

            //????????????????????????????????????
            result.add( departmentVo );
        }

        return result;
    }

    @Override
    public Object getDepName( String hoscode, String depcode ) {
        Department department =
                departmentRepository.getDepartmentByHoscodeAndDepcode( hoscode, depcode );

        if ( department != null ) {
            return department.getDepname();
        }
        return null;
    }
}


