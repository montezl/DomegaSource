package com.dmj.util;

/* loaded from: ItemThresholdRegUtil.class */
public class ItemThresholdRegUtil {
    public static int getThreshold(String thresholdStr, String item) {
        int index;
        String item2 = item.trim().toUpperCase();
        if (null != thresholdStr && (index = thresholdStr.indexOf(item2)) != -1) {
            return Integer.parseInt(thresholdStr.substring(index - 2, index));
        }
        return 0;
    }

    public static String getRegAnswerString(String thresholdStr, int thresholdVal) {
        int t = thresholdStr.length() / 3;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < t; i++) {
            int a = Integer.parseInt(thresholdStr.substring(i * 3, (i * 3) + 2));
            if (a >= thresholdVal) {
                buffer.append(thresholdStr.substring((i * 3) + 2, (i * 3) + 3));
            }
        }
        return buffer.toString();
    }

    public static String getRegAnswerString_single(String thresholdStr, int thresholdVal) {
        int t = thresholdStr.length() / 3;
        String answer = "";
        int val = 0;
        for (int i = 0; i < t; i++) {
            int a = Integer.parseInt(thresholdStr.substring(i * 3, (i * 3) + 2));
            if (a >= thresholdVal && a > val) {
                answer = thresholdStr.substring((i * 3) + 2, (i * 3) + 3);
                val = a;
            }
        }
        return answer;
    }

    public static void main(String[] args) {
    }
}
