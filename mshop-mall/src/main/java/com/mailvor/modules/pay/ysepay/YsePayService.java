package com.mailvor.modules.pay.ysepay;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.MshopException;
import com.mailvor.config.PayConfig;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.service.PayService;
import com.mailvor.modules.user.domain.MwUserBank;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.MwUserBankService;
import com.mailvor.modules.user.service.MwUserExtraService;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import com.mailvor.utils.StringUtils;
import com.yinsheng.YinShengClient;
import com.yinsheng.command.*;
import com.yinsheng.command.base.HeadResponseCommand;
import com.yinsheng.command.base.MerchantHeadRequestCommand;
import com.yinsheng.command.publicInterface.AccountRespCommand;
import com.yinsheng.command.publicInterface.QueryAmountReqCommand;
import com.yinsheng.command.publicInterface.QueryAmountRespCommand;
import com.yinsheng.command.scancode.FrontCodePayReqCommand;
import com.yinsheng.command.scancode.FrontCodePayRespCommand;
import com.yinsheng.command.smsVerify.ConfirmVerifyReqCommand;
import com.yinsheng.command.smsVerify.ConfirmVerifyRespCommand;
import com.yinsheng.command.transfer.TransferInnerReqCommand;
import com.yinsheng.command.transfer.TransferInnerRespCommand;
import com.yinsheng.command.wallet.*;
import com.yinsheng.command.walletprotocolpay.*;
import com.yinsheng.common.ServiceEnum;
import com.yinsheng.common.YsPayConstant;
import com.yinsheng.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.mailvor.config.PayConfig.PAY_DESC;
import static com.mailvor.config.PayConfig.PAY_TITLE;
import static com.yinsheng.utils.YsMpPayUtils.YS_VALIDY_SIGN_PUBLIC_CER;

@Component
@Slf4j
public class YsePayService {

	@Resource
	private PayService payService;

	@Resource
	private MwPayChannelService payChannelService;

	@Resource
	private IGenerator generator;

	@Resource
	private MwUserBankService bankService;

	@Resource
	private PayConfig payConfig;

	@Resource
	private MwUserExtraService userExtraService;

	public FrontCodePayRespCommand alipay(PayChannelDto certProfile, String orderId, String price) {
		YsePayConfig yesConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);

		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.WALLET_PAY_FRONT_CODE, YsPayConstant.ENV_PRD,
				yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		// 先构建请求客户端
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.WALLET_PAY_FRONT_CODE.getApiName());
		FrontCodePayReqCommand requestCommand = new FrontCodePayReqCommand();
		requestCommand.setRequestNo(getRequestNo(orderId));
		requestCommand.setPayeeMerchantNo(yesConfig.getMerchantNo());
		requestCommand.setOrderDesc(PAY_TITLE);
		requestCommand.setAmount(price);
		requestCommand.setBankType("1903000");
