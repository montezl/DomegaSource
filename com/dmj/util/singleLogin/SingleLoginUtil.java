package com.dmj.util.singleLogin;

import cn.hutool.core.convert.Convert;
import cn.hutool.setting.Setting;
import com.dmj.util.schedule.task.StartLoader;

/* loaded from: SingleLoginUtil.class */
public class SingleLoginUtil {
    public static Setting singleLoginSetting = StartLoader.SingleLoginSetting;

    public static String getDefaultKey() {
        String defaultKey = singleLoginSetting.get("default", "key");
        return defaultKey;
    }

    public static boolean showThreeLogin() {
        String showThreeLogin = singleLoginSetting.get("default", "showThreeLogin");
        return Convert.toBool(showThreeLogin, false).booleanValue();
    }

    public static String getBaseLoginUrl() {
        String defaultKey = getDefaultKey();
        String baseLoginUrl = singleLoginSetting.get(defaultKey, "baseLoginUrl");
        return baseLoginUrl;
    }

    public static String getLoginUrl() {
        String defaultKey = getDefaultKey();
        String loginUrl = singleLoginSetting.get(defaultKey, "loginUrl");
        return loginUrl;
    }

    public static String getLogoutUrl() {
        String defaultKey = getDefaultKey();
        String logoutUrl = singleLoginSetting.get(defaultKey, "logoutUrl");
        return logoutUrl;
    }
}
