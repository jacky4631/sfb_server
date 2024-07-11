package com.mailvor.modules.meituan;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.meituan.config.MeituanConfig;
import com.mailvor.modules.meituan.param.MeituanBaseParam;
import com.mailvor.modules.meituan.param.MeituanGoodsParam;
import com.mailvor.modules.meituan.param.MeituanLinkParam;
import com.mailvor.modules.meituan.param.MeituanOrderParam;
import com.mailvor.modules.tk.domain.MailvorMtOrder;
import com.mailvor.modules.tk.vo.MtDataVo;
import com.mailvor.modules.tk.vo.MtResVo;
import com.mailvor.utils.StringUtils;
import com.sankuai.api.gateway.Client;
import com.sankuai.api.gateway.Request;
import com.sankuai.api.gateway.Response;
import com.sankuai.api.gateway.constant.Constants;
import com.sankuai.api.gateway.constant.ContentType;
import com.sankuai.api.gateway.constant.HttpHeader;
import com.sankuai.api.gateway.constant.HttpSchema;
import com.sankuai.api.gateway.enums.Method;
import com.sankuai.api.gateway.util.MessageDigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static com.mailvor.modules.meituan.constants.MeituanConstants.MT_MEDIUM_PREFIX;

/**
 * 美团官方接口
 * @author:
 * @createTime: 2024/01/24 14:55
 * @description:
 */
@Slf4j
@Component
public class MeituanService {
    //API域名
    private final static String HOST = "media.meituan.com";

    @Resource
    private MeituanConfig meituanConfig;
    @Resource
    private RestTemplate restTemplate;

    public JSONObject goodsList(MeituanGoodsParam param){
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
            JSONObject res = requestMeituan(param, path);
            if(res.getInteger("code") == 0) {
                JSONObject data = res.getJSONObject("data");
                if(data.getInteger("code") == 0) {
                    JSONArray dataList = data.getJSONArray("dataList");
                    if(!dataList.isEmpty()) {
                        MtResVo resVo = new MtResVo();
                        resVo.setCode(200);
                        MtDataVo dataVo = new MtDataVo();
                        resVo.setMsg(dataVo);
                        dataVo.setPositionIndex(data.getString("scrollId"));
                        ArrayList<MailvorMtOrder> records = new ArrayList<>(dataList.size());
                        for(Object obj : dataList) {
                            JSONObject dataObj = (JSONObject) obj;
                            Long refundTime = dataObj.getLong("refundTime");
                            Long payTime = dataObj.getLong("payTime");
                            Long updateTime = dataObj.getLong("updateTime");
                            MailvorMtOrder order = MailvorMtOrder.builder()
                                    .uniqueItemId(dataObj.getLong("orderId"))
                                    .orderId(dataObj.getLong("orderId"))
                                    //300等于3%
                                    .balanceCommissionRatio(dataObj.getDouble("commissionRate")/10000)
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
                            if(status == 2){
                                order.setItemStatus(0);
                                order.setItemBizStatus(1);
                            } else if(status == 3){
                                order.setItemStatus(1);
                                order.setItemBizStatus(2);
                            } else if(status == 4 || status == 5){
                                order.setItemStatus(3);
                                order.setItemBizStatus(99);
                            } else if(status == 6){
                                order.setItemStatus(1);
                                order.setItemBizStatus(3);
                            }
                            String sid = dataObj.getString("sid");
                            if(StringUtils.isNotBlank(sid)) {
                                order.setUid(Long.parseLong(sid.substring(4)));
                            }
                            records.add(order);
                        }
                        dataVo.setRecords(records);
                        return resVo;
                    }

                }
            }
            return null;
        } catch (Exception e) {
            log.error("采集美团订单错误：{}", e);
            return null;
        }
    }

    public JSONObject getCode(MeituanLinkParam param, Long uid) throws Exception {
        //请求path，不同的接口修改为对应的接口，以下只是一个接口
        String path = "/cps_open/common/api/v1/get_referral_link";
        param.setSid(MT_MEDIUM_PREFIX + uid);
        return requestMeituan(param, path);
    }


    public JSONObject requestMeituan(MeituanBaseParam param, String path) throws Exception {

        String body = JSON.toJSONString(param);

        Map<String, String> headers = new HashMap();
        //（必填）根据期望的Response内容类型设置
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //（必填）Body MD5,服务端会校验Body内容是否被篡改
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(body));
        //（POST请求必选）请求Body内容格式
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_JSON);

        // 其他接口协议定义的请求头，具体参见接口；或者媒体自己增加的请求头
        headers.put("Media-Request-Id", ""+System.currentTimeMillis());
        //自定义参与签名Header前缀（可选,默认只有"S-Ca-"开头的参与到Header签名）
        List<String> customHeadersToSignPrefix = new ArrayList<String>();
        // 必须放入的签名认证请求头S-Ca-App和S-Ca-Timestamp，则需要放入"S-Ca-Signature-Headers"请求头中!!!，必须放入
        customHeadersToSignPrefix.add("S-Ca-App");
        customHeadersToSignPrefix.add("S-Ca-Timestamp");
        // 其他接口协议定义的验签请求头，具体参见接口；或者媒体自己增加的验签请求头，保证数据安全，对应在headers里面必须有值
        customHeadersToSignPrefix.add("Cps-Request-Id");

        Request request = new Request(Method.POST_STRING, HttpSchema.HTTPS + HOST, path,
                meituanConfig.getAppKey(), meituanConfig.getAppSecret(), Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        request.setSignHeaderPrefixList(customHeadersToSignPrefix);
        //request不用在放入bodys和querys，这个是其他请求方式使用的，除非接口指定需要使用，默认当前暂不使用
        //request.setBodys(null);
        //request.setQuerys(null);

        //一定为原始计算md5的body
        request.setStringBody(body);

        //调用服务端
        Response response = Client.execute(request);

        return JSON.parseObject(response.getBody());
    }

    public static void main(String[] args) {
        MeituanService service = new MeituanService();
        service.meituanConfig = new MeituanConfig();
        service.meituanConfig.setAppKey("69046b24b3c87d643a336fa5836");
        service.meituanConfig.setAppSecret("cd71f10457398c50e0a4c36dcaf");
        MeituanOrderParam param = new MeituanOrderParam();
//        param.setScrollId("[1720663736, \"1\", \"1\", \"3101155483703593602\"]");
        param.setStartTime(DateUtil.beginOfDay(new Date()).getTime()/1000);
        param.setEndTime(DateUtil.endOfDay(new Date()).getTime()/1000);
        service.order(param);
    }
}
