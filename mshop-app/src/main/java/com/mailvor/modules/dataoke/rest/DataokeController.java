package com.mailvor.modules.dataoke.rest;

import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.param.*;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.service.TkService;
import com.mailvor.modules.tk.vo.DataokeResVo;
import com.mailvor.modules.tk.vo.GoodsParseVo;
import com.mailvor.modules.user.config.AppDataConfig;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

import static com.mailvor.modules.tk.constants.TkConstants.*;

@RestController
@RequestMapping("/tao")
@Slf4j
public class DataokeController {
    @Resource
    private DataokeService service;

    @Resource
    private TkService tkService;

    @Value("${tb.pid.channelPid}")
    private String channelPid;

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RestTemplate restTemplate;

    @Resource
    private MwUserUnionService userUnionService;

    @Resource
    private MwSystemConfigService systemConfigService;
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
            pid = channelPid;
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

        return service.goodsParse(content);
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
                param.setPid(channelPid);
                param.setRelationId(userUnion.getTbPid());
            }
        }
        return service.parseTbActivity(param);
    }

    @UserCheck
    @GetMapping(value = "/parse/content")
    public Object parseContent(ParseContentParam param) throws UnsupportedEncodingException {
        MwUser mwUser = LocalUser.getUser();
        return tkService.mixParse(param, mwUser);
    }

    @GetMapping(value = "/parse/content2")
    public JSONObject parseContent2(ParseContentParam param) {
        return tkService.mixParse2(param, LocalUser.getUser());
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
                pid = channelPid;
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
}
