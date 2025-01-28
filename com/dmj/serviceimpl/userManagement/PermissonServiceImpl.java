package com.dmj.serviceimpl.userManagement;

import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.userManagement.UserDAOImpl;
import com.dmj.service.userManagement.PermissonService;
import com.zht.db.TypeEnum;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/* loaded from: PermissonServiceImpl.class */
public class PermissonServiceImpl implements PermissonService {
    BaseDaoImpl2<?, ?, ?> basedao = new BaseDaoImpl2<>();
    Logger log = Logger.getLogger(getClass());
    UserDAOImpl dao = new UserDAOImpl();

    @Override // com.dmj.service.userManagement.PermissonService
    public Object deleteOneByNum(String colum, String valule, Class cla) throws Exception {
        return this.dao.deleteOneByNum(colum, valule, cla);
    }

    @Override // com.dmj.service.userManagement.PermissonService
    public List getList(String colum, String valule, Class cla) throws Exception {
        return this.dao.getList(colum, valule, cla);
    }

    @Override // com.dmj.service.userManagement.PermissonService
    public Object getOneByNum(String colum, String valule, Class cla) throws Exception {
        return this.dao.getOneByNum(colum, valule, cla);
    }

    @Override // com.dmj.service.userManagement.PermissonService
    public boolean hasPermission(String userNum, String url) {
        if (null == userNum || null == url) {
            return false;
        }
        return this.dao.hasPermission(userNum, url);
    }

    @Override // com.dmj.service.userManagement.PermissonService
    public Map<String, Object> getAllResourceUrl() {
        try {
            return this.basedao._queryOrderMap("select url,name from resource", TypeEnum.StringObject, null);
        } catch (Exception e) {
            this.log.error("getAllResourceUrl()：获取所有资源url和name", e);
            e.printStackTrace();
            return null;
        }
    }

    public Integer saveOne(Object obj) {
        return Integer.valueOf(this.basedao.save(obj));
    }

    @Override // com.dmj.service.userManagement.PermissonService
    public String haveUrl(String userNum, String url) {
        return this.dao.haveUrl(userNum, url);
    }

    @Override // com.dmj.service.userManagement.PermissonService
    public String haveUrl2(String uri) {
        return this.dao.haveUrl2(uri);
    }
}
