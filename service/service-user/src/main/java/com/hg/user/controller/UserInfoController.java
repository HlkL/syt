package com.hg.user.controller;

import com.hg.common.result.Result;
import com.hg.syt.vo.user.LoginVo;
import com.hg.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-16 01:35
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/user")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation( value = "用户登录")
    @PostMapping("/login")
    public Result login( @RequestBody LoginVo loginVo ){

        Map<String,Object> info = userInfoService.login( loginVo );
        return Result.ok( info );
    }

    @ApiOperation( value = "发送邮箱验证码" )
    @GetMapping("/sendCode/{email}")
    public Result sendCode( @PathVariable("email") String email){

        userInfoService.sendEmailCode( email );
        return Result.ok( "验证码已发送" );
    }
}


