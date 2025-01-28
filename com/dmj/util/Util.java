package com.dmj.util;

import cn.hutool.core.convert.Convert;
import com.dmj.auth.CreateFile;
import com.dmj.auth.bean.License;
import com.dmj.auth.util.GetMacUtil;
import com.dmj.auth.util.SystemUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* loaded from: Util.class */
public class Util {
    public static List ArrayToList(String[] str) {
        if (null == str || str.equals("")) {
            return new ArrayList();
        }
        return Arrays.asList(str);
    }

    public static String getPath() {
        return "C://license.lic";
    }

    public List<String> getTableNameByType(String type) {
        Properties pro = new Properties();
        String str = getClass().getResource("").getPath().toString().replace("%20", " ");
        String path = str.substring(1, str.indexOf("WEB-INF")) + "WEB-INF/history_data.properties";
        List<String> tableNameList = new ArrayList<>();
        try {
            InputStream in = new FileInputStream(path);
            pro.load(in);
            String tableNameStr = pro.getProperty(type);
            if (tableNameStr != null) {
                String[] tableNameArray = tableNameStr.split(Const.STRING_SEPERATOR);
                for (String tableName : tableNameArray) {
                    tableNameList.add(tableName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableNameList;
    }

    public static char String2Digital(String answer) {
        if (null == answer) {
            answer = "";
        }
        byte j = 0;
        int length = answer.length();
        for (int i = 0; i < length; i++) {
            switch (answer.charAt(i)) {
                case 'A':
                    j = (byte) (j + 1);
                    break;
                case 'B':
                    j = (byte) (j + 2);
                    break;
                case 'C':
                    j = (byte) (j + 4);
                    break;
                case 'D':
                    j = (byte) (j + 8);
                    break;
                case 'E':
                    j = (byte) (j + 16);
                    break;
                case 'F':
                    j = (byte) (j + 32);
                    break;
                case 'J':
                    j = (byte) (j + 32);
                    break;
            }
        }
        return (char) (j + 48);
    }

    public static char convertStringToChar(String str) {
        int i;
        int i2;
        char charAt;
        char c;
        int strlen = str.length();
        int dig = 0;
        if (strlen > 0) {
            byte b = 0;
            while (true) {
                byte i3 = b;
                if (i3 < strlen) {
                    if (str.charAt(i3) >= 'A' && str.charAt(i3) <= 'Z') {
                        i = dig;
                        i2 = 1;
                        charAt = str.charAt(i3);
                        c = 'A';
                    } else if (str.charAt(i3) >= 'a' && str.charAt(i3) <= 'z') {
                        i = dig;
                        i2 = 1;
                        charAt = str.charAt(i3);
                        c = 'a';
                    } else {
                        return (char) 0;
                    }
                    dig = i + (i2 << (charAt - c));
                    b = (byte) (i3 + 1);
                } else {
                    return (char) (dig + 48);
                }
            }
        } else {
            return (char) 0;
        }
    }

    public static String convert8421ToString(char item) {
        int n = (char) (item - '0');
        byte i = 0;
        int m = n;
        String str = "";
        while (m > 0) {
            if ((m & 1) == 1) {
                str = str + ((char) (65 + i));
            }
            i = (byte) (i + 1);
            m = n >>> i;
        }
        return str;
    }

    public static String auth(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return "授权文件不存在 ！！！";
            }
            License lic = CreateFile.getLicense(filePath);
            Map<String, String> map = SystemUtil.getSystemInfo();
            if (null == map || null == lic) {
                return "授权文件文件  signature验证为false 信息是否有修改 ！！！！！ if: null == map || null == lic";
            }
            boolean macResult = false;
            List<String> macsList = GetMacUtil.getMacList(50);
            int i = 0;
            while (true) {
                if (i >= macsList.size()) {
                    break;
                }
                if (null != macsList.get(i)) {
                    System.out.println("macList " + i + "  : " + macsList.get(i) + "   " + lic.getMac().replace("-", "").replace(":", ""));
                    System.out.println(macsList.get(i).equals(lic.getMac().replace("-", "").replace(":", "")));
                    if (macsList.get(i).equals(lic.getMac().replace("-", "").replace(":", ""))) {
                        macResult = true;
                        break;
                    }
                }
                i++;
            }
            if (!macResult) {
                return "授权文件信息验证  mac 地址不匹配！！！";
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "授权文件文件异常  验证signature  报错！！！！！";
        }
    }

    public static String getSimpleFileNameByFilePath(String path) {
        if (null == path || path.equals("")) {
            return null;
        }
        String[] rs = path.split("/");
        String name = rs[rs.length - 1];
        int position = name.indexOf(".");
        if (position <= 0) {
            return name;
        }
        return name.substring(0, position);
    }

    public static void main(String[] args) {
        System.out.println(String2Digital("BCD"));
    }

    public static String sort(String str) {
        char[] ch = str.toCharArray();
        Arrays.sort(ch);
        String str2 = new String(ch);
        return str2.toUpperCase();
    }

    public static double suitAllObjSingleJudge(String answer, Map<String, Object> defineMap) {
        double score = 0.0d;
        int lengout = Integer.parseInt(defineMap.get("lengout").toString());
        double one1 = Convert.toDouble(defineMap.get("one1"), Double.valueOf(0.0d)).doubleValue();
        double one2 = Convert.toDouble(defineMap.get("one2"), Double.valueOf(0.0d)).doubleValue();
        double one3 = Convert.toDouble(defineMap.get("one3"), Double.valueOf(0.0d)).doubleValue();
        double one4 = Convert.toDouble(defineMap.get("one4"), Double.valueOf(0.0d)).doubleValue();
        double one5 = Convert.toDouble(defineMap.get("one5"), Double.valueOf(0.0d)).doubleValue();
        double one6 = Convert.toDouble(defineMap.get("one6"), Double.valueOf(0.0d)).doubleValue();
        double one7 = Convert.toDouble(defineMap.get("one7"), Double.valueOf(0.0d)).doubleValue();
        double one8 = Convert.toDouble(defineMap.get("one8"), Double.valueOf(0.0d)).doubleValue();
        double one9 = Convert.toDouble(defineMap.get("one9"), Double.valueOf(0.0d)).doubleValue();
        double one10 = Convert.toDouble(defineMap.get("one10"), Double.valueOf(0.0d)).doubleValue();
        double one11 = Convert.toDouble(defineMap.get("one11"), Double.valueOf(0.0d)).doubleValue();
        double one12 = Convert.toDouble(defineMap.get("one12"), Double.valueOf(0.0d)).doubleValue();
        double one13 = Convert.toDouble(defineMap.get("one13"), Double.valueOf(0.0d)).doubleValue();
        double one14 = Convert.toDouble(defineMap.get("one14"), Double.valueOf(0.0d)).doubleValue();
        double one15 = Convert.toDouble(defineMap.get("one15"), Double.valueOf(0.0d)).doubleValue();
        double one16 = Convert.toDouble(defineMap.get("one16"), Double.valueOf(0.0d)).doubleValue();
        double one17 = Convert.toDouble(defineMap.get("one17"), Double.valueOf(0.0d)).doubleValue();
        double one18 = Convert.toDouble(defineMap.get("one18"), Double.valueOf(0.0d)).doubleValue();
        double one19 = Convert.toDouble(defineMap.get("one19"), Double.valueOf(0.0d)).doubleValue();
        double one20 = Convert.toDouble(defineMap.get("one20"), Double.valueOf(0.0d)).doubleValue();
        double one21 = Convert.toDouble(defineMap.get("one21"), Double.valueOf(0.0d)).doubleValue();
        double one22 = Convert.toDouble(defineMap.get("one22"), Double.valueOf(0.0d)).doubleValue();
        double one23 = Convert.toDouble(defineMap.get("one23"), Double.valueOf(0.0d)).doubleValue();
        double one24 = Convert.toDouble(defineMap.get("one24"), Double.valueOf(0.0d)).doubleValue();
        double one25 = Convert.toDouble(defineMap.get("one25"), Double.valueOf(0.0d)).doubleValue();
        double one26 = Convert.toDouble(defineMap.get("one26"), Double.valueOf(0.0d)).doubleValue();
        double deduction = Double.parseDouble(defineMap.get("deduction").toString());
        double fullscore = Double.parseDouble(defineMap.get("fullScore").toString());
        String hasError = defineMap.get("hasErrorSection").toString();
        double inspectionlevel = Double.parseDouble(defineMap.get("inspectionlevel").toString());
        String multiple = defineMap.get("multiple").toString();
        String answer_define = defineMap.get("answer").toString();
        if (multiple.equals("0")) {
            double[] temp_one = {0.0d, one1, one2, one3, one4, one5, one6, one7, one8, one9, one10, one11, one12, one13, one14, one15, one16, one17, one18, one19, one20, one21, one22, one23, one24, one25, one26};
            int rightNum = 0;
            int errorNum = 0;
            if (answer.length() <= 1) {
                for (int i = 0; i < answer.length(); i++) {
                    if (answer_define.indexOf(answer.charAt(i)) != -1) {
                        rightNum++;
                    } else {
                        errorNum++;
                    }
                }
            }
            score = temp_one[rightNum];
            if (errorNum > 0) {
                score = 0.0d;
            }
        } else if (inspectionlevel == 0.0d) {
            double[] temp_one2 = {0.0d, one1, one2, one3, one4, one5, one6, one7, one8, one9, one10, one11, one12, one13, one14, one15, one16, one17, one18, one19, one20, one21, one22, one23, one24, one25, one26};
            int rightNum2 = 0;
            int errorNum2 = 0;
            if (answer.length() <= lengout) {
                for (int i2 = 0; i2 < answer.length(); i2++) {
                    if (answer_define.indexOf(answer.charAt(i2)) != -1) {
                        rightNum2++;
                    } else {
                        errorNum2++;
                    }
                }
            }
            score = temp_one2[rightNum2] + (deduction * errorNum2);
            if (score < 0.0d) {
                score = 0.0d;
            }
            if (score > fullscore) {
                score = fullscore;
            }
            if (errorNum2 > 0 && hasError.equals("0")) {
                score = 0.0d;
            }
        } else {
            Map<Character, Double> map = new HashMap<>();
            map.put('A', Double.valueOf(one1));
            map.put('B', Double.valueOf(one2));
            map.put('C', Double.valueOf(one3));
            map.put('D', Double.valueOf(one4));
            map.put('E', Double.valueOf(one5));
            map.put('F', Double.valueOf(one6));
            map.put('G', Double.valueOf(one7));
            map.put('H', Double.valueOf(one8));
            map.put('I', Double.valueOf(one9));
            map.put('J', Double.valueOf(one10));
            map.put('K', Double.valueOf(one11));
            map.put('L', Double.valueOf(one12));
            map.put('M', Double.valueOf(one13));
            map.put('N', Double.valueOf(one14));
            map.put('O', Double.valueOf(one15));
            map.put('P', Double.valueOf(one16));
            map.put('Q', Double.valueOf(one17));
            map.put('R', Double.valueOf(one18));
            map.put('S', Double.valueOf(one19));
            map.put('T', Double.valueOf(one20));
            map.put('U', Double.valueOf(one21));
            map.put('V', Double.valueOf(one22));
            map.put('W', Double.valueOf(one23));
            map.put('X', Double.valueOf(one24));
            map.put('Y', Double.valueOf(one25));
            map.put('Z', Double.valueOf(one26));
            int errorNum3 = 0;
            if (answer.length() <= lengout) {
                for (int i3 = 0; i3 < answer.length(); i3++) {
                    char a = answer.charAt(i3);
                    score += map.get(Character.valueOf(a)).doubleValue();
                    if (map.get(Character.valueOf(a)).doubleValue() <= 0.0d) {
                        errorNum3++;
                    }
                }
            }
            if (score < 0.0d) {
                score = 0.0d;
            }
            if (score > fullscore) {
                score = fullscore;
            }
            if (errorNum3 > 0 && hasError.equals("0")) {
                score = 0.0d;
            }
        }
        return score;
    }

    public static String getRandom(int len, int type) throws Exception {
        String random = "";
        for (int i = 0; i < len; i++) {
            random = random + ((int) (Math.random() * 10.0d));
        }
        return random;
    }

    public static <T> T clone(T t, boolean z) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new ObjectOutputStream(byteArrayOutputStream).writeObject(t);
            return (T) new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).readObject();
        } catch (Exception e) {
            if (z) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    public static <T> T clone(T t) {
        return (T) clone(t, false);
    }
}
