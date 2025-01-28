package com.zht.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import java.math.BigDecimal;

/* loaded from: Jsons.class */
public class Jsons {
    static final SerializeConfig serializeConfig = new SerializeConfig();

    static {
        serializeConfig.put(Number.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        serializeConfig.put(BigDecimal.class, ToStringSerializer.instance);
    }

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, serializeConfig, new SerializerFeature[]{SerializerFeature.PrettyFormat});
    }

    public static JSONObject parseObject(String text) {
        return JSON.parseObject(text);
    }

    public static <T> T parseObject(String str, TypeReference<T> typeReference) {
        return (T) JSON.parseObject(str, typeReference, new Feature[0]);
    }
}
