package com.hg.feign.client;

import com.hg.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-13 16:31
 */
@FeignClient(name = "service-common",path = "/admin/common/dict")
public interface DictFeignClient {

    /**
     * 根据id查询子字段数据
     */
    @GetMapping("/findChildData/{id}")
    Result findDictChildDataById( @PathVariable("id") Long id );

    /**
     * 字典数据导出
     */
    @GetMapping(value = "/exportData")
    void exportData( HttpServletResponse response);

    /**
     * 导入字典数据
     * @param file element-ui文件上传默认名称为file
     */
    @PostMapping("/importData")
    Result importData( @RequestParam("file") MultipartFile file );

    /**
     * 获取数据字典名称
     */
    @GetMapping(value = "/getName/{dictCode}/{value}")
    String getName( @PathVariable("dictCode")String dictCode,
                           @PathVariable("value")String value );

    /**
     * 获取数据字典名称
     */
    @GetMapping(value = "/getName/{value}")
    String getName( @PathVariable("value")String value );
}


