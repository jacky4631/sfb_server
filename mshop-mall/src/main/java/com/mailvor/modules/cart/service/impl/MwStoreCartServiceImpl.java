/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.cart.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.CartTypeEnum;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.enums.ProductTypeEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.activity.domain.MwStoreBargain;
import com.mailvor.modules.activity.service.MwStoreBargainService;
import com.mailvor.modules.activity.service.mapper.MwStoreBargainMapper;
import com.mailvor.modules.activity.service.mapper.MwStoreCombinationMapper;
import com.mailvor.modules.activity.service.mapper.MwStoreSeckillMapper;
import com.mailvor.modules.cart.domain.MwStoreCart;
import com.mailvor.modules.cart.service.MwStoreCartService;
import com.mailvor.modules.cart.service.dto.MwStoreCartDto;
import com.mailvor.modules.cart.service.dto.MwStoreCartQueryCriteria;
import com.mailvor.modules.cart.service.mapper.StoreCartMapper;
import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.order.service.dto.CountDto;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.service.MwStoreProductAttrService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author huangyu
 * @date 2020-05-12
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreCartServiceImpl extends BaseServiceImpl<StoreCartMapper, MwStoreCart> implements MwStoreCartService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private StoreCartMapper mwStoreCartMapper;
    @Autowired
    private MwStoreSeckillMapper storeSeckillMapper;
    @Autowired
    private MwStoreBargainMapper mwStoreBargainMapper;
    @Autowired
    private MwStoreCombinationMapper storeCombinationMapper;
    @Autowired
    private MwStoreProductService productService;
    @Autowired
    private MwStoreProductAttrService productAttrService;
    @Autowired
    private MwStoreBargainService storeBargainService;
    @Autowired
    private MwUserService userService;


    /**
     * 删除购物车
     *
     * @param uid uid
     * @param ids 购物车id集合
     */
    @Override
    public void removeUserCart(Long uid, List<String> ids) {
        List<Long> newids = ids.stream().map(Long::new).collect(Collectors.toList());
        mwStoreCartMapper.delete(Wrappers.<MwStoreCart>lambdaQuery()
                .eq(MwStoreCart::getUid, uid)
                .in(MwStoreCart::getId, newids));
    }

    /**
     * 改购物车数量
     *
     * @param cartId  购物车id
     * @param cartNum 数量
     * @param uid     uid
     */
    @Override
    public void changeUserCartNum(Long cartId, int cartNum, Long uid) {
        MwStoreCart cart = this.lambdaQuery()
                .eq(MwStoreCart::getUid, uid)
                .eq(MwStoreCart::getId, cartId)
                .one();
        if (cart == null) {
            throw new MshopException("购物车不存在");
        }

        if (cartNum <= 0) {
            throw new MshopException("库存错误");
        }

        //普通商品库存
        int stock = productService.getProductStock(cart.getProductId()
                , cart.getProductAttrUnique(), "");
        if (stock < cartNum) {
            throw new MshopException("该产品库存不足" + cartNum);
        }

        if (cartNum == cart.getCartNum()) {
            return;
        }

        MwStoreCart storeCart = new MwStoreCart();
        storeCart.setCartNum(cartNum);
        storeCart.setId(cartId);

        mwStoreCartMapper.updateById(storeCart);


    }

    /**
     * 购物车列表
     *
     * @param uid     用户id
     * @param cartIds 购物车id，多个逗号隔开
     * @param status  0-购购物车列表
     * @return map valid-有效购物车 invalid-失效购物车
     */
    @Override
    public Map<String, Object> getUserProductCartList(Long uid, String cartIds, Integer status) {
       LambdaQueryWrapper<MwStoreCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreCart::getUid, uid)
                .eq(MwStoreCart::getIsPay, OrderInfoEnum.PAY_STATUS_0.getValue())
                .orderByDesc(MwStoreCart::getId);
        if (status == null) {
            wrapper.eq(MwStoreCart::getIsNew, CartTypeEnum.NEW_0.getValue());
        }
        if (StrUtil.isNotEmpty(cartIds)) {
            wrapper.in(MwStoreCart::getId, Arrays.asList(cartIds.split(",")));
        }
        List<MwStoreCart> carts = mwStoreCartMapper.selectList(wrapper);

        List<MwStoreCartQueryVo> valid = new ArrayList<>();
        List<MwStoreCartQueryVo> invalid = new ArrayList<>();

        for (MwStoreCart storeCart : carts) {
            MwStoreProductQueryVo storeProduct = null;
            if (storeCart.getCombinationId() != null && storeCart.getCombinationId() > 0) {
                storeProduct = ObjectUtil.clone(storeCombinationMapper.combinatiionInfo(storeCart.getCombinationId()));
            } else if (storeCart.getSeckillId() != null && storeCart.getSeckillId() > 0) {
                storeProduct = ObjectUtil.clone(storeSeckillMapper.seckillInfo(storeCart.getSeckillId()));
            } else if (storeCart.getBargainId() != null && storeCart.getBargainId() > 0) {
                storeProduct = ObjectUtil.clone(mwStoreBargainMapper.bargainInfo(storeCart.getBargainId()));
            } else {
                //必须得重新克隆创建一个新对象
                storeProduct = ObjectUtil.clone(productService
                        .getStoreProductById(storeCart.getProductId()));
            }

            MwStoreCartQueryVo storeCartQueryVo = generator.convert(storeCart, MwStoreCartQueryVo.class);

            if (ObjectUtil.isNull(storeProduct)) {
                this.removeById(storeCart.getId());
            } else if (ShopCommonEnum.SHOW_0.getValue().equals(storeProduct.getIsShow()) || (storeProduct.getStock() == 0 && StrUtil.isEmpty(storeCart.getProductAttrUnique()))) {
                storeCartQueryVo.setProductInfo(storeProduct);
                invalid.add(storeCartQueryVo);
            } else {
                if (StrUtil.isNotEmpty(storeCart.getProductAttrUnique())) {
                    MwStoreProductAttrValue productAttrValue = productAttrService
                            .uniqueByAttrInfo(storeCart.getProductAttrUnique());
                    if (ObjectUtil.isNull(productAttrValue) || productAttrValue.getStock() == 0) {
                        storeCartQueryVo.setProductInfo(storeProduct);
                        invalid.add(storeCartQueryVo);
                    } else {
                        storeProduct.setAttrInfo(productAttrValue);
                        storeCartQueryVo.setProductInfo(storeProduct);

                        //设置真实价格
                        //设置VIP价格
                        double vipPrice = userService.setLevelPrice(
                                productAttrValue.getPrice().doubleValue(), uid);
                        //砍价金额
                        if ( storeCart.getBargainId() > 0
                               ) {
                            vipPrice = storeProduct.getPrice().doubleValue();
                        }
                        //设置拼团价格
                       if(storeCart.getCombinationId() > 0 ){
                            vipPrice = productAttrValue.getPinkPrice().doubleValue();
                        }
                        //设置秒杀价格
                        if( storeCart.getSeckillId() > 0){
                            vipPrice = productAttrValue.getSeckillPrice().doubleValue();
                        }
                        storeCartQueryVo.setTruePrice(vipPrice);
                        //设置会员价
                        storeCartQueryVo.setVipTruePrice(productAttrValue.getPrice()
                                .doubleValue());
                        storeCartQueryVo.setCostPrice(productAttrValue.getCost()
                                .doubleValue());
                        storeCartQueryVo.setTrueStock(productAttrValue.getStock());

                        valid.add(storeCartQueryVo);

                    }
                } else {
                    //设置VIP价格,营销商品不参与
                    double vipPrice = userService.setLevelPrice(
                            storeProduct.getPrice().doubleValue(), uid);
                    if (storeCart.getCombinationId() > 0 || storeCart.getSeckillId() > 0
                            || storeCart.getBargainId() > 0) {
                        vipPrice = storeProduct.getPrice().doubleValue();
                    }

                    storeCartQueryVo.setTruePrice(vipPrice);
                    // 设置会员价
                    storeCartQueryVo.setVipTruePrice(0d);
                    storeCartQueryVo.setCostPrice(storeProduct.getCost()
                            .doubleValue());
                    storeCartQueryVo.setTrueStock(storeProduct.getStock());
                    storeCartQueryVo.setProductInfo(storeProduct);

                    valid.add(storeCartQueryVo);
                }
            }

        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("valid", valid);
        map.put("invalid", invalid);
        return map;
    }

    /**
     * 添加购物车
     * @param uid               用户id
     * @param productId         普通产品编号
     * @param cartNum           购物车数量
     * @param productAttrUnique 属性唯一值
     * @param isNew             1 加入购物车直接购买  0 加入购物车
     * @param combinationId     拼团id
     * @param seckillId         秒杀id
     * @param bargainId         砍价id
     * @return 购物车id
     */
    @Override
    public long addCart(Long uid, Long productId, Integer cartNum, String productAttrUnique,
                        Integer isNew, Long combinationId, Long seckillId, Long bargainId) {

        this.checkProductStock(uid, productId, cartNum, productAttrUnique, combinationId, seckillId, bargainId);
       LambdaQueryWrapper<MwStoreCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreCart::getUid, uid)
                .eq(MwStoreCart::getIsPay, OrderInfoEnum.PAY_STATUS_0.getValue())
                .eq(MwStoreCart::getProductId, productId)
                .eq(MwStoreCart::getIsNew, isNew)
                .eq(MwStoreCart::getProductAttrUnique, productAttrUnique)
                .eq(MwStoreCart::getBargainId, bargainId)
                .eq(MwStoreCart::getCombinationId, combinationId)
                .eq(MwStoreCart::getSeckillId, seckillId)
                .orderByDesc(MwStoreCart::getId)
                .last("limit 1");

        MwStoreCart cart = mwStoreCartMapper.selectOne(wrapper);

        MwStoreCart storeCart = MwStoreCart.builder()
                .cartNum(cartNum)
                .productAttrUnique(productAttrUnique)
                .productId(productId)
                .bargainId(bargainId)
                .combinationId(combinationId)
                .seckillId(seckillId)
                .isNew(isNew)
                .uid(uid)
                .build();
        if (cart != null) {
            if (CartTypeEnum.NEW_0.getValue().equals(isNew)) {
                storeCart.setCartNum(cartNum + cart.getCartNum());
            }
            storeCart.setId(cart.getId());
            mwStoreCartMapper.updateById(storeCart);
        } else {
            mwStoreCartMapper.insert(storeCart);
        }

        return storeCart.getId();
    }

    /**
     * 返回当前用户购物车总数量
     *
     * @param uid 用户id
     * @return int
     */
    @Override
    public int getUserCartNum(Long uid) {
        return mwStoreCartMapper.cartSum(uid);
    }

