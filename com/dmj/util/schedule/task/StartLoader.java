package com.dmj.util.schedule.task;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import com.dmj.auth.util.MD5;
import com.dmj.util.Const;
import com.dmj.util.StaticClassResources;
import com.dmj.util.config.Configuration;
import com.taobao.arthas.agent.attach.ArthasAgent;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/* loaded from: StartLoader.class */
public class StartLoader {
    public static Setting SingleLoginSetting = null;
    public static Props TkResource = null;
    public static Props XkwLoginResource = null;
    public static Props DaTiKaSetting = null;

    public static void init() {
        loadSkinSetting(null);
        loadSingleLoginSetting();
        Configuration.getInstance();
        loadTkResource();
        loadXkwLoginResource();
        loadDatTiKaConff();
        startArthasAgent();
    }

    public static String loadSkinSetting(String group) {
        Setting setting = new Setting("skin.ini", Charset.forName("UTF8"), false);
        StaticClassResources.SkinSetting.clear();
        if (group == null) {
            group = (String) setting.getMap("default").get(Const.skin);
        }
        Map<String, String> map = setting.getMap(group);
        if (map.size() == 0) {
            return "指定的皮肤不存在，请检查！";
        }
        StaticClassResources.SkinSetting = (Map) ObjectUtil.cloneByStream(map);
        return "加载皮肤成功！";
    }

    public static String loadSingleLoginSetting() {
        Setting setting = new Setting("singleLogin.ini", Charset.forName("UTF8"), true);
        setting.autoLoad(true, flag -> {
            SingleLoginSetting = setting;
        });
        SingleLoginSetting = setting;
        return "加载单点登录配置文件成功！";
    }

    public static String loadTkResource() {
        Props props = new Props("tk.properties");
        TkResource = props;
        return "题库配置文件加载成功！！！";
    }

    public static String loadXkwLoginResource() {
        Props props = new Props("zujuan.properties");
        XkwLoginResource = props;
        return "组卷配置文件加载成功！！！";
    }

    public static String startArthasAgent() {
        HashMap<String, String> configMap = new HashMap<>();
        String agentId = "edei" + StaticClassResources.EdeiInfo.getSystemId();
        String password = new MD5().getMD5ofStr(StaticClassResources.EdeiInfo.getPid()).toLowerCase();
        String password2 = password.substring(password.length() - 6);
        configMap.put("arthas.username", "arthas");
        configMap.put("arthas.password", password2);
        configMap.put("arthas.telnetPort", "-1");
        configMap.put("arthas.httpPort", "-1");
        configMap.put("arthas.appName", agentId);
        configMap.put("arthas.agentId", agentId);
        configMap.put("arthas.tunnelServer", StaticClassResources.EdeiInfo.getArthasUrl());
        ArthasAgent.attach(configMap);
        System.out.println("arthas agent 启动成功,appName:" + agentId + "P" + password2);
        return "arthas agent 启动成功,appName:" + agentId;
    }

    public static String loadDatTiKaConff() {
        Props props = new Props("datika.properties");
        DaTiKaSetting = props;
        return "答题卡配置文件加载成功！！！";
    }
}
