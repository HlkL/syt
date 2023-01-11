package com.hg.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.common.result.Result;
import com.hg.syt.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author HG
 */
public interface DictService extends IService<Dict> {

    /**
     * 根据id查询子字段数据
     * @param id id
     * @return 结果集
     */
    Result findDictChildDataById( Long id );

    /**
     * 数据字典数据导出
     * @param response response
     */
    void exportData( HttpServletResponse response );

    /**
     * 导入字典数据
     * @param multipartFile excel文件
     * @return 响应结果
     */
    Result importData( MultipartFile multipartFile );
}
