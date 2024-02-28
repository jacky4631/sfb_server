package com.mailvor.modules.quartz.task;

import com.google.common.collect.Lists;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 统计所有预估
 * //每天执行一次
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class FeeTriggerTask {
    @Resource
    private MwUserService userService;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    protected void run(String param) throws InterruptedException {
        List<Long> uids = new ArrayList<>();
        try {
            if(StringUtils.isNotBlank(param)) {
                String[] strs = param.split(",");
                for(int i = 0; i < strs.length; i++) {
                    uids.add(Long.parseLong(strs[i]));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        //只要用户提交或者自动绑定订单，就会增加积分，就会更新用户的更新时间
        //获取今天有更新的用户，uid从大到小排列,一次100条 执行
        //读取今天有更新的用户
        List<Object> idList = RedisUtil.getTodayUids();
        if (idList.isEmpty() && uids.isEmpty()) {
            return;
        }
        uids.addAll(idList.stream().map(id-> (long) id).collect(toList()));
        uids = uids.stream().distinct().collect(toList());
        Collections.reverse(uids);
        List<List<Long>> partition = Lists.partition(uids, 100);
        if (partition.size() > 0) {
            try{
                for (List<Long> list : partition) {
                    userService.initFeeLogs(1, list);
                    userService.initFeeLogs(6, list);
                    userService.initFeeLogs(3, list);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
