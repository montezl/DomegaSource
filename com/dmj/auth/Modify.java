package com.dmj.auth;

import com.dmj.util.DateUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

/* loaded from: Modify.class */
public class Modify {
    Logger log = Logger.getLogger(getClass());

    /* JADX WARN: Code restructure failed: missing block: B:19:0x000e, code lost:
    
        if (r8.equals("") != false) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void modifyAuth(javax.servlet.ServletContext r5, int r6, java.lang.String r7) {
        /*
            Method dump skipped, instructions count: 273
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dmj.auth.Modify.modifyAuth(javax.servlet.ServletContext, int, java.lang.String):void");
    }

    public static void modifyAuth1(ServletContext context, String licPath1, String str) {
        File file = new File(licPath1);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        RandomAccessFile raf = null;
        FileLock lock = null;
        FileChannel channel = null;
        try {
            try {
                raf = new RandomAccessFile(file, "rws");
                channel = raf.getChannel();
                while (true) {
                    try {
                        lock = channel.lock();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    } catch (OverlappingFileLockException e3) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (lock != null) {
                        break;
                    }
                }
                String filePath = "";
                if (null == filePath || filePath.equals("")) {
                    String confPath = context.getRealPath("/");
                    filePath = CreateFile.getLicPath(confPath);
                }
                Properties easprop = new Properties();
                FileInputStream fis = new FileInputStream(filePath);
                easprop.load(fis);
                String systemId = easprop.getProperty("systemId");
                if (null == systemId || systemId.equals("")) {
                    systemId = "NULL";
                }
                String str2 = systemId + "=";
                String tempSystemId = easprop.getProperty("tempSystemId");
                fis.close();
                String qufen = "";
                StringBuffer sb = new StringBuffer();
                while (true) {
                    try {
                        String line = raf.readLine();
                        if (line == null || line.trim().equals("")) {
                            break;
                        }
                        String line2 = new String(line.getBytes("ISO-8859-1"), "utf-8");
                        if (line2.indexOf(systemId + "=") == -1) {
                            sb.append(line2 + "\r\n");
                        } else {
                            sb.append(systemId + "=" + tempSystemId + "\r\n");
                            qufen = "1";
                        }
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                if (!qufen.equals("1")) {
                    sb.append(systemId + "=" + tempSystemId + "\r\n");
                }
                byte[] bs = sb.toString().getBytes("utf-8");
                raf.setLength(bs.length);
                raf.seek(0L);
                raf.write(bs);
                if (lock != null) {
                    try {
                        lock.release();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e7) {
                        e7.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e12) {
                e12.printStackTrace();
                if (lock != null) {
                    try {
                        lock.release();
                    } catch (IOException e8) {
                        e8.printStackTrace();
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e9) {
                        e9.printStackTrace();
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e10) {
                        e10.printStackTrace();
                    }
                }
            } catch (IOException e11) {
                e11.printStackTrace();
                if (lock != null) {
                    try {
                        lock.release();
                    } catch (IOException e13) {
                        e13.printStackTrace();
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e14) {
                        e14.printStackTrace();
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e15) {
                        e15.printStackTrace();
                    }
                }
            }
        } catch (Throwable th) {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e16) {
                    e16.printStackTrace();
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e17) {
                    e17.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e18) {
                    e18.printStackTrace();
                }
            }
            throw th;
        }
    }

    private static String getDnum(String dnum, long createDate, int addDay) {
        int recordDays = 0;
        if (null != dnum && !dnum.equals("")) {
            recordDays = Integer.parseInt(dnum) + addDay;
        }
        long nowDays = (System.currentTimeMillis() - createDate) / 86400000;
        int days = (int) (nowDays > ((long) recordDays) ? nowDays : recordDays);
        String dnum2 = "000000000000" + days;
        return dnum2.substring(dnum2.length() - 10, dnum2.length());
    }

    private static boolean dateCompare(Date date, long createDay, String days) {
        Date createDate = new Date(createDay);
        int runDays = Integer.parseInt(days);
        boolean result = date.after(DateUtil.getDate(createDate, runDays));
        return result;
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

    public static void main(String[] args) throws Exception {
    }
}
