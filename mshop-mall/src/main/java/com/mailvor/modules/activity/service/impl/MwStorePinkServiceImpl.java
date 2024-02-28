/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.enums.PinkEnum;
import com.mailvor.modules.activity.domain.MwStoreCombination;
import com.mailvor.modules.activity.domain.MwStorePink;
import com.mailvor.modules.activity.service.MwStoreCombinationService;
import com.mailvor.modules.activity.service.MwStorePinkService;
import com.mailvor.modules.activity.service.MwStoreVisitService;
import com.mailvor.modules.activity.service.dto.PinkAllDto;
import com.mailvor.modules.activity.service.dto.PinkDto;
import com.mailvor.modules.activity.service.dto.PinkUserDto;
import com.mailvor.modules.activity.service.dto.MwStorePinkDto;
import com.mailvor.modules.activity.service.dto.MwStorePinkQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreCombinationMapper;
import com.mailvor.modules.activity.service.mapper.MwStorePinkMapper;
import com.mailvor.modules.activity.vo.PinkInfoVo;
import com.mailvor.modules.activity.vo.MwStoreCombinationQueryVo;
import com.mailvor.modules.activity.vo.MwStorePinkQueryVo;
import com.mailvor.modules.cart.service.MwStoreCartService;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.order.vo.MwStoreOrderQueryVo;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.vo.MwUserQueryVo;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
public class MwStorePinkServiceImpl extends BaseServiceImpl<MwStorePinkMapper, MwStorePink> implements MwStorePinkService {

    @Autowired
    private IGenerator generator;
    @Autowired
    private MwStorePinkMapper mwStorePinkMapper;
    @Autowired
    private MwStoreCombinationMapper mwStoreCombinationMapper;
    @Autowired
    private MwStoreCombinationService combinationService;
    @Autowired
    private MwStoreOrderService storeOrderService;
    @Autowired
    private MwUserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MwUserService mwUserService;

    @Autowired
    private MwStoreCartService mwStoreCartService;

    @Autowired
    private MwStoreVisitService mwStoreVisitService;


    /**
     * 取消拼团
     * @param uid 用户id
     * @param cid 团购产品id
     * @param pinkId 拼团id
     */
    @Override
    public void removePink(Long uid, Long cid, Long pinkId) {
        MwStorePink pink = this.lambdaQuery().eq(MwStorePink::getId,pinkId)
                .eq(MwStorePink::getUid,uid)
                .eq(MwStorePink::getCid,cid)
                .eq(MwStorePink::getKId,0)  //团长
                .eq(MwStorePink::getIsRefund,OrderInfoEnum.PINK_REFUND_STATUS_0.getValue())
                .eq(MwStorePink::getStatus,OrderInfoEnum.REFUND_STATUS_1.getValue())
                .gt(MwStorePink::getStopTime,new Date())
                .one();
        if(pink == null) {
            throw new MshopException("拼团不存在或已经取消");
        }

        PinkUserDto pinkUserDto = this.getPinkMemberAndPinK(pink);
        List<MwStorePink> pinkAll = pinkUserDto.getPinkAll();
        MwStorePink pinkT = pinkUserDto.getPinkT();
        List<Long> idAll = pinkUserDto.getIdAll();
        List<Long> uidAll = pinkUserDto.getUidAll();
        int count = pinkUserDto.getCount();
        if(count < 1){
            this.pinkComplete(uidAll,idAll,uid,pinkT);
            throw new MshopException("拼团已完成，无法取消");
        }
        //如果团长取消拼团，团队还有人，就把后面的人作为下一任团长
        MwStorePink nextPinkT = null;
        if(pinkAll.size() > 0){
            nextPinkT = pinkAll.get(0);
        }

        //先退团长的money
        storeOrderService.orderApplyRefund("","","拼团取消开团",pinkT.getOrderId(),pinkT.getUid());
        this.orderPinkFailAfter(pinkT.getUid(),pinkT.getId());

        //把团长下个人设置为团长
        if(ObjectUtil.isNotNull(nextPinkT)){
           LambdaQueryWrapper<MwStorePink> wrapperO = new LambdaQueryWrapper<>();
            MwStorePink storePinkO = new MwStorePink();
            storePinkO.setKId(0L); //设置团长
            storePinkO.setStatus(OrderInfoEnum.PINK_STATUS_1.getValue());
            storePinkO.setStopTime(pinkT.getStopTime());
            storePinkO.setId(nextPinkT.getId());
            mwStorePinkMapper.updateById(storePinkO);

            //原有团长的数据变更成新团长下面
            wrapperO.eq(MwStorePink::getKId,pinkT.getId());
            MwStorePink storePinkT = new MwStorePink();
            storePinkT.setKId(nextPinkT.getId());
            mwStorePinkMapper.update(storePinkT,wrapperO);

            //update order
            MwStoreOrder storeOrder = new MwStoreOrder();
            storeOrder.setId(nextPinkT.getId());
            storeOrderService.updateById(storeOrder);

        }
    }

