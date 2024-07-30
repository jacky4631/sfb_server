package com.mailvor.modules.shop.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.modules.shop.domain.MwAppVersion;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：LionCity
 * @date ：Created in 2020-12-09 10:25
 * @description：
 * @modified By：
 * @version:
 */

@Data
public class MwAppVersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 版本code */
    private String versionCode;

    /** 版本名称 */
    private String versionName;

    /** 版本描述 */
    private String versionInfo;

    /** 安卓下载链接 */
    private String downloadUrl;

    /**是否强制升级*/
    private Boolean forceUpdate;

    public void copy(MwAppVersion source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
