package com.dmj.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import javax.servlet.ServletContext;

/* loaded from: OnOffUtil.class */
public class OnOffUtil {
    public static String getOpenOcs(String systemId, String asUrl) {
        String openOcs = "1";
        String uid = GUID.getGUIDStr();
        String _url = StrUtil.format("{}/app/free/{}/{}", new Object[]{asUrl, systemId, uid});
        try {
            String res = HttpRequest.get(_url).timeout(30000).execute().body();
            if (res.equals(DESUtils.md5Free(uid))) {
                openOcs = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openOcs;
    }

    public static void cacheOpenOcsInfo(ServletContext context, String asUrl, String systemId) {
        String openOcs = getOpenOcs(systemId, asUrl);
        context.setAttribute("openOcs", openOcs);
        StaticClassResources.EdeiInfo.setFree("0".equals(openOcs));
    }

    public static boolean getAppEnabled(String asUrl, String systemId, String body) {
        String _url = StrUtil.format("{}/app/enable/{}/{}", new Object[]{asUrl, systemId, body});
        try {
            String res = HttpRequest.get(_url).timeout(5000).execute().body();
            return res.equals("1");
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void cacheAppEnabledInfo(ServletContext context, String asUrl, String systemId, String body) {
        boolean z = -1;
        switch (body.hashCode()) {
            case -1879145925:
                if (body.equals("student")) {
                    z = false;
                    break;
                }
                break;
            case -1439577118:
                if (body.equals("teacher")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case Const.clipError_failure /* 0 */:
                boolean studentAppEnabled = getAppEnabled(asUrl, systemId, "student");
                context.setAttribute("studentAppEnabled", Boolean.valueOf(studentAppEnabled));
                StaticClassResources.EdeiInfo.setStudentAppEnabled(studentAppEnabled);
                return;
            case true:
                boolean teacherAppEnabled = getAppEnabled(asUrl, systemId, "teacher");
                context.setAttribute("teacherAppEnabled", Boolean.valueOf(teacherAppEnabled));
                StaticClassResources.EdeiInfo.setTeacherAppEnabled(teacherAppEnabled);
                return;
            default:
                return;
        }
    }
}
