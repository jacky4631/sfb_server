package com.mailvor.modules.tk.service;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.request.get.PromotionCodeReq;
import com.jd.open.api.sdk.request.kplunion.UnionOpenPromotionCommonGetRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenPromotionCommonGetResponse;
import com.mailvor.modules.tk.config.JdConfig;
import com.mailvor.modules.tk.param.ParseJdParam;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 * @author shenji
 * @date 2022/10/27
 */
@Slf4j
@Component
public class JdService {

    @Resource
    private JdConfig jdConfig;

    /**
     *
     *{
     *       "jd_union_open_promotion_common_get_response": {
     *             "getResult": {
     *                   "code": "200",
     *                   "data": {
     *                         "clickURL": "http://union-click.jd.com/jdc?XXXXXXXXXX",
     *                         "jCommand": "6.0复制整段话 http://JhT7V5wlKygHDK京口令内容#J6UFE5iMn***"
     *                   },
     *                   "message": "success"
     *             }
     *       }
     * }
     *
     * 异常示例
     * jsonxml
     * {
     *       "code": "2001906",
     *       "errorMessage": "优惠券已过期",
     *       "errorSolution": "更换优惠券链接"
     * }
     *
     * */
    public UnionOpenPromotionCommonGetResponse getWords(ParseJdParam param) throws Exception {
        JdClient client=new DefaultJdClient(jdConfig.getServer(),null,jdConfig.getAppKey(),jdConfig.getAppSecret());
        UnionOpenPromotionCommonGetRequest request=new UnionOpenPromotionCommonGetRequest();
        PromotionCodeReq promotionCodeReq=new PromotionCodeReq();
        promotionCodeReq.setMaterialId(param.getMaterialId());
        promotionCodeReq.setCouponUrl(param.getCouponLink());
        if(StringUtils.isNotBlank(param.getSiteId())) {
            promotionCodeReq.setSiteId(param.getSiteId());
        } else {
            promotionCodeReq.setSiteId(jdConfig.getSiteId());
        }
        if(param.getPositionId() !=null ){
            promotionCodeReq.setPositionId(Long.parseLong(param.getPositionId()));
        }
        request.setPromotionCodeReq(promotionCodeReq);
        request.setVersion("1.0");
        return client.execute(request);
    }
}
