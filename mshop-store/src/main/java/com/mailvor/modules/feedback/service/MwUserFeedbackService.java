/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.feedback.service;
import com.mailvor.common.service.BaseService;
import com.mailvor.domain.PageResult;
import com.mailvor.modules.feedback.domain.MwUserFeedback;
import com.mailvor.modules.feedback.service.dto.MwUserFeedbackDto;
import com.mailvor.modules.feedback.service.dto.MwUserFeedbackQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
/**
* @author wangjun
* @date 2024-05-27
*/
public interface MwUserFeedbackService  extends BaseService<MwUserFeedback> {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MwUserFeedbackDto> queryAll(MwUserFeedbackQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwUserFeedbackDto>
    */
    List<MwUserFeedback> queryAll(MwUserFeedbackQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwUserFeedbackDto> all, HttpServletResponse response) throws IOException;
}
