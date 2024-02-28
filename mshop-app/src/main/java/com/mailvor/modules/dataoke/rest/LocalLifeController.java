package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.MshopException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.tk.service.dto.DyLifeCityChildrenDto;
import com.mailvor.modules.tk.service.dto.DyLifeCityDto;
import com.mailvor.modules.tk.service.dto.DyLifeCityListDto;
import com.mailvor.modules.user.domain.MwUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ku")
@Slf4j
@Api(tags = "商城：本地生活")
public class LocalLifeController {
    static final String GEOCODER_URL = "http://api.map.baidu.com/geocoder?location=%s,%s&output=json";

    static Map<String, Integer> geoMap = new HashMap<>();

    @Resource
    private KuService kuService;

    @Resource
    private RestTemplate restTemplate;
    /**
     *
     * @param platform 平台ID：1. 星巴克；2. 华莱士；3. 瑞幸咖啡；5. 肯德基；6. 喜茶；7. 电影票；8. 奈雪的茶；9. 麦当劳
     * @return the word
     */

    @ApiOperation(value = "本地生活列表")
    @UserCheck
    @GetMapping(value = "/locallife")
    public JSONObject localLife(@RequestParam String platform) {
        String channel;
        MwUser mwUser = LocalUser.getUser();
        if(mwUser != null) {
            channel = mwUser.getUid().toString();
        } else {
            channel = "0";
        }
        //平台ID：1. 星巴克；2. 华莱士；3. 瑞幸咖啡；5. 肯德基；6. 喜茶；7. 电影票；8. 奈雪的茶；9. 麦当劳
        //channel 自有平台代理标识，传入订单接口同步返回（1-30字符，仅支持 英文、"_"、数字）
        return kuService.localLife(platform, channel);
    }

    @ApiOperation(value = "本地生活订单采集")
    @GetMapping(value = "/locallife/order")
    public JSONObject localLifeOrder(@RequestParam(required = false, defaultValue = "1") Integer page,
                                     @RequestParam(required = false, defaultValue = "100") Integer size,
                                     Long startTime, Long endTime, Integer platform) {

        return kuService.localLifeOrder(page, size, startTime, endTime, platform);
    }


    @ApiOperation(value = "抖音本地生活：获取城市")
    @GetMapping(value = "/dy/life/getcity")
    public JSONObject getCity(@RequestParam Double longitude,
                                 @RequestParam Double latitude) {
        if(geoMap.isEmpty()) {
            //生成省份_城市和值对应的map
            DyLifeCityDto cityDto = kuService.dyLifeCityList();
            List<DyLifeCityListDto> listDtos = cityDto.getData().getList();
            for(DyLifeCityListDto listDto : listDtos) {
                List<DyLifeCityChildrenDto> childrenDtos = listDto.getChildren();
                for(DyLifeCityChildrenDto childrenDto : childrenDtos) {
                    geoMap.put(listDto.getTitle() + "_" + childrenDto.getTitle(), childrenDto.getVal());
                }
            }
        }
        String province = null;
        String city = null;
        String district = null;
        Integer val = null;
        String url = String.format(GEOCODER_URL, latitude, longitude);
        String str = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(str);
        if("OK".equals(jsonObject.getString("status"))) {
            JSONObject addressObj = jsonObject.getJSONObject("result").getJSONObject("addressComponent");
            province = addressObj.getString("province");
            city = addressObj.getString("city");
            district = addressObj.getString("district");
            val = geoMap.get(province + "_" + city);
            if(val == null) {
                //直辖市
                val = geoMap.get(province + "_" + district);
            }
        }
        if(val == null) {
            throw new MshopException("无法获取定位");
        }
        JSONObject res = new JSONObject();
        res.put("val", val);
        res.put("province", province);
        res.put("city", city);
        res.put("district", district);
        res.put("longitude", longitude);
        res.put("latitude", latitude);

        return res;
    }


    @ApiOperation(value = "抖音本地生活：商品列表")
    @GetMapping(value = "/dy/life/goods/list")
    public JSONObject dyLifeList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "10") Integer size,
                                 @RequestParam(required = false) String cityCode,
                                 @RequestParam(required = false) String categoryId,
                                 @RequestParam(required = false) Integer sort,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Double longitude,
                                 @RequestParam(required = false) Double latitude) {

        return kuService.dyLifeList(page, size, cityCode, categoryId, sort, keyword, longitude, latitude);
    }

    @ApiOperation(value = "抖音本地生活：城市列表")
    @GetMapping(value = "/dy/life/city/list")
    public DyLifeCityDto dyLifeCityList() {

        return kuService.dyLifeCityList();
    }


    @ApiOperation(value = "抖音本地生活：分类")
    @GetMapping(value = "/dy/life/category/list")
    public DyLifeCityDto dyLifeCategoryList() {

        return kuService.dyLifeCategoryList();
    }

    @ApiOperation(value = "抖音本地生活：转链")
    @UserCheck
    @GetMapping(value = "/dy/life/goods/word")
    public JSONObject dyLife(@RequestParam String id) {
        String channel;
        MwUser mwUser = LocalUser.getUser();
        if(mwUser != null) {
            channel = mwUser.getUid().toString();
        } else {
            channel = "0";
        }
        //平台ID：1. 星巴克；2. 华莱士；3. 瑞幸咖啡；5. 肯德基；6. 喜茶；7. 电影票；8. 奈雪的茶；9. 麦当劳
        //channel 自有平台代理标识，传入订单接口同步返回（1-30字符，仅支持 英文、"_"、数字）
        return kuService.dyLifeWord(id, channel);
    }
}
