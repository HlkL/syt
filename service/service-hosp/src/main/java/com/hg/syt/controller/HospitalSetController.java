package com.hg.syt.controller;

import com.hg.syt.model.hosp.HospitalSet;
import com.hg.syt.service.HospitalSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-08 20:06
 */
@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Resource
    private HospitalSetService hospitalSetService;

    @ApiOperation( value = "查询医院所有设置" )
    @GetMapping("/findAll")
    public List<HospitalSet> findAll(){
        return hospitalSetService.list();
    }

    @ApiOperation( value = "逻辑删除医院设置" )
    @DeleteMapping("/{id}")
    public boolean removeHospitalById( @PathVariable("id") Long id ){
        return hospitalSetService.removeById( id );
    }
}


