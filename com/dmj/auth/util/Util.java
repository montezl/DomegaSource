package com.dmj.auth.util;

import com.dmj.auth.CreateFile;
import com.dmj.auth.SignProvider;
import com.dmj.auth.bean.License;
import com.dmj.util.Const;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;
import org.jfree.util.Log;

/* loaded from: Util.class */
public class Util {
    public static final String getString(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 255) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString(bytes[i] & 255, 16));
        }
        return buf.toString();
    }

    public static final byte[] getBytes(String hex) {
        char[] chars = hex.toCharArray();
        byte[] bytes = new byte[chars.length / 2];
        int byteCount = 0;
        for (int i = 0; i < chars.length; i += 2) {
            byte newByte = (byte) (0 | hexCharToByte(chars[i]));
            bytes[byteCount] = (byte) (((byte) (newByte << 4)) | hexCharToByte(chars[i + 1]));
            byteCount++;
        }
        return bytes;
    }

    private static final byte hexCharToByte(char ch) {
        switch (ch) {
            case ',':
                return (byte) 10;
            case '-':
            case '.':
            case '/':
            default:
                return (byte) 0;
            case '0':
                return (byte) 0;
            case '1':
                return (byte) 1;
            case '2':
                return (byte) 2;
            case '3':
                return (byte) 3;
            case '4':
                return (byte) 4;
            case '5':
                return (byte) 5;
            case '6':
                return (byte) 6;
            case '7':
                return (byte) 7;
            case '8':
                return (byte) 8;
            case '9':
                return (byte) 9;
        }
    }

    public static String decodeStr(String str) {
        StringBuffer buffer = new StringBuffer();
        if (null != str && str.length() > 0) {
            int index = str.length() / 4;
            for (int i = 1; i <= index; i++) {
                char ch = str.charAt((i * 4) - 1);
                if (ch == 'A') {
                    buffer.append(Const.STRING_SEPERATOR);
                } else {
                    buffer.append(ch);
                }
            }
        }
        return buffer.toString();
    }

    public static String encodeStr(String str) {
        StringBuffer buffer = new StringBuffer();
        if (null != str && str.length() > 0) {
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch == ',') {
                    buffer.append("0x0A");
                } else {
                    buffer.append("0x0" + ch);
                }
            }
        }
        return buffer.toString();
    }

    public static boolean verfify(String licPath) throws Exception {
        License license2 = CreateFile.getLicense(licPath);
        ByteUtil.getPub(license2.getVal());
        new CreateFile();
        License license = CreateFile.getLicense(licPath);
        byte[] sign = ByteUtil.combinByteArray(decodeStr(license.getSignature()));
        byte[] pubKey = ByteUtil.getPub(ByteUtil.getRrandomByLocal(licPath));
        new CreateFile();
        String licenseString = CreateFile.getLicenceStringByLocal(licPath);
        new SignProvider();
        return SignProvider.verify(pubKey, licenseString, sign);
    }

    public static Float round(Float value, int scale) {
        Float result = Float.valueOf(0.0f);
        if (null != value) {
            result = Float.valueOf(new BigDecimal(String.valueOf(value)).setScale(scale, RoundingMode.HALF_UP).floatValue());
        }
        return result;
    }

    public static String[] getConnectionInfo(String filePath) {
        if (null == filePath) {
            return null;
        }
        try {
            if (filePath.equals("")) {
                return null;
            }
            Properties easprop = new Properties();
            FileInputStream fis = new FileInputStream(filePath);
            easprop.load(fis);
            String[] info = {(String) easprop.get("jdbcUrl"), (String) easprop.get("user"), (String) easprop.get("password")};
            return info;
        } catch (Exception e) {
            Log.debug(" 修改页码： 获取连接信息", e);
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] createSignaturer(String filePath, String prik) throws Exception {
        byte[] priKey = ByteUtil.getPri(prik);
        String licenseString = CreateFile.getLicenceStringByLocal(filePath);
        new Signaturer();
        byte[] signed = Signaturer.sign(priKey, licenseString);
        return signed;
    }

    public static void main(String[] args) {
    }
}
