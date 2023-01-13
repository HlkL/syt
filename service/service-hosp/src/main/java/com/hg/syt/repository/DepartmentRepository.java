package com.hg.syt.repository;

import com.hg.syt.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author HG
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    Department getDepartmentByHoscodeAndDepcode( String hoscode, String depcode );
}

