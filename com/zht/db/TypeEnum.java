package com.zht.db;

import java.lang.reflect.Type;

/* loaded from: TypeEnum.class */
public enum TypeEnum {
    StringObject(String.class, Object.class),
    StringString(String.class, String.class),
    StringInteger(String.class, Integer.class),
    IntegerString(Integer.class, String.class),
    ObjectLong(Object.class, Long.class);

    private Type k;
    private Type v;

    TypeEnum(Type k, Type v) {
        this.k = k;
        this.v = v;
    }

    public Type getK() {
        return this.k;
    }

    public Type getV() {
        return this.v;
    }
}
