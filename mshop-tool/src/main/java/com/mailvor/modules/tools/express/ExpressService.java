/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.express;

import cn.hutool.http.HttpUtil;
import com.mailvor.enums.ShipperCodeEnum;
import com.mailvor.modules.tools.express.dao.ExpressInfo;
import com.mailvor.modules.tools.express.config.ExpressProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Base64Utils;

import java.io.Serializable;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 物流查询服务
 * <p>
 * 快递鸟即时查询API http://www.kdniao.com/api-track
 */
public class ExpressService implements Serializable {

    private final Log logger = LogFactory.getLog(ExpressService.class);
    //请求url
    private String ReqURL = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";

    private ExpressProperties properties;

    public ExpressProperties getProperties() {
        return properties;
    }

    public void setProperties(ExpressProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取物流供应商名
     *
     * @param vendorCode
     * @return
     */
    public String getVendorName(String vendorCode) {
        for (Map<String, String> item : properties.getVendors()) {
            if (item.get("code").equals(vendorCode)) {
                return item.get("name");
            }
        }
        return null;
    }

    /**
     * 获取物流信息
     *
     * @param OrderCode
     * @param ShipperCode
     * @return
     */
    public ExpressInfo getExpressInfo(String OrderCode, String ShipperCode, String LogisticCode, String lastFourNumber) {
        try {
            String result = getOrderTracesByJson(OrderCode,ShipperCode, LogisticCode,lastFourNumber);
            ObjectMapper objMap = new ObjectMapper();
            ExpressInfo ei = objMap.readValue(result, ExpressInfo.class);
            ei.setShipperName(getVendorName(ShipperCode));
            return ei;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Json方式 查询订单物流轨迹
     *
     * @throws Exception
     */
    private String getOrderTracesByJson(String OrderCode,String ShipperCode, String LogisticCode,String lastFourNumber) throws Exception {
        if (!properties.isEnable()) {
            return null;
        }

        //处理顺丰查询轨迹需手机号码后4位
        String requestData;
        if (ShipperCode.equals(ShipperCodeEnum.SF.getValue())) {
            requestData = "{'OrderCode':'" + OrderCode + "','ShipperCode':'" + ShipperCode + "','LogisticCode':'" + LogisticCode + "','CustomerName':'" + lastFourNumber + "'}";
        } else {
            requestData = "{'OrderCode':'" + OrderCode + "','ShipperCode':'" + ShipperCode + "','LogisticCode':'" + LogisticCode + "'}";
        }

        Map<String, Object> params = new HashMap<>();
        params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
        params.put("EBusinessID", properties.getAppId());
        params.put("RequestType", "1002");
        String dataSign = encrypt(requestData, properties.getAppKey(), "UTF-8");
        params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
        params.put("DataType", "2");

        String result = HttpUtil.post(ReqURL, params);

        //根据公司业务处理返回的信息......

        return result;
    }

    /**
     * MD5加密
     *
     * @param str     内容
     * @param charset 编码方式
     * @throws Exception
     */
    private String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Sign签名生成
     *
     * @param content  内容
     * @param keyValue Appkey
     * @param charset  编码方式
     * @return DataSign签名
     */
    private String encrypt(String content, String keyValue, String charset) {
        if (keyValue != null) {
            content = content + keyValue;
        }
        byte[] src;
        try {
            src = MD5(content, charset).getBytes(charset);
            return Base64Utils.encodeToString(src);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }


}
