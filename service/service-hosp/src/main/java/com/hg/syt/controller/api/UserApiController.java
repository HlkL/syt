package com.hg.syt.controller.api;

import com.hg.common.result.Result;
import com.hg.syt.model.hosp.Hospital;
import com.hg.syt.repository.HospitalRepository;
import com.hg.syt.service.DepartmentService;
import com.hg.syt.service.HospitalService;
import com.hg.syt.vo.hosp.DepartmentVo;
import com.hg.syt.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-15 14:09
 */
@Api(tags = "用户预约挂号API接口")
@RestController
@RequestMapping("/api/hosp/index")
public class UserApiController {

    @Resource
    private HospitalService hospitalService;

    @Resource
    private HospitalRepository hospitalRepository;

    @Resource
    private DepartmentService departmentService;


    @ApiOperation( value = "获取医院列表")
    @GetMapping("/{page}/{limit}")
    public Result getHospList( @PathVariable("page")Integer page,
                               @PathVariable("limit")Integer limit,
                               HospitalQueryVo hospitalQueryVo ){

        Page selectPage = hospitalService.selectPage( page, limit, hospitalQueryVo );
        return Result.ok( selectPage );
    }

    @ApiOperation( value = "根据名称查询医院" )
    @GetMapping("/{hosname}")
    public Result queryHospByHospName( @PathVariable("hosname")String hosname ){

        List<Hospital> hospitals = hospitalRepository.findHospitalByHosnameLike( hosname );

        return Result.ok( hospitals );
    }

    @ApiOperation( value = "获取科室列表")
    @GetMapping("/getDepartmentList/{hoscode}")
    public Result getDepartmentListByHospCode( @PathVariable("hoscode")String hospCode ){

        List<DepartmentVo> departmentVos = departmentService.queryAllDeptByHospCode( hospCode );

        return Result.ok( departmentVos );
    }

    @ApiOperation( value = "获取医院预约挂号详情")
    @GetMapping("/getHospDetails/{hoscode}")
    public Result getHospDetailsByHospCode( @PathVariable("hoscode")String hospCode ){

        Map<String, Object> map = hospitalService.getHospDetailsByHospCode( hospCode );
        return Result.ok( map );
    }


}


