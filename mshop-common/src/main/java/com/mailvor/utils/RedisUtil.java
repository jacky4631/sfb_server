/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.constant.ShopConstants.*;

@SuppressWarnings("unchecked")
public class RedisUtil {
    private static RedisTemplate<String,Object> redisTemplate = SpringContextUtils
            .getBean("redisTemplate",RedisTemplate.class);

    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public static boolean expire(String key,long time){
        try {
            if(time>0){
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效 失效时间为负数，说明该主键未设置失效时间（失效时间默认为-1）
     */
    public static long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false 不存在
     */
    public static boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public static void del(String ... key){
        if(key!=null&&key.length>0){
            if(key.length==1){
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }


    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key){
        return key==null?null:(T)redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增 此时value值必须为int类型 否则报错
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 保存历史
     * @return true成功 false失败
     */
    public static boolean setHistory(Long uid,String value) {
        try {
            redisTemplate.opsForList().remove(MSHOP_USER_HISTORY + uid, 1, value);  //先从列表中删除这个元素

            if (redisTemplate.opsForList().size(MSHOP_USER_HISTORY + uid) < 10) {
                redisTemplate.opsForList().leftPush(MSHOP_USER_HISTORY + uid, value);
            } else {
                //历史数据只保留最近的10条，超过10条,则弹出去
                redisTemplate.opsForList().leftPush(MSHOP_USER_HISTORY + uid, value);
                redisTemplate.opsForList().rightPop(MSHOP_USER_HISTORY + uid);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static List<Object> getHistory(Long uid) {
        try {
            return redisTemplate.opsForList().range(MSHOP_USER_HISTORY + uid, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }

    }

    public static boolean clearHistory(Long uid) {
        try {
            return redisTemplate.delete(MSHOP_USER_HISTORY + uid);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private static String doubleCheckPid(String pid) {
        if(StringUtils.isBlank(pid)) {
            return null;
        }
        Set<String> findKeySet = redisTemplate.keys(getTbPidBindKey() + "*" + pid);
        //如果没有绑定用户 直接返回该pid
        if(findKeySet.isEmpty()) {
            return pid;
        }
        //重新获取pid
        Object pidKey = redisTemplate.opsForSet().pop(getTbPoolKey());
        if(pidKey != null) {
            pid = pidKey.toString();
            return doubleCheckPid(pid);
        }
        return null;
    }
    public static String getPid(Long uid, String type) {
        //追单处需要通过pid获取用户uid 如 pid:tb:{uid}:{pid}
        //查找是否存在已经绑定的pid
        Object pidObj = null;
        String key = getTbPidBindKey() + uid;
        Set<String> findKeySet = redisTemplate.keys(key + "*");
        if(findKeySet.isEmpty()) {
            //没有已经绑定的pid,从list中取
            pidObj = redisTemplate.opsForSet().pop(getTbPoolKey());
            pidObj = doubleCheckPid(pidObj == null ? null : pidObj.toString());
        } else {
            String findKey = findKeySet.stream().findFirst().orElse(null);
            String[] splitKey = findKey.split(":");
            if(splitKey.length == 5) {
                pidObj = splitKey[4];
            }
        }
        //如果pidObj不为null，保存到PID_TB_BINDING，并设置过期时间
        if(pidObj != null) {
            //分享的pid过期时间为2小时，直接购买的为20分钟
            Duration duration;
            if("share".equals(type)) {
                duration = Duration.ofHours(2);
            } else {
                duration = Duration.ofMinutes(20);
            }
            redisTemplate.opsForValue().set(key + ":" + pidObj, "1", duration);
        }
        return pidObj == null ? null : pidObj.toString();

    }

    public static void setPid(String pid) {
        redisTemplate.opsForSet().add(getTbPoolKey(), pid);
    }
    public static void setPids(List<String> pids) {
        redisTemplate.opsForSet().add(getTbPoolKey(), pids.toArray());
    }

    protected static String getTbPoolKey() {
        return PID_TB_POOL + ":" + PAY_NAME;
    }
    public static String getTbPidBindKey() {
        return PID_TB_BINDING + PAY_NAME + ":";
    }
    /**
     * key pid
     * value uid
     *
     * @return the binding pid
     */
    public static Map<String,String> getBindingPid() {
        Set keys = redisTemplate.keys(getTbPidBindKey() + "*");
        Map<String,String> pidMap = new HashMap<>();
        keys.stream().forEach(key->{
            String[] splitKey = key.toString().split(":");
            if(splitKey.length != 5) {
                return;
            }
            String uid = splitKey[3];
            String pid = splitKey[4];
            pidMap.put(pid, uid);
        });
        return pidMap;
    }

    public static Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public static void setTodayUid(Long uid) {
        redisTemplate.opsForSet().add(FEE_TODAY_UID, uid);
    }
    public static List<Object> getTodayUids() {
        //返回并移除redis中的30个元素
        return redisTemplate.opsForSet().pop(FEE_TODAY_UID, 90);
    }
    public static void setFeeUid(Long uid) {
        setTodayUid(uid);
    }
    public static void setFeeUid(Long[] uid) {
        redisTemplate.opsForSet().add(FEE_TODAY_UID, uid);
    }

}
