package com.hg.common.controller;

import com.hg.common.result.Result;
import com.hg.common.service.DictService;
import com.hg.syt.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-10 23:58
 */

@Api( tags = "数据字典管理" )
@RestController
@RequestMapping( "/admin/common/dict" )
//@CrossOrigin
public class DictController {

    @Resource
    private DictService dictService;

    @ApiOperation( value = "根据id查询子字段数据")
    @GetMapping("/findChildData/{id}")
    public Result findDictChildDataById( @PathVariable("id") Long id ){
        return Result.ok(dictService.findDictChildDataById( id ));
    }

    @ApiOperation(value="字典数据导出")
    @GetMapping(value = "/exportData")
    public void exportData( HttpServletResponse response) {
        dictService.exportData(response);
    }

    /**
     * @param file element-ui文件上传默认名称为file
     * @return
     */
    @ApiOperation( "导入字典数据" )
    @PostMapping("/importData")
    public Result importData( @RequestParam("file") MultipartFile file ){
        return dictService.importData( file );
    }

    @ApiOperation(value="获取数据字典名称")
    @GetMapping(value = "/getName/{dictCode}/{value}")
    public String getName( @PathVariable("dictCode")String dictCode,
                           @PathVariable("value")String value ){

        return dictService.getDictName( dictCode,value );
    }

    @ApiOperation(value="获取数据字典名称")
    @GetMapping(value = "/getName/{value}")
    public String getName( @PathVariable("value")String value ){

        return dictService.getDictName( "",value );
    }

    @ApiOperation(value="根据dict_code查询其子节点")
    @GetMapping(value = "/findNode/{dictCode}")
    public Result queryChildNodeByDictCode( @PathVariable("dictCode")String dictCode ){

        List<Dict> childNodes = dictService.getChildNode( dictCode );
        return Result.ok( childNodes );
    }
}


