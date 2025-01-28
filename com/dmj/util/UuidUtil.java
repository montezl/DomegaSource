package com.dmj.util;

import com.zht.db.DbUtils;

/* loaded from: UuidUtil.class */
public class UuidUtil {
    public static long getUuid() {
        return DbUtils.getUuid();
    }
}
