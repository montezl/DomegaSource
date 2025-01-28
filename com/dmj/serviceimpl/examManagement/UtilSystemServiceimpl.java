package com.dmj.serviceimpl.examManagement;

import cn.hutool.core.util.StrUtil;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.service.examManagement.UtilSystemService;
import java.util.HashMap;
import java.util.Map;

/* loaded from: UtilSystemServiceimpl.class */
public class UtilSystemServiceimpl implements UtilSystemService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();

    @Override // com.dmj.service.examManagement.UtilSystemService
    public int getAutoID(String tableName, String columnName) {
        Map args = new HashMap();
        args.put("tableName", tableName);
        int id = 0;
        if (StrUtil.isNotEmpty(this.dao2._queryStr("SELECT /* shard_host_HG=Write */ id_val FROM id_record WHERE  tableName={tableName} ", args))) {
            id = this.dao2._queryInt("SELECT /* shard_host_HG=Write */ id_val FROM id_record WHERE  tableName={tableName} ", args).intValue();
        }
        if (id == 0) {
            String countsql = " select /* shard_host_HG=Write */ IFNULL( max(" + columnName + "),0) from " + tableName;
            Integer counts = this.dao2._queryInt(countsql, args);
            if (-1 == counts.intValue()) {
                counts = 1;
            }
            id = counts.intValue();
            args.put("counts", counts);
            this.dao2._execute("INSERT INTO id_record(id_val,tableName) VALUES({counts},{tableName})", args);
        }
        int id2 = id + 1;
        args.put("id", Integer.valueOf(id2));
        if (id2 != 0) {
            this.dao2._execute("UPDATE id_record SET id_val={id} WHERE tableName={tableName}", args);
        }
        return id2;
    }

    @Override // com.dmj.service.examManagement.UtilSystemService
    public Long getAutoID_long(String tableName, String columnName) {
        Map args = new HashMap();
        args.put("tableName", tableName);
        long id = 0;
        if (StrUtil.isNotEmpty(this.dao2._queryStr("SELECT id_val FROM id_record WHERE  tableName={tableName} ", args))) {
            id = this.dao2._queryLong("SELECT id_val FROM id_record WHERE  tableName={tableName} ", args).longValue();
        }
        if (id == 0) {
            String str = " select  IFNULL( max(" + columnName + "),0) from " + tableName;
            Long counts = this.dao2._queryLong("SELECT id_val FROM id_record WHERE  tableName={tableName} ", args);
            if (-1 == counts.longValue()) {
                counts = 1L;
            }
            args.put("counts", counts);
            id = counts.longValue();
            this.dao2._execute("INSERT INTO id_record(id_val,tableName) VALUES({counts},{tableName})", args);
        }
        long id2 = id + 1;
        args.put("id", Long.valueOf(id2));
        if (id2 != 0) {
            this.dao2._execute("UPDATE id_record SET id_val={id} WHERE tableName={tableName} ", args);
        }
        return Long.valueOf(id2);
    }
}
