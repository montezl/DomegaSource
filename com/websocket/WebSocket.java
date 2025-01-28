package com.websocket;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.dmj.util.session.OnlineUserContext;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.struts2.ServletActionContext;

@ServerEndpoint("/websocket")
/* loaded from: WebSocket.class */
public class WebSocket {
    private static int onlineCount = 0;
    private static ReentrantLock lock = new ReentrantLock(true);
    private static CopyOnWriteArraySet<WebSocket> Sessions = new CopyOnWriteArraySet<>();
    private Session session;
    private HttpSession httpSession;
    private static final String prefix = "REQUEST";
    private Set<Thread> Threads = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Sessions.add(this);
        setHttpSession(session);
        addOnlineCount();
    }

    public void setHttpSession(Session session) {
        try {
            Field httpSessionIdField = session.getClass().getDeclaredField("httpSessionId");
            httpSessionIdField.setAccessible(true);
            String httpSessionId = (String) httpSessionIdField.get(session);
            HttpSession httpSession = OnlineUserContext.getSession(httpSessionId);
            setHttpSession(httpSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose() {
        Sessions.remove(this);
        subOnlineCount();
        releaseThread();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            SocketMsg socketMsg = (SocketMsg) JSON.parseObject(message, SocketMsg.class);
            String requestId = socketMsg.getRequestId();
            getHttpSession().setAttribute(prefix + requestId, this);
        } catch (Exception e) {
        }
    }

    private void addThread() {
        Thread thread = Thread.currentThread();
        this.Threads.add(thread);
    }

    private void releaseThread() {
        this.Threads.forEach(thread -> {
            try {
                thread.stop();
            } catch (Exception e) {
            }
        });
    }

    public static void ding() {
        send(null);
    }

    public static void ding(HttpServletRequest request) {
        send(request, (String) null);
    }

    public static void send(String msg) {
        send(null, null, msg);
    }

    public static void send(HttpServletRequest request, String msg) {
        send(request, null, msg);
    }

    public static void send(String percent, String msg) {
        send(null, percent, msg);
    }

    public static void send(HttpServletRequest request, String percent, String msg) {
        WebSocket webSocket;
        HttpServletRequest newRequest = request;
        if (newRequest == null) {
            try {
                newRequest = ServletActionContext.getRequest();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        HttpSession session = newRequest.getSession();
        String requestId = newRequest.getParameter("requestId");
        if (StrUtil.isEmpty(requestId) || (webSocket = (WebSocket) session.getAttribute(prefix + requestId)) == null || StrUtil.isEmpty(msg)) {
            return;
        }
        SocketMsg socketMsg = new SocketMsg();
        socketMsg.setMsg(msg);
        socketMsg.setPercent(percent);
        socketMsg.setSessionId(session.getId());
        socketMsg.setRequestId(requestId);
        try {
            webSocket.sendMessage(JSON.toJSONString(socketMsg));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        try {
            lock.lock();
            this.session.getBasicRemote().sendText(message);
            lock.unlock();
        } catch (Throwable th) {
            lock.unlock();
            throw th;
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        onlineCount--;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public HttpSession getHttpSession() {
        return this.httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }
}
