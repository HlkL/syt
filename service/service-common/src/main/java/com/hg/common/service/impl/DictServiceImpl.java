package com.hg.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.common.mapper.DictMapper;
import com.hg.common.result.Result;
import com.hg.common.service.DictService;
import com.hg.syt.model.cmn.Dict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-11 00:01
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public Result findDictChildDataById( Long id ) {

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
        return Result.ok( dictList );
    }

    private boolean isChild( Long id ) {
        return this.query().eq( "parent_id", id ).count() > 0;
    }
}


