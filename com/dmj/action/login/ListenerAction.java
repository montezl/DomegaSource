package com.dmj.action.login;

import com.dmj.service.userManagement.UserService;
import com.dmj.serviceimpl.userManagement.UserServiceImpl;
import com.zht.db.ServiceFactory;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* loaded from: ListenerAction.class */
public class ListenerAction implements ServletContextListener {
    UserService userService = (UserService) ServiceFactory.getObject(new UserServiceImpl());

    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("tomcat关闭");
    }

    public void contextInitialized(ServletContextEvent arg0) {
        try {
            this.userService.deleteAllUserFromOnlineUser();
            List reslist = this.userService.queryresource("0", 0);
            arg0.getServletContext().setAttribute("conreslist", reslist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
