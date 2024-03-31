package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.tk.config.DataokeConfig;
import com.mailvor.modules.tk.param.RankingListParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.util.HttpUtil;
import com.mailvor.modules.tk.util.SignMD5Util;
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

import static com.mailvor.modules.tk.constants.TkConstants.*;

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
            list.stream().forEach(subData->{
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
            JSONObject data = dataokeService.rankingList(param);;
            //接口数据缓存2个小时
            redisUtils.set(HOME_DATA_HOT, data, HOME_DATA_EXPIRED);
            return data;
        }
        return dataObj;
    }

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

    @GetMapping(value = "/everyone/buy")
    public Object everyoneBuy() {
        Object dataObj = redisUtils.get(HOME_DATA_EVERY);
        if(dataObj == null) {
            String url = API_PREFIX + "/goods/search?version=v1.0.0&appKey=612bcfe884763&choice=1&brand=1&activityGroup=1,3,4,6,11&sign=448080add3799cb87538ad6116786763&pageId=1&pageSize=10";
            JSONObject data = restTemplate.getForObject(url, JSONObject.class);

            //接口数据缓存6个小时
            redisUtils.set(HOME_DATA_EVERY, data, HOME_DATA_EXPIRED/4);
            return data;
        }
        return dataObj;
    }

    @GetMapping(value = "/hot-words")
    public JSONObject hotWords() {
        //https://openapi.dataoke.com/api/etc/search/list-hot-words
        return restTemplate
                .getForObject(
                        String.format("%s/etc/search/list-hot-words?version=v1.0.0&appKey=612bcfe884763&sign=8beae01e0da745fc62fcc954fafa2944",
                        API_PREFIX), JSONObject.class);
    }
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


    @GetMapping(value = "/big/list")
    public Object goodsBigList(@RequestParam(defaultValue = "1") Integer pageId,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam String params) {
        String url = String.format("%s/goods/search",
                API_PREFIX, pageId, pageSize, config.getKey());
        TreeMap<String, String> paraMap = new TreeMap<>();
//        paraMap.put("version", "v1.0");
        paraMap.put("appKey", config.getKey());
        String[] keys = params.split("\\?&amp;")[1].split("&amp;");
        for(String key: keys) {
            String[] pams = key.split("=");
            paraMap.put(pams[0], pams[1]);
        }
//        paraMap.put("pageId", pageId.toString());
//        paraMap.put("pageSize", pageSize.toString());
//        paraMap.put("add_type", "0");
//        paraMap.put("chooseIndex", "4");
//        paraMap.put("searchType", "1");
//        paraMap.put("classify", cid.toString());
//        paraMap.put("salesNumMin", "100");
//        paraMap.put("minCouponPrice", "100");
//        paraMap.put("platform-select", "1");
//        paraMap.put("collector-select", "0");
//        paraMap.put("goods_type", "platform-goods");
//        paraMap.put("cids", cid.toString());
//
//        paraMap.put("minSales", "100");
//        paraMap.put("sortType", "4");
//        paraMap.put("goods_num", "0");
//        paraMap.put("cids", "4");
        String sign = SignMD5Util.getSignStr(paraMap, config.getSecret());
        paraMap.put("sign", sign);

        String data = "";
        try {
            data = HttpUtil.httpGetRequest(url, paraMap);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return JSON.parseObject(data);
    }

    /**
     * 部分页面分类
     * 319=捡漏清单-暂未使用 411=大额优惠券
     *
     * @return the object
     */
    @GetMapping(value = "/cate")
    public Object pickCate(String id) {
        String url = CMS_PREFIX + "/column-conf?preview=&id="+ id;
        return restTemplate.getForObject(url, JSONObject.class);
    }

    @GetMapping(value = "/check/cate")
    public JSONObject checkCate() {
        return restTemplate
                .getForObject(CHECK_CATE, JSONObject.class);
    }

    @GetMapping(value = "/brand/goods/list")
    public JSONObject brandGoodsList(Integer pageId, Integer pageSize, String brandId) {
        return restTemplate
                .getForObject(String.format(
                        "%s/delanys/brand/get-goods-list?version=v1.0.0&pageId=%s&pageSize=%s&brandId=%s&appKey=%s",
                        API_PREFIX, pageId, pageSize, brandId, config.getKey()), JSONObject.class);
    }
    @GetMapping(value = "/nine/cate")
    public JSONObject nineCate() {
        return restTemplate
                .getForObject(NINE_CATE, JSONObject.class);
    }
    @GetMapping(value = "/nine/top")
    public JSONObject nineTOP() {
        return restTemplate
                .getForObject(NINE_TOP, JSONObject.class);
    }

    @GetMapping(value = "/nine/list")
    public JSONObject nineList(Integer pageId, Integer pageSize, String cid) {
        return restTemplate
                .getForObject(String.format(NINE_LIST,
                        cid, pageId, pageSize), JSONObject.class);
    }
}
