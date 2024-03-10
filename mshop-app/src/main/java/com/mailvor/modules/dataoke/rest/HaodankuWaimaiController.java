package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.user.domain.MwUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ku")
@Slf4j
public class HaodankuWaimaiController {
    @Resource
    private KuService kuService;
    @GetMapping(value = "/waimai/list")
    public ApiResult meiTuanActivityList() {
        JSONObject mList = kuService.meiTuanActivityList(1, 10).getJSONObject("data");
        JSONArray mRed = mList.getJSONArray("red_activity");
        mRed.stream().forEach(o -> {
            if(o instanceof JSONObject) {
                ((JSONObject)o).put("type", 1);
            }
        });
        JSONArray mTime = mList.getJSONArray("time_activity");
        mTime.stream().forEach(o -> {
            if(o instanceof JSONObject) {
                ((JSONObject)o).put("type", 1);
            }
        });
        JSONObject eList = kuService.eleActivityList(1, 10).getJSONObject("data");
        JSONArray eRed = eList.getJSONArray("red_activity");
        eRed.stream().forEach(o -> {
            if(o instanceof JSONObject) {
                ((JSONObject)o).put("type", 2);
            }
        });
        JSONArray eTime = eList.getJSONArray("time_activity");
        eTime.stream().forEach(o -> {
            if(o instanceof JSONObject) {
                ((JSONObject)o).put("type", 2);
            }
        });
        JSONArray res = new JSONArray();
        res.addAll(mRed);
        res.addAll(eRed);
        res.addAll(mTime);
        res.addAll(eTime);
        return ApiResult.ok(res);
    }
    @UserCheck
    @GetMapping(value = "/waimai/word")
    public JSONObject meiTuanWord(@RequestParam String activityId, @RequestParam Integer type) {
        //美团转链
        if(type == 1) {
            return kuService.meiTuanWord(activityId);
        }
        //饿了么转链
        MwUser mwUser = LocalUser.getUser();
        Long uid = 0L;
        if(mwUser != null) {
            uid = mwUser.getUid();
        }
        return kuService.eleWord(activityId, uid);
    }
    @GetMapping(value = "/ele/list")
    public ApiResult eleActivityList() {
        JSONObject eList = kuService.eleActivityList(1, 10).getJSONObject("data");
        JSONArray eRed = eList.getJSONArray("red_activity");
        eRed.stream().forEach(o -> {
            if(o instanceof JSONObject) {
                ((JSONObject)o).put("type", 2);
            }
        });
        JSONArray eTime = eList.getJSONArray("time_activity");
        eTime.stream().forEach(o -> {
            if(o instanceof JSONObject) {
                ((JSONObject)o).put("type", 2);
            }
        });
        JSONArray res = new JSONArray();
        res.addAll(eRed);
        res.addAll(eTime);
        return ApiResult.ok(res);
    }
}
