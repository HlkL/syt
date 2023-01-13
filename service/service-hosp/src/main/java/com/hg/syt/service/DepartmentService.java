package com.hg.syt.service;


import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

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
}

