/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.gen.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代码生成配置
 * @author Zheng Jie
 * @date 2019-01-03
 */
@Data
@NoArgsConstructor
@TableName("system_gen_config")
public class GenConfig {

    public GenConfig(String tableName) {
        this.cover = false;
        this.moduleName = "mshop-mall";
        this.author = "wangjun";
        this.pack = "com.mailvor.modules";
        this.tableName = tableName;
    }

    @TableId
    private Long id;

    /**表明**/
    private String tableName;

    /** 接口名称 **/
    private String apiAlias;

    /** 包路径 */
    private String pack;

    /** 模块名 */
    private String moduleName;

    /** 前端文件路径 */
    private String path;

    /** 前端文件路径 */
    private String apiPath;

    /** 作者 */
    private String author;

    /** 表前缀 */
    private String prefix;

    /** 是否覆盖 */
    private Boolean cover;
}
