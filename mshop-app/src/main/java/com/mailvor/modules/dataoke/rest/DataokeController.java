package com.mailvor.modules.dataoke.rest;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.MshopException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.config.TbConfig;
import com.mailvor.modules.tk.param.*;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.service.TkService;
import com.mailvor.modules.tk.vo.DataokeResVo;
import com.mailvor.modules.tk.vo.GoodsParseVo;
import com.mailvor.modules.tk.vo.TkParseCodeVO;
import com.mailvor.modules.user.config.AppDataConfig;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.user.service.dto.TljDataDto;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mailvor.constant.SystemConfigConstants.TLJ_KEY;
import static com.mailvor.modules.tk.constants.TkConstants.HOME_DATA_CATEGORY_TB;

@RestController
@RequestMapping("/tao")
@Slf4j
public class DataokeController {
    @Resource
    private DataokeService service;

    @Resource
    private TkService tkService;

    @Resource
    private TbConfig tbConfig;

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RestTemplate restTemplate;

    @Resource
    private MwUserUnionService userUnionService;

    @Resource
    private MwSystemConfigService systemConfigService;

    @Resource
    private MwUserService userService;
    /**
     * redis保存在线地址 key:url.home, value参照根目录data/home.json
     * */
    @UserCheck
    @GetMapping(value = "/home/url")
    public AppDataConfig getUrl() {
        AppDataConfig config = systemConfigService.getAppDataConfig();

        if(LocalUser.getUser() != null) {
            //替换分享地址
            String shareUrl = config.getShare();
            shareUrl += "?v=" + Base64Encoder.encode(LocalUser.getUser().getCode());
            config.setShare(shareUrl);
        }
        return config;
    }

    @GetMapping(value = "/goods/list")
    public JSONObject getGoodList(GoodsListParam goodQueryParam) {

        return service.goodsList(goodQueryParam);
    }

    @GetMapping(value = "/goods/search")
    public JSONObject goodsSearch(GoodsSearchParam goodQueryParam) {

        return service.goodsSearch(goodQueryParam);
    }
    @GetMapping(value = "/goods/detail")
    public JSONObject getGoodsDetail(String goodsId) {

        return service.goodsDetail(goodsId);
    }

    @GetMapping(value = "/goods/comment/list")
    public JSONObject getCommentList(GoodsCommentParam param) {

        return service.getCommentList(param);
    }

    /**
     * Goods word json object.
     *
     * @param goodsId the goods id
     * @param type    默认传空， type=share时，说明分享生成的口令，pid需要绑定更长时间，5小时，
     * @param uid 如果uid存在不等于0说明是店铺订单
     * @return the json object
     */
    @UserCheck
    @GetMapping(value = "/goods/word")
    public JSONObject goodsWord(String goodsId, @RequestParam(required = false) String type,
                                @RequestParam(required = false) Long uid) {

        String pid = null;
        MwUserUnion userUnion = null;
        MwUser mwUser = LocalUser.getUser();
        if(uid != null && uid > 0) {
            userUnion = userUnionService.getOne(uid);
        } else {
            if(mwUser != null) {
                userUnion = userUnionService.getOne(mwUser.getUid());
            }

        }

        //获取pid为了实现自动追单
        String channelId = null;
        if(userUnion != null && StringUtils.isNotBlank(userUnion.getTbPid())) {
            //如果渠道id存在使用渠道id，并使用渠道pid
            pid = tbConfig.getChannelPid();
            channelId = userUnion.getTbPid();
        }
        if(channelId == null && mwUser != null) {
            pid = RedisUtil.getPid(mwUser.getUid(), type);
        }
        JSONObject resVo = service.goodsWord(goodsId, pid, channelId);

        return resVo;

    }

    @GetMapping(value = "/goods/parse")
    public DataokeResVo<GoodsParseVo> goodsParse(String content) {

        return service.goodsParse(content, null, null);
    }

