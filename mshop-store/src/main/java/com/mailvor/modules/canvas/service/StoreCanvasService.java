/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.canvas.service;
import com.mailvor.common.service.BaseService;
import com.mailvor.modules.canvas.domain.StoreCanvas;
import com.mailvor.modules.canvas.service.dto.StoreCanvasDto;
import com.mailvor.modules.canvas.service.dto.StoreCanvasQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.mailvor.domain.PageResult;
/**
* @author mshop
* @date 2021-02-01
*/
public interface StoreCanvasService  extends BaseService<StoreCanvas>{

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<StoreCanvasDto>  queryAll(StoreCanvasQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<StoreCanvasDto>
    */
    List<StoreCanvas> queryAll(StoreCanvasQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<StoreCanvasDto> all, HttpServletResponse response) throws IOException;
}
