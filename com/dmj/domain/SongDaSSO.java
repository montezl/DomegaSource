package com.dmj.domain;

/* loaded from: SongDaSSO.class */
public class SongDaSSO {
    private String code;
    private String message;
    private SongDaSSOUser data;

    public SongDaSSO(SongDaSSOUser data) {
        this.data = data;
    }

    public SongDaSSO() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SongDaSSOUser getData() {
        return this.data;
    }

    public void setData(SongDaSSOUser data) {
        this.data = data;
    }
}
