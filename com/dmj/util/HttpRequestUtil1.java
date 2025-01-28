package com.dmj.util;

import com.alibaba.fastjson.JSONObject;
import com.dmj.action.base.BaseAction;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.zht.db.ServiceFactory;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

/* loaded from: HttpRequestUtil1.class */
public class HttpRequestUtil1 extends BaseAction {
    private SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());

    public boolean startVisit(String urlString, String path) {
        StringBuffer document = new StringBuffer();
        try {
            String visitURI = this.systemService.getImageServerUri() + urlString + "?path=" + path;
            URL url = new URL(visitURI);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            while (true) {
                String Result = reader.readLine();
                if (Result == null) {
                    break;
                }
                document.append(Result);
            }
            document.toString();
            int code = urlCon.getResponseCode();
            if (code != 200) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override // com.dmj.action.base.BaseAction
    public byte[] getImageStream(String urlString) {
        try {
            String visitURI = this.systemService.getImageServerUri() + urlString;
            URL url = new URL(visitURI);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlCon.getInputStream();
            byte[] aa = FileUtil.toByteArray(inputStream);
            return aa;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getImageStream1(String urlString) {
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

    public boolean delOneFile(String para) {
        return startVisit("imageAction!deleteOneFile.action", para);
    }

    public boolean delFolder(String para) {
        return startVisit("imageAction!deleteFolder.action=", para);
    }

    public boolean copyFolder(String para) {
        return startVisitCopy("imageAction!copyFolder.action", para);
    }

    public boolean startVisitCopy(String urlString, String path) {
        StringBuffer document = new StringBuffer();
        try {
            String visitURI = this.systemService.getImageServerUri() + urlString + "?" + path;
            URL url = new URL(visitURI);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            while (true) {
                String Result = reader.readLine();
                if (Result == null) {
                    break;
                }
                document.append(Result);
            }
            document.toString();
            int code = urlCon.getResponseCode();
            if (code != 200) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public byte[] dispatherRequest(HttpServletRequest req) {
        String vmPath = req.getContextPath();
        String uri = req.getRequestURI();
        String ur = uri.replace(vmPath, "");
        Enumeration names = req.getParameterNames();
        StringBuffer sb = new StringBuffer("?");
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            sb.append(name);
            sb.append("=");
            sb.append(req.getParameterValues(name)[0]);
            sb.append("&");
        }
        String visitURI = ur + sb.toString();
        return getImageStream(visitURI);
    }

    public static byte[] InputStreamToByte(InputStream iStrm) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        while (true) {
            int ch = iStrm.read();
            if (ch != -1) {
                bytestream.write(ch);
            } else {
                byte[] imgdata = bytestream.toByteArray();
                bytestream.close();
                return imgdata;
            }
        }
    }

    public static JSONObject getRequestByAPI(String POST_URL, String parm) {
        PrintWriter out = null;
        try {
            try {
                URL realUrl = new URL(POST_URL);
                URLConnection conn = realUrl.openConnection();
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
                out.print(parm);
                out.flush();
                InputStream is = conn.getInputStream();
                byte[] aa = InputStreamToByte(is);
                String t = new String(aa, "UTF-8");
                JSONObject json = JSONObject.parseObject(t);
                if (out != null) {
                    out.close();
                }
                return json;
            } catch (Exception e) {
                e.printStackTrace();
                if (out != null) {
                    out.close();
                    return null;
                }
                return null;
            }
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    public static JSONObject getRequestByAPI2(String POST_URL, String parm, String[] header, String type) {
        InputStream is;
        PrintWriter out = null;
        try {
            try {
                URL realUrl = new URL(POST_URL);
                URLConnection conn = realUrl.openConnection();
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                String symbole = "";
                if (type.equals("1")) {
                    symbole = "Basic ";
                } else if (type.equals("2")) {
                    symbole = "Bearer ";
                }
                conn.setRequestProperty(header[0], symbole + header[1]);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
                out.print(parm);
                out.flush();
                if (((HttpURLConnection) conn).getResponseCode() >= 400) {
                    is = ((HttpURLConnection) conn).getErrorStream();
                } else {
                    is = conn.getInputStream();
                }
                byte[] aa = InputStreamToByte(is);
                String t = new String(aa, "UTF-8");
                JSONObject json = JSONObject.parseObject(t);
                if (out != null) {
                    out.close();
                }
                return json;
            } catch (Exception e) {
                e.printStackTrace();
                if (out != null) {
                    out.close();
                    return null;
                }
                return null;
            }
        } catch (Throwable th) {
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    public static void main(String[] args) {
        getRequestByAPI("http://192.168.1.25:26603/edei_q/loginAction!loginExecute4.action", "");
    }
}
