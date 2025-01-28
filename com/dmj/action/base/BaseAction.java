package com.dmj.action.base;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.dmj.action.base.HttpUrlImageUtils.ImageStreamUtil;
import com.dmj.util.Const;
import com.dmj.util.config.Configuration;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

/* loaded from: BaseAction.class */
public class BaseAction extends ActionSupport implements SessionAware, ServletRequestAware, ServletResponseAware, BaseUtilInterface {
    private static final long serialVersionUID = 1;
    public ActionContext context = ActionContext.getContext();
    public HttpServletRequest request;
    public HttpServletResponse response;
    public SessionMap session;
    public PrintWriter out;
    public String useImageServer;
    public String newImageServer;
    public String picUrl;
    public String base;

    public void setSession(Map map) {
        this.session = (SessionMap) map;
    }

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
        this.base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        Object newImageServerObj = request.getSession().getServletContext().getAttribute(Const.newImageServer);
        if (newImageServerObj != null) {
            this.newImageServer = newImageServerObj.toString();
        }
        Object Ser = Configuration.getInstance().getUseImageServer();
        if (Ser != null) {
            this.useImageServer = Ser.toString();
        }
        Object Pic = request.getSession().getServletContext().getAttribute(Const.IMAGEACCESSPATH);
        if (Pic != null) {
            this.picUrl = Pic.toString();
        }
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
        setOut();
    }

    public void setOut() {
        try {
            this.response.setContentType("text/html;charset=UTF-8");
            this.out = this.response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServletOutputStream getOutStream() {
        try {
            this.response.reset();
            return this.response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] dispatherRequest() {
        String vmPath = this.request.getContextPath();
        String uri = this.request.getRequestURI();
        String ur = uri.replace(vmPath, "");
        Enumeration names = this.request.getParameterNames();
        StringBuffer sb = new StringBuffer("?");
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            sb.append(name);
            sb.append("=");
            sb.append(this.request.getParameterValues(name)[0]);
            sb.append("&");
        }
        String url = "";
        Object Pic = this.request.getSession().getServletContext().getAttribute(Const.IMAGEACCESSPATH_D);
        if (Pic != null) {
            url = Pic.toString();
        }
        String visitURI = url + ur + sb.toString();
        return getImageStream(visitURI);
    }

    public byte[] getImageStream(String urlString) {
        return ImageStreamUtil.getImageStream(urlString);
    }

    public boolean Skip() {
        String path = this.request.getContextPath();
        String basePath = this.request.getScheme() + "://" + this.request.getServerName() + ":" + this.request.getServerPort() + path + "/";
        return (null == this.useImageServer || !this.useImageServer.equals("0")) && this.picUrl != null && this.picUrl.indexOf(basePath) == -1;
    }

    public boolean needForward() {
        if (this.useImageServer.equals("2")) {
            return true;
        }
        if (this.useImageServer.equals("1") && !isPicServer()) {
            return true;
        }
        return false;
    }

    public boolean isPicServer() {
        String ip = this.request.getServerName();
        return this.newImageServer.indexOf(ip) != -1;
    }

    public Rsp forward() {
        Map<String, String[]> params = this.request.getParameterMap();
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String[]> en : params.entrySet()) {
            map.put(en.getKey(), en.getValue());
        }
        String url = this.request.getServletPath();
        HttpResponse rsp = HttpUtil.createPost(this.newImageServer + url).form(map).execute();
        return new Rsp(rsp);
    }

    public String getAttribute(String arg0) {
        return String.valueOf(this.request.getAttribute(arg0));
    }

    public void setParameter(String arg0, String value) {
        this.request.setAttribute(arg0, value);
    }

    public Object get(String key) {
        return this.session.get(key);
    }
}
