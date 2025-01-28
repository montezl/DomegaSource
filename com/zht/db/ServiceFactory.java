package com.zht.db;

import java.lang.reflect.Proxy;

/* loaded from: ServiceFactory.class */
public class ServiceFactory {
    public static Object getObject(Object service) {
        return Proxy.newProxyInstance(service.getClass().getClassLoader(), service.getClass().getInterfaces(), new ServiceManager(service));
    }
}
