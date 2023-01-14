package com.hg.syt.service;

import com.hg.syt.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据医院编号 和 科室编号 查询排班规则数据分页显示
     * @param page page
     * @param limit limit
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 排班规则数据
     */
    Map<String, Object> getRuleSchedule( Integer page, Integer limit, String hoscode, String depcode );

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @param workDate 工作日期
     * @return 排班详细信息
     */
    List<Schedule> getDetailSchedule( String hoscode, String depcode, String workDate );
}


