package com.hg.syt.controller;

import com.hg.common.exception.SytException;
import com.hg.common.result.Result;
import com.hg.syt.model.hosp.HospitalSet;
import com.hg.syt.service.HospitalSetService;
import com.hg.syt.vo.hosp.HospitalSetQueryVo;
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
@Api( tags = "医院设置管理" )
@RestController
@RequestMapping( "/admin/hosp/hospitalSet" )
@CrossOrigin
public class HospitalSetController {

    @Resource
    private HospitalSetService hospitalSetService;

    @ApiOperation( value = "查询医院所有设置" )
    @GetMapping( "/findAll" )
    public Result findAll() {
        //全局异常test
        try {
            int i = 1 / 0;
        } catch ( Exception e ) {
            throw new SytException( "预约挂号异常",201 );
        }
        List<HospitalSet> hospitalSets = hospitalSetService.list();
        return Result.ok( hospitalSets );
    }

    @ApiOperation( value = "逻辑删除医院设置" )
    @DeleteMapping( "/{id}" )
    public Result removeHospitalById( @PathVariable( "id" ) Long id ) {
        boolean flag = hospitalSetService.removeById( id );
        return flag ? Result.ok() : Result.fail();
    }

    @ApiOperation( value = "分页查询医院设置" )
    @PostMapping( "/findPageHospSet/{current}/{limit}" )
    public Result pageHospSet( @PathVariable( "current" ) long current,
                               @PathVariable( "limit" ) long limit,
                               @RequestBody( required = false ) HospitalSetQueryVo hospitalSetQueryVo ) {
        return Result.ok( hospitalSetService.pageHospSet( current, limit, hospitalSetQueryVo ) );
    }

    @ApiOperation( value = "添加医院设置" )
    @PostMapping( "/saveHospSet" )
    public Result saveHospSet( @RequestBody HospitalSet hospitalSet ) {
        return hospitalSetService.saveHospSet( hospitalSet );
    }

    @ApiOperation( value = "根据id查询医院设置" )
    @GetMapping( "/fingHospSet/{id}" )
    public Result getHospSetById( @PathVariable( "id" ) Long id ) {
        HospitalSet hospitalSet = hospitalSetService.getById( id );
        return hospitalSet != null ? Result.ok( hospitalSet ) : Result.fail( "没有该医院信息" );
    }

    @ApiOperation( value = "更新医院设置" )
    @PutMapping( "/updateHospSet" )
    public Result updateHospSet( @RequestBody HospitalSet hospitalSet ) {
        boolean isSuccess = hospitalSetService.updateById( hospitalSet );
        return isSuccess ? Result.ok( "更新数据成功" ) : Result.fail( "更新数据失败" );
    }

    @ApiOperation( value = "批量删除医院设置" )
    @DeleteMapping( "/batchRemoveHospSet" )
    public Result batchRemoveHospSet( @RequestBody List<Long> ids ) {
        boolean isRemove = hospitalSetService.removeByIds( ids );
        return isRemove ? Result.ok( "删除成功" ) : Result.fail( "删除失败" );
    }

    @ApiOperation( value = "医院设置锁定和解锁" )
    @PutMapping( "/lockHospSet/{id}/{status}" )
    public Result lockHospSet( @PathVariable( "id" ) Long id,
                               @PathVariable( "status" ) Integer status ) {

        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId( id );
        hospitalSet.setStatus( status );
        return this.updateHospSet( hospitalSet );
    }

    @ApiOperation( value = "发送医院设置接口密钥" )
    @GetMapping( "/sendSecretKey/{id}" )
    public Result sendSecretKey( @PathVariable( "id" ) Long id ) {
        return hospitalSetService.sendSecretKey( id );
    }
}


