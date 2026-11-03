/**
 * Copyright 2018 bejson.com
 */
/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.express.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Auto-generated: 2018-07-19 22:27:22
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Traces {

    @JsonProperty("AcceptStation")
    @Schema(description = "描述")
    private String AcceptStation;

    @JsonProperty("AcceptTime")
    @Schema(description = "时间")
    private String AcceptTime;

    public String getAcceptStation() {
        return AcceptStation;
    }

    public void setAcceptStation(String AcceptStation) {
        this.AcceptStation = AcceptStation;
    }

    public String getAcceptTime() {
        return AcceptTime;
    }

    public void setAcceptTime(String AcceptTime) {
        this.AcceptTime = AcceptTime;
    }

}
