package com.dmj.service.userManagement;

import java.util.List;
import java.util.Map;

/* loaded from: PermissonService.class */
public interface PermissonService {
    Object getOneByNum(String str, String str2, Class cls) throws Exception;

    Object deleteOneByNum(String str, String str2, Class cls) throws Exception;

    List getList(String str, String str2, Class cls) throws Exception;

    boolean hasPermission(String str, String str2);

    Map<String, Object> getAllResourceUrl();

    String haveUrl(String str, String str2);

    String haveUrl2(String str);
}
