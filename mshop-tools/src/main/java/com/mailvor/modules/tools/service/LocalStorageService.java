/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.tools.service.dto.LocalStorageDto;
import com.mailvor.modules.tools.service.dto.LocalStorageQueryCriteria;
import com.mailvor.modules.tools.domain.LocalStorage;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-13
*/
public interface LocalStorageService  extends BaseService<LocalStorage>{

    /**
     * 分页查询
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    Map<String, Object> queryAll(LocalStorageQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部数据
     * @param criteria 条件
     * @return /
     */
    List<LocalStorageDto> queryAll(LocalStorageQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id /
     * @return /
     */
    LocalStorageDto findById(Long id);

    /**
     * 上传
     * @param name 文件名称
     * @param file 文件
     * @return /
     */
    LocalStorageDto create(String name, MultipartFile file);


    /**
     * 多选删除
     * @param ids /
     */
    void deleteAll(Long[] ids);

    /**
     * 导出数据
     * @param localStorageDtos 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<LocalStorageDto> localStorageDtos, HttpServletResponse response) throws IOException;

    /**
     * 修改文件
     * @param resources
     */
    void updateLocalStorage(LocalStorageDto resources);
}
