package com.dmj.cs.util;

import cn.hutool.core.util.StrUtil;
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

/* loaded from: SingleDoubleMarkingCache.class */
public class SingleDoubleMarkingCache {
    private static HessianService hessianService = (HessianService) ServiceFactory.getObject(new HessianServiceImpl());
    private static final Lock lock = new ReentrantLock();
    private static Cache<String, Object> cache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(30, TimeUnit.MINUTES).initialCapacity(100).removalListener(new 1()).build();

    public static Map<String, Map<String, Object>> get(String examPaperNum) {
        lock.lock();
        try {
            Map<String, Map<String, Object>> map = (Map) getVal(examPaperNum);
            if (map == null || map.size() == 0) {
                map = hessianService.GetQuestionSingleOrDoubleMarkInfo(examPaperNum);
                if (map != null && map.size() != 0) {
                    put(examPaperNum, map);
                } else {
                    lock.unlock();
                    return null;
                }
            }
            Map<String, Map<String, Object>> map2 = map;
            lock.unlock();
            return map2;
        } catch (Throwable th) {
            lock.unlock();
            throw th;
        }
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
        lock.lock();
        try {
            if (StrUtil.isNotEmpty(key)) {
                cache.invalidate(key);
            }
            lock.unlock();
        } catch (Throwable th) {
            lock.unlock();
            throw th;
        }
    }

    public static void remove(List<String> keys) {
        if (keys != null && keys.size() > 0) {
            cache.invalidateAll(keys);
        }
    }

    public static synchronized void clear(String examPaperNum, String groupNum) {
        Map<String, Object> map;
        if (!StrUtil.isEmpty(examPaperNum) && !StrUtil.isEmpty(groupNum) && (map = (Map) cache.getIfPresent(examPaperNum)) != null && map.containsKey(groupNum)) {
            map.remove(groupNum);
        }
    }

    public static void invalidateAll() {
        cache.invalidateAll();
    }
}
