package com.dmj.util.session;

import cn.hutool.core.util.StrUtil;
import com.dmj.domain.OnlineUser;
import com.dmj.domain.User;
import com.dmj.util.Const;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.jfree.util.Log;

/* loaded from: SessionListener.class */
public class SessionListener implements HttpSessionAttributeListener, HttpSessionListener {
    public void sessionDestroyed(HttpSessionEvent event) {
        try {
            HttpSession session = event.getSession();
            OnlineUserContext.deleteOnlineUser(session);
        } catch (Exception e) {
            Log.info("sessionDestroyed", e);
        }
    }

    public void kickOutAndlogin(HttpSession session, User user) {
        if (OnlineUserContext.containsUser(session, user)) {
            destroySession(session, user);
        }
        OnlineUserContext.addSession(session);
        OnlineUserContext.addOnlineUser(session, user);
    }

    public void destroySession(HttpSession session, User loginUser) {
        OnlineUser user = OnlineUserContext.getOnlineUser(session, loginUser);
        OnlineUserContext.deleteOnlineUser(user);
    }

    private void login(HttpSession session, User user) {
        String userType = user.getUsertype();
        if (StrUtil.isBlank(userType)) {
            return;
        }
        boolean z = -1;
        switch (userType.hashCode()) {
            case 50:
                if (userType.equals("2")) {
                    z = false;
                    break;
                }
                break;
            case 51:
                if (userType.equals("3")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case Const.clipError_failure /* 0 */:
                studentLogin(session, user);
                break;
            case true:
                break;
            default:
                kickOutAndlogin(session, user);
                return;
        }
        parentLogin(session, user);
    }

    private void parentLogin(HttpSession session, User parent) {
        if (!"3".equals(parent.getUsertype())) {
            return;
        }
        User student = (User) session.getAttribute(Const.LOGIN_USER);
        if (student != null && "2".equals(student.getUsertype())) {
            OnlineUserContext.deleteOnlineUser(session);
        }
        kickOutAndlogin(session, parent);
    }

    private void studentLogin(HttpSession session, User student) {
        if (!"2".equals(student.getUsertype())) {
            return;
        }
        User parent = (User) session.getAttribute(Const.LOGIN_PARENTUSER);
        if (parent != null && "3".equals(parent.getUsertype())) {
            return;
        }
        kickOutAndlogin(session, student);
    }

    public void attributeAdded(HttpSessionBindingEvent event) {
        HttpSession session = event.getSession();
        String key = event.getName();
        if (!key.equals(Const.LOGIN_PARENTUSER) && !key.equals(Const.LOGIN_USER)) {
            return;
        }
        User user = (User) event.getValue();
        login(session, user);
    }

    public void attributeRemoved(HttpSessionBindingEvent event) {
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
    }

    public void sessionCreated(HttpSessionEvent se) {
    }
}
