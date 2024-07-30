package com.mailvor.modules.pay.allinpay.syb.lib;

import org.apache.commons.codec.binary.Base64;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class SybUtil {
	/**
	 * md5
	 *
	 * @param b
	 * @return
	 */
	public static String md5(byte[] b) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(b);
			byte[] hash = md.digest();
			StringBuffer outStrBuf = new StringBuffer(32);
			for (int i = 0; i < hash.length; i++) {
				int v = hash[i] & 0xFF;
				if (v < 16) {
					outStrBuf.append('0');
				}
				outStrBuf.append(Integer.toString(v, 16).toLowerCase());
			}
			return outStrBuf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new String(b);
		}
	}

	/**
	 * 判断字符串是否为空
	 *
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		if (s == null || "".equals(s.trim()))
			return true;
		return false;
	}

	/**
	 * 生成随机码
	 *
	 * @param n
	 * @return
	 */
	public static String getValidatecode(int n) {
		Random random = new Random();
		String sRand = "";
		n = n == 0 ? 4 : n;// default 4
		for (int i = 0; i < n; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
		}
		return sRand;
	}



	public static boolean validSign(TreeMap<String, String> param,
			String appkey, String signType) throws Exception {
		if (param != null && !param.isEmpty()) {
			if (!param.containsKey("sign"))
				return false;
			String sign = param.remove("sign");
			if ("MD5".equals(signType)) {// 如果是md5则需要把md5的key加入到排序
				param.put("key", appkey);
			}
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : param.entrySet()) {
				if (entry.getValue() != null && entry.getValue().length() > 0) {
					sb.append(entry.getKey()).append("=")
							.append(entry.getValue()).append("&");
				}
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			if ("MD5".equals(signType)) {
				return sign.toLowerCase().equals(
						md5(sb.toString().getBytes("UTF-8")).toLowerCase());
			} else if("SM2".equals(signType)){
				PublicKey publicKey = SmUtil.pubKeySM2FromBase64Str(appkey);
				return SmUtil.verifySM3SM2(publicKey, "Allinpay", Base64.decodeBase64(sign), sb.toString().getBytes("UTF-8"));
			}else {
				return rsaVerifyPublickey(sb.toString(), sign, appkey, "UTF-8");
			}
		}
		return false;
	}

	public static boolean rsaVerifyPublickey(String content, String sign,
			String publicKey, String charset) throws Exception {
		try {
			PublicKey pubKey = getPublicKeyFromX509("RSA",
					Base64.decodeBase64(publicKey.getBytes()));
			return rsaVerifyPublickey(content, sign, pubKey, charset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("RSAcontent = " + content + ",sign=" + sign
					+ ",charset = " + charset, e);
		}
	}

	public static boolean rsaVerifyPublickey(String content, String sign,
			PublicKey pubKey, String charset) throws Exception {
		try {
			Signature signature = Signature
					.getInstance("SHA1WithRSA");

			signature.initVerify(pubKey);

			if (charset == null || "".equals(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			return signature.verify(Base64.decodeBase64(sign.getBytes()));
		} catch (Exception e) {
			throw e;
		}
	}
	public static String unionSign(TreeMap<String, String> params,String appkey,
			String signType) throws Exception {
		// TODO Auto-generated method stub

		params.remove("sign");
		if ("MD5".equals(signType)) {// 如果是md5则需要把md5的key加入到排序
			params.put("key", appkey);
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getValue() != null && entry.getValue().length() > 0) {
				sb.append(entry.getKey()).append("=").append(entry.getValue())
						.append("&");
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		String sign = "";
		if ("MD5".equals(signType)) {
			System.out.println(sb.toString());
			sign = md5(sb.toString().getBytes("UTF-8"));// 记得是md5编码的加签
			params.remove("key");
		}else if("SM2".equals(signType)){
			System.out.println(sb.toString());
			PrivateKey privkey = SmUtil.privKeySM2FromBase64Str(appkey);
			sign = SmUtil.signSM3SM2RetBase64(privkey, params.get("appid"), sb.toString().getBytes("UTF-8"));//签名
		} else {
			System.out.println(sb.toString());
			sign = rsaSign(sb.toString(), appkey, "UTF-8");
		}
		return sign;
	}

	public static String rsaSign(String content, String privateKey,
			String charset) throws Exception {
		PrivateKey priKey = getPrivateKeyFromPKCS8("RSA",
				Base64.decodeBase64(privateKey.getBytes()));
		return rsaSign(content, priKey, charset);
	}

	public static String rsaSign(String content, byte[] privateKey,
			String charset) throws Exception {
		PrivateKey priKey = getPrivateKeyFromPKCS8("RSA", privateKey);
		return rsaSign(content, priKey, charset);
	}

	public static String rsaSign(String content, PrivateKey priKey,
			String charset) throws Exception {
		Signature signature = Signature
				.getInstance("SHA1WithRSA");
		signature.initSign(priKey);
		if (charset == null || "".equals(charset)) {
			signature.update(content.getBytes());
		} else {
			signature.update(content.getBytes(charset));
		}
		byte[] signed = signature.sign();

		return new String(Base64.encodeBase64(signed));
	}

	public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
			byte[] encodedKey) throws Exception {

		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
	}

	public static PublicKey getPublicKeyFromX509(String algorithm,
			byte[] encodedKey) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

		return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
	}
}
