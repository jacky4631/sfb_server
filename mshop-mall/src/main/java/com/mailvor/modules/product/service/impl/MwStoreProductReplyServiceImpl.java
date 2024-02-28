/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.product.domain.MwStoreProductReply;
import com.mailvor.modules.product.service.MwStoreProductReplyService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.service.dto.MwStoreProductReplyDto;
import com.mailvor.modules.product.service.dto.MwStoreProductReplyQueryCriteria;
import com.mailvor.modules.product.service.mapper.StoreProductReplyMapper;
import com.mailvor.modules.product.vo.ReplyCountVo;
import com.mailvor.modules.product.vo.MwStoreProductReplyQueryVo;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.mailvor.utils.OrderUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
* @author mazhongjun
* @date 2020-05-12
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreProductReplyServiceImpl extends BaseServiceImpl<StoreProductReplyMapper, MwStoreProductReply> implements MwStoreProductReplyService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private MwUserService mwUserService;

    @Autowired
    private MwStoreProductService mwStoreProductService;


    /**
     * 评价数据
     * @param productId 商品id
     * @return ReplyCountVO
     */
    @Override
    public ReplyCountVo getReplyCount(long productId) {
        Long sumCount = productReplyCount(productId);

        if(sumCount == 0) {
            return new ReplyCountVo();
        }

        //好评
        Long goodCount = this.baseMapper.selectCount(Wrappers.<MwStoreProductReply>lambdaQuery()
                .eq(MwStoreProductReply::getProductId,productId)
                .eq(MwStoreProductReply::getProductScore,5));

        //中评
        Long inCount = this.baseMapper.selectCount(Wrappers.<MwStoreProductReply>lambdaQuery()
                .eq(MwStoreProductReply::getProductId,productId)
                .lt(MwStoreProductReply::getProductScore,5)
                .gt(MwStoreProductReply::getProductScore,2));

        //差评
        Long poorCount = this.baseMapper.selectCount(Wrappers.<MwStoreProductReply>lambdaQuery()
                .eq(MwStoreProductReply::getProductId,productId)
                .lt(MwStoreProductReply::getProductScore,2));

        //好评率
        String replyChance = ""+ OrderUtil.getRoundFee(NumberUtil.mul(NumberUtil.div(goodCount,sumCount),100));
        String replyStar = ""+OrderUtil.getRoundFee(NumberUtil.mul(NumberUtil.div(goodCount,sumCount),5));

        return ReplyCountVo.builder()
                .sumCount(sumCount)
                .goodCount(goodCount)
                .inCount(inCount)
                .poorCount(poorCount)
                .replyChance(replyChance)
                .replySstar(replyStar)
                .build();

    }

    /**
     * 处理评价
     * @param replyQueryVo replyQueryVo
     * @return MwStoreProductReplyQueryVo
     */
    @Override
    public MwStoreProductReplyQueryVo handleReply(MwStoreProductReplyQueryVo replyQueryVo) {
        MwStoreCartQueryVo cartInfo = JSONObject.parseObject(replyQueryVo.getCartInfo()
                , MwStoreCartQueryVo.class);
        if(ObjectUtil.isNotNull(cartInfo)){
            if(ObjectUtil.isNotNull(cartInfo.getProductInfo())){
                if(ObjectUtil.isNotNull(cartInfo.getProductInfo().getAttrInfo())){
                    replyQueryVo.setSku(cartInfo.getProductInfo().getAttrInfo().getSku());
                }
            }
        }

        BigDecimal star = NumberUtil.add(replyQueryVo.getProductScore(),
                replyQueryVo.getServiceScore());

        star = NumberUtil.div(star,2);

        replyQueryVo.setStar(String.valueOf(star.intValue()));

        if(StrUtil.isEmpty(replyQueryVo.getComment())){
            replyQueryVo.setComment("此用户没有填写评价");
        }

        return replyQueryVo;
    }

    /**
     * 获取单条评价
     * @param productId 商品di
     * @return MwStoreProductReplyQueryVo
     */
    @Override
    public MwStoreProductReplyQueryVo getReply(long productId) {
        MwStoreProductReplyQueryVo vo = this.baseMapper.getReply(productId);
        if(ObjectUtil.isNotNull(vo)){
            return handleReply(this.baseMapper.getReply(productId));
        }
        return null;
    }


    /**
     * 获取评价列表
     * @param productId 商品id
     * @param type 0-全部 1-好评 2-中评 3-差评
     * @param page page
     * @param limit limit
     * @return list
     */
    @Override
    public List<MwStoreProductReplyQueryVo> getReplyList(long productId, int type, int page, int limit) {
        List<MwStoreProductReplyQueryVo> newList = new ArrayList<>();
        Page<MwStoreProductReply> pageModel = new Page<>(page, limit);
        List<MwStoreProductReplyQueryVo> list = this.baseMapper
                .selectReplyList(pageModel,productId,type);
        List<MwStoreProductReplyQueryVo> list1 = list.stream().map(i ->{
            MwStoreProductReplyQueryVo vo = new MwStoreProductReplyQueryVo();
            BeanUtils.copyProperties(i,vo);
            if(i.getPictures().contains(",")){
                vo.setPics(i.getPictures().split(","));
            }
            return vo;
        }).collect(Collectors.toList());
        for (MwStoreProductReplyQueryVo queryVo : list1) {
            newList.add(handleReply(queryVo));
        }
        return newList;
    }

    @Override
    public Long getInfoCount(Integer oid, String unique) {
        LambdaQueryWrapper<MwStoreProductReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreProductReply::getUnique, unique).eq(MwStoreProductReply::getOid, oid);
        return this.baseMapper.selectCount(wrapper);
    }

    @Override
    public Long productReplyCount(long productId) {

        return this.baseMapper.selectCount(Wrappers.<MwStoreProductReply>lambdaQuery()
                .eq(MwStoreProductReply::getProductId,productId));

    }

    @Override
    public Long replyCount(String unique) {
       LambdaQueryWrapper<MwStoreProductReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreProductReply::getUnique,unique);
        return this.baseMapper.selectCount(wrapper);
    }

    /**
     * 好评比例
     * @param productId 商品id
     * @return %
     */
    @Override
    public String replyPer(long productId) {
       LambdaQueryWrapper<MwStoreProductReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreProductReply::getProductId,productId)
                .eq(MwStoreProductReply::getIsDel,ShopCommonEnum.DELETE_0.getValue())
                .eq(MwStoreProductReply::getProductScore,5);
        Long productScoreCount = this.baseMapper.selectCount(wrapper);
        Long count = productReplyCount(productId);
        if(count > 0){
            return ""+OrderUtil.getRoundFee(NumberUtil.mul(NumberUtil.div(productScoreCount,count),100));
        }

        return "0";
    }





    //===================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreProductReplyQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreProductReply> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwStoreProductReplyDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreProductReply> queryAll(MwStoreProductReplyQueryCriteria criteria){
        List<MwStoreProductReply> storeProductReplyList =  baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreProductReply.class, criteria));
        storeProductReplyList.forEach(mwStoreProductReply->{
            mwStoreProductReply.setUser(mwUserService.getById(mwStoreProductReply.getUid()));
            mwStoreProductReply.setStoreProduct(mwStoreProductService.getById(mwStoreProductReply.getProductId()));
        });
        return storeProductReplyList;
    }


    @Override
    public void download(List<MwStoreProductReplyDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreProductReplyDto mwStoreProductReply : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户ID", mwStoreProductReply.getUid());
            map.put("订单ID", mwStoreProductReply.getOid());
            map.put("唯一id", mwStoreProductReply.getUnique());
            map.put("产品id", mwStoreProductReply.getProductId());
            map.put("某种商品类型(普通商品、秒杀商品）", mwStoreProductReply.getReplyType());
            map.put("商品分数", mwStoreProductReply.getProductScore());
            map.put("服务分数", mwStoreProductReply.getServiceScore());
            map.put("评论内容", mwStoreProductReply.getComment());
            map.put("评论图片", mwStoreProductReply.getPics());
            map.put("管理员回复内容", mwStoreProductReply.getMerchantReplyContent());
            map.put("管理员回复时间", mwStoreProductReply.getMerchantReplyTime());
            map.put("0未回复1已回复", mwStoreProductReply.getIsReply());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
