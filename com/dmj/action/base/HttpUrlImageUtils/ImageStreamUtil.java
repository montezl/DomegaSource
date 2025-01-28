package com.dmj.action.base.HttpUrlImageUtils;

import com.dmj.util.FileUtil;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* loaded from: ImageStreamUtil.class */
public class ImageStreamUtil {
    public static byte[] getImageStream(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlCon.getInputStream();
            byte[] aa = FileUtil.toByteArray(inputStream);
            return aa;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
