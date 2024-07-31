package com.mailvor.modules.monitor.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.monitor.domain.Visits;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitsMapper extends CoreMapper<Visits> {
    @Select("select * FROM system_visits where create_time between #{time1} and #{time2}")
    List<Visits> findAllVisits(@Param("time1") String time1, @Param("time2")String time2);
}
