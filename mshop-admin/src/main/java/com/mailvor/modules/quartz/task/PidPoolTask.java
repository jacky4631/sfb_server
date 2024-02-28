package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.config.PidPoolBean;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 淘宝订单绑定用户
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Slf4j
@Component
public class PidPoolTask {
    @Resource
    private PidPoolBean pidPoolBean;

    protected void run(String paramStr) {
        //pid绑定 优先查找订单pid绑定用户
        //追单号绑定 每30秒查找之前40秒的订单，优化查找是否有追单号相同的用户，有直接绑定，
        //足迹绑定 如果哪个用户有20分钟内的访问记录，自动绑定给该用户
        //足迹绑定成功，淘宝订单 自动绑定该用户的追单号，为数组类型
        //
        PidPoolParam param;
        if(StringUtils.isEmpty(paramStr) || "{}".equals(paramStr)) {
            param = new PidPoolParam();
        } else {
            param = JSON.parseObject(paramStr, PidPoolParam.class);
        }
        List<String> pidList = param.getPidList();
        if(CollectionUtils.isEmpty(pidList)) {
            pidList = pidPoolBean.getPool();
        }
        //todo 这步可能会导致pid误插入，所以在取pid时需要双重校验
        Set<String> bindingKeys = RedisUtil.keys(RedisUtil.getTbPidBindKey() + "*");
        List<String> finalPidList = Arrays.asList(pidList.toArray(new String[0])).stream().collect(Collectors.toList());
        bindingKeys.stream().forEach(bindingKey->{
            String[] splitKey = bindingKey.split(":");
            if(splitKey.length == 6) {
                finalPidList.remove(splitKey[5]);
            }

        });
        if(finalPidList.isEmpty()) {
            return;
        }
        RedisUtil.setPids(finalPidList);


    }

    @Data
    public static class PidPoolParam{

        private List<String> pidList;

    }

}
