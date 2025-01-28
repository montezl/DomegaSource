package com.dmj.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dmj.util.app.EdeiInfo;
import com.dmj.util.msg.RspMsg;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/* loaded from: WebUtil.class */
public class WebUtil {
    public static RspMsg sendSmsByPost(String url, String postContent) {
        StringBuilder sb;
        int httpRspCode;
        HttpURLConnection httpURLConnection = null;
        try {
            try {
                URL httpURL = new URL(url);
                httpURLConnection = (HttpURLConnection) httpURL.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                httpURLConnection.addRequestProperty("Content-Type", "application/json");
                httpURLConnection.connect();
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(postContent.getBytes("UTF-8"));
                os.flush();
                sb = new StringBuilder();
                httpRspCode = httpURLConnection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
                httpURLConnection.disconnect();
            }
            if (httpRspCode != 200) {
                httpURLConnection.disconnect();
                return RspMsg.error("发送验证码异常，请重试。");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    br.close();
                    RspMsg msg = (RspMsg) JSON.parseObject(sb.toString(), RspMsg.class);
                    httpURLConnection.disconnect();
                    return msg;
                }
                sb.append(line);
            }
        } catch (Throwable th) {
            httpURLConnection.disconnect();
            throw th;
        }
    }

    public static int downloadAppHtml(String url, String dstPath) {
        try {
            File zipFile = cn.hutool.core.io.FileUtil.file(cn.hutool.core.io.FileUtil.getTmpDirPath() + GUID.getGUIDStr() + ".zip");
            long size = HttpUtil.downloadFile(url, zipFile);
            if (size == 0) {
                return 0;
            }
            ZipUtil.unzip(zipFile);
            cn.hutool.core.io.FileUtil.clean(dstPath);
            String zipFileName = zipFile.getName();
            String srcDirPath = zipFile.getParent() + "/" + zipFileName.substring(0, zipFileName.lastIndexOf("."));
            File srcDir = new File(srcDirPath);
            for (File f : srcDir.listFiles()) {
                cn.hutool.core.io.FileUtil.move(f, new File(dstPath), true);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int[] updateAppHtml(EdeiInfo edeiInfo) {
        String teacherAppHtmlUrl = StrUtil.format("{}/app/downloadHtmlZip/teacher/{}/{}", new Object[]{edeiInfo.getAsUrl(), edeiInfo.getSystemId(), edeiInfo.getEdeiVersion()});
        int teacherStatus = downloadAppHtml(teacherAppHtmlUrl, edeiInfo.getProjectPath() + "/app/teacher");
        String studentAppHtmlUrl = StrUtil.format("{}/app/downloadHtmlZip/student/{}/{}", new Object[]{edeiInfo.getAsUrl(), edeiInfo.getSystemId(), edeiInfo.getEdeiVersion()});
        int studentStatus = downloadAppHtml(studentAppHtmlUrl, edeiInfo.getProjectPath() + "/app/student");
        return new int[]{studentStatus, teacherStatus};
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", "17610755825");
        map.put("type", "教师");
        map.put("templateNum", "4");
        map.put("server", "http://192.168.1.10:18080/edei");
        sendSmsByPost("http://192.168.1.10:18080/appServer/sms/get", JSONObject.toJSONString(map));
    }
}
