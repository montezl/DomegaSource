package com.zht.db;

import com.alibaba.fastjson.JSON;
import com.dmj.cs.util.ReportCache;
import com.dmj.util.StaticClassResources;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;

/* loaded from: ServiceManager.class */
public class ServiceManager implements InvocationHandler {
    private Object service;
    private Logger logger = Logger.getLogger(getClass());

    public ServiceManager(Object service) {
        this.service = service;
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object obj = null;
        boolean enabledTransaction = method.isAnnotationPresent(Transaction.class);
        boolean enabledCache = method.isAnnotationPresent(Cache.class);
        boolean isReportCacheDisabled = StaticClassResources.EdeiInfo.isReportCacheDisabled();
        String key = null;
        Cache cacheAnnotation = null;
        if (!isReportCacheDisabled && enabledCache) {
            cacheAnnotation = (Cache) method.getAnnotation(Cache.class);
            if (cacheAnnotation.put()) {
                key = method.getDeclaringClass().getName() + "." + method.getName() + "--" + JSON.toJSONString(args);
                obj = ReportCache.get(key);
                if (obj != null) {
                    return obj;
                }
            }
        }
        try {
            try {
                DbUtils.beginTransaction(enabledTransaction, method);
                obj = method.invoke(this.service, args);
                DbUtils.commitTransaction(method);
                if (!isReportCacheDisabled && enabledCache && cacheAnnotation.put()) {
                    ReportCache.put(key, obj);
                }
                DbUtils.endTransaction(method);
                if (!isReportCacheDisabled && enabledCache) {
                    boolean deleteAll = cacheAnnotation.deleteAll();
                    String[] removeServices = cacheAnnotation.removeService();
                    if (deleteAll) {
                        ReportCache.invalidateAll();
                    } else if (removeServices.length > 0) {
                        ReportCache.removeServices(removeServices);
                    }
                }
            } catch (Exception e) {
                throwException(e);
                DbUtils.endTransaction(method);
                if (!isReportCacheDisabled && enabledCache) {
                    boolean deleteAll2 = cacheAnnotation.deleteAll();
                    String[] removeServices2 = cacheAnnotation.removeService();
                    if (deleteAll2) {
                        ReportCache.invalidateAll();
                    } else if (removeServices2.length > 0) {
                        ReportCache.removeServices(removeServices2);
                    }
                }
            }
            return obj;
        } catch (Throwable th) {
            DbUtils.endTransaction(method);
            if (!isReportCacheDisabled && enabledCache) {
                boolean deleteAll3 = cacheAnnotation.deleteAll();
                String[] removeServices3 = cacheAnnotation.removeService();
                if (deleteAll3) {
                    ReportCache.invalidateAll();
                } else if (removeServices3.length > 0) {
                    ReportCache.removeServices(removeServices3);
                }
            }
            throw th;
        }
    }

    private void throwException(Exception e) {
        this.logger.error((Object) null, e);
        DbUtils.rollbackTransaction();
        SubException cause = e.getCause();
        if (cause != null && cause.getClass() == SubException.class) {
            SubException subException = cause;
            throw new RuntimeException(subException.getMsg());
        }
        throw new RuntimeException(getMsg(e));
    }

    private String getMsg(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String str = "\r\n" + sw.toString() + "\r\n";
            if (pw != null) {
                pw.close();
            }
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return str;
        } catch (Exception e2) {
            if (pw != null) {
                pw.close();
            }
            if (sw == null) {
                return "";
            }
            try {
                sw.close();
                return "";
            } catch (IOException e12) {
                e12.printStackTrace();
                return "";
            }
        } catch (Throwable th) {
            if (pw != null) {
                pw.close();
            }
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e13) {
                    e13.printStackTrace();
                }
            }
            throw th;
        }
    }
}
