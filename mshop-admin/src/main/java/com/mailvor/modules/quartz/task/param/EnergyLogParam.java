package com.mailvor.modules.quartz.task.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EnergyLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> platforms;

    private List<Long> uids;
}
