package com.hg.syt.repository;


import com.hg.syt.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author HG
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule getScheduleByHoscode( String hoscode );

    Schedule getScheduleByHoscodeAndHosScheduleId( String hoscode, String hosScheduleId );
}
