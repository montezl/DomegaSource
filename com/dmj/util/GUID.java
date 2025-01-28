package com.dmj.util;

/* loaded from: GUID.class */
public class GUID {
    public static long getGUID() {
        return UuidUtil.getUuid();
    }

    public static String getGUIDStr() {
        return String.valueOf(getGUID());
    }
}
