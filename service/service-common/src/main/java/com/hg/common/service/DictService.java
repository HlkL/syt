package com.hg.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.common.result.Result;
import com.hg.syt.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author HG
 */
public interface DictService extends IService<Dict> {

    /**
     * 根据id查询子字段数据
     * @param id id
     * @return 结果集
     */
    List<Dict> findDictChildDataById( Long id );

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

    /**
     * 获取数据字典名称
     * @param dictCode dictCode
     * @param value value
     * @return 字典名称
     */
    String getDictName( String dictCode, String value );

    /**
     * 根据dict_cod获取其子节点
     * @param dictCode dictCode
     * @return 子节点集合
     */
    List<Dict> getChildNode( String dictCode );
}