    /**
     * 计算还差几人拼团
     * @param pink 拼团信息
     * @return int
     */
    @Override
    public int surplusPeople(MwStorePink pink) {
        List<MwStorePink> listT = new ArrayList<>();
        if(pink.getKId() > 0){ //团长存在
            listT = this.getPinkMember(pink.getKId());
        }else{
            listT = this.getPinkMember(pink.getId());
        }

        return pink.getPeople() - (listT.size() + 1);
    }




    /**
     * 拼团明细
     * @param id 拼团id
     * @param uid 用户id
     */
    @Override
    public PinkInfoVo pinkInfo(Long id, Long uid) {
        MwStorePink pink = this.getPinkUserOne(id);
        if(ObjectUtil.isNull(pink)) {
            throw new MshopException("拼团不存在");
        }
        if( OrderInfoEnum.PINK_REFUND_STATUS_1.getValue().equals(pink.getIsRefund())){
            throw new MshopException("订单已退款");
        }

        int isOk = 0;//判断拼团是否完成
        int userBool = 0;//判断当前用户是否在团内  0未在 1在
        int pinkBool = 0;//判断拼团是否成功  0未 1是 -1结束

        PinkUserDto pinkUserDto = this.getPinkMemberAndPinK(pink);
        MwStorePink pinkT = pinkUserDto.getPinkT();
        List<MwStorePink> pinkAll = pinkUserDto.getPinkAll();
        List<Long> idAll = pinkUserDto.getIdAll();
        List<Long> uidAll = pinkUserDto.getUidAll();
        int count = pinkUserDto.getCount();

        if(count < 0) {
            count = 0;
        }
        if(OrderInfoEnum.PINK_STATUS_2.getValue().equals(pinkT.getStatus())){
            pinkBool = PinkEnum.PINK_BOOL_1.getValue();
            isOk = PinkEnum.IS_OK_1.getValue();
        }else if(pinkT.getStatus() == 3){
            pinkBool = PinkEnum.PINK_BOOL_MINUS_1.getValue();
            isOk = PinkEnum.IS_OK_0.getValue();
        }else{
            //组团完成
            if(count < 1){
                isOk = PinkEnum.IS_OK_1.getValue();
                pinkBool = this.pinkComplete(uidAll,idAll,uid,pinkT);
            }else{
                pinkBool = this.pinkFail(pinkAll,pinkT,pinkBool);
            }
        }

        //团员是否在团
        if(ObjectUtil.isNotNull(pinkAll)){
            for (MwStorePink storePink : pinkAll) {
                if(storePink.getUid().equals(uid)) {
                    userBool = PinkEnum.USER_BOOL_1.getValue();
                }
            }
        }
        //团长
        if(pinkT.getUid().equals(uid)) {
            userBool = PinkEnum.USER_BOOL_1.getValue();
        }

        MwStoreCombinationQueryVo storeCombinationQueryVo = mwStoreCombinationMapper.getCombDetail(pink.getCid());
        if(ObjectUtil.isNull(storeCombinationQueryVo)) {
            throw new MshopException("拼团不存在或已下架");
        }

        MwUserQueryVo userInfo = userService.getMwUserById(uid);
        MwStoreOrder mwStoreOrder = storeOrderService.getById(pink.getOrderIdKey());
        //拼团访问量
        mwStoreCombinationMapper.incBrowseNum(pink.getPid());
        //拼团访客人数
        mwStoreVisitService.addStoreVisit(uid, pink.getPid());
        return PinkInfoVo.builder()
                .count(count)
                .currentPinkOrder(this.getCurrentPinkOrderId(id,uid))
                .isOk(isOk)
                .pinkAll(this.handPinkAll(pinkAll))
                .pinkBool(pinkBool)
                .pinkT(this.handPinkT(pinkT))
                .storeCombination(storeCombinationQueryVo)
                .userBool(userBool)
                .userInfo(userInfo)
                .build();

    }



