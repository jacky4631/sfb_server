/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.feedback.rest;
import java.util.Arrays;

import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import lombok.AllArgsConstructor;
import com.mailvor.modules.feedback.domain.MwUserFeedback;
import com.mailvor.modules.feedback.service.MwUserFeedbackService;
import com.mailvor.modules.feedback.service.dto.MwUserFeedbackQueryCriteria;
import com.mailvor.modules.feedback.service.dto.MwUserFeedbackDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
/**
* @author wangjun
* @date 2024-05-27
*/
@AllArgsConstructor
@Api(tags = "userFeedback管理")
@RestController
@RequestMapping("/api/mwUserFeedback")
public class MwUserFeedbackController {

    private final MwUserFeedbackService mwUserFeedbackService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwUserFeedback:list')")
    public void download(HttpServletResponse response, MwUserFeedbackQueryCriteria criteria) throws IOException {
        mwUserFeedbackService.download(generator.convert(mwUserFeedbackService.queryAll(criteria), MwUserFeedbackDto.class), response);
    }

    @GetMapping
    @Log("查询userFeedback")
    @ApiOperation("查询userFeedback")
    @PreAuthorize("@el.check('admin','mwUserFeedback:list')")
    public ResponseEntity<PageResult<MwUserFeedbackDto>> getMwUserFeedbacks(MwUserFeedbackQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwUserFeedbackService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增userFeedback")
    @ApiOperation("新增userFeedback")
    @PreAuthorize("@el.check('admin','mwUserFeedback:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwUserFeedback resources){
        return new ResponseEntity<>(mwUserFeedbackService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改userFeedback")
    @ApiOperation("修改userFeedback")
    @PreAuthorize("@el.check('admin','mwUserFeedback:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwUserFeedback resources){
        mwUserFeedbackService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除userFeedback")
    @ApiOperation("删除userFeedback")
    @PreAuthorize("@el.check('admin','mwUserFeedback:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            mwUserFeedbackService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
