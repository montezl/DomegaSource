package com.dmj.serviceimpl.systemManagement;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.systemManagement.SystemDAOImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Area;
import com.dmj.domain.AwardPoint;
import com.dmj.domain.Baseinfolog;
import com.dmj.domain.Data;
import com.dmj.domain.Examlog;
import com.dmj.domain.Log;
import com.dmj.domain.QuestionManger;
import com.dmj.domain.StatisticRelation;
import com.dmj.domain.Subject;
import com.dmj.domain.TreeData;
import com.dmj.domain.User;
import com.dmj.domain.config;
import com.dmj.domain.leq.Leiceng;
import com.dmj.domain.vo.Imgpath;
import com.dmj.service.questionGroup.QuestionGroupService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.questionGroup.QuestionGroupImpl;
import com.dmj.util.Const;
import com.dmj.util.ConstConfig;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.ImageUtil;
import com.zht.db.DbUtils;
import com.zht.db.RowArg;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import common.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;

/* loaded from: SystemServiceImpl.class */
public class SystemServiceImpl implements SystemService {
    BaseDaoImpl2<?, ?, ?> basedao = new BaseDaoImpl2<>();
    SystemDAOImpl dao = new SystemDAOImpl();
    Logger log = Logger.getLogger(getClass());
    public static SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    public static QuestionGroupService qgs = (QuestionGroupService) ServiceFactory.getObject(new QuestionGroupImpl());

