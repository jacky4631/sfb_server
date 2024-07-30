package com.mailvor.modules.user.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
public class AppDataConfig implements Serializable {
    /**
     * 好单库cms的cid
     * 申请链接：https://user.cms.haodanku.com/record
     * 例如：https://renyigo8.kuaizhan.com/?cid=6bpB4iTC#/     kuCid=6bpB4iTC
     * */
    @NotBlank(message = "好单库cms的cid不能为空")
    private String kuCid;


    /**
     * app分享地址，其他人扫二维码打开的app页面地址
     * 使用快站地址
     * */
    @NotBlank(message = "app分享地址不能为空")
    private String share = "https://dianlb903.kuaizhan.com/3083083322";


    /**
     * app分享文字描述
     * 需要包含#url#   【#APPNAME#】
     * */
    @NotBlank(message = "app分享文字描述不能为空")
    private String shareContent = "推荐这个APP给你，这款连衣裙，我拿了18件，全卖了，无货源开店，日更4000+商品！\\n━┉┉┉┉∞┉┉┉┉━\\n下载地址：#url#\\n━┉┉┉┉∞┉┉┉┉━\\n#code#或应用市场搜索【#APPNAME#】下载注册";

    /**
     * 商品分享地址，其他人扫商品二维码打开的商品页面地址
     * 使用快站地址
     * */
    @NotBlank(message = "分享商品地址不能为空")
    private String shareGoods = "https://dianlb903.kuaizhan.com/1386038537";


    /**
     * 支付宝红包id
     * */
    private String alired = "763052262";
    /**
     * 淘宝看视频领红包
     * */
    private String taored = "https://m.tb.cn/h.5w5TddH";
    /**
     * 实名认证合同预览地址
     * */
    private String contractPreviewUrl = "";

    /**
     * 登录跳过邀请码 1=可跳过邀请码 0=强制邀请码
     * */
    private String skipCode = "1";


    /**
     * 当开启易宝银行卡支付时，记录订单支付状态
     * */
    private String yeePaySuccUrl = "https://cash.yeepay.com/newwap/pages/mobile/success";
    private String yeePayFailUrl = "https://cash.yeepay.com/newwap/pages/mobile/fail";
    /**
     * app弹窗红包设置
     * */
    private AppPopConfig huodong;

}
