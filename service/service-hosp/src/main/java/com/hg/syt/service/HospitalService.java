package com.hg.syt.service;

import com.hg.syt.model.hosp.Hospital;
import com.hg.syt.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author HG
 */
public interface HospitalService {
    /**
     * 上传医院信息
     * @param request
     */
    void save( HttpServletRequest request );

    /**
     * 获取医院信息
     * @param request
     * @return
     */
    Hospital getHosp( HttpServletRequest request );

    /**
     * 签名校验
     * @param request request
     * @return 解析request
     */
    Map<String, Object> signatureVerification( HttpServletRequest request );

    /**
     * 获取医院分页列表
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    Page selectPage( Integer page, Integer limit, HospitalQueryVo hospitalQueryVo );

    /**
     * 更新医院状态
     * @param id id
     * @param status 状态
     */
    void updateHospStatus( String id, Integer status );

    /**
     * 根据id获取医院信息
     * @param id id
     * @return map
     */
    Map<String, Object> getHospById( String id );

    String getHospName( String hoscode );

    /**
     * 获取医院预约挂号详情
     * @param hospCode 医院编号
     * @return 挂号详情
     */
    Map<String, Object> getHospDetailsByHospCode( String hospCode );
}
