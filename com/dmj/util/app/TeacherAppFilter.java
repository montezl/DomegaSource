package com.dmj.util.app;

import com.dmj.util.Const;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* loaded from: TeacherAppFilter.class */
public class TeacherAppFilter implements Filter {
    private static String teacherAppBaseUrl = "teacherApp";
    private static String[] teacherAppVerifiableUrl;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession session;
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (req.getMethod().equals("OPTIONS")) {
            res.setStatus(200);
            return;
        }
        String url = req.getRequestURI();
        req.getContextPath();
        String requestSource = req.getParameter("requestSource");
        if ("teacherApp".equals(requestSource) && !verify(url) && (null == (session = req.getSession()) || null == session.getAttribute(Const.LOGIN_USER))) {
            res.setStatus(401);
            res.getWriter().write("没有权限");
        } else {
            chain.doFilter(req, res);
        }
    }

    boolean isAjax(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString());
    }

    public void init(FilterConfig arg0) throws ServletException {
        teacherAppBaseUrl = arg0.getInitParameter("teacherAppBaseUrl");
        teacherAppVerifiableUrl = arg0.getInitParameter("teacherAppVerifiableUrl").split(Const.STRING_SEPERATOR);
    }

    private boolean verify(String url) {
        for (int i = 0; i < teacherAppVerifiableUrl.length; i++) {
            if (url.indexOf(teacherAppVerifiableUrl[i]) != -1) {
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
