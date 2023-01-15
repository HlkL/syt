package com.hg.syt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hg.common.exception.SytException;
import com.hg.common.helper.HttpRequestHelper;
import com.hg.common.result.ResultCodeEnum;
import com.hg.common.utils.MD5;
import com.hg.feign.client.DictFeignClient;
import com.hg.syt.model.hosp.Hospital;
import com.hg.syt.model.hosp.Schedule;
import com.hg.syt.repository.HospitalRepository;
import com.hg.syt.service.HospitalService;
import com.hg.syt.service.HospitalSetService;
import com.hg.syt.vo.hosp.HospitalQueryVo;
import com.hg.syt.vo.hosp.ScheduleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
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

    @Resource
    private DictFeignClient dictFeignClient;

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

    @Override
    public Page selectPage( Integer page, Integer limit, HospitalQueryVo hospitalQueryVo ) {

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties( hospitalQueryVo,hospital );

        Sort sort = Sort.by( Sort.Direction.DESC, "createTime");
        //分页构造器
        PageRequest pageRequest = PageRequest.of( page - 1, limit,sort );
        //创建ExampleMatcher对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
                .withIgnoreCase( true );

        Example<Hospital> example = Example.of( hospital, exampleMatcher );

        Page<Hospital> hospitalPage = hospitalRepository.findAll( example, pageRequest );

        //医院等级封装
        hospitalPage.getContent().forEach( this::setHospitalHospType );

        return hospitalPage;
    }

    @Override
    public void updateHospStatus( String id, Integer status ) {
        Hospital hospital = hospitalRepository.findById( id ).get();
        hospital.setStatus( status );
        hospital.setUpdateTime( new Date() );
        hospitalRepository.save( hospital );
    }

    @Override
    public Map<String, Object> getHospById( String id ) {
        Hospital hospital = hospitalRepository.findById( id ).get();
        return setHospitalDetails( hospital );
    }

    @Override
    public String getHospName( String hoscode ) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode( hoscode );
        if ( hospital != null ) {
            return hospital.getHosname();
        }
        return null;
    }

    @Override
    public Map<String, Object> getHospDetailsByHospCode( String hospCode ) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode( hospCode );
        return setHospitalDetails( hospital );
    }

    private Map<String, Object> setHospitalDetails( Hospital hospital ) {
        this.setHospitalHospType( hospital );
        HashMap<String, Object> result = new HashMap<>( 2 );

        result.put( "hospital",hospital );
        result.put( "bookingRule",hospital.getBookingRule() );
        hospital.setBookingRule( null );
        return result;
    }

    /**
     * 医院等级设置
     */
    private void setHospitalHospType( Hospital hospital ){
        //根据dictCode和value获取医院等级名称
        String hospTypeString = dictFeignClient.getName( "Hostype", hospital.getHostype() );
        //获取省 市 地区
        String province = dictFeignClient.getName( hospital.getProvinceCode() );
        String city = dictFeignClient.getName( hospital.getCityCode() );
        String district = dictFeignClient.getName( hospital.getDistrictCode() );

        hospital.getParam().put( "hospTypeString",hospTypeString );
        hospital.getParam().put( "addressString",province+city+district );
    }

}


