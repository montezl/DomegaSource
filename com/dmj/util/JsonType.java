package com.dmj.util;

import com.alibaba.fastjson.TypeReference;
import com.dmj.cs.bean.ClipRect;
import com.dmj.cs.bean.ScoringPointRect;
import com.dmj.domain.configration.ConfigField;
import java.util.List;
import java.util.Map;

/* loaded from: JsonType.class */
public class JsonType {
    public static final TypeReference<List<Map<String, Object>>> List2Map1_StringObject = new 1();
    public static final TypeReference<List<Map<String, String>>> List2Map1_StringString = new 2();
    public static final TypeReference<List<Map<String, Integer>>> List2Map1_StringInteger = new 3();
    public static final TypeReference<Map<String, Object>> Map1_StringObject = new 4();
    public static final TypeReference<Map<String, String>> Map1_StringString = new 5();
    public static final TypeReference<Map<String, Integer>> Map1_StringInteger = new 6();
    public static final TypeReference<Map<String, ClipRect>> Map1_StringClipRect = new 7();
    public static final TypeReference<List<ScoringPointRect>> List2Bean1_ScoringPointRect = new 8();
    public static final TypeReference<List<ConfigField>> List2Bean1_ConfigField = new 9();
    public static final TypeReference<String[]> Array1_String = new 10();
    public static final TypeReference<List<String>> List1_String = new 11();
    public static final TypeReference<List<Map<String, Map<String, Object>>>> List3Map2_StringMap1_StringObject = new 12();
    public static final TypeReference<List<List<Map<String, Object>>>> List3List2Map1_StringObject = new 13();
}
