package com.dmj.util;

import com.dmj.action.base.BaseAction;
import com.dmj.auth.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletContext;
import net.sf.json.JSONObject;
import org.apache.struts2.ServletActionContext;

/* loaded from: Conffig.class */
public class Conffig extends BaseAction {
    private String typeName;

    public void gettypeValue() throws Exception {
        new CommonUtil();
        String src2 = CommonUtil.getRootPath(this.request);
        String filePath = src2 + "WEB-INF/classes/conff.properties";
        Properties easprop = new Properties();
        FileInputStream fis = new FileInputStream(filePath);
        easprop.load(fis);
        this.out.write((String) easprop.get(this.typeName));
    }

    public void getConfigFilePath() {
        String type = this.request.getParameter("type");
        if (null == type || type.equals("")) {
            return;
        }
        String pathString = "";
        if (null == type || type.equals("")) {
            pathString = "";
        } else if (type.equals("0")) {
            pathString = CommonUtil.getRootPath(this.request) + "/WEB-INF/classes/druid.properties";
        } else if (type.equals("1")) {
            pathString = CommonUtil.getRootPath(this.request) + "/WEB-INF/classes/conff.properties";
        }
        try {
            Runtime.getRuntime().exec("notepad.exe " + pathString);
            this.out.write(pathString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String addPara() {
        return "addPara";
    }

    public void readPara() {
        String json;
        FileInputStream fis = null;
        try {
            try {
                new CommonUtil();
                String src2 = CommonUtil.getRootPath(this.request);
                String filePath = src2 + "WEB-INF/classes/conff.properties";
                NewProperties newProperties = new NewProperties();
                fis = new FileInputStream(filePath);
                newProperties.load(fis);
                Set<?> set = newProperties.keySet();
                StringBuffer buffer = new StringBuffer();
                buffer.append("[");
                for (Object obj : set) {
                    buffer.append("{\"" + obj.toString() + "\":\"" + newProperties.getProperty(obj.toString()) + "\"},");
                }
                json = buffer.toString();
                if (json != null) {
                    json = json.substring(0, json.length() - 1) + "]";
                }
                newProperties.clear();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e2) {
                json = "F";
                this.out.write(json);
                e2.printStackTrace();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }
            this.out.write(json);
        } catch (Throwable th) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                    throw th;
                }
            }
            throw th;
        }
    }

