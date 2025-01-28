package com.dmj.util;

import com.dmj.auth.CreateFile;
import com.dmj.auth.bean.License;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import org.apache.log4j.Logger;

/* loaded from: DateUtil.class */
public class DateUtil {
    public Logger log = Logger.getLogger(getClass());

    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(date);
    }

    public static String getCurrentTime2() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return f.format(date);
    }

    public static String longToString(long l) {
        Date date = new Date(l);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(date);
    }

    public static String getCurrentDay() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(date);
    }

    public static String FormatDateToString(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(date);
    }

    public static long formatString(String s) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(s);
            return date.getTime();
        } catch (ParseException pex) {
            pex.printStackTrace();
            return 0L;
        }
    }

    public static Date getScheduleStartTime() throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() + 86400000));
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 00:00:00");
    }

    public static String getLastYearString() {
        GregorianCalendar g = new GregorianCalendar();
        int year = g.get(1);
        return String.valueOf(year - 1);
    }

    public static boolean authone(String conffPath) throws Exception {
        License lic = CreateFile.getLicense(CreateFile.getLicPath(conffPath));
        if (null == lic) {
            return false;
        }
        Date date = lic.getExpiredDate();
        return new Date().after(date);
    }

    public static boolean autlicense(String licPath, String licPath1) throws Exception {
        File file = new File(licPath1);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        Properties easprop = new Properties();
        FileInputStream fis = new FileInputStream(licPath);
        easprop.load(fis);
        String systemId = easprop.getProperty("systemId");
        if (null == systemId || systemId.equals("")) {
            systemId = "NULL";
        }
        String tempSystemId = easprop.getProperty("tempSystemId");
        Properties easprop1 = new Properties();
        FileInputStream fis1 = new FileInputStream(licPath1);
        easprop1.load(fis1);
        String tempSystemId1 = easprop1.getProperty(systemId);
        if (null == tempSystemId || tempSystemId.equals("") || null == tempSystemId1 || tempSystemId1.equals("") || tempSystemId1.equals(tempSystemId)) {
            return false;
        }
        return true;
    }

    public static int authonelicense(String conffPath) throws Exception {
        License lic = CreateFile.getLicense(CreateFile.getLicPath(conffPath));
        if (null == lic) {
            return 0;
        }
        Date date = lic.getExpiredDate();
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long expiredDate = sdf.parse(dateFormater.format(date)).getTime();
        long currenttime = sdf.parse(dateFormater.format(new Date())).getTime();
        int d = (int) (Math.abs(expiredDate - currenttime) / 86400000);
        return d;
    }

    public static int authonelicense_zy(Date date) throws Exception {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long expiredDate = sdf.parse(dateFormater.format(date)).getTime();
        long currenttime = sdf.parse(dateFormater.format(new Date())).getTime();
        int d = (int) (Math.abs(expiredDate - currenttime) / 86400000);
        return d;
    }

    public static String authtwo(String conffPath) throws Exception {
        License lic = CreateFile.getLicense(CreateFile.getLicPath(conffPath));
        lic.getExpiredDate();
        if (null == lic) {
            return "";
        }
        return lic.getSchool();
    }

    public static boolean authV(String conffPath) throws Exception {
        License lic = CreateFile.getLicense(CreateFile.getLicPath(conffPath));
        if (null == lic) {
            return false;
        }
        return Const.CUR_VERSION.equals(lic.getVersion().replace(Const.VERSION_tag, "").replace("q", ""));
    }

    public static String getTitle(String conffPath) throws Exception {
        License lic = CreateFile.getLicense(CreateFile.getLicPath(conffPath));
        if (null == lic) {
            return null;
        }
        return lic.getVendor();
    }

    public static boolean getReportExpired(String conffPath) throws Exception {
        License lic = CreateFile.getLicense(CreateFile.getLicPath(conffPath));
        if (null == lic) {
            return false;
        }
        Date date = lic.getReportExpiredDate();
        return new Date().after(date);
    }

    public static Date getDate(Date date, int days) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(5, days);
        Date date2 = calendar.getTime();
        return date2;
    }

    public static void main(String[] args) {
        long j;
        if (5 <= 6) {
            j = 6;
        } else {
            j = 5;
        }
        Long.parseLong("1421822094911");
    }
}
