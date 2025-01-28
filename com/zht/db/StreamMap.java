package com.zht.db;

import java.util.HashMap;

/* loaded from: StreamMap.class */
public class StreamMap extends HashMap {
    public static StreamMap create() {
        return new StreamMap();
    }

    public StreamMap put(String key, Object value) {
        super.put((StreamMap) key, (String) value);
        return this;
    }

    public StreamMap put(String key, Object value, boolean notPutExpression) {
        if (notPutExpression) {
            return this;
        }
        return put(key, value);
    }

    public static StreamMap init(String key, Object value) {
        return create().put(key, value);
    }

    public static StreamMap init(String key1, Object value1, String key2, Object value2) {
        return create().put(key1, value1).put(key2, value2);
    }

    public static StreamMap init(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        return create().put(key1, value1).put(key2, value2).put(key3, value3);
    }

    public static StreamMap init(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
        return create().put(key1, value1).put(key2, value2).put(key3, value3).put(key4, value4);
    }
}
