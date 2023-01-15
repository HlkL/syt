package com.hg.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.syt.model.user.UserInfo;
import com.hg.user.mapper.UserInfoMapper;
import com.hg.user.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-16 01:40
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}


