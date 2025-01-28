package com.dmj.daoimpl.base;

import com.dmj.util.DateUtil;
import org.apache.log4j.Logger;

/* loaded from: MyException.class */
public class MyException extends RuntimeException {
    private static final long serialVersionUID = -1956737539579057897L;
    private final Logger logger;

    public MyException(Throwable cause, String message) {
        super(message, cause);
        this.logger = Logger.getLogger(getClass());
        this.logger.error(DateUtil.getCurrentTime() + " : " + ExceptionDetailUtil.getThrowableDetail(cause));
    }

    public MyException(Throwable cause, String message, String arg) {
        super(message + ((arg == null || arg.length() <= 0) ? "" : "\r\n参数：" + arg), cause);
        this.logger = Logger.getLogger(getClass());
        this.logger.error(DateUtil.getCurrentTime() + " : " + ExceptionDetailUtil.getThrowableDetail(cause) + ((arg == null || arg.length() <= 0) ? "" : "\r\n参数：" + arg));
    }
}
