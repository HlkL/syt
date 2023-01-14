package com.hg.syt.controller;

import com.hg.common.result.Result;
import com.hg.syt.service.DepartmentService;
import com.hg.syt.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-14 02:54
 */
@Api(tags = "医院科室管理")
@RestController
@RequestMapping("/admin/hosp/department")
@CrossOrigin
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @ApiOperation( value = "根据医院编号查询所有科室信息")
    @GetMapping("/queryAll/{hoscode}")
    public Result queryAllDeptByHospCode( @PathVariable("hoscode")String hoscode ){

        List<DepartmentVo> list = departmentService.queryAllDeptByHospCode( hoscode );
        return Result.ok( list );
    }
}


