package com.hg.syt.service;

import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-13 01:50
 */
public interface ScheduleService {
    /**
     * 上传排班信息
     * @param request request
     */
    void saveSchedule( HttpServletRequest request );

    /**
     * 查询排班信息
     * @param request request
     * @return 分页信息
     */
    Page findPageSchedule( HttpServletRequest request );

    /**
     * 删除排班信息
     * @param request request
     */
    void removeSchedule( HttpServletRequest request );
}


