/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.dict.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.dict.domain.DictDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author huangyu
* @date 2020-05-14
*/
@Repository
public interface DictDetailMapper extends CoreMapper<DictDetail> {

    @Select("<script>SELECT d.* from system_dict_detail d LEFT JOIN system_dict t on d.dict_id = t.id where d.is_del=0 <if test = \"label !=null\" > and d.label LIKE concat('%', #{label}, '%') </if> <if test = \"dictName != ''||dictName !=null\" > AND t.name = #{dictName} order by d.sort asc</if></script>")
    List<DictDetail> selectDictDetailList(@Param("label") String label,@Param("dictName") String dictName);
}