    @Override // com.dmj.service.systemManagement.SystemService
    public Area getAeraInfo() throws Exception {
        List list = this.dao.getAeraInfo();
        if (null != list && list.size() > 0) {
            return (Area) list.get(0);
        }
        return null;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer saveOrUpdate(Area area) throws Exception {
        Area a = getAeraInfo();
        if (null == a) {
            return Integer.valueOf(this.basedao.save(area));
        }
        return this.basedao.update(area);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Data> getDataByType(String type) {
        if (null == type || type.trim().length() <= 0) {
            return null;
        }
        return this.dao.getDataByType(type);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer save(Object obj) throws Exception {
        return Integer.valueOf(this.basedao.save(obj));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer update(Object obj) throws Exception {
        return this.basedao.update(obj);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Object getOneByNum(String colum, String value, Class cla) throws Exception {
        return this.dao.getOneByNum(colum, value, cla);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getSchools() {
        return this.dao.getSchools();
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getSchools(String schoolName) {
        return this.dao.getSchools(schoolName);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getexportschool(String rootPath, String schoolName, String userId) {
        this.log.info("-----------导出学校信息--------------");
        List<Map<String, Object>> list = getexportschool2(schoolName);
        this.log.info("-----------导出学校信息--------------");
        String folderPath = "ExportFolder/export_excel_school" + userId;
        String excelName = "" == schoolName ? Const.EXAM_EXPORT + "_全部学校列表" : Const.EXAM_EXPORT + "_包含”" + schoolName + "“的学校列表";
        String filePath = folderPath + "/" + excelName + ".xlsx";
        FileUtil.del(rootPath + filePath);
        this.log.info("---导出学校信息---文件【" + filePath + "】#######################开始导出 ####################### ");
        ExcelWriter writer = ExcelUtil.getWriter(rootPath + filePath);
        writer.addHeaderAlias(Const.EXPORTREPORT_schoolNum, "学校编号");
        writer.addHeaderAlias("schoolName", "学校全称");
        writer.addHeaderAlias("shortname", "学校简称");
        writer.addHeaderAlias("areaName", "所属区域");
        writer.addHeaderAlias("name", "学校类型");
        writer.addHeaderAlias("schoolAddress", "学校地址");
        writer.addHeaderAlias("description", "备注");
        for (int i = 0; i < 6; i++) {
            if (i > 1) {
                writer.setColumnWidth(i, 40);
            }
            writer.setColumnWidth(i, 15);
        }
        writer.getCellStyle().setWrapText(true);
        writer.setOnlyAlias(true);
        writer.write(list);
        writer.close();
        this.log.info("---导出学校信息---文件【" + filePath + "】#######################导出结束 ####################### ");
        return filePath;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> getexportschool2(String schoolName) {
        String sql;
        Map args = new HashMap();
        if (null == schoolName || schoolName.equals("")) {
            sql = "SELECT s1.schoolNum ,s1.schoolName, s1.shortname,s1.areaName ,s1.schoolType  ,s1.schoolAddress ,s1.description ,d.`name`from  (SELECT s.schoolNum ,s.schoolName, s.shortname,a.areaName ,s.schoolType  ,s.schoolAddress ,s.description  from (SELECT schoolNum ,schoolName, shortname,schoolType,schoolAddress,description,areaNum from school )s  ";
        } else {
            sql = "SELECT s1.schoolNum ,s1.schoolName, s1.shortname,s1.areaName ,s1.schoolType  ,s1.schoolAddress ,s1.description ,d.`name`from  (SELECT s.schoolNum ,s.schoolName, s.shortname,a.areaName ,s.schoolType  ,s.schoolAddress ,s.description  from (SELECT schoolNum ,schoolName, shortname,schoolType,schoolAddress,description,areaNum from school where  schoolName like {schoolName} )s  ";
            args.put("schoolName", "%" + schoolName + "%");
        }
        return this.basedao._queryMapList(sql + "LEFT JOIN area a on a.areaNum =s.areaNum ) s1 LEFT JOIN `data` d on s1.schoolType=d.`value` ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getSchoolQuotaList(String schoolName, String groupNum) {
        return this.dao.getSchoolQuotaList(schoolName, groupNum);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer addschoolquota(String examPaperNum, String groupNum, String schoolNumStr, String loginName) {
        String str = "";
        String[] groupNumArr = groupNum.split("_");
        String[] schoolNumArr = schoolNumStr.split(Const.STRING_SEPERATOR);
        for (String str2 : schoolNumArr) {
            for (String str3 : groupNumArr) {
                Map args = new HashMap();
                args.put("examPaperNum", examPaperNum);
                args.put("groupNum", str3);
                args.put(Const.EXPORTREPORT_schoolNum, str2);
                args.put("loginName", loginName);
                str = str + this.basedao._execute("INSERT INTO `schoolquota` (`id`,`exampaperNum`, `groupNum`, `schoolNum`, `insertUser`, `insertDate`) VALUES (UUID_SHORT(),{examPaperNum} , {groupNum} , {schoolNum} ,{loginName}, now()) ON DUPLICATE KEY UPDATE updateUser={loginName} ,updateDate=now() ", args);
            }
        }
        if (str.indexOf("0") != -1) {
            return 0;
        }
        return 1;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer delschoolquota(String examPaperNum, String groupNum, String schoolNumStr, String loginName) {
        String str = "";
        String[] groupNumArr = groupNum.split("_");
        String[] schoolNumArr = schoolNumStr.split(Const.STRING_SEPERATOR);
        for (String str2 : schoolNumArr) {
            for (String str3 : groupNumArr) {
                Map args = new HashMap();
                args.put("groupNum", str3);
                args.put("schoolnum", str2);
                str = str + this.basedao._execute("delete from schoolquota where groupNum={groupNum}  and schoolnum={schoolnum} ", args);
            }
        }
        if (str.indexOf("0") != -1) {
            return 0;
        }
        return 1;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void updateEnforce(String enforce) {
        Map args = StreamMap.create().put("enforce", (Object) enforce);
        this.basedao._execute("update schoolgroup set enforce={enforce} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String querySchoolIfDivide() {
        String num = this.basedao.queryStr("SELECT ifnull(count(1),0) from school s LEFT JOIN schoolgroup sg on s.id=sg.schoolNum where s.isDelete='F' and sg.id is null", null);
        return num;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String fenzu(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        String num = this.basedao._queryStr("SELECT fenzuyuejuan from exampaper where examPaperNum={examPaperNum} ", args);
        if ("1".equals(num)) {
            String sgId = this.basedao._queryStr("select sg.id from schoolgroup sg left join school sch on sch.id=sg.schoolNum where sch.isDelete='F' limit 1", null);
            if (StrUtil.isEmpty(sgId)) {
                num = "0";
            }
        }
        return num;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String checkgroupSchoolGroupInfo(String groupNum) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        Map args = StreamMap.create().put("newGroupNum", (Object) newGroupNum.substring(1, newGroupNum.length()));
        String info = this.basedao._queryStr("SELECT GROUP_CONCAT(s1.schoolGroupName) from(  SELECT DISTINCT schoolGroupNum,schoolGroupName FROM schoolgroup  )s1 LEFT JOIN(   SELECT DISTINCT slg.schoolGroupNum from schoolquota sq INNER JOIN schoolgroup slg on sq.schoolNum=slg.schoolNum where sq.groupNum in ({newGroupNum[]})  )s2 on s1.schoolGroupNum=s2.schoolGroupNum   where s2.schoolGroupNum is null", args);
        String exampaperNum = this.basedao._queryStr("select distinct examPaperNum from questiongroup where groupNum in ({newGroupNum[]}) ", args);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            info = "";
        }
        return info;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map<String, Object> checkteacherquota(String groupNum) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        Map args = StreamMap.create().put("newGroupNum", (Object) newGroupNum.substring(1, newGroupNum.length()));
        return this.basedao._queryOrderMap("SELECT sq.schoolNum,sum(qo.num) from schoolquota sq  INNER JOIN questiongroup_user qu on sq.groupNum=qu.groupNum  INNER JOIN user u on qu.userNum=u.id and sq.schoolNum=u.schoolnum INNER JOIN quota qo on qu.groupNum=qo.groupNum and qu.userNum=qo.insertUser where sq.groupNum in ({newGroupNum[]}) and qu.userType<>2 GROUP BY sq.schoolNum", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map<String, Object> checkteacherquotaGroup(String groupNum, String schoolGroupNum) {
        String sql;
        String[] groupNumArr = groupNum.split("_");
        Map args = StreamMap.create().put("groupNum", (Object) groupNumArr[0]).put("schoolGroupNum", (Object) schoolGroupNum);
        if ("-2".equals(schoolGroupNum)) {
            sql = "select -2 schoolGroupNum,IFNULL(sum(qo.num),0) userTotalnum from user u left join questiongroup_user qgu on qgu.userNum=u.id and qgu.groupNum={groupNum} and qgu.userType<>2 left join quota qo on qo.groupNum=qgu.groupNum and qo.insertUser=qgu.userNum ";
        } else {
            String sgWStr = "-1".equals(schoolGroupNum) ? " where sg.schoolGroupNum is null " : " where sg.schoolGroupNum={schoolGroupNum} ";
            sql = "select IFNULL(sg.schoolGroupNum,-1) schoolGroupNum,IFNULL(sum(qo.num),0) userTotalnum from user u left join questiongroup_user qgu on qgu.userNum=u.id and qgu.groupNum={groupNum} and qgu.userType<>2 left join quota qo on qo.groupNum=qgu.groupNum and qo.insertUser=qgu.userNum left join schoolgroup sg on sg.schoolNum=u.schoolnum " + sgWStr;
        }
        return this.basedao._queryOrderMap(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map checkteachercount(String groupNum) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        Map args = StreamMap.create().put("newGroupNum", (Object) newGroupNum.substring(1, newGroupNum.length()));
        return this.basedao._queryOrderMap("SELECT sq.schoolNum,ifnull(qu.count,0) from schoolquota sq LEFT JOIN(  SELECT u.schoolNum,count(1)count from questiongroup_user qu left JOIN user u on qu.userNum=u.id where qu.groupNum in ({newGroupNum[]}) and qu.userType<>2 GROUP BY u.schoolNum )qu on sq.schoolNum=qu.schoolNum where sq.groupNum in ({newGroupNum[]}) ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map<String, Object> checkteachercountGroup(String groupNum, String schoolGroupNum) {
        String sql;
        String[] groupNumArr = groupNum.split("_");
        Map args = StreamMap.create().put("groupNum", (Object) groupNumArr[0]).put("schoolGroupNum", (Object) schoolGroupNum);
        if ("-2".equals(schoolGroupNum)) {
            sql = "select -2 schoolGroupNum,IFNULL(count(qgu.userNum),0) teaCount from user u left join questiongroup_user qgu on qgu.userNum=u.id and qgu.groupNum={groupNum} and qgu.userType<>2 ";
        } else {
            String sgWStr = "-1".equals(schoolGroupNum) ? " where sg.schoolGroupNum is null " : " where sg.schoolGroupNum={schoolGroupNum} ";
            sql = "select IFNULL(sg.schoolGroupNum,-1) schoolGroupNum,IFNULL(count(qgu.userNum),0) teaCount from user u left join questiongroup_user qgu on qgu.userNum=u.id and qgu.groupNum={groupNum} and qgu.userType<>2 left join schoolgroup sg on sg.schoolNum=u.schoolnum " + sgWStr;
        }
        return this.basedao._queryOrderMap(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map checktizuzhang(String groupNum) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        Map args = StreamMap.create().put("newGroupNum", (Object) newGroupNum.substring(1, newGroupNum.length()));
        return this.basedao._queryOrderMap("SELECT sq.schoolNum,GROUP_CONCAT(DISTINCT qu.userType) from schoolquota sq  INNER JOIN questiongroup_user qu on sq.groupNum=qu.groupNum  INNER JOIN user u on qu.userNum=u.id and sq.schoolNum=u.schoolnum where sq.groupNum in ({newGroupNum[]}) and qu.userType<>2 GROUP BY sq.schoolNum; ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map checktizuzhangGroup(String groupNum, String schoolGroupNum) {
        String sql;
        String[] groupNumArr = groupNum.split("_");
        Map args = StreamMap.create().put("groupNum", (Object) groupNumArr[0]).put("schoolGroupNum", (Object) schoolGroupNum);
        if ("-2".equals(schoolGroupNum)) {
            sql = "select -2 schoolGroupNum,IFNULL(count(qgu.userNum),0) tizuzhangCount from user u left join questiongroup_user qgu on qgu.userNum=u.id and qgu.groupNum={groupNum} and qgu.userType=1 ";
        } else {
            String sgWStr = "-1".equals(schoolGroupNum) ? " where sg.schoolGroupNum is null " : " where sg.schoolGroupNum={schoolGroupNum} ";
            sql = "select IFNULL(sg.schoolGroupNum,-1) schoolGroupNum,IFNULL(count(qgu.userNum),0) tizuzhangCount from user u left join questiongroup_user qgu on qgu.userNum=u.id and qgu.groupNum={groupNum} and qgu.userType=1 left join schoolgroup sg on sg.schoolNum=u.schoolnum " + sgWStr;
        }
        return this.basedao._queryOrderMap(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void updateschoolquota(String examPaperNum, String groupNum, String schooloruserNum, String num, String type, String loginNum) {
        String sql;
        String tablename;
        String num_old;
        String caozuo;
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        if (License.SCHOOL.equals(type)) {
            sql = "update schoolquota set num={num} where groupNum in ({newGroupNum[]}) and schoolNum={schooloruserNum} ";
            tablename = "schoolquota";
        } else {
            sql = "update quota set num={num}  where groupNum in ({newGroupNum[]}) and insertUser={schooloruserNum}";
            tablename = "quota";
        }
        Map args = StreamMap.create().put("exampaperNum", (Object) examPaperNum).put("newGroupNum", (Object) newGroupNum2).put("num", (Object) num).put("schooloruserNum", (Object) schooloruserNum).put("table", (Object) tablename).put(Const.LOGIN_USER, (Object) loginNum);
        for (String group : groupNumArr) {
            args.put("group", group);
            if (License.SCHOOL.equals(type)) {
                num_old = this.basedao._queryStr("select num from schoolquota where groupNum={group} and schoolNum={schooloruserNum}", args);
                caozuo = "学校任务量由" + num_old + "调整为" + num;
            } else {
                num_old = this.basedao._queryStr("select num from quota where groupNum={group} and insertUser={schooloruserNum}", args);
                caozuo = "教师任务量由" + num_old + "调整为" + num;
            }
            args.put("caozuo", caozuo);
            if (!num.equals(num_old)) {
                this.basedao._execute("INSERT INTO quotalog(`exampaperNum`, `groupNum`, `caozuo`,`usernum`, `updateUser`, `updateDate`)  VALUES ({exampaperNum}, {group}, {caozuo},{schooloruserNum}, {loginuser}, NOW()); ", args);
            }
        }
        this.basedao._execute(sql, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer divideequalquota(String examPaperNum, String groupNum, String schoolNum, String loginNum) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        String sql = "SELECT num from schoolquota where  groupNum in ({newGroupNum[]})  and schoolNum={schoolNum} ";
        Double allTotalNum = this.basedao._queryDouble(sql, args);
        String sql2 = "SELECT distinct qu.userNum from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id where  qu.groupNum in ({newGroupNum[]})  and u.schoolNum={schoolNum} ";
        List list = this.basedao._queryColList(sql2, args);
        int avg = (int) Math.ceil(allTotalNum.doubleValue() / list.size());
        String h = "";
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < groupNumArr.length; j++) {
                int finalI = i;
                int finalJ = j;
                Map args2 = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("groupNum", (Object) groupNumArr[finalJ]).put("avg", (Object) Integer.valueOf(avg)).put("insertUser", list.get(finalI));
                h = h + this.basedao._execute("INSERT INTO `quota` (`exampaperNum`, `groupNum`, `num`, `insertUser`, `insertDate`) values({examPaperNum},{groupNum},{avg},{insertUser},now() ) ON DUPLICATE KEY UPDATE num={avg} ", args2);
            }
        }
        if (h.indexOf("0") != -1) {
            return 0;
        }
        return 1;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getdivideequalquota(String examPaperNum, String groupNum, String schoolNum, String loginNum) {
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        String sql = "SELECT num from schoolquota where  groupNum in ({newGroupNum[]})  and schoolNum={schoolNum} ";
        Double allTotalNum = this.basedao._queryDouble(sql, args);
        String sql2 = "SELECT distinct qu.userNum from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id where  qu.groupNum in ({newGroupNum[]})  and u.schoolNum={schoolNum} ";
        List list = this.basedao._queryColList(sql2, args);
        int avg = (int) Math.ceil(allTotalNum.doubleValue() / list.size());
        List resultlist = new ArrayList();
        resultlist.add(Integer.valueOf(avg));
        resultlist.add(Integer.valueOf(avg * list.size()));
        return resultlist;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void divideequalquotaGroup(String examPaperNum, String groupNum, String schoolGroupNum, String groupQuota) {
        String[] groupNumArr = groupNum.split("_");
        Map args = new HashMap();
        args.put("groupNum", groupNumArr[0]);
        args.put("schoolGroupNum", schoolGroupNum);
        String sgWStr = "-1".equals(schoolGroupNum) ? " and sg.schoolGroupNum is null " : " and sg.schoolGroupNum={schoolGroupNum} ";
        String userSql = "SELECT distinct qu.userNum from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id left join schoolgroup sg on sg.schoolNum=u.schoolnum where qu.groupNum={groupNum} " + sgWStr;
        List list = this.basedao._queryColList(userSql, args);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        int avg = NumberUtil.ceilDiv(Convert.toInt(groupQuota, 0).intValue(), list.size());
        List<Map<String, Object>> paramList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (String str : groupNumArr) {
                Map args2 = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("groupNum", (Object) str).put("avg", (Object) Integer.valueOf(avg)).put("insertUser", list.get(i));
                paramList.add(args2);
            }
        }
        if (CollUtil.isNotEmpty(paramList)) {
            this.basedao._batchExecute("INSERT INTO `quota` (`exampaperNum`, `groupNum`, `num`, `insertUser`, `insertDate`) values({examPaperNum},{groupNum},{avg},{insertUser},now() ) ON DUPLICATE KEY UPDATE num={avg} ", paramList);
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getdivideequalquotaGroup(String examPaperNum, String groupNum, String schoolGroupNum, String groupQuota) {
        String userSql;
        String[] groupNumArr = groupNum.split("_");
        Map args = new HashMap();
        args.put("groupNum", groupNumArr[0]);
        args.put("schoolGroupNum", schoolGroupNum);
        if ("-2".equals(schoolGroupNum)) {
            userSql = "SELECT distinct qu.userNum from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id where qu.groupNum={groupNum} ";
        } else {
            String sgWStr = "-1".equals(schoolGroupNum) ? " and sg.schoolGroupNum is null " : " and sg.schoolGroupNum={schoolGroupNum} ";
            userSql = "SELECT distinct qu.userNum from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id left join schoolgroup sg on sg.schoolNum=u.schoolnum where qu.groupNum={groupNum} " + sgWStr;
        }
        List list = this.basedao._queryColList(userSql, args);
        List resultlist = new ArrayList();
        if (CollUtil.isEmpty(list)) {
            return resultlist;
        }
        int avg = NumberUtil.ceilDiv(Convert.toInt(groupQuota, 0).intValue(), list.size());
        resultlist.add(Integer.valueOf(avg));
        resultlist.add(Integer.valueOf(avg * list.size()));
        return resultlist;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> autoJisuanYingpanliang(String examPaperNum, String groupNum, String schoolGroupNum, String groupQuota) {
        String userSql;
        String sql4;
        String[] groupNumArr = groupNum.split("_");
        Map args = new HashMap();
        args.put("groupNum", groupNumArr[0]);
        args.put("schoolGroupNum", schoolGroupNum);
        String sgWStr = "-1".equals(schoolGroupNum) ? " and sg.schoolGroupNum is null " : " and sg.schoolGroupNum={schoolGroupNum} ";
        if ("-2".equals(schoolGroupNum)) {
            userSql = "SELECT qu.userNum,qo.num,u.schoolnum schoolNum from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id left join quota qo on qo.groupNum=qu.groupNum and qo.insertUser=qu.userNum  where qu.groupNum={groupNum}  group by qu.userNum";
        } else {
            userSql = "SELECT qu.userNum,qo.num,u.schoolnum schoolNum from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id left join schoolgroup sg on sg.schoolNum=u.schoolnum left join quota qo on qo.groupNum=qu.groupNum and qo.insertUser=qu.userNum  where qu.groupNum={groupNum} " + sgWStr + " group by qu.userNum";
        }
        List<Map<String, Object>> list = this.basedao._queryMapList(userSql, TypeEnum.StringObject, args);
        Object _category = this.basedao._queryObject("select category from define where id={groupNum} union all select category from subdefine where id={groupNum} ", args);
        args.put("category", _category);
        Map<String, Object> epMap = this.basedao._querySimpleMap("select ep1.examNum,ep1.gradeNum,ep1.subjectNum,ep1.isHidden,ep1.pexamPaperNum,ep1.examPaperNum,ep2.subjectNum psubjectNum from exampaper ep1 left join exampaper ep2 on ep2.examPaperNum=ep1.pexamPaperNum where ep1.examPaperNum={category}", args);
        epMap.put("groupNum", groupNumArr[0]);
        epMap.put("schoolGroupNum", schoolGroupNum);
        Map<String, Object> esgsMap = this.basedao._querySimpleMap("select isToSchool from examschoolgroupsetting where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and schoolGroupNum={schoolGroupNum}", epMap);
        List<Map<String, Object>> dbautoMapList = this.basedao._queryMapList("select groupNum,yuguSanpinglv,gongzuoliangZhanbi from distributeAuto where exampaperNum={pexamPaperNum} and subjectNum={subjectNum} ", TypeEnum.StringObject, epMap);
        List<Map<String, Object>> qmsMapList = this.basedao._queryMapList("SELECT groupNum,makType,judgetype from questiongroup_mark_setting WHERE exampaperNum={pexamPaperNum} ", TypeEnum.StringObject, epMap);
        List<Map<String, Object>> quotaMapList = this.basedao._queryMapList("select groupNum,insertUser,num from quota where exampaperNum={pexamPaperNum} ", TypeEnum.StringObject, epMap);
        BigDecimal currentGongzuoliangZhanbi = BigDecimal.valueOf(0L);
        Optional<Map<String, Object>> res = dbautoMapList.stream().filter(m -> {
            return groupNumArr[0].equals(Convert.toStr(m.get("groupNum")));
        }).findAny();
        if (res.isPresent()) {
            Map<String, Object> dbautoMap = res.get();
            Optional<Map<String, Object>> res2 = qmsMapList.stream().filter(m2 -> {
                return groupNumArr[0].equals(Convert.toStr(m2.get("groupNum")));
            }).findAny();
            if (res2.isPresent()) {
                Map<String, Object> qms = res2.get();
                dbautoMap.put("makType", "1".equals(qms.get("makType")) ? "1" : "0");
                dbautoMap.put("judgetype", Convert.toStr(qms.get("judgetype"), "0"));
            }
            int currentQuestionGroupAllWork_tishu = Convert.toInt(groupQuota, 0).intValue();
            currentGongzuoliangZhanbi = Convert.toBigDecimal(dbautoMap.get("gongzuoliangZhanbi"), BigDecimal.valueOf(100L));
            if ("1".equals(dbautoMap.get("makType"))) {
                BigDecimal yuguSanpinglv = Convert.toBigDecimal(dbautoMap.get("yuguSanpinglv"), BigDecimal.valueOf(0L)).divide(BigDecimal.valueOf(100L));
                BigDecimal quesZhanbi = BigDecimal.valueOf(2L).add(yuguSanpinglv);
                currentQuestionGroupAllWork_tishu = Convert.toInt(BigDecimal.valueOf(currentQuestionGroupAllWork_tishu).multiply(quesZhanbi).setScale(0, 0)).intValue();
                currentGongzuoliangZhanbi = currentGongzuoliangZhanbi.divide(quesZhanbi, 4, 4);
            }
            int currentQuestionGroupAassignedWork_tishu = list.stream().mapToInt(m3 -> {
                return Convert.toInt(m3.get("num"), 0).intValue();
            }).sum();
            int i = currentQuestionGroupAllWork_tishu - currentQuestionGroupAassignedWork_tishu;
        }
        if (currentGongzuoliangZhanbi.compareTo(BigDecimal.valueOf(0L)) <= 0) {
            return null;
        }
        String ur_subStr = "";
        if ("T".equals(Convert.toStr(epMap.get("isHidden")))) {
            ur_subStr = " inner join userrole_sub urs on urs.userNum=ur.userNum and urs.exampaperNum={examPaperNum} ";
        }
        if ("-2".equals(schoolGroupNum)) {
            sql4 = "select u.schoolnum schoolNum,ur.userNum,u.realname from userrole ur " + ur_subStr + "LEFT JOIN role r on r.roleNum=ur.roleNum LEFT JOIN `user` u on u.id=ur.userNum where r.examPaperNum={pexamPaperNum} and r.type='4' and u.id is not null  group by ur.userNum";
        } else {
            sql4 = "select u.schoolnum schoolNum,ur.userNum,u.realname from userrole ur " + ur_subStr + "LEFT JOIN role r on r.roleNum=ur.roleNum LEFT JOIN `user` u on u.id=ur.userNum left join schoolgroup sg on sg.schoolNum=u.schoolnum where r.examPaperNum={pexamPaperNum} and r.type='4' and u.id is not null " + sgWStr + " group by ur.userNum";
        }
        List<Map<String, Object>> allTeaList = this.basedao._queryMapList(sql4, TypeEnum.StringObject, epMap);
        Map<String, Object> schToCountMap = this.basedao._queryOrderMap("select schoolNum,count(studentId) schCount from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={psubjectNum} group by schoolNum", TypeEnum.StringObject, epMap);
        BigDecimal finalCurrentGongzuoliangZhanbi = currentGongzuoliangZhanbi;
        List<Map<String, Object>> addTeaList = (List) list.stream().filter(m4 -> {
            return Convert.toInt(m4.get("num"), 0).intValue() == 0;
        }).collect(Collectors.toList());
        addTeaList.forEach(oneTeaObj -> {
            BigDecimal currentTeacherAllWork_stu;
            String currentSchoolNum = Convert.toStr(oneTeaObj.get(Const.EXPORTREPORT_schoolNum), "");
            String currentUserNum = Convert.toStr(oneTeaObj.get("userNum"), "");
            BigDecimal.valueOf(0L);
            if (null != esgsMap && "0".equals(Convert.toStr(esgsMap.get("isToSchool")))) {
                BigDecimal groupStuNumber = Convert.toBigDecimal(groupQuota, BigDecimal.valueOf(0L));
                BigDecimal groupTeaNumber = Convert.toBigDecimal(Integer.valueOf(allTeaList.size()), BigDecimal.valueOf(1L));
                currentTeacherAllWork_stu = groupStuNumber.divide(groupTeaNumber, 4, 0);
            } else {
                BigDecimal schoolStuNumber = Convert.toBigDecimal(schToCountMap.get(currentSchoolNum), BigDecimal.valueOf(0L));
                BigDecimal schoolTeaNumber = Convert.toBigDecimal(Long.valueOf(allTeaList.stream().filter(m5 -> {
                    return currentSchoolNum.equals(Convert.toStr(m5.get(Const.EXPORTREPORT_schoolNum)));
                }).count()), BigDecimal.valueOf(1L));
                currentTeacherAllWork_stu = schoolStuNumber.divide(schoolTeaNumber, 4, 0);
            }
            BigDecimal[] currentTeacherAssignedWork_stu = {BigDecimal.valueOf(0L)};
            quotaMapList.stream().filter(m6 -> {
                return currentUserNum.equals(Convert.toStr(m6.get("insertUser"))) && Convert.toInt(m6.get("num"), 0).intValue() > 0;
            }).forEach(oneQuesGroupObj -> {
                String currentGroupNum = Convert.toStr(oneQuesGroupObj.get("groupNum"), "");
                Optional<Map<String, Object>> res3 = dbautoMapList.stream().filter(m7 -> {
                    return currentGroupNum.equals(Convert.toStr(m7.get("groupNum")));
                }).findAny();
                if (res3.isPresent()) {
                    Map<String, Object> autoMap = res3.get();
                    BigDecimal gongzuoliangZhanbi = Convert.toBigDecimal(autoMap.get("gongzuoliangZhanbi"), BigDecimal.valueOf(100L));
                    if (gongzuoliangZhanbi.compareTo(BigDecimal.valueOf(0L)) <= 0) {
                        return;
                    }
                    BigDecimal yuguSanpinglv2 = Convert.toBigDecimal(autoMap.get("yuguSanpinglv"), BigDecimal.valueOf(0L));
                    BigDecimal quesZhanbi2 = BigDecimal.valueOf(2L).add(yuguSanpinglv2);
                    Optional<Map<String, Object>> res4 = qmsMapList.stream().filter(m8 -> {
                        return currentGroupNum.equals(Convert.toStr(m8.get("groupNum")));
                    }).findAny();
                    if (res4.isPresent() && "1".equals(Convert.toStr(res4.get().get("makType")))) {
                        gongzuoliangZhanbi = gongzuoliangZhanbi.divide(quesZhanbi2, 4, 4);
                    }
                    BigDecimal workNumber_stu = Convert.toBigDecimal(oneQuesGroupObj.get("num")).multiply(gongzuoliangZhanbi).setScale(4, 1);
                    currentTeacherAssignedWork_stu[0] = currentTeacherAssignedWork_stu[0].add(workNumber_stu);
                }
            });
            BigDecimal currentTeacherUnassignedWork_stu = currentTeacherAllWork_stu.subtract(currentTeacherAssignedWork_stu[0]);
            int currentTeacherUnassignedWork_tishu = Convert.toInt(currentTeacherUnassignedWork_stu.divide(finalCurrentGongzuoliangZhanbi, 0, 0)).intValue();
            oneTeaObj.put("currentTeacherUnassignedWork_tishu", Integer.valueOf(currentTeacherUnassignedWork_tishu < 0 ? 0 : currentTeacherUnassignedWork_tishu));
        });
        return addTeaList;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String batchTeacherZhidingquota(String examPaperNum, String groupNum, String schoolNum, String num, String type, String loginNum) {
        String str;
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str2 : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str2;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = new HashMap();
        args.put("newGroupNum", newGroupNum2);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        String sql = "SELECT IFNULL(count(1),0) from questiongroup_user qu inner JOIN user u on qu.userNum=u.id LEFT JOIN quota qo on qu.groupNum=qo.groupNum and qu.userNum=qo.insertUser  where  qu.groupNum in ({newGroupNum[]})  and u.schoolnum={schoolNum}  and qo.id is not null ";
        String count = this.basedao._queryStr(sql, args);
        if (!"0".equals(count)) {
            String sql2 = "INSERT INTO `quota` (`exampaperNum`, `groupNum`, `num`, `insertUser`, `insertDate`)  SELECT qu.exampaperNum,qu.groupNum,0,qu.userNum,now() from questiongroup_user qu  inner JOIN user u on qu.userNum=u.id LEFT JOIN quota qo on qu.groupNum=qo.groupNum and qu.userNum=qo.insertUser where  qu.groupNum in ({newGroupNum[]})  and u.schoolnum={schoolNum} and qu.userType<>2 and qo.id is null ";
            str = this.basedao._execute(sql2, args) + "";
        } else {
            str = divideequalquota(examPaperNum, groupNum, schoolNum, loginNum) + "";
        }
        return str;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void batchTeacherZhidingquotaGroup(String examPaperNum, String groupNum, String schoolGroupNum, String groupquota) {
        String sql;
        String currentTime = DateUtil.getCurrentTime();
        String[] groupNumArr = groupNum.split("_");
        Map args = new HashMap();
        args.put("groupNum", groupNumArr[0]);
        args.put("schoolGroupNum", schoolGroupNum);
        if ("-2".equals(schoolGroupNum)) {
            sql = "select qgu.exampaperNum,qgu.groupNum,qgu.userNum,qo.num from questiongroup_user qgu inner join user u on u.id=qgu.userNum left join quota qo on qo.groupNum=qgu.groupNum and qo.insertUser=qgu.userNum where qgu.groupNum={groupNum} and qgu.userType=0 ";
        } else {
            String sgWStr = "-1".equals(schoolGroupNum) ? " and sg.schoolGroupNum is null " : " and sg.schoolGroupNum={schoolGroupNum} ";
            sql = "select qgu.exampaperNum,qgu.groupNum,qgu.userNum,qo.num from questiongroup_user qgu inner join user u on u.id=qgu.userNum left join quota qo on qo.groupNum=qgu.groupNum and qo.insertUser=qgu.userNum left join schoolgroup sg on sg.schoolNum=u.schoolnum where qgu.groupNum={groupNum} and qgu.userType=0 " + sgWStr;
        }
        List<Map<String, Object>> userList = this.basedao._queryMapList(sql, TypeEnum.StringObject, args);
        userList.stream().filter(u -> {
            return null != u.get("num");
        }).findAny().orElse(null);
        List<Map<String, Object>> addList = (List) userList.stream().filter(u2 -> {
            return null == u2.get("num");
        }).collect(Collectors.toList());
        addList.forEach(u3 -> {
            u3.put("num", 0);
            u3.put("insertDate", currentTime);
        });
        this.basedao._batchExecute("INSERT INTO `quota` (`exampaperNum`, `groupNum`, `num`, `insertUser`, `insertDate`)  values ({exampaperNum},{groupNum},{num},{userNum},{insertDate})", addList);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map getSchoolGroupAllQuota(String groupNum) {
        String sql;
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        String newGroupNum2 = newGroupNum.substring(1, newGroupNum.length());
        Map args = StreamMap.create().put("newGroupNum", (Object) newGroupNum2).put("newGroupNum2", (Object) newGroupNum2);
        String exampaperNum = this.basedao._queryStr("select distinct exampaperNum from questiongroup where groupNum in ({newGroupNum[]}) ", args);
        String enforce = systemService.fenzu(String.valueOf(exampaperNum));
        if ("0".equals(enforce)) {
            sql = "select -1 schoolGroupNum,ifnull(sum(num),0) from schoolquota where groupNum in ({newGroupNum[]}) ";
        } else {
            sql = "SELECT slg.schoolGroupNum,ifnull(sum(num),0) from schoolgroup slg LEFT JOIN schoolquota sq on slg.schoolNum=sq.schoolNum and sq.groupNum in ({newGroupNum[]}) GROUP BY slg.schoolGroupNum ";
        }
        return this.basedao._queryOrderMap(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getEnforce() {
        Object enforce1 = this.basedao.queryObject("SELECT MAX(enforce) from schoolgroup");
        String num = (enforce1 == null || "".equals(enforce1)) ? "0" : String.valueOf(enforce1);
        return num;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Object deleteOneByNum(String colum, String valule, Class cla) throws Exception {
        return this.dao.deleteOneByNum(colum, valule, cla);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer deleteSchool(String schoolNum) {
        if (null == schoolNum) {
            return null;
        }
        return this.dao.deleteschool(schoolNum);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map<String, String> getCurrentJie() {
        return this.dao.getCurrentJie();
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getObjectsByParam(Map<String, String> map, Class cla) {
        return this.dao.getObjectsByParam(map, cla);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getBasicLog(Baseinfolog bLog, int pageStart, int pageSize) {
        return this.dao.getBasicLog(bLog, pageStart, pageSize);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer getAllBaseLogRowCount(Baseinfolog bLog) {
        return this.dao.getAllBaseLogRowCount(bLog);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer getAllExamLogRowCount(Examlog eLog) {
        return this.dao.getAllExamLogRowCount(eLog);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getExamLog(Examlog eLog, int pageStart, int pageSize) {
        return this.dao.getExamLog(eLog, pageStart, pageSize);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Log> getAllLogs(Log log) {
        return this.dao.getAllLogs(log);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public int getAllLogsCount(Log log) {
        return this.dao.getAllLogsCount(log);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map<Data, List<Data>> getExamSetList(String category) {
        return this.dao.getExamSetList(category);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void updateDataTable(int id, String isDefault, String isLock) {
        this.dao.updateDataTable(id, isDefault, isLock);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void updateImg_Set(int id, String isDefault) {
        this.dao.updateImg_Set(id, isDefault);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String[] getImgIsDeleteMark() {
        return this.dao.getImgIsDeleteMark();
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map<Data, List<Data>> getExamSet8KList(String category) {
        return this.dao.getExamSet8KList(category);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer setDBPath(String filePath) {
        return this.dao.setDBPath(filePath);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void deleteTableImg(String examNum, String paperImg_checked, String questionImg_checked, String questionScoreImg_checked, String examineNumImg_checked) {
        this.dao.deleteTableImg(examNum, paperImg_checked, questionImg_checked, questionScoreImg_checked, examineNumImg_checked);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer checkStuCount(String schoolNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.basedao._queryInt("SELECT count(1) FROM school WHERE schoolNum={schoolNum} and isDelete='F'", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer checkStuNameCount(String schoolNum, String schoolName, String schtype) {
        String sql = "select count(1) from school where schoolName={schoolName}  and isDelete='F' limit 1";
        if (schtype.equals("edit")) {
            sql = "select  count(1) from school where schoolNum!={schoolNum} and schoolName={schoolName} limit 1";
        }
        Map args = StreamMap.create().put("schoolName", (Object) schoolName).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        return this.basedao._queryInt(sql, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer loadedei(String sqlFilePath, String commandSet, String[] argArry) {
        Runtime runTime = Runtime.getRuntime();
        new Properties();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String time = f.format(date);
        try {
            File sqlFolder = new File(sqlFilePath);
            if (!sqlFolder.exists()) {
                sqlFolder.mkdirs();
            }
            StringBuffer commandBuffer = new StringBuffer();
            String tableName = argArry[5] + "-" + time + ".sql";
            File sqlFile = new File(sqlFilePath + File.separator + tableName);
            if (sqlFile.exists()) {
                sqlFile.delete();
            }
            if (!sqlFile.exists()) {
                sqlFile.createNewFile();
            }
            commandBuffer.append(commandSet);
            commandBuffer.append("--default-character-set=utf8 --hex-blob -r " + sqlFile);
            runTime.exec(commandBuffer.toString());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void savePath(String id, String time, String filePath2, String insertuser) {
        Map args = StreamMap.create().put("operate", (Object) "1").put("para", (Object) filePath2).put("insertuser", (Object) insertuser).put("time", (Object) time);
        this.basedao._execute("insert  into  config(type,operate,para,insertuser,insertDate,description)  values('0',{operate} ,{para} ,{insertuser} ,{time},'备份路径')", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void savetimesplit(String id, String time, String timesplit, String insertuser) {
        Map args = StreamMap.create().put("operate", (Object) "2").put("timesplit", (Object) timesplit).put("insertuser", (Object) insertuser).put("time", (Object) time);
        this.basedao._execute("insert  into  config(type,operate,para,insertuser,insertDate,description)  values('0',{operate},{timesplit},{insertuser},{time},'备份时间间隔')", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void savedeletetime(String id, String time, String deletetime, String insertuser) {
        Map args = StreamMap.create().put("operate", (Object) "3").put("deletetime", (Object) deletetime).put("insertuser", (Object) insertuser).put("time", (Object) time);
        this.basedao._execute("insert  into  config(type,operate,para,insertuser,insertDate,description)  values('0',{operate},{deletetime},{insertuser},{time},'备份删除时间')", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer savebegintime(String id, String begintime, String time, String insertuser) {
        Map args = StreamMap.create().put("operate", (Object) "4").put("begintime", (Object) begintime).put("insertuser", (Object) insertuser).put("time", (Object) time);
        return Integer.valueOf(this.basedao._execute("insert  into  config(type,operate,para,insertuser,insertDate,description)  values('0',{operate},{begintime},{insertuser},{time},'第一次备份时间')", args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void filesplitdelete(String path) {
        String filePath = path.replace('\\', '/');
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String deletetimeList() {
        return this.dao.deletetimeList();
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String timesplitList() {
        return this.dao.timesplitList();
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String begintimesplitList() {
        return this.dao.begintimesplitList();
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String filepath(AwardPoint awardPoint) {
        return this.dao.filepath(awardPoint);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List begintimeList(AwardPoint awardPoint) {
        try {
            Map args = StreamMap.create().put("operate", (Object) "4");
            return this.basedao._queryBeanList("select  para  from   config  where  operate={operate} and type='0' ", AwardPoint.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List fileexits() {
        try {
            Map args = StreamMap.create().put("operate", (Object) "1");
            return this.basedao._queryBeanList("select para  from   config  where  operate={operate}  and type='0'  ", AwardPoint.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer updatepath(String filePath2, String insertuser, String insertDate) {
        Map args = StreamMap.create().put("filePath2", (Object) filePath2).put("insertuser", (Object) insertuser).put("insertDate", (Object) insertDate).put("operate", (Object) "1");
        return Integer.valueOf(this.basedao._execute("update  config  set para={filePath2}, insertuser={insertuser} , insertDate={insertDate}  where operate={operate}  and type='0' ", args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List savetimesplitList() {
        try {
            Map args = StreamMap.create().put("operate", (Object) "2");
            return this.basedao._queryArrayList("select id  from   config  where  operate={operate} and type='0'  ", args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer updatesavetimesplit(String timesplit, String insertuser, String insertDate) {
        Map args = StreamMap.create().put("timesplit", (Object) timesplit).put("insertuser", (Object) insertuser).put("insertDate", (Object) insertDate).put("operate", (Object) "2");
        return Integer.valueOf(this.basedao._execute("update  config  set para={timesplit} , insertuser={insertuser} , insertDate={insertDate}  where operate={operate} and type='0'  ", args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List deleteFileList() {
        try {
            Map args = StreamMap.create().put("operate", (Object) "3");
            return this.basedao._queryArrayList("select  id  from   config  where  operate={operate}  and type='0'  ", args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer updatedeleteFile(String deleteTime, String insertuser, String insertDate) {
        Map args = StreamMap.create().put("deleteTime", (Object) deleteTime).put("insertuser", (Object) insertuser).put("insertDate", (Object) insertDate).put("operate", (Object) "3");
        return Integer.valueOf(this.basedao._execute("update  config  set para={deleteTime}, insertuser={insertuser} , insertDate={insertDate}  where operate={operate} ", args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Object[] questionMangerList(QuestionManger questionManger, int count, String subjectNum) {
        try {
            StringBuffer buffer = new StringBuffer("  select   b.name, a.subjectName,b.subjectNum, b.id,b.insertUser, d.name as stageName,d.`value`  from   subject     a      left  join   questionType   b     on   a.subjectNum=b.subjectNum   left  join (select distinct stage from  basegrade) c      on b.stage=c.stage     left  join  data  d   on  c.stage=d.`value`   where d.type='5'  ");
            Map args = new HashMap();
            if (subjectNum != null && !subjectNum.equals("")) {
                buffer.append("and b.subjectNum={subjectNum}   ");
                args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            }
            buffer.append("order  by  b.subjectNum,d.`value` asc ");
            buffer.append(" limit {PageStart}, {PageSize}");
            args.put("PageStart", Integer.valueOf(questionManger.getPageStart()));
            args.put("PageSize", Integer.valueOf(questionManger.getPageSize()));
            List list = this.basedao._queryBeanList(buffer.toString(), QuestionManger.class, args);
            return new Object[]{list, Integer.valueOf(count)};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public int getAllquestionMangerCount(QuestionManger questionManger, String subjectNum) {
        return this.dao.getAllquestionMangerCount(questionManger, subjectNum);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List subjectList(QuestionManger questionManger) {
        return this.dao.subjectList(questionManger);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void delquestion(String[] ids, String user, String date) {
        this.dao.delquestion(ids, user, date);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void addQuestiontype(String subjectNum, String name, String user, String date, String stage) {
        this.dao.addQuestiontype(subjectNum, name, user, date, stage);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer exitsQuestiontype(String subjectNum, String name, String stage) {
        return this.dao.exitsQuestiontype(subjectNum, name, stage);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List statgeList(QuestionManger questionManger) {
        return this.dao.statgeList(questionManger);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List questionupList(String id, QuestionManger questionManger) {
        return this.dao.questionupList(id, questionManger);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void updateQuestiontype(String subjectNum, String name, String user, String date, String stage, QuestionManger questionManger) {
        this.dao.updateQuestiontype(subjectNum, name, user, date, stage, questionManger);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String examNumexecute(String exams, String gradeNum, String subjectNum) {
        return this.dao.examNumexecute(exams, gradeNum, subjectNum);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List examNumexecute2(String exams, String gradeNum, String subjectNum) {
        return this.dao.examNumexecute2(exams, gradeNum, subjectNum);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<AwardPoint> getExam() {
        return this.dao.getExam();
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<AwardPoint> getSchool(String examNum) {
        return this.dao.getSchool(examNum);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<AwardPoint> getGrade(String examNum, String schoolNum) {
        return this.dao.getGrade(examNum, schoolNum);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<AwardPoint> getSubject(String examNum, String gradeNum) {
        return this.dao.getSubject(examNum, gradeNum);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void deleteImg(String examPaperNum, String paperImg_checked, String questionImg_checked, String questionScoreImg_checked, String examineNumImg_checked, String exampapertypeimage_ckecked, String illegalimage_checked) {
        this.dao.deleteImg(examPaperNum, paperImg_checked, questionImg_checked, questionScoreImg_checked, examineNumImg_checked, exampapertypeimage_ckecked, illegalimage_checked);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String dataBasecpu(String dataName) {
        return this.dao.dataBasecpu(dataName);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public byte[] splitimgurl(String oneurl, String endurl) {
        String file = oneurl + "/" + endurl;
        File imgFile = new File(file);
        ServletContext sc = ServletActionContext.getServletContext();
        String realPath = sc.getRealPath("/");
        if (!imgFile.exists()) {
            file = realPath + "/common/image/bucunzai.png";
        } else if (imgFile.length() == 0) {
            file = realPath + "/common/image/tupiansunhuai.png";
        }
        return FileUtil.readBytes(file);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String cteateimgepathyz() {
        Map args = new HashMap();
        args.put("type", "2");
        Integer configint2 = this.basedao._queryInt("select count(1) from config where type={type} ", args);
        Integer configint3 = this.basedao.queryInt("select count(1) from config where type='3'", null);
        String type = "-1";
        if (configint2.intValue() == 0 && configint3.intValue() == 0) {
            type = "0";
        } else if (configint3.intValue() == 0) {
            type = "1";
        } else if (configint2.intValue() == 0) {
            type = "2";
        }
        return type;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void createsaveurl(String url) {
        String url2 = url.replace('\\', '/');
        Map args = new HashMap();
        args.put("url", url2);
        int cunzai = this.basedao._queryInt("select count(1) from config where type='2' and para={url} ", args).intValue();
        if (cunzai == 0) {
            args.put("type", "2");
            Object omax = this.basedao._queryObject("select IFNULL(max(operate),1) from config where type={type} ", args);
            Integer omaxint = Integer.valueOf(omax.toString());
            Integer omaxint2 = Integer.valueOf(omaxint.intValue() + 1);
            this.basedao.execute("update config set selected='2'");
            args.put("omaxint", omaxint2);
            args.put("url", url2);
            this.basedao._execute("insert  into config (type,operate,para,insertUser,insertDate,description,selected) values({type},{omaxint},{url},'1',now(),'图片存放根路径','1')", args);
            return;
        }
        this.basedao.execute("update config set selected='2'");
        this.basedao._execute(" update config set selected='1' where para={url} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void cteateimgepath(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        if (this.basedao._queryObject("select id from imgpath where examNum={examNum} and selected='1'", args) == null) {
            args.put("type", "2");
            Imgpath ip = (Imgpath) this.basedao._queryBean("select IFNULL(max(operate),1) imgpathid,para locationurl  from config where type={type} and selected='1'", Imgpath.class, args);
            String examimgpath = ip.getLocationurl().replace("\\", "\\/");
            args.put("examimgpath", examimgpath);
            args.put("locationnum", ip.getImgpathid());
            args.put("filename", GUID.getGUIDStr());
            this.basedao._execute("insert  into imgpath (examNum,location,locationnum,filename,selected,pwd,insertDate) values({examNum},{examimgpath},{locationnum},{filename},'1','',now())", args);
        }
    }

    public static boolean deletefile(String delpath) throws FileNotFoundException, IOException {
        try {
            File file = new File(delpath);
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delpath + "/" + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                    } else if (delfile.isDirectory()) {
                        deletefile(delpath + "/" + filelist[i]);
                    }
                }
                file.delete();
            }
            return true;
        } catch (FileNotFoundException e) {
            return true;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void deleteoneimg(String id, int type) {
        Map args = new HashMap();
        args.put("id", id);
        if (type == 1) {
            Imgpath ip = (Imgpath) this.basedao._queryBean("select s.img tableimg,ip.location locationurl  from (select  imgpath,img   from questionimage    WHERE scoreId={id} )s left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            Imgpath scoreip = (Imgpath) this.basedao._queryBean("select s.scoreimg tableimg,ip.location locationurl  from (select  imgpath,scoreimg   from scoreimage  WHERE scoreId={id} )s left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (ip != null) {
                try {
                    String questionimageurl = ip.getLocationurl() + "/" + ip.getTableimg();
                    ImageUtil.deleteOneFile(questionimageurl);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (scoreip != null) {
                String str = scoreip.getLocationurl() + "/" + scoreip.getTableimg();
                ImageUtil.deleteOneFile("select s.scoreimg tableimg,ip.location locationurl  from (select  imgpath,scoreimg   from scoreimage  WHERE scoreId={id} )s left join (select id,location from imgpath )ip on s.imgpath=ip.id");
            }
            return;
        }
        if (type == 2) {
            Imgpath ip2 = (Imgpath) this.basedao._queryBean("select s.img tableimg,ip.location locationurl  from (select  imgpath,img   from examinationnumimg      WHERE regid={id} )s left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            Imgpath exampapertypeimageip = (Imgpath) this.basedao._queryBean("select s.img tableimg,ip.location locationurl  from (select  imgpath,img   from exampapertypeimage      WHERE regid={id})s left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            Imgpath illegalimageip = (Imgpath) this.basedao._queryBean("select s.img tableimg,ip.location locationurl  from (select  imgpath,img   from illegalimage        WHERE regid={id} )s left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            Imgpath studentpaperimageip = (Imgpath) this.basedao._queryBean("select s.img tableimg,ip.location locationurl  from (select  imgpath,img   from studentpaperimage        WHERE regid={id} )s left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (ip2 != null) {
                try {
                    String examinationnumimgurl = ip2.getLocationurl() + "/" + ip2.getTableimg();
                    ImageUtil.deleteOneFile(examinationnumimgurl);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return;
                }
            }
            if (exampapertypeimageip != null) {
                String exampapertypeimageurl = exampapertypeimageip.getLocationurl() + "/" + exampapertypeimageip.getTableimg();
                ImageUtil.deleteOneFile(exampapertypeimageurl);
            }
            if (illegalimageip != null) {
                String illegalimageurl = illegalimageip.getLocationurl() + "/" + illegalimageip.getTableimg();
                ImageUtil.deleteOneFile(illegalimageurl);
            }
            if (studentpaperimageip != null) {
                String studentpaperimageurl = studentpaperimageip.getLocationurl() + "/" + studentpaperimageip.getTableimg();
                ImageUtil.deleteOneFile(studentpaperimageurl);
            }
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void copyFolder(String fromPath, String toPath, String fromPathval, String toPathval) {
        Map args = new HashMap();
        args.put("toPathval", toPathval);
        args.put("toPath", toPath);
        args.put("fromPathval", fromPathval);
        this.basedao._execute(" update   imgpath  set locationnum={toPathval} ,location={toPath}  where locationnum={fromPathval} ", args);
        File targetFile = new File(toPath);
        createFile(targetFile, false);
        File file = new File(fromPath);
        if (targetFile.isDirectory() && file.isDirectory()) {
            copyFileToDir(targetFile.getAbsolutePath(), listFile(file));
        }
        try {
            deletefile(fromPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void createFile(File file, boolean isFile) {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                createFile(file.getParentFile(), false);
                return;
            }
            if (isFile) {
                try {
                    file.createNewFile();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            file.mkdir();
        }
    }

    public static void copyFileToDir(String toDir, String[] filePath) {
        if (toDir == null || "".equals(toDir)) {
            return;
        }
        File targetFile = new File(toDir);
        if (!targetFile.exists()) {
            targetFile.mkdir();
        } else if (!targetFile.isDirectory()) {
            return;
        }
        for (String str : filePath) {
            File file = new File(str);
            if (file.isDirectory()) {
                copyFileToDir(toDir + "/" + file.getName(), listFile(file));
            } else {
                copyFileToDir(toDir, file, "");
            }
        }
    }

    public static void copyFileToDir(String toDir, File file, String newName) {
        String newFile;
        if (newName != null && !"".equals(newName)) {
            newFile = toDir + "/" + newName;
        } else {
            newFile = toDir + "/" + file.getName();
        }
        File tFile = new File(newFile);
        copyFile(tFile, file);
    }

    public static void copyFile(File toFile, File fromFile) {
        if (toFile.exists()) {
            return;
        }
        createFile(toFile, true);
        try {
            InputStream is = new FileInputStream(fromFile);
            FileOutputStream fos = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                fos.write(buffer);
            }
            is.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static String[] listFile(File dir) {
        String absolutPath = dir.getAbsolutePath();
        String[] paths = dir.list();
        String[] files = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            files[i] = absolutPath + "/" + paths[i];
        }
        return files;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getParaFromConfig(String type, String operate) {
        Map args = StreamMap.create().put("type", (Object) type).put("operate", (Object) operate);
        Object object = this.basedao._queryObject("SELECT para FROM config WHERE type={type} AND operate={operate} ", args);
        if (null != object) {
            return String.valueOf(object);
        }
        return null;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public config getOpeFromConfig(String type) {
        Map args = StreamMap.create().put("type", (Object) type);
        config con = (config) this.basedao._queryBean("SELECT type,operate,para FROM config WHERE type={type} ", config.class, args);
        return con;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getImageServerUri() {
        String imageAccessPath = getParaFromConfig("4", ConstConfig.ImgServer_http_HttpHost);
        String access_path = "";
        if (null != imageAccessPath && !imageAccessPath.equals("")) {
            String port = getParaFromConfig("4", ConstConfig.ImgServer_http_HttpPort);
            String project = getParaFromConfig("4", ConstConfig.ImgServer_http_HttpProject);
            access_path = "http://" + imageAccessPath + ":" + port + "/" + project + "/";
        }
        return access_path;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getIsLevelClass(String examNum, String gradeNum, String subjectNum, String jieInfo) {
        if (null == examNum || "".equals(examNum)) {
            return "";
        }
        if ("".equals(gradeNum)) {
            gradeNum = null;
        }
        if ("".equals(subjectNum)) {
            subjectNum = null;
        }
        if ("".equals(jieInfo)) {
            jieInfo = null;
        }
        String result = "";
        try {
            Map args = new HashMap();
            args.put(Const.EXPORTREPORT_examNum, examNum);
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args.put("jieInfo", jieInfo);
            Object result0 = this.basedao._queryObject("select getIsLevelClass({examNum},{gradeNum},{subjectNum},{jieInfo})", args);
            if (result0 != null) {
                result = result0.toString();
            }
        } catch (Exception e) {
        }
        return result;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getIsUpgrade(String examNum, String gradeNum, String jieInfo, String isHistory) {
        String isHistory2;
        Connection conn = DbUtils.getConnection();
        CallableStatement pstat = null;
        if (null != isHistory && isHistory.equals("F")) {
            isHistory2 = "0";
        } else {
            isHistory2 = "1";
        }
        try {
            try {
                pstat = conn.prepareCall("{? = call /* shard_host_HG=Read */ getIsUpgrade(?,?,?,?)}");
                pstat.setString(2, examNum);
                pstat.setString(3, gradeNum);
                pstat.setString(4, jieInfo);
                pstat.setString(5, isHistory2);
                pstat.registerOutParameter(1, 1);
                pstat.execute();
                String result = pstat.getString(1);
                DbUtils.close(pstat, conn);
                return result;
            } catch (SQLException e) {
                this.log.info("获取年级是否是升级的数据", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            DbUtils.close(pstat, conn);
            throw th;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<AjaxData> getTopStasticItem() {
        try {
            return this.basedao.queryBeanList("select statisticId num,statisticName name,sonStatisticId ext1,sonStatisticName ext2 from statisticrelation where description = 'top' ", AjaxData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer delTopStasticItem(String staId, String sonId) throws Exception {
        String delChar = staId.substring(0, 2);
        Map args = StreamMap.create().put("delChar", (Object) ("%" + delChar + "%"));
        this.basedao._execute("delete from statisticrelation where statisticId like {delChar}", args);
        Map args2 = StreamMap.create().put("staId", (Object) staId);
        this.basedao._execute("delete from statisticitem_school where rootId={staId} ", args2);
        Map args3 = StreamMap.create().put("delChar", (Object) delChar);
        return Integer.valueOf(this.basedao._execute("update statisticletter set status = 'F' where letter = {delChar} ", args3));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer changeTopName(String staId, String sonId, String stasticName) throws Exception {
        Map args = StreamMap.create().put("stasticName", (Object) stasticName).put("staId", (Object) staId);
        uniformname(staId, stasticName);
        return Integer.valueOf(this.basedao._execute("update statisticrelation set updateDate = now() ,statisticName = {stasticName}  where statisticId = {staId}  ", args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getStasticMax() {
        Object object = this.basedao.queryObject("select ASCII(SUBSTR(MAX(statisticId) FROM 1 FOR 1)) from statisticrelation  ");
        if (null != object) {
            return String.valueOf(object);
        }
        return "-1";
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer stasticName_exist(String stasticName) {
        Map args = StreamMap.create().put("stasticName", (Object) stasticName);
        return this.basedao._queryInt("SELECT COUNT(statisticName) FROM statisticrelation WHERE statisticName={stasticName} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer addStasticTop(String staMax, String stasticName, String users, int count) {
        if (count >= 1) {
            return 1;
        }
        Map args = StreamMap.create().put("staMax", (Object) staMax).put("stasticName", (Object) stasticName).put(License.USERS, (Object) users);
        this.basedao._execute("INSERT INTO statisticrelation (statisticId,statisticName,statisticItem,sonStatisticId,sonStatisticName,isLeaf,description,insertUser,insertDate,updateUser,updateDate)  VALUES ( {staMax},{stasticName}, '-1','-1','-1','F','top',{users}, now(), {users}, now())", args);
        Map args2 = StreamMap.create().put("letter", (Object) staMax.substring(0, 2));
        this.basedao._execute("update statisticletter set status = 'T' where letter = {letter}  ", args2);
        return Integer.valueOf(count);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getNewStatisticId() {
        Object object = this.basedao.queryObject("select letter from statisticletter where status = 'F' and isdelete = 'F' LIMIT 1 ");
        if (null != object) {
            return String.valueOf(object);
        }
        return "-1";
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<TreeData> getStasticItem(String staId) {
        Map args = new HashMap();
        args.put("statisticId", "%" + staId.substring(0, 2) + "%");
        List list = this.basedao._queryArrayList("select id from statisticrelation where statisticId like {statisticId}  and statisticItem='-1' ", args);
        if (list.size() > 0) {
            return this.basedao._queryBeanList(" select statisticId id, '-1' pid, statisticName name,'-1' stage,'Y' isLeaf from statisticrelation where description = 'top' and statisticId like {statisticId} ", TreeData.class, args);
        }
        return this.basedao._queryBeanList(" select statisticId id, '-1' pid, statisticName name,'-1' stage,'Y' isLeaf from statisticrelation where description = 'top' and statisticId like {statisticId}  union   select sonStatisticId id ,statisticId pid ,sonStatisticName name,statisticName stage,isLeaf from statisticrelation where statisticId like {statisticId} ", TreeData.class, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer getNewSonNum(String staId) {
        Map args = new HashMap();
        args.put("statisticId", "%" + staId.substring(0, 2) + "%");
        Object object = this.basedao._queryObject("select MAX(CONVERT(SUBSTR(sonStatisticId FROM 3),SIGNED)) num from statisticrelation where statisticId like {statisticId}  ", args);
        if (null != object) {
            return Integer.valueOf(String.valueOf(object));
        }
        return -1;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer saveStatistic(String[] ids, String[] pid, String[] names, String[] pnames, String[] isleafs, String staId, String uid) throws Exception {
        int[] returnCode = null;
        String date = DateUtil.getCurrentTime();
        String delChar = staId.substring(0, 2);
        Map args = new HashMap();
        args.put("delChar", "%" + delChar + "%");
        List list1 = this.basedao._queryArrayList("select id from statisticrelation where statisticId like {delChar}  and statisticItem='-1' ", args);
        if (list1.size() <= 0) {
            this.basedao._execute("delete from statisticrelation where statisticId like {delChar} ", args);
            List<StatisticRelation> list = new ArrayList<>();
            if (null != ids && ids.length > 0) {
                int j = 0;
                if (ids.length == 1 && !ids[0].equals("")) {
                    Map args2 = StreamMap.create().put("statisticId", (Object) (delChar + ids[0].substring(2))).put("statisticName", (Object) names[0]).put("uid", (Object) uid);
                    this.basedao._execute("INSERT INTO statisticrelation (statisticId,statisticName,statisticItem,sonStatisticId,sonStatisticName,isLeaf,description,insertUser,insertDate,updateUser,updateDate)  VALUES ( {statisticId},{statisticName}, '-1','-1','-1','F','top',{uid}, now(), {uid}, now())", args2);
                    return 1;
                }
                if (ids.length == 1 && ids[0].equals("")) {
                    Map args3 = StreamMap.create().put("delChar", (Object) delChar);
                    this.basedao._execute("update statisticletter set status = 'F' where letter = {delChar}  ", args3);
                    return 1;
                }
                for (int i = 1; i < ids.length; i++) {
                    StatisticRelation k = new StatisticRelation();
                    Matcher m = Pattern.compile(".*[a-zA-Z]+.*").matcher(pid[i]);
                    if (m.matches()) {
                        k.setStatisticId(delChar + pid[i].substring(2));
                    } else {
                        k.setStatisticId(delChar + pid[i].substring(9));
                    }
                    k.setStatisticName(pnames[i]);
                    if (isleafs[i].equals("T")) {
                        k.setSonStatisticId(ids[i]);
                    } else {
                        Matcher m2 = Pattern.compile(".*[a-zA-Z]+.*").matcher(ids[i]);
                        if (m2.matches()) {
                            k.setSonStatisticId(delChar + ids[i].substring(2));
                        } else {
                            k.setSonStatisticId(delChar + ids[i].substring(9));
                        }
                    }
                    k.setSonStatisticName(names[i]);
                    k.setIsDelete("F");
                    k.setInsertDate(date);
                    k.setInsertUser(uid);
                    if (isleafs[i].equals("T")) {
                        k.setIsLeaf("T");
                        k.setStatisticItem("01");
                    } else {
                        k.setIsLeaf("F");
                        k.setStatisticItem("00");
                    }
                    Matcher m3 = Pattern.compile(".*[a-zA-Z]+.*").matcher(pid[i]);
                    if (m3.matches()) {
                        if (j == 0 && (delChar + pid[i].substring(2)).equals(staId)) {
                            k.setDescription("top");
                            j++;
                        }
                    } else if (j == 0 && (delChar + pid[i].substring(9)).equals(staId)) {
                        k.setDescription("top");
                        j++;
                    }
                    list.add(k);
                    if (list.size() >= 20) {
                        returnCode = this.basedao.batchSave(list);
                        list.clear();
                    }
                    if (list.size() > 0 && list.size() < 20) {
                        returnCode = this.basedao.batchSave(list);
                        list.clear();
                    }
                    if (i == ids.length) {
                        returnCode = this.basedao.batchSave(list);
                        list.clear();
                    }
                }
            }
            if (null != returnCode) {
                return 1;
            }
            return null;
        }
        if (ids.length <= 1) {
            if (ids.length == 1 && ids[0].equals("")) {
                this.basedao._execute("delete from statisticrelation where statisticId like {delChar} ", args);
                Map args4 = StreamMap.create().put("delChar", (Object) delChar);
                this.basedao._execute("update statisticletter set status = 'F' where letter = {delChar}  ", args4);
                return 1;
            }
            return 1;
        }
        if (isleafs[1].equals("T")) {
            Map args22 = StreamMap.create().put("sonStatisticId", (Object) ids[1]).put("sonStatisticName", (Object) names[1]).put("statisticId", (Object) ("%" + staId.substring(0, 2) + "%"));
            this.basedao._execute(" update statisticrelation set  updateDate = now() ,statisticItem='01' ,sonStatisticId={sonStatisticId} ,sonStatisticName={sonStatisticName} ,description='top',isLeaf='T' where statisticId like {statisticId} ", args22);
        } else {
            Map args32 = StreamMap.create().put("sonStatisticId", (Object) (delChar + ids[1].substring(2))).put("sonStatisticName", (Object) names[1]).put("statisticId", (Object) ("%" + staId.substring(0, 2) + "%"));
            this.basedao._execute(" update statisticrelation set  updateDate = now() , statisticItem='00' ,sonStatisticId={sonStatisticId} ,sonStatisticName={sonStatisticName},description='top',isLeaf='F' where statisticId like {statisticId}  ", args32);
        }
        List<StatisticRelation> list2 = new ArrayList<>();
        for (int i2 = 2; i2 < ids.length; i2++) {
            StatisticRelation k2 = new StatisticRelation();
            Matcher m4 = Pattern.compile(".*[a-zA-Z]+.*").matcher(pid[i2]);
            if (m4.matches()) {
                k2.setStatisticId(delChar + pid[i2].substring(2));
            } else {
                k2.setStatisticId(delChar + pid[i2].substring(9));
            }
            k2.setStatisticName(pnames[i2]);
            if (isleafs[i2].equals("T")) {
                k2.setSonStatisticId(ids[i2]);
            } else {
                Matcher m5 = Pattern.compile(".*[a-zA-Z]+.*").matcher(ids[i2]);
                if (m5.matches()) {
                    k2.setSonStatisticId(delChar + ids[i2].substring(2));
                } else {
                    k2.setSonStatisticId(delChar + ids[i2].substring(9));
                }
            }
            k2.setSonStatisticName(names[i2]);
            k2.setIsDelete("F");
            k2.setInsertDate(date);
            k2.setInsertUser(uid);
            if (isleafs[i2].equals("T")) {
                k2.setIsLeaf("T");
                k2.setStatisticItem("01");
            } else {
                k2.setIsLeaf("F");
                k2.setStatisticItem("00");
            }
            list2.add(k2);
            if (list2.size() >= 20) {
                returnCode = this.basedao.batchSave(list2);
                list2.clear();
            }
            if (list2.size() > 0 && list2.size() < 20) {
                returnCode = this.basedao.batchSave(list2);
                list2.clear();
            }
            if (i2 == ids.length) {
                returnCode = this.basedao.batchSave(list2);
                list2.clear();
            }
        }
        if (null != returnCode) {
            return 1;
        }
        return null;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String isLeafOrNot(String pnum, String staId) {
        Map args = StreamMap.create().put("pnum", (Object) pnum).put("statisticId", (Object) ("%" + staId.substring(0, 2) + "%"));
        Object object = this.basedao._queryObject(" select isLeaf from statisticrelation where sonStatisticId={pnum}  and  statisticId like {statisticId}  ", args);
        if (null == object) {
            return "F";
        }
        return String.valueOf(object);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<AjaxData> getAllSchool2(String pnumvalue) {
        try {
            Map args = StreamMap.create().put("pnumvalue", (Object) pnumvalue);
            return this.basedao._queryBeanList("select s.id num,s.shortname name,(case when sta.sonStatisticId is  null then 'F' ELSE 'T' end )ext1 from school s left join (select sonStatisticId from statisticrelation sta where statisticId={pnumvalue} and statisticItem='01')sta on sta.sonStatisticId=s.id order by convert(s.shortname using gbk)", AjaxData.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<AjaxData> getAllSchool(String pnumvalue, String schoolName) {
        String sql;
        Map args = new HashMap();
        if (schoolName.equals("")) {
            sql = "select s.id num,s.shortname name,(case when sta.sonStatisticId is  null then 'F' ELSE 'T' end )ext1 from   (select id,shortname from school where 1=1 ) s";
        } else {
            sql = "select s.id num,s.shortname name,(case when sta.sonStatisticId is  null then 'F' ELSE 'T' end )ext1 from   (select id,shortname from school where 1=1 and  shortname like {schoolName} ) s";
            args.put("schoolName", "%" + schoolName + "%");
        }
        String sql2 = sql + " left join  (select sonStatisticId from statisticrelation sta where  LEFT(statisticId,2)= LEFT({pnumvalue},2) and statisticItem='01')sta on sta.sonStatisticId=s.id  order by convert(s.shortname using gbk)";
        args.put("pnumvalue", pnumvalue);
        return this.basedao._queryBeanList(sql2, AjaxData.class, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<TreeData> checkLeaf(String staId) throws Exception {
        Map args = StreamMap.create().put("statisticId", (Object) ("%" + staId.substring(0, 2) + "%"));
        return this.basedao._queryBeanList("select a.sonStatisticId id,a.sonStatisticName name from statisticrelation a  left join statisticrelation b on a.sonStatisticId=b.statisticId  where a.isLeaf = 'F' and a.statisticId like {statisticId}   and b.statisticName is NULL ", TreeData.class, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String checkDuplicateAdd(String pnumvalue, String pnameval, String num, String name, String staId) {
        Map args = StreamMap.create().put("pnumvalue", (Object) pnumvalue).put("name", (Object) name);
        Object object = this.basedao._queryObject(" select sonStatisticName from statisticrelation where statisticId={pnumvalue} and sonStatisticName={name} ", args);
        if (null == object) {
            return "F";
        }
        return "T";
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List getuserInfo(String userid, String usertype, ServletContext context) {
        if (userid == null || usertype == null) {
            return null;
        }
        return this.dao.getuserInfo(userid, usertype, context);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer changeStasticNodeName(String staId, String id, String pid, String name, String pname, String isleaf) throws Exception {
        Map args = new HashMap();
        args.put("name", name);
        args.put("pid", pid);
        args.put("id", id);
        this.basedao._execute("update statisticrelation set updateDate = now() , sonStatisticName={name} where statisticId={pid}  and sonStatisticId={id} ", args);
        Map args2 = StreamMap.create().put("name", (Object) name).put("id", (Object) id);
        uniformname(id, name);
        return Integer.valueOf(this.basedao._execute("update statisticrelation set updateDate = now() , statisticName={name}  where statisticId={id}  ", args2));
    }

    public void uniformname(String id, String name) {
        List<RowArg> sqlList = new ArrayList<>();
        Map args = StreamMap.create().put("name", (Object) name).put("id", (Object) id);
        sqlList.add(new RowArg("update statisticitem set sItemName = {name}  where sItemId = {id} ", args));
        sqlList.add(new RowArg("update statisticitem set pItemName = {name}  where pItemId = {id} ", args));
        sqlList.add(new RowArg("update statisticitem set topItemName ={name} where topItemId = {id} ", args));
        sqlList.add(new RowArg("update statisticitem_school set sItemName = {name} where sItemId = {id} ", args));
        sqlList.add(new RowArg("update statisticitem_school set pItemName = {name} where pItemId = {id} ", args));
        sqlList.add(new RowArg("update statisticitem_school set topItemName = {name}  where topItemId ={id} ", args));
        sqlList.add(new RowArg("update statisticlevel set statisticName = {name}  where statisticId = {id} ", args));
        sqlList.add(new RowArg("update statisticlevel_fufen set statisticName = {name}  where statisticId = {id}", args));
        sqlList.add(new RowArg("update statistic_question set statisticName = {name}  where statisticId = {id} ", args));
        sqlList.add(new RowArg("update knowlege_middle_data_statistic set statisticName = {name}  where statisticId = {id} ", args));
        sqlList.add(new RowArg("update ability_middle_data_statistic set statisticName = {name}  where statisticId = {id} ", args));
        sqlList.add(new RowArg("update questiontype_middle_data_statistic set statisticName ={name}   where statisticId = {id} ", args));
        this.basedao._batchExecute(sqlList);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Map<String, String> getGradeMap() {
        return this.basedao.queryOrderMap("select DISTINCT gradeNum,gradeName from grade order by gradeNum", TypeEnum.StringString, null);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Subject> getTMList(String gradeNum) throws Exception {
        try {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
            return this.basedao._queryBeanList("select t.psubjectNum pid,s1.subjectName ext1,GROUP_CONCAT(distinct t.ssubjectNum ORDER BY t.ssubjectNum ASC SEPARATOR '') ext5,GROUP_CONCAT(distinct s2.subjectName ORDER BY t.ssubjectNum ASC SEPARATOR ',') subjectName,t.status from totalscoremanagement t  left join subject s1 on s1.subjectNum=t.psubjectNum   left join subject s2 on s2.subjectNum=t.ssubjectNum   where t.gradeNum={gradeNum}  GROUP BY t.psubjectNum ORDER BY t.psubjectNum,t.ssubjectNum ", Subject.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getNewTotalId() {
        for (int i = 20; i < 100; i++) {
            int finalI = i;
            Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) Integer.valueOf(finalI));
            Object object = this.basedao._queryObject("select subjectNum from subject where pid=-1 and subjectNum>0 and subjectNum ={subjectNum}  ", args);
            if (null == object) {
                return String.valueOf(i);
            }
        }
        return "-1";
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer totalName_exist(String totalName) {
        Map args = StreamMap.create().put("totalName", (Object) totalName);
        return this.basedao._queryInt("select COUNT(subjectNum) from subject where subjectName={totalName} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String createTotalSubject(String totalSubNum, String totalName, String[] subjectNums, String gradeNum) {
        Integer orderNumVal = Integer.valueOf(this.basedao.queryInt("select MAX(orderNum) from subject ", null).intValue() + 1);
        Map args = StreamMap.create().put("totalSubNum", (Object) totalSubNum).put("totalName", (Object) totalName).put("orderNumVal", (Object) orderNumVal);
        this.basedao._execute("INSERT INTO subject (subjectNum,subjectName,pid,isHidden,subjectType,mainType,insertUser,insertDate,updateUser,updateDate,isDelete,orderNum)  VALUES ({totalSubNum},{totalName}, '-1', 'T', '0', '5', '-1', NOW(), '0', NOW(), 'F', {orderNumVal})", args);
        List<Map> mapList = new ArrayList<>();
        for (int i = 0; i < subjectNums.length; i++) {
            int finalI = i;
            Map args2 = StreamMap.create().put("totalSubNum", (Object) totalSubNum).put("ssubjectNum", (Object) subjectNums[finalI]).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
            mapList.add(args2);
        }
        this.basedao._batchExecute("INSERT INTO totalscoremanagement(psubjectNum,ssubjectNum,gradeNum,type,weight,status,insertUser,insertDate,updateUser,updateDate)  VALUES ({totalSubNum}, {ssubjectNum}, {gradeNum}, '0', '1', 'T', '-1', NOW(), '-1', NOW())", mapList);
        return Const.SUCCESS;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Subject> getOneTotalSubject(String psub, String gradeNum) {
        Map args = StreamMap.create().put("psub", (Object) psub).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.basedao._queryBeanList("select id,ssubjectNum subjectNum,type,weight ext1,status from totalscoremanagement where psubjectNum={psub} and gradeNum={gradeNum}  order by ssubjectNum ", Subject.class, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void deleteOneTotalSub(String psub, String gradeNum, String subjectNum) {
        Map args = StreamMap.create().put("psub", (Object) psub).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        this.basedao._execute("delete from totalscoremanagement where psubjectNum={psub} and gradeNum={gradeNum} and ssubjectNum={subjectNum} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void updateTotalSubject(String psub, String gradeNum, String[] subjectNum) {
        Map args1 = StreamMap.create().put("psub", (Object) psub).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        String status = this.basedao._queryStr("select DISTINCT status from totalscoremanagement where psubjectNum={psub}  and gradeNum={gradeNum}", args1);
        for (int i = 0; i < subjectNum.length; i++) {
            int finalI1 = i;
            Map args = StreamMap.create().put("psub", (Object) psub).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("ssubjectNum", (Object) subjectNum[finalI1]);
            if (this.basedao._queryObject("select * from totalscoremanagement where psubjectNum={psub}  and gradeNum={gradeNum} and ssubjectNum={ssubjectNum}", args) == null) {
                Map args2 = StreamMap.create().put("psub", (Object) psub).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                String totalSubNum = this.basedao._queryStr("select psubjectNum from totalscoremanagement where psubjectNum={psub} and gradeNum={gradeNum} limit 1", args2);
                int finalI = i;
                Map args3 = StreamMap.create().put("totalSubNum", (Object) totalSubNum).put("ssubjectNum", (Object) subjectNum[finalI]).put(Const.CORRECT_SCORECORRECT, (Object) status).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                this.basedao._execute("INSERT INTO totalscoremanagement(psubjectNum,ssubjectNum,gradeNum,type,weight,status,insertUser,insertDate,updateUser,updateDate)  VALUES ({totalSubNum}, {ssubjectNum} , {gradeNum}, '0', '1', {status}, '-1', NOW(), '-1', NOW())", args3);
            }
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void updateTotalName(String psub, String totalName) {
        Map args = StreamMap.create().put("totalName", (Object) totalName).put("psub", (Object) psub);
        this.basedao._execute("update subject set subjectName={totalName} where subjectNum={psub} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void delTotalItem(String psub) {
        Map args = StreamMap.create().put("psub", (Object) psub);
        this.basedao._execute("delete from totalscoremanagement where psubjectNum={psub}  ", args);
        this.basedao._execute("delete from subject where subjectNum={psub} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer totalItemUsedOrNot(String psub) {
        Map args = StreamMap.create().put("psub", (Object) psub);
        return this.basedao._queryInt("select COUNT(subjectNum) from studentlevel where subjectNum={psub} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void delTotalItemAll(String psub) {
        Map args = StreamMap.create().put("psub", (Object) psub);
        this.basedao._execute("delete from totalscoremanagement where psubjectNum={psub}   ", args);
        this.basedao._execute("delete from subject where subjectNum={psub}   ", args);
        this.basedao._execute("delete from studentlevel where subjectNum={psub} ", args);
        this.basedao._execute("delete from classlevel where subjectNum={psub}  ", args);
        this.basedao._execute("delete from gradelevel where subjectNum={psub} ", args);
        this.basedao._execute("delete from arealevel where subjectNum={psub}   ", args);
        this.basedao._execute("delete from statisticstudentlevel where subjectNum={psub}  ", args);
        this.basedao._execute("delete from statisticlevel where subjectNum={psub} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void hideOrShowTotal(String psub, String gradeNum, String status) {
        Map args = StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) status).put("psub", (Object) psub).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        this.basedao._execute("update totalscoremanagement set status={status} where psubjectNum={psub} and gradeNum={gradeNum}   ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String setAsBase(String staId, String userId) {
        String flag = "T";
        Map args = StreamMap.create().put("staId", (Object) staId).put("userId", (Object) userId);
        try {
            this.basedao._execute("call statisticItem_school({staId},{userId})", args);
        } catch (Exception e) {
            flag = "F";
            e.printStackTrace();
        }
        return flag;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<String> getBaseLeiceng() {
        List<Map<String, Object>> list1 = this.basedao.queryMapList("select sts.topItemId from statisticitem_school sts LEFT JOIN statisticrelation sl on sts.topItemId = sl.statisticId where sl.description = 'top' GROUP BY sts.topItemId");
        List<String> list = new ArrayList<>();
        for (Map<String, Object> map : list1) {
            list.add(map.get("topItemId") + "");
        }
        return list;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> getLimbNode2(String userId) {
        String sql;
        Map args = StreamMap.create().put("userId", (Object) userId);
        Map<String, Object> map = this.basedao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if (userId.equals("-1") || userId.equals("-2") || map != null) {
            sql = "select DISTINCT sItemId id,pItemId pId,sItemName name from statisticitem_school where statisticItem = '00' UNION select DISTINCT ss.pItemId id,ss.topItemId pid,ss.pItemName name from statisticitem_school ss LEFT JOIN statisticrelation sr on ss.pItemId = sr.statisticId  where  sr.description='top' ORDER BY pid ";
        } else {
            sql = "select DISTINCT sts.sItemId id,sts.pItemId pId,sts.sItemName name from statisticitem_school sts LEFT JOIN (select distinct g.topItemId,g.topItemName from statisticitem_school g left join schoolscanpermission h on h.schoolNum = g.sItemId and h.userNum={userId}  left join  user u on u.schoolNum = g.sItemId  and u.id = {userId}  and u.usertype=1 left join school s on g.sItemId=s.id where g.statisticItem='01' and (h.userNum is not null OR u.id is not null) and s.isDelete='F') q on q.topItemId =sts.sItemId where statisticItem = '00' and q.topItemId is not null UNION select DISTINCT ss.pItemId id,ss.topItemId pid,ss.pItemName name from statisticitem_school ss LEFT JOIN statisticrelation sr on ss.pItemId = sr.statisticId LEFT JOIN (select distinct g.topItemId,g.topItemName from statisticitem_school g left join schoolscanpermission h on h.schoolNum = g.sItemId and h.userNum={userId}  left join  user u on u.schoolNum = g.sItemId  and u.id = {userId} and u.usertype=1 left join school s on g.sItemId=s.id where g.statisticItem='01' and (h.userNum is not null OR u.id is not null) and s.isDelete='F') q on q.topItemId =ss.pItemId  where  sr.description='top' and q.topItemId is not null ";
        }
        return this.basedao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> getLimbNode(String userId) {
        String sql;
        Map args = StreamMap.create().put("userId", (Object) userId);
        if (userId.equals("-1") || userId.equals("-2")) {
            sql = "select DISTINCT sItemId id,pItemId pId,sItemName name from statisticitem_school where statisticItem = '00' UNION select DISTINCT ss.pItemId id,ss.topItemId pid,ss.pItemName name from statisticitem_school ss LEFT JOIN statisticrelation sr on ss.pItemId = sr.statisticId  where  sr.description='top' ORDER BY pid ";
        } else {
            sql = "select DISTINCT sts.sItemId id,sts.pItemId pId,sts.sItemName name from statisticitem_school sts LEFT JOIN (select distinct g.topItemId,g.topItemName from statisticitem_school g left join schauthormanage h on h.schoolNum = g.sItemId and h.userId={userId}  left join  user u on u.schoolNum = g.sItemId  and u.id = {userId}  and u.usertype=1 left join school s on g.sItemId=s.id where g.statisticItem='01' and (h.teacherName is not null OR u.id is not null) and s.isDelete='F') q on q.topItemId =sts.sItemId where statisticItem = '00' and q.topItemId is not null UNION select DISTINCT ss.pItemId id,ss.topItemId pid,ss.pItemName name from statisticitem_school ss LEFT JOIN statisticrelation sr on ss.pItemId = sr.statisticId LEFT JOIN (select distinct g.topItemId,g.topItemName from statisticitem_school g left join schauthormanage h on h.schoolNum = g.sItemId and h.userId={userId}  left join  user u on u.schoolNum = g.sItemId  and u.id = {userId} and u.usertype=1 left join school s on g.sItemId=s.id where g.statisticItem='01' and (h.teacherName is not null OR u.id is not null) and s.isDelete='F') q on q.topItemId =ss.pItemId  where  sr.description='top' and q.topItemId is not null ";
        }
        return this.basedao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> getLimbNode2(String exam, String userId) {
        String sql;
        String examStr = "";
        if (exam != null && !exam.equals("")) {
            examStr = " and ss.examNum={exam} ";
        }
        if (userId.equals("-1") || userId.equals("-2")) {
            sql = "select -1 id,-1 pId,'学校' name UNION select DISTINCT ss.sItemId id,ss.pItemId pId,ss.sItemName name from statisticitem ss where ss.statisticItem = '00' " + examStr + "UNION select 'ZZ1' id,'ZZ1' pId,'全区' name UNION select DISTINCT ss.pItemId id,ss.topItemId pid,ss.pItemName name from statisticitem ss LEFT JOIN statisticrelation sr on ss.pItemId = sr.statisticId  where  sr.description='top' " + examStr + "ORDER BY pid ";
        } else {
            sql = "select -1 id,-1 pId,'学校' name union select s.id,s.pId,s.name from (select s.pid ,t1.topItemId id,t1.topItemName name,count(t2.schoolNum) s_s1,COUNT(t1.sItemId) s_s2  FROM (select topItemId,topItemName,sItemId,sItemName FROM statisticitem  where statisticItem='01' and examNum={exam}  ) t1  LEFT JOIN (select schoolNUm from schauthormanage where userId={userId} UNION select schoolNum from user where id= {userId} ) t2 ON t1.sItemId = t2.schoolNum  LEFT JOIN (  select DISTINCT ss.sItemId id,ss.pItemId pId,ss.sItemName name from statisticitem ss where ss.statisticItem = '00' " + examStr + "UNION select 'ZZ1' id,'ZZ1' pId,'全区' name UNION select DISTINCT ss.pItemId id,ss.topItemId pid,ss.pItemName name from statisticitem ss LEFT JOIN statisticrelation sr on ss.pItemId = sr.statisticId  where  sr.description='top' " + examStr + ") s ON t1.topItemId = s.id GROUP BY t1.topItemId  HAVING s_s1=s_s2 )s  ";
        }
        Map args = StreamMap.create().put("exam", (Object) exam).put("userId", (Object) userId);
        return this.basedao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<List> getteachUnitInfo_old(String exam, String grade, String subCompose, String islevel, String levelcalss, String teachUnit, String subject, String userId, String level, String viewAllReports, String sType, String flag) {
        String sql;
        String sql2;
        String sql22;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        String examStr = "";
        if (exam != null && !exam.equals("")) {
            examStr = " and ss.examNum={exam} ";
        }
        String xuankezuheStr = "";
        if (subCompose != null && !subCompose.equals("")) {
            xuankezuheStr = " and xuankezuhe={subCompose} ";
            args.put("subCompose", subCompose);
        }
        if (teachUnit == null || !teachUnit.equals("")) {
        }
        String sTypeStr = "";
        if (sType != null && !sType.equals("")) {
            sTypeStr = " and cl.studentType={sType} ";
            args.put("sType", sType);
        }
        String nameStr = "concat_ws('-',sc.schoolName,c.className) name,";
        if (flag.equals("1")) {
            nameStr = "c.className name,";
        }
        String subjectStr = "";
        if (subject != null && !subject.equals("") && !subject.equals("null")) {
            subjectStr = " and cl.subjectNum={subject} ";
        }
        if (userId.equals("-1") || userId.equals("-2")) {
            sql = "select * from (select pItemId pId,sItemId id,sItemName name,statisticitem,'3' orderNum from statisticitem where examNum={exam}  and Level=0 UNION select 'ZZ' pId,'ZZ1' id,'全区' name,'00','1' UNION select DISTINCT 'ZZ' pid,pItemId id,pItemName,'00','2' from statisticitem ss LEFT JOIN statisticrelation sr  on ss.pItemId = sr.statisticId  where  sr.description='top' and ss.examNum={exam} )t left join (select distinct cl.statisticId from statisticlevel cl where cl.examNum={exam} and cl.gradeNum={grade} " + subjectStr + " and cl.statisticType='0' and cl.source='0' and cl.xuankezuhe='0' " + sTypeStr + "         union          select distinct cl.schoolNum from gradelevel cl where cl.examNUm={exam} and cl.gradeNum={grade} " + subjectStr + " and cl.statisticType='0' and cl.source='0' and cl.xuankezuhe='0' " + sTypeStr + " ) t1 on t.id=t1.statisticId where t1.statisticId is not null order by t.orderNum,statisticitem DESC,convert(name using gbk),id,pid";
            sql2 = "select DISTINCT s.sItemId pId,c.id," + nameStr + " '02' statisticitem from statisticitem s LEFT JOIN classlevel cl on s.sItemId = cl.schoolNum LEFT JOIN class c ON cl.classNum = c.id left join school sc on cl.schoolNum = sc.id where s.examNum={exam} and `Level`=0 and statisticItem='01'  and cl.statisticType='0' and cl.source='0'  and cl.examNum= {exam} and cl.gradeNum={grade} " + xuankezuheStr + sTypeStr + subjectStr;
            if (levelcalss.equals("T")) {
                sql2 = "select DISTINCT s.sItemId pId,c.id," + nameStr + " '02' statisticitem from statisticitem s LEFT JOIN classlevel cl on s.sItemId = cl.schoolNum LEFT JOIN levelclass c ON cl.classNum = c.id left join school sc on cl.schoolNum = sc.id where s.examNum=" + exam + " and c.subjectNum='" + subject + "' and `Level`=0 and statisticItem='01' and cl.statisticType='0' and cl.source='0' and cl.examNum= " + exam + " and cl.gradeNum=" + grade + xuankezuheStr + sTypeStr + subjectStr;
                if (islevel.equals("0")) {
                    sql2 = "select DISTINCT s.sItemId pId,c.id," + nameStr + " '02' statisticitem from statisticitem s LEFT JOIN classlevel_fc cl on s.sItemId = cl.schoolNum LEFT JOIN class c ON cl.classNum = c.id left join school sc on cl.schoolNum = sc.id where s.examNum=" + exam + " and `Level`=0 and statisticItem='01' and cl.statisticType='0' and cl.source='0' and cl.examNum= " + exam + " and cl.gradeNum=" + grade + xuankezuheStr + sTypeStr + subjectStr;
                }
            }
        } else {
            sql = "select * from (select * from (select s.id,s.pId,s.name,s.statisticitem,'1' orderNum from (select s.pid ,t1.topItemId id,t1.topItemName name,s.statisticitem,count(t2.schoolNum) s_s1,COUNT(t1.sItemId) s_s2  FROM (select topItemId,topItemName,sItemId,sItemName,statisticitem FROM statisticitem  where statisticItem='01' and examNum={exam}  ) t1  LEFT JOIN (select schoolNUm from schauthormanage where userId={userId} UNION select schoolNum from user where id= {userId} ) t2 ON t1.sItemId = t2.schoolNum  LEFT JOIN (  select DISTINCT ss.sItemId id,ss.pItemId pId,ss.sItemName name,statisticitem from statisticitem ss where ss.statisticItem = '00' " + examStr + "UNION select 'ZZ1' id,'ZZ' pId,'全区' name,'00' UNION select DISTINCT ss.pItemId id,'ZZ' pid,ss.pItemName name,'00' from statisticitem ss LEFT JOIN statisticrelation sr on ss.pItemId = sr.statisticId  where  sr.description='top' " + examStr + ") s ON t1.topItemId = s.id GROUP BY t1.topItemId HAVING s_s1=s_s2 )s union select s.sitemid,s.id,s.sitemname,s.statisticItem,'2' orderNum from (select t1.sitemid ,t1.sitemname,t1.pitemId id,t1.pitemName name,t1.statisticItem,count(t2.schoolNum) s_s1,COUNT(t1.sItemId) s_s2  FROM (select pitemId,pitemName,sItemId,sItemName,statisticitem FROM statisticitem  where statisticItem='01' and examNum={exam}  ) t1  LEFT JOIN (select schoolNUm from schauthormanage where userId={userId}  UNION select schoolNum from user where id= {userId}  ) t2 ON t1.sItemId = t2.schoolNum left join  (select s.pid ,t1.topItemId id,t1.topItemName name,count(t2.schoolNum) s_s1,COUNT(t1.sItemId) s_s2  FROM (select topItemId,topItemName,sItemId,sItemName FROM statisticitem  where statisticItem='01' and examNum={exam} ) t1  LEFT JOIN (select schoolNUm from schauthormanage where userId={userId} UNION select schoolNum from user where id= {userId} ) t2 ON t1.sItemId = t2.schoolNum  LEFT JOIN (  select DISTINCT ss.sItemId id,ss.pItemId pId,ss.sItemName name from statisticitem ss where ss.statisticItem = '00' " + examStr + "UNION select 'ZZ1' id,'ZZ' pId,'全区' name UNION select DISTINCT ss.pItemId id,ss.topItemId pid,ss.pItemName name from statisticitem ss LEFT JOIN statisticrelation sr on ss.pItemId = sr.statisticId  where  sr.description='top' " + examStr + ") s ON t1.topItemId = s.id GROUP BY t1.topItemId  ) s1 ON t1.pitemid = s1.id  where s1.id is not null GROUP BY t1.pItemId,t1.sitemid HAVING s_s1 = s_s2 )s )t left join (select distinct cl.statisticId from statisticlevel cl  where cl.examNum={exam} and cl.gradeNum={grade} " + subjectStr + " and cl.statisticType='0' and cl.source='0' and cl.xuankezuhe='0' " + sTypeStr + "         union          select distinct cl.schoolNum from gradelevel cl where cl.examNUm={exam} and cl.gradeNum={grade} " + subjectStr + " and cl.statisticType='0' and cl.source='0' and cl.xuankezuhe='0' " + sTypeStr + " ) t1 on t.id=t1.statisticId where t1.statisticId is not null)t order by t.orderNum,statisticitem DESC,convert(name using gbk),id,pid ";
            Map args2 = StreamMap.create().put("userId", (Object) userId);
            String pc = this.basedao._queryStr("SELECT max(permission_class)pc FROM userposition WHERE userNum={userId}  ", args2);
            String sql23 = "select DISTINCT s.sItemId pId,c.id," + nameStr + " '02' statisticitem from statisticitem s LEFT JOIN classlevel cl on s.sItemId = cl.schoolNum LEFT JOIN class c ON cl.classNum = c.id left join school sc on cl.schoolNum = sc.id  LEFT JOIN (select schoolNUm from schauthormanage where userId={userId}  UNION select schoolNum from user where id= {userId} ) t2 ON s.sItemId = t2.schoolNum ";
            if (!pc.equals("1") && (viewAllReports.equals("0") || !pc.equals("null"))) {
                sql23 = sql23 + "left join (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade}  AND userNum={userId} AND subjectNum={subject}  UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade} AND userNum={userId}  AND subjectNum={subject} ) u on u.classNum = cl.classNum ";
            }
            sql2 = sql23 + "where t2.schoolNum is not null and s.examNum={exam} and `Level`=0 and statisticItem='01' and cl.statisticType='0' and cl.source='0' and cl.examNum={exam} and cl.gradeNum={grade} " + xuankezuheStr + sTypeStr + subjectStr;
            if (!pc.equals("1") && (viewAllReports.equals("0") || !pc.equals("null"))) {
                sql2 = sql2 + " and u.classNum is not null";
            }
            if (levelcalss.equals("T")) {
                String sql24 = "select DISTINCT s.sItemId pId,c.id," + nameStr + " '02' statisticitem from statisticitem s LEFT JOIN classlevel cl on s.sItemId = cl.schoolNum ";
                if (subject.length() >= 3) {
                    sql22 = sql24 + "LEFT JOIN levelclass c ON cl.classNum = c.id left join school sc on cl.schoolNum = sc.id ";
                } else {
                    sql22 = sql24 + "LEFT JOIN class c ON cl.classNum = c.id left join school sc on cl.schoolNum = sc.id ";
                }
                String sql25 = sql22 + " LEFT JOIN (select schoolNUm from schauthormanage where userId={userId}  UNION select schoolNum from user where id= {userId} ) t2 ON s.sItemId = t2.schoolNum ";
                if (!pc.equals("1") && (viewAllReports.equals("0") || !pc.equals("null"))) {
                    sql25 = sql25 + "left join (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade}   AND userNum={userId}  AND subjectNum={subject}  UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade}  AND userNum={userId}  AND subjectNum={subject} ) u on u.classNum = cl.classNum ";
                }
                sql2 = sql25 + "where t2.schoolNum is not null and cl.subjectNum={subject}  and s.examNum={exam}  and `Level`=0 and statisticItem='01' and cl.statisticType='0' and cl.source='0' and cl.examNum= {exam} and cl.gradeNum={grade}" + xuankezuheStr + sTypeStr + subjectStr;
                if (!pc.equals("1") && (viewAllReports.equals("0") || !pc.equals("null"))) {
                    sql2 = sql2 + " and u.classNum is not null";
                }
                if (islevel == null || islevel.equals("")) {
                    Map args3 = StreamMap.create().put("grade", (Object) grade);
                    islevel = this.basedao._queryStr("select islevel from grade where gradeNUm={grade} and isdelete='F' limit 0,1", args3);
                }
                if (islevel.equals("0")) {
                    String sql26 = "select DISTINCT s.sItemId pId,c.id," + nameStr + " '02' statisticitem from statisticitem s LEFT JOIN classlevel_fc cl on s.sItemId = cl.schoolNum LEFT JOIN class c ON cl.classNum = c.id left join school sc on cl.schoolNum = sc.id  LEFT JOIN (select schoolNUm from schauthormanage where userId={userId}  UNION select schoolNum from user where id= {userId} ) t2 ON s.sItemId = t2.schoolNum ";
                    if (!pc.equals("1") && (viewAllReports.equals("0") || !pc.equals("null"))) {
                        sql26 = sql26 + "left join (SELECT DISTINCT classNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade}   AND userNum={userId}  AND subjectNum={subject}  UNION ALL SELECT DISTINCT classNum FROM userposition WHERE gradeNum={grade}  AND userNum={userId} AND subjectNum={subject} ) u on u.classNum = cl.classNum ";
                    }
                    sql2 = sql26 + "where t2.schoolNum is not null and s.examNum={exam} and `Level`=0 and statisticItem='01' and cl.statisticType='0' and cl.source='0' and cl.examNum= {exam}  and cl.gradeNum={grade} " + xuankezuheStr + sTypeStr + subjectStr;
                    if (!pc.equals("1") && (viewAllReports.equals("0") || !pc.equals("null"))) {
                        sql2 = sql2 + " and u.classNum is not null";
                    }
                }
            }
            args.put("userId", userId);
        }
        List<List> list = new ArrayList<>();
        List<Map<String, Object>> list1 = this.basedao._queryMapList(sql, TypeEnum.StringObject, args);
        for (Map<String, Object> map : list1) {
            map.put("id", map.get("id") + "");
            map.put("pId", map.get("pId") + "");
        }
        list.add(list1);
        if (level != null && level.equals("0")) {
            List<Map<String, Object>> list2 = this.basedao._queryMapList(sql2 + " ORDER BY pid,c.classname", TypeEnum.StringObject, args);
            for (Map<String, Object> map2 : list2) {
                map2.put("id", map2.get("id") + "");
                map2.put("pId", map2.get("pId") + "");
            }
            list.add(list2);
        }
        return list;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<List> getteachUnitInfo(String exam, String grade, String subCompose, String islevel, String subject, String userId, String level, String viewAllReports, String sType, List<Map<String, Object>> loginTeaPositionList) {
        String ownSql;
        String allClassSql;
        String ownSql1;
        String ownSql2;
        String ownSql3;
        List<List> list = new ArrayList<>();
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        String subject2 = "".equals(subject) ? null : subject;
        String levelclass = getIsLevelClass(exam, grade, subject2, null);
        String sTypeStr = StrUtil.isEmptyOrUndefined(sType) ? "" : " and cl.studentType={sType} ";
        String xuankezuheStr = StrUtil.isEmptyOrUndefined(subCompose) ? "" : " and cl.xuankezuhe={subCompose}  ";
        String subjectStr = StrUtil.isEmptyOrUndefined(subject2) ? "" : " and cl.subjectNum={subject} ";
        String subjectStr2 = StrUtil.isEmptyOrUndefined(subject2) ? "" : " or subjectNum={subject} ";
        String isMoreSchoolSql = "select 'ZZ' pId,cl.schoolNum id,sch.shortname `name`,'01' statisticitem,'1' orderNum from gradelevel cl left join school sch on sch.id = cl.schoolNum where cl.examNum={exam}  and cl.gradeNum={grade}  " + subjectStr + sTypeStr + xuankezuheStr + "group by cl.schoolNum";
        Map args = new HashMap();
        args.put("sType", sType);
        args.put("subCompose", subCompose);
        args.put("subject", subject2);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("userId", userId);
        List _queryBeanList = this.basedao._queryBeanList(isMoreSchoolSql, Leiceng.class, args);
        if (null != _queryBeanList) {
            if (_queryBeanList.size() > 1) {
                arrayList = this.basedao._queryBeanList("SELECT t.pId,t.id,t.name,t.statisticitem FROM ( SELECT pItemId pId,sItemId id,sItemName `name`,statisticitem,'3' orderNum FROM statisticitem WHERE examNum = {exam}  GROUP BY pItemId,sItemId UNION SELECT 'ZZ' pId,'ZZ1' id,'全区' `name`,'00' statisticitem,'1' orderNum UNION all SELECT DISTINCT 'ZZ' pid,pItemId id,pItemName,'00' statisticitem,'2' orderNum FROM statisticitem ss LEFT JOIN statisticrelation sr ON ss.pItemId = sr.statisticId WHERE sr.description = 'top' AND ss.examNum = {exam}  ) t ORDER BY t.orderNum,t.statisticitem,CONVERT(t.name USING gbk)", Leiceng.class, args);
                int i = 0;
                while (i < arrayList.size()) {
                    if ("01".equals(((Leiceng) arrayList.get(i)).getStatisticitem())) {
                        boolean isExist = false;
                        int j = 0;
                        while (true) {
                            if (j >= _queryBeanList.size()) {
                                break;
                            }
                            if (!((Leiceng) arrayList.get(i)).getId().equals(((Leiceng) _queryBeanList.get(j)).getId())) {
                                j++;
                            } else {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            arrayList.remove(i);
                            i--;
                        }
                    }
                    i++;
                }
            } else {
                arrayList = _queryBeanList;
            }
        }
        if ("0".equals(level)) {
            if ("T".equals(levelclass)) {
                if ("0".equals(islevel)) {
                    allClassSql = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel_fc cl INNER JOIN class c on c.id = cl.classNum WHERE cl.examNum = {exam} and cl.gradeNum = {grade} " + subjectStr + sTypeStr + xuankezuheStr + "GROUP BY cl.classNum ORDER BY length(c.className),CONVERT (c.className USING gbk)";
                } else {
                    allClassSql = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel cl INNER JOIN levelclass c on c.id = cl.classNum and c.subjectNum=cl.subjectNum WHERE cl.examNum ={exam} and cl.gradeNum = {grade}  and cl.subjectNum ={subject} " + sTypeStr + xuankezuheStr + "GROUP BY cl.classNum ORDER BY length(c.className),CONVERT (c.className USING gbk)";
                }
            } else {
                allClassSql = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel cl INNER JOIN class c on c.id = cl.classNum WHERE cl.examNum = {exam} and cl.gradeNum = {grade} " + subjectStr + sTypeStr + xuankezuheStr + " GROUP BY cl.classNum  ORDER BY length(c.className),CONVERT (c.className USING gbk)";
            }
            List<?> _queryBeanList2 = this.basedao._queryBeanList(allClassSql, Leiceng.class, args);
            arrayList.addAll(_queryBeanList2);
            if ("-1".equals(userId) || "-2".equals(userId)) {
                arrayList2.addAll(_queryBeanList2);
            } else {
                if ("T".equals(levelclass)) {
                    if ("0".equals(islevel)) {
                        ownSql1 = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel_fc cl INNER JOIN class c on c.id = cl.classNum ";
                        ownSql2 = "WHERE cl.examNum = {exam}  and cl.gradeNum = {grade}  " + subjectStr + sTypeStr + xuankezuheStr;
                        ownSql3 = "GROUP BY cl.classNum ORDER BY cl.schoolNum,length(c.className),CONVERT (c.className USING gbk)";
                    } else {
                        ownSql1 = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel cl INNER JOIN levelclass c on c.id = cl.classNum and c.subjectNum=cl.subjectNum ";
                        ownSql2 = "WHERE cl.examNum = {exam} and cl.gradeNum = {grade}  and cl.subjectNum = {subject}  " + sTypeStr + xuankezuheStr;
                        ownSql3 = "GROUP BY cl.classNum ORDER BY cl.schoolNum,length(c.className),CONVERT (c.className USING gbk)";
                    }
                } else {
                    ownSql1 = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel cl INNER JOIN class c on c.id = cl.classNum ";
                    ownSql2 = "WHERE cl.examNum = {exam}  and cl.gradeNum = {grade} " + subjectStr + sTypeStr + xuankezuheStr;
                    ownSql3 = "GROUP BY cl.classNum ORDER BY cl.schoolNum,length(c.className),CONVERT (c.className USING gbk)";
                }
                String claPermission = 0 == "1" ? viewAllReports : "1".toString();
                if ("0".equals(claPermission)) {
                    String upSql = ownSql1 + "INNER JOIN (SELECT DISTINCT classNum,schoolNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade} and (type='2' " + subjectStr2 + " ) and userNum={userId}  UNION SELECT DISTINCT classNum,schoolNum FROM userposition WHERE gradeNum={grade}  and (type='2' " + subjectStr2 + " ) and userNum={userId} ) up on up.classNum = cl.classNum LEFT JOIN user u on u.schoolNum = up.schoolNum " + ownSql2 + "and u.id = {userId}  " + ownSql3;
                    List<?> _queryBeanList3 = this.basedao._queryBeanList(upSql, Leiceng.class, args);
                    if ("0".equals(viewAllReports)) {
                        arrayList2.addAll(_queryBeanList3);
                    } else if (null == _queryBeanList3 || _queryBeanList3.size() == 0) {
                        String ownSql4 = ownSql1 + " inner join (select schoolNum from schauthormanage where userId={userId} UNION select schoolNum from user where id= {userId}  ) ss on ss.schoolNum = cl.schoolNum " + ownSql2 + ownSql3;
                        arrayList2 = this.basedao._queryBeanList(ownSql4, Leiceng.class, args);
                    } else {
                        String ownSql5 = ownSql1 + " inner join schauthormanage sm on sm.schoolNum = cl.schoolNum " + ownSql2 + "and sm.userId = {userId} " + ownSql3;
                        arrayList2 = this.basedao._queryBeanList(ownSql5, Leiceng.class, args);
                        arrayList2.addAll(_queryBeanList3);
                    }
                } else {
                    String ownSql6 = ownSql1 + " inner join (select schoolNum from schauthormanage where userId={userId}  UNION select schoolNum from user where id= {userId} ) ss on ss.schoolNum = cl.schoolNum " + ownSql2 + ownSql3;
                    arrayList2 = this.basedao._queryBeanList(ownSql6, Leiceng.class, args);
                }
                if (CollUtil.isNotEmpty(loginTeaPositionList)) {
                    List<String> positionClassNumList = new ArrayList<>();
                    String isAllClass = "0";
                    int i2 = 0;
                    int iLen = loginTeaPositionList.size();
                    while (true) {
                        if (i2 >= iLen) {
                            break;
                        }
                        Map<String, Object> onePositionMap = loginTeaPositionList.get(i2);
                        String permission_grade = Convert.toStr(onePositionMap.get("permission_grade"), "0");
                        String permission_subject = Convert.toStr(onePositionMap.get("permission_subject"), "0");
                        String permission_class = Convert.toStr(onePositionMap.get("permission_class"), "0");
                        String subjectNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_subjectNum));
                        String gradeNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_gradeNum));
                        String classNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_classNum));
                        if ("1".equals(permission_grade)) {
                            if ("1".equals(permission_subject)) {
                                if ("1".equals(permission_class)) {
                                    isAllClass = "1";
                                    break;
                                }
                                if (StrUtil.isNotEmpty(classNum)) {
                                    positionClassNumList.add("cla_" + classNum);
                                }
                                i2++;
                            } else {
                                if (null != subject2 && subject2.equals(subjectNum)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    }
                                    if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add("cla_" + classNum);
                                    }
                                }
                                i2++;
                            }
                        } else {
                            if (null != grade && grade.equals(gradeNum)) {
                                if ("1".equals(permission_subject)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    }
                                    if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add("cla_" + classNum);
                                    }
                                } else if (null != subject2 && subject2.equals(subjectNum)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    }
                                    if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add("cla_" + classNum);
                                    }
                                }
                            }
                            i2++;
                        }
                    }
                    if ("0".equals(isAllClass)) {
                        arrayList2 = (List) arrayList2.stream().filter(m -> {
                            return positionClassNumList.contains(m.getId());
                        }).collect(Collectors.toList());
                    }
                }
            }
        } else {
            if ("-1".equals(userId) || "-2".equals(userId)) {
                ownSql = "select '' pId,g.schoolNum id,g.shortname `name`,g.statisticitem from (select cl.schoolNum,s.shortname,'01' statisticitem from gradelevel cl INNER join school s on s.id = cl.schoolNum where cl.examNum={exam}  and cl.gradeNum={grade}  " + subjectStr + sTypeStr + xuankezuheStr + "group by cl.schoolNum ) g ORDER BY convert(g.shortname using gbk)";
            } else {
                ownSql = "select '' pId,g.schoolNum id,g.shortname `name`,g.statisticitem from (select cl.schoolNum,s.shortname,'01' statisticitem from gradelevel cl INNER join school s on s.id = cl.schoolNum left join schauthormanage h on h.schoolNum = s.id and h.userId={userId}  left join user t on t.schoolNum = s.id and t.id = {userId}  and t.usertype=1 where cl.examNum={exam} and cl.gradeNum={grade} " + subjectStr + sTypeStr + xuankezuheStr + " and (h.schoolNum is not null or t.schoolNum is not null) group by cl.schoolNum ) g ORDER BY convert(g.shortname using gbk)";
            }
            arrayList2 = this.basedao._queryBeanList(ownSql, Leiceng.class, args);
        }
        list.add(arrayList);
        list.add(arrayList2);
        return list;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<List> getteachUnitInfo_AppShenSu(String exam, String grade, String subCompose, String islevel, String subject, String userId, String level, String viewAllReports, String sType, List<Map<String, Object>> loginTeaPositionList, String isSchoolScanCon) {
        String ownSql;
        String allClassSql;
        String ownSql1;
        String ownSql2;
        String ownSql3;
        List<List> list = new ArrayList<>();
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        String subject2 = "".equals(subject) ? null : subject;
        String levelclass = getIsLevelClass(exam, grade, subject2, null);
        String sTypeStr = StrUtil.isEmptyOrUndefined(sType) ? "" : " and cl.studentType={sType} ";
        String xuankezuheStr = StrUtil.isEmptyOrUndefined(subCompose) ? "" : " and cl.xuankezuhe={subCompose}  ";
        String subjectStr = StrUtil.isEmptyOrUndefined(subject2) ? "" : " and cl.subjectNum={subject} ";
        String subjectStr2 = StrUtil.isEmptyOrUndefined(subject2) ? "" : " or subjectNum={subject} ";
        String isMoreSchoolSql = "select 'ZZ' pId,cl.schoolNum id,sch.shortname `name`,'01' statisticitem,'1' orderNum from gradelevel cl left join school sch on sch.id = cl.schoolNum where cl.examNum={exam}  and cl.gradeNum={grade}  " + subjectStr + sTypeStr + xuankezuheStr + "group by cl.schoolNum";
        Map args = new HashMap();
        args.put("sType", sType);
        args.put("subCompose", subCompose);
        args.put("subject", subject2);
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("userId", userId);
        List _queryBeanList = this.basedao._queryBeanList(isMoreSchoolSql, Leiceng.class, args);
        if (null != _queryBeanList) {
            if (_queryBeanList.size() > 1) {
                arrayList = this.basedao._queryBeanList("SELECT t.pId,t.id,t.name,t.statisticitem FROM ( SELECT pItemId pId,sItemId id,sItemName `name`,statisticitem,'3' orderNum FROM statisticitem WHERE examNum = {exam}  GROUP BY pItemId,sItemId UNION SELECT 'ZZ' pId,'ZZ1' id,'全区' `name`,'00' statisticitem,'1' orderNum UNION all SELECT DISTINCT 'ZZ' pid,pItemId id,pItemName,'00' statisticitem,'2' orderNum FROM statisticitem ss LEFT JOIN statisticrelation sr ON ss.pItemId = sr.statisticId WHERE sr.description = 'top' AND ss.examNum = {exam}  ) t ORDER BY t.orderNum,t.statisticitem,CONVERT(t.name USING gbk)", Leiceng.class, args);
                int i = 0;
                while (i < arrayList.size()) {
                    if ("01".equals(((Leiceng) arrayList.get(i)).getStatisticitem())) {
                        boolean isExist = false;
                        int j = 0;
                        while (true) {
                            if (j >= _queryBeanList.size()) {
                                break;
                            }
                            if (!((Leiceng) arrayList.get(i)).getId().equals(((Leiceng) _queryBeanList.get(j)).getId())) {
                                j++;
                            } else {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            arrayList.remove(i);
                            i--;
                        }
                    }
                    i++;
                }
            } else {
                arrayList = _queryBeanList;
            }
        }
        Map<String, Object> map = this.basedao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if ("0".equals(level)) {
            if ("T".equals(levelclass)) {
                if ("0".equals(islevel)) {
                    allClassSql = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel_fc cl INNER JOIN class c on c.id = cl.classNum WHERE cl.examNum = {exam} and cl.gradeNum = {grade} " + subjectStr + sTypeStr + xuankezuheStr + "GROUP BY cl.classNum ORDER BY length(c.className),CONVERT (c.className USING gbk)";
                } else {
                    allClassSql = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel cl INNER JOIN levelclass c on c.id = cl.classNum and c.subjectNum=cl.subjectNum WHERE cl.examNum ={exam} and cl.gradeNum = {grade}  and cl.subjectNum ={subject} " + sTypeStr + xuankezuheStr + "GROUP BY cl.classNum ORDER BY length(c.className),CONVERT (c.className USING gbk)";
                }
            } else {
                allClassSql = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel cl INNER JOIN class c on c.id = cl.classNum WHERE cl.examNum = {exam} and cl.gradeNum = {grade} " + subjectStr + sTypeStr + xuankezuheStr + " GROUP BY cl.classNum  ORDER BY length(c.className),CONVERT (c.className USING gbk)";
            }
            List<?> _queryBeanList2 = this.basedao._queryBeanList(allClassSql, Leiceng.class, args);
            arrayList.addAll(_queryBeanList2);
            if ("-1".equals(userId) || "-2".equals(userId) || null != map || null == isSchoolScanCon || "0".equals(isSchoolScanCon)) {
                arrayList2.addAll(_queryBeanList2);
            } else {
                if ("T".equals(levelclass)) {
                    if ("0".equals(islevel)) {
                        ownSql1 = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel_fc cl INNER JOIN class c on c.id = cl.classNum ";
                        ownSql2 = "WHERE cl.examNum = {exam}  and cl.gradeNum = {grade}  " + subjectStr + sTypeStr + xuankezuheStr;
                        ownSql3 = "GROUP BY cl.classNum ORDER BY cl.schoolNum,length(c.className),CONVERT (c.className USING gbk)";
                    } else {
                        ownSql1 = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel cl INNER JOIN levelclass c on c.id = cl.classNum and c.subjectNum=cl.subjectNum ";
                        ownSql2 = "WHERE cl.examNum = {exam} and cl.gradeNum = {grade}  and cl.subjectNum = {subject}  " + sTypeStr + xuankezuheStr;
                        ownSql3 = "GROUP BY cl.classNum ORDER BY cl.schoolNum,length(c.className),CONVERT (c.className USING gbk)";
                    }
                } else {
                    ownSql1 = "SELECT cl.schoolNum pId,CONCAT('cla_',cl.classNum) id,c.className `name`,'02' statisticitem FROM classlevel cl INNER JOIN class c on c.id = cl.classNum ";
                    ownSql2 = "WHERE cl.examNum = {exam}  and cl.gradeNum = {grade} " + subjectStr + sTypeStr + xuankezuheStr;
                    ownSql3 = "GROUP BY cl.classNum ORDER BY cl.schoolNum,length(c.className),CONVERT (c.className USING gbk)";
                }
                String claPermission = 0 == "1" ? viewAllReports : "1".toString();
                if ("0".equals(claPermission)) {
                    String upSql = ownSql1 + "INNER JOIN (SELECT DISTINCT classNum,schoolNum FROM userposition_record WHERE examNum={exam} and gradeNum={grade} and (type='2' " + subjectStr2 + " ) and userNum={userId}  UNION SELECT DISTINCT classNum,schoolNum FROM userposition WHERE gradeNum={grade}  and (type='2' " + subjectStr2 + " ) and userNum={userId} ) up on up.classNum = cl.classNum LEFT JOIN user u on u.schoolNum = up.schoolNum " + ownSql2 + "and u.id = {userId}  " + ownSql3;
                    List<?> _queryBeanList3 = this.basedao._queryBeanList(upSql, Leiceng.class, args);
                    if ("0".equals(viewAllReports)) {
                        arrayList2.addAll(_queryBeanList3);
                    } else if (null == _queryBeanList3 || _queryBeanList3.size() == 0) {
                        String ownSql4 = ownSql1 + " inner join (select schoolNum from schauthormanage where userId={userId} UNION select schoolNum from user where id= {userId}  ) ss on ss.schoolNum = cl.schoolNum " + ownSql2 + ownSql3;
                        arrayList2 = this.basedao._queryBeanList(ownSql4, Leiceng.class, args);
                    } else {
                        String ownSql5 = ownSql1 + " inner join schauthormanage sm on sm.schoolNum = cl.schoolNum " + ownSql2 + "and sm.userId = {userId} " + ownSql3;
                        arrayList2 = this.basedao._queryBeanList(ownSql5, Leiceng.class, args);
                        arrayList2.addAll(_queryBeanList3);
                    }
                } else {
                    String ownSql6 = ownSql1 + " inner join (select schoolNum from schoolscanpermission where userNum={userId}  UNION select schoolNum from user where id= {userId} ) ss on ss.schoolNum = cl.schoolNum " + ownSql2 + ownSql3;
                    arrayList2 = this.basedao._queryBeanList(ownSql6, Leiceng.class, args);
                }
                if (CollUtil.isNotEmpty(loginTeaPositionList)) {
                    List<String> positionClassNumList = new ArrayList<>();
                    String isAllClass = "0";
                    int i2 = 0;
                    int iLen = loginTeaPositionList.size();
                    while (true) {
                        if (i2 >= iLen) {
                            break;
                        }
                        Map<String, Object> onePositionMap = loginTeaPositionList.get(i2);
                        String permission_grade = Convert.toStr(onePositionMap.get("permission_grade"), "0");
                        String permission_subject = Convert.toStr(onePositionMap.get("permission_subject"), "0");
                        String permission_class = Convert.toStr(onePositionMap.get("permission_class"), "0");
                        String subjectNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_subjectNum));
                        String gradeNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_gradeNum));
                        String classNum = Convert.toStr(onePositionMap.get(Const.EXPORTREPORT_classNum));
                        if ("1".equals(permission_grade)) {
                            if ("1".equals(permission_subject)) {
                                if ("1".equals(permission_class)) {
                                    isAllClass = "1";
                                    break;
                                }
                                if (StrUtil.isNotEmpty(classNum)) {
                                    positionClassNumList.add("cla_" + classNum);
                                }
                                i2++;
                            } else {
                                if (null != subject2 && subject2.equals(subjectNum)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    }
                                    if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add("cla_" + classNum);
                                    }
                                }
                                i2++;
                            }
                        } else {
                            if (null != grade && grade.equals(gradeNum)) {
                                if ("1".equals(permission_subject)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    }
                                    if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add("cla_" + classNum);
                                    }
                                } else if (null != subject2 && subject2.equals(subjectNum)) {
                                    if ("1".equals(permission_class)) {
                                        isAllClass = "1";
                                        break;
                                    }
                                    if (StrUtil.isNotEmpty(classNum)) {
                                        positionClassNumList.add("cla_" + classNum);
                                    }
                                }
                            }
                            i2++;
                        }
                    }
                    if ("0".equals(isAllClass)) {
                        arrayList2 = (List) arrayList2.stream().filter(m -> {
                            return positionClassNumList.contains(m.getId());
                        }).collect(Collectors.toList());
                    }
                }
            }
        } else {
            if ("-1".equals(userId) || "-2".equals(userId) || null != map || null == isSchoolScanCon || "0".equals(isSchoolScanCon)) {
                ownSql = "select '' pId,g.schoolNum id,g.shortname `name`,g.statisticitem from (select cl.schoolNum,s.shortname,'01' statisticitem from gradelevel cl INNER join school s on s.id = cl.schoolNum where cl.examNum={exam}  and cl.gradeNum={grade}  " + subjectStr + sTypeStr + xuankezuheStr + "group by cl.schoolNum ) g ORDER BY convert(g.shortname using gbk)";
            } else {
                ownSql = "select '' pId,g.schoolNum id,g.shortname `name`,g.statisticitem from (select cl.schoolNum,s.shortname,'01' statisticitem from gradelevel cl INNER join school s on s.id = cl.schoolNum left join schoolscanpermission h on h.schoolNum = s.id   left join user t on t.schoolNum = s.id and t.id = {userId}  and t.usertype=1 where cl.examNum={exam} and cl.gradeNum={grade} " + subjectStr + sTypeStr + xuankezuheStr + " and (h.schoolNum is not null or t.schoolNum is not null) group by cl.schoolNum ) g ORDER BY convert(g.shortname using gbk)";
            }
            arrayList2 = this.basedao._queryBeanList(ownSql, Leiceng.class, args);
        }
        list.add(arrayList);
        list.add(arrayList2);
        return list;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer getUserPosition(String userId, String examNum, String gradeNum, String reportNum) {
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("reportNum", reportNum);
        return this.basedao._queryInt("select count(a.type) from (select DISTINCT type from userPosition where userNum={userId} )a inner join (select dvalue from report_position)b on a.type=b.dvalue", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer getUserPositions(String userId) {
        Map args = new HashMap();
        args.put("userId", userId);
        return this.basedao._queryInt("select count(a.type) from (select DISTINCT type from userPosition where userNum={userId} )a inner join (select dvalue from report_position)b on a.type=b.dvalue", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> getSchoolByLeiceng(String leiceng, String userId) {
        String sql;
        String leicengStr = "";
        if (leiceng != null && !leiceng.equals("") && !leiceng.equals("-1")) {
            leicengStr = " and ss.statisticItem='01' and ss.topItemId={leiceng} ";
        }
        Map args = new HashMap();
        args.put("leiceng", leiceng);
        args.put("userId", userId);
        Map<String, Object> map = this.basedao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if (userId.equals("-1") || userId.equals("-2") || null != map) {
            sql = "select s.id sItemId,s.schoolName sItemName from school s left join statisticitem_school ss on s.id = ss.sItemId where 1=1 and s.isDelete='F' " + leicengStr + " group by ss.sItemId order by ss.sItemId+0 ";
        } else {
            sql = "select s.id sItemId,s.schoolName sItemName from school s left join statisticitem_school ss on s.id = ss.sItemId LEFT JOIN (select DISTINCT schoolNum from schoolscanpermission where userNum={userId}  UNION select schoolNum from user where id={userId} ) t on s.id = t.schoolNum where t.schoolNum is not null and s.isDelete='F' " + leicengStr + " GROUP BY ss.sItemId order by t.schoolNum";
        }
        return this.basedao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String cancelBase(String staId) {
        String flag = "T";
        Map args = StreamMap.create().put("staId", (Object) staId);
        int count = this.basedao._execute("delete from statisticitem_school where rootId={staId} ", args);
        if (count < 0) {
            flag = "F";
        }
        return flag;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String isBaseLeiceng(String staId) {
        String flag = "F";
        Map args = StreamMap.create().put("staId", (Object) staId);
        Object rootId = this.basedao._queryObject("select rootId from statisticitem_school where rootId={staId} ", args);
        if (rootId != null && !rootId.equals("")) {
            flag = "T";
        }
        return flag;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public boolean maxMACcheck(String userNum, String fingerprintId) {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put("fingerprintId", (Object) fingerprintId);
        String result = this.basedao._queryStr("select if ((select LENGTH(para) from config where type = 13)> (select COUNT(1) from logindevice where userNum = {userNum} ) || (select COUNT(1) from logindevice where userNum = {userNum}  and fingerprintId ={fingerprintId}  )>0 ,'true','false')", args);
        if (result.equals("false")) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public boolean insertTeacherRecord(String userNum, String userName, String usertype, String deviceName, String fingerprintId, String description) throws SQLException {
        Map args = StreamMap.create().put("userNum", (Object) userNum).put("fingerprintId", (Object) fingerprintId);
        int count = this.basedao._queryInt("select COUNT(1) from logindevice where userNum = {userNum} and fingerprintId ={fingerprintId}  ", args).intValue();
        if (count == 0) {
            Map args2 = StreamMap.create().put("userNum", (Object) userNum).put("userName", (Object) userName).put("usertype", (Object) usertype).put("deviceName", (Object) deviceName).put("fingerprintId", (Object) fingerprintId).put("description", (Object) description);
            this.basedao._execute("insert into logindevice (userNum,userName,userType,deviceName,fingerprintId,description,insertTime) values ({userNum},{userName},{usertype},{deviceName},{fingerprintId},{description},now())", args2);
            return true;
        }
        return true;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public boolean delTeacherRecord(String userNum) {
        Map args = StreamMap.create().put("userNum", (Object) userNum);
        try {
            this.basedao._execute("DELETE from logindevice where userNum ={userNum} ORDER BY insertTime LIMIT 1", args);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String removeYudingSet(String id, String pid) {
        String flag = "F";
        List<RowArg> list = new ArrayList<>();
        Map args = StreamMap.create().put("id", (Object) id).put("pid", (Object) pid);
        list.add(new RowArg("delete from reserveyuejuannum where itemid={id} and pitemid={pid} ", args));
        Map args2 = StreamMap.create().put("id", (Object) id);
        list.add(new RowArg("delete from reserveyuejuannum where pitemid= {id} ", args2));
        try {
            this.basedao._batchExecute(list);
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String setLevel(String grade, String school, String user) {
        String flag = "F";
        Map args = StreamMap.create().put("user", (Object) user).put("grade", (Object) grade);
        try {
            this.basedao._execute("update grade set islevel='1', updateDate=now(),updateUser={user} where isDelete='F' and gradeNum={grade} ", args);
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String cancelLevel(String grade, String school, String user) {
        String flag = "F";
        Map args = StreamMap.create().put("user", (Object) user).put("grade", (Object) grade);
        try {
            this.basedao._execute("update grade set islevel='0', updateDate=now(),updateUser={user} where isDelete='F' and gradeNum={grade} ", args);
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void deleteUserroleByExam(String examNum, String[] gudingRoleNumArr) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        List<Object> roleNumList = this.basedao._queryColList("SELECT roleNum FROM role WHERE examNum = {examNum}  ", args);
        List<Map> mapList = new ArrayList<>();
        for (int i = 0; i < roleNumList.size(); i++) {
            int finalI = i;
            Map args2 = StreamMap.create().put("roleNum", roleNumList.get(finalI));
            mapList.add(args2);
        }
        this.basedao._batchExecute("delete from userrole where roleNum = {roleNum} ", mapList);
        StringBuffer epSql = new StringBuffer();
        epSql.append("select examPaperNum from exampaper ");
        epSql.append("where examNum={examNum} and isHidden='T'");
        Map args3 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        List<Object> epNumList = this.basedao._queryColList(epSql.toString(), args3);
        List<Map> mapList2 = new ArrayList<>();
        for (Object epNum : epNumList) {
            Map args4 = StreamMap.create().put("epNum", epNum);
            mapList2.add(args4);
        }
        this.basedao._batchExecute("delete from userrole_sub where exampaperNum={epNum} ".toString(), mapList2);
        List<Map> mapList3 = new ArrayList<>();
        for (int j = 0; j < gudingRoleNumArr.length; j++) {
            int finalJ = j;
            Map args5 = StreamMap.create().put("type", (Object) gudingRoleNumArr[finalJ]).put("roleNum", (Object) gudingRoleNumArr[finalJ]);
            mapList3.add(args5);
        }
        this.basedao._batchExecute("delete a from userrole a LEFT JOIN (select ur.userNum from userrole ur INNER JOIN role r on r.roleNum = ur.roleNum and r.type={type}  GROUP BY ur.userNum) b on b.userNum = a.userNum where b.userNum is null and a.roleNum={roleNum} ", mapList3);
        Map args6 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        this.basedao._execute("delete FROM role WHERE examNum = {examNum} ", args6);
        Map args7 = StreamMap.create().put("userType", (Object) "1").put("roleNum", (Object) Const.ROLE_TIZUZHANG).put("roleNum2", (Object) "1");
        this.basedao._execute("delete a from userrole a LEFT JOIN (select ur.userNum from userrole ur INNER JOIN questiongroup_user qgu on qgu.userNum = ur.userNum and userType={userType}  where ur.roleNum<>{roleNum}  and ur.roleNum<>{roleNum2}  GROUP BY ur.userNum) b on b.userNum = a.userNum where b.userNum is null and a.roleNum={roleNum} ", args7);
        StringBuffer logSql = new StringBuffer();
        logSql.append("delete from userrole_log ");
        logSql.append("where examNum={examNum}");
        Map args8 = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        this.basedao._execute(logSql.toString(), args8);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public String getDengji(String examNum, String gradeNum, String subjectNum, String fufen) {
        String fufenStr = "";
        if (null != fufen) {
            fufenStr = fufen.equals("1") ? "_fufen" : "";
        }
        String subSql = null == subjectNum ? "" : " and subjectNum={subjectNum} ";
        StringBuffer sql = new StringBuffer();
        sql.append("select average_dengji from arealevel" + fufenStr);
        sql.append(" where examNum={examNum} and gradeNum={gradeNum} ");
        sql.append(subSql);
        sql.append(" and average_dengji is not null ");
        sql.append(" limit 1 ");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return null == this.basedao._queryObject(sql.toString(), args) ? "F" : "T";
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> getSchoolSetting(String loginuser) {
        Map args = new HashMap();
        args.put("insertuser", loginuser);
        this.basedao._execute("insert INTO schoolpermission (ID,schoolNum,insertUser,insertDate,yifenyiduan,classControl,gradeControl,quControl,rankControl,cengciControl,fenduanControl) SELECT UUID_SHORT(),s.id,{insertuser},NOW(),'1','1','1','1','1','1','1' from school s LEFT JOIN schoolpermission p on s.id=p.schoolNum WHERE p.yifenyiduan is null", args);
        return this.basedao.queryMapList("SELECT s.id schoolnum,s.schoolNum num,s.schoolName,p.ID,p.yifenyiduan,p.classControl,p.gradeControl,p.quControl,p.rankControl,p.cengciControl,p.fenduanControl from school s LEFT JOIN schoolpermission p on s.id=p.schoolNum order by convert(s.schoolName using gbk)");
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> getSchoolLoginSetting(String loginuser) {
        Map args = new HashMap();
        args.put("insertuser", loginuser);
        this.basedao._execute("insert INTO schoollogin (ID,schoolNum,insertUser,insertDate,islogin,starttime,endtime,shuoming) SELECT UUID_SHORT(),s.id,{insertuser},NOW(),'0','','','' from school s LEFT JOIN schoollogin p on s.id=p.schoolNum WHERE p.islogin is null", args);
        return this.basedao.queryMapList("SELECT s.id schoolnum,s.schoolNum num,s.schoolName,p.ID,p.islogin,p.starttime,p.endtime,p.shuoming from school s LEFT JOIN schoollogin p on s.id=p.schoolNum order by num");
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer updateschoolsetting(int id, String status, int insertuser, String type) {
        String sql = "UPDATE schoolpermission set " + type + "={status} ,updateUser={insertuser} , updateDate=NOW() where schoolNum= {id} ";
        Map args = StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) status).put("insertuser", (Object) Integer.valueOf(insertuser)).put("id", (Object) Integer.valueOf(id));
        return Integer.valueOf(this.basedao._execute(sql, args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer updateschoolLoginsetting(int id, String islogin, int insertuser, String allstarttime, String allendtime, String allshuoming) {
        Map args = StreamMap.create().put("islogin", (Object) islogin).put("insertuser", (Object) Integer.valueOf(insertuser)).put("id", (Object) Integer.valueOf(id)).put("allstarttime", (Object) allstarttime).put("allendtime", (Object) allendtime).put("allshuoming", (Object) allshuoming);
        return Integer.valueOf(this.basedao._execute("UPDATE schoollogin set islogin={islogin} ,updateUser={insertuser} , updateDate=NOW(),starttime={allstarttime},endtime={allendtime},shuoming={allshuoming}  where schoolNum= {id} ", args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer addschoolsetting(int schoolnum, String status, int insertuser, String type) {
        String sql = "insert INTO schoolpermission (ID,schoolNum,insertUser,insertDate," + type + ") VALUES (UUID_SHORT(),{schoolnum} ,{insertuser} ,NOW(), {status})";
        Map args = StreamMap.create().put("schoolnum", (Object) Integer.valueOf(schoolnum)).put("insertuser", (Object) Integer.valueOf(insertuser)).put(Const.CORRECT_SCORECORRECT, (Object) status);
        return Integer.valueOf(this.basedao._execute(sql, args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer addschoolLoginsetting(int schoolnum, String islogin, int insertuser, String allstarttime, String allendtime, String allshuoming) {
        Map args = StreamMap.create().put("schoolnum", (Object) Integer.valueOf(schoolnum)).put("insertuser", (Object) Integer.valueOf(insertuser)).put("islogin", (Object) islogin).put("allstarttime", (Object) allstarttime).put("allendtime", (Object) allendtime).put("allshuoming", (Object) allshuoming);
        return Integer.valueOf(this.basedao._execute("insert INTO schoollogin (ID,schoolNum,insertUser,insertDate,islogin,starttime,endtime,shuoming) VALUES (UUID_SHORT(),{schoolnum} ,{insertuser} ,NOW(), {islogin},{allstarttime},{allendtime},{allshuoming})", args));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void uploadLogo(String schoolId, String base64) {
        Object id = this.basedao._queryObject("select id from schoollogo where schoolNum={schoolId}", StreamMap.create().put("schoolId", (Object) schoolId));
        if (null != id) {
            this.basedao._execute("update schoollogo set logo={base64} where id={id}", StreamMap.create().put("base64", (Object) base64).put("id", id));
        } else {
            this.basedao.insert("schoollogo", new String[]{"id", Const.EXPORTREPORT_schoolNum, "logo"}, new Object[]{Long.valueOf(GUID.getGUID()), schoolId, base64});
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void changeAll(String updateuser, String status, String type) {
        String sql = "UPDATE schoolpermission set " + type + "={status} ,updateUser={insertuser} , updateDate=NOW() where " + type + "!={status}";
        Map args = new HashMap();
        args.put(Const.CORRECT_SCORECORRECT, status);
        args.put("insertuser", updateuser);
        this.basedao._execute(sql, args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void changeAllLogin(String updateuser, String allstarttime, String allendtime, String allshuoming, String islogin) {
        Map args = new HashMap();
        args.put("islogin", islogin);
        args.put("insertuser", updateuser);
        if (islogin.equals("0")) {
            args.put("allstarttime", "");
            args.put("allendtime", "");
            args.put("allshuoming", "");
        } else {
            args.put("allstarttime", allstarttime);
            args.put("allendtime", allendtime);
            args.put("allshuoming", allshuoming);
        }
        this.basedao._execute("UPDATE schoollogin set islogin={islogin} ,updateUser={insertuser} , updateDate=NOW(),starttime={allstarttime},endtime={allendtime},shuoming={allshuoming} where islogin!={islogin}", args);
    }

    public void updateSchoolQuota(String[] groupNums, String schoolGroupNum, String schoolNum) {
        String sgWStr;
        Map args = new HashMap();
        args.put("oneGroupNum", groupNums[0]);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("schoolGroupNum", schoolGroupNum);
        List<Map<String, Object>> paramsList = new ArrayList<>();
        if (StrUtil.isEmpty(schoolGroupNum)) {
            int num = Convert.toInt(this.basedao._queryObject("select sum(q.num) from quota q left join user u on u.id=q.insertUser where q.groupNum={oneGroupNum} and u.schoolNum={schoolNum}", args), 0).intValue();
            for (String groupNum : groupNums) {
                Map args_update = new HashMap();
                args_update.put("num", Integer.valueOf(num));
                args_update.put("groupNum", groupNum);
                args_update.put(Const.EXPORTREPORT_schoolNum, schoolNum);
                paramsList.add(args_update);
            }
        } else {
            if ("-2".equals(schoolGroupNum) || "-1".equals(schoolGroupNum)) {
                sgWStr = " and sg.schoolGroupNum is null ";
            } else {
                sgWStr = " and sg.schoolGroupNum={schoolGroupNum} ";
            }
            String selSql = "select u.schoolNum,IFNULL(sum(q.num),0) num from quota q left join user u on u.id=q.insertUser left join schoolgroup sg on sg.schoolNum=u.schoolNum where q.groupNum={oneGroupNum} " + sgWStr + " and u.id is not null group by u.schoolNum";
            List<Map<String, Object>> numList = this.basedao._queryMapList(selSql, TypeEnum.StringObject, args);
            for (String groupNum2 : groupNums) {
                numList.forEach(sch -> {
                    HashMap hashMap = new HashMap();
                    hashMap.put("num", sch.get("num"));
                    hashMap.put("groupNum", groupNum2);
                    hashMap.put(Const.EXPORTREPORT_schoolNum, sch.get(Const.EXPORTREPORT_schoolNum));
                    paramsList.add(hashMap);
                });
            }
        }
        if (CollUtil.isNotEmpty(paramsList)) {
            this.basedao._batchExecute("update schoolquota set num={num} where groupNum={groupNum} and schoolNum={schoolNum}", paramsList);
        }
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public void updateYuguSanpinglv(String groupNum, String yuguSanpinglv, User u) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("yuguSanpinglv", yuguSanpinglv);
        Object id = this.basedao._queryObject("select id from distributeauto where groupNum={groupNum} limit 1", args);
        if (null != id) {
            this.basedao._execute("update distributeauto set yuguSanpinglv={yuguSanpinglv} where groupNum={groupNum}", args);
        }
        this.log.info(StrUtil.format("组内调剂调整题组【{}】的预估三评率为{}，操作人【id:{},username:{}】", new Object[]{groupNum, yuguSanpinglv, u.getId(), u.getUsername()}));
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Object> getUserAssignedSchoolList(String loginUserId) {
        Map args = new HashMap();
        args.put("loginUserId", loginUserId);
        return this.basedao._queryColList("select schoolnum schoolNum from user where id={loginUserId} union select schoolNum from schauthormanage where userId={loginUserId} ", args);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public List<Map<String, Object>> getExamSchList() {
        return this.basedao._queryMapList("select examNum,schoolNum from gradelevel group by examNum,schoolNum", TypeEnum.StringObject, null);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer getTeachUnitShowNum(String num, String examNum, String gradeNum, String subjectNum, String subjectType, String subCompose, String unit_val, String statistic_val, String parentUnit_val, String source, String statisticType) {
        String sql = "";
        String subjectNumStr = "";
        if (!subjectNum.equals("null")) {
            subjectNumStr = " and subjectNum={subjectNum} ";
        }
        Integer minnumOfStudent = 0;
        String[] unit_vals = unit_val.split(Const.STRING_SEPERATOR);
        String[] statistic_vals = statistic_val.split(Const.STRING_SEPERATOR);
        for (int i = 0; i < unit_vals.length; i++) {
            if (statistic_vals[i].equals("00")) {
                sql = "select min(numOfStudent)numOfStudent from statisticlevel where examNum={examNum} and gradeNum={gradeNum} " + subjectNumStr + " and xuankezuhe={subCompose} and studentType={subjectType}  and statisticId={unit_val} and source={source} and statisticType={statisticType}";
            } else if (statistic_vals[i].equals("01")) {
                sql = "select min(numOfStudent)numOfStudent from gradelevel where examNum={examNum} and gradeNum={gradeNum} and schoolNum={unit_val}  " + subjectNumStr + " and xuankezuhe={subCompose} and studentType={subjectType}  and source={source} and statisticType={statisticType} ";
            } else if (statistic_vals[i].equals("02")) {
                sql = "select min(numOfStudent)numOfStudent from classlevel where examNum={examNum} and gradeNum={gradeNum}  and classNum={unit_val} " + subjectNumStr + " and xuankezuhe={subCompose} and studentType={subjectType}    and source={source} and statisticType={statisticType} ";
            }
            Map args = new HashMap();
            args.put(Const.EXPORTREPORT_examNum, examNum);
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args.put("subCompose", subCompose);
            args.put("subjectType", subjectType);
            args.put("unit_val", unit_vals[i]);
            args.put("source", source);
            args.put("statisticType", statisticType);
            Integer numOfStudent = this.basedao._queryInt(sql, args);
            if (i == 0) {
                minnumOfStudent = numOfStudent;
            } else if (numOfStudent.intValue() < minnumOfStudent.intValue()) {
                minnumOfStudent = numOfStudent;
            }
        }
        return minnumOfStudent;
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer getTotalPage(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        return Convert.toInt(this.basedao._queryObject("select totalPage from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", args), 0);
    }

    @Override // com.dmj.service.systemManagement.SystemService
    public Integer submitSch(String schoolnum, String shuoming, String starttime, String endtime, String islogin, String userid) {
        String startStr = " ";
        if (null != starttime && !starttime.equals("")) {
            startStr = startStr + ",starttime={starttime}";
        }
        String endStr = " ";
        if (null != endtime && !endtime.equals("")) {
            endStr = endStr + ",endtime={endtime}";
        }
        String shuomingStr = " ";
        if (null != shuoming && !shuoming.equals("")) {
            shuomingStr = shuomingStr + ",shuoming={shuoming}";
        }
        String sql = "UPDATE schoollogin SET updateUser={userid}, updateDate=NOW()" + startStr + endStr + shuomingStr + " WHERE schoolnum={schoolnum}";
        Map args = new HashMap();
        args.put("shuoming", shuoming);
        args.put("userid", userid);
        args.put("islogin", islogin);
        args.put("starttime", starttime);
        args.put("endtime", endtime);
        args.put("schoolnum", schoolnum);
        return Integer.valueOf(this.basedao._execute(sql, args));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.dmj.service.systemManagement.SystemService
    public Map<String, String> getschoolpermissionMap(String schoolNum, String insertUser) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        Map _querySimpleMap = this.basedao._querySimpleMap("SELECT * from schoolpermission WHERE schoolNum={schoolNum} limit 1", args);
        if (null == _querySimpleMap || _querySimpleMap.size() == 0) {
            this.basedao._execute("insert INTO schoolpermission (ID,schoolNum,insertUser,insertDate,yifenyiduan,classControl,gradeControl,quControl,rankControl,cengciControl,fenduanControl) SELECT UUID_SHORT(),s.id,{insertuser},NOW(),'1','1','1','1','1','1','1' from school s LEFT JOIN schoolpermission p on s.id=p.schoolNum WHERE p.yifenyiduan is null", args);
            _querySimpleMap = this.basedao._querySimpleMap("SELECT * from schoolpermission WHERE schoolNum={schoolNum} limit 1", args);
        }
        return _querySimpleMap;
    }
}