    /**
     * 返回正在拼团的人数
     *
     * @param id 拼团id
     * @return int
     */
    @Override
    public Long pinkIngCount(Long id) {
        return this.lambdaQuery()
                .eq(MwStorePink::getId,id)
                .eq(MwStorePink::getStatus,OrderInfoEnum.PINK_STATUS_1.getValue())
                .count();
    }


    /**
     * 创建拼团
     * @param order 订单
     */
    @Override
    public void createPink(MwStoreOrderQueryVo order) {
        order = storeOrderService.handleOrder(order);
        Long pinkCount = mwStorePinkMapper.selectCount(Wrappers.<MwStorePink>lambdaQuery()
                .eq(MwStorePink::getOrderId,order.getOrderId()));
        if(pinkCount > 0) {
            return;
        }
    }

    /**
     * 判断用户是否在团内
     * @param id 拼团id
     * @param uid 用户id
     * @return boolean true=在
     */
    @Override
    public boolean getIsPinkUid(Long id, Long uid) {
        Long count = this.lambdaQuery()
                .eq(MwStorePink::getIsRefund, OrderInfoEnum.PINK_REFUND_STATUS_0.getValue())
                .eq(MwStorePink::getUid,uid)
                .and(i->i.eq(MwStorePink::getKId,id).or().eq(MwStorePink::getId,id))
                .count();
        return count > 0;
    }

    /**
     * 获取拼团完成的商品总件数
     * @return int
     */
    @Override
    public int getPinkOkSumTotalNum() {
        return mwStorePinkMapper.sumNum();
    }

    /**
     * 获取拼团成功的用户
     * @param uid uid
     * @return list
     */
    @Override
    public List<String> getPinkOkList(Long uid) {
        List<String> list = new ArrayList<>();
        List<PinkDto> pinkDTOList = mwStorePinkMapper.getPinkOkList(uid);
        for (PinkDto pinkDTO : pinkDTOList) {
            list.add(pinkDTO.getNickname()+"拼团成功");
        }
        return list;
    }


    /**
     * 获取团长拼团数据
     * @param cid 拼团产品id
     * @return PinkAllDto pindAll-参与的拼团的id 集合  list-团长参与的列表
     */
    @Override
    public PinkAllDto getPinkAll(Long cid) {
        Map<String,Object> map = new LinkedHashMap<>(2);
        List<PinkDto> list = mwStorePinkMapper.getPinks(cid);
        List<Long> pindAll = new ArrayList<>();//参与的拼团的id 集合
        for (PinkDto pinkDto : list) {
            pinkDto.setCount(String.valueOf(this.getPinkPeople(pinkDto.getId()
                    ,pinkDto.getPeople())));
            Date date = pinkDto.getStopTime();
            pinkDto.setH(String.valueOf(DateUtil.hour(date,true)));
            pinkDto.setI(String.valueOf(DateUtil.minute(date)));
            pinkDto.setS(String.valueOf(DateUtil.second(date)));
            pindAll.add(pinkDto.getId());
        }


        return PinkAllDto.builder()
                .list(list)
                .pindAll(pindAll)
                .build();
    }


    /**
     * 处理团员
     * @param pinkAll 拼团数据
     * @return list
     */
    private List<MwStorePinkQueryVo> handPinkAll(List<MwStorePink> pinkAll) {

        List<MwStorePinkQueryVo> list = generator.convert(pinkAll, MwStorePinkQueryVo.class);
        for (MwStorePinkQueryVo queryVo : list) {
            MwUserQueryVo userQueryVo = userService.getMwUserById(queryVo.getUid().longValue());
            queryVo.setAvatar(userQueryVo.getAvatar());
            queryVo.setNickname(userQueryVo.getNickname());
        }
        return list;
    }

