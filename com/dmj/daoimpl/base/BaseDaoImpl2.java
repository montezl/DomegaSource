package com.dmj.daoimpl.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.dmj.util.Const;
import com.sun.istack.internal.NotNull;
import com.zht.db.DbUtils;
import com.zht.db.IgnoreCaseLinkedHashMap;
import com.zht.db.RowArg;
import com.zht.db.TypeEnum;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.dbutils.DbRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/* loaded from: BaseDaoImpl2.class */
public class BaseDaoImpl2<T, K, V> {
    protected DbRunner qr = new DbRunner();

    public int execute(String sql) {
        try {
            return this.qr.update(getConnection(), sql);
        } catch (Exception e) {
            throw new MyException(e, sql);
        }
    }

    public int execute(String sql, Object[] params) {
        return update(sql, params);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> int _execute(String sql, Object obj) {
        return _update(sql, obj);
    }

    public int update(String sql, Object[] params) {
        try {
            return this.qr.update(getConnection(), sql, params);
        } catch (Exception e) {
            throw new MyException(e, sql);
        }
    }

    public <T> int _update(String sql, T arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return this.qr.update(conn, Args.access$000(args), Args.access$100(args));
        } catch (Exception e) {
            throw new MyException(e, sql);
        }
    }

    public <T> int save(T obj) {
        return save(null, obj);
    }

