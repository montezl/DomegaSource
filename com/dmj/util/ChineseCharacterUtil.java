package com.dmj.util;

import cn.hutool.core.comparator.PinyinComparator;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Map;

/* loaded from: ChineseCharacterUtil.class */
public class ChineseCharacterUtil {
    static final int GB_SP_DIFF = 160;
    static final int[] secPosValueList = {1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600};
    static final char[] firstLetter = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z'};

    public static String getSpells(String characters) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {
            char ch = characters.charAt(i);
            if ((ch >> 7) == 0) {
                if (ch >= 'A' && ch <= 'Z') {
                    ch = (char) (ch + ' ');
                }
                buffer.append(ch);
            } else {
                char spell = getFirstLetter(ch).charValue();
                buffer.append(String.valueOf(spell));
            }
        }
        return buffer.toString();
    }

    public static Character getFirstLetter(char ch) {
        try {
            byte[] uniCode = String.valueOf(ch).getBytes("GBK");
            if (uniCode[0] < 128 && uniCode[0] > 0) {
                return null;
            }
            return Character.valueOf(convert(uniCode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static char convert(byte[] bytes) {
        char result = '#';
        for (int i = 0; i < bytes.length; i++) {
            int i2 = i;
            bytes[i2] = (byte) (bytes[i2] - GB_SP_DIFF);
        }
        int secPosValue = (bytes[0] * 100) + bytes[1];
        int i3 = 0;
        while (true) {
            if (i3 >= 23) {
                break;
            }
            if (secPosValue < secPosValueList[i3] || secPosValue >= secPosValueList[i3 + 1]) {
                i3++;
            } else {
                result = firstLetter[i3];
                break;
            }
        }
        return result;
    }

    public static Comparator<Map<String, Object>> sortByPinyinOfMap(String keyName) {
        PinyinComparator pyComparator = new PinyinComparator();
        return new 1(keyName, pyComparator);
    }

    public static Comparator<Map<String, Object>> sortByPinyinOfMap_contain(String keyName, String[] containArr) {
        PinyinComparator pyComparator = new PinyinComparator();
        return new 2(keyName, containArr, pyComparator);
    }

    public static void main(String[] args) {
        getSpells("JW张三李四王五");
    }
}
