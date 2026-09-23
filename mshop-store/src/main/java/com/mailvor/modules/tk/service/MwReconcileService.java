package com.mailvor.modules.tk.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.tk.domain.MwReconcileLog;
import com.mailvor.modules.tk.domain.MwReconcileDiff;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MwReconcileService extends BaseService<MwReconcileLog> {

    Map<String, Object> queryAll(Map<String, Object> criteria, Pageable pageable);

    List<MwReconcileLog> queryAll(Map<String, Object> criteria);

    Map<String, Object> queryDiffAll(Map<String, Object> criteria, Pageable pageable);

    List<MwReconcileDiff> queryDiffAll(Map<String, Object> criteria);

    void handleDiff(Long diffId, String action, String remark);

    MwReconcileLog triggerReconcile(Date date, List<String> platforms);

    boolean existsReconcileForDate(Date date);

    List<MwReconcileDiff> exportDiffs(Long logId);
}
