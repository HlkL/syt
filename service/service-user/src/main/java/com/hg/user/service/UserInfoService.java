package com.hg.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.syt.model.user.UserInfo;
import com.hg.syt.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户登录
     * @param loginVo
     * @return
     */
    Map<String, Object> login( LoginVo loginVo );

    /**
     * 邮箱验证码发送
     * @param email
     */
    void sendEmailCode( String email );
}
