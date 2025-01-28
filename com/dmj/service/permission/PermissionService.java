package com.dmj.service.permission;

import java.util.List;
import java.util.Map;

/* loaded from: PermissionService.class */
public interface PermissionService {
    int updateLastLoginTime(int i);

    List<String> getStudentPermission(String str, String str2);

    Object get_studentTime_warn(String str, String str2, Integer num);

    List<Map> getClientOrders(String str, String str2, String str3, String str4, String str5);
}
