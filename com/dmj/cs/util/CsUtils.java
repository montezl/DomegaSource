package com.dmj.cs.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.util.IOUtils;
import com.dmj.util.Const;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.io.FileUtils;

/* loaded from: CsUtils.class */
public class CsUtils {
    public static DecimalFormat df = new DecimalFormat("00");

    public static String getString(Object o) {
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    public static int getInt(Object o) {
        if (o == null) {
            return 0;
        }
        return Integer.parseInt(o.toString());
    }

    public static boolean getBoolean(Object o) {
        if (o == null) {
            return false;
        }
        return Boolean.parseBoolean(o.toString()) || o.toString().equals("1");
    }

    public static double getDouble(Object o) {
        if (o == null) {
            return 0.0d;
        }
        return Double.valueOf(o.toString()).doubleValue();
    }

    public static boolean IsNullOrEmpty(String str) {
        return str == null || str.equals("null") || str.equals("");
    }

    public static Map<String, HashMap<String, Object>> toMap2(String json) {
        return (Map) JSON.parse(json);
    }

    public static Map<String, JSONArray> toMap3(String json) {
        return (Map) JSON.parse(json);
    }

    public static List<Object> toObjectList(String json) {
        return (List) JSON.parse(json);
    }

    public static List<String> toStringList(String json) {
        return (List) JSON.parse(json);
    }

    public static List<byte[]> toByteArraList(String json) {
        return (List) JSON.parse(json);
    }

    public static Map<Integer, Object> toMap4(String json) {
        List<Map<String, Object>> list = (List) JSON.parse(json);
        Map<Integer, Object> map = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (Map<String, Object> m : list) {
                int key = 0;
                Object value = null;
                for (Map.Entry<String, Object> entry : m.entrySet()) {
                    String key0 = entry.getKey();
                    Object value0 = entry.getValue();
                    if (key0.equals("k")) {
                        key = Integer.parseInt(value0.toString());
                    }
                    if (key0.equals("v")) {
                        value = value0;
                    }
                }
                map.put(Integer.valueOf(key), value);
            }
        }
        return map;
    }

    public static void writeByteArrayToFile(String location, String fileName, String base64ByteArratStr) throws Throwable {
        FileUtil.writeBytes(IOUtils.decodeBase64(base64ByteArratStr), location + "/" + fileName);
    }

    public static void writeByteArrayToFile(String location, String fileName, byte[] bytes) throws Throwable {
        FileUtils.writeByteArrayToFile(new File(location + "/" + fileName), bytes);
    }

    public static boolean IsNullOrEmpty(Collection array) {
        if (array == null || array.size() == 0) {
            return true;
        }
        return false;
    }

    public static int size(Collection array) {
        if (array == null) {
            return 0;
        }
        return array.size();
    }

    public static boolean IsNullOrEmpty(Object[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        return false;
    }

    public static void batchWriteByteArrayToFile(String location, List<Object[]> list) throws Throwable {
        for (int i = 0; i < list.size(); i++) {
            Object[] array = list.get(i);
            FileUtil.writeBytes((byte[]) array[1], location + "/" + array[0]);
        }
    }

    public static byte[] readFileToByteArray(String location, String fileName) throws Throwable {
        return FileUtil.readBytes(new File(location + "/" + fileName));
    }

    public static void copyFile(String location, String srcFile, String destFile) {
        File src = new File(location + "/" + srcFile);
        if (src.exists()) {
            try {
                FileUtil.copy(src, new File(location + "/" + destFile), true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeByteArrayToFile(String fileName, byte[] bytes) throws Throwable {
        FileUtil.writeBytes(bytes, fileName);
    }

    public static boolean deleteFile(String location) {
        return FileUtil.del(new File(location));
    }

    public static String[] GetDownFileList(String fileName) {
        try {
            List<String> list = new ArrayList<>();
            File newFile = new File(fileName);
            if (!newFile.exists()) {
            }
            File[] files = newFile.listFiles();
            if (files == null || files.length == 0) {
                return new String[0];
            }
            for (int i = 0; i < files.length; i++) {
                String ex = getExtensionName(files[i].getName()).toLowerCase();
                if (Const.IMAGE_FORMAT.equals(ex) || "bmp".equals(ex) || "tif".equals(ex) || "png".equals(ex)) {
                    list.add(files[i].getName());
                }
            }
            return (String[]) list.toArray(new String[list.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String getExtensionName(String filename) {
        int dot;
        if (filename != null && filename.length() > 0 && (dot = filename.lastIndexOf(46)) > -1 && dot < filename.length() - 1) {
            return filename.substring(dot + 1);
        }
        return filename;
    }

    public static String getClipSampleId(String str, String abValue, int pageNo) {
        String[] array = str.split("\\r\\n");
        for (String s : array) {
            if (s.startsWith(abValue + pageNo)) {
                return s.replace(abValue + pageNo + ":", "");
            }
        }
        return null;
    }

    public static String AddZero(String str) {
        if (str.length() == 2) {
            return str;
        }
        String newStr = "00" + str;
        return newStr.substring(newStr.length() - 2);
    }

    public static String AddZero(int num) {
        return df.format(num);
    }

    public static int getMax(String result) {
        if (IsNullOrEmpty(result) || result.length() % 3 != 0) {
            return 0;
        }
        char[] chars = result.toCharArray();
        int max = 0;
        for (int i = 0; i < chars.length; i += 3) {
            int val = Integer.parseInt(new String(new char[]{chars[i + 1], chars[i + 2]}));
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    public static String reverseRegResult(String regResult) {
        if (regResult.length() % 3 != 0) {
            return regResult;
        }
        char[] chars = regResult.toCharArray();
        String newRegResult = "";
        for (int i = 0; i < chars.length; i += 3) {
            char c = chars[i];
            if (chars.length == 6) {
                c = c == 'A' ? 'T' : 'F';
            }
            newRegResult = newRegResult + new String(new char[]{chars[i + 1], chars[i + 2], c});
        }
        return newRegResult;
    }

    public static String reverseOrderRegResult(String regResult) {
        if (regResult.length() % 3 != 0) {
            return regResult;
        }
        char[] chars = regResult.toCharArray();
        AtomicReference<String> newRegResult = new AtomicReference<>("");
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < chars.length; i += 3) {
            char c = chars[i];
            if (chars.length == 6) {
                c = c == 'A' ? 'T' : 'F';
            }
            map.put(Character.valueOf(c), Integer.valueOf(Integer.parseInt(new String(new char[]{chars[i + 1], chars[i + 2]}))));
        }
        map.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(item -> {
            newRegResult.updateAndGet(v -> {
                return v + df.format(item.getValue()) + item.getKey();
            });
        });
        return newRegResult.get();
    }

    public static int[] getRegMinOrMaxResult(String answer, String regResult) {
        if (StrUtil.isEmpty(answer)) {
            return new int[]{0, 0};
        }
        new HashMap();
        AtomicInteger max = new AtomicInteger(0);
        AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
        Arrays.stream(StrUtil.split(regResult, 3)).forEach(s -> {
            char[] chars = s.toCharArray();
            String option = Convert.toStr(Character.valueOf(chars[2]));
            if (answer.contains(option)) {
                int value = Convert.toInt(chars[0] + "" + chars[1]).intValue();
                min.set(Math.min(min.get(), value));
                max.set(Math.max(max.get(), value));
            }
        });
        return new int[]{min.get(), max.get()};
    }
}
