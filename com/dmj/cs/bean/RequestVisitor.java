package com.dmj.cs.bean;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.dmj.cs.util.LiteDeviceResolver;
import com.dmj.util.Const;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/* loaded from: RequestVisitor.class */
public class RequestVisitor {
    private String ua;
    private String remoteAddr;
    private String localAddr;
    private String lastUrl;
    private String sessionId;
    private String lastVisitorTime;
    private String device;
    private String expirationTime;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getUa() {
        return this.ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getLocalAddr() {
        return this.localAddr;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public String getLastUrl() {
        return this.lastUrl;
    }

    public void setLastUrl(String lastUrl) {
        this.lastUrl = lastUrl;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLastVisitorTime() {
        return this.lastVisitorTime;
    }

    public void setLastVisitorTime(String lastVisitorTime) {
        this.lastVisitorTime = lastVisitorTime;
    }

    public String getDevice() {
        return this.device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public static void write(HttpServletRequest req) {
        HttpSession session = req.getSession();
        RequestVisitor visitor = (RequestVisitor) session.getAttribute("RequestVisitorKey");
        if (visitor == null) {
            visitor = new RequestVisitor();
            session.setAttribute("RequestVisitorKey", visitor);
        }
        String ua = req.getHeader("User-Agent");
        String remoteAddr = getIpAddr(req);
        String localAddr = req.getLocalAddr();
        String lastUrl = req.getRequestURI();
        String sessionId = session.getId();
        String device = new LiteDeviceResolver().resolveDevice(req).getDeviceInfo();
        DateTime time = DateTime.now();
        visitor.setLastVisitorTime(DateTime.now().toString());
        visitor.setExpirationTime(DateUtil.offsetSecond(time, session.getMaxInactiveInterval()).toString());
        visitor.setUa(ua);
        visitor.setRemoteAddr(remoteAddr);
        visitor.setLocalAddr(localAddr);
        visitor.setLastUrl(lastUrl);
        visitor.setSessionId(sessionId);
        visitor.setDevice(device);
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        if (ipAddress != null && ipAddress.length() > 15 && ipAddress.indexOf(Const.STRING_SEPERATOR) > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(Const.STRING_SEPERATOR));
        }
        return ipAddress;
    }
}
