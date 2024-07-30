package com.mailvor.modules.pay.allinpay.syb.lib;

public class SybConstants {
	//正式环境测试参数
//	public static final String SYB_ORGID = "";//集团/机构模式下该参数不为空，且appid与key是与次参数对应
//	public static final String SYB_CUSID = "990440148166000";
//	public static final String SYB_APPID = "00000003";
//	public static final String SYB_MD5_APPKEY = "a0ea3fa20dbd7bb4d5abf1d59d63bae8";
//	public static final String SYB_APIURL = "https://vsp.allinpay.com/apiweb/unitorder";//生产环境
//	public static final String SIGN_TYPE = "SM2";
//	public static final String SYB_RSACUSPRIKEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO0HpPUP+eHk//Ba6ZOePvoZVDpOCRtt943oeVfCTllye43bqja1jVIaebX0MgX+yPYnWIQIOJ9ubSH0R4iyY9y1/HR00qkUpfW3/0usBPt9qn7r0xtFHerhVCd4dT2rKb2Oc5IhKOg05cw/BmMFohMkFsqt0jlrUXI8zJOlLIcxAgMBAAECgYA9lt/pAYa3iK5sQOMyhUrt54j4QXCiXPeXOxHUmNuM6G9sU+itoI0hCVoYymP5JNQJCf45CH3WB3Z5/SRdQ6Uoo1cjao6cCohPLxMSfJglsZCHckPH53o25RKEza4njIgKC+yN7HAhanKymhw/yYQ6i0aXq38zFIk8djMtE7R6xQJBAP6jvNy7UhPKO5rxGFKR+MvvbO3qnYH6x0jZCGY3FlxuGfbavueOiFtMeK67FuDv683dcUKi+M48yR4kH5CfIusCQQDuS9KF6mlm3kHAiZWgVhE8VVNYGpRLCRDgAKm4InGmvk5mUv+O1yAtAFVAEHWIgD4awC7Eqf1YFrSF/It9HV9TAkEAsXiU7JJxhfFw0XAvL30lFZ1tIfReinSp6A+7VuIV552k4vNaEjC4wEjv43fpXiRZCEXJ5lOHbNXYpfUvOrBuuQJAOpow8rf8Jc0g1G3Be0XPRUwii/c1YuKe4Meo9VybIIuKkkV1Dba/9fEwBepGTURkgYWjur+nSyOCT7UUxLcVewJAPLig8dVfKpsiNwYuveEYMcFaO5xoRuiB7v+CMmvxpuuK+rrFS+d7RdmwDbnBiDV4JkTgFObUiGvB7MtS+LGfhw==";
//	public static final String SYB_RSATLPUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCm9OV6zH5DYH/ZnAVYHscEELdCNfNTHGuBv1nYYEY9FrOzE0/4kLl9f7Y9dkWHlc2ocDwbrFSm0Vqz0q2rJPxXUYBCQl5yW3jzuKSXif7q1yOwkFVtJXvuhf5WRy+1X5FOFoMvS7538No0RpnLzmNi3ktmiqmhpcY/1pmt20FHQQIDAQAB";
////	/**商户sm2私钥,用于向通联发起请求前进行签名**/
//	public static final String SYB_SM2PPRIVATEKEY = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgjj4Rk+b0YjwO+UwXofnHf4bK+kaaY5Btkd8nMP2VimmgCgYIKoEcz1UBgi2hRANCAAQqlALW4qGC3bP1x3wo5QsKxaCMEZJ2ODTTwOQ+d8UGU7GoK/y/WMBQWf5upMnFU06p5FxGooXYYoBtldgm03hq";
////	/**通联平台sm2公钥，用于请求返回或者通联通知的验签**/
//	public static final String SYB_SM2TLPUBKEY = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEBQicgWm0KAMqhO3bdqMUEDrKQvYg8cCXHhdGwq7CGE6oJDzJ1P/94HpuVdBf1KidmPxr7HOH+0DAnpeCcx9TcQ==";

