package com.dmj.daoimpl.sysConfig;

import cn.hutool.core.convert.Convert;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.configration.ConfigField;
import com.zht.db.StreamMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

/* loaded from: sysConfigDaoImpl.class */
public class sysConfigDaoImpl {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();

    public List<ConfigField> getConfig() {
        return this.dao.queryBeanList("select * from configration order by convert(confKey using gbk)", ConfigField.class);
    }

    public List getConfigKey() {
        List<ConfigField> config = getConfig();
        List<String> list = new ArrayList<>();
        for (ConfigField configration : config) {
            String key = configration.getConfKey();
            list.add(key);
        }
        return list;
    }

    public int[] updateConfig(List<ConfigField> sysconfigrationList) {
        return this.dao.batchUpdate("configration", sysconfigrationList, true);
    }

    public int[] insertConfig(List<ConfigField> configrationList) {
        return this.dao.batchSave("configration", configrationList);
    }

    public void updateConffConfig() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String path = request.getSession().getServletContext().getRealPath("/");
        String filePath = path + "WEB-INF/classes/conff.properties";
        Props props = PropsUtil.get(filePath);
        List<Map<String, String>> list = (List) props.entrySet().stream().map(item -> {
            Map<String, String> map = StreamMap.create().put("confKey", (Object) Convert.toStr(item.getKey(), "")).put("confValue", (Object) Convert.toStr(item.getValue(), ""));
            return map;
        }).collect(Collectors.toList());
        this.dao._batchUpdate("update configration set confValue={confValue} where confKey={confKey}", list);
    }
}
