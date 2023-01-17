package com.hg.user.controller.api;

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
 * @description 用户邮箱登录api
 * @create 2023-01-17 15:32
 */
@Api(tags = "用户邮箱登录API")
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {


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