    public void updatePara() {
        String[] params;
        new Base64();
        String param = this.request.getParameter("param");
        FileOutputStream fot = null;
        FileInputStream fis = null;
        try {
            try {
                new CommonUtil();
                String src2 = CommonUtil.getRootPath(this.request);
                String filePath = src2 + "WEB-INF/classes/conff.properties";
                Properties easprop = new NewProperties();
                FileInputStream fis2 = new FileInputStream(filePath);
                easprop.load(fis2);
                String param2 = URLDecoder.decode(Base64.decode(param));
                if (param2 != null && (params = param2.split("\\%\\$\\#\\&")) != null) {
                    for (String str : params) {
                        String[] para = str.split("=");
                        if (para.length > 1) {
                            easprop.put(para[0], para[1]);
                        } else {
                            easprop.put(para[0], "");
                        }
                    }
                }
                FileOutputStream fot2 = new FileOutputStream(filePath);
                easprop.store(fot2, "The New properties file");
                ServletContext context = ServletActionContext.getServletContext();
                String typeString = (String) context.getAttribute("type");
                String showAnalyiseImage = (String) context.getAttribute("showAnalyiseImage");
                String sysVersion = (String) context.getAttribute("sysVersion");
                if (null != this.typeName && !this.typeName.equals("")) {
                    context.setAttribute(this.typeName, easprop.getProperty(this.typeName));
                    if (fot2 != null) {
                        try {
                            fot2.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    if (fis2 != null) {
                        fis2.close();
                    }
                    return;
                }
                Set<Object> set2 = easprop.keySet();
                for (Object obj : set2) {
                    context.setAttribute(obj.toString(), easprop.getProperty(obj.toString()));
                }
                context.setAttribute("type", typeString);
                context.setAttribute("showAnalyiseImage", showAnalyiseImage);
                context.setAttribute("sysVersion", sysVersion);
                easprop.clear();
                if (fot2 != null) {
                    try {
                        fot2.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (fis2 != null) {
                    fis2.close();
                }
                this.out.write("T");
            } catch (Exception e3) {
                this.out.write("F");
                e3.printStackTrace();
                if (0 != 0) {
                    try {
                        fot.close();
                    } catch (Exception e4) {
                        e4.printStackTrace();
                        return;
                    }
                }
                if (0 != 0) {
                    fis.close();
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    fot.close();
                } catch (Exception e5) {
                    e5.printStackTrace();
                    throw th;
                }
            }
            if (0 != 0) {
                fis.close();
            }
            throw th;
        }
    }

    public void loadPara() {
        FileInputStream fis = null;
        try {
            try {
                new CommonUtil();
                String src2 = CommonUtil.getRootPath(this.request);
                String filePath = src2 + "WEB-INF/classes/conff.properties";
                ServletContext context = ServletActionContext.getServletContext();
                String typeString = (String) context.getAttribute("type");
                String showAnalyiseImage = (String) context.getAttribute("showAnalyiseImage");
                String sysVersion = (String) context.getAttribute("sysVersion");
                Properties easprop = new Properties();
                fis = new FileInputStream(filePath);
                easprop.load(fis);
                if (null != this.typeName && !this.typeName.equals("")) {
                    context.setAttribute(this.typeName, easprop.getProperty(this.typeName));
                    try {
                        fis.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                Set<Object> set = easprop.keySet();
                for (Object obj : set) {
                    context.setAttribute(obj.toString(), easprop.getProperty(obj.toString()));
                }
                context.setAttribute("type", typeString);
                context.setAttribute("showAnalyiseImage", showAnalyiseImage);
                context.setAttribute("sysVersion", sysVersion);
                easprop.clear();
                fis.close();
                this.out.write("T");
            } catch (Exception e2) {
                this.out.write("F");
                e2.printStackTrace();
                try {
                    fis.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } finally {
            try {
                fis.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
        }
    }

    public static String getParameter(String path, String type) {
        try {
            String filePath = path + File.separator + "WEB-INF/classes/conff.properties";
            Properties easprop = new Properties();
            FileInputStream fis = new FileInputStream(filePath);
            easprop.load(fis);
            return (String) easprop.get(type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSysconffParameter(String path, String type) {
        try {
            String filePath = path + File.separator + "WEB-INF/classes/sysconff.properties";
            Properties easprop = new Properties();
            FileInputStream fis = new FileInputStream(filePath);
            easprop.load(fis);
            return (String) easprop.get(type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean loadPara(String path, String type) {
        ServletContext context = ServletActionContext.getServletContext();
        Properties easprop = new Properties();
        try {
            try {
                String filePath = path + File.separator + "WEB-INF/classes/conff.properties";
                FileInputStream fis = new FileInputStream(filePath);
                easprop.load(fis);
                if (null != type && !type.equals("")) {
                    context.setAttribute(type, easprop.getProperty(type));
                    easprop.clear();
                    return true;
                }
                Set<Object> set = easprop.keySet();
                for (Object obj : set) {
                    context.setAttribute(obj.toString(), easprop.getProperty(obj.toString()));
                }
                easprop.clear();
                fis.close();
                easprop.clear();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                easprop.clear();
                easprop.clear();
                return false;
            }
        } catch (Throwable th) {
            easprop.clear();
            throw th;
        }
    }

    public static void setParameter(String path, String type, String value) {
        try {
            String filePath = path + File.separator + "WEB-INF/classes/conff.properties";
            Properties easprop = new Properties();
            FileInputStream fis = new FileInputStream(filePath);
            easprop.load(fis);
            easprop.setProperty(type, value);
            FileOutputStream os = new FileOutputStream(filePath);
            easprop.store(os, (String) null);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDataBaeTableParameter(String path, String type) {
        try {
            String filePath = path + File.separator + "WEB-INF/history_data.properties";
            Properties easprop = new Properties();
            FileInputStream fis = new FileInputStream(filePath);
            easprop.load(fis);
            return (String) easprop.get(type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getSmsKey() {
        JSONObject jsonObject = JSONObject.fromObject("{\"url\": \"http://qw.api.taobao.com/router/rest\", \"appkey\": \"DELETED\", \"secret\": \"DELETED\",\"yhzcyzm\": \"SMS_56230021\",\"xgmmyzm\": \"SMS_56230019\",\"sfyzm\": \"SMS_56230025\",\"zcyz\": \"注册验证\",\"sfyz\": \"达美嘉教育科技有限公司\",}");
        return jsonObject;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public static void main(String[] args) {
    }

    public static String validLic() {
        ServletContext context = ServletActionContext.getServletContext();
        String licPath = (String) context.getAttribute(Const.licPath);
        String filePath = (String) context.getAttribute(Const.project_path);
        String info = "";
        try {
            if (!DateUtil.authV(filePath)) {
                info = "license信息错误！！！！！";
            }
            if (DateUtil.authone(filePath)) {
                info = "icense过期！！！！！";
            }
            String aa = Util.auth(licPath);
            if (null != aa) {
                info = aa;
            }
            if (null != info && !info.equals("")) {
                context.setAttribute(Const.result_tag, "F");
                context.setAttribute(Const.result_tag_info, info);
            }
        } catch (Exception e) {
            e.printStackTrace();
            info = "license信息异常";
        }
        return info;
    }
}
