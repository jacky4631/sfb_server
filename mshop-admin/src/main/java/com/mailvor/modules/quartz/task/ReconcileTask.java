package com.mailvor.modules.quartz.task;

import com.mailvor.modules.tk.service.MwReconcileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ReconcileTask {

    private final MwReconcileService reconcileService;

    public ReconcileTask(MwReconcileService reconcileService) {
        this.reconcileService = reconcileService;
    }

    public void run() {
        log.info("开始执行每日对账任务...");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = cal.getTime();

        List<String> platforms = Arrays.asList("TB", "JD", "PDD", "DY");
        for (String platform : platforms) {
            try {
                reconcileService.triggerReconcile(yesterday, Collections.singletonList(platform));
                log.info("平台 {} 对账任务已创建", platform);
            } catch (Exception e) {
                log.error("平台 {} 对账任务执行失败", platform, e);
            }
        }
        log.info("每日对账任务执行完毕");
    }
}
