package com.hg.syt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.common.result.Result;
import com.hg.syt.model.hosp.HospitalSet;
import com.hg.syt.vo.hosp.HospitalSetQueryVo;

/**
 * @author HG
 */
public interface HospitalSetService extends IService<HospitalSet> {
    /**
     * 分页查询医院设置
     *
     * @param current
     * @param limit
     * @param hospitalSetQueryVo
     * @return
     */
    Page<HospitalSet> pageHospSet( long current, long limit, HospitalSetQueryVo hospitalSetQueryVo );

    Result saveHospSet( HospitalSet hospitalSet );

    Result sendSecretKey( Long id );
}
