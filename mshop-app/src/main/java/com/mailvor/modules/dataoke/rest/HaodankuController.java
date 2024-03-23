package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.constant.ShopConstants;
import com.mailvor.modules.tk.config.PddConfig;
import com.mailvor.modules.tk.param.DyListParam;
import com.mailvor.modules.tk.param.GoodsListDyParam;
import com.mailvor.modules.tk.param.KuCustomParam;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.tk.vo.HotWordsVo;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.mailvor.modules.tk.constants.TkConstants.*;

@RestController
@RequestMapping("/ku")
@Slf4j
public class HaodankuController {
    public static final String API_CMS_PREFIX = "https://api.cmspro.haodanku.com";
    public static final String PPD_CMS_PREFIX= API_CMS_PREFIX + "/duohaodan";

    public static final String PDD_WORD = "http://v2.api.haodanku.com/get_pdditems_link";
    public static final String PDD_NAV = PPD_CMS_PREFIX + "/index/index?type=1&cid=XDgQx14e";
    public static final String PDD_CATE = PPD_CMS_PREFIX + "/index/category?type=1&cid=XDgQx14e";
    public static final String PDD_LIST = PPD_CMS_PREFIX + "/pddItem/lists?cat_id=%s&min_id=%s&cid=XDgQx14e";
    public static final String DY_NAV = PPD_CMS_PREFIX + "/index/index?type=9&cid=";
    public static final String DY_CATE = PPD_CMS_PREFIX + "/index/category?type=9&cid=";
    public static final String DY_LIST = API_CMS_PREFIX + "/douyin/item/getListsByDy?page=%s&page_size=%s&cate_id=%s&is_get_similar=0&search_type=%s&sort_type=1&cos_fee_min=1&cid=";
    public static final String PICK_CATE = "http://v2.api.haodanku.com/activity_category?code=zFxBnq&version=2.0.1";
    public static final String PICK_LIST = "http://v2.api.haodanku.com/activity_items?cat_id=%s&min_id=%s&cs=1&code=zFxBnq";
    //小样种草机
    public static final String MINI_LIST = "http://v2.api.haodanku.com/makeup_items?code=9HxUb0&keyword=%s&min_id=%s&back=10";

    //首页banner
    public static final String BANNER_LIST = API_CMS_PREFIX + "/index/index?cid=";

    public static final String CUSTOM_CATE = API_CMS_PREFIX + "/activity/hdkActivityInfo?id=%s&cid=";
    public static final String CUSTOM_LIST = API_CMS_PREFIX + "/activity/hdkActivityItemList";

    public static final String DY_WORD = API_CMS_PREFIX +"/douyin/douyin/getActivityLink";

    //link_type=6
    public static final String ACTIVITY_DETAIL = API_CMS_PREFIX +"/meetingActivity/detail?id=%s&cid=";

    public static final String HOT_WORDS_JD = "https://wq.jd.com/bases/searchhotword/GetHotWords?_=1663927810750&sceneval=2&callback=jsonpCBKB";

    public static final String HOT_WORDS_KU = "https://api.cmspro.haodanku.com/index/hotKeyword?cid=";

    private static final Long HOME_DATA_EXPIRED = 86400L;

    @Value("${haodanku.key}")
    private String key;
    @Resource
    private PddConfig pddConfig;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private KuService kuService;

