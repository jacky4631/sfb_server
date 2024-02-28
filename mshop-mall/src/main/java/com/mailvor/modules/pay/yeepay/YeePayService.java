package com.mailvor.modules.pay.yeepay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.config.PayConfig;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.service.PayService;
import com.mailvor.modules.pay.yeepay.dto.CardBinDto;
import com.mailvor.modules.user.domain.MwUserCard;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.MwUserCardService;
import com.mailvor.modules.user.service.dto.CardDto;
import com.mailvor.modules.user.service.dto.UserCardDto;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import com.yeepay.yop.sdk.exception.YopClientException;
import com.yeepay.yop.sdk.service.account.AccountClient;
import com.yeepay.yop.sdk.service.account.AccountClientBuilder;
import com.yeepay.yop.sdk.service.account.request.PayOrderRequest;
import com.yeepay.yop.sdk.service.account.response.PayOrderResponse;
import com.yeepay.yop.sdk.service.cashier.CashierClient;
import com.yeepay.yop.sdk.service.cashier.CashierClientBuilder;
import com.yeepay.yop.sdk.service.cashier.model.UnifiedOrderUnifiedCashierOrderResponseDTOResult;
import com.yeepay.yop.sdk.service.cashier.request.UnifiedOrderRequest;
import com.yeepay.yop.sdk.service.cashier.response.UnifiedOrderResponse;
import com.yeepay.yop.sdk.service.frontcashier.FrontcashierClient;
import com.yeepay.yop.sdk.service.frontcashier.FrontcashierClientBuilder;
import com.yeepay.yop.sdk.service.frontcashier.request.BindcardRequestV2Request;
import com.yeepay.yop.sdk.service.frontcashier.request.GetcardbinRequest;
import com.yeepay.yop.sdk.service.frontcashier.response.BindcardRequestV2Response;
import com.yeepay.yop.sdk.service.frontcashier.response.GetcardbinResponse;
import com.yeepay.yop.sdk.utils.DigitalEnvelopeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
public class YeePayService {
	private static final CashierClient api = CashierClientBuilder.builder().build();
	private static final FrontcashierClient bindApi = FrontcashierClientBuilder.builder().build();
	private static final AccountClient extractApi = AccountClientBuilder.builder().build();
	@Resource
	private PayService payService;

	@Resource
	private MwPayChannelService payChannelService;

	@Resource
	private IGenerator generator;

	@Resource
	private PayConfig payConfig;

	@Resource
	private MwUserCardService cardService;
	/**
	 * 快捷支付
	 * */
	public Map<String, Object> bankPay(PayChannelDto certProfile, String orderId, String price, String phone, MwUserCardQueryVo card, Integer type){
		YeePayConfig yeeConfig = JSON.parseObject(certProfile.getCertProfile(), YeePayConfig.class);

        UnifiedOrderRequest request = new UnifiedOrderRequest();
        request.setParentMerchantNo(yeeConfig.getParentMerchantNo());
        request.setMerchantNo(yeeConfig.getMerchantNo());
        request.setOrderId(orderId);
        request.setOrderAmount(new BigDecimal(price));
		String title = payConfig.getTitle();
		if(type == 1) {
			title = title.replace("一年服务费", "体验卡");
		} else if(type == 2){
			title = title.replace("一年服务费", "月卡服务费");
		}
        request.setGoodsName(title);
        request.setFundProcessType("REAL_TIME");
        request.setNotifyUrl(certProfile.getNotifyUrl());
		//页面回调地址
		//支付成功后跳转的URL，如商户指定页面回调地址， 支付完成后会从易宝的支付成功页跳转至商家指定页面，
		// 只有走标准收银台的订单此地址才有作用。注意：最大长度200个字符
//        request.setReturnUrl("https://notify.merchant.com/xxx");
		JSONObject cardInfo = new JSONObject();
		cardInfo.put("cardName", card.getCardName());
		cardInfo.put("idCardNo", card.getCardNo());

//		if(StringUtils.isNotBlank(card.getBankNo())) {
//			cardInfo.put("bankCardNo", card.getBankNo());
//		}
//		if(StringUtils.isNotBlank(card.getPhone())) {
//			cardInfo.put("mobilePhoneNo", card.getPhone());
//		}
		request.setPayerInfo(cardInfo.toJSONString());
        request.setLimitPayType("YJZF,EBANK");
//		request.setCardType("DEBIT");
        request.setAggParam("{\"scene\":\"XIANXIA\"}");
        request.setNoCardParam("{\"userNo\":\"" + phone + "\",\"userType\":\"PHONE\"}");
		Map<String, Object> res = new HashMap<>();
        try {
            UnifiedOrderResponse response = api.unifiedOrder(request);

			//银行卡下单统一返回1=成功 0=失败
			log.info("易宝银行卡支付报文{} 返回{}",JSON.toJSONString(request), JSON.toJSONString(response));
			UnifiedOrderUnifiedCashierOrderResponseDTOResult result = response.getResult();
			res.put("code", "00000".equals(result.getCode()) ? "1": "0");
			res.put("url", result.getCashierUrl());
			res.put("message", result.getMessage());
			res.put("uniqueOrderNo", result.getUniqueOrderNo());
            log.info("result:{}", response.getResult());
        } catch (YopClientException e) {
			res.put("code", "0");
            log.error("Exception when calling CashierClient#unifiedOrder, ex:", e);
        }
		return res;
	}

