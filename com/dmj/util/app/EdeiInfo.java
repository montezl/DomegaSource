package com.dmj.util.app;

import java.io.Serializable;
import java.lang.management.ManagementFactory;

/* loaded from: EdeiInfo.class */
public class EdeiInfo implements Serializable {
    private String systemId;
    private String vendor;
    private String edeiVersion;
    private String projectPath;
    private String asUrl;
    private String arthasUrl;
    private boolean moreSchool;
    private boolean free;
    private boolean studentAppEnabled;
    private boolean teacherAppEnabled;
    private boolean reportCacheDisabled;
    private String pid;

    public String getSystemId() {
        return this.systemId;
    }

    public EdeiInfo setSystemId(String systemId) {
        this.systemId = systemId;
        return this;
    }

    public String getEdeiVersion() {
        return this.edeiVersion;
    }

    public EdeiInfo setEdeiVersion(String edeiVersion) {
        this.edeiVersion = edeiVersion;
        return this;
    }

    public String getProjectPath() {
        return this.projectPath;
    }

    public EdeiInfo setProjectPath(String projectPath) {
        this.projectPath = projectPath;
        return this;
    }

    public String getAsUrl() {
        return this.asUrl;
    }

    public EdeiInfo setAsUrl(String asUrl) {
        this.asUrl = asUrl;
        return this;
    }

    public boolean getFree() {
        return this.free;
    }

    public EdeiInfo setFree(boolean free) {
        this.free = free;
        return this;
    }

    public boolean getStudentAppEnabled() {
        return this.studentAppEnabled;
    }

    public EdeiInfo setStudentAppEnabled(boolean studentAppEnabled) {
        this.studentAppEnabled = studentAppEnabled;
        return this;
    }

    public boolean getTeacherAppEnabled() {
        return this.teacherAppEnabled;
    }

    public EdeiInfo setTeacherAppEnabled(boolean teacherAppEnabled) {
        this.teacherAppEnabled = teacherAppEnabled;
        return this;
    }

    public String getVendor() {
        return this.vendor;
    }

    public EdeiInfo setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public boolean getMoreSchool() {
        return this.moreSchool;
    }

    public EdeiInfo setMoreSchool(boolean moreSchool) {
        this.moreSchool = moreSchool;
        return this;
    }

    public boolean isReportCacheDisabled() {
        return this.reportCacheDisabled;
    }

    public void setReportCacheDisabled(boolean reportCacheDisabled) {
        this.reportCacheDisabled = reportCacheDisabled;
    }

    public String getArthasUrl() {
        return this.arthasUrl;
    }

    public EdeiInfo setArthasUrl(String arthasUrl) {
        this.arthasUrl = arthasUrl;
        return this;
    }

    public String getPid() {
        return this.pid;
    }

    public EdeiInfo setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public static String getProccessPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return pid;
    }
}
