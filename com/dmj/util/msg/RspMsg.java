package com.dmj.util.msg;

import com.alibaba.fastjson.JSON;
import com.dmj.util.Const;
import javax.servlet.http.HttpServletResponse;

/* loaded from: RspMsg.class */
public class RspMsg {
    private int code;
    private String msg;
    private Object data;

    public RspMsg() {
    }

    public RspMsg(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return getCode() == 200;
    }

    public boolean isError() {
        return getCode() != 200;
    }

    public static RspMsg success(Object data) {
        return new RspMsg(200, "恭喜，获取数据成功", data);
    }

    public static RspMsg success(String msg, Object data) {
        return new RspMsg(200, msg, data);
    }

    public static RspMsg error(String msg) {
        return error(Integer.valueOf(Const.height_500), msg, (Object) null);
    }

    public static RspMsg error(Object data) {
        return error(Integer.valueOf(Const.height_500), "很遗憾，获取数据失败", data);
    }

    public static RspMsg error(String msg, Object data) {
        return error(Integer.valueOf(Const.height_500), msg, data);
    }

    public static RspMsg error(Integer status, String msg, Object data) {
        return new RspMsg(status.intValue(), msg, data);
    }

    public static RspMsg error(HttpServletResponse rsp, String msg) {
        return error(rsp, Integer.valueOf(Const.height_500), msg, null);
    }

    public static RspMsg error(HttpServletResponse rsp, Object data) {
        return error(rsp, Integer.valueOf(Const.height_500), "很遗憾，获取数据失败", data);
    }

    public static RspMsg error(HttpServletResponse rsp, String msg, Object data) {
        return error(rsp, Integer.valueOf(Const.height_500), msg, data);
    }

    public static RspMsg error(HttpServletResponse rsp, Integer status, String msg, Object data) {
        if (rsp != null) {
            rsp.setStatus(status.intValue());
        }
        return new RspMsg(status.intValue(), msg, data);
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }
}
