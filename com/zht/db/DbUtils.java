package com.zht.db;

import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/* loaded from: DbUtils.class */
public class DbUtils {
    private static final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public static HikariDataSource ds = null;
    private static ThreadLocal<TransactionUtil> tl = new ThreadLocal<>();
    public static Boolean printSQL = false;
    public static Boolean writeLog = false;
    public static Properties plugin = new Properties();
    public static String username = null;
    public static String password = null;
    public static int maxActiveConnections = 0;
    public static String serverStartTime = f.format(new Date());

    static {
        loadDs();
    }

    public static PoolMonitor getPoolMonitor() {
        return new PoolMonitor(ds);
    }

    public static void closeDataSource() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }

    public static void loadDs() {
        try {
            closeDataSource();
            Properties properties = new Properties();
            try {
                InputStream inputStream = DbUtils.class.getClassLoader().getResourceAsStream("hikari.properties");
                properties.load(inputStream);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (properties.containsKey("ex.printSQL")) {
                printSQL = Boolean.valueOf("true".equals(properties.getProperty("ex.printSQL").toLowerCase()));
            }
            if (properties.containsKey("ex.writeLog")) {
                writeLog = Boolean.valueOf("true".equals(properties.getProperty("ex.writeLog").toLowerCase()));
            }
            username = properties.getProperty("username");
            properties.setProperty("username", DESUtils.decrypt(username));
            password = properties.getProperty("password");
            properties.setProperty("password", DESUtils.decrypt(password));
            HikariConfigEx config = HikariConfigEx.getInstance(properties).addPlugin();
            ds = new HikariDataSource(config);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private DbUtils() {
    }

    public static Connection getConnection() {
        maxActiveConnections = Math.max(maxActiveConnections, ds.getHikariPoolMXBean().getActiveConnections());
        TransactionUtil tsu = tl.get();
        if (null == tsu || !tsu.isStartTransaction()) {
            try {
                Connection conn = ds.getConnection();
                return conn;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return tsu.getConnection();
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x002f, code lost:
    
        if (r0 == null) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static long getUuid() {
        /*
            Method dump skipped, instructions count: 251
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zht.db.DbUtils.getUuid():long");
    }

    public static boolean IsAutoTrans(Connection con) {
        try {
            return con.getAutoCommit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取连接的AutoCommit失败...", e);
        }
    }

    public static void beginTransaction(boolean enabledTransaction, Method method) throws RuntimeException {
        if (tl.get() == null) {
            TransactionUtil tsu = new TransactionUtil();
            if (enabledTransaction) {
                try {
                    Connection conn = ds.getConnection();
                    conn.setAutoCommit(false);
                    tsu.setConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("设置AutoCommit失败...", e);
                }
            }
            tsu.setMethod(method);
            tsu.setStartTransaction(enabledTransaction);
            tl.set(tsu);
        }
    }

    public static boolean isStartTransaction() {
        TransactionUtil tsu = tl.get();
        if (null == tsu) {
            return false;
        }
        return tsu.isStartTransaction();
    }

    public static void commitTransaction(Method m) {
        TransactionUtil tsu = tl.get();
        if (tsu != null) {
            try {
                Connection conn = tsu.getConnection();
                if (tsu.isStartTransaction() && !IsAutoTrans(conn) && tsu.hasFullExecute(m)) {
                    conn.commit();
                }
            } catch (SQLException e) {
                throw new RuntimeException("提交事务失败...", e);
            }
        }
    }

    public static void rollbackTransaction() {
        TransactionUtil tsu = tl.get();
        if (tsu != null) {
            try {
                Connection conn = tsu.getConnection();
                if (tsu.isStartTransaction() && conn != null && !IsAutoTrans(conn)) {
                    conn.rollback();
                }
            } catch (SQLException e) {
                throw new RuntimeException("回滚事务失败...", e);
            }
        }
    }

    public static void endTransaction(Method m) {
        TransactionUtil tsu = tl.get();
        if (tsu != null) {
            try {
                if (tsu.hasFullExecute(m)) {
                    Connection conn = tsu.getConnection();
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                    tl.remove();
                    tl.set(null);
                }
            } catch (SQLException e) {
                throw new RuntimeException("事务回滚失败...", e);
            }
        }
    }

    public static void close(Connection conn) {
        TransactionUtil tsu = tl.get();
        if (tsu != null) {
            try {
                if (!tsu.isStartTransaction() && conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("关闭连接失败...", e);
            }
        }
    }

    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet resultSet, Statement statement) {
        close(resultSet, statement, null);
    }

    public static void close(Statement statement, Connection conn) {
        close(null, statement, conn);
    }

    public static void close(ResultSet resultSet, Connection conn) {
        close(resultSet, null, conn);
    }

    public static void close(ResultSet rs, Statement st, Connection conn) {
        close(rs);
        close(st);
        close(conn);
    }

    public static void closeDs() {
        ds.close();
    }
}
