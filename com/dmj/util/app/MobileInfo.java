package com.dmj.util.app;

/* loaded from: MobileInfo.class */
public class MobileInfo {
    private String mobile;
    private String code;
    private long sendTime;

    public MobileInfo(String mobile, String code, long sendTime) {
        this.mobile = mobile;
        this.code = code;
        this.sendTime = sendTime;
    }

    public MobileInfo() {
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
}
