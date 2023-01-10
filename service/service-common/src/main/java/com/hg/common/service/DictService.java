package com.hg.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.common.result.Result;
import com.hg.syt.model.cmn.Dict;

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
}
