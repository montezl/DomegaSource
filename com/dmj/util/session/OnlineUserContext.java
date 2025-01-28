package com.dmj.util.session;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import com.dmj.cs.bean.RequestVisitor;
import com.dmj.domain.OnlineUser;
import com.dmj.domain.User;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.service.userManagement.UserService;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.serviceimpl.userManagement.UserServiceImpl;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.zht.db.ServiceFactory;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;

/* loaded from: OnlineUserContext.class */
public class OnlineUserContext {
    private static Map<String, HttpSession> sessionMap = new ConcurrentHashMap();
    private static Map<String, OnlineUser> userMap = new ConcurrentHashMap();
    private static SystemService sys = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private static UserService userService = (UserService) ServiceFactory.getObject(new UserServiceImpl());

    public static boolean containsSession(String sessionId) {
        return sessionMap.containsKey(sessionId);
    }

    public static void addSession(HttpSession session) {
        sessionMap.put(session.getId(), session);
    }

    public static void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    public static void addOnlineUser(HttpSession session, User loginUser) {
        OnlineUser user = toOnlineUser(session, loginUser);
        String userKey = user.getUserName() + getDevice(session);
        userMap.put(userKey, user);
        try {
            sys.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getDevice(HttpSession session) {
        return ((RequestVisitor) session.getAttribute("RequestVisitorKey")).getDevice();
    }

    private static OnlineUser toOnlineUser(HttpSession session, User loginUser) {
        OnlineUser user = new OnlineUser();
        user.setId(GUID.getGUIDStr());
        user.setUserNum(loginUser.getId());
        user.setUserName(loginUser.getUsername());
        user.setSessionid(session.getId());
        user.setInsertTime(DateUtil.getCurrentTime());
        user.setUsertype((String) session.getAttribute("logType"));
        return user;
    }

    public static List<OnlineUser> getOnlineUserList() {
        return (List) userMap.values().stream().collect(Collectors.toList());
    }

    public static long getOnlineUserCount() {
        Collection<OnlineUser> list = userMap.values();
        cn.hutool.core.date.DateUtil.offsetSecond(new Date(), 180);
        long count = list.stream().filter(u -> {
            String sessionId = u.getSessionid();
            if (!sessionMap.containsKey(sessionId)) {
                return false;
            }
            Date lastVisitorDate = new Date(sessionMap.get(sessionId).getLastAccessedTime());
            if (cn.hutool.core.date.DateUtil.between(lastVisitorDate, DateTime.now(), DateUnit.SECOND) < 180) {
                return true;
            }
            return false;
        }).count();
        return count;
    }

    public static void deleteOnlineUser(HttpSession session) {
        String sessionId = session.getId();
        sessionMap.remove(sessionId);
        for (OnlineUser user : userMap.values()) {
            if (user.getSessionid().equals(sessionId)) {
                String userKey = user.getUserName() + getDevice(session);
                userMap.remove(userKey);
                userService.delOnlineuserBySessionid(sessionId);
            }
        }
    }

    public static void deleteOnlineUser(OnlineUser user) {
        String sessionId = user.getSessionid();
        if (sessionMap.containsKey(sessionId)) {
            HttpSession session = sessionMap.get(sessionId);
            session.invalidate();
        }
    }

    public static OnlineUser getOnlineUser(HttpSession session, User user) {
        String userKey = user.getUsername() + getDevice(session);
        return userMap.get(userKey);
    }

    public static HttpSession getSession(String sessionId) {
        if (sessionMap.containsKey(sessionId)) {
            return sessionMap.get(sessionId);
        }
        return null;
    }

    public static boolean containsUser(HttpSession session, User user) {
        String userKey = user.getUsername() + getDevice(session);
        return userMap.containsKey(userKey);
    }
}
