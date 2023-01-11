package com.hg.common.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hg.common.mapper.DictMapper;
import com.hg.syt.model.cmn.Dict;
import com.hg.syt.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

/**
 * @author hougen
 * @program syt_parent
 * @description easyexcel监听
 * @create 2023-01-11 20:11
 */
public class EasyExcelListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public EasyExcelListener( DictMapper dictMapper ) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke( DictEeVo dictEeVo, AnalysisContext analysisContext ) {
        //将监听到的数据存入数据库中
        Dict dict = new Dict();
        BeanUtils.copyProperties( dictEeVo,dict );
        dictMapper.insert( dict );
    }

    @Override
    public void doAfterAllAnalysed( AnalysisContext analysisContext ) {

    }
}


