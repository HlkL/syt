package com.hg.syt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hg.common.exception.SytException;
import com.hg.common.helper.HttpRequestHelper;
import com.hg.common.result.ResultCodeEnum;
import com.hg.common.utils.MD5;
import com.hg.syt.model.hosp.Hospital;
import com.hg.syt.repository.HospitalRepository;
import com.hg.syt.service.HospitalService;
import com.hg.syt.service.HospitalSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Resource
    private HospitalSetService hospitalSetService;

    @Override
    public void save( HttpServletRequest request ) {
        Map<String, Object> map = signatureVerification( request );

        //图片使用base64编码存储并将"+"替换成了" ",需要把图片信息中的" "转换回来
        //<img src="data:image/png;base64,logoData"/>
        String logoData = ( String ) map.get( "logoData" );
        logoData = logoData.replaceAll( " ","+" );
        map.put( "logoData",logoData );

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
        } else {
            hospital.setStatus( 0 );
            hospital.setCreateTime( new Date() );
        }
        hospital.setUpdateTime( new Date() );
        hospital.setIsDeleted( 0 );
        hospitalRepository.save( hospital );

    }

    @Override
    public Hospital getHosp( HttpServletRequest request ) {
        Map<String, Object> map = signatureVerification( request );
        String hoscode = ( String ) map.get( "hoscode" );

        return hospitalRepository.getHospitalByHoscode( hoscode );
    }

    public Map<String, Object> signatureVerification( HttpServletRequest request ) {
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap( parameterMap );

        //获取医院签名
        String sign = ( String ) map.get( "sign" );

        //对传过来的医院接口进行签名校验
        String hoscode = ( String ) map.get( "hoscode" );
        if ( StringUtils.isEmpty( hoscode ) ) {
            throw new SytException( ResultCodeEnum.SIGN_ERROR );
        }

        //签名比较
        String signKey = hospitalSetService.getSignKey( hoscode );
        String signKeyMd5 = MD5.encrypt( signKey );

        if ( !sign.equals( signKeyMd5 ) ) {
            throw new SytException( ResultCodeEnum.SIGN_ERROR );
        }
        return map;
    }

}


