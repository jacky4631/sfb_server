package com.mailvor.modules.dataoke.rest;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtk.api.utils.SignMd5Util;
import com.mailvor.modules.tk.config.DataokeConfig;
import com.mailvor.modules.tk.param.RankingListParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.mailvor.modules.tk.constants.TkConstants.*;

/**
 * 大淘客cms接口控制器
 * @author mailvor
 */
@RestController
@RequestMapping("/cms")
@Slf4j
public class DataokeCmsController {
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private DataokeConfig config;

    @Resource
    private DataokeService dataokeService;

    @GetMapping(value = "/brand/list")
    public Object brandList() {
        Object dataObj = redisUtils.get(HOME_DATA_BRAND_LIST);
        if(dataObj == null) {
            JSONObject data = restTemplate.getForObject(String.format("%s/brand-list", CMS_PREFIX), JSONObject.class);
            List list = data.getJSONObject("data").getJSONArray("list").subList(0, 3);
            int splitSize = 4;
            list.forEach(subData->{
                Map map = (Map)subData;
                List subList = (List)map.get("list");
                map.put("list", subList.size() < splitSize ? subList : subList.subList(0, splitSize));
            });
            //接口数据缓存6小时
            redisUtils.set(HOME_DATA_BRAND_LIST, list, HOME_DATA_EXPIRED);
            return list;
        }
        return dataObj;
    }

    @GetMapping(value = "/hot")
    public Object hot() {
        Object dataObj = redisUtils.get(HOME_DATA_HOT);
        if(dataObj == null) {
            RankingListParam param = new RankingListParam();
            param.setRankType(1);
            param.setPageId(1);
            param.setPageSize(2);
            JSONObject data = dataokeService.rankingList(param);
            //接口数据缓存2个小时
            redisUtils.set(HOME_DATA_HOT, data, HOME_DATA_EXPIRED);
            return data;
        }
        return dataObj;
    }

    /**
     * 获取大淘客cms首页数据
     */
    @GetMapping(value = "/ddq")
    public Object ddq() {
        Object dataObj = redisUtils.get(HOME_DATA_DDQ);
        if(dataObj == null) {
            JSONObject data = dataokeService.ddq(null);
            //接口数据缓存24个小时
            redisUtils.set(HOME_DATA_DDQ, data, HOME_DATA_EXPIRED);
            return data;
        }
        return dataObj;
    }

    /**
     * 每天必买
     */
    @GetMapping(value = "/everyone/buy")
    public Object everyoneBuy() throws URISyntaxException {
        Object dataObj = redisUtils.get(HOME_DATA_EVERY);
        if(dataObj == null) {
            TreeMap<String, Object> paraMap = new TreeMap<>();
            paraMap.put("version", "v1.0.0");
            paraMap.put("appKey", config.getKey());
            paraMap.put("pageId", "1");
            paraMap.put("pageSize", "10");
            paraMap.put("choice", "1");
            paraMap.put("brand", "1");
            paraMap.put("activityGroup", "1,3,4,6,11");
            String urlParams = paraMap.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
            paraMap.put("sign", SignMd5Util.sign(urlParams, config.getSecret()));
            String res = HttpUtil.get(API_PREFIX + "/goods/search", paraMap);
            JSONObject data = JSON.parseObject(res);

            //接口数据缓存6个小时
            redisUtils.set(HOME_DATA_EVERY, data, HOME_DATA_EXPIRED/4);
            return data;
        }
        return dataObj;
    }

    /**
     * 热搜词
     */
    @GetMapping(value = "/hot-words")
    public JSONObject hotWords() {
        // 使用配置中的appKey动态生成签名
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v1.0.0");
        paraMap.put("appKey", config.getKey());
        String urlParams = paraMap.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        String sign = SignMd5Util.sign(urlParams, config.getSecret());
        return restTemplate
                .getForObject(
                        String.format("%s/etc/search/list-hot-words?version=v1.0.0&appKey=%s&sign=%s",
                        API_PREFIX, config.getKey(), sign), JSONObject.class);
    }

    /**
     * 排行榜分类
     */
    @GetMapping(value = "/ranking/cate")
    public JSONObject rankingCate() {
        JSONObject data = (JSONObject) redisUtils.get(HOME_DATA_TOP_CATE);
        if(data == null) {
            data = restTemplate
                    .getForObject(RANKING_CATE, JSONObject.class);
            //接口数据缓存24小时
            redisUtils.set(HOME_DATA_TOP_CATE, data, HOME_DATA_EXPIRED);
            return data;
        }
        return data;
    }

    /**
     * 大牌商品列表
     */
    @GetMapping(value = "/big/list")
    public Object goodsBigList(@RequestParam(defaultValue = "1") Integer pageId,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam String params) {
        String url = String.format("%s/goods/search", API_PREFIX);
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("appKey", config.getKey());
        String[] keys = params.split("\\?&amp;")[1].split("&amp;");
        for(String key: keys) {
            String[] pams = key.split("=");
            paraMap.put(pams[0], pams[1]);
        }
        String urlParams = paraMap.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        paraMap.put("sign", SignMd5Util.sign(urlParams, config.getSecret()));

        String data = HttpUtil.get(url, paraMap);
        return JSON.parseObject(data);
    }

    /**
     * 部分页面分类
     * 319=捡漏清单-暂未使用 411=大额优惠券
     */
    @GetMapping(value = "/cate")
    public Object pickCate(String id) {
        String url = CMS_PREFIX + "/column-conf?preview=&id="+ id;
        return restTemplate.getForObject(url, JSONObject.class);
    }

    /**
     * 检查分类
     */
    @GetMapping(value = "/check/cate")
    public JSONObject checkCate() {
        return restTemplate
                .getForObject(CHECK_CATE, JSONObject.class);
    }

    /**
     * 品牌商品列表
     */
    @GetMapping(value = "/brand/goods/list")
    public JSONObject brandGoodsList(Integer pageId, Integer pageSize, String brandId) {
        return restTemplate
                .getForObject(String.format(
                        "%s/delanys/brand/get-goods-list?version=v1.0.0&pageId=%s&pageSize=%s&brandId=%s&appKey=%s",
                        API_PREFIX, pageId, pageSize, brandId, config.getKey()), JSONObject.class);
    }

    /**
     * 9块9分类
     */
    @GetMapping(value = "/nine/cate")
    public JSONObject nineCate() {
        return restTemplate
                .getForObject(NINE_CATE, JSONObject.class);
    }

    /**
     * 9块9榜单
     */
    @GetMapping(value = "/nine/top")
    public JSONObject nineTOP() {
        return restTemplate
                .getForObject(NINE_TOP, JSONObject.class);
    }

    /**
     * 9块9商品列表
     */
    @GetMapping(value = "/nine/list")
    public JSONObject nineList(Integer pageId, Integer pageSize, String cid) {
        return restTemplate
                .getForObject(String.format(NINE_LIST,
                        cid, pageId, pageSize), JSONObject.class);
    }
}
