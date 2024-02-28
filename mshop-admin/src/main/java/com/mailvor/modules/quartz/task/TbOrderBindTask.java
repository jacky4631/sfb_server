package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.tk.domain.MailvorTbOrder;
import com.mailvor.modules.tk.service.MailvorTbOrderService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 淘宝订单绑定用户
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Slf4j
@Component
public class TbOrderBindTask {
    @Resource
    protected MailvorTbOrderService orderService;

    @Resource
    protected MwUserService mwUserService;
    @Resource
    protected MwUserUnionService userUnionService;

    @Resource
    private SuStoreOrderService suStoreOrderService;

    @Value("${tb.pid.excludes}")
    private List<String> excludePids;

    protected void run(String paramStr) {
        //渠道id绑定
        //动态pid绑定 动态pid绑定用户
        //追单号绑定 查找是否有追单号相同的用户，有直接绑定，
        OrderBindParam param;
        if(StringUtils.isEmpty(paramStr) || "{}".equals(paramStr)) {
            param = new OrderBindParam();
        } else {
            param = JSON.parseObject(paramStr, OrderBindParam.class);
        }
        List<MailvorTbOrder> tbOrders = orderService.getUnbindOrderList(param.getPrior());
        if(tbOrders.isEmpty()) {
            return;
        }
        Map<String,String> pidMap = RedisUtil.getBindingPid();

        List<String> nos = new ArrayList<>();
        List<String> tbPids = new ArrayList<>();
        tbOrders.forEach(tbOrder -> {
            nos.add(DateUtils.getAdditionalNo(tbOrder.getTradeParentId().toString()));
            if(tbOrder.getRelationId() != null && tbOrder.getRelationId() > 0) {
                tbPids.add(tbOrder.getRelationId().toString());
            }

        });

        List<MwUser> users = mwUserService.queryContainsAdditionalNo(nos);
        Map<String, MwUser> userMap = new HashMap<>();
        //追单号有可能重复 直接用toMap会报错
        for(MwUser user: users) {
            String addNo = user.getAdditionalNo();
            if(userMap.get(addNo) == null) {
                userMap.put(addNo, user);
            }
        }
        //根据淘宝渠道id找到用户id
        Map<String, MwUserUnion> hasChannelIdUserMap;
        if(tbPids.isEmpty()) {
            hasChannelIdUserMap = new HashMap<>();
        } else {
            List<MwUserUnion> hasChannelIdUsers = userUnionService.listByTbPid(tbPids);
            hasChannelIdUserMap = hasChannelIdUsers.stream()
                    .collect(Collectors.toMap(MwUserUnion::getTbPid, user -> user));
        }

        tbOrders.stream().forEach(tbOrder -> {
            if(excludePids.contains(tbOrder.getAdzoneId().toString())){
                return;
            }
            //渠道订单绑定
            Long relationId = tbOrder.getRelationId();
            if(relationId != null){
                MwUserUnion user = hasChannelIdUserMap.get(relationId.toString());
                if(user != null) {
                    //绑定订单 用户记录积分，参照订单找回
                    suStoreOrderService.bindOrder(user.getUid(), tbOrder);
                    return;
                }
            }

            //pid绑定
            String uidStr = pidMap.get(param.getPidPrefix() + tbOrder.getAdzoneId());
            if(uidStr != null) {
                suStoreOrderService.bindOrder(Long.parseLong(uidStr), tbOrder);
                return;
            }

            //用户追单号绑定
            String additionalNo = DateUtils.getAdditionalNo(tbOrder.getTradeParentId().toString());
            MwUser user = userMap.get(additionalNo);
            if(user != null) {
                //绑定订单 用户记录积分，参照订单找回
                suStoreOrderService.bindOrder(user.getUid(), tbOrder);
            }
        });
    }

    @Data
    public static class OrderBindParam{
        /**
         * 多少时间之前的订单 分钟
         * */
        private Integer prior = 200;


        /**
         * pid前缀， 目前取的 苏分宝网站的pid
         * */
        private String pidPrefix = "mm_52650383_39582271_";
    }

}
