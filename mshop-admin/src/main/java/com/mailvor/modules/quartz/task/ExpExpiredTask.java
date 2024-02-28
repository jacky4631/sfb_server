package com.mailvor.modules.quartz.task;

import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.MwUserExtraService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.mailvor.modules.utils.TkUtil.EXPIRED_LEVEL;

/**
 * 体验卡过期任务
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class ExpExpiredTask {
    @Resource
    private MwUserExtraService userExtraService;

    protected void run(String param) {
      userExtraService.expiredUser();
    }

}