//		requestCommand.setTranType("01");
//		requestCommand.setIsDivision("N");
		requestCommand.setNotifyUrl(certProfile.getNotifyUrl());
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, requestCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		FrontCodePayRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), FrontCodePayRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
		if("COM004".equals(respCommand.getSubCode()) || "COM000".equals(respCommand.getSubCode())) {
			return respCommand;
		}
		return null;
	}


	// 正扫
	public void testFrontScan() {


	}
	/**
	 *
	 * <p>协议支付 -签约请求</p>
	 *
	 * @user ljt 2022年6月8日 下午2:24:25
	 */
	public String signProtocol(PayChannelDto certProfile, String phone, MwUserCardQueryVo card, String bankNo) throws Exception {
		MwUserBank userBank = bankService.findOne(card.getUid(), bankNo);
		if(userBank != null && userBank.getSign() == 1) {
			throw new MshopException("该银行卡已签约");
		}

		YsePayConfig yesConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);

		//查询银行卡
		QueryCardRespCommand cardResp = queryBankCard(yesConfig, bankNo);

		//校验银行卡是否支持 目前只校验借记卡
		QueryBankListRespCommand bankListResp = queryBankList(yesConfig);
		SupportBankModelRespDto supportBank = bankListResp.getBankList().stream().filter(respDto ->
				respDto.getCode().equals(cardResp.getBankCode()) && "Y".equals(respDto.getSupportDebitCard()))
				.findFirst().orElse(null);
		if(supportBank == null) {
			throw new MshopException("该银行卡不支持");
		}

		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.WALLET_PROTOCOL, YsPayConstant.ENV_PRD,
				yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.WALLET_PROTOCOL.getApiName());
		com.yinsheng.command.walletprotocolpay.SignProtocolReqCommand reqCommand = new com.yinsheng.command.walletprotocolpay.SignProtocolReqCommand();
		String requestNo = DateUtil.format(new Date(), "YYYYMMdd") + IdUtil.objectId();
		reqCommand.setRequestNo(requestNo);
		reqCommand.setPayeeMerchantNo(yesConfig.getMerchantNo());
		reqCommand.setUserId("uid" + card.getUid());
		reqCommand.setPayerBankAccountNo(DESUtils.encryptByUTF(bankNo,yesConfig.getDesKey()));
		reqCommand.setPayerBankAccountName(DESUtils.encryptByUTF(card.getCardName(),yesConfig.getDesKey()));
		reqCommand.setPayerPhone(DESUtils.encryptByUTF(phone,yesConfig.getDesKey()));
		reqCommand.setPayerCredentialNo(DESUtils.encryptByUTF(card.getCardNo(),yesConfig.getDesKey()));
		reqCommand.setPayerCredentialType("IDCARD");
		// reqCommand.setCvv(cvv);
		// reqCommand.setValidTimeEnd(validTimeEnd);
		// reqCommand.setPayerCredentialType(payerCredentialType);
		// reqCommand.setMobileIMEI(mobileIMEI);
		log.info("reqCommand=========》{}",JsonParser.parserJavaObjectToJsonString(reqCommand, false));
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		SignProtocolRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), SignProtocolRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		log.info("respJson=========》 {}", respJson);
		//签约成功 保存信息
		if("COM004".equals(respCommand.getSubCode())) {
			//如果不存在 保存；存在 更新协议号
			if(userBank == null) {
				userBank = MwUserBank.builder()
						.bankNo(bankNo)
						.bankCode(cardResp.getBankCode())
						.cardType(cardResp.getCardType())
						.bankName(supportBank.getBankName())
						.uid(card.getUid())
						.protocolNo(respCommand.getProtocolNo())
						.requestNo(requestNo)
						.phone(phone)
						.sign(0)
						.build();
				bankService.save(userBank);
			} else {
				userBank.setProtocolNo(respCommand.getProtocolNo());
				userBank.setRequestNo(requestNo);
				bankService.updateById(userBank);
			}
			return respCommand.getRequestNo();
		} else {
			throw new MshopException(respCommand.getSubMsg());
		}
	}

	/**
	 *
	 * <p>协议支付-签约请求确认</p>
	 *
	 * @user ljt 2022年6月8日 下午2:25:48
	 */
	public void signProtocolConfirm(PayChannelDto certProfile, String requestNo, String code) {
		YsePayConfig yesConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.WALLET_PROTOCOL_CONFIRM, YsPayConstant.ENV_PRD,
				yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.WALLET_PROTOCOL_CONFIRM.getApiName());
		com.yinsheng.command.walletprotocolpay.SignProtocolConfirmReqCommand reqCommand = new com.yinsheng.command.walletprotocolpay.SignProtocolConfirmReqCommand();
		reqCommand.setRequestNo(requestNo);
		reqCommand.setSmsCode(code);
		// reqCommand.setCvv("");
		// reqCommand.setValidTimeEnd("");
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		SignProtocolRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), SignProtocolRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
		if("COM000".equals(respCommand.getSubCode())) {
			MwUserBank userBank = bankService.findByRequestNo(requestNo);
			if(userBank == null) {
				throw new MshopException("该银行卡不存在");
			}
			userBank.setProtocolNo(respCommand.getProtocolNo());
			userBank.setSign(1);
			bankService.updateById(userBank);
			bankService.setDefault(userBank.getUid(), userBank.getId());
		}
	}

	/**
	 *
	 * <p>协议支付-创建协议支付订单</p>
	 *
	 * @user ljt 2022年6月8日 下午2:26:30
	 */
	public Map<String, Object> createProtocol(PayChannelDto certProfile, String orderId, String price, Long uid, String protocolNo) {
		YsePayConfig yesConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.WALLET_PROTOCOL_CREATE_ORDER, YsPayConstant.ENV_PRD,
				yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.WALLET_PROTOCOL_CREATE_ORDER.getApiName());
		com.yinsheng.command.walletprotocolpay.CreateProtocolReqCommand requestCommand = new com.yinsheng.command.walletprotocolpay.CreateProtocolReqCommand();
		requestCommand.setRequestNo(DateUtil.format(new Date(), "YYYYMMdd") + orderId);
		requestCommand.setPayeeMerchantNo(yesConfig.getMerchantNo());
		requestCommand.setOrderDesc(PAY_DESC);
		requestCommand.setAmount(price);
		requestCommand.setUserId("uid" + uid);
		requestCommand.setProtocolNo(protocolNo);