	//测试环境调试参数
//	public static final String SYB_ORGID = "";
//	public static final String SYB_CUSID = "990581007426001";
//	public static final String SYB_APPID = "00000051";
//	public static final String SYB_MD5_APPKEY = "allinpay888";
//	public static final String SYB_APIURL = "https://test.allinpaygd.com/apiweb/unitorder";//生产环境
//	public static final String SIGN_TYPE = "MD5";
//	/**商户RSA私钥,用于向通联发起请求前进行签名**/
//	public static final String SYB_RSACUSPRIKEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJgHMGYsspghvP+yCbjLG43CkZuQ3YJyDcmEKxvmgblITfmiTPx2b9Y2iwDT9gnLGExTDm1BL2A8VzMobjaHfiCmTbDctu680MLmpDDkVXmJOqdlXh0tcLjhN4+iDA2KkRqiHxsDpiaKT6MMBuecXQbJtPlVc1XjVhoUlzUgPCrvAgMBAAECgYAV9saYTGbfsdLOF5kYo0dve1JxaO7dFMCcgkV+z2ujKtNmeHtU54DlhZXJiytQY5Dhc10cjb6xfFDrftuFcfKCaLiy6h5ETR8jyv5He6KH/+X6qkcGTkJBYG1XvyyFO3PxoszQAs0mrLCqq0UItlCDn0G72MR9/NuvdYabGHSzEQJBAMXB1/DUvBTHHH4LiKDiaREruBb3QtP72JQS1ATVXA2v6xJzGPMWMBGQDvRfPvuCPVmbHENX+lRxMLp39OvIn6kCQQDEzYpPcuHW/7h3TYHYc+T0O6z1VKQT2Mxv92Lj35g1XqV4Oi9xrTj2DtMeV1lMx6n/3icobkCQtuvTI+AcqfTXAkB6bCz9NwUUK8sUsJktV9xJN/JnrTxetOr3h8xfDaJGCuCQdFY+rj6lsLPBTnFUC+Vk4mQVwJIE0mmjFf22NWW5AkAmsVaRGkAmui41Xoq52MdZ8WWm8lY0BLrlBJlvveU6EPqtcZskWW9KiU2euIO5IcRdpvrB6zNMgHpLD9GfMRcPAkBUWOV/dH13v8V2Y/Fzuag/y5k3/oXi/WQnIxdYbltad2xjmofJ7DbB7MJqiZZD8jlr8PCZPwRNzc5ntDStc959";
//	/**通联平台RSA公钥，用于请求返回或者通联通知的验签**/
//	public static final String SYB_RSATLPUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDYXfu4b7xgDSmEGQpQ8Sn3RzFgl5CE4gL4TbYrND4FtCYOrvbgLijkdFgIrVVWi2hUW4K0PwBsmlYhXcbR+JSmqv9zviVXZiym0lK3glJGVCN86r9EPvNTusZZPm40TOEKMVENSYaUjCxZ7JzeZDfQ4WCeQQr2xirqn6LdJjpZ5wIDAQAB";
//
//	/**商户sm2私钥,用于向通联发起请求前进行签名**/
//	public static final String SYB_SM2PPRIVATEKEY = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgNqz1EieIP8QVzV7vEmx5e8f7XN7/MIzoeXgEinxcG0agCgYIKoEcz1UBgi2hRANCAAQNfkEgaCQ4cdZ4aD2LWMcnkk5LALQfL05oY8x8XQDIyUM44N15YcTwtFNvHYgyeNRa93vlEUutp935n6rp4yuf";
//	/**通联平台sm2公钥，用于请求返回或者通联通知的验签**/
//	public static final String SYB_SM2TLPUBKEY = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE/BnA8BawehBtH0ksPyayo4pmzL/u1FQ2sZcqwOp6bjVqQX4tjo930QAvHZPJ2eez8sCz/RYghcqv4LvMq+kloQ==";

}
