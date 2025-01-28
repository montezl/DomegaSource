package com.dmj.util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: EncodingFilter.class */
public class EncodingFilter extends HttpServlet implements Filter {
    private static final long serialVersionUID = 1;
    private String encoding = "UTF-8";

    public void init(FilterConfig config) throws ServletException {
        this.encoding = config.getInitParameter("encoding");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        response.setCharacterEncoding(this.encoding);
        request.setCharacterEncoding(this.encoding);
        CorsUtil.cors(req, res);
        chain.doFilter(req, res);
    }

    public void destroy() {
        super.destroy();
    }
}
