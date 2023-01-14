package com.hg.syt.controller;

import com.hg.common.result.Result;
import com.hg.syt.service.HospitalService;
import com.hg.syt.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-13 15:14
 */
@Api( tags = "医院管理" )
@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {

    @Resource
    private HospitalService hospitalService;

    @ApiOperation( value = "获取医院分页列表")
    @GetMapping("/{page}/{limit}")
    public Result getHospitalPageList( @PathVariable("page") Integer page,
                                       @PathVariable("limit") Integer limit,
                                       HospitalQueryVo hospitalQueryVo ){

        Page pageList = hospitalService.selectPage( page,limit,hospitalQueryVo );
        return Result.ok( pageList );
    }

    @ApiOperation( value = "更新医院状态")
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable("id") String id,
                                   @PathVariable("status") Integer status){

        hospitalService.updateHospStatus( id,status );
        return Result.ok( "更新成功" );
    }

    @ApiOperation( value = "根据id获取医院信息")
    @GetMapping("/showHospById/{id}")
    public Result showHospById( @PathVariable("id")String id ){

        Map<String,Object> map = hospitalService.getHospById( id );
        return Result.ok( map );
    }
}


