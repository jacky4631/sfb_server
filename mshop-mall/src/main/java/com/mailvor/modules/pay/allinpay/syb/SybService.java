package com.mailvor.modules.pay.allinpay.syb;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.pay.allinpay.syb.lib.SybPayService;
import com.mailvor.modules.pay.allinpay.syb.lib.SybUtil;
import com.mailvor.config.PayConfig;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.service.PayService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@Component
@Slf4j
public class SybService {
	@Resource
	private PayService payService;

	@Resource
	private MwPayChannelService payChannelService;

	@Resource
	private PayConfig payConfig;
	public Map<String, String> alipay(PayChannelDto certProfile, String orderId, String price) throws Exception{
		SybConfig sybKey = JSON.parseObject(certProfile.getCertProfile(), SybConfig.class);
		SybPayService service = new SybPayService(sybKey);
		Double priceD = Double.parseDouble(price)*100;

		return service.payAli(priceD.longValue(), orderId, payConfig.getTitle(),
				payConfig.getDesc(), "20", certProfile.getNotifyUrl());

	}

	public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");//通知传输的编码为GBK
		response.setCharacterEncoding("UTF-8");
		TreeMap<String,String> params = getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容
		log.info("通联支付回调{}", JSON.toJSONString(params));
		try {

			String orderId = params.get("chnltrxid");
			MwUserRecharge recharge = payService.getRecharge(orderId);
			PayChannelDto payChannel = payService.getChannel(recharge);
			SybConfig sybKey = JSON.parseObject(payChannel.getCertProfile(), SybConfig.class);

			boolean isSign = SybUtil.validSign(params, sybKey.getRsaTlPubKey(), params.get("signtype"));// 接受到推送通知,首先验签
			if(isSign) {
				//减少通道剩余额度
				payChannelService.decPrice(recharge.getPrice(), payChannel.getId());
				//完成订单
				payService.setUserLevel(orderId);
			}
			//验签完毕进行业务处理
		} catch (Exception e) {//处理异常
			// TODO: handle exception
			e.printStackTrace();
		}
		finally{//收到通知,返回success
			response.getOutputStream().write("success".getBytes());
			response.flushBuffer();
		}
	}

	/**
	 * 动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容由于收银宝加字段而引起的签名异常
	 * @param request
	 * @return
	 */
	private TreeMap<String, String> getParams(HttpServletRequest request){
		TreeMap<String, String> map = new TreeMap<String, String>();
		Map reqMap = request.getParameterMap();
		for(Object key:reqMap.keySet()){
			String value = ((String[])reqMap.get(key))[0];
			System.out.println(key+";"+value);
			map.put(key.toString(),value);
		}
		return map;
	}

}
