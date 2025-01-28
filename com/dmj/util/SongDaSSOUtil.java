package com.dmj.util;

import com.alibaba.fastjson.JSON;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/* loaded from: SongDaSSOUtil.class */
public class SongDaSSOUtil {
    public String doPostParam(String url, Map<String, String> map) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            List<BasicNameValuePair> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            HttpEntity ent = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(ent);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseContent = EntityUtils.toString(entity, "UTF-8");
            response.close();
            httpClient.close();
            return responseContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String doPostJson(String url, Map params) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(JSON.toJSONString(params)));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseContent = EntityUtils.toString(entity, "UTF-8");
            response.close();
            httpClient.close();
            return responseContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String sign(Map<String, String> paramValues, String secret) {
        StringBuilder sb = new StringBuilder();
        List<String> paramNames = new ArrayList<>(paramValues.size());
        paramNames.addAll(paramValues.keySet());
        Collections.sort(paramNames);
        sb.append(secret);
        for (String paramName : paramNames) {
            sb.append(paramName).append(paramValues.get(paramName));
        }
        String sign = getMD5(sb.toString().toLowerCase());
        return sign;
    }

    private String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(255 & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(255 & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(255 & encryption[i]));
                }
            }
            return strBuf.toString().toUpperCase();
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (NoSuchAlgorithmException e2) {
            return "";
        }
    }
}