//		requestCommand.setCvv("");
		// requestCommand.setValidTimeEnd("");
		requestCommand.setNotifyUrl(certProfile.getNotifyUrl());
		// requestCommand.setCurrency("");
//		requestCommand.setBusiCode("");
		// requestCommand.setTranType("");
		// requestCommand.setIsDivision("");
//		requestCommand.setRemark("快捷协议支付订单备注");
//		InstallmentInfo installmentInfo = new InstallmentInfo();
//		installmentInfo.setDiscountFeeMode("3");
//		installmentInfo.setDiscountFeeRate(new BigDecimal(1));
//		installmentInfo.setInstallmentNumIn(1);
//		requestCommand.setInstallmentInfo(JsonParser.parserJavaObjectToJsonString(installmentInfo, false));
		// requestCommand.setTimeOut("");
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, requestCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		CreateProtocolRespCommand respCommand = JsonParser.parserJsonStringToJavaObject(
				baseResponseCommand.getBizResponseJson(), CreateProtocolRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
		if(!"COM004".equals(respCommand.getSubCode())) {
			throw new MshopException("支付失败");
		}
		Map<String, Object> res = new HashMap<>();
		res.put("bizSn", respCommand.getPaygateBizSn());
		return res;
	}

	/**
	 *
	 * <p>协议支付-支付请求短信确认</p>
	 *
	 * @user ljt 2022年6月8日 下午2:30:46
	 */
	public boolean fastPayMsgVerify(PayChannelDto certProfile, String paygateBizSn, String code) {
		YsePayConfig yesConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.WALLET_PROTOCOL_PAY_CONFIRM,
				YsPayConstant.ENV_PRD, yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.WALLET_PROTOCOL_PAY_CONFIRM.getApiName());
		com.yinsheng.command.walletprotocolpay.FastPayMsgVerifyReqCommand requestCommand = new com.yinsheng.command.walletprotocolpay.FastPayMsgVerifyReqCommand();
		requestCommand.setPaygateBizSn(paygateBizSn);
		requestCommand.setSmsCode(code);
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, requestCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		FastPayMsgVerifyRespCommand respCommand = JsonParser.parserJsonStringToJavaObject(
				baseResponseCommand.getBizResponseJson(), FastPayMsgVerifyRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
		if(respCommand == null || !"COM000".equals(respCommand.getSubCode()) || "FAILED".equals(respCommand.getState())) {
			throw new MshopException("支付失败");
		}
		return true;
	}

	/**
	 *
	 * <p>协议支付-支付短信重发</p>
	 *
	 * @user ljt 2022年6月8日 下午2:37:02
	 */
	public void protocolPayResend(PayChannelDto certProfile, String paygateBizSn) {
		YsePayConfig yesConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.WALLET_PROTOCOL_PAY_CONFIRM_RESEND,
				YsPayConstant.ENV_TEST, yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.WALLET_PROTOCOL_PAY_CONFIRM_RESEND.getApiName());
		ProtocolPayResendReqCommand reqCommand = new ProtocolPayResendReqCommand();
		reqCommand.setPaygateBizSn(paygateBizSn);
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		ProtocolPayResendRespCommand respCommand = JsonParser.parserJsonStringToJavaObject(
				baseResponseCommand.getBizResponseJson(), ProtocolPayResendRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
	}

	/**
	 *
	 * <p>协议支付-协议状态查询</p>
	 *
	 * @user ljt 2022年6月8日 下午2:38:01
	 */
	public void queryProtocol(PayChannelDto certProfile, String protocolNo) {
		YsePayConfig yesConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.WALLET_PROTOCOL_QUERY, YsPayConstant.ENV_PRD,
				yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.WALLET_PROTOCOL_QUERY.getApiName());
		QueryProtocolReqCommand reqCommand = new QueryProtocolReqCommand();
		reqCommand.setProtocolNo(protocolNo);
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		QueryProtocolRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), QueryProtocolRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
	}

	/**
	 * <p>协议支付-协议解约</p>
	 *
	 * @user ljt 2022年6月8日 上午11:34:01
	 */
	public void cancelProtocol(PayChannelDto certProfile, String protocolNo) {
		YsePayConfig yesConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.WALLET_PROTOCOL_CANCEL, YsPayConstant.ENV_PRD,
				yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.WALLET_PROTOCOL_CANCEL.getApiName());
		CancelProtocolReqCommand reqCommand = new CancelProtocolReqCommand();
		reqCommand.setProtocolNo(protocolNo);
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		CancelProtocolRespCommand respCommand = JsonParser.parserJsonStringToJavaObject(
				baseResponseCommand.getBizResponseJson(), CancelProtocolRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
	}

	/**
	 *
	 * <p>银行卡信息查询</p>
	 *
	 * @user ljt 2022年6月15日 上午11:40:44
	 */
	public QueryCardRespCommand queryBankCard(YsePayConfig yesConfig, String bankNo) {
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.QUERY_CARD, YsPayConstant.ENV_PRD,
				yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.QUERY_CARD.getApiName());
		QueryCardReqCommand reqCommand = new QueryCardReqCommand();
		reqCommand.setBankAccountNo(bankNo);
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		QueryCardRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), QueryCardRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);

		return respCommand;
	}


	/**
	 *
	 * <p>查询快捷银行支持列表</p>
	 *
	 * @user ljt 2022年6月15日 上午11:09:40
	 */
	public QueryBankListRespCommand queryBankList(YsePayConfig yesConfig) {
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.QUERY_BANK_LIST, YsPayConstant.ENV_PRD,
				yesConfig.getPrxPath(), yesConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yesConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.QUERY_BANK_LIST.getApiName());
		QueryBankListReqCommand reqCommand = new QueryBankListReqCommand();
		reqCommand.setProtocolType("01");
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		QueryBankListRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), QueryBankListRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
		return respCommand;
	}

