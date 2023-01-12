package com.hg.common.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.common.listener.EasyExcelListener;
import com.hg.common.mapper.DictMapper;
import com.hg.common.result.Result;
import com.hg.common.service.DictService;
import com.hg.syt.model.cmn.Dict;
import com.hg.syt.vo.cmn.DictEeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-11 00:01
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Override
    public List<Dict> findDictChildDataById( Long id ) {

        //查询字段数据
        List<Dict> dictList = this.query().eq( "parent_id", id ).list();

        //是否有子字段
        dictList.forEach( dict -> {
            if ( this.isChild( dict.getId() ) ) {
                dict.setHasChildren( true );
            }else {
                dict.setHasChildren( false );
            }
        } );
        return dictList;
    }

    @Override
    public void exportData( HttpServletResponse response ) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            List<Dict> dicts = baseMapper.selectList( null );

            //将dict转化为dictEeVo
            List<DictEeVo> dictEeVos = dicts.stream().map( dict -> {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties( dict, dictEeVo );
                return dictEeVo;
            } ).collect( Collectors.toList() );

            //调用接口将数据写入excel中
            EasyExcel.write( response.getOutputStream(), DictEeVo.class ).sheet( "数据字典" )
                    .doWrite( dictEeVos );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @CacheEvict(value = "dict", allEntries=true)
    @Override
    public Result importData( MultipartFile multipartFile ) {
        try {
            EasyExcel.read( multipartFile.getInputStream(), DictEeVo.class,new EasyExcelListener( baseMapper ) )
                    .sheet().doRead();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        return Result.ok( "数据导入成功" );
    }

    private boolean isChild( Long id ) {
        return this.query().eq( "parent_id", id ).count() > 0;
    }
}