    @UserCheck
    @GetMapping(value = "/pdd/goods/word")
    public JSONObject getPDDWord(String goodsSign, Long goodsId) {

//        JSONObject body = new JSONObject();
        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(key);
        sb.append("&pid=");
        sb.append(pddConfig.getPid());
        sb.append("&goods_sign=");
        sb.append(goodsSign);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                PDD_WORD,
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody());
    }

    @UserCheck
    @GetMapping(value = "/shortLink")
    public ApiResult getShortLink(String link) throws UnsupportedEncodingException {

        return ApiResult.ok(kuService.shortLink(link));
    }

    @GetMapping(value = "/pdd/nav")
    public JSONObject pddNav() {
        return restTemplate
                .getForObject(PDD_NAV, JSONObject.class);
    }
    @GetMapping(value = "/pdd/cate")
    public JSONObject pddCate() {
        return restTemplate
                .getForObject(PDD_CATE, JSONObject.class);
    }
    @GetMapping(value = "/pdd/list")
    public JSONObject pddList(Integer cateId, Integer pageId) {
        return restTemplate
                .getForObject(String.format(PDD_LIST, cateId, pageId), JSONObject.class);
    }

    @GetMapping(value = "/dy/nav")
    public JSONObject dyNav() {
        return restTemplate
                .getForObject(DY_NAV + kuService.getKuCid(), JSONObject.class);
    }
    @GetMapping(value = "/dy/cate")
    public JSONObject dyCate() {
        return restTemplate
                .getForObject(DY_CATE + kuService.getKuCid(), JSONObject.class);
    }
    @GetMapping(value = "/dy/list")
    public JSONObject dyList(DyListParam param) {
        String url = String.format(DY_LIST + kuService.getKuCid(), param.getPageId(), param.getPageSize(), param.getCateId(), param.getSearchType());
        if(param.getFirstCid() != null){
            url = url + "&first_cids=" + param.getFirstCid();
        }
        return restTemplate
                .getForObject(url, JSONObject.class);
    }


    @GetMapping(value = "/pick/cate")
    public JSONObject pickCate() {
        return restTemplate
                .getForObject(PICK_CATE, JSONObject.class);
    }

    @GetMapping(value = "/pick/list")
    public JSONObject pickList(Integer cateId, Integer pageId) {
        return restTemplate
                .getForObject(String.format(PICK_LIST,
                        cateId, pageId), JSONObject.class);
    }

    @GetMapping(value = "/mini/list")
    public JSONObject miniList(String keyword, Integer pageId) {
        return restTemplate
                .getForObject(String.format(MINI_LIST,
                        keyword, pageId), JSONObject.class);
    }

    @GetMapping(value = "/banner")
    public JSONObject banner() {
        return restTemplate
                .getForObject(BANNER_LIST + kuService.getKuCid(), JSONObject.class);
    }

    @GetMapping(value = "/banners")
    public JSONArray banners() {
        JSONArray homeBanner = (JSONArray) redisUtils.get(HOME_BANNER);
        if(homeBanner == null) {
            JSONObject origData = restTemplate
                    .getForObject(BANNER_LIST + kuService.getKuCid(), JSONObject.class);
            JSONObject data = origData.getJSONObject("data");
            homeBanner = data.getJSONArray("banners");
            redisUtils.set(HOME_BANNER, homeBanner, HOME_DATA_EXPIRED);
            redisUtils.set(HOME_TILES, data.getJSONArray("tile_long"), HOME_DATA_EXPIRED);
        }
        return homeBanner;
    }
    @GetMapping(value = "/tiles")
    public JSONArray tiles() {
        JSONArray homeTiles = (JSONArray) redisUtils.get(HOME_TILES);
        if(homeTiles == null) {
            JSONObject origData = restTemplate
                    .getForObject(BANNER_LIST + kuService.getKuCid(), JSONObject.class);
            JSONObject data = origData.getJSONObject("data");
            homeTiles = data.getJSONArray("tile_long");
            redisUtils.set(HOME_BANNER, data.getJSONArray("banners"), HOME_DATA_EXPIRED);
            redisUtils.set(HOME_TILES, homeTiles, HOME_DATA_EXPIRED);
        }
        return homeTiles;
    }



    @GetMapping(value = "/custom/cate")
    public JSONObject customCate(String id) {
        return restTemplate
                .getForObject(String.format(CUSTOM_CATE + kuService.getKuCid(), id), JSONObject.class);
    }

    @GetMapping(value = "/custom/list")
    public JSONObject customList(KuCustomParam param) {
        param.setCategory_id(param.getClassify());
        param.setCid(kuService.getKuCid());
        TreeMap<String, String> map = JSON.parseObject(JSON.toJSONString(param), TreeMap.class);
        StringBuilder sb = new StringBuilder();
        sb.append(CUSTOM_LIST);
        sb.append("?");
        map.forEach((key, value)->{
            sb.append(key);
            sb.append("=");
            sb.append(value);
            sb.append("&");
        });
        return restTemplate
                .getForObject(sb.toString(), JSONObject.class);
    }

    @GetMapping(value = "/dy/banner/word")
    public JSONObject getDyWord() {

        StringBuilder sb = new StringBuilder();
        sb.append("activity_id=3");
        sb.append("&domain=jkj.mailvor.com");
        sb.append("&mix_activity_id=7122297162473406756");
        sb.append("&activity_url=sslocal://webcast_webview?url=https%3A%2F%2Fmix.jinritemai.com%2Ffalcon%2Fmix_page%2Findex.html%3F__live_platform__%3Dwebcast%26activity_tag%3D%25E4%25B8%25AA%25E6%258A%25A4%25E5%25AE%25B6%25E6%25B8%2585%26allowMediaAutoPlay%3D1%26enter_from%3Ddouke%26hide_nav_bar%3D1%26hide_system_video_poster%3D1%26id%3D7122297162473406756%26origin_type%3Ddouke%26should_full_screen%3D1%26trans_status_bar%3D1%26entrance_info%3D%257B%2522ecom_scene_id%2522%253A%25221209%2522%257D");
        sb.append("&cid=FazG6352");
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                DY_WORD,
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody());
    }

    @GetMapping(value = "/activity/detail")
    public JSONObject parseMeeting(String id) {
        return restTemplate
                .getForObject(String.format(ACTIVITY_DETAIL + kuService.getKuCid(), id), JSONObject.class);
    }


    @GetMapping(value = "/hot/words")
    public ApiResult hotWords() {
        List<HotWordsVo> words;
        try {
            words = RedisUtil.get(ShopConstants.MSHOP_HOT_WORDS);
        }catch (JSONException e) {
            words = null;
        }
        if(CollectionUtils.isEmpty(words)) {

            //读取库的关键词
            JSONObject kuObj = restTemplate
                    .getForObject(HOT_WORDS_KU + kuService.getKuCid(), JSONObject.class);
            words = JSON.parseArray(kuObj.getJSONObject("data").getJSONArray("recently_keyword").toString(),
                    HotWordsVo.class);


            RedisUtil.set(ShopConstants.MSHOP_HOT_WORDS, words, 60*60);
        }
        return ApiResult.ok(words).setMsg("获取成功");
    }

    @GetMapping(value = "/dy/product/cate")
    public JSONObject dyProductCate() {
        String key = TkUtil.getMixedPlatformKey(DY_CATE);
        Object obj = redisUtils.get(key);
        if(obj != null) {
            return (JSONObject) obj;
        }

        JSONObject cateList = kuService.dyProductCateList();
        redisUtils.set(key, cateList, 7*24*3600);
        return cateList;
    }
    @GetMapping(value = "/dy/product/list")
    public JSONObject dyProductList(GoodsListDyParam param) {
        return kuService.dyProductList(param);
    }

    @GetMapping(value = "/dy/product/detail")
    public JSONObject dyProductDetail(@RequestParam String itemId) {
        return kuService.dyProductDetail(itemId);
    }

    @UserCheck
    @GetMapping(value = "/dy/product/word")
    public JSONObject dyProductWord(@RequestParam String itemId) {
        String channel;
        MwUser mwUser = LocalUser.getUser();
        if(mwUser != null) {
            channel = mwUser.getUid().toString();
        } else {
            channel = "0";
        }
        return kuService.dyProductWord(itemId, channel);
    }
    public static void main(String[] args) {
//        String res = "jsonpCBKB({\"retcode\":0,\"retmsg\":\"\",\"from\":\"cache\",\"def\":\n" +
//                "[\"手机\",\"洗衣机\",\"电视\",\"冰箱\",\"笔记本\",\"手表\",\"耳机\",\"空调\",\"充电宝\",\"路由器\",\"保温杯\",\"牛奶\"], \"owner\":\n" +
//                "[\"广角镜头手机\",,,\"除湿机\",\"飞科剃须刀\",\"容声冰箱\",\"燃气壁挂炉\",\"面包机\",\"桃核手串\",\"唇彩唇蜜\",\"电脑桌\",\"微波炉\",\"天燃气灶双灶\",\"防尘鞋架\",\"金士顿内存\",\"无纺布鞋套\",\"空气炸锅\",\"脱脂牛奶\",\"游戏本\",\"脸部卸妆油\"]})";
//
//        res = res.replace("jsonpCBKB(", "").replace(")", "");
//        JSONObject jsonObject = JSON.parseObject(res);
//        String[] array = jsonObject.getJSONArray("owner").toArray(new String[0]);
//        List<HotWordsVo> words = new ArrayList<>();
//        for(String arrStr: array) {
//            if(StringUtils.isNotBlank(arrStr)) {
//                words.add(new HotWordsVo(arrStr));
//            }
//
//            if(words.size() >=10) {
//                break;
//            }
//        }
//        if(words.isEmpty()) {
//            //读取库的关键词
//
//        }
        JSONObject kuObj = JSON.parseObject("{\"code\":200,\"msg\":\"操作成功\",\"data\":{\"hot_keyword\":[{\"jump_url\":\"http:\\/\\/www.haodanku.com\\/IndexActivity\\/index?activity=19\",\"is_red\":0,\"icon\":\"http:\\/\\/img-haodanku-com.cdn.fudaiapp.com\\/FoSUl67Li0JfPJkHziXWlGpHywcu\",\"keyword\":\"淘礼金专区\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"http:\\/\\/img-haodanku-com.cdn.fudaiapp.com\\/FlG7Oh5yc70RoksmSOxMh_ErxJ8R\",\"keyword\":\"口罩\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"洗面奶\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"牛奶\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"http:\\/\\/img-haodanku-com.cdn.fudaiapp.com\\/FlG7Oh5yc70RoksmSOxMh_ErxJ8R\",\"keyword\":\"螺蛳粉\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"洗衣液\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"http:\\/\\/img-haodanku-com.cdn.fudaiapp.com\\/FlG7Oh5yc70RoksmSOxMh_ErxJ8R\",\"keyword\":\"棉柔巾\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"面膜\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"牙膏\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"卫衣\"}],\"recently_keyword\":[{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"洗面奶\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"避孕套\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"洗衣液\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"洗发水\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"四件套\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"牛奶\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"抽纸\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"大米\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"洗脸巾\"},{\"jump_url\":\"\",\"is_red\":0,\"icon\":\"\",\"keyword\":\"卫生巾\"}]}}",
                JSONObject.class);


        List<HotWordsVo> words = JSON.parseArray(kuObj.getJSONObject("data").getJSONArray("recently_keyword").toString(),
                HotWordsVo.class);

        System.out.println(words.size());
    }
}