	public String notify(HttpServletRequest request) {
		//获取回调数据
		String cipherText = request.getParameter("response");
		log.info("易宝支付回调加密数据{}", cipherText);

		try {
			//默认从配置文件中获取易宝公钥和商户私钥
			String plaintText = DigitalEnvelopeUtils.decrypt(cipherText, "RSA2048");
			log.info("易宝支付回调解密数据{}", plaintText);
			JSONObject res = JSON.parseObject(plaintText);
			String status = res.getString("status");
			if("SUCCESS".equals(status)) {
				String orderId = res.getString("orderId");
				MwUserRecharge recharge = payService.getRecharge(orderId);
				PayChannelDto payChannel = payService.getChannel(recharge);
				//减少通道剩余额度
				payChannelService.decPrice(recharge.getPrice(), payChannel.getId());
				//完成订单
				payService.setUserLevel(orderId);
				return "SUCCESS";
			}
			//验签完毕进行业务处理
		} catch (Exception e) {//处理异常
			// TODO: handle exception
			e.printStackTrace();
		}
		return "FAIL";
	}

	public CardBinDto getCardBin(String card) {
		GetcardbinRequest request = new GetcardbinRequest();
		request.setBankCardNo(card);
		try {
			GetcardbinResponse response = bindApi.getcardbin(request);
			log.info("result:{}", response.getResult());

			CardBinDto binDto = generator.convert(response.getResult(), CardBinDto.class);
			if("NOP00000".equals(response.getResult().getCode())) {
				binDto.setCode(1);
				binDto.setMessage("识别成功");
			}
			return binDto;
		} catch (YopClientException e) {
			log.error("Exception when calling FrontcashierClient#getcardbin, ex:", e);
		}
		return new CardBinDto();
	}

	public Map<String,Object> bindCard(Long uid, PayChannelDto certProfile, String bankNo, String phone, MwUserCardQueryVo card){
		CardBinDto cardBinDto = getCardBin(bankNo);
		Map<String, Object> res = new HashMap<>();
		if(cardBinDto.getCode()  != 1) {
			res.put("code", cardBinDto.getCode());
			res.put("message", cardBinDto.getMessage());

			return res;
		}

		YeePayConfig yeeConfig = JSON.parseObject(certProfile.getCertProfile(), YeePayConfig.class);

		//todo 绑卡成功需要 更新原MwUserCard信息
		BindcardRequestV2Request request = new BindcardRequestV2Request();
		request.setParentMerchantNo(yeeConfig.getParentMerchantNo());
		request.setMerchantNo(yeeConfig.getMerchantNo());
		request.setMerchantFlowId(System.currentTimeMillis()+"");
		request.setUserNo(phone);
		request.setUserType("PHONE");
		request.setBankCardNo(bankNo);
		request.setUserName(card.getCardName());
		request.setIdCardNo(card.getCardNo());
		request.setPhone(phone);
//		request.setCvv2("cvv2_example");
//		request.setValidthru("validthru_example");
//		request.setOrderValidate(56);
		request.setAuthType("COMMON_FOUR");
		request.setCardType("OD");
		request.setIsSMS("false");
		try {
			BindcardRequestV2Response response = bindApi.bindcardRequestV2(request);

			log.info("result:{}", response.getResult());
			//绑卡成功 保存
			if("NOP00000".equals(response.getResult().getCode())) {
				MwUserCard userCard = cardService.getById(uid);
				if(userCard !=null) {
					UserCardDto userCardDto = userCard.getCards();
					if(userCardDto == null) {
						userCardDto = new UserCardDto();
						userCardDto.setCards(Arrays.asList(CardDto.builder().no(bankNo).name(cardBinDto.getBankName()).build()));
					} else {
						List<CardDto> cardDtos = userCardDto.getCards();
						CardDto findCard = cardDtos.stream().filter(cardDto -> cardDto.getNo().equals(bankNo)).findFirst().orElse(null);
						if(findCard != null) {
							res.put("code", -1);
							res.put("message", "该卡已绑定");

							return res;
						}
						cardDtos.add(CardDto.builder().no(bankNo).name(cardBinDto.getBankName()).build());
					}
					cardService.updateById(userCard);
				}
				res.put("code", 1);
				res.put("message", "绑定成功");

				return res;
			}
		} catch (YopClientException e) {
			log.error("Exception when calling FrontcashierClient#bindcardRequestV2, ex:", e);
		}
		res.put("code", -1);
		res.put("message", "该银行卡不支持");
		return res;
	}

	public PayOrderResponse extract(PayChannelDto certProfile, String extractId,BigDecimal extractPrice) {
		YeePayConfig yeeConfig = JSON.parseObject(certProfile.getCertProfile(), YeePayConfig.class);

		PayOrderRequest request = new PayOrderRequest();
		request.setParentMerchantNo(yeeConfig.getParentMerchantNo());
		request.setMerchantNo(yeeConfig.getMerchantNo());
		request.setRequestNo("sfb_" + extractId);
		request.setOrderAmount(extractPrice);
//		request.setFeeChargeSide("当商户承担且计费方式为预付实扣或后收时，不支持收款方承担；当平台商或服务商承担时无需指定此手续费承担方；");
		request.setReceiveType("REAL_TIME");
		request.setReceiverAccountNo("62258xxx18496388");
		request.setReceiverAccountName("xx");
//		request.setReceiverBankCode("ICBC");
		request.setBankAccountType("DEBIT_CARD");
//		request.setBranchBankCode("branchBankCode_example");
		request.setComments("提现成功");
//		request.setTerminalType("PC");
		request.setNotifyUrl(certProfile.getNotifyUrl());
//		request.setRemark("remark_example");
//		request.setReceiptComments("receiptComments_example");
//		request.setRiskInfo("riskInfo_example");
		try {
			PayOrderResponse response = extractApi.payOrder(request);
			log.info("result:{}", response.getResult());
			return response;
		} catch (YopClientException e) {
			log.error("Exception when calling AccountClient#payOrder, ex:", e);
		}
		return null;
	}
}
