package com.hg.syt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.common.result.Result;
import com.hg.common.utils.MD5;
import com.hg.syt.mapper.HospitalSetMapper;
import com.hg.syt.model.hosp.HospitalSet;
import com.hg.syt.service.HospitalSetService;
import com.hg.syt.vo.hosp.HospitalSetQueryVo;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-08 20:02
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>
        implements HospitalSetService {

    @Override
    public Page<HospitalSet> pageHospSet( long current, long limit, HospitalSetQueryVo hospitalSetQueryVo ) {

        //创建page对象
        Page<HospitalSet> page = new Page<>( current, limit );
        //条件构造器
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.like( hospitalSetQueryVo.getHosname() != null, "hosname", hospitalSetQueryVo.getHosname() )
                .eq( hospitalSetQueryVo.getHoscode() != null, "hoscode", hospitalSetQueryVo.getHoscode() );

        return this.page( page, queryWrapper );
    }

    @Override
    public Result saveHospSet( HospitalSet hospitalSet ) {

        //设置状态 1 使用,0不能使用
        hospitalSet.setStatus( 1 );
        //签名密钥
        Random random = new Random();
        hospitalSet.setSignKey( MD5.encrypt( System.currentTimeMillis() + random.nextInt( 10000 ) + "" ) );
        boolean save = this.save( hospitalSet );

        return save ? Result.ok( "添加医院设置成功" ) : Result.fail( "添加失败" );
    }

    @Override
    public Result sendSecretKey( Long id ) {

        HospitalSet hospitalSet = this.getById( id );

        //TODO 发送短信
        return Result.ok( hospitalSet.getSignKey() );
    }
}


