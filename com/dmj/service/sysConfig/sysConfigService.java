package com.dmj.service.sysConfig;

import com.dmj.domain.configration.ConfigField;
import com.zht.db.Transaction;
import java.util.List;

/* loaded from: sysConfigService.class */
public interface sysConfigService {
    List<ConfigField> getConfig();

    List<ConfigField> getConfigKey();

    @Transaction
    String updateConfig(List<ConfigField> list);

    @Transaction
    void insertConfig(List<ConfigField> list);

    @Transaction
    void updateConffConfig();
}
