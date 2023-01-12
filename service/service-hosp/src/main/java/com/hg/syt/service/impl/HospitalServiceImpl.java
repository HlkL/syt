package com.hg.syt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hg.common.helper.HttpRequestHelper;
import com.hg.syt.model.hosp.Hospital;
import com.hg.syt.repository.HospitalRepository;
import com.hg.syt.service.HospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-12 17:35
 */
@Service
@Slf4j
public class HospitalServiceImpl implements HospitalService {

    @Resource
    private HospitalRepository hospitalRepository;

    @Override
    public void save( HttpServletRequest request ) {
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap( parameterMap );

        //将map转化为hospital对象
        String mapStr = JSONObject.toJSONString( map );
        Hospital hospital = JSONObject.parseObject( mapStr, Hospital.class );

        //mongo中是否存在hosp数据
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode( hospital.getHoscode() );
        //存在数据
        if ( hospitalExist != null ) {
            hospital.setId( hospitalExist.getId() );
            hospital.setStatus( hospitalExist.getStatus() );
            hospital.setCreateTime( hospitalExist.getCreateTime() );
            hospital.setUpdateTime( new Date() );
            hospital.setIsDeleted( 0 );
            hospitalRepository.save( hospital );
        } else {
            hospital.setStatus( 0 );
            hospital.setCreateTime( new Date() );
            hospital.setUpdateTime( new Date() );
            hospital.setIsDeleted( 0 );
            hospitalRepository.save( hospital );
        }
    }

}