    public <T> int save(String tableName, T obj) {
        String sql = "";
        Connection conn = null;
        Statement state = null;
        ResultSet rs = null;
        try {
            try {
                conn = getConnection();
                Map<String, Object> beanMap = getPropertys(obj);
                String tableName2 = tableName == null ? obj.getClass().getSimpleName() : tableName;
                StringBuffer sbf1 = new StringBuffer("insert into " + tableName2 + " (");
                StringBuffer sbf2 = new StringBuffer(") values(");
                List<Object> objects = new ArrayList<>();
                state = conn.createStatement();
                rs = state.executeQuery("select * from " + tableName2 + " where 1=-1");
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String columnName = rsmd.getColumnName(i + 1);
                    if (!rsmd.isAutoIncrement(i + 1) || null != beanMap.get(columnName)) {
                        sbf1.append(columnName + Const.STRING_SEPERATOR);
                        sbf2.append("?,");
                        objects.add(beanMap.get(columnName));
                    }
                }
                sql = sbf1.substring(0, sbf1.length() - 1) + sbf2.substring(0, sbf2.length() - 1) + ")";
                int update = this.qr.update(conn, sql, objects.toArray());
                DbUtils.close(rs, state, conn);
                return update;
            } catch (Exception e) {
                throw new MyException(e, sql, JSON.toJSONString(obj));
            }
        } catch (Throwable th) {
            DbUtils.close(rs, state, conn);
            throw th;
        }
    }

    public <T> Integer update(T obj) {
        return update((BaseDaoImpl2<T, K, V>) obj, true);
    }

    public <T> Integer update(T obj, boolean allowedUpdateNull) {
        try {
            try {
                Connection conn = getConnection();
                Map<String, Object> beanMap = getPropertys(obj);
                String tableName = obj.getClass().getSimpleName().toUpperCase();
                DatabaseMetaData dbmd = conn.getMetaData();
                ResultSet rs = dbmd.getPrimaryKeys(conn.getCatalog(), null, tableName);
                ArrayList arrayList = new ArrayList();
                StringBuffer where = new StringBuffer("where 1=1 ");
                while (rs.next()) {
                    String col = rs.getString(4);
                    Object oo = beanMap.get(col);
                    if (null == oo) {
                        throw new RuntimeException("主键信息为空 ...");
                    }
                    where.append(" and " + col + "=? ");
                    arrayList.add(beanMap.get(col));
                }
                String tj = "update " + tableName + " set ";
                StringBuffer sbf1 = new StringBuffer();
                List<Object> objects = new ArrayList<>();
                DatabaseMetaData m_DBMetaData = conn.getMetaData();
                ResultSet colRet = m_DBMetaData.getColumns(conn.getCatalog(), null, tableName, "%");
                while (colRet.next()) {
                    String columnName = colRet.getString("COLUMN_NAME");
                    Object value = beanMap.get(columnName);
                    if (allowedUpdateNull || value != null) {
                        sbf1.append(columnName + "=? , ");
                        objects.add(value);
                    }
                }
                if (0 == sbf1.length()) {
                    throw new RuntimeException("没有需要更新的信息 ...");
                }
                String sql = tj + sbf1.substring(0, sbf1.length() - 2) + ((Object) where);
                objects.addAll(arrayList);
                Integer valueOf = Integer.valueOf(this.qr.update(conn, sql, objects.toArray()));
                DbUtils.close(colRet);
                DbUtils.close(rs, conn);
                return valueOf;
            } catch (Exception e) {
                throw new MyException(e, "", JSON.toJSONString(obj));
            }
        } catch (Throwable th) {
            DbUtils.close((ResultSet) null);
            DbUtils.close((ResultSet) null, (Connection) null);
            throw th;
        }
    }

    public <T> Integer update(String tableName, T obj, boolean allowedUpdateNull) {
        try {
            try {
                Connection conn = getConnection();
                Map<String, Object> beanMap = getPropertys(obj);
                String tableName2 = tableName == null ? obj.getClass().getSimpleName() : tableName;
                DatabaseMetaData dbmd = conn.getMetaData();
                ResultSet rs = dbmd.getPrimaryKeys(conn.getCatalog(), null, tableName2);
                ArrayList arrayList = new ArrayList();
                StringBuffer where = new StringBuffer("where 1=1 ");
                while (rs.next()) {
                    String col = rs.getString(4);
                    Object oo = beanMap.get(col);
                    if (null == oo) {
                        throw new RuntimeException("主键信息为空 ...");
                    }
                    where.append(" and " + col + "=? ");
                    arrayList.add(beanMap.get(col));
                }
                String tj = "update " + tableName2 + " set ";
                StringBuffer sbf1 = new StringBuffer();
                List<Object> objects = new ArrayList<>();
                DatabaseMetaData m_DBMetaData = conn.getMetaData();
                ResultSet colRet = m_DBMetaData.getColumns(conn.getCatalog(), null, tableName2, "%");
                while (colRet.next()) {
                    String columnName = colRet.getString("COLUMN_NAME");
                    Object value = beanMap.get(columnName);
                    if (allowedUpdateNull || value != null) {
                        sbf1.append(columnName + "=? , ");
                        objects.add(value);
                    }
                }
                if (0 == sbf1.length()) {
                    throw new RuntimeException("没有需要更新的信息 ...");
                }
                String sql = tj + sbf1.substring(0, sbf1.length() - 2) + ((Object) where);
                objects.addAll(arrayList);
                Integer valueOf = Integer.valueOf(this.qr.update(conn, sql, objects.toArray()));
                DbUtils.close(colRet);
                DbUtils.close(rs, conn);
                return valueOf;
            } catch (Exception e) {
                throw new MyException(e, "", JSON.toJSONString(obj));
            }
        } catch (Throwable th) {
            DbUtils.close((ResultSet) null);
            DbUtils.close((ResultSet) null, (Connection) null);
            throw th;
        }
    }

    public <T> int[] batchSave(List<T> list) {
        return batchSave((String) null, list);
    }

    /* JADX WARN: Type inference failed for: r0v35, types: [java.lang.Object[], java.lang.Object[][]] */
    public <T> int[] batchSave(String tableName, List<T> list) {
        try {
            try {
                int len = list.size();
                if (len == 0) {
                    throw new RuntimeException("没有需要更新的信息 ...");
                }
                Connection conn = getConnection();
                T t = list.get(0);
                if (tableName == null) {
                    tableName = t.getClass().getSimpleName();
                }
                StringBuffer sbf1 = new StringBuffer("insert into " + tableName + " (");
                StringBuffer sbf2 = new StringBuffer(") values(");
                List<String> columns = new ArrayList<>();
                Map<String, Object> beanMap0 = getPropertys(t);
                Statement state = conn.createStatement();
                ResultSet rs = state.executeQuery("select * from " + tableName + " where 1=-1");
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String columnName = rsmd.getColumnName(i + 1);
                    if (!rsmd.isAutoIncrement(i + 1) || null != beanMap0.get(columnName)) {
                        sbf1.append(columnName + Const.STRING_SEPERATOR);
                        sbf2.append("?,");
                        columns.add(columnName);
                    }
                }
                String sql = sbf1.substring(0, sbf1.length() - 1) + sbf2.substring(0, sbf2.length() - 1) + ")";
                ?? r0 = new Object[len];
                for (int i2 = 0; i2 < len; i2++) {
                    Map<String, Object> beanMap = getPropertys(list.get(i2));
                    List<Object> objects = new ArrayList<>();
                    for (String column : columns) {
                        objects.add(beanMap.get(column));
                    }
                    r0[i2] = objects.toArray();
                }
                int[] batch = this.qr.batch(conn, sql, (Object[][]) r0);
                DbUtils.close(rs, state, conn);
                return batch;
            } catch (Exception e) {
                throw new MyException(e, "", JSON.toJSONString(list));
            }
        } catch (Throwable th) {
            DbUtils.close(null, null, null);
            throw th;
        }
    }

    public <T> void batchSave(List<T> list, int step) {
        batchSave(null, list, step);
    }

    public <T> void batchSave(String tableName, List<T> list, int step) {
        String sql = "";
        int len = list.size();
        if (null == list || list.size() == 0) {
            throw new RuntimeException("没有需要保存的信息 ...");
        }
        if (step > len) {
            step = len;
        }
        PreparedStatement st = null;
        Connection conn = null;
        ResultSet rs = null;
        Statement state = null;
        try {
            try {
                conn = DbUtils.getConnection();
                T t = list.get(0);
                if (tableName == null) {
                    tableName = t.getClass().getSimpleName();
                }
                getPropertys(t);
                StringBuffer sbf1 = new StringBuffer("insert into " + tableName + " (");
                StringBuffer sbf2 = new StringBuffer(") values(");
                List<Object> ColNameList = new ArrayList<>();
                String sql0 = "select * from " + tableName + " where 1=-1";
                Map<String, Object> beanMap0 = getPropertys(t);
                state = conn.createStatement();
                rs = state.executeQuery(sql0);
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String columnName = rsmd.getColumnName(i + 1);
                    if (!rsmd.isAutoIncrement(i + 1) || null != beanMap0.get(columnName)) {
                        sbf1.append("`" + columnName + "`,");
                        sbf2.append("?,");
                        ColNameList.add(columnName);
                    }
                }
                sql = sbf1.substring(0, sbf1.length() - 1) + sbf2.substring(0, sbf2.length() - 1) + ")";
                st = conn.prepareStatement(sql);
                for (int i2 = 0; i2 < list.size(); i2++) {
                    Map<String, Object> beanMap = getPropertys(list.get(i2));
                    for (int j = 0; j < ColNameList.size(); j++) {
                        st.setObject(j + 1, beanMap.get(ColNameList.get(j)));
                    }
                    st.addBatch();
                    if (i2 % step == 0) {
                        st.executeBatch();
                        st.clearBatch();
                    }
                }
                st.executeBatch();
                DbUtils.close(rs, st);
                DbUtils.close(state, conn);
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        throw new RuntimeException(e1);
                    }
                }
                throw new MyException(e, sql, JSON.toJSONString(list));
            }
        } catch (Throwable th) {
            DbUtils.close(rs, st);
            DbUtils.close(state, conn);
            throw th;
        }
    }

    public <T> int[] batchUpdate(List<T> list) {
        return batchUpdate(list, true);
    }

    /* JADX WARN: Type inference failed for: r0v48, types: [java.lang.Object[], java.lang.Object[][]] */
    public <T> int[] batchUpdate(String tableName, List<T> list, boolean allowedUpdateNull) {
        try {
            try {
                int len = list.size();
                if (len == 0) {
                    throw new RuntimeException("没有需要更新的信息 ...");
                }
                Connection conn = getConnection();
                T t = list.get(0);
                if (StrUtil.isEmpty(tableName)) {
                    tableName = t.getClass().getSimpleName().toUpperCase();
                }
                Map<String, Object> beanMap0 = getPropertys(t);
                DatabaseMetaData dbmd = conn.getMetaData();
                ResultSet rs = dbmd.getPrimaryKeys(conn.getCatalog(), null, tableName);
                StringBuffer where = new StringBuffer("where 1=1 ");
                String tj = "update " + tableName + " set ";
                StringBuffer sbf1 = new StringBuffer();
                List<String> colList = new ArrayList<>();
                while (rs.next()) {
                    String col = rs.getString(4);
                    Object oo = beanMap0.get(col);
                    if (null == oo) {
                        throw new RuntimeException("主键信息为空 ...");
                    }
                    where.append(" and " + col + "=? ");
                    colList.add(col);
                }
                DatabaseMetaData m_DBMetaData = conn.getMetaData();
                ResultSet colRet = m_DBMetaData.getColumns(conn.getCatalog(), "%", tableName, "%");
                List<String> columns = new ArrayList<>();
                while (colRet.next()) {
                    String columnName = colRet.getString("COLUMN_NAME");
                    Object value = beanMap0.get(columnName);
                    if (allowedUpdateNull || value != null) {
                        sbf1.append(columnName + "=? , ");
                        columns.add(columnName);
                    }
                }
                if (0 == sbf1.toString().length()) {
                    throw new RuntimeException("没有需要更新的信息 ...");
                }
                String sql = tj + sbf1.substring(0, sbf1.length() - 2) + ((Object) where);
                ?? r0 = new Object[len];
                for (int i = 0; i < len; i++) {
                    Map<String, Object> beanMap = getPropertys(list.get(i));
                    List<Object> objects = new ArrayList<>();
                    List<Object> whereOjbect = new ArrayList<>();
                    for (int j = 0; j < colList.size(); j++) {
                        whereOjbect.add(beanMap.get(colList.get(j)));
                    }
                    for (String column : columns) {
                        Object value2 = beanMap.get(column);
                        if (null != value2) {
                            objects.add(beanMap.get(column));
                        }
                    }
                    objects.addAll(whereOjbect);
                    r0[i] = objects.toArray();
                }
                int[] batch = this.qr.batch(conn, sql, (Object[][]) r0);
                DbUtils.close(colRet);
                DbUtils.close(rs, conn);
                return batch;
            } catch (SQLException e) {
                throw new MyException(e, "", JSON.toJSONString(list));
            }
        } catch (Throwable th) {
            DbUtils.close((ResultSet) null);
            DbUtils.close((ResultSet) null, (Connection) null);
            throw th;
        }
    }

    public <T> int[] batchUpdate(List<T> list, boolean allowedUpdateNull) {
        return batchUpdate(null, list, allowedUpdateNull);
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [java.lang.Object[], java.lang.Object[][]] */
    public <T> int[] _batchUpdate(String sql, List<T> list) {
        int len = list.size();
        if (len == 0) {
            return new int[0];
        }
        Connection conn = getConnection();
        ?? r0 = new Object[list.size()];
        String cloneSql = "";
        for (int i = 0; i < list.size(); i++) {
            try {
                T arg = list.get(i);
                BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
                r0[i] = Args.access$100(args);
                if (i == 0) {
                    cloneSql = Args.access$000(args);
                }
            } catch (Exception e) {
                throw new MyException(e, sql, JSON.toJSONString(list));
            }
        }
        return this.qr.batch(conn, cloneSql, (Object[][]) r0);
    }

    /* JADX WARN: Type inference failed for: r0v22, types: [java.lang.Object[], java.lang.Object[][]] */
    public <T> Integer[] _batchUpdate(String sql, List<T> list, int step) {
        int len = list.size();
        if (len == 0) {
            return new Integer[0];
        }
        List<List<T>> allSqls = CollUtil.splitList(list, step);
        Connection conn = getConnection();
        List<Integer> rtvList = new ArrayList<>();
        String cloneSql = "";
        try {
            for (List<T> subList : allSqls) {
                ?? r0 = new Object[subList.size()];
                for (int i = 0; i < subList.size(); i++) {
                    T arg = subList.get(i);
                    BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
                    r0[i] = Args.access$100(args);
                    if (i == 0) {
                        cloneSql = Args.access$000(args);
                    }
                }
                int[] rtv = this.qr.batch(conn, cloneSql, (Object[][]) r0);
                rtvList.addAll((Collection) Arrays.stream(rtv).boxed().collect(Collectors.toList()));
            }
            return (Integer[]) rtvList.toArray(new Integer[rtvList.size()]);
        } catch (Exception e) {
            throw new MyException(e, sql, JSON.toJSONString(list));
        }
    }

    public <T> Integer[] _batchExecute(String sql, List<T> list, int step) {
        return _batchUpdate(sql, list, step);
    }

    public <T> int[] _batchExecute(String sql, List<T> list) {
        return _batchUpdate(sql, list);
    }

    public int[] batchExecute(List<String> sqls) {
        Statement st = null;
        Connection conn = null;
        StringBuffer sbf = new StringBuffer();
        try {
            try {
                conn = getConnection();
                st = conn.createStatement();
                for (String sql : sqls) {
                    st.addBatch(sql);
                    sbf.append(sql + "\r\n");
                }
                int[] executeBatch = st.executeBatch();
                DbUtils.close(st, conn);
                return executeBatch;
            } catch (SQLException e) {
                throw new MyException(e, sbf.toString());
            }
        } catch (Throwable th) {
            DbUtils.close(st, conn);
            throw th;
        }
    }

    public int[] batchExecute(String sql, Object[][] args) {
        try {
            Connection conn = getConnection();
            return this.qr.batch(conn, sql, args);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(args));
        }
    }

    /* JADX WARN: Type inference failed for: r0v26, types: [java.lang.Object[], java.lang.Object, java.lang.Object[][]] */
    public int[] batchExecuteByLimit(String sql, Object[][] args, int step) {
        try {
            Connection conn = DbUtils.getConnection();
            if (args.length <= step) {
                return this.qr.batch(conn, sql, args);
            }
            int length = args.length;
            int num = ((length + step) - 1) / step;
            List<Integer> returnList = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                int fromIndex = i * step;
                int toIndex = (i + 1) * step < length ? (i + 1) * step : length;
                ?? r0 = new Object[toIndex - fromIndex];
                System.arraycopy(args, fromIndex, r0, 0, r0.length);
                int[] v = this.qr.batch(conn, sql, (Object[][]) r0);
                CollUtil.addAll(returnList, v);
            }
            return returnList.stream().mapToInt((v0) -> {
                return Integer.valueOf(v0);
            }).toArray();
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(args));
        }
    }

    public void batchExecuteByLimit(List<String> sqls, int step) {
        Statement st = null;
        Connection conn = null;
        StringBuffer sbf = new StringBuffer();
        try {
            try {
                conn = DbUtils.getConnection();
                st = conn.createStatement();
                for (int i = 0; i < sqls.size(); i++) {
                    if (DbUtils.printSQL.booleanValue()) {
                    }
                    st.addBatch(sqls.get(i));
                    sbf.append(sqls.get(i) + "\r\n");
                    if ((i + 1) % step == 0) {
                        st.executeBatch();
                        st.clearBatch();
                    }
                }
                if (sqls.size() % step != 0) {
                    st.executeBatch();
                }
                DbUtils.close(st, conn);
            } catch (SQLException e) {
                throw new MyException(e, sbf.toString());
            }
        } catch (Throwable th) {
            DbUtils.close(st, conn);
            throw th;
        }
    }

    public void _batchExecuteByLimit(List<String> sqls, int step) {
        Statement st = null;
        Connection conn = null;
        StringBuffer sbf = new StringBuffer();
        try {
            try {
                conn = DbUtils.getConnection();
                st = conn.createStatement();
                for (int i = 0; i < sqls.size(); i++) {
                    if (DbUtils.printSQL.booleanValue()) {
                    }
                    st.addBatch(sqls.get(i));
                    sbf.append(sqls.get(i) + "\r\n");
                    if ((i + 1) % step == 0) {
                        st.executeBatch();
                        st.clearBatch();
                    }
                }
                if (sqls.size() % step != 0) {
                    st.executeBatch();
                }
                DbUtils.close(st, conn);
            } catch (SQLException e) {
                throw new MyException(e, sbf.toString());
            }
        } catch (Throwable th) {
            DbUtils.close(st, conn);
            throw th;
        }
    }

    public int[] _batchExecute(List<String> sqls, @NotNull Object arg) {
        int[] rtv = new int[0];
        try {
            if (CollUtil.isNotEmpty(sqls)) {
                rtv = new int[sqls.size()];
                int sqlsSize = sqls.size();
                for (int i = 0; i < sqlsSize; i++) {
                    String sql = sqls.get(i);
                    rtv[i] = _execute(sql, arg);
                }
            }
            return rtv;
        } catch (Exception e) {
            throw new MyException(e, JSON.toJSONString(sqls), JSON.toJSONString(arg));
        }
    }

    public void _batchExecute(List<String> sqls, @NotNull List<Object> argList) {
        try {
            if (CollUtil.isNotEmpty(sqls)) {
                List<RowArg> rowArgList = new ArrayList<>();
                int sqlsSize = sqls.size();
                for (int i = 0; i < sqlsSize; i++) {
                    String sql = sqls.get(i);
                    Object arg = CollUtil.get(argList, i);
                    rowArgList.add(new RowArg(sql, arg));
                }
                _batchExecute(rowArgList);
            }
        } catch (Exception e) {
            throw new MyException(e, JSON.toJSONString(sqls), JSON.toJSONString(argList));
        }
    }

    public void _batchExecute(List<RowArg> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        try {
            Map<String, List<RowArg>> map = (Map) list.stream().collect(Collectors.groupingBy(rowArg -> {
                return rowArg.getSql();
            }));
            map.forEach((k, v) -> {
                _batchExecute(k, (List) v.stream().map(r -> {
                    return r.getArg();
                }).collect(Collectors.toList()));
            });
        } catch (Exception e) {
            throw new MyException(e, JSON.toJSONString(list));
        }
    }

    public void _batchExecute(List<RowArg> list, int step) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        try {
            Map<String, List<RowArg>> map = (Map) list.stream().collect(Collectors.groupingBy(rowArg -> {
                return rowArg.getSql();
            }));
            map.forEach((k, v) -> {
                _batchExecute(k, (List) v.stream().map(r -> {
                    return r.getArg();
                }).collect(Collectors.toList()), step);
            });
        } catch (Exception e) {
            throw new MyException(e, JSON.toJSONString(list));
        }
    }

    public LinkedHashMap<String, LinkedHashMap<String, Object>> query2OrderMap(String sql, String key) {
        return query2OrderMap(sql, key, null);
    }

    public LinkedHashMap<String, LinkedHashMap<String, Object>> query2OrderMap(String sql, String key, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (LinkedHashMap) this.qr.query(conn, sql, new 1(this, key), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> LinkedHashMap<String, LinkedHashMap<String, Object>> _query2OrderMap(String sql, String key, T arg) {
        Connection conn = getConnection();
        try {
            2 r0 = new 2(this, key);
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (LinkedHashMap) this.qr.query(conn, Args.access$000(args), r0, Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public Map<String, Object[]> queryArrayMap(String sql, String key) {
        return queryArrayMap(sql, key, null);
    }

    public Map<String, Object[]> queryArrayMap(String sql, String key, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (Map) this.qr.query(conn, sql, new 3(this, key), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> Map<String, Object[]> _queryArrayMap(String sql, String key, T arg) {
        Connection conn = getConnection();
        try {
            4 r0 = new 4(this, key);
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (Map) this.qr.query(conn, Args.access$000(args), r0, Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public Map<String, Map<String, Object>> query2Map(String sql, String key) {
        return query2Map(sql, key, null);
    }

    public Map<String, Map<String, Object>> query2Map(String sql, String key, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (Map) this.qr.query(conn, sql, new 5(this, key), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> Map<String, Map<String, Object>> _query2Map(String sql, String key, T arg) {
        Connection conn = getConnection();
        try {
            6 r0 = new 6(this, key);
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (Map) this.qr.query(conn, Args.access$000(args), r0, Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public List<Object[]> queryArrayList(String sql) {
        return queryArrayList(sql, null);
    }

    public List<Object[]> queryArrayList(String sql, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (List) this.qr.query(conn, sql, new ArrayListHandler(), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> List<T[]> _queryArrayList(String sql, Class<T> cls, Object arg) {
        List<Object[]> objects = _queryArrayList(sql, arg);
        if (CollUtil.isNotEmpty(objects)) {
            return (List) objects.stream().map(arr -> {
                return CollUtil.toList(arr).stream().map(a -> {
                    return Convert.convert(cls, a);
                }).toArray();
            }).collect(Collectors.toList());
        }
        return new ArrayList();
    }

    public <T> List<Object[]> _queryArrayList(String sql, Object arg) {
        Connection conn = getConnection();
        ArrayListHandler arrayListHandler = new ArrayListHandler();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (List) this.qr.query(conn, Args.access$000(args), arrayListHandler, Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> List<T[]> _queryArrayList(String sql, Class<T> cls, Object arg, int from, int count) {
        List<Object[]> objects = _queryArrayList(sql, arg, from, count);
        if (CollUtil.isNotEmpty(objects)) {
            return (List) objects.stream().map(arr -> {
                return CollUtil.toList(arr).stream().map(a -> {
                    return Convert.convert(cls, a);
                }).toArray();
            }).collect(Collectors.toList());
        }
        return new ArrayList();
    }

    public <T> List<Object[]> _queryArrayList(String sql, Object arg, int from, int count) {
        Connection conn = getConnection();
        ArrayListHandler arrayListHandler = new ArrayListHandler();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg, from, count);
            return (List) this.qr.query(conn, Args.access$000(args), arrayListHandler, Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public Object[] queryArray(String sql) {
        return queryArray(sql, null);
    }

    public Object[] queryArray(String sql, Object[] arg) {
        Connection conn = getConnection();
        try {
            Object[] rtbv = (Object[]) this.qr.query(conn, sql, new ArrayHandler(), arg);
            if (rtbv.length == 0) {
                return null;
            }
            return rtbv;
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> Object[] _queryArray(String sql, T arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            Object[] rtbv = (Object[]) this.qr.query(conn, Args.access$000(args), new ArrayHandler(), Args.access$100(args));
            if (rtbv.length == 0) {
                return null;
            }
            return rtbv;
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> List<T> queryBeanList(String sql, Class<T> cls) {
        return queryBeanList(sql, cls, null);
    }

    public <T> List<T> queryBeanList(String sql, Class<T> cls, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (List) this.qr.query(conn, sql, new BeanListHandler(cls), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> List<T> _queryBeanList(String sql, Class<T> cls, Object arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (List) this.qr.query(conn, Args.access$000(args), new BeanListHandler(cls), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> List<T> _queryBeanList(String sql, Class<T> cls, Object arg, int from, int count) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg, from, count);
            return (List) this.qr.query(conn, Args.access$000(args), new BeanListHandler(cls), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public List<Map<String, Object>> queryMapList(String sql) {
        return queryMapList(sql, null);
    }

    public List<Map<String, Object>> queryMapList(String sql, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (List) this.qr.query(conn, sql, new MapListHandler(), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <K, V> List<Map<String, V>> _queryMapList(String sql, TypeEnum typeEnum, Object arg) {
        Connection conn = getConnection();
        Type V = Object.class;
        if (typeEnum != null) {
            try {
                V = typeEnum.getV();
            } catch (Exception e) {
                throw new MyException(e, sql);
            }
        }
        Type finalV = V;
        BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
        return (List) this.qr.query(conn, Args.access$000(args), rs -> {
            ArrayList arrayList = new ArrayList();
            while (rs.next()) {
                IgnoreCaseLinkedHashMap ignoreCaseLinkedHashMap = new IgnoreCaseLinkedHashMap();
                ResultSetMetaData rsmd = rs.getMetaData();
                int cols = rsmd.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    String columnName = rsmd.getColumnLabel(i);
                    if (null == columnName || 0 == columnName.length()) {
                        columnName = rsmd.getColumnName(i);
                    }
                    ignoreCaseLinkedHashMap.put(columnName, Convert.convert(finalV, rs.getObject(i)));
                }
                arrayList.add(ignoreCaseLinkedHashMap);
            }
            return arrayList;
        }, Args.access$100(args));
    }

    public <K, V> List<Map<String, V>> _queryMapList(String sql, TypeEnum typeEnum, Object arg, int from, int count) {
        Connection conn = getConnection();
        Type V = Object.class;
        if (typeEnum != null) {
            try {
                V = typeEnum.getV();
            } catch (Exception e) {
                throw new MyException(e, sql);
            }
        }
        Type finalV = V;
        BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg, from, count);
        return (List) this.qr.query(conn, Args.access$000(args), rs -> {
            ArrayList arrayList = new ArrayList();
            while (rs.next()) {
                IgnoreCaseLinkedHashMap ignoreCaseLinkedHashMap = new IgnoreCaseLinkedHashMap();
                ResultSetMetaData rsmd = rs.getMetaData();
                int cols = rsmd.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    String columnName = rsmd.getColumnLabel(i);
                    if (null == columnName || 0 == columnName.length()) {
                        columnName = rsmd.getColumnName(i);
                    }
                    ignoreCaseLinkedHashMap.put(columnName, Convert.convert(finalV, rs.getObject(i)));
                }
                arrayList.add(ignoreCaseLinkedHashMap);
            }
            return arrayList;
        }, Args.access$100(args));
    }

    public Map<String, Object> querySimpleMap(String sql) {
        return querySimpleMap(sql, null);
    }

    public Map<String, Object> querySimpleMap(String sql, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (Map) this.qr.query(conn, sql, new MapHandler(), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <K, V> Map<String, V> _querySimpleMap(String sql, Object arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (Map) this.qr.query(conn, Args.access$000(args), new MapHandler(), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> T queryBean(String str, Class<T> cls, Object[] objArr) {
        try {
            return (T) this.qr.query(getConnection(), str, new BeanHandler(cls), objArr);
        } catch (SQLException e) {
            throw new MyException(e, str, JSON.toJSONString(objArr));
        }
    }

    public <T> T _queryBean(String str, Class<T> cls, Object obj) {
        Connection connection = getConnection();
        try {
            Args map2Array = map2Array(str, obj);
            return (T) this.qr.query(connection, Args.access$000(map2Array), new BeanHandler(cls), Args.access$100(map2Array));
        } catch (SQLException e) {
            throw new MyException(e, str, JSON.toJSONString(obj));
        }
    }

    public Object queryObject(String sql) {
        return queryObject(sql, Object.class, null);
    }

    public <T> T queryObject(String sql, Class<T> cls) {
        return queryObject(sql, cls, null);
    }

    public Object queryObject(String sql, Object[] arg) {
        Connection conn = getConnection();
        try {
            return this.qr.query(conn, sql, new ScalarHandler(), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> T queryObject(String str, Class<T> cls, Object[] objArr) {
        try {
            return (T) this.qr.query(getConnection(), str, new ScalarHandler(), objArr);
        } catch (SQLException e) {
            throw new MyException(e, str, JSON.toJSONString(objArr));
        }
    }

    public Object _queryObject(String sql, Object arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return this.qr.query(conn, Args.access$000(args), new ScalarHandler(), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> T _queryObject(String str, Class<T> cls, Object obj) {
        Connection connection = getConnection();
        try {
            Args map2Array = map2Array(str, obj);
            return (T) this.qr.query(connection, Args.access$000(map2Array), new ScalarHandler(), Args.access$100(map2Array));
        } catch (SQLException e) {
            throw new MyException(e, str, JSON.toJSONString(obj));
        }
    }

    public byte[] _queryBlob(String sql, Object arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (byte[]) this.qr.query(conn, Args.access$000(args), new ScalarHandler(), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public byte[] queryBlob(String sql, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (byte[]) this.qr.query(conn, sql, new ScalarHandler(), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public String _queryStr(String sql, Object arg) {
        return Convert.toStr(_queryObject(sql, arg));
    }

    public String queryStr(String sql, Object[] arg) {
        return Convert.toStr(queryObject(sql, arg));
    }

    public Integer _queryInt(String sql, Object arg) {
        return Convert.toInt(_queryObject(sql, arg));
    }

    public Integer queryInt(String sql, Object[] arg) {
        return Convert.toInt(queryObject(sql, arg));
    }

    public Float _queryFloat(String sql, Object arg) {
        return Convert.toFloat(_queryObject(sql, arg));
    }

    public Float queryFloat(String sql, Object[] arg) {
        return Convert.toFloat(queryObject(sql, arg));
    }

    public Double _queryDouble(String sql, Object arg) {
        return Convert.toDouble(_queryObject(sql, arg));
    }

    public Double queryDouble(String sql, Object[] arg) {
        return Convert.toDouble(queryObject(sql, arg));
    }

    public Long _queryLong(String sql, Object arg) {
        return Convert.toLong(_queryObject(sql, arg));
    }

    public Long queryLong(String sql, Object[] arg) {
        return Convert.toLong(queryObject(sql, arg));
    }

    public BigDecimal _queryBigDecimal(String sql, Object arg) {
        return Convert.toBigDecimal(_queryObject(sql, arg));
    }

    public BigDecimal queryBigDecimal(String sql, Object[] arg) {
        return Convert.toBigDecimal(queryObject(sql, arg));
    }

    public Boolean _queryBoolean(String sql, Object arg) {
        return Convert.toBool(_queryObject(sql, arg));
    }

    public Boolean queryBoolean(String sql, Object[] arg) {
        return Convert.toBool(queryObject(sql, arg));
    }

    public <T> List<T> queryColList(String sql) {
        return queryColList(sql, null);
    }

    public <T> List<T> queryColList(String sql, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (List) this.qr.query(conn, sql, new ColumnListHandler(), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public List<byte[]> queryBlobList(String sql, Object[] arg) {
        Connection conn = getConnection();
        try {
            return (List) this.qr.query(conn, sql, new ColumnListHandler(), arg);
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public List<byte[]> _queryBlobList(String sql, Object arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (List) this.qr.query(conn, Args.access$000(args), new ColumnListHandler(), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public List<byte[]> _queryBlobList(String sql, Object arg, int from, int count) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg, from, count);
            return (List) this.qr.query(conn, Args.access$000(args), new ColumnListHandler(), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> List<T> _queryColList(String sql, Class<T> cls, Object arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (List) this.qr.query(conn, Args.access$000(args), new ColumnListHandler(), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public <T> List<T> _queryColList(String sql, Class<T> cls, Object arg, int from, int count) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg, from, count);
            return (List) this.qr.query(conn, Args.access$000(args), new ColumnListHandler(), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public List<Object> _queryColList(String sql, Object arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (List) this.qr.query(conn, Args.access$000(args), new ColumnListHandler(), Args.access$100(args));
        } catch (SQLException e) {
            throw new MyException(e, sql, JSON.toJSONString(arg));
        }
    }

    public void insert(String table, String[] fields, Object[] values) {
        Connection conn = getConnection();
        String sql = "insert into " + table + " (";
        String vl = "";
        for (String str : fields) {
            sql = sql + str + Const.STRING_SEPERATOR;
            vl = vl + "?,";
        }
        String sql2 = sql.substring(0, sql.length() - 1) + ") value (" + vl.substring(0, vl.length() - 1) + ")";
        try {
            this.qr.update(conn, sql2, values);
        } catch (SQLException e) {
            throw new MyException(e, sql2, JSON.toJSONString(values));
        }
    }

    public void batchInsert(String table, String[] fields, List<Object[]> values) {
        Connection conn = getConnection();
        String sql = "insert into " + table + " (";
        String vl = "";
        for (String str : fields) {
            sql = sql + str + Const.STRING_SEPERATOR;
            vl = vl + "?,";
        }
        String sql2 = sql.substring(0, sql.length() - 1) + ") value (" + vl.substring(0, vl.length() - 1) + ")";
        try {
            this.qr.batch(conn, sql2, (Object[][]) values.toArray(new Object[values.size()]));
        } catch (SQLException e) {
            throw new MyException(e, sql2, JSON.toJSONString(values));
        }
    }

    public <K, V> LinkedHashMap<K, V> queryOrderMap(String sql, TypeEnum typeEnum, Object[] values) {
        Connection conn = getConnection();
        try {
            return (LinkedHashMap) this.qr.query(conn, sql, rs -> {
                IgnoreCaseLinkedHashMap ignoreCaseLinkedHashMap = new IgnoreCaseLinkedHashMap();
                while (rs.next()) {
                    ignoreCaseLinkedHashMap.put(Convert.convert(typeEnum.getK(), rs.getObject(1)), Convert.convert(typeEnum.getV(), rs.getObject(2)));
                }
                return ignoreCaseLinkedHashMap;
            }, values);
        } catch (Exception e) {
            throw new MyException(e, sql);
        }
    }

    public <K, V> LinkedHashMap<K, V> _queryOrderMap(String sql, TypeEnum typeEnum, Object arg) {
        Connection conn = getConnection();
        try {
            BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
            return (LinkedHashMap) this.qr.query(conn, Args.access$000(args), rs -> {
                IgnoreCaseLinkedHashMap ignoreCaseLinkedHashMap = new IgnoreCaseLinkedHashMap();
                while (rs.next()) {
                    ignoreCaseLinkedHashMap.put(Convert.convert(typeEnum.getK(), rs.getObject(1)), Convert.convert(typeEnum.getV(), rs.getObject(2)));
                }
                return ignoreCaseLinkedHashMap;
            }, Args.access$100(args));
        } catch (Exception e) {
            throw new MyException(e, sql);
        }
    }

    public List<List<Map<String, Object>>> _queryMoreResultList(String sql, Object arg) {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<List<Map<String, Object>>> list = new ArrayList<>();
        try {
            try {
                BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, arg);
                conn = getConnection();
                stmt = conn.prepareCall(Args.access$000(args));
                this.qr.fillStatement(stmt, Args.access$100(args));
                for (boolean hadResults = stmt.execute(); hadResults; hadResults = stmt.getMoreResults()) {
                    List<Map<String, Object>> oneResultCol = new ArrayList<>();
                    rs = stmt.getResultSet();
                    while (rs != null && rs.next()) {
                        Map<String, Object> result = new IgnoreCaseLinkedHashMap<>();
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int cols = rsmd.getColumnCount();
                        for (int i = 1; i <= cols; i++) {
                            String columnName = rsmd.getColumnLabel(i);
                            if (null == columnName || 0 == columnName.length()) {
                                columnName = rsmd.getColumnName(i);
                            }
                            result.put(columnName, rs.getObject(i));
                        }
                        oneResultCol.add(result);
                    }
                    list.add(oneResultCol);
                }
                DbUtils.close(rs, stmt, conn);
                return list;
            } catch (Exception e) {
                throw new MyException(e, sql, JSON.toJSONString(arg));
            }
        } catch (Throwable th) {
            DbUtils.close(rs, stmt, conn);
            throw th;
        }
    }

    /* JADX WARN: Incorrect inner types in method signature: <T:Ljava/lang/Object;>(Ljava/lang/String;TT;)Lcom/dmj/daoimpl/base/BaseDaoImpl2<TT;TK;TV;>.Args; */
    /* JADX WARN: Multi-variable type inference failed */
    private Args map2Array(String sql, Object obj) {
        Map<String, Object> m;
        if (obj == 0) {
            return new Args(this, sql, (Object[]) null);
        }
        if (obj instanceof Map) {
            m = (Map) obj;
        } else {
            m = getPropertys(obj);
        }
        String cloneSql = sql;
        List<Object> argList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String old = matcher.group();
            String key = matcher.group(1).trim();
            String replaceStr = "?";
            Object value = m.get(key);
            if (key.lastIndexOf("[]") > -1) {
                if (value == null && key.length() > 2) {
                    value = m.get(key.substring(0, key.length() - 2));
                }
                if (value == null) {
                    argList.add(value);
                } else if (value instanceof String) {
                    String[] args = value.toString().split(Const.STRING_SEPERATOR);
                    Collections.addAll(argList, args);
                    replaceStr = fillWenHao(args.length);
                } else if (value.getClass().isArray()) {
                    Object[] args2 = (Object[]) value;
                    Collections.addAll(argList, args2);
                    replaceStr = fillWenHao(args2.length);
                } else if (value instanceof Collection) {
                    Collection args3 = (Collection) value;
                    Collections.addAll(argList, args3);
                    replaceStr = fillWenHao(args3.size());
                }
            } else {
                argList.add(value);
            }
            cloneSql = cloneSql.replace(old, replaceStr);
        }
        Object[] values = argList.toArray();
        return new Args(this, cloneSql, values);
    }

    private String fillWenHao(int count) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append("?,");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /* JADX WARN: Incorrect inner types in method signature: <T:Ljava/lang/Object;>(Ljava/lang/String;TT;II)Lcom/dmj/daoimpl/base/BaseDaoImpl2<TT;TK;TV;>.Args; */
    private Args map2Array(String sql, Object obj, int from, int count) {
        BaseDaoImpl2<T, K, V>.Args args = map2Array(sql, obj);
        String limitSql = Args.access$000(args) + " limit ?,? ";
        Object[] argArray = Args.access$100(args) == null ? new Object[0] : Args.access$100(args);
        List<Object> argList = (List) Arrays.stream(argArray).collect(Collectors.toList());
        Collections.addAll(argList, Integer.valueOf(from), Integer.valueOf(count));
        return new Args(this, limitSql, argList.toArray());
    }

    private Connection getConnection() {
        return DbUtils.getConnection();
    }

    public <T> Map<String, Object> getPropertys(T object) {
        if (object instanceof Map) {
            return (Map) object;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            Class<?> objClass = object.getClass();
            Field[] fields = objClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(object));
            }
            return map;
        } catch (Exception e) {
            throw new MyException(e, "获取属性异常：" + JSON.toJSONString(object));
        }
    }

    public long getUuid() {
        return Long.valueOf(queryObject("SELECT UUID_SHORT()").toString()).longValue();
    }
}
