package com.dmj.util.app;

import com.dmj.util.Const;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: AppFilter.class */
public class AppFilter implements Filter {
    private static String appLoginUrl = "appLogin";
    private static String appSMSLoginUrl = "appSMSLogin";
    private static String appSidLoginUrl = "appSidLogin";
    private static String appFreePwdUrl = "appFreePwdLogin";
    private static String baseUrl = "appIndex";
    private static String[] verifiableUrl;
    private static final long expirationTime = 300000;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        ServletContext context = request.getServletContext();
        String contextPath = req.getContextPath();
        String url = req.getRequestURI();
        String requestSource = req.getParameter("requestSource");
        if ("parentApp".equals(requestSource)) {
            if (!((Boolean) request.getServletContext().getAttribute("studentAppEnabled")).booleanValue() && url.indexOf("app!getAppUpdateInfo.action") == -1) {
                if (isAjax(req)) {
                    res.getWriter().write("App服务已关闭");
                    res.setStatus(401);
                    return;
                } else {
                    res.sendRedirect(contextPath + "/appIndex!serverClosed.action");
                    return;
                }
            }
            if (url.indexOf(appLoginUrl) != -1 || url.indexOf(appSMSLoginUrl) != -1 || url.indexOf(appSidLoginUrl) != -1 || url.indexOf(appFreePwdUrl) != -1) {
                AppUserInfo userinfo = new AppUserInfo();
                userinfo.setExpirationTime(expirationTime);
                userinfo.setLastTime(new Date().getTime());
                String username = req.getParameter("username");
                if (!StringUtils.isNullOrEmpty(username)) {
                    userinfo.setUserName(AesCbc.decrypt(username));
                    request.setAttribute("username", userinfo.getUserName());
                }
                String password = req.getParameter("password");
                if (!StringUtils.isNullOrEmpty(password)) {
                    userinfo.setPassword(AesCbc.decrypt(password));
                    request.setAttribute("password", userinfo.getPassword());
                }
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                String token = uuid + userinfo.getUserName();
                userinfo.setToken(token);
                request.setAttribute("userinfo", userinfo);
                request.setAttribute("AccessToken", token);
            } else if (verify(url)) {
                String username2 = req.getParameter("username");
                if (!StringUtils.isNullOrEmpty(username2)) {
                    request.setAttribute("username", AesCbc.decrypt(username2));
                }
                String password2 = req.getParameter("password");
                if (!StringUtils.isNullOrEmpty(password2)) {
                    request.setAttribute("password", AesCbc.decrypt(password2));
                }
                String userType = req.getParameter("userType");
                req.setAttribute("userType", userType);
            } else if (url.indexOf(baseUrl) != -1 && !url.equals(contextPath + "/appIndex!error.action")) {
                String token2 = req.getParameter("AccessToken");
                ConcurrentHashMap<String, AppUserInfo> map = (ConcurrentHashMap) context.getAttribute("AccessToken");
                if (map != null && map.containsKey(token2)) {
                    AppUserInfo info = map.get(token2);
                    req.setAttribute("userinfo", info);
                    if (StringUtils.isNullOrEmpty(token2) || (info != null && token2.indexOf(info.getUserName()) == -1)) {
                        res.sendRedirect(contextPath + "/appIndex!error.action");
                        return;
                    }
                } else {
                    res.sendRedirect(contextPath + "/appIndex!error.action");
                    return;
                }
            }
        }
        chain.doFilter(req, res);
    }

    boolean isAjax(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString());
    }

    public void init(FilterConfig arg0) throws ServletException {
        baseUrl = arg0.getInitParameter("baseUrl");
        appLoginUrl = arg0.getInitParameter("appLoginUrl");
        appSMSLoginUrl = arg0.getInitParameter("appSMSLoginUrl");
        appSidLoginUrl = arg0.getInitParameter("appSidLoginUrl");
        appFreePwdUrl = arg0.getInitParameter("appFreePwdLoginUrl");
        verifiableUrl = arg0.getInitParameter("verifiableUrl").split(Const.STRING_SEPERATOR);
    }

    private boolean verify(String url) {
        for (int i = 0; i < verifiableUrl.length; i++) {
            if (url.indexOf(verifiableUrl[i]) != -1) {
                return true;
            }
        }
        return false;
    }

    private void write(String message, HttpServletResponse res) {
        try {
            res.getWriter().write(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
    }
}
