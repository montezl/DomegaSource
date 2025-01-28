package com.dmj.action.login;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.jfree.util.Log;

/* loaded from: SessionListenerAction.class */
public class SessionListenerAction implements HttpSessionListener {
    public void sessionCreated(HttpSessionEvent event) {
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        try {
            event.getSession();
        } catch (Exception e) {
            Log.info("sessionDestroyed", e);
        }
    }
}
