package com.dmj.auth.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/* loaded from: License.class */
public class License {
    public static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    public static final String IP = "ip";
    public static final String MAC = "mac";
    public static final String SERIALNUMBER = "serialNumber";
    public static final String IP_c = "IP";
    public static final String MAC_c = "MAC";
    public static final String SERIALNUMBER_c = "CPU";
    public static final String SIGNATURE = "signature";
    public static final String EXPIREDDATE = "expiredDate";
    public static final String SCHOOLS = "schools";
    public static final String USERS = "users";
    public static final String STUDENTS = "TheNumberOfStudentsBeAllowed";
    public static final String VERSION = "version";
    public static final String VENDOR = "vendor";
    public static final String TIMESTAMP = "Timestamp";
    public static final String REPORT_EXPIREDDATE = "reportExpiredDate";
    public static final String COMPANYNAME_VALUE = "do_mega Co.Ltd";
    public static final String COMPANY_NAME = "CompanyName";
    public static final String SCHOOL = "school";
    public static final String CPUID = "CpuId";
    public static final String VALUE = "val";
    public static final String DNUM = "dnum";
    public static final String SYSTYPE = "systemType";
    public static final String SYSVERSION = "sysVersion";
    public static final String SHOWANALYISEIMAGE = "showAnalyiseImage";
    public static final String OPENOCS = "openOcs";
    public static final String SYSTEMID = "systemId";
    public static final String NUMBEROFTESTSALLOWEDPERMONTH = "numberOfTestsAllowedPerMonth";
    private Date expiredDate;
    private String expiredDateStr;
    private String version;
    private String vendor;
    private String ip;
    private String mac;
    private String signature;
    private String users;
    private String school;
    private String schools;
    private String serialNumber = null;
    private String CpuId;
    private String timestamp;
    private String companyName;
    private String students;
    private Date reportExpiredDate;
    private String reportExpiredDateStr;
    private String val;
    private String dnum;
    private String systemType;
    private String sysVersion;
    private String showAnalyiseImage;
    private String openOcs;
    private String systemId;
    private String numberOfTestsAllowedPerMonth;
    private boolean isExpired;

    public String getShowAnalyiseImage() {
        return this.showAnalyiseImage;
    }

    public void setShowAnalyiseImage(String showAnalyiseImage) {
        this.showAnalyiseImage = showAnalyiseImage;
    }

    public String toString() {
        return "License [expiredDate=" + this.expiredDate + ", ip=" + this.ip + ", mac=" + this.mac + ", school=" + this.schools + ", serialNumber=" + this.serialNumber + ", signature=" + this.signature + ", users=" + this.users + ", vendor=" + this.vendor + ", version=" + this.version + "]";
    }

    public String getDnum() {
        return this.dnum;
    }

    public void setDnum(String dnum) {
        this.dnum = dnum;
    }

    public Date getExpiredDate() {
        return this.expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getVendor() {
        return this.vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUsers() {
        return this.users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getSchool() {
        return this.school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isExpired() {
        return (this.expiredDate == null || this.expiredDate == null || System.currentTimeMillis() - this.expiredDate.getTime() <= 0) ? false : true;
    }

    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    public String getCpuId() {
        return this.CpuId;
    }

    public void setCpuId(String cpuId) {
        this.CpuId = cpuId;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStudents() {
        return this.students;
    }

    public void setStudents(String students) {
        this.students = students;
    }

    public Date getReportExpiredDate() {
        return this.reportExpiredDate;
    }

    public void setReportExpiredDate(Date reportExpiredDate) {
        this.reportExpiredDate = reportExpiredDate;
    }

    public String getSchools() {
        return this.schools;
    }

    public void setSchools(String schools) {
        this.schools = schools;
    }

    public String getExpiredDateStr() {
        return this.expiredDateStr;
    }

    public void setExpiredDateStr(String expiredDateStr) {
        this.expiredDateStr = expiredDateStr;
    }

    public String getReportExpiredDateStr() {
        return this.reportExpiredDateStr;
    }

    public void setReportExpiredDateStr(String reportExpiredDateStr) {
        this.reportExpiredDateStr = reportExpiredDateStr;
    }

    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getSystemType() {
        return this.systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getSysVersion() {
        return this.sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }

    public String getOpenOcs() {
        return this.openOcs;
    }

    public void setOpenOcs(String openOcs) {
        this.openOcs = openOcs;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getNumberOfTestsAllowedPerMonth() {
        return this.numberOfTestsAllowedPerMonth;
    }

    public void setNumberOfTestsAllowedPerMonth(String numberOfTestsAllowedPerMonth) {
        this.numberOfTestsAllowedPerMonth = numberOfTestsAllowedPerMonth;
    }
}
