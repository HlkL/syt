package com.hg.syt.repository;


import com.hg.syt.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @author HG
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule getScheduleByHoscode( String hoscode );

    Schedule getScheduleByHoscodeAndHosScheduleId( String hoscode, String hosScheduleId );

    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate( String hoscode, String depcode, Date toDate );
}