/**
 * 银盛回调参数name:version value:v2.0.0
 * 银盛回调参数name:charset value:UTF-8
 * 银盛回调参数name:requestId value:4C4134902887B9AA9447379A12B22717
 * 银盛回调参数name:sign value:cx/tfbrJGJP7DVhfSNQEG5eR9UD0aY9t+1rziEudH3wlQYos4lORlqSVefFvoYVvJek5AXWr7YV6wybqyGYkSW/sondw9omnO6h9zIKzPha8KpyQu47JdINFJNi6UPmjwBcZ6eU5pDmSFChZRlpyAnK43wPMU9tZEKAucT7ipus=
 * 银盛回调参数name:signType value:RSA
 * 银盛回调参数name:serviceNo value:protocolPayNotify
 * 银盛回调参数name:bizResponseJson value:{"amount":"0.5","cardType":"","channelRecvSn":"202309041055840000000821010012K21693829184851069","channelSendSn":"2023090433090415895598670310001","extendParams":"","openId":"","orderDesc":"","payMode":"12","payTime":"20230904200624","payeeFee":"0.0","payeeMerchantNo":"","payerFee":"0.0","remark":"协议支付订单","requestNo":"202309041698668664225529856","settlementAmt":"0.5","srcFee":"","state":"SUCCESS","tradeDate":"20230904","tradeSn":"01O230904158955690","userId":""}
 * */
	public String notify(HttpServletRequest request) throws YsChannelClientException {
		Map<String,String> params = new HashMap<>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext ();) {
			String name =  ( String )iter.next();
			String[] values = (String[])requestParams.get(name);
			String valueStr="";
			for(int i = 0;i < values.length; i++){
				valueStr = (i== values.length-1)?valueStr+values[i]:valueStr+values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name,valueStr);
//			log.info("银盛回调参数name:{} value:{}", name, valueStr);
		}
		log.info("银盛回调参数{}", JSON.toJSONString(params));
		if(!YsMpPayUtils.verifySign(params, YS_VALIDY_SIGN_PUBLIC_CER)) {
			log.error("银盛回调验签失败");
			return "fail";
		}
		String bizResponseJson = params.get("bizResponseJson");
		if(StringUtils.isBlank(bizResponseJson)) {
			log.error("银盛回调bizResponseJson为空");
			return "fail";
		}
		JSONObject resJson = JSON.parseObject(bizResponseJson);
		String requestNo = resJson.getString("requestNo");
		int prefixLength = 8;
		if(requestNo.length() <= prefixLength) {
			log.error("银盛回调requestNo长度不对");
			return "fail";
		}
		String orderId = requestNo.substring(prefixLength, requestNo.length());
		MwUserRecharge recharge = payService.getRecharge(orderId);
		if(recharge == null) {
			log.error("银盛回调订单不存在");
			return "fail";
		}
		PayChannelDto payChannel = payService.getChannel(recharge);
		if(payChannel == null) {
			log.error("银盛回调通道不存在");
			return "fail";
		}
		try {
			if(resJson.getString("state").equals("SUCCESS")) {
				//减少通道剩余额度
				payChannelService.decPrice(recharge.getPrice(), payChannel.getId());
				//完成订单
				payService.setUserLevel(orderId);
				return "success";
			}
			//验签完毕进行业务处理
		} catch (Exception e) {//处理异常
			log.error("银盛回调异常{}", e);
		}
		return "FAIL";
	}


	public MwUserExtra walletRegister(PayChannelDto payChannel, Long uid, String name, String cardNo, String phone) {
		YsePayConfig yseConfig = JSON.parseObject(payChannel.getCertProfile(), YsePayConfig.class);
		//先构建请求客户端
		YinShengClient sofaClient = new YinShengClient(ServiceEnum.WALLET_REGISTER, YsPayConstant.ENV_PRD,
				yseConfig.getPrxPath(), yseConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yseConfig.getMerchantNo());
		headRequestCommand.setServiceNo("walletRegister");
		WalletRegisterReqCommand requestCommand = new WalletRegisterReqCommand();
		requestCommand.setRequestNo(getRequestNo());
		requestCommand.setName(name);
		requestCommand.setCertType("01");
		requestCommand.setCertNo(cardNo);
		requestCommand.setMobile(phone);
		HeadResponseCommand baseResponseCommand = sofaClient.doAction(headRequestCommand, requestCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		// 返回
		WalletRegisterRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), WalletRegisterRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, true);
		System.out.println("respJson=========》" + respJson);
		String merchantNo;
		if(!"COM000".equals(respCommand.getSubCode())) {
			if(respCommand.getSubMsg().contains("已注册成功,请勿重复注册")) {
				UserInfoRespCommand userInfoResp = userInfo(yseConfig, cardNo);
				merchantNo = userInfoResp.getMerchantNo();
			} else {
				throw new MshopException("钱包注册失败:" + respJson);
			}
		} else {
			merchantNo = respCommand.getMerchantNo();
		}
		MwUserExtra userExtra = userExtraService.getById(uid);
		if(userExtra == null) {
			userExtra = MwUserExtra.builder().uid(uid).merchantNo(merchantNo).build();
			userExtraService.save(userExtra);
		} else {
			userExtra.setMerchantNo(merchantNo);
			userExtraService.updateById(userExtra);
		}
		return userExtra;
	}

	/**
	 * <p>钱包信息查询</p>
	 *
	 * @user linxl 2021年4月7日 下午1:02:17
	 */
	public UserInfoRespCommand userInfo(YsePayConfig yseConfig, String cardNo) {
		//先构建请求客户端
		YinShengClient sofaClient = new YinShengClient(ServiceEnum.WALLET_USER_INFO, YsPayConstant.ENV_PRD,
				yseConfig.getPrxPath(), yseConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(yseConfig.getMerchantNo());
		headRequestCommand.setServiceNo("userInfo");
		UserInfoReqCommand requestCommand = new UserInfoReqCommand();
//		requestCommand.setMerchantNo("Y134025654766093");
		requestCommand.setCertNo(cardNo);
		HeadResponseCommand baseResponseCommand = sofaClient.doAction(headRequestCommand, requestCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		// 返回
		UserInfoRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), UserInfoRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, true);
		System.out.println("respJson=========》" + respJson);
		if(!"COM000".equals(respCommand.getSubCode())) {
			throw new MshopException("钱包信息查询失败");
		}
		return respCommand;
	}

	/**
	 * <p>钱包绑卡请求</p>
	 *
	 * @user linxl 2021年4月7日 下午1:02:17
	 */
	public BindCardRespCommand bindCard(PayChannelDto payChannel, String merchantNo, String bankNo, String phone) {
		YsePayConfig ysePayConfig = JSON.parseObject(payChannel.getCertProfile(), YsePayConfig.class);
		//先构建请求客户端
		YinShengClient sofaClient = new YinShengClient(ServiceEnum.WALLET_BIND_CARD, YsPayConstant.ENV_PRD,
				ysePayConfig.getPrxPath(), ysePayConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(ysePayConfig.getMerchantNo());
		headRequestCommand.setServiceNo("bindCard");
		BindCardReqCommand requestCommand = new BindCardReqCommand();
		requestCommand.setRequestNo(getRequestNo());
		requestCommand.setMerchantNo(merchantNo);
		requestCommand.setBindFunction("02");
		requestCommand.setBankAccountNo(bankNo);
		requestCommand.setBankMobile(phone);
		HeadResponseCommand baseResponseCommand = sofaClient.doAction(headRequestCommand, requestCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		// 返回
		BindCardRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), BindCardRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, true);
		System.out.println("respJson=========》" + respJson);
		if(!"COM000".equals(respCommand.getSubCode())) {
			throw new MshopException("绑卡失败:" + respJson);
		}
		return respCommand;

	}

	/**
	 *
	 * <p>短信验证码确认</p>
	 *
	 * @user ljt 2022年6月15日 上午11:01:49
	 */
	public ConfirmVerifyRespCommand confirmVerify(PayChannelDto payChannel, String requestNo, String authSn, String code) {
		YsePayConfig ysePayConfig = JSON.parseObject(payChannel.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.CONFIRM_VERIFY, YsPayConstant.ENV_PRD,
				ysePayConfig.getPrxPath(), ysePayConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(ysePayConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.CONFIRM_VERIFY.getApiName());
		ConfirmVerifyReqCommand reqCommand = new ConfirmVerifyReqCommand();
		reqCommand.setRequestNo(requestNo);
		reqCommand.setAuthSn(authSn);
		reqCommand.setSmsCode(code);
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		ConfirmVerifyRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), ConfirmVerifyRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("respJson=========》" + respJson);
		if(!"COM000".equals(respCommand.getSubCode())) {
			throw new MshopException("短信验证失败:" + respJson);
		}
		return respCommand;
	}

	public WithdrawRespCommand extract(PayChannelDto certProfile, String extractId, BigDecimal extractPrice, String merchantNo, String linkId) {
		AccountRespCommand respCommand = queryAmount(certProfile, merchantNo);
		//查询银盛账户有多少可提现金额
		BigDecimal cashAmount = new BigDecimal(respCommand.getCashAmount());

		//计算需要转账多少钱 先内部转账到银盛钱包
		//如果银盛钱包可提现金额小于需要提现的金额 就转账
		if(cashAmount.compareTo(extractPrice) == -1) {
			BigDecimal transferPrice = NumberUtil.sub(extractPrice, cashAmount);
			transferInner(certProfile, merchantNo, transferPrice.toString());
		}
		//这里需要继续查询余额,直到到账后才执行提现
		for(int i = 0; i < 10; i++) {
			respCommand = queryAmount(certProfile, merchantNo);
			//查询银盛账户有多少可提现金额
			cashAmount = new BigDecimal(respCommand.getCashAmount());
			if(cashAmount.compareTo(extractPrice) != -1) {
				//提现-1元 因为提现手续费1元 NumberUtil.sub(extractPrice, 1)
				return withdraw(certProfile, extractId, extractPrice.toString(), merchantNo, linkId);
			}
			//循环10次 每次间隔500毫秒查询余额
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	/**
	 *
	 * <p>平台内转账申请</p>
	 *
	 * @user ljt 2022年6月16日 上午9:54:36
	 */
	public void transferInner(PayChannelDto payChannel, String merchantNo, String extractPrice) {
		YsePayConfig ysePayConfig = JSON.parseObject(payChannel.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.TRANSFER_INNER, YsPayConstant.ENV_PRD,
				ysePayConfig.getPrxPath(), ysePayConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(ysePayConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.TRANSFER_INNER.getApiName());
		TransferInnerReqCommand reqCommand = new TransferInnerReqCommand();
		reqCommand.setAmount(extractPrice);
		reqCommand.setMerchantNo(ysePayConfig.getMerchantNo());
		reqCommand.setOrderDesc("平台内转账申请");
		reqCommand.setPayeeMerchantNo(merchantNo);
		reqCommand.setPayerMerchantNo(ysePayConfig.getMerchantNo());
		reqCommand.setRequestNo(getRequestNo());
		reqCommand.setTransferType("P2C");
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		TransferInnerRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), TransferInnerRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("transferInner respJson=========》" + respJson);
		if(!"COM000".equals(respCommand.getSubCode())) {
			throw new MshopException("钱包转账失败:" + respJson);
		}
	}

	/**
	 * <p>钱包账户提现</p>
	 *
	 * @user linxl 2021年4月7日 下午1:02:17
	 */
	public WithdrawRespCommand withdraw(PayChannelDto certProfile, String extractId, String extractPrice, String merchantNo, String linkId) {
		YsePayConfig ysePayConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);
		//先构建请求客户端
		YinShengClient sofaClient = new YinShengClient(ServiceEnum.WALLET_WITHDRAW, YsPayConstant.ENV_PRD,
				ysePayConfig.getPrxPath(), ysePayConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(ysePayConfig.getMerchantNo());
		headRequestCommand.setServiceNo("withdraw");
		WithdrawReqCommand requestCommand = new WithdrawReqCommand();
		requestCommand.setRequestNo(getRequestNo(extractId));
		requestCommand.setMerchantNo(merchantNo);
		requestCommand.setAmount(extractPrice.toString());
		requestCommand.setLinkId(linkId);
		requestCommand.setRemark(PayConfig.appName());
//		requestCommand.setNotifyUrl("http://10.213.32.57:8082/ysmp-notify-ci/testnotify");
		HeadResponseCommand baseResponseCommand = sofaClient.doAction(headRequestCommand, requestCommand);
		YinshengLogger.logBizInfo(baseResponseCommand);
		// 返回
		WithdrawRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), WithdrawRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, true);
		System.out.println("withdraw respJson=========》" + respJson);
		if("COM000".equals(respCommand.getSubCode()) || "COM004".equals(respCommand.getSubCode())) {
			return respCommand;
		}
		throw new MshopException("提现失败:" + respCommand.getSubMsg());
	}

	/**
	 *
	 * <p>账户余额查询</p>
	 */
	public AccountRespCommand queryAmount(PayChannelDto certProfile, String merchantNo) {
		YsePayConfig ysePayConfig = JSON.parseObject(certProfile.getCertProfile(), YsePayConfig.class);
		// 先构建请求客户端
		YinShengClient yinShengClient = new YinShengClient(ServiceEnum.QUERY_AMOUNT, YsPayConstant.ENV_PRD,
				ysePayConfig.getPrxPath(), ysePayConfig.getPrxPwd());
		MerchantHeadRequestCommand headRequestCommand = new MerchantHeadRequestCommand()
				.setSrcMerchantNo(ysePayConfig.getMerchantNo());
		headRequestCommand.setServiceNo(ServiceEnum.QUERY_AMOUNT.getApiName());
		QueryAmountReqCommand reqCommand = new QueryAmountReqCommand();
		reqCommand.setMerchantNo(merchantNo);
		HeadResponseCommand baseResponseCommand = yinShengClient.doAction(headRequestCommand, reqCommand);
		QueryAmountRespCommand respCommand = JsonParser
				.parserJsonStringToJavaObject(baseResponseCommand.getBizResponseJson(), QueryAmountRespCommand.class);
		String respJson = JsonParser.parserJavaObjectToJsonString(respCommand, false);
		System.out.println("账户余额查询 respJson=========》" + respJson);
		AccountRespCommand accountRespCommand = respCommand.getAccountList().get(0);
		return accountRespCommand;
	}

	private String getRequestNo() {
		return DateUtil.format(new Date(), "YYYYMMdd") + IdUtil.objectId();
	}
	private String getRequestNo(String orderId) {
		return DateUtil.format(new Date(), "YYYYMMdd") + orderId;
	}
	public static void main(String[] args) {
		System.out.println(DateUtil.format(new Date(), "YYYYMMdd") + IdUtil.objectId());
	}
}
