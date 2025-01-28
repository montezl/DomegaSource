package com.dmj.serviceimpl.sysConfig;

import com.dmj.daoimpl.sysConfig.sysConfigDaoImpl;
import com.dmj.domain.configration.ConfigField;
import com.dmj.service.sysConfig.sysConfigService;
import com.dmj.util.config.Configuration;
import com.dmj.util.msg.RspMsg;
import java.util.Iterator;
import java.util.List;

/* loaded from: sysConfigServiceImpl.class */
public class sysConfigServiceImpl implements sysConfigService {
    private sysConfigDaoImpl sysConfigDAO = new sysConfigDaoImpl();

    @Override // com.dmj.service.sysConfig.sysConfigService
    public List getConfig() {
        return this.sysConfigDAO.getConfig();
    }

    @Override // com.dmj.service.sysConfig.sysConfigService
    public List getConfigKey() {
        return this.sysConfigDAO.getConfigKey();
    }

    @Override // com.dmj.service.sysConfig.sysConfigService
    public String updateConfig(List<ConfigField> sysConfigList) {
        String errorMsg = null;
        Iterator<ConfigField> it = sysConfigList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ConfigField configField = it.next();
            String confKey = configField.getConfKey();
            RspMsg check = Configuration.getInstance().check(confKey, configField.getConfValue());
            if (check.isError()) {
                errorMsg = check.getMsg();
                break;
            }
        }
        if (errorMsg == null) {
            this.sysConfigDAO.updateConfig(sysConfigList);
            Configuration.reload();
            errorMsg = "恭喜，更新成功！";
        }
        return errorMsg;
    }

    @Override // com.dmj.service.sysConfig.sysConfigService
    public void insertConfig(List<ConfigField> configrationList) {
        this.sysConfigDAO.insertConfig(configrationList);
    }

    @Override // com.dmj.service.sysConfig.sysConfigService
    public void updateConffConfig() {
        this.sysConfigDAO.updateConffConfig();
    }
}
