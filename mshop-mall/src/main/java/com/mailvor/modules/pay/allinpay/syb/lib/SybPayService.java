package com.mailvor.modules.pay.allinpay.syb.lib;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.pay.allinpay.syb.SybConfig;

import java.util.Map;
import java.util.TreeMap;

public class SybPayService {
	private SybConfig sybKey;

	public SybPayService(SybConfig sybKey) {
		this.sybKey = sybKey;
	}

	/**
	 *
	 * @param trxamt
	 * @param reqsn
	 * @param paytype
	 * @param body
	 * @param remark
	 * @param acct
	 * @param validtime
	 * @param notify_url
	 * @param limit_pay
	 * @param idno
	 * @param truename
	 * @param asinfo
	 * @param sub_appid
	 * @param goods_tag  单品优惠信息
	 * @param chnlstoreid
	 * @param subbranch
	 * @param cusip   限云闪付JS支付业务
	 * @param fqnum   限支付宝分期业务
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> pay(long trxamt,String reqsn,String paytype,String body,String remark,String validtime,String notify_url,
								  String acct,
								  String limit_pay,
			String idno,String truename,String asinfo,String sub_appid,String goods_tag,String benefitdetail,String chnlstoreid,String subbranch,String extendparams,String cusip,String fqnum) throws Exception{
		HttpConnectionUtil http = new HttpConnectionUtil(sybKey.getApiUrl() + "/pay");
		http.init();
		TreeMap<String,String> params = new TreeMap<String,String>();
		if(!SybUtil.isEmpty(sybKey.getOrgId()))
			params.put("orgid", sybKey.getOrgId());
		params.put("cusid", sybKey.getCusId());
		params.put("appid", sybKey.getAppId());
		params.put("version", "11");
		params.put("trxamt", String.valueOf(trxamt));
		params.put("reqsn", reqsn);
		params.put("paytype", paytype);
		params.put("randomstr", SybUtil.getValidatecode(8));
		params.put("body", body);
		params.put("remark", remark);
		params.put("validtime", validtime);
		params.put("acct", acct);
		params.put("notify_url", notify_url);
		params.put("limit_pay", limit_pay);
		params.put("sub_appid", sub_appid);
		params.put("goods_tag", goods_tag);
		params.put("benefitdetail", benefitdetail);
		params.put("chnlstoreid", chnlstoreid);
		params.put("subbranch", subbranch);
		params.put("extendparams", extendparams);
		params.put("cusip", cusip);
		params.put("fqnum", fqnum);
		params.put("idno", idno);
		params.put("truename", truename);
		params.put("asinfo", asinfo);
		params.put("signtype", "RSA");
		String appKey = sybKey.getRsaCusPriKey();
		params.put("sign", SybUtil.unionSign(params,appKey,"RSA"));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(result);
		return map;

	}

	public Map<String, String> payAli(long trxamt,String reqsn,String body,String remark,String validtime,String notify_url) throws Exception {
		return pay(trxamt, reqsn, "A03", body,
				remark, validtime, notify_url,"",
				"","","","",
				"", "", "", "", "", "", "","");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,String> handleResult(String result) throws Exception{
		System.out.println("ret:"+result);
		Map map = JSON.parseObject(result, Map.class);
		if(map == null){
			throw new Exception("返回数据错误");
		}
		if("SUCCESS".equals(map.get("retcode"))){
			TreeMap tmap = new TreeMap();
			tmap.putAll(map);
			String appkey = sybKey.getRsaCusPriKey();
			if(SybUtil.validSign(tmap, appkey, "RSA")){
				System.out.println("签名成功");
				return map;
			}else{
				throw new Exception("验证签名失败");
			}

		}else{
			throw new Exception(map.get("retmsg").toString());
		}
	}

}
