package com.mailvor.modules.utils;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

public class RsaUtil {
    private static final String RSA_PUBLIC = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy/3AAGL0RPsPz83b6i0thYeXeYlY+3lmABg3+jSpzHWYViFmOCSSmWXg9zifiu3w+nGxSADXX4953MtA7NP5dk1ad1vCm3LSeCC15Eu6fkfZ4O61mXVRh5WewoI+GlGx4c9h8l9NJfk1IrBn7KiCw+t/tHdbrjkk+4ixRLKJtvc8Sd0TX9KewbQ4VT4mI0xAd4e9PgUMLgCRH0Fmx2HZ/g6ki1fpsUXNByShWH1ueuy06flhXKPj0rOX7urEJ/Aw31PHzGyjpvjV3qcutp8rtTLxHZcwzPP1gaA7QxyAcoWW9csZBjiBiZZXPWEmiJpGTj6cl5U+RGhgtMghDBqWQQIDAQAB";
    private static final String RSA_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDL/cAAYvRE+w/PzdvqLS2Fh5d5iVj7eWYAGDf6NKnMdZhWIWY4JJKZZeD3OJ+K7fD6cbFIANdfj3ncy0Ds0/l2TVp3W8KbctJ4ILXkS7p+R9ng7rWZdVGHlZ7Cgj4aUbHhz2HyX00l+TUisGfsqILD63+0d1uuOST7iLFEsom29zxJ3RNf0p7BtDhVPiYjTEB3h70+BQwuAJEfQWbHYdn+DqSLV+mxRc0HJKFYfW567LTp+WFco+PSs5fu6sQn8DDfU8fMbKOm+NXepy62nyu1MvEdlzDM8/WBoDtDHIByhZb1yxkGOIGJllc9YSaImkZOPpyXlT5EaGC0yCEMGpZBAgMBAAECggEAObeXTsSw1p+urp5RfeXAfoqJ2lJ5QdXsfVRc6kdiT75qFtymXnZCoHKJ/4ke4yFF0KbAgV3zkk83LLi5qwijCc0DLshgvLj1QhYHQQwIPVi5FgKUkVH3eoRkAib1yV6aFQknbnt2iasEVMdS8EwRi7ujS32GDDqQ8zaO6aAWhRTXsn7C/V1SDhSH7RiPojDVHcrQ5vFoRFouARZ9GKgRzpH8xLt2AAQ22+kZGZ86nzwSwgGYXyL+QnJP6x8H9UMln3tcGTBAhfYjrBjLSV+kQsQkHoIjGhzvKrMGk3+Kk7KG4egqViTKpwA/WPnfSonXNPWaqo2Oj0R5oIJKyt+vbQKBgQDrmfNMz0xWUpbbZQVuIO0wGqkILxV2i4WMfyNVgM1vhK8CsDpUu5gldZ0KqJbY4bTVQTWDftgJ0PFr5Skoh6rZY5PTKDeRSos5/P64cT1FwPFsU3HHbybw8BMP/TZ7ymECBRkY+AUuyqgNWw4GpasTqplWT5BbsW0gTE89quvuGwKBgQDdpyr1row6OO7PWgy5Pcx02KAoWn2PV7PS5Lmn3eb7ULDcUDZ2qcyKLJ+/oAF5f1Yw8kadnj5PQMYaA//+uDx3WikiNOaI6HbrRmt2gN9UGsS3YPTjx03LRBWwtlEtaHgnXy6biBNrw8OjSXptA3V53ajzuITgaLi5IDNC+21i0wKBgD6Q96eiNWeHL0C8JyED5Xks206tEkoU6zZQXRXLysKevcs8+YCfANyRy06VTtnxtpAbbRaqjLyunC5HARMeVCS+6Pbea0NORIL1yL86c0ce4f4eesy3m+PIHYfVeq6NJA3hRLT4yIeQkehaEfN582CZoXbmUjPHfm9hVHnP9i+ZAoGAQdpbshiEwvuNP/+iDhslNoXxt2uDKGZHH94ippQP/xSvJxWRyNmT0m2FnLoeFgnWAr7IaiXMn2FAOhR8JyJ3nIIl/Gq5H23pfucyIEgTkfpN/amJuG7LVxvIUfMo1RPXtuLZhw37ryW/ZzD6dY70hRttCdQFbmLOlvnRIRJwIk8CgYBk+1q4YkzeguUpAJ3pIBrzJFUsnhseSJV2td1POgk4pFZ2D7/vI9Ks9LU2uEEmjq2t+6wEdHpMY2I2vtlVWkdVemPicmbEn/G0gxk7H5tdKi+uqAtwW69WUpvtN/fKPaB6Ytr9735fMVzYeZM87DYIvFnrHup1LfAzuNgvuGmboQ==";

    public static String decrypt(String encryptStr, String priKey) {
        RSA rsa = new RSA(priKey, null);
        return rsa.decryptStr(encryptStr, KeyType.PrivateKey);
    }
    public static String encrypt(String str, String pubKey) {
        RSA rsa = new RSA(null, pubKey);
        return rsa.encryptBase64(str, KeyType.PublicKey);
    }

    public static void main(String[] args) {
        String encStr = RsaUtil.encrypt("4be0a6e2714a904148ebfeb33daf31b3", RSA_PUBLIC);
        System.out.println("encStr:  " + encStr);
        String decStr = RsaUtil.decrypt(encStr, RSA_PRIVATE);
        System.out.println(decStr);
//        RSA rsa = new RSA();
//        // 获取公钥
//        String pubKey = rsa.getPublicKeyBase64();
//        System.out.println("pubKey:"+pubKey);
//        // 获取私钥
//        String priKey = rsa.getPrivateKeyBase64();
//        System.out.println("priKey:"+priKey);
//
//
//        // 初始化RSA工具并设置私钥
//        RSA priRsa = new RSA(priKey, null);
//        // 初始化RSA工具并设置公钥
//        RSA pubRsa = new RSA(null, pubKey);
//        // 初始化自定义密钥对RSA工具，但是一般不这么用，因为公私钥需要分离
//
//        // 公钥加密
//        String encodeStr = pubRsa.encryptBase64("1234456", KeyType.PublicKey);
//        System.out.println("encodeStr by public key:" + encodeStr);
//        // 私钥解密
//        String decodeStr = priRsa.decryptStr(encodeStr, KeyType.PrivateKey);
//        System.out.println("decodeStr by private key:" + decodeStr);

    }
}
