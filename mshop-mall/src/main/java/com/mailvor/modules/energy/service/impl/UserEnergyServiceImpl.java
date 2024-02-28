/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.energy.config.*;
import com.mailvor.modules.energy.domain.UserEnergy;
import com.mailvor.modules.energy.domain.UserEnergyLog;
import com.mailvor.modules.energy.dto.EnergyConfigDto;
import com.mailvor.modules.energy.dto.MonthCardConfigDto;
import com.mailvor.modules.energy.dto.UserEnergyDto;
import com.mailvor.modules.energy.dto.UserEnergyQueryCriteria;
import com.mailvor.modules.energy.service.UserEnergyService;
import com.mailvor.modules.energy.service.mapper.UserEnergyLogMapper;
import com.mailvor.modules.energy.service.mapper.UserEnergyMapper;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2023-02-04
*/
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserEnergyServiceImpl extends BaseServiceImpl<UserEnergyMapper, UserEnergy> implements UserEnergyService {

    private static final BigDecimal DEFAULT_MIN_ENERGY = BigDecimal.valueOf(0.1);
    @Autowired
    private IGenerator generator;

    @Autowired
    private UserEnergyMapper mapper;

    @Resource
    private MwSystemConfigService configService;

    @Resource
    private UserEnergyLogMapper energyLogMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(UserEnergyQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<UserEnergy> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public List<UserEnergy> queryAll(UserEnergyQueryCriteria criteria){
        return mapper.selectList(QueryHelpPlus.getPredicate(UserEnergyQueryCriteria.class, criteria));
    }

    @Override
    public boolean addEnergy(Long uid, Long oid, String platform, Integer level, Integer energyType) {
        //level 0=自己 1=上级 2=上上级
        EnergyConfigDto energyConfigDto = configService.getEnergyConfig();
        Double energyD = 0d;
        if("tb".equals(platform)) {
            EnergyTbConfig config = energyConfigDto.getTbConfig();
            if(level == 0) {
                energyD = config.getValue();
            } else if (level == 1) {
                energyD = config.getValueOne();
            } else {
                energyD = config.getValueTwo();
            }
        } else if("jd".equals(platform)) {
            EnergyJdConfig config = energyConfigDto.getJdConfig();
            if(level == 0) {
                energyD = config.getValue();
            } else if (level == 1) {
                energyD = config.getValueOne();
            } else {
                energyD = config.getValueTwo();
            }
        } else if("pdd".equals(platform)) {
            EnergyPddConfig config = energyConfigDto.getPddConfig();
            if(level == 0) {
                energyD = config.getValue();
            } else if (level == 1) {
                energyD = config.getValueOne();
            } else {
                energyD = config.getValueTwo();
            }
        } else if("dy".equals(platform)) {
            EnergyDyConfig config = energyConfigDto.getDyConfig();
            if(level == 0) {
                energyD = config.getValue();
            } else if (level == 1) {
                energyD = config.getValueOne();
            } else {
                energyD = config.getValueTwo();
            }
        } else if("vip".equals(platform)) {
            EnergyVipConfig config = energyConfigDto.getVipConfig();
            if(level == 0) {
                energyD = config.getValue();
            } else if (level == 1) {
                energyD = config.getValueOne();
            } else {
                energyD = config.getValueTwo();
            }
        }
        return addEnergy(uid, oid, platform, BigDecimal.valueOf(energyD), energyType);
    }


    @Override
    public boolean addMonthEnergy(Long uid, Long oid, String platform, Integer level, Integer energyType) {
        //level 0=自己 1=上级 2=上上级
        MonthCardConfigDto config = configService.getMonthCardConfig();
        Double energyD;
        if(level == 0) {
            energyD = config.getValue();
        } else if (level == 1) {
            energyD = config.getValueOne();
        } else {
            energyD = config.getValueTwo();
        }
        return addEnergy(uid, oid, platform, BigDecimal.valueOf(energyD), energyType);
    }
    @Override
    public boolean addEnergy(Long uid, Long oid, String platform, BigDecimal addEnergy, Integer energyType) {
        //如果热度小于0.1 给0.1
        if(addEnergy.compareTo(DEFAULT_MIN_ENERGY) == -1) {
            addEnergy = DEFAULT_MIN_ENERGY;
        }

        //type 类型 0=赠送 1=推广
        UserEnergy userEnergy = getById(uid);
        if(userEnergy == null) {
            userEnergy = initEnergy(uid);
        }
        BigDecimal total = BigDecimal.ZERO;
        if("tb".equals(platform)) {
            if(energyType == 0) {
                userEnergy.setTbEnergy(NumberUtil.add(userEnergy.getTbEnergy(), addEnergy));
                total = userEnergy.getTbEnergy();
            } else if(energyType == 1) {
                userEnergy.setTbTuiEnergy(NumberUtil.add(userEnergy.getTbTuiEnergy(), addEnergy));
                total = userEnergy.getTbTuiEnergy();
            }
        } else if("jd".equals(platform)) {
            if(energyType == 0) {
                userEnergy.setJdEnergy(NumberUtil.add(userEnergy.getJdEnergy(), addEnergy));
                total = userEnergy.getJdEnergy();
            } else if(energyType == 1) {
                userEnergy.setJdTuiEnergy(NumberUtil.add(userEnergy.getJdTuiEnergy(), addEnergy));
                total = userEnergy.getJdTuiEnergy();
            }
        } else if("pdd".equals(platform)) {
            if(energyType == 0) {
                userEnergy.setPddEnergy(NumberUtil.add(userEnergy.getPddEnergy(), addEnergy));
                total = userEnergy.getPddEnergy();
            } else if(energyType == 1) {
                userEnergy.setPddTuiEnergy(NumberUtil.add(userEnergy.getPddTuiEnergy(), addEnergy));
                total = userEnergy.getPddTuiEnergy();
            }
        } else if("dy".equals(platform)) {
            if(energyType == 0) {
                userEnergy.setDyEnergy(NumberUtil.add(userEnergy.getDyEnergy(), addEnergy));
                total = userEnergy.getDyEnergy();
            } else if(energyType == 1) {
                userEnergy.setDyTuiEnergy(NumberUtil.add(userEnergy.getDyTuiEnergy(), addEnergy));
                total = userEnergy.getDyTuiEnergy();
            }
        } else if("vip".equals(platform)) {
            if(energyType == 0) {
                userEnergy.setVipEnergy(NumberUtil.add(userEnergy.getVipEnergy(), addEnergy));
                total = userEnergy.getVipEnergy();
            } else if(energyType == 1) {
                userEnergy.setVipTuiEnergy(NumberUtil.add(userEnergy.getVipTuiEnergy(), addEnergy));
                total = userEnergy.getVipTuiEnergy();
            }

        }
        log.info("增加{}热度：用户uid: {} 平台{} 增加热度{} 当前热度{}", energyType==0?"赠送":"推广", uid, platform, addEnergy, total);
        energyLogMapper.insert(UserEnergyLog.builder().uid(uid).oid(oid).platform(platform).energy(addEnergy).totalEnergy(total).type(1).build());
        return saveOrUpdate(userEnergy);
    }

    @Override
    public boolean decEnergy(Long uid, String platform, BigDecimal decEnergy, Integer energyType) {
        //type 类型 0=赠送 1=推广
        UserEnergy userEnergy = getById(uid);
        if(userEnergy == null) {
            userEnergy = initEnergy(uid);
        }
        BigDecimal total = BigDecimal.ZERO;
        if("tb".equals(platform)) {
            if(energyType == 0) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getTbEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setTbEnergy(nowEnergy);
                total = userEnergy.getTbEnergy();
            } else if(energyType == 1) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getTbTuiEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setTbTuiEnergy(nowEnergy);
                total = userEnergy.getTbTuiEnergy();
            }
        } else if("jd".equals(platform)) {
            if(energyType == 0) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getJdEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setJdEnergy(nowEnergy);
                total = userEnergy.getJdEnergy();
            } else if(energyType == 1) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getJdTuiEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setJdTuiEnergy(nowEnergy);
                total = userEnergy.getJdTuiEnergy();
            }
        } else if("pdd".equals(platform)) {
            if(energyType == 0) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getPddEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setPddEnergy(nowEnergy);
                total = userEnergy.getPddEnergy();
            } else if(energyType == 1) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getPddTuiEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setPddTuiEnergy(nowEnergy);
                total = userEnergy.getPddTuiEnergy();
            }
        } else if("dy".equals(platform)) {
            if(energyType == 0) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getDyEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setDyEnergy(nowEnergy);
                total = userEnergy.getDyEnergy();
            } else if(energyType == 1) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getDyTuiEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setDyTuiEnergy(nowEnergy);
                total = userEnergy.getDyTuiEnergy();
            }
        } else if("vip".equals(platform)) {
            if(energyType == 0) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getVipEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setVipEnergy(nowEnergy);
                total = userEnergy.getVipEnergy();
            } else if(energyType == 1) {
                BigDecimal nowEnergy = NumberUtil.sub(userEnergy.getVipTuiEnergy(), decEnergy);
                if(nowEnergy.compareTo(BigDecimal.ZERO) < 0) {
                    nowEnergy = BigDecimal.ZERO;
                }
                userEnergy.setVipTuiEnergy(nowEnergy);
                total = userEnergy.getVipTuiEnergy();
            }
        }
        log.info("减少{}热度：用户uid: {} 平台{} 增加热度{} 当前热度{}", energyType==0?"赠送":"推广", uid, platform, decEnergy, total);
        energyLogMapper.insert(UserEnergyLog.builder().uid(uid).platform(platform).energy(decEnergy).totalEnergy(total).type(2).build());
        return saveOrUpdate(userEnergy);
    }

    @Override
    public List<UserEnergy> getEnergyList(BigDecimal dayEnergy) {
        return list(new LambdaQueryWrapper<UserEnergy>()
                .ge(UserEnergy::getTbEnergy, dayEnergy)
                .or()
                .ge(UserEnergy::getJdEnergy, dayEnergy)
                .or()
                .ge(UserEnergy::getPddEnergy, dayEnergy)
                .or()
                .ge(UserEnergy::getDyEnergy, dayEnergy)
                .or()
                .ge(UserEnergy::getVipEnergy, dayEnergy));
    }
    @Override
    public List<UserEnergy> getEnergyTuiList(BigDecimal dayEnergy) {
        return list(new LambdaQueryWrapper<UserEnergy>()
                .ge(UserEnergy::getTbTuiEnergy, dayEnergy)
                .or()
                .ge(UserEnergy::getJdTuiEnergy, dayEnergy)
                .or()
                .ge(UserEnergy::getPddTuiEnergy, dayEnergy)
                .or()
                .ge(UserEnergy::getDyTuiEnergy, dayEnergy)
                .or()
                .ge(UserEnergy::getVipTuiEnergy, dayEnergy));
    }
    @Override
    public UserEnergyDto getEnergy(Long uid, boolean detailInfo) {
        UserEnergy userEnergy = getById(uid);
        if(!detailInfo) {
            if(userEnergy == null) {
                return new UserEnergyDto();
            }
            UserEnergyDto energyDto = generator.convert(userEnergy, UserEnergyDto.class);
            energyDto.setTotalEnergy(NumberUtil.add(energyDto.getTbEnergy(), energyDto.getJdEnergy(),
                    energyDto.getPddEnergy(), energyDto.getDyEnergy(), energyDto.getVipEnergy(),
                    energyDto.getTbTuiEnergy(), energyDto.getJdTuiEnergy(), energyDto.getPddTuiEnergy(),
                    energyDto.getDyTuiEnergy(), energyDto.getVipTuiEnergy()));
            return energyDto;
        }

        EnergyConfigDto configDto = configService.getEnergyConfig();
        BigDecimal zeroB = BigDecimal.ZERO;
        Double dayTuiEnergy = configDto.getDayTuiEnergy();
        BigDecimal dayTuiB = BigDecimal.valueOf(dayTuiEnergy);
        UserEnergyDto energyDto;
        if(userEnergy == null) {
            energyDto = new UserEnergyDto();
            energyDto.setTbDay(dayTuiB);
            energyDto.setJdDay(dayTuiB);
            energyDto.setPddDay(dayTuiB);
            energyDto.setDyDay(dayTuiB);
            energyDto.setVipDay(dayTuiB);
        } else {
            energyDto = generator.convert(userEnergy, UserEnergyDto.class);
            energyDto.setTotalEnergy(NumberUtil.add(energyDto.getTbEnergy(), energyDto.getJdEnergy(),
                    energyDto.getPddEnergy(), energyDto.getDyEnergy(), energyDto.getVipEnergy(),
                    energyDto.getTbTuiEnergy(), energyDto.getJdTuiEnergy(), energyDto.getPddTuiEnergy(),
                    energyDto.getDyTuiEnergy(), energyDto.getVipTuiEnergy()));
            if(userEnergy.getTbDay().compareTo(zeroB) == 0) {
                energyDto.setTbDay(BigDecimal.valueOf(dayTuiEnergy));
            }
            if(userEnergy.getJdDay().compareTo(zeroB) == 0) {
                energyDto.setJdDay(BigDecimal.valueOf(dayTuiEnergy));
            }
            if(userEnergy.getPddDay().compareTo(zeroB) == 0) {
                energyDto.setPddDay(BigDecimal.valueOf(dayTuiEnergy));
            }
            if(userEnergy.getDyDay().compareTo(zeroB) == 0) {
                energyDto.setDyDay(BigDecimal.valueOf(dayTuiEnergy));
            }
            if(userEnergy.getVipDay().compareTo(zeroB) == 0) {
                energyDto.setVipDay(BigDecimal.valueOf(dayTuiEnergy));
            }
        }
        energyDto.setDefaultDayEnergy(dayTuiB);
        energyDto.setDayEnergyMax(BigDecimal.valueOf(configDto.getDayTuiEnergyMax()));
        return energyDto;
    }

    @Override
    public UserEnergyDto setEnergy(Long uid, String platform, BigDecimal dayEnergy) {
        UserEnergy userEnergy = getById(uid);
        if(userEnergy == null) {
            userEnergy = initEnergy(uid);
        }
        switch (platform){
            case "tb":
                userEnergy.setTbDay(dayEnergy);
                break;
            case "jd":
                userEnergy.setJdDay(dayEnergy);
                break;
            case "pdd":
                userEnergy.setPddDay(dayEnergy);
                break;
            case "dy":
                userEnergy.setDyDay(dayEnergy);
                break;
            case "vip":
                userEnergy.setVipDay(dayEnergy);
                break;
        }
        saveOrUpdate(userEnergy);
        return getEnergy(uid, true);
    }

    protected UserEnergy initEnergy(Long uid) {
        return UserEnergy.builder()
                .uid(uid)
                .tbEnergy(BigDecimal.ZERO)
                .jdEnergy(BigDecimal.ZERO)
                .pddEnergy(BigDecimal.ZERO)
                .dyEnergy(BigDecimal.ZERO)
                .vipEnergy(BigDecimal.ZERO)
                .tbTuiEnergy(BigDecimal.ZERO)
                .jdTuiEnergy(BigDecimal.ZERO)
                .pddTuiEnergy(BigDecimal.ZERO)
                .dyTuiEnergy(BigDecimal.ZERO)
                .vipTuiEnergy(BigDecimal.ZERO)
                .build();
    }

    public static void main(String[] args) {
        if(BigDecimal.valueOf(0.01).compareTo(DEFAULT_MIN_ENERGY) == -1) {
            System.out.println(BigDecimal.valueOf(0.01) + "小于" + DEFAULT_MIN_ENERGY);
        }
    }
}
