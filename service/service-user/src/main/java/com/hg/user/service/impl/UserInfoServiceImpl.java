package com.hg.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.common.exception.SytException;
import com.hg.common.helper.JwtHelper;
import com.hg.common.result.ResultCodeEnum;
import com.hg.common.utils.RandomUtil;
import com.hg.common.utils.RegexUtils;
import com.hg.syt.model.user.UserInfo;
import com.hg.syt.model.user.UserLoginRecord;
import com.hg.syt.vo.user.LoginVo;
import com.hg.user.mapper.UserInfoMapper;
import com.hg.user.mapper.UserLoginRecordMapper;
import com.hg.user.service.UserInfoService;
import com.hg.user.utils.ConstantPropertiesUtils;
import com.hg.user.utils.HttpClientUtils;
import com.hg.user.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    private MailUtils mailUtils;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;

    @Transactional( rollbackFor = Exception.class)
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

        //微信登录跳转过来绑定手机号
        UserInfo userInfo;
        if ( !StringUtils.isEmpty( loginVo.getOpenid() ) ) {
            userInfo = this.query().eq( "openid", loginVo.getOpenid() ).one();
            if ( userInfo != null ) {
                userInfo.setPhone( loginVo.getPhone() );
                //将两条数据合并
                baseMapper.deleteById( userInfo.getId() );
                this.updateById( userInfo );
            }else {
                throw new SytException( ResultCodeEnum.DATA_ERROR );
            }
        }else {
            //查询数据库是否注册
            userInfo = this.query().eq( "phone", phone ).one();
            if ( userInfo == null ) {
                //自动注册
                userInfo = new UserInfo();
                userInfo.setName("用户"+ RandomUtil.getSixBitRandom() );
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                this.save(userInfo);
            }
        }

        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new SytException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //TODO 记录登录 单点登录
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(loginVo.getIp());
        userLoginRecordMapper.insert(userLoginRecord);


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
            throw new SytException( "邮箱格式错误",416 );
        }
        //生成验证码
        String code = RandomUtil.getSixBitRandom();
        //发送邮件
        mailUtils.sendSimpleMail( email,code );
        //存入缓存
        redisTemplate.opsForValue().set( email,code,1, TimeUnit.MINUTES );

    }

    @Override
    public Map<String,Object> getLoginParam() {
        try {
            HashMap<String, Object> result = new HashMap<>();
            result.put( "appid", ConstantPropertiesUtils.WX_OPEN_APP_ID );
            result.put( "scope", "snsapi_login" );
            String wxOpenRedirectUrl = ConstantPropertiesUtils.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl= URLEncoder.encode( wxOpenRedirectUrl, "utf8" );
            result.put( "redirect_uri",wxOpenRedirectUrl );
            result.put( "state",System.currentTimeMillis()+"" );

            return result;
        } catch ( UnsupportedEncodingException e ) {
            throw new SytException( "编码异常",444 );
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String WeixinCallBack( String code, String state ) {
        //获取临时票据 code
        log.info( "code: "+ code );

        //使用code ,微信appid和微信密钥 appscrect ,请求微信,获取access_toke
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        String accessTokenUrl = String.format( baseAccessTokenUrl,
                ConstantPropertiesUtils.WX_OPEN_APP_ID,
                ConstantPropertiesUtils.WX_OPEN_APP_SECRET,
                code);

        //前端跳转路径
        StringBuilder result = new StringBuilder(
                "redirect:" + ConstantPropertiesUtils.YYGH_BASE_URL
                        + "/weixin/callback?token="
        );
        try {
            String accessTokenInfo = HttpClientUtils.get( accessTokenUrl );
            log.info( "accessTokenInfo: "+ accessTokenInfo );

            JSONObject jsonObject = JSONObject.parseObject( accessTokenInfo );
            String accessToken = jsonObject.getString( "access_token" );
            String openId = jsonObject.getString( "openid" );

            //查看数据库中是否存在用户信息
            UserInfo userInfo = this.query().eq( "openid", openId ).one();
            if ( userInfo == null ) {
                //使用 accessToken 和 openId 再次请求微信获取用户登录信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);

                //请求微信
                String resultUserInfo = HttpClientUtils.get( userInfoUrl );
                if ( StringUtils.isEmpty( resultUserInfo ) ) {
                    throw new SytException( "用户信息获取失败",433 );
                }

                log.info( "用户登录基本信息:" + resultUserInfo );
                //解析用户信息
                JSONObject userInfoString = JSONObject.parseObject( resultUserInfo );
                //用户微信昵称
                String nickname = userInfoString.getString( "nickname" );
                //头像
                String headImgUrl = userInfoString.getString( "headimgurl" );

                //将用户信息添加到数据库
                userInfo = new UserInfo();
                userInfo.setNickName( nickname );
                userInfo.setStatus( 1 );
                userInfo.setOpenid( openId );
                this.save( userInfo );
            }

            String name = userInfo.getName();
            if(StringUtils.isEmpty(name )) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }

            //使用jwt生成token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            result.append( token );

            //返回token字符串
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                result.append( "&openid" ).append( userInfo.getOpenid() );
            } else {
                result.append( "&openid=\"\"" );
            }

            result.append( "&name=" ).append( URLEncoder.encode( name,"utf8" ) );

            return  result.toString();

        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
