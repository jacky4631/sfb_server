package com.mailvor.modules.meituan;


import com.mailvor.common.json.JsonArray;
import com.mailvor.common.json.JsonObject;
import com.mailvor.modules.meituan.param.MeituanBaseParam;
import com.mailvor.modules.meituan.param.MeituanGoodsParam;
import com.mailvor.modules.meituan.param.MeituanLinkParam;
import com.mailvor.modules.meituan.param.MeituanOrderParam;
import com.mailvor.modules.tk.domain.MailvorMtOrder;
import com.mailvor.modules.tk.vo.MtDataVo;
import com.mailvor.modules.tk.vo.MtResVo;
import com.mailvor.modules.meituan.utils.MeituanClient;
import com.mailvor.modules.meituan.utils.MeituanSidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;


/**
 * 美团官方接口
 * @author:
 * @createTime: 2024/01/24 14:55
 * @description:
 */
@Slf4j
@Component
public class MeituanService {
    @Resource
    private MeituanClient meituanClient;

    public JsonObject goodsList(MeituanGoodsParam param){
        //请求path，不同的接口修改为对应的接口，以下只是一个接口
        String path = "/cps_open/common/api/v1/query_coupon";
        try {
            return requestMeituan(param, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MtResVo order(MeituanOrderParam param){

        //请求path，不同的接口修改为对应的接口，以下只是一个接口
        String path = "/cps_open/common/api/v1/query_order";
        try {
            JsonObject res = requestMeituan(param, path);
            if(res != null && Integer.valueOf(0).equals(res.getInteger("code"))) {
                JsonObject data = res.getJSONObject("data");
                if(data != null && Integer.valueOf(0).equals(data.getInteger("code"))) {
                    JsonArray dataList = data.getJSONArray("dataList");
                    if(dataList == null) {
                        dataList = new JsonArray();
                    }
                    MtResVo resVo = new MtResVo();
                    resVo.setCode(200);
                    MtDataVo dataVo = new MtDataVo();
                    resVo.setMsg(dataVo);
                    dataVo.setPositionIndex(data.getString("scrollId"));
                    ArrayList<MailvorMtOrder> records = new ArrayList<>(dataList.size());
                    for(Object obj : dataList) {
                        JsonObject dataObj = (JsonObject) obj;
                        String sid = dataObj.getString("sid");
                        Long uid = MeituanSidUtil.parse(sid);
                        if (uid == null) {
                            continue;
                        }
                        Double commission = dataObj.getDouble("commissionRate");
                        Long refundTime = dataObj.getLong("refundTime");
                        Long payTime = dataObj.getLong("payTime");
                        Long updateTime = dataObj.getLong("updateTime");
                        MailvorMtOrder order = MailvorMtOrder.builder()
                                .uniqueItemId(dataObj.getLong("orderId"))
                                .orderId(dataObj.getLong("orderId"))
                                //300等于3%
                                .balanceCommissionRatio(commission == null ? 0D : commission / 100)
                                .orderPayTime(payTime != null ? new Date(payTime*1000) : null)
                                .shopName(dataObj.getString("productName"))
                                .actualOrderAmount(dataObj.getDouble("payPrice"))
                                .actualItemAmount(dataObj.getDouble("payPrice"))
                                .shopUuid("")
                                .balanceAmount(dataObj.getDouble("profit"))
                                .modifyTime(updateTime != null ? new Date(updateTime*1000) : null)
                                .refundDate(refundTime != null ? new Date(refundTime*1000) : null)
                                .build();
                        Integer status = dataObj.getInteger("status");
                        if(Integer.valueOf(2).equals(status)){
                            order.setItemStatus(0);
                            order.setItemBizStatus(1);
                        } else if(Integer.valueOf(3).equals(status)){
                            order.setItemStatus(1);
                            order.setItemBizStatus(2);
                        } else if(Integer.valueOf(4).equals(status) || Integer.valueOf(5).equals(status)){
                            order.setItemStatus(3);
                            order.setItemBizStatus(99);
                        } else if(Integer.valueOf(6).equals(status)){
                            order.setItemStatus(1);
                            order.setItemBizStatus(3);
                        }
                        order.setSid(sid);
                        if (uid > 0) {
                            order.setUid(uid);
                        }
                        records.add(order);
                    }
                    dataVo.setRecords(records);
                    return resVo;

                }
            }
            return null;
        } catch (Exception e) {
            log.error("采集美团订单错误", e);
            return null;
        }
    }

    public JsonObject getCode(MeituanLinkParam param, Long uid) throws Exception {
        //请求path，不同的接口修改为对应的接口，以下只是一个接口
        String path = "/cps_open/common/api/v1/get_referral_link";
        param.setSid(MeituanSidUtil.build(uid));
        return requestMeituan(param, path);
    }


    public JsonObject requestMeituan(MeituanBaseParam param, String path) throws Exception {
        return meituanClient.request(param, path);
    }
}
