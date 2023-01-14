package com.hg.syt.controller.api;

import com.hg.common.result.Result;
import com.hg.syt.model.hosp.Department;
import com.hg.syt.model.hosp.Hospital;
import com.hg.syt.service.DepartmentService;
import com.hg.syt.service.HospitalService;
import com.hg.syt.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-12 17:37
 */
@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Resource
    private HospitalService hospitalService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ScheduleService scheduleService;

    @ApiOperation( value = "上传医院信息")
    @PostMapping("/saveHospital")
    public Result saveHosp( HttpServletRequest request ){
        hospitalService.save( request );
        return Result.ok( "上传成功" );
    }

    @ApiOperation( value = "获取医院信息")
    @PostMapping("/hospital/show")
    public Result getHosp( HttpServletRequest request ){
        Hospital hospital= hospitalService.getHosp( request );
        return Result.ok( hospital );
    }

    @ApiOperation( value = "上传科室信息")
    @PostMapping("/saveDepartment")
    public Result saveDepartment( HttpServletRequest request ){
        departmentService.saveDepartment( request );
        return Result.ok( "上传成功");
    }

    @ApiOperation( value = "查询科室信息")
    @PostMapping("/department/list")
    public Result findDepartment( HttpServletRequest request ){
        Page page = departmentService.findPageDepartment( request );
        return Result.ok( page );
    }

    @ApiOperation( value = "删除科室信息")
    @PostMapping("/department/remove")
    public Result removeDepartment( HttpServletRequest request ){
        departmentService.removeDepartment( request );
        return Result.ok( "删除成功" );
    }


    @ApiOperation( value = "上传排班信息")
    @PostMapping("/saveSchedule")
    public Result saveSchedule( HttpServletRequest request ){
        scheduleService.saveSchedule( request );
        return Result.ok( "上传成功" );
    }

    @ApiOperation( value = "查询排班信息")
    @PostMapping("/schedule/list")
    public Result findSchedule( HttpServletRequest request ){
        Page page = scheduleService.findPageSchedule( request );
        return Result.ok( page );
    }


    @ApiOperation( value = "删除排班信息")
    @PostMapping("/schedule/remove")
    public Result removeSchedule( HttpServletRequest request ){
        scheduleService.removeSchedule( request );
        return Result.ok( "删除成功" );
    }
}