    @GetMapping(value = "/goods/category")
    public JSONObject getCategory() {
        JSONObject homeCategory = (JSONObject) redisUtils.get(HOME_DATA_CATEGORY_TB);
        if(homeCategory == null) {
            homeCategory = service.getCategory();
            //接口数据缓存24小时
            redisUtils.set(HOME_DATA_CATEGORY_TB, homeCategory, 24*3600);

        }
        return homeCategory;
    }

    @GetMapping(value = "/topic/list")
    public JSONObject getTopic() {

        return service.getTopic();
    }

    @GetMapping(value = "/banner/list")
    public JSONObject getBanner() {

        return service.getBanner();
    }


    @GetMapping(value = "/activity/list")
    public JSONObject getTbActivityList(TbActivityListParam param) {

        return service.getTbActivityList(param);
    }

    @UserCheck
    @GetMapping(value = "/activity/parse")
    public JSONObject parseTbActivityList(TbActivityParseParam param) {
        MwUser mwUser = LocalUser.getUser();
        //获取pid为了实现自动追单
        if(mwUser != null) {
            MwUserUnion userUnion = userUnionService.getOne(mwUser.getUid());
            //如果渠道id存在使用渠道id，并使用渠道pid
            if(userUnion != null && StringUtils.isNotBlank(userUnion.getTbPid())) {
                param.setPid(tbConfig.getChannelPid());
                param.setRelationId(userUnion.getTbPid());
            }
        }
        return service.parseTbActivity(param);
    }

    @UserCheck
    @PostMapping(value = "/parse/content")
    public TkParseCodeVO parseContentPost(@RequestBody ParseContentParam param) {
        MwUser mwUser = LocalUser.getUser();
        return tkService.mixParse(param, mwUser);
    }

    @GetMapping(value = "/goods/similar/list")
    public JSONObject getGoodSimilarList(@RequestParam String id, @RequestParam(defaultValue = "10") String size) {

        return service.goodsSimilarList(id, size);
    }

    @GetMapping(value = "/ddq")
    public JSONObject ddq(@RequestParam String roundTime) {

        return service.ddq(roundTime);
    }

    @GetMapping(value = "/ranking/list")
    public JSONObject rankingList(RankingListParam param) {
        return service.rankingList(param);
    }

    @UserCheck
    @GetMapping(value = "/shop/convert")
    public JSONObject shopConvert(String shopId, String shopName) {

        MwUser mwUser = LocalUser.getUser();
        //获取pid为了实现自动追单
        String pid = null;
        String channelId = null;
        if(mwUser != null) {
            MwUserUnion userUnion = userUnionService.getOne(mwUser.getUid());
            //如果渠道id存在使用渠道id，并使用渠道pid
            if(userUnion != null && StringUtils.isNotBlank(userUnion.getTbPid())) {
                pid = tbConfig.getChannelPid();
                channelId = userUnion.getTbPid();
            }
        }
        if(channelId == null && mwUser != null) {
            pid = RedisUtil.getPid(mwUser.getUid(), "self");
        }
        return service.shopConvert(shopId, shopName, pid, channelId);
    }

    @GetMapping(value = "/brand/list")
    public JSONObject getBrandList(Integer cid, Integer pageId) {
        if(cid == 0) {
            return restTemplate
                    .getForObject(String.format("https://cmscg.dataoke.com/cms-v2/brand-list?page=%s&page_size=10",
                            pageId), JSONObject.class);
        }
        return service.getBrandList(cid, pageId);
    }

