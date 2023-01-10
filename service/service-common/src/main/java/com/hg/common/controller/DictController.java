package com.hg.common.controller;

import com.hg.common.result.Result;
import com.hg.common.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-10 23:58
 */

@Api( tags = "数据字典管理" )
@RestController
@RequestMapping( "/admin/common/dict" )
@CrossOrigin
public class DictController {

    @Resource
    private DictService dictService;


    @ApiOperation( value = "根据id查询子字段数据")
    @GetMapping("/findChildData/{id}")
    public Result findDictChildDataById( @PathVariable("id") Long id ){
        return dictService.findDictChildDataById( id );
    }
}


