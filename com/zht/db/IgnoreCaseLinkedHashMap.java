package com.zht.db;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/* loaded from: IgnoreCaseLinkedHashMap.class */
public class IgnoreCaseLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    private final Map<String, String> lowerCaseMap = new HashMap();
    private static final long serialVersionUID = -2848100435296897392L;

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        Object realKey = key;
        if (key instanceof String) {
            realKey = this.lowerCaseMap.get(((String) key).toLowerCase(Locale.ENGLISH));
        }
        return super.containsKey(realKey);
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        Object obj2 = obj;
        if (obj instanceof String) {
            obj2 = this.lowerCaseMap.get(((String) obj).toLowerCase(Locale.ENGLISH));
        }
        return (V) super.get(obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        String str = k;
        if (k instanceof String) {
            String str2 = (String) k;
            str = this.lowerCaseMap.put(str2.toLowerCase(Locale.ENGLISH), str2);
        }
        V v2 = (V) super.remove(str);
        super.put(k, v);
        return v2;
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            put(key, value);
        }
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        Object obj2 = obj;
        if (obj instanceof String) {
            obj2 = this.lowerCaseMap.remove(obj.toString().toLowerCase(Locale.ENGLISH));
        }
        return (V) super.remove(obj2);
    }
}
