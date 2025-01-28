package com.dmj.cs.util;

import cn.hutool.core.util.StrUtil;
import com.dmj.cs.bean.CsDefine;
import com.dmj.cs.service.HessianService;
import com.dmj.cs.serviceimpl.HessianServiceImpl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zht.db.ServiceFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: DefineCache.class */
public class DefineCache {
    private static HessianService hessianService = (HessianService) ServiceFactory.getObject(new HessianServiceImpl());
    private static final Lock lock = new ReentrantLock();
    private static Cache<String, Object> cache = CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(30, TimeUnit.MINUTES).initialCapacity(20).removalListener(new 1()).build();

    public static Map<String, CsDefine> get(String examPaperNum, boolean removeCache) {
        lock.lock();
        if (removeCache) {
            try {
                remove(examPaperNum);
            } catch (Throwable th) {
                lock.unlock();
                throw th;
            }
        }
        Map<String, CsDefine> map = (Map) getVal(examPaperNum);
        if (map == null) {
            map = hessianService.getTemplateNeedCsDefineMap(examPaperNum);
            if (map == null || map.size() == 0) {
                throw new RuntimeException(StrUtil.format("{} 对应的双向细目表记录为空", new Object[]{examPaperNum}));
            }
            put(examPaperNum, map);
        }
        Map<String, CsDefine> map2 = map;
        lock.unlock();
        return map2;
    }

    public static Object getVal(String key) {
        if (StrUtil.isNotEmpty(key)) {
            return cache.getIfPresent(key);
        }
        return null;
    }

    public static void put(String key, Object value) {
        if (StrUtil.isNotEmpty(key) && value != null) {
            cache.put(key, value);
        }
    }

    public static void remove(String key) {
        if (StrUtil.isNotEmpty(key)) {
            cache.invalidate(key);
        }
    }

    public static void remove(List<String> keys) {
        if (keys != null && keys.size() > 0) {
            cache.invalidateAll(keys);
        }
    }

    public static void invalidateAll() {
        cache.invalidateAll();
    }
}
