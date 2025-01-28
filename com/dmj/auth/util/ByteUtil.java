package com.dmj.auth.util;

import com.dmj.auth.CreateFile;
import com.dmj.auth.bean.License;
import com.dmj.util.Const;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Random;

/* loaded from: ByteUtil.class */
public class ByteUtil {
    public static String combinString(byte[] sign) {
        StringBuffer buffer = new StringBuffer();
        for (byte a : sign) {
            buffer.append(((int) a) + Const.STRING_SEPERATOR);
        }
        return buffer.substring(0, buffer.length() - 1);
    }

    public static byte[] combinByteArray(String str) {
        if (null != str && str.trim().length() > 0) {
            String[] sss = str.split(Const.STRING_SEPERATOR);
            byte[] pubk = new byte[sss.length];
            for (int i = 0; i < sss.length; i++) {
                pubk[i] = Byte.valueOf(sss[i]).byteValue();
            }
            return pubk;
        }
        return null;
    }

    public static byte[] getPri(String random) throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        SecureRandom secrand = new SecureRandom(random.getBytes());
        keygen.initialize(1024, secrand);
        KeyPair keys = keygen.genKeyPair();
        PrivateKey prikey = keys.getPrivate();
        byte[] priKey = Base64.encodeToByte(prikey.getEncoded(), true);
        return priKey;
    }

    public static byte[] getPub(String random) throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        SecureRandom secrand = new SecureRandom(random.getBytes());
        keygen.initialize(1024, secrand);
        KeyPair keys = keygen.genKeyPair();
        PublicKey pubkey = keys.getPublic();
        byte[] pubKey = Base64.encodeToByte(pubkey.getEncoded(), true);
        return pubKey;
    }

    public static String getRrandom() {
        Random random = new Random();
        long l = random.nextLong();
        return String.valueOf(l);
    }

    public static String getRrandomByLocal(String licPath) throws Exception {
        new CreateFile();
        License lic = CreateFile.getLicense(licPath);
        if (null == lic) {
            return "获取license信息失败！！";
        }
        return lic.getVal();
    }
}