    /**
     * 处理团长
     * @param pinkT 拼团
     * @return MwStorePinkQueryVo
     */
    private MwStorePinkQueryVo handPinkT(MwStorePink pinkT) {
        MwStorePinkQueryVo pinkQueryVo =  generator.convert(pinkT, MwStorePinkQueryVo.class);
        MwUserQueryVo userQueryVo = userService.getMwUserById(pinkQueryVo.getUid().longValue());
        pinkQueryVo.setAvatar(userQueryVo.getAvatar());
        pinkQueryVo.setNickname(userQueryVo.getNickname());

        return pinkQueryVo;
    }


    /**
     * 获取当前拼团数据返回订单编号
     * @param id 拼团id
     * @param uid uid
     * @return string
     */
    private String getCurrentPinkOrderId(Long id, Long uid) {
        MwStorePink pink = mwStorePinkMapper.selectOne(Wrappers.<MwStorePink>lambdaQuery()
                .eq(MwStorePink::getId,id).eq(MwStorePink::getUid,uid));
        if(pink == null){
            pink = mwStorePinkMapper.selectOne(Wrappers.<MwStorePink>lambdaQuery()
                    .eq(MwStorePink::getKId,id).eq(MwStorePink::getUid,uid));
            if(pink == null) {
                return "";
            }
        }
        return pink.getOrderId();
    }


    /**
     * 获取拼团的团员
     * @param kid 团长id
     * @return list
     */
    private List<MwStorePink> getPinkMember(Long kid) {
        return this.lambdaQuery().eq(MwStorePink::getKId,kid)
                .eq(MwStorePink::getIsRefund,OrderInfoEnum.PINK_REFUND_STATUS_0.getValue())
                .orderByAsc(MwStorePink::getId)
                .list();
    }



    /**
     * 获取一条拼团数据
     * @param id 拼团id
     * @return MwStorePink
     */
    private MwStorePink getPinkUserOne(Long id) {
        return this.lambdaQuery().eq(MwStorePink::getId,id).one();
    }

    /**
     * 拼团人数完成时，判断全部人都是未退款状态
     * @return boolean
     */
    private boolean getPinkStatus(List<Long> idAll) {
        Long count = this.lambdaQuery().in(MwStorePink::getId,idAll)
                .eq(MwStorePink::getIsRefund,OrderInfoEnum.PINK_REFUND_STATUS_1.getValue())
                .count();
        if(count == 0) {
            return true;
        }
        return false;
    }


    /**
     * 拼团完成更改数据写入内容
     * @param uidAll 用户id集合
     * @param idAll 拼团id集合
     * @param uid uid
     * @param pinkT 团长
     */
    private int pinkComplete(List<Long> uidAll,List<Long> idAll,Long uid,
                             MwStorePink pinkT) {
        boolean pinkStatus = this.getPinkStatus(idAll);//判断是否有退款的
        int pinkBool = PinkEnum.PINK_BOOL_0.getValue();
        if(pinkStatus){
            //更改状态
           LambdaQueryWrapper<MwStorePink> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(MwStorePink::getId,idAll);

            MwStorePink storePink = new MwStorePink();
            storePink.setStopTime(new Date());
            storePink.setStatus(OrderInfoEnum.PINK_STATUS_2.getValue());

            this.update(storePink,wrapper);

            if(uidAll.contains(uid)){
                pinkBool = PinkEnum.PINK_BOOL_1.getValue();
            }

            //todo 模板消息
        }

        return pinkBool;

    }

    /**
     * 拼团失败退款之后
     * @param uid 用户id
     * @param pid 团长id
     */
    private void orderPinkFailAfter(Long uid, Long pid) {
        MwStorePink mwStorePink = new MwStorePink();
       LambdaQueryWrapper<MwStorePink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStorePink::getId,pid);
        mwStorePink.setStatus(OrderInfoEnum.PINK_STATUS_3.getValue());
        mwStorePink.setStopTime(new Date());
        mwStorePinkMapper.update(mwStorePink,wrapper);

