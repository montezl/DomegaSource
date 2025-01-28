package com.dmj.util;

import com.dmj.action.base.BaseAction;
import com.dmj.domain.Log;
import com.dmj.domain.User;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.zht.db.ServiceFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/* loaded from: CommonUtil.class */
public class CommonUtil extends BaseAction {
    SystemService sysService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    Logger log = Logger.getLogger(getClass());

    public User getLoginUser(HttpServletRequest request) {
        HttpSession session;
        if (null == request || null == (session = request.getSession(true))) {
            return null;
        }
        User user = (User) session.getAttribute(Const.LOGIN_USER);
        return user;
    }

    public String getLoginUserName(HttpServletRequest request) {
        HttpSession session;
        User user;
        if (null == request || null == (session = request.getSession(true)) || null == (user = (User) session.getAttribute(Const.LOGIN_USER))) {
            return null;
        }
        return user.getUsername();
    }

    public String getLoginUserNum(HttpServletRequest request) {
        HttpSession session;
        User user;
        if (null == request || null == (session = request.getSession(true))) {
            return null;
        }
        if ("P".equals(session.getAttribute("logType"))) {
            user = (User) session.getAttribute(Const.LOGIN_PARENTUSER);
        } else {
            user = (User) session.getAttribute(Const.LOGIN_USER);
        }
        if (null == user) {
            return null;
        }
        return user.getId();
    }

    public static String getSystemType(HttpServletRequest request) {
        HttpSession session;
        String type;
        if (null == request || null == (session = request.getSession(true)) || null == (type = (String) session.getAttribute(Const.SYSTEM_TYPE)) || type.equals("")) {
            return "1";
        }
        return type;
    }

    public boolean addLog(String url, String name, String userid, String ip, String desc) {
        try {
            Log log = new Log();
            log.setRequestUrl(url);
            log.setIp(ip);
            log.setInsertUser(userid + "");
            log.setInsertDate(DateUtil.getCurrentTime());
            log.setOperate(name);
            this.sysService.save(log);
            return true;
        } catch (Exception e) {
            this.log.error("addLog():添加日志", e);
            e.printStackTrace();
            return false;
        }
    }

    public static String getRootPath(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        String path = request.getSession().getServletContext().getRealPath("/");
        return path;
    }

    public static String getRootPath(Class cla) {
        String path = cla.getProtectionDomain().getCodeSource().getLocation().getPath();
        return path;
    }

    public String getLoginUserid(HttpServletRequest request) {
        HttpSession session;
        User user;
        if (null == request || null == (session = request.getSession(true)) || null == (user = (User) session.getAttribute(Const.LOGIN_USER))) {
            return null;
        }
        return user.getUserid();
    }

    public static boolean isMobile(String str) {
        Pattern p = Pattern.compile("^1[23456789]\\d{9}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public String openStuPaper(String usertype, String stuExamImg) {
        try {
            String openStuPaper = "1";
            if ("0".equals(usertype)) {
                openStuPaper = "1";
            } else if ("1".equals(usertype)) {
                if ("1".equals(stuExamImg) || "2".equals(stuExamImg)) {
                    openStuPaper = "1";
                } else {
                    openStuPaper = "0";
                }
            } else if ("2".equals(usertype)) {
                if ("1".equals(stuExamImg) || "3".equals(stuExamImg)) {
                    openStuPaper = "1";
                } else {
                    openStuPaper = "0";
                }
            }
            return openStuPaper;
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }

    public static boolean isPassword(String password) {
        Pattern p1 = Pattern.compile("^(?!([a-zA-Z]+|\\d+)$)[a-zA-Z\\d]{6,20}$");
        return p1.matcher(password).find();
    }

    public static boolean isPassword2(String password) {
        Pattern p1 = Pattern.compile("^[a-zA-Z0-9]{6,20}$");
        return p1.matcher(password).find();
    }
}
