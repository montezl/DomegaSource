package com.zht.db;

/* loaded from: RowArg.class */
public class RowArg<T, K, V> {
    private String sql;
    private Object arg;

    public RowArg(String sql, Object arg) {
        this.sql = sql;
        this.arg = arg;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object getArg() {
        return this.arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }
}
