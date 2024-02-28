/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.tools.service.AliOssService;
import com.mailvor.modules.user.domain.MwUserCard;
import com.mailvor.modules.user.service.MwUserCardService;
import com.mailvor.modules.user.service.dto.MwUserCardQueryCriteria;
import com.mailvor.modules.user.service.mapper.UserCardMapper;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import com.mailvor.modules.utils.AesUtil;
import com.mailvor.utils.StringUtils;
import com.mailvor.utils.WordUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.modules.user.config.ShopConfig.*;

/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserCardServiceImpl extends BaseServiceImpl<UserCardMapper, MwUserCard> implements MwUserCardService {

    @Resource
    private IGenerator generator;

    @Resource
    private UserCardMapper cardMapper;
    @Resource
    private AliOssService ossService;

    @Override
    public MwUserCardQueryVo getUserCardById(Long uid) {
        MwUserCard card = this.getById(uid);
        if(card == null) {
            return new MwUserCardQueryVo();
        }
        return convert(card);
    }

    protected MwUserCardQueryVo convert(MwUserCard card) {
        MwUserCardQueryVo queryVo = generator.convert(card, MwUserCardQueryVo.class);
        queryVo.setPhone(AesUtil.decrypt(queryVo.getPhone()));
        queryVo.setCardFPath(AesUtil.decrypt(queryVo.getCardFPath()));
        queryVo.setCardBPath(AesUtil.decrypt(queryVo.getCardBPath()));
        queryVo.setFacePath(AesUtil.decrypt(queryVo.getFacePath()));
        queryVo.setCardNo(AesUtil.decrypt(card.getCardNoEnc()));
        queryVo.setBankNo(AesUtil.decrypt(card.getBankNoEnc()));
        if("dyb".equals(PAY_NAME)) {
            String contractPath = card.getContractPathNow();
            if(StringUtils.isBlank(contractPath)) {
                contractPath = card.getContractPath();
            }
            queryVo.setContractPath(AesUtil.decrypt(contractPath));
        } else {
            queryVo.setContractPath(AesUtil.decrypt(queryVo.getContractPath()));
        }
        return queryVo;
    }
    @Override
    public MwUserCard getByCardNo(String cardNo) {
        return getOne(new LambdaQueryWrapper<MwUserCard>()
                .eq(MwUserCard::getCardNoMd5,cardNo));

    }

    @Override
    public Map<String, Object> queryAll(MwUserCardQueryCriteria criteria, Pageable pageable) {
        criteria.setBlurryEnc(AesUtil.encrypt(criteria.getBlurry()));
        criteria.setBlurry(null);
        getPage(pageable);
        PageInfo<MwUserCard> page = new PageInfo<>(baseMapper.selectList(QueryHelpPlus.getPredicate(MwUserCard.class, criteria)));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<MwUserCardQueryVo> vos = page.getList().stream().map(mwUserCard -> convert(mwUserCard)).collect(Collectors.toList());
        map.put("content", vos);
        map.put("totalElements", page.getTotal());
        return map;
    }
    @Override
    public void clearSignature(Long uid) {
        cardMapper.clearSignature(uid);
    }

    @Override
    public String generateContract(Long uid, MwUserCard card) throws Exception{
        return generateContract(uid, card, false);
    }
    @Override
    public String generateContract(Long uid, MwUserCard card, boolean convert) throws Exception {
        //这里生成电子合同
        LocalDateTime updateTime = LocalDateTime.ofInstant(card.getCreateTime().toInstant(), ZoneOffset.UTC);

        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("cardName", card.getCardName());
        replaceMap.put("cardNo", AesUtil.decrypt(card.getCardNoEnc()));
        replaceMap.put("phone", AesUtil.decrypt(card.getPhone()));
        replaceMap.put("faceVerify", "实人已认证");
        replaceMap.put("SignDate", updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String pdfPath = WordUtil.generateEContract(LOCAL_PATH, uid, replaceMap,
                AesUtil.decrypt(card.getFacePath()), CONTRACT_URL,LICENSE_URL,
                SEAL_URL, AesUtil.decrypt(card.getCardFPath()), AesUtil.decrypt(card.getCardBPath()));
        //合同上传路径
        String contractPath = uid.toString() + File.separator + 3 + File.separator;

        //解析文件名
        String filename;
        if(StringUtils.isNotBlank(card.getContractPath())) {
            String decContractPath = AesUtil.decrypt(card.getContractPath());
            filename = decContractPath.substring(pdfPath.lastIndexOf(File.separator) + 1, decContractPath.length());
        } else {
            filename = pdfPath.substring(pdfPath.lastIndexOf(File.separator) + 1, pdfPath.length());
        }
        //上传合同
        String contractUrl = ossService.uploadCard(new FileInputStream(pdfPath), filename, contractPath);
        if(contractUrl == null) {
            throw new MshopException("电子合同生成失败");
        }
        if(convert) {
            card.setContractPathNow(AesUtil.encrypt(contractUrl));
        } else {
            String encContractUrl = AesUtil.encrypt(contractUrl);
            card.setContractPath(encContractUrl);
            card.setContractPathNow(encContractUrl);
        }
        saveOrUpdate(card);
        return contractUrl;
    }

    @Override
    public void cleanCard(Long uid) {
        MwUserCard card = getById(uid);
        if(card!=null){
            card.setCardNoMd5(card.getCardNoMd5() + System.currentTimeMillis());
            updateById(card);
            removeById(uid);
        }
    }

    @Override
    public void cleanCardBank(Long uid) {
        MwUserCard card = getById(uid);
        if(card != null) {
            card.setBankNoMd5("");
            card.setBankNoEnc("");
            updateById(card);
        }
    }

    @Override
    public void cleanCardPhone(Long uid) {
        MwUserCard card = getById(uid);
        if(card != null) {
            card.setPhone("");
            updateById(card);
        }
    }

    @Async
    @Override
    public void re(String id) throws Exception {
        //需要校验身份证正反面是否存在 人脸地址是否存在
        //需要注意合同生成时间是支付时间
        if("all".equals(id)) {
            List<MwUserCard> cards = this.list();
            for(MwUserCard card : cards) {
                generateContract(card.getUid(), card);
            }

        } else {
            Long uid = Long.parseLong(id);
            generateContract(uid, getById(uid));
        }
    }
    @Async
    @Override
    public void convertContract(Long uid) {
        //如果不是直接返回
        if(!"dyb".equals(PAY_NAME)) {
            return;
        }
        MwUserCard userCard = getById(uid);
        if(userCard == null) {
            return;
        }
        //当需要转换合同 但是已转换路径不为空 说明已经转换过 返回
        if(StringUtils.isNotBlank(userCard.getContractPathNow()) || StringUtils.isBlank(userCard.getContractPath())) {
            return;
        }
        try {
            generateContract(uid, userCard, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AesUtil.init("YwGop09n6WLonxZQzaihHA==");
        MwUserCard card = new MwUserCard();
        String pdfPath = "/www/wwwroot/mshop/card/6\\ET303430403.PDF";
        String filename;
        if(StringUtils.isNotBlank(card.getContractPath())) {
            filename = AesUtil.decrypt(card.getContractPath()).substring(pdfPath.lastIndexOf(File.separator) + 1, pdfPath.length());
        } else {
            filename = pdfPath.substring(pdfPath.lastIndexOf(File.separator) + 1, pdfPath.length());
        }
        System.out.println(filename);

        card.setContractPath("+ORvODCkb/4DiTEH8Ob1rlgGfnsfxR6GMLkaGA==");
        if(StringUtils.isNotBlank(card.getContractPath())) {
            filename = AesUtil.decrypt(card.getContractPath()).substring(pdfPath.lastIndexOf(File.separator) + 1, pdfPath.length());
        } else {
            filename = pdfPath.substring(pdfPath.lastIndexOf(File.separator) + 1, pdfPath.length());
        }
        System.out.println(filename);
    }
}