       LambdaQueryWrapper<MwStorePink> wrapperT = new LambdaQueryWrapper<>();
        wrapperT.eq(MwStorePink::getKId,pid);
        mwStorePinkMapper.update(mwStorePink,wrapperT);
        //todo 模板消息
    }


    /**
     * 拼团失败 退款
     * @param pinkAll 拼团数据,不包括团长
     * @param pinkT 团长数据
     * @param pinkBool PinkEnum
     */
    private int pinkFail(List<MwStorePink> pinkAll, MwStorePink pinkT, int pinkBool) {
        Date now = new Date();
        //拼团时间超时  退款
        if(DateUtil.compare(pinkT.getStopTime(),now) < 0){
            pinkBool = PinkEnum.PINK_BOOL_MINUS_1.getValue();
            pinkAll.add(pinkT);
            //处理退款
            for (MwStorePink storePink : pinkAll) {
                storeOrderService.orderApplyRefund("","","拼团时间超时",storePink.getOrderId(),storePink.getUid());
                this.orderPinkFailAfter(pinkT.getUid(),storePink.getId());
            }
        }

        return pinkBool;
    }

    /**
     * 获取参团人和团长和拼团总人数
     * @param pink 拼团
     * @return PinkUserDto
     */
    private PinkUserDto getPinkMemberAndPinK(MwStorePink pink) {
        //查找拼团团员和团长
        List<MwStorePink> pinkAll = null;
        MwStorePink pinkT = null;
        //查找拼团团员和团长
        if(pink.getKId() > 0){ //团长存在
            pinkAll = this.getPinkMember(pink.getKId());
            pinkT =  this.getPinkUserOne(pink.getKId());
        }else{
            pinkAll = this.getPinkMember(pink.getId());
            pinkT =  pink;
        }
        //收集拼团用户id和拼团id
        List<Long> idAll = pinkAll.stream().map(MwStorePink::getId).collect(Collectors.toList());
        List<Long> uidAll = pinkAll.stream().map(MwStorePink::getUid).collect(Collectors.toList());

        idAll.add(pinkT.getId());
        uidAll.add(pinkT.getUid());
        //还差几人
        int count =  pinkT.getPeople() - (pinkAll.size() + 1);


        return PinkUserDto.builder()
                .pinkAll(pinkAll)
                .pinkT(pinkT)
                .idAll(idAll)
                .uidAll(uidAll)
                .count(count)
                .build();
    }


    /**
     * 计算获取团长还差多少人拼团成功
     * @param kid 团长参与拼团id
     * @param people 当前满足拼团的人数
     * @return int
     */
    private Long getPinkPeople(Long kid, int people) {
       LambdaQueryWrapper<MwStorePink> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(MwStorePink::getKId,kid)
                .eq(MwStorePink::getIsRefund, OrderInfoEnum.PINK_REFUND_STATUS_0.getValue());
        //加上团长自己
        Long count = mwStorePinkMapper.selectCount(wrapper) + 1;
        return people - count;
    }

    //=================================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStorePinkQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStorePink> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<MwStorePinkDto> mwStorePinkDtos = generator.convert(page.getList(), MwStorePinkDto.class);
        mwStorePinkDtos.forEach(i ->{
            MwUser mwUser = mwUserService.getById(i.getUid());
            MwStoreCombination storeCombination = combinationService.getById(i.getCid());
            i.setNickname(mwUser.getNickname());
            i.setPhone(mwUser.getPhone());
            i.setUserImg(mwUser.getAvatar());
            i.setProduct(storeCombination.getTitle());
            i.setImage(storeCombination.getImage());
            i.setCountPeople(this.count(new LambdaQueryWrapper<MwStorePink>().eq(MwStorePink::getCid,i.getCid())));
        });
        map.put("content", mwStorePinkDtos);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStorePink> queryAll(MwStorePinkQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStorePink.class, criteria));
    }


    @Override
    public void download(List<MwStorePinkDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStorePinkDto mwStorePink : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户id", mwStorePink.getUid());
            map.put("订单id 生成", mwStorePink.getOrderId());
            map.put("订单id  数据库", mwStorePink.getOrderIdKey());
            map.put("购买商品个数", mwStorePink.getTotalNum());
            map.put("购买总金额", mwStorePink.getTotalPrice());
            map.put("拼团产品id", mwStorePink.getCid());
            map.put("产品id", mwStorePink.getPid());
            map.put("拼团总人数", mwStorePink.getPeople());
            map.put("拼团产品单价", mwStorePink.getPrice());
            map.put(" stopTime",  mwStorePink.getStopTime());
            map.put("团长id 0为团长", mwStorePink.getKId());
            map.put("是否发送模板消息0未发送1已发送", mwStorePink.getIsTpl());
            map.put("是否退款 0未退款 1已退款", mwStorePink.getIsRefund());
            map.put("状态1进行中2已完成3未完成", mwStorePink.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
