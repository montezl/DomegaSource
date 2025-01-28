package com.dmj.auth;

import com.dmj.auth.bean.License;
import com.dmj.auth.util.ByteUtil;
import com.dmj.auth.util.Util;
import com.dmj.util.Conffig;
import com.dmj.util.Const;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/* loaded from: CreateFile.class */
public class CreateFile {
    public static String getLicenceStringByLocal(String filePath) throws Exception {
        StringBuffer buffer = new StringBuffer();
        Properties easprop = new Properties();
        FileInputStream fis = new FileInputStream(filePath);
        easprop.load(fis);
        buffer.append((String) easprop.get(License.IP));
        buffer.append("^");
        buffer.append((String) easprop.get(License.MAC));
        buffer.append("^");
        buffer.append((String) easprop.get(License.SERIALNUMBER));
        buffer.append("^");
        buffer.append(License.format.parse((String) easprop.get("expiredDate")));
        buffer.append("^");
        buffer.append((String) easprop.get("version"));
        buffer.append("^");
        buffer.append((String) easprop.get(License.VENDOR));
        buffer.append("^");
        buffer.append((String) easprop.get(License.USERS));
        buffer.append("^");
        buffer.append((String) easprop.get(License.STUDENTS));
        buffer.append("^");
        buffer.append((String) easprop.get(License.TIMESTAMP));
        buffer.append("^");
        String reportExpiredDate = (String) easprop.get(License.REPORT_EXPIREDDATE);
        if (null != reportExpiredDate && !reportExpiredDate.equals("")) {
            buffer.append((String) easprop.get(License.REPORT_EXPIREDDATE));
            buffer.append("^");
        }
        buffer.append((String) easprop.get(License.SCHOOLS));
        buffer.append("^");
        buffer.append((String) easprop.get(License.VALUE));
        buffer.append("^");
        buffer.append((String) easprop.get(License.DNUM));
        buffer.append("^");
        String systype = (String) easprop.get(License.SYSTYPE);
        if (null != systype && !systype.equals("")) {
            buffer.append((String) easprop.get(License.SYSTYPE));
            buffer.append("^");
            buffer.append((String) easprop.get("sysVersion"));
            buffer.append("^");
            buffer.append((String) easprop.get("showAnalyiseImage"));
            buffer.append("^");
        }
        String openOcs = (String) easprop.get("openOcs");
        if (null != openOcs && !openOcs.equals("")) {
            buffer.append((String) easprop.get("openOcs"));
            buffer.append("^");
            buffer.append((String) easprop.get("systemId"));
            buffer.append("^");
            buffer.append((String) easprop.get(License.NUMBEROFTESTSALLOWEDPERMONTH));
            buffer.append("^");
        }
        return buffer.toString();
    }

    public static License getLicense(String filePath) throws Exception {
        Properties easprop = new Properties();
        FileInputStream fis = new FileInputStream(filePath);
        easprop.load(fis);
        License license = new License();
        license.setExpiredDate(License.format.parse((String) easprop.get("expiredDate")));
        String reportExpiredDate = (String) easprop.get(License.REPORT_EXPIREDDATE);
        if (null == reportExpiredDate || reportExpiredDate.equals("")) {
            reportExpiredDate = "09/30/8888";
        }
        license.setReportExpiredDate(License.format.parse(reportExpiredDate));
        license.setVersion((String) easprop.get("version"));
        String username = (String) easprop.get(License.VENDOR);
        String resultName = new String(username.getBytes("ISO-8859-1"), "utf8");
        license.setVendor(resultName);
        license.setIp((String) easprop.get(License.IP));
        license.setMac((String) easprop.get(License.MAC));
        license.setSignature((String) easprop.get(License.SIGNATURE));
        license.setUsers((String) easprop.get(License.USERS));
        license.setSchool((String) easprop.get(License.SCHOOL));
        license.setSerialNumber((String) easprop.get(License.SERIALNUMBER));
        license.setStudents((String) easprop.get(License.STUDENTS));
        license.setSchools((String) easprop.get(License.SCHOOLS));
        license.setVal((String) easprop.get(License.VALUE));
        license.setDnum((String) easprop.get(License.DNUM));
        license.setTimestamp((String) easprop.get(License.TIMESTAMP));
        license.setSystemType((String) easprop.get(License.SYSTYPE));
        license.setSysVersion((String) easprop.get("sysVersion"));
        license.setShowAnalyiseImage((String) easprop.get("showAnalyiseImage"));
        String openOcs = (String) easprop.get("openOcs");
        if (null != openOcs && !openOcs.equals("")) {
            license.setOpenOcs((String) easprop.get("openOcs"));
            license.setSystemId((String) easprop.get("systemId"));
            license.setNumberOfTestsAllowedPerMonth((String) easprop.get(License.NUMBEROFTESTSALLOWEDPERMONTH));
        }
        fis.close();
        return license;
    }

    public static void writeSignature(byte[] signedtrue, String filePath) throws Exception {
        Properties easprop = new Properties();
        FileInputStream fis = new FileInputStream(filePath);
        easprop.load(fis);
        ByteUtil.combinString(signedtrue);
        easprop.setProperty(License.SIGNATURE, Util.encodeStr(ByteUtil.combinString(signedtrue)));
        OutputStream output = new FileOutputStream(filePath);
        easprop.store(output, "");
        output.close();
        fis.close();
    }

    public static String getLicPath(String conffPath) {
        String settingPathString = Conffig.getParameter(conffPath, Const.licPath);
        if (null == settingPathString || settingPathString.equals("")) {
            return "C://license.lic";
        }
        return settingPathString;
    }

    public static void main(String[] args) throws Exception {
        Util.verfify("F://license.lic");
    }
}
