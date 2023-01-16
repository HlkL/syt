package com.hg.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.common.exception.SytException;
import com.hg.common.helper.JwtHelper;
import com.hg.common.result.ResultCodeEnum;
import com.hg.common.utils.RandomUtil;
import com.hg.common.utils.RegexUtils;
import com.hg.syt.model.user.UserInfo;
import com.hg.syt.vo.user.LoginVo;
import com.hg.user.mapper.UserInfoMapper;
import com.hg.user.service.UserInfoService;
import com.hg.user.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.hg.common.result.ResultCodeEnum.CODE_ERROR;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-16 01:40
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private MailUtil mailUtil;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Map<String, Object> login( LoginVo loginVo ) {

        //参数校验
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if ( StringUtils.isEmpty( phone ) || StringUtils.isEmpty( code ) ) {
            throw new SytException( ResultCodeEnum.PARAM_ERROR );
        }
        //验证码校验
        String codeStr = redisTemplate.opsForValue().get( phone );
        if ( codeStr == null || !codeStr.equals( code ) ) {
            throw new SytException( CODE_ERROR );
        }

        //查询数据库是否注册
        UserInfo userInfo = this.query().eq( "phone", phone ).one();
        if ( userInfo == null ) {
            //自动注册
            userInfo = new UserInfo();
            userInfo.setName("用户"+ RandomUtil.getSixBitRandom() );
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            this.save(userInfo);
        }

        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new SytException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //TODO 记录登录

        //返回页面显示名称
        Map<String, Object> result = new HashMap<>( 2 );
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        //token
        String token = JwtHelper.createToken( userInfo.getId(), userInfo.getName() );
        result.put("name", name);
        result.put("token", token);

        return result;
    }

    @Override
    public void sendEmailCode( String email ) {
        //邮箱校验
        if ( StringUtils.isEmpty( email ) || RegexUtils.isEmailInvalid( email ) ) {
            throw new SytException( "邮箱格式错误",216 );
        }
        //生成验证码
        String code = RandomUtil.getSixBitRandom();
        //发送邮件
        mailUtil.sendSimpleMail( email,code );
        //存入缓存
        redisTemplate.opsForValue().set( email,code,1, TimeUnit.MINUTES );

    }
}