    @GetMapping(value = "/brand/goods/list")
    public JSONObject getBrandGoodsList(String brandId, Integer pageId, Integer pageSize) {

        return service.getBrandGoodsList(brandId, pageId, pageSize);
    }
    @UserCheck
    @GetMapping(value = "/tlj/goods/list")
    public JSONObject getZeroGoodList(GoodsListParam goodQueryParam) throws ApiException {
        goodQueryParam.setPriceUpperLimit("5");
        goodQueryParam.setCommissionRateLowerLimit("30");
        goodQueryParam.setCouponPriceLowerLimit("4");
        JSONObject res = service.goodsList(goodQueryParam);
        //如果是第一页 并且已登录 查看已领取的商品

        if(goodQueryParam.getPageId() == 1 && LocalUser.getUser() != null) {
            Long uid = LocalUser.getUser().getUid();
            MwUserUnion userUnion = userUnionService.getOne(uid);
            if(userUnion != null) {
                getUse(userUnion);
                if(userUnion.getTljData() != null && !CollectionUtils.isEmpty(userUnion.getTljData().getData())){
                    for(JSONObject data : userUnion.getTljData().getData()) {
                        JSONObject detail = data.getJSONObject("detail");
                        //表明淘礼金已经发送，可能未领取 未使用
                        detail.put("tljSend", true);
                        boolean tljGet = data.getDoubleValue("get_rate")==100;
                        detail.put("tljGet", tljGet);
                        boolean used = data.getDoubleValue("use_rate")==100;
                        detail.put("tljUse", used);
                        detail.put("tljBind", data.getLongValue("orderId") != 0);
                        //如果未使用 领取时间超过一天 显示过期
                        if(tljGet && !used) {
                            long betweenDay = DateUtil.betweenDay(DateUtil.parseDateTime(data.getString("getTljDate")), new Date(), false);
                            detail.put("tljExpired", betweenDay >= 1);
                        }
                        res.getJSONObject("data").getJSONArray("list").add(0, detail);
                    }

                }
            }
        }
        return res;
    }
    /**
     * Goods word json object.
     *
     * @param goodsId the goods id
     * @return the json object
     */
    @AuthCheck
    @GetMapping(value = "/tlj/goods/word")
    public JSONObject tljGoodsWord(String goodsId) throws ApiException {

        Long uid = LocalUser.getUser().getUid();
        MwUserUnion userUnion = userUnionService.getOne(uid);

        //获取pid为了实现自动追单
        String channelId = null;
        if(userUnion != null && StringUtils.isNotBlank(userUnion.getTbPid())) {
            //如果渠道id存在使用渠道id，并使用渠道pid
            channelId = userUnion.getTbPid();
        }
        String pid = tbConfig.getTljPid();
        //如果淘礼金领取次数>=2 判断淘礼金是否使用了，未使用，可以使用，已经使用，直接返回
        //如果淘礼金领取次数=1 需要校验用户粉丝是否大于1，才能领取
        //如果淘礼金领取次数=0
        if(userUnion.getTljCount() >= 2) {
            JSONObject useData = getUse(userUnion);
            //如果已经使用，返回错误
            JSONObject data = useData.getJSONObject(goodsId);
            if(data != null && data.getBooleanValue("use")) {
                throw new MshopException("已经超过补贴次数");
            }
        } else if (userUnion.getTljCount() == 1) {
            //如果已经使用 提示邀请好友 如果还没使用，允许使用
            JSONObject useData = getUse(userUnion);
            JSONObject data = useData.getJSONObject(goodsId);
            //如果不是同一个商品，提示邀请好友
            if(data == null) {
                Long count = userService.getSpreadCount(uid, ShopCommonEnum.GRADE_0.getValue());
                if(count == 0) {
                    throw new MshopException("邀请好友后方可领取补贴");
                }
            } else {
                //如果已经使用，提示已经使用
                if(data.getBooleanValue("use")) {
                    throw new MshopException("该礼金已经使用");
                }
            }

        }
        String suffixGoodsId = "";
        String[] goodsIdSplit = goodsId.split("-");
        if(goodsIdSplit.length > 1) {
            suffixGoodsId = goodsIdSplit[1];
        } else {
            suffixGoodsId = goodsId;
        }

        String tljKey = TLJ_KEY + uid + ":" + suffixGoodsId;
        Object redisData = redisUtils.get(tljKey);
        String sendUrl = null;
        if(redisData != null) {
            JSONObject tljObj = (JSONObject) redisData;
            sendUrl = tljObj.getString("send_url");
        } else {

            JSONObject detail = service.goodsDetail(goodsId);
            JSONObject data = detail.getJSONObject("data");
            double startPrice = data.getDoubleValue("originalPrice");
            double couponPrice = data.getDoubleValue("couponPrice");
            double tljPrice = startPrice - couponPrice - 1;
            //限制最大淘礼金金额为10元，防止被刷
            if(tljPrice > 10 || tljPrice < 1) {
                throw new MshopException("该商品不支持淘礼金");
            }
            JSONObject tljObj = tkService.createTlj(goodsId, tljPrice);
            JSONObject tljRes = tljObj.getJSONObject("tbk_dg_vegas_tlj_create_response").getJSONObject("result");
            boolean success = tljRes.getBooleanValue("success");
            if(!success) {
                throw new MshopException("淘礼金领取失败，请换商品重试");
            }
            JSONObject tljData = tljRes.getJSONObject("model");
            sendUrl = tljData.getString("send_url");
            tljData.put("goodsId", goodsId);
            tljData.put("detail", data);
            tljData.put("getTljDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            //淘礼金领取成功 增加次数 设置redis 24小时过期
            userUnion.setTljCount(userUnion.getTljCount() + 1);
            TljDataDto tljDataDto = userUnion.getTljData();
            if(tljDataDto == null) {
                tljDataDto = new TljDataDto();
                List<JSONObject> tljIds = new ArrayList<>();
                tljDataDto.setData(tljIds);
                userUnion.setTljData(tljDataDto);
            }
            tljDataDto.getData().add(tljData);
            userUnionService.saveOrUpdate(userUnion);
            redisUtils.set(tljKey, tljData, 24*3600);
        }

        JSONObject resVo = new JSONObject();
        resVo.put("code", 0);
        resVo.put("url", sendUrl);
        return resVo;

    }

    @AuthCheck
    @GetMapping(value = "/tlj/use")
    public JSONObject getTljUse() throws ApiException {

        Long uid = LocalUser.getUser().getUid();
        MwUserUnion userUnion = userUnionService.getOne(uid);
        return getUse(userUnion);

    }

    protected JSONObject getUse(MwUserUnion userUnion) throws ApiException {
        JSONObject resVo = new JSONObject();
        resVo.put("code", 0);
        resVo.put("canBuy", true);
        if(userUnion.getTljData() == null) {
            return resVo;
        }
        List<JSONObject> tljDataList = userUnion.getTljData().getData();

        boolean shouldSave = false;
        for(JSONObject data : tljDataList) {
            String tljId = data.getString("rights_id");
            double useRate = data.getDoubleValue("use_rate");
            double getRate = data.getDoubleValue("get_rate");
            //如果领取并且使用 直接返回
            JSONObject rate = new JSONObject();
            if(useRate == 100 && getRate == 100) {
                rate.put("use", true);
                rate.put("get", true);
                resVo.put(data.getString("goodsId"), rate);
            } else {
                shouldSave = true;
                JSONObject tljUse = tkService.getTljUse(tljId);

                JSONObject tljRes = tljUse.getJSONObject("tbk_dg_vegas_tlj_report_response");
                if(tljRes.getBooleanValue("result_success")) {
                    JSONObject extra = tljRes.getJSONObject("model").getJSONObject("extra");
                    //因为每个用户指领取一个，所以比例只要领了比例就是100
                    //只要领取比例是100，就代表无法再次领取其他商品
                    useRate = extra.getDouble("use_rate");
                    getRate = extra.getDouble("get_rate");
                    if(getRate == 100) {
                        rate.put("get", true);
                    }
                    if(useRate == 100) {
                        rate.put("use", true);
                    }
                    resVo.put(data.getString("goodsId"), rate);
                    data.put("get_rate", getRate);
                    data.put("use_rate", useRate);
                }
            }
        }
        if(shouldSave) {
            userUnionService.saveOrUpdate(userUnion);
        }
        //如果领取次数大于等于2 无法再次领取
        if(userUnion.getTljCount() >= 2) {
            resVo.put("canBuy", false);
        }

        return resVo;
    }
}
