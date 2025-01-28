package com.ssm.utils;

import cn.hutool.core.util.StrUtil;
import java.math.BigInteger;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;

/* loaded from: AesUtils.class */
public class AesUtils {
    public static String aesEncrypt(String str, String key) throws Exception {
        if (StrUtil.isEmpty(str) || StrUtil.isEmpty(key)) {
            return "";
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
        byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
        return new BASE64Encoder().encode(bytes);
    }

    public static String md5(String encryptStr) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(encryptStr.getBytes("utf-8"));
            byte[] bPwd = md.digest();
            String pwd = new BigInteger(1, bPwd).toString(16);
            if (pwd.length() % 2 == 1) {
                pwd = "0" + pwd;
            }
            return pwd;
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }
}
