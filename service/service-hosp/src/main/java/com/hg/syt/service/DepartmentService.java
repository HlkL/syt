package com.hg.syt.service;


import com.hg.syt.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author HG
 */
public interface DepartmentService {
    /**
     * 上传科室信息
     * @param request request
     */
    void saveDepartment( HttpServletRequest request );

    /**
     * 查询科室信息
     * @param request request
     * @return 科室分页信息
     */
    Page findPageDepartment( HttpServletRequest request );

    /**
     * 删除科室信息
     * @param request request
     */
    void removeDepartment( HttpServletRequest request );

    /**
     * 根据医院编号查询所有科室信息
     * @param hoscode 医院编号
     * @return 所有科室信息
     */
    List<DepartmentVo> queryAllDeptByHospCode( String hoscode );

    Object getDepName( String hoscode, String depcode );
}

