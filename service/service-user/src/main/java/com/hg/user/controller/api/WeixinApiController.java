package com.hg.user.controller.api;

import com.hg.common.result.Result;
import com.hg.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author hougen
 * @program syt_parent
 * @description 用户微信登录
 * @create 2023-01-17 15:37
 */
@Api(tags = "用户微信登录API")
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Resource
    private UserInfoService userInfoService;


    @ApiOperation( value = "获取生成微信登录参数")
    @GetMapping("/getLoginParam")
    @ResponseBody
    public Result getLoginParam(){

        Map<String,Object> params =  userInfoService.getLoginParam();
        return Result.ok( params );
    }

    /**
     * 微信登录回调
     * @return
     */
    @ApiOperation( value = "微信登录回调")
    @GetMapping("/callback")
    public String callBack(String code,String state){
        return userInfoService.WeixinCallBack( code,state );
    }
}


