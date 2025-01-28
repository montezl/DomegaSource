package com.dmj.util.singleLogin;

import cn.hutool.setting.Setting;
import com.dmj.util.schedule.task.StartLoader;

/* loaded from: ZkhySingleLoginUtil.class */
public class ZkhySingleLoginUtil {
    public static Setting singleLoginSetting = StartLoader.SingleLoginSetting;
    public static String vendor = "zkhy";

    public static String getServer() {
        return singleLoginSetting.get(vendor, "casServer");
    }

    public static String getClintId() {
        return singleLoginSetting.get(vendor, "client_id");
    }

    public static String getClintSecret() {
        return singleLoginSetting.get(vendor, "client_secret");
    }

    public static boolean pcShow() {
        return "true".equals(singleLoginSetting.get(vendor, "pcShow").toLowerCase());
    }

    public static boolean appShow() {
        return "true".equals(singleLoginSetting.get(vendor, "appShow").toLowerCase());
    }
}
