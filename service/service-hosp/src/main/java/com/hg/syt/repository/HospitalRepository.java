package com.hg.syt.repository;

import com.hg.syt.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-12 17:33
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    /**
     * 是否存在医院数据
     * 不需要写具体实现,方法名称需要符合spring data规范,类似 spring data jpa
     * @param hoscode
     * @return
     */
    Hospital getHospitalByHoscode( String hoscode );
}