//    @Override
//    public MwStoreCartQueryVo getMwStoreCartById(Serializable id){
//        return MwStoreCartMapper.getMwStoreCartById(id);
//    }

    /**
     * 检测商品/秒杀/砍价/拼团库存
     *
     * @param uid               用户ID
     * @param productId         产品ID
     * @param cartNum           购买数量
     * @param productAttrUnique 商品属性Unique
     * @param combinationId     拼团产品ID
     * @param seckillId         秒杀产品ID
     * @param bargainId         砍价产品ID
     */
    @Override
    public void checkProductStock(Long uid, Long productId, Integer cartNum, String productAttrUnique,
                                  Long combinationId, Long seckillId, Long bargainId) {
        Date now = new Date();
        //拼团
        if (combinationId != null && combinationId > 0) {
            MwStoreProduct product = productService
                    .lambdaQuery().eq(MwStoreProduct::getId, productId)
                    .eq(MwStoreProduct::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                    .one();
            if (product == null) {
                throw new MshopException("该产品已下架或删除");
            }

            int stock = productService.getProductStock(productId, productAttrUnique, ProductTypeEnum.PINK.getValue());
            if (stock < cartNum) {
                throw new MshopException(product.getStoreName() + "库存不足" + cartNum);
            }
            //秒杀
        } else if (seckillId != null && seckillId > 0) {
            MwStoreProduct product = productService
                    .lambdaQuery().eq(MwStoreProduct::getId, productId)
                    .eq(MwStoreProduct::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                    .one();
            if (product == null) {
                throw new MshopException("该产品已下架或删除");
            }

            int stock = productService.getProductStock(productId, productAttrUnique, ProductTypeEnum.SECKILL.getValue());
            if (stock < cartNum) {
                throw new MshopException(product.getStoreName() + "库存不足" + cartNum);
            }
            //砍价
        } else if (bargainId != null && bargainId > 0) {
            MwStoreBargain mwStoreBargain = storeBargainService
                    .lambdaQuery().eq(MwStoreBargain::getId, bargainId)
                    .eq(MwStoreBargain::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                    .le(MwStoreBargain::getStartTime, now)
                    .ge(MwStoreBargain::getStopTime, now)
                    .one();
            if (mwStoreBargain == null) {
                throw new MshopException("该产品已下架或删除");
            }
            if (mwStoreBargain.getStock() < cartNum) {
                throw new MshopException("该产品库存不足");
            }

        } else {
            MwStoreProduct product = productService
                    .lambdaQuery().eq(MwStoreProduct::getId, productId)
                    .eq(MwStoreProduct::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                    .one();
            if (product == null) {
                throw new MshopException("该产品已下架或删除");
            }

            int stock = productService.getProductStock(productId, productAttrUnique, "");
            if (stock < cartNum) {
                throw new MshopException(product.getStoreName() + "库存不足" + cartNum);
            }
        }

    }


    //====================================================================//


    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreCartQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreCart> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwStoreCartDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreCart> queryAll(MwStoreCartQueryCriteria criteria) {
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreCart.class, criteria));
    }


    @Override
    public void download(List<MwStoreCartDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreCartDto mwStoreCart : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户ID", mwStoreCart.getUid());
            map.put("类型", mwStoreCart.getType());
            map.put("商品ID", mwStoreCart.getProductId());
            map.put("商品属性", mwStoreCart.getProductAttrUnique());
            map.put("商品数量", mwStoreCart.getCartNum());
            map.put("添加时间", mwStoreCart.getAddTime());
            map.put("0 = 未购买 1 = 已购买", mwStoreCart.getIsPay());
            map.put("是否删除", mwStoreCart.getIsDel());
            map.put("是否为立即购买", mwStoreCart.getIsNew());
            map.put("拼团id", mwStoreCart.getCombinationId());
            map.put("秒杀产品ID", mwStoreCart.getSeckillId());
            map.put("砍价id", mwStoreCart.getBargainId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<CountDto> findCateName() {
        return mwStoreCartMapper.findCateName();
    }
}
