package com.hg.syt.controller;

import com.hg.common.result.Result;
import com.hg.syt.service.HospitalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation( value = "上传医院")
    @PostMapping("/saveHospital")
    public Result saveHosp( HttpServletRequest request ){
        hospitalService.save( request );
        return Result.ok( "上传成功" );
    }
}



