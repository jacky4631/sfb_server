package com.mailvor.modules.quartz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.tk.param.*;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.vo.DataokeResVo;
import com.mailvor.modules.tk.vo.GoodsDetailVo;
import com.mailvor.modules.tk.vo.GoodsListVo;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 热度订单定时任务，根据热度生成订单，需要从订单池中获取订单
 * 生成淘客最终订单
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class OrderTaskUtil {
    @Resource
    private DataokeService dataokeService;

    public List<GoodsDetailVo> getGoodsList() {
        int pageNum = DateUtils.randomInt(1, 200);
        GoodsListParam param = new GoodsListParam();
        param.setPageSize(200);
        param.setPageId(pageNum);
        DataokeResVo<GoodsListVo> res = dataokeService.goodsVOS(param);
        if(res == null || res.getData() == null || CollectionUtils.isEmpty(res.getData().getList())) {
            return null;
        }
        List<GoodsDetailVo> detailVos = res.getData().getList();
        return detailVos;
    }
    public JSONArray getGoodsListJd() {
        //京店商品最大300多页
        int pageNum = DateUtils.randomInt(1, 15);
        GoodsListJDParam param = new GoodsListJDParam();
        param.setPageSize(200);
        param.setPageId(pageNum);
        JSONObject res = dataokeService.rankListJD(param);
        if(res == null || res.getJSONObject("data") == null
                || CollectionUtils.isEmpty(res.getJSONObject("data").getJSONArray("list"))) {
            return null;
        }
        return res.getJSONObject("data").getJSONArray("list");
    }

    public JSONArray getGoodsListPdd() {
        //拼多多商品最大50多页
        int pageNum = DateUtils.randomInt(1, 50);
        GoodsListPddParam param = new GoodsListPddParam();
        param.setPageSize(100);
        param.setPage(pageNum);
        JSONObject res = dataokeService.goodsListPdd(param);
        if(res == null || res.getJSONObject("data") == null
                || CollectionUtils.isEmpty(res.getJSONObject("data").getJSONArray("goodsList"))) {
            return null;
        }
        return res.getJSONObject("data").getJSONArray("goodsList");
    }
    public JSONArray getGoodsListDy() {
        //拼多多商品最大50多页
        int pageNum = DateUtils.randomInt(1, 90);
        GoodsSearchDyParam param = new GoodsSearchDyParam();
        param.setPageSize(20);
        param.setPage(pageNum);
        JSONObject res = dataokeService.dyGoodsSearch(param);
        if(res == null || res.getJSONObject("data") == null
                || CollectionUtils.isEmpty(res.getJSONObject("data").getJSONArray("list"))) {
            return null;
        }
        return res.getJSONObject("data").getJSONArray("list");
    }

    public JSONArray getGoodsListVip() {
        int index = DateUtils.randomInt(0, 4);
        int maxPage;
        String keyword;
        if(index == 0) {
            maxPage = 70;
            keyword = "美妆";
        } else if(index == 0) {
            maxPage = 60;
            keyword = "母婴";
        }else if(index == 2) {
            maxPage = 30;
            keyword = "鞋包";
        }else if(index == 3) {
            maxPage = 100;
            keyword = "女装";
        }else {
            maxPage = 50;
            keyword = "男装";
        }
        int pageNum = DateUtils.randomInt(1, maxPage);
        GoodsListVipParam param = new GoodsListVipParam();
        param.setPageSize(50);
        param.setPage(pageNum);
        param.setKeyword(keyword);
        param.setChanTag("0");
        param.setOpenId("0");
        param.setRealCall("false");
        JSONObject res = dataokeService.goodsListVip(param);

        try {
            return res.getJSONObject("data").getJSONArray("goodsInfoList");
        }catch (Exception e) {
            log.error("获取唯品会商品异常：{}", e);
            return null;
        }

    }
    public GoodsDetailVo getGoods(List<GoodsDetailVo> goodsList, double minFee, double maxFee) {
        if(CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        List<GoodsDetailVo> detailVos = goodsList.stream().filter(goodsDetailVo ->
        {
            double endPrice = goodsDetailVo.getActualPrice();
            double commissionShare = goodsDetailVo.getCommissionRate();
            double commission = endPrice*commissionShare/100;
            return commission > minFee && commission < maxFee;
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(detailVos)) {
            return null;
        }
        int randomIndex = DateUtils.randomInt(0, detailVos.size()-1);
        GoodsDetailVo selected = detailVos.get(randomIndex);
        goodsList.remove(selected);
        return selected;
    }

    public JSONObject getGoodsJd(JSONArray goodsList, double minFee, double maxFee) {
        if(CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        List detailVos = goodsList.stream().filter(goodsDetailVo -> {
            JSONObject map = (JSONObject) goodsDetailVo;
            double endPrice = map.getDouble("actualPrice");
            double commissionShare = map.getDouble("commissionShare");
            double commission = endPrice*commissionShare/100;
            return commission > minFee && commission < maxFee && StringUtils.isNotBlank(map.getString("picMain"));
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(detailVos)) {
            return null;
        }
        int randomIndex = DateUtils.randomInt(0, detailVos.size()-1);
        JSONObject selected = (JSONObject)detailVos.get(randomIndex);
        goodsList.remove(selected);
        return selected;
    }


    public JSONObject getGoodsPdd(JSONArray goodsList, double minFee, double maxFee) {
        if(CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        List detailVos = goodsList.stream().filter(goodsDetailVo -> {
            JSONObject map = (JSONObject) goodsDetailVo;
            double endPrice = map.getDouble("minGroupPrice");
            double commissionShare = map.getDouble("promotionRate");
            double commission = endPrice*commissionShare/100;
            return commission > minFee && commission < maxFee;
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(detailVos)) {
            return null;
        }
        int randomIndex = DateUtils.randomInt(0, detailVos.size()-1);
        JSONObject selected = (JSONObject)detailVos.get(randomIndex);
        goodsList.remove(selected);
        return selected;
    }

    public JSONObject getGoodsDy(JSONArray goodsList, double minFee, double maxFee) {
        if(CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        List detailVos = goodsList.stream().filter(goodsDetailVo -> {
            JSONObject map = (JSONObject) goodsDetailVo;
            double commission = map.getDouble("cosFee");
            return commission > minFee && commission < maxFee;
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(detailVos)) {
            return null;
        }
        int randomIndex = DateUtils.randomInt(0, detailVos.size()-1);
        JSONObject selected = (JSONObject)detailVos.get(randomIndex);
        goodsList.remove(selected);
        return selected;
    }

    public JSONObject getGoodsVip(JSONArray goodsList, double minFee, double maxFee) {
        if(CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        List detailVos = goodsList.stream().filter(goodsDetailVo -> {
            JSONObject map = (JSONObject) goodsDetailVo;
            double commission = 0;
            try {
                commission = Double.parseDouble(map.getString("commission"));
            }catch (Exception e) {
                e.printStackTrace();
            }
            return commission > minFee && commission < maxFee;
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(detailVos)) {
            return null;
        }
        int randomIndex = DateUtils.randomInt(0, detailVos.size()-1);
        JSONObject selected = (JSONObject)detailVos.get(randomIndex);
        goodsList.remove(selected);
        return selected;
    }

    public String getOrderId(int len) {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
            rs.append(r.nextInt(10));
        }
        return rs.toString();
    }

    public BigDecimal getDiscountOne(Map<String, MwSystemUserLevel> levelMap, String platform) {
        //因为会员等级一级分佣比例为20%，获取当前平台
        BigDecimal discountOne = BigDecimal.valueOf(20);
        MwSystemUserLevel topLevel = levelMap.get(platform);
        if(topLevel != null) {
            discountOne = topLevel.getDiscountOne();
        }
        return discountOne;
    }

    public static void main(String[] args) {
        OrderTaskUtil task = new OrderTaskUtil();
////        System.out.println(task.getOrderId(19));
////        String millis = String.valueOf(System.currentTimeMillis());
////        System.out.println(millis);
////        System.out.println(millis.substring(millis.length() - 10, millis.length()));
        for(int i = 0; i < 1000; i++) {
            String ll = task.getOrderId(10);
            System.out.println(ll);
        }

    }
}
