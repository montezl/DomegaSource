package com.dmj.cs.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.dmj.cs.bean.ClipPageMarkSample;
import com.dmj.cs.bean.CsDefine;
import com.dmj.cs.bean.MissingPaper;
import com.dmj.cs.bean.Question;
import com.dmj.cs.bean.Template;
import com.dmj.cs.bean.TemplateRecord;
import com.dmj.cs.bean.UploadPicFile;
import com.dmj.cs.bean.ZTreeNode;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AnswerQuestionImage;
import com.dmj.domain.Answerexampaperimage;
import com.dmj.domain.ExampaperQuestionimage;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.JsonType;
import com.dmj.util.StaticClassResources;
import com.dmj.util.config.Configuration;
import com.zht.db.Jsons;
import com.zht.db.RowArg;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.xpath.XPathConstants;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/* loaded from: Dbutil.class */
public class Dbutil {
    protected BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    private Logger log = Logger.getLogger(getClass());

    public boolean UpdatePicServerInfo(int[] fileds, String[] values, String user) {
        for (int i = 0; i < fileds.length; i++) {
            int finalI = i;
            Map<String, Object> args = StreamMap.create().put("type", (Object) 4).put("operate", (Object) Integer.valueOf(fileds[finalI])).put("para", (Object) values[finalI]).put("insertUser", (Object) user).put("insertDate", (Object) DateUtil.getCurrentTime());
            Object o = this.dao2._queryObject("select id from config where type={type} and operate={operate}", args);
            args.put("id", o);
            if (o == null) {
                this.dao2._execute("insert config(type,operate,para,insertUser,insertDate) values ({type},{operate},{para},{insertUser},{insertDate})", args);
            } else {
                this.dao2._execute("update config set para={para} where id={id}", args);
            }
        }
        return true;
    }

    public Object[] GetLoginId(String username, String password) {
        String sql;
        if (username.equals("admin")) {
            sql = "select id,1,realname from user where (username={username} or loginname={username})" + ((password == null || password.equals("")) ? "" : "and password=md5({password}) ") + "limit 0,1";
        } else {
            sql = "SELECT u.id,min(s.type),u.realname from user u INNER JOIN schoolscanpermission s on s.userNum=u.id WHERE (username={username} or loginname={username}) and `password`=md5({password})  GROUP BY s.userNum limit 0,1";
        }
        if (StrUtil.isNotBlank(password)) {
            password = password.toLowerCase();
        }
        return this.dao2._queryArray(sql, StreamMap.create().put("username", (Object) username).put("password", (Object) password));
    }

    public List<Object[]> GetNoDoneExamList(String userNum) {
        if (userNum.equals("-1")) {
            return this.dao2.queryArrayList("SELECT  e.examNum,e.examName,e.examDate FROM exam e where e.isDelete='F' and e.status <> '9'  GROUP BY e.examNum order by examDate desc");
        }
        String sql = "SELECT  e.examNum,e.examName,e.examDate FROM exam e  JOIN examschool es ON e.examNum = es.examNum  JOIN schoolscanpermission ss ON (( ss.type = 2 AND es.schoolNum = ss.schoolNum ) OR ss.type = 1 ) where " + (userNum.equals("-1") ? "1=1" : "ss.userNum={userNum}") + " and e.isDelete='F' and e.status <> {status}  GROUP BY e.examNum order by examDate desc";
        return this.dao2._queryArrayList(sql, StreamMap.create().put("userNum", (Object) userNum).put(Const.CORRECT_SCORECORRECT, (Object) "9"));
    }

    public List<Object[]> GetPaperSizeList() {
        return this.dao2._queryArrayList("select value , name from data where type={type} order by orderNum", StreamMap.create().put("type", (Object) "9"));
    }

    public Object GetExamPaperNum(String examNum, String gradeNum, String subjectNum) {
        return this.dao2._queryObject("SELECT examPaperNum from exampaper WHERE examNum={examNum}  and gradeNum={gradeNum} and subjectNum={subjectNum} limit 1 ", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    public Object GetExamPaperNum(String examNum, String gradeNum, String subjectNum, Boolean isCheckSelfOrOut) {
        String sql = "SELECT examPaperNum from exampaper WHERE examNum={examNum} and gradeNum={gradeNum}  and subjectNum={subjectNum}" + (isCheckSelfOrOut.booleanValue() ? " and templateType='1'" : "") + " limit 1 ";
        return this.dao2._queryObject(sql, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    public LinkedHashMap<String, LinkedHashMap<String, Object>> GetQuestionDictionaryMap(String paperNum, String key) {
        return this.dao2._query2OrderMap("call getquestionlist({paperNum})", key, StreamMap.create().put("paperNum", (Object) paperNum));
    }

    public boolean UpdateNotice(String examPaperNum, String examineeInstructions) {
        this.dao2._execute("update exampaper set examineeInstructions={examineeInstructions} where examPaperNum={examPaperNum}", StreamMap.create().put("examineeInstructions", (Object) examineeInstructions).put("examPaperNum", (Object) examPaperNum));
        return true;
    }

    public List<Object[]> GetGradeList(String examNum, String testingCentreId, int pageDoType, boolean isBl, String loginUser) {
        StringBuffer sql = new StringBuffer();
        if (!isBl) {
            String wstr = "";
            switch (pageDoType) {
                case Const.clipError_failure /* 0 */:
                    wstr = " and e.templateType='0'";
                    break;
                case 1:
                    wstr = " and e.templateType='1'";
                    break;
            }
            sql.append(" SELECT DISTINCT g.gradeNum,g.gradeName FROM examinationroom m ");
            sql.append(" join ( ");
            sql.append("    SELECT t.id,t.testingCentreName,ts.examNum from testingcentre_school ts ");
            sql.append("    JOIN examschool es ON es.schoolNum = ts.schoolNum AND ts.examNum = es.examNum ");
            if (!loginUser.equals("-1")) {
                sql.append("    JOIN schoolscanpermission ss on ((ss.type=2 and es.schoolNum=ss.schoolNum) or ss.type=1) ");
            }
            sql.append("    JOIN testingcentre t on t.id = ts.testingCentreId ");
            sql.append("    where ts.examNum={examNum} ");
            sql.append(loginUser.equals("-1") ? "" : " and ss.userNum={loginUser}");
            sql.append(" ) ts ");
            sql.append(" on m.testingCentreId=ts.id and m.examNum = ts.examNum ");
            sql.append(" join basegrade g on g.gradeNum=m.gradeNum ");
            sql.append(" where m.examNum={examNum} ");
            sql.append("-1".equals(testingCentreId) ? "" : " and ts.id ={testingCentreId}");
            sql.append(wstr);
            sql.append(" order by g.gradeNum ");
        } else {
            sql.append(" select DISTINCT g.gradeNum,g.gradeName from cantrecognized c ");
            sql.append(" join ( ");
            sql.append("    SELECT t.id,t.testingCentreName,ts.examNum from testingcentre_school ts ");
            sql.append("    JOIN examschool es on es.schoolNum = ts.schoolNum and ts.examNum=es.examNum ");
            if (!loginUser.equals("-1")) {
                sql.append("    JOIN schoolscanpermission ss on ((ss.type=2 and es.schoolNum=ss.schoolNum) or ss.type=1) ");
            }
            sql.append("    JOIN testingcentre t on t.id = ts.testingCentreId ");
            sql.append("    where ts.examNum={examNum} ");
            sql.append(loginUser.equals("-1") ? "" : " and ss.userNum={loginUser}");
            sql.append(" ) ts ");
            sql.append(" on c.testingCentreId=ts.id ");
            sql.append(" JOIN exampaper p on p.examPaperNum=c.examPaperNum and p.examNum = ts.examNum ");
            sql.append(" join basegrade g on g.gradeNum=p.gradeNum ");
            sql.append(" where p.examNum={examNum} ");
            sql.append("-1".equals(testingCentreId) ? "" : " and ts.id ={testingCentreId}");
            sql.append(" order by g.gradeNum ");
        }
        return this.dao2._queryArrayList(sql.toString(), StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put("loginUser", (Object) loginUser));
    }

    public List<Object[]> GetSubjectList(String examNum, String gradeNum, String testingCentreId, int pageDoType, boolean isBl, String loginUser) {
        StringBuffer sql = new StringBuffer();
        if (!isBl) {
            String wstr = "";
            switch (pageDoType) {
                case Const.clipError_failure /* 0 */:
                    wstr = " and e.templateType='0'";
                    break;
                case 1:
                    wstr = " and e.templateType='1'";
                    break;
            }
            sql.append(" select s.subjectNum,s.subjectName from (  ");
            sql.append(" select subjectNum,testingCentreId,gradeNum,examNum from examinationroom where examNum={examNum} and gradeNum={gradeNum} GROUP BY subjectNum,testingCentreId   ");
            sql.append(" ) m  ");
            sql.append("  LEFT JOIN `subject` s on s.subjectNum=m.subjectNum  ");
            sql.append("  LEFT JOIN exampaper e ON s.subjectNum=e.subjectNum  ");
            sql.append(" join ( ");
            sql.append("    SELECT t.id,t.testingCentreName,ts.examNum from testingcentre_school ts ");
            sql.append("    JOIN examschool es ON es.schoolNum = ts.schoolNum AND ts.examNum = es.examNum ");
            if (!loginUser.equals("-1")) {
                sql.append("    JOIN schoolscanpermission ss on ((ss.type=2 and es.schoolNum=ss.schoolNum) or ss.type=1) ");
            }
            sql.append("    JOIN testingcentre t on t.id = ts.testingCentreId ");
            sql.append("    where ts.examNum={examNum} ");
            sql.append(loginUser.equals("-1") ? "" : " and ss.userNum={loginUser} ");
            sql.append(" ) ts ");
            sql.append(" on m.testingCentreId=ts.id and e.examNum = ts.examNum ");
            sql.append(" WHERE 1=1 ");
            sql.append(wstr);
            sql.append(" and e.gradeNum={gradeNum} and e.examPaperNum=e.pexampaperNum ");
            sql.append(" group by s.subjectNum ORDER BY s.orderNum,s.subjectNum ");
        } else {
            sql.append(" select DISTINCT s.subjectNum,s.subjectName from cantrecognized c ");
            sql.append(" join ( ");
            sql.append("    SELECT t.id,t.testingCentreName,ts.examNum from testingcentre_school ts ");
            sql.append("    JOIN examschool es on es.schoolNum = ts.schoolNum and ts.examNum=es.examNum ");
            if (!loginUser.equals("-1")) {
                sql.append("    JOIN schoolscanpermission ss on ((ss.type=2 and es.schoolNum=ss.schoolNum) or ss.type=1) ");
            }
            sql.append("    JOIN testingcentre t on t.id = ts.testingCentreId ");
            sql.append("    where ts.examNum={examNum} ");
            sql.append(loginUser.equals("-1") ? "" : " and ss.userNum={loginUser}");
            sql.append(" ) ts ");
            sql.append(" on c.testingCentreId=ts.id ");
            sql.append(" JOIN exampaper p on p.examPaperNum=c.examPaperNum and p.examNum = ts.examNum ");
            sql.append(" join `subject` s on s.subjectNum=p.subjectNum ");
            sql.append(" where p.examNum={examNum} and p.examPaperNum=p.pexampaperNum");
            sql.append("-1".equals(testingCentreId) ? "" : " and ts.id ={testingCentreId}");
            sql.append(" order by s.subjectNum ");
        }
        return this.dao2._queryArrayList(sql.toString(), StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("testingCentreId", (Object) testingCentreId).put("loginUser", (Object) loginUser));
    }

    public List<Object[]> GetSchoolList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.dao2._queryArrayList("SELECT s.id,s.schoolName from examinationnum n LEFT JOIN school s on s.id=n.schoolNum where n.examNum={examNum} and n.testingCentreId = {testingCentreId} and n.gradeNum={gradeNum} and n.subjectNum={subjectNum} GROUP BY s.id ORDER BY convert(s.schoolName using gbk)", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    public Map<String, Object> GetExamPaperDictionary(String[] fields, String examNum, String gradeNum, String subjectNum, String templateType) {
        String sl;
        String sl2 = "";
        if (fields != null && fields.length > 0) {
            for (String f : fields) {
                sl2 = sl2 + f + Const.STRING_SEPERATOR;
            }
            sl = sl2.substring(0, sl2.length() - 1);
        } else {
            sl = " * ";
        }
        String ty = templateType.equals("-1") ? "" : "and templateType={templateType} ";
        String sql = "select " + sl + " from exampaper WHERE examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}" + ty;
        return this.dao2._querySimpleMap(sql, StreamMap.create().put("templateType", (Object) templateType).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    public Map<String, Object> GetExamPaperDictionary(String[] fields, String examPaperNum) {
        String sl;
        String sl2 = "";
        if (fields != null && fields.length > 0) {
            for (String f : fields) {
                sl2 = sl2 + f + Const.STRING_SEPERATOR;
            }
            sl = sl2.substring(0, sl2.length() - 1);
        } else {
            sl = " * ";
        }
        String sql = "select " + sl + " from exampaper WHERE exampapernum={examPaperNum} limit 1 ";
        return this.dao2._querySimpleMap(sql, StreamMap.create().put("examPaperNum", (Object) examPaperNum));
    }

    public String GetPaperSizeName(String paperSizeNum) {
        return this.dao2._queryStr("select name from data where value={paperSizeNum}", StreamMap.create().put("paperSizeNum", (Object) paperSizeNum));
    }

    public Object GetTemplateInfo(String examPaperNum, String abtype) {
        return this.dao2._queryObject("select templateInfo from template where exampapernum={exampapernum} and type={abtype} limit 1", StreamMap.create().put("exampapernum", (Object) examPaperNum).put("abtype", (Object) abtype));
    }

    public String getNotExistTemplateInfo(String examPaperNum, boolean isAb) {
        Object oo = this.dao2._queryObject("SELECT GROUP_CONCAT(type) from template WHERE exampaperNum={exampaperNum}", StreamMap.create().put("exampapernum", (Object) examPaperNum));
        if (oo != null) {
            String info = oo.toString();
            if (isAb) {
                int a = info.indexOf("A");
                int b = info.indexOf("B");
                if (a == -1 && b == -1) {
                    return "还未制作模板";
                }
                if (a == -1) {
                    return "还未制作A卷模板";
                }
                if (b == -1) {
                    return "还未制作B卷模板";
                }
                return "0";
            }
            if (info.indexOf("N") == -1) {
                return "还未制作模板";
            }
            return "0";
        }
        return "还未制作模板";
    }

    public List<Object[]> GetExtTemplateInfo(String examPaperNum) {
        return this.dao2._queryArrayList("select type,templateInfo from template where exampapernum={exampapernum}", StreamMap.create().put("exampapernum", (Object) examPaperNum));
    }

    public List<Object[]> GetTemplateImage(String examPaperNum, String abtype) {
        return this.dao2._queryArrayList("SELECT img,page from answersheetstemplateimage WHERE exampapernum={exampapernum} and type={type} order by page", StreamMap.create().put("exampapernum", (Object) examPaperNum).put("type", (Object) abtype));
    }

    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return f.format(date);
    }

    public void saveClipPageMark(Map<Integer, byte[]> clipPageMarkImgMap, String examNum, String gradeNum, String subjectNum, String abType, String scannerNum, boolean isMother, String location, String filename, int imgpathId, String user) throws Throwable {
        if (clipPageMarkImgMap == null || clipPageMarkImgMap.size() == 0) {
            return;
        }
        String clipPageMarkPicDirUrl = filename + "/22/" + gradeNum + "-" + subjectNum + "/" + scannerNum + "/" + abType + "/" + GUID.getGUIDStr();
        String time = DateUtil.getCurrentTime();
        for (Map.Entry<Integer, byte[]> item : clipPageMarkImgMap.entrySet()) {
            int page = item.getKey().intValue();
            byte[] imgBytes = item.getValue();
            if (imgBytes != null && imgBytes.length > 0) {
                String img = clipPageMarkPicDirUrl + "/" + page + ".png";
                CsUtils.writeByteArrayToFile(location + "/" + img, imgBytes);
                ClipPageMarkSample sample = new ClipPageMarkSample();
                sample.setId(GUID.getGUIDStr());
                sample.setExamNum(Integer.valueOf(examNum));
                sample.setGradeNum(Integer.valueOf(gradeNum));
                sample.setSubjectNum(Integer.valueOf(subjectNum));
                sample.setAbType(abType);
                sample.setPage(Integer.valueOf(page));
                sample.setScannerNum(scannerNum);
                sample.setMother(isMother);
                sample.setImg(img);
                sample.setImgpath(Integer.valueOf(imgpathId));
                sample.setInsertUser(user);
                sample.setUpdateUser(user);
                sample.setInsertDate(time);
                sample.setUpdateDate(time);
                this.dao2.save(sample);
            }
        }
    }

    public Object GetExamineeNumLength(String examNum, String testingCentreId, String gradeNum, String subjectNum) {
        String sql = "SELECT LENGTH(examineeNum) l  FROM examinationnum where examNum={examNum} " + (IsNullOrEmpty(testingCentreId) ? "" : " and testingCentreId={testingCentreId} ") + (IsNullOrEmpty(gradeNum) ? "" : " and gradeNum={gradeNum} ") + (IsNullOrEmpty(subjectNum) ? "" : " and subjectNum={subjectNum} ") + " GROUP BY  l  ORDER BY count(1) desc LIMIT 1";
        return this.dao2._queryObject(sql, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    private boolean IsNullOrEmpty(String str) {
        return str == null || str.equals("");
    }

    public boolean DelTemplate(String examPaperNum) {
        List<String> sqllist = new ArrayList<>();
        sqllist.add("DELETE FROM answersheetstemplateimage where exampapernum={exampapernum}");
        sqllist.add("DELETE FROM template where exampapernum={exampapernum}");
        this.dao2._batchExecute(sqllist, StreamMap.create().put("exampapernum", (Object) examPaperNum));
        return true;
    }

    public boolean IsExistDefineData(String examPaperNum) {
        Object oo = this.dao2._queryObject("SELECT id from define WHERE exampapernum={exampapernum} limit 0,1", StreamMap.create().put("exampapernum", (Object) examPaperNum));
        if (null == oo) {
            return false;
        }
        return true;
    }

    public String GetExamRoomStr(String examRoomNum) {
        return this.dao2._queryStr("SELECT examinationRoomNum FROM examinationroom WHERE id={examRoomNum}", StreamMap.create().put("examRoomNum", (Object) examRoomNum));
    }

    public int[] GetStuCountInfo(String scanType, String examNum, String paperNum, String testingCentreId, String gradeNum, String examRoomNum) {
        int[] re = new int[2];
        if ("1".equals(scanType)) {
            Map<String, Object> map = this.dao2._querySimpleMap("SELECT count(em.studentId) as c ,ep.totalPage  from examinationnum em   LEFT JOIN exampaper ep on em.examNum=ep.examNum and em.gradeNum=ep.gradeNum and em.subjectNum=ep.subjectNum  where ep.exampapernum={exampapernum} and em.testingCentreId={testingCentreId} and em.gradeNum={gradeNum} ", StreamMap.create().put("exampapernum", (Object) paperNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum));
            if (map != null && map.size() > 0) {
                re[0] = Integer.parseInt(map.get("c").toString());
                re[1] = re[0] * Integer.parseInt(map.get("totalPage").toString());
            }
        } else {
            Map args = StreamMap.create().put("examRoomNum", (Object) examRoomNum).put("exampapernum", (Object) paperNum);
            Object studentCount = this.dao2._queryObject("SELECT count(1) from examinationnum WHERE examinationRoomNum={examRoomNum}", args);
            re[0] = studentCount == null ? 0 : Integer.parseInt(studentCount.toString());
            Object totalpageCount = this.dao2._queryObject("SELECT totalPage from exampaper WHERE exampapernum={exampapernum}", args);
            re[1] = re[0] * (totalpageCount == null ? 0 : Integer.parseInt(totalpageCount.toString()));
        }
        return re;
    }

    public List<Object[]> GetScanBatchList(String examNum, String gradeNum, String testingCentreId, String subjectNum, String scannerNum, String loginUser) {
        String str = "";
        if (!loginUser.equals("-1")) {
            str = " and scannerNum={scannerNum} ";
        }
        String sql = "SELECT batch,insertDate,scannerIp,scannerNum from batch where examNum={examNum} and gradeNum={gradeNum} and testingCentreId={testingCentreId} and subjectNum={subjectNum} " + str + " order by insertDate,scannerNum desc";
        return this.dao2._queryArrayList(sql, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    public List<Object[]> GetBatchList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.dao2._queryArrayList("SELECT b.batch,scannerIp,b.insertDate,b.scannerNum,count(r.id) from batch b  left join regexaminee r on r.batch=b.batch WHERE b.examNum={examNum} and b.gradeNum={gradeNum} and b.subjectNum={subjectNum} and b.testingCentreId={testingCentreId} GROUP BY b.batch", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("testingCentreId", (Object) testingCentreId));
    }

    public Map<String, Object> GetRegCountByScanBatch(String examPaperNum, String testingCentreId) {
        return this.dao2._queryOrderMap("SELECT batch,count(1) from regexaminee WHERE examPaperNum={examPaperNum} and testingCentreId={testingCentreId} GROUP BY batch", TypeEnum.StringObject, StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCentreId));
    }

    public boolean AddScanBatch(String examNum, String gradeNum, String testingCentreId, String subjectNum, String scannerNum, String batchNo, String loginUser, String picBatchNum) {
        Object batch = this.dao2._queryObject("select batch from batch where batch={batch} limit 1", StreamMap.init("batch", batchNo));
        if (!ObjectUtil.isEmpty(batch)) {
            return true;
        }
        int batchName = 1;
        Object max = this.dao2._queryObject("SELECT max(scannerIp) from batch where scannerNum ={scannerNum}  and examNum={examNum} and testingCentreId={testingCentreId} and gradeNum={gradeNum}  and subjectNum={subjectNum}", StreamMap.create().put("scannerNum", (Object) scannerNum).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
        if (null != max) {
            batchName = Integer.valueOf(max.toString()).intValue() + 1;
        }
        String[] fileds1 = {"id", Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_subjectNum, Const.EXPORTREPORT_gradeNum, "insertDate", "testingCentreId", "insertUser", "batch", "scannerNum", "scannerIp", "picBatchNum"};
        Object[] values1 = {Long.valueOf(GUID.getGUID()), examNum, subjectNum, gradeNum, DateUtil.getCurrentTime(), testingCentreId, loginUser, batchNo, scannerNum, Integer.valueOf(batchName), picBatchNum};
        this.dao2.insert("batch", fileds1, values1);
        return true;
    }

    public boolean DelScanBatch(String batch) {
        this.dao2._execute("DELETE FROM batch WHERE batch={batch}", StreamMap.create().put("batch", (Object) batch));
        return true;
    }

    public List<Object[]> GetFinishedExamRoomList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        return this.dao2._queryArrayList("Call querylist({examNum},{subjectNum},{gradeNum},{testingCentreId})", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("testingCentreId", (Object) testingCentreId));
    }

    public List<Object[]> GetFinishedExamSubjectList(String examNum, String gradeNum, String testingCentreId) {
        return this.dao2._queryArrayList("call querylist_batch({examNum},{gradeNum},{testingCentreId})", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("testingCentreId", (Object) testingCentreId));
    }

    public List<Object[]> GetUploadList(String examNum, String gradeNum, String subjectNum, String testingCentreId, int count) {
        String sql = "select u.id,CONCAT(u2.realname,' ( ',SUBSTR(u.insertdate,6),' [ ',u.count,'/" + count + " ] )') ,u.state,u.scannernum from uploadpicrecord u LEFT JOIN user u2 on u2.id=u.insertuser LEFT JOIN exampaper p on p.examPaperNum=u.exampapernum WHERE p.examNum={examNum} and p.gradeNum={gradeNum} and p.subjectNum={subjectNum}  and u.testingCentreId={testingCentreId}";
        return this.dao2._queryArrayList(sql, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("testingCentreId", (Object) testingCentreId));
    }

    public List<Map<String, Object>> GetUploadMapList(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("testingCentreId", (Object) testingCentreId);
        StringBuffer sbf = new StringBuffer("SELECT count(1) FROM examinationnum WHERE\texamNum ={examNum} AND gradeNum ={gradeNum} AND subjectNum ={subjectNum} AND testingCentreId ={testingCentreId}");
        Object studentCount = this.dao2._queryObject(sbf.toString(), args);
        args.put("studentCount", studentCount);
        sbf.setLength(0);
        sbf.append(" select u.id,u2.realname insertUser,SUBSTR(u.insertDate,6) insertDate, u.count uploadCount, {studentCount} studentCount ,p.totalPage totalPage ,u.state ,u.batchNum from uploadpicrecord u ");
        sbf.append(" LEFT JOIN user u2 on u2.id=u.insertuser ");
        sbf.append(" LEFT JOIN exampaper p on p.examPaperNum=u.exampapernum ");
        sbf.append(" WHERE p.examNum={examNum} and p.gradeNum={gradeNum} and p.subjectNum={subjectNum} and u.testingCentreId={testingCentreId} and u.id is not null");
        return this.dao2._queryMapList(sbf.toString(), TypeEnum.StringObject, args);
    }

    public Map<String, Object> GetUploadDictionary(String id) {
        return this.dao2._querySimpleMap("SELECT scannernum,state,downloadscannernum,batchNum from uploadpicrecord where id= {id}", StreamMap.create().put("id", (Object) id));
    }

    public int GetExamStuCount(String examPaperNum, String testingCentreId) {
        Object oo = this.dao2._queryObject("SELECT count(em.studentId)   from examinationnum em    LEFT JOIN exampaper ep on em.examNum=ep.examNum and em.gradeNum=ep.gradeNum and em.subjectNum=ep.subjectNum  where ep.examPaperNum={examPaperNum} and em.testingCentreId={testingCentreId}", StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCentreId));
        if (oo == null) {
            return 0;
        }
        return Integer.parseInt(oo.toString());
    }

    public Map<String, Object> GetImgPathConfigDictionary(String examNum) {
        return this.dao2._querySimpleMap("select location,filename,id from imgpath where examNum={examNum} and selected=1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum));
    }

    public List<Object[]> GetExamRoomList(String examNum, String gradeNum, String testingCentreId, String subjectNum, boolean isBl) {
        String sql;
        if (!isBl) {
            sql = "SELECT id,examinationRoomName from examinationroom WHERE examNum={examNum} and testingCentreId={testingCentreId} and gradeNum={gradeNum}  and subjectNum={subjectNum} ORDER BY convert(examinationRoomName USING gbk)";
        } else {
            sql = "SELECT DISTINCT em.id,em.examinationRoomName from cantrecognized c right join examinationroom em on em.id=c.examinationRoomNum LEFT JOIN exampaper ep ON ep.examPaperNum=c.examPaperNum  where ep.examNum={examNum} and ep.gradeNum={gradeNum} and ep.subjectNum={subjectNum}  and em.testingCentreId={testingCentreId} order by em.examinationRoomNum";
        }
        return this.dao2._queryArrayList(sql, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    public List<Object[]> GetTestingCenterList(String examNum, String loginUser, boolean isBl) {
        StringBuffer sql = new StringBuffer();
        if (!isBl) {
            sql.append(" SELECT ts.id,ts.testingCentreName FROM examinationroom m ");
            sql.append(" join ( ");
            sql.append("    SELECT t.id,t.testingCentreName,ts.examNum from testingcentre_school ts ");
            sql.append("    JOIN examschool es ON es.schoolNum = ts.schoolNum AND ts.examNum = es.examNum ");
            if (!loginUser.equals("-1")) {
                sql.append("    JOIN schoolscanpermission ss on ((ss.type=2 and es.schoolNum=ss.schoolNum) or ss.type=1) ");
            }
            sql.append("    JOIN testingcentre t on t.id = ts.testingCentreId ");
            sql.append("    where ts.examNum={examNum} ");
            sql.append(loginUser.equals("-1") ? "" : " and ss.userNum={loginUser}");
            sql.append(" ) ts ");
            sql.append(" on m.testingCentreId=ts.id and m.examNum = ts.examNum ");
            sql.append(" GROUP BY ts.id order by convert(ts.testingCentreName using gbk) ");
        } else {
            sql.append(" select DISTINCT ts.id,ts.testingCentreName from cantrecognized c ");
            sql.append(" join ( ");
            sql.append("    SELECT t.id,t.testingCentreName,ts.examNum from testingcentre_school ts ");
            sql.append("    JOIN examschool es on es.schoolNum = ts.schoolNum and ts.examNum=es.examNum ");
            if (!loginUser.equals("-1")) {
                sql.append("    JOIN schoolscanpermission ss on ((ss.type=2 and es.schoolNum=ss.schoolNum) or ss.type=1) ");
            }
            sql.append("    JOIN testingcentre t on t.id = ts.testingCentreId ");
            sql.append("    where ts.examNum={examNum} ");
            sql.append(loginUser.equals("-1") ? "" : " and ss.userNum={loginUser}");
            sql.append(" ) ts ");
            sql.append(" on c.testingCentreId=ts.id ");
            sql.append(" JOIN exampaper p on p.examPaperNum=c.examPaperNum and p.examNum = ts.examNum ");
            sql.append(" GROUP BY ts.id order by convert(ts.testingCentreName using gbk) ");
        }
        return this.dao2._queryArrayList(sql.toString(), StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("loginUser", (Object) loginUser));
    }

    public String GetScannerNum(String batchNo) {
        Object oo = this.dao2._queryObject("SELECT scannerNum from batch where batch={batchNo}", StreamMap.create().put("batchNo", (Object) batchNo));
        if (oo == null) {
            return null;
        }
        return oo.toString();
    }

    public boolean DeleteByOneRegid(String regid, boolean DoObjective, boolean DoSubjective, boolean isDelStuImage) {
        List<Object> subscoreIdList;
        List<Object> subscoreIdList2;
        List<String> list = new ArrayList<>();
        List<RowArg> rowArglist = new ArrayList<>();
        try {
            String location = getImgLocation(regid) + "/";
            Map args = new HashMap();
            args.put("regid", regid);
            Object oo = this.dao2._queryObject("select img from examinationnumimg where regid={regid}", args);
            if (oo != null) {
                list.add(location + oo);
            }
            rowArglist.add(new RowArg("delete from examinationnumimg where regid={regid}", args));
            rowArglist.add(new RowArg("delete from cliperror where regid={regid}", args));
            rowArglist.add(new RowArg("delete from examineenumerror where regid={regid}", args));
            rowArglist.add(new RowArg("delete from teacherappeal where regId={regid}", args));
            rowArglist.add(new RowArg("delete from clippagemark where regid={regid}", args));
            if (isDelStuImage) {
                rowArglist.add(new RowArg("delete from scanrecord where id={regid}", args));
                rowArglist.add(new RowArg("delete from regexaminee where id={regid}", args));
                Object oo2 = this.dao2._queryObject("select img from studentpaperimage where regid={regid}", args);
                if (oo2 != null) {
                    list.add(location + oo2);
                    String bigImg = String.valueOf(oo2);
                    String srcFileName = bigImg.substring(0, bigImg.lastIndexOf(46)) + "_src" + bigImg.substring(bigImg.lastIndexOf(46));
                    list.add(location + srcFileName);
                }
                rowArglist.add(new RowArg("delete from studentpaperimage where regid={regid}", args));
            }
            rowArglist.add(new RowArg("delete from cantrecognized where regid={regid}", args));
            rowArglist.add(new RowArg("delete from illegal where regid={regid}", args));
            Object oo3 = this.dao2._queryObject("select img from illegalimage where regid={regid}", args);
            if (oo3 != null) {
                list.add(location + oo3);
            }
            rowArglist.add(new RowArg("delete from illegalimage where regid={regid}", args));
            Object oo4 = this.dao2._queryObject("select img from exampapertypeimage where regid={regid}", args);
            if (oo4 != null) {
                list.add(location + oo4);
            }
            rowArglist.add(new RowArg("delete from exampapertypeimage where regid={regid}", args));
            rowArglist.add(new RowArg("delete from corner where regid={regid}", args));
            List<Object> rlist = this.dao2._queryColList("SELECT id from choosenamerecord WHERE regId={regid} ", args);
            new ArrayList();
            for (int k = 0; rlist != null && k < rlist.size(); k++) {
                Map one = StreamMap.create().put("scoreid", rlist.get(k)).put("id", rlist.get(k));
                Object oo5 = this.dao2._queryObject("select img from questionimage where scoreid={scoreid}", one);
                if (oo5 != null) {
                    list.add(location + oo5);
                }
                rowArglist.add(new RowArg("delete from choosenamerecord where id={id}", one));
            }
            List<Object> scoreIdList = new ArrayList<>();
            if (DoSubjective && null != (subscoreIdList2 = this.dao2._queryColList("select id  from score WHERE regid={regid}", args))) {
                scoreIdList.addAll(subscoreIdList2);
            }
            if (DoObjective && null != (subscoreIdList = this.dao2._queryColList("select id  from objectivescore WHERE regid={regid}", args))) {
                scoreIdList.addAll(subscoreIdList);
            }
            for (int j = 0; scoreIdList != null && j < scoreIdList.size(); j++) {
                Map<String, Object> arg = StreamMap.create().put("scoreid", scoreIdList.get(j));
                Object oo6 = this.dao2._queryObject("select img from questionimage where scoreId={scoreid}", arg);
                if (oo6 != null) {
                    list.add(location + oo6);
                }
                rowArglist.add(new RowArg("delete from questionimage where scoreId={scoreid}", arg));
                Object oo7 = this.dao2._queryObject("select scoreimg from scoreimage where scoreId={scoreid}", arg);
                if (oo7 != null) {
                    list.add(location + oo7);
                }
                rowArglist.add(new RowArg("delete from scoreimage where scoreId={scoreid}", arg));
                rowArglist.add(new RowArg("delete from score where id={scoreid}", arg));
                rowArglist.add(new RowArg("delete from task where scoreId={scoreid}", arg));
                rowArglist.add(new RowArg("delete from reMark where scoreId={scoreid}", arg));
                rowArglist.add(new RowArg("delete from objectivescore where id={scoreid}", arg));
                Object oo8 = this.dao2._queryObject("select img from objitem where scoreid={scoreid}", arg);
                if (oo8 != null) {
                    list.add(location + oo8);
                }
                rowArglist.add(new RowArg("delete from objitem where scoreid={scoreid}", arg));
                rowArglist.add(new RowArg("delete from tag where scoreId={scoreid}", arg));
                rowArglist.add(new RowArg("delete from markerror where scoreId={scoreid}", arg));
                Object oo9 = this.dao2._queryObject("select img from remarkimg where scoreId={scoreid}", arg);
                if (oo9 != null) {
                    list.add(location + oo9);
                }
                rowArglist.add(new RowArg("delete from remarkimg where scoreId={scoreid}", arg));
                rowArglist.add(new RowArg("delete from testquestion where scoreId={scoreid}", arg));
                rowArglist.add(new RowArg("delete from test_task where scoreId={scoreid}", arg));
            }
            if (rowArglist.size() > 0) {
                this.dao2._batchExecute(rowArglist);
            }
            FileUtil.deleteFiles(list);
            return true;
        } catch (Exception eeee) {
            throw new RuntimeException(eeee);
        }
    }

    public List<Object> GetRegIdListByCNum(String CNum) {
        return this.dao2._queryColList("SELECT id from regexaminee where cNum={CNum}", StreamMap.create().put("CNum", (Object) CNum));
    }

    public Object GetObject(String sql) {
        return this.dao2._queryObject(sql, null);
    }

    public List<Object> GetColList(String sql) {
        return this.dao2._queryColList(sql, null);
    }

    public boolean Execute(String sql) {
        this.dao2._execute(sql, null);
        return true;
    }

    public List<Object> GetScanRecord(String examPaperNum, String testingCentreId, String examRoomNum, String batch, String scanType) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCentreId).put("examRoomNum", (Object) examRoomNum).put("batch", (Object) batch);
        if ("0".equals(scanType)) {
            return this.dao2._queryColList("select id  from scanrecord WHERE examPaperNum={examPaperNum} and testingCentreId= {testingCentreId} and examinationRoomNum={examRoomNum}  UNION  select id  from regexaminee WHERE examPaperNum={examPaperNum} and testingCentreId=' {testingCentreId} and examinationRoomNum={examRoomNum}  GROUP BY id ", args);
        }
        return this.dao2._queryColList("select id  from scanrecord WHERE examPaperNum={examPaperNum} and batch in ({batch[]})  UNION  select id  from regexaminee WHERE examPaperNum={examPaperNum} and batch in ({batch[]})  GROUP BY id ", args);
    }

    public boolean DelCorrectStatus(String examPaperNum, String testingCentreId, String examRoomNum, String scanType) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCentreId).put("examRoomNum", (Object) examRoomNum);
        if ("1".equals(scanType)) {
            Object o = this.dao2._queryObject("SELECT id from regexaminee where examPaperNum={examPaperNum} and testingCentreId={testingCentreId} limit 1 ", args);
            if (null == o) {
                this.dao2._execute("delete from correctstatus where examPaperNum={examPaperNum} and testingCentreId={testingCentreId}", args);
                return true;
            }
            return true;
        }
        this.dao2._execute("delete from correctstatus WHERE examPaperNum={examPaperNum} and examnitionRoom={examRoomNum}", args);
        return true;
    }

    public String GetStuPaperImgIdList(String examPaperNum, String testingCentreId) {
        List<Map<String, Object>> map = this.dao2._queryMapList("SELECT r.id,e.examinationRoomNum,r.cNum,r.type,r.batch from regexaminee r LEFT JOIN examinationroom e on r.examinationRoomNum=e.id  where r.examPaperNum={examPaperNum} and r.testingCentreId={testingCentreId}", TypeEnum.StringObject, StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCentreId));
        return JSONObject.toJSONString(map);
    }

    public String GetImage(String id) {
        return this.dao2._queryStr("SELECT  img from studentpaperimage WHERE regId={id}", StreamMap.create().put("id", (Object) id));
    }

    public Object[] GetOneUploadPicRecord(String id) {
        return this.dao2._queryArray("SELECT scannernum,state,downloadscannernum,batchNum from uploadpicrecord where id={id}", StreamMap.create().put("id", (Object) id));
    }

    public boolean UpdateOneUploadPicRecord(String id, String state, String scannerNum, String count, String userId) {
        StringBuffer sql = new StringBuffer("update uploadpicrecord set enddate={enddate},doUser={doUser},done={done} ");
        if (StrUtil.isNotEmpty(count)) {
            sql.append(" ,count=count+{count} ");
        }
        if (StrUtil.isNotEmpty(scannerNum)) {
            sql.append(" ,downloadscannernum={downloadscannernum} ");
        }
        if (state.equals("10")) {
            sql.append(" ,unlocked={unlocked} ");
            state = "1";
        }
        if (StrUtil.isNotEmpty(state)) {
            sql.append(" ,state={state} ");
        }
        sql.append("  where id={id} ");
        String finalState = state;
        this.dao2._execute(sql.toString(), StreamMap.create().put("enddate", (Object) DateUtil.getCurrentTime()).put("doUser", (Object) userId).put("done", (Object) 0).put("state", (Object) finalState).put("count", (Object) count).put("downloadscannernum", (Object) scannerNum).put("unlocked", (Object) 1).put("id", (Object) id));
        return true;
    }

    public boolean SaveQuestionOrAnswerImage(Question q) {
        String File;
        Map<String, Object> array = GetImgPathConfigDictionary(q.getExamNum());
        Map<String, String> args = StreamMap.create().put("examPaperNum", (Object) q.getExamPaperNum()).put("questionNum", (Object) q.getQuestionNum());
        AnswerQuestionImage answerQuestionImage = null;
        ExampaperQuestionimage exampaperQuestionimage = null;
        this.log.info("【保存小切图】---------" + q.getTableName() + "------------------start-----------------");
        if (q.getTableName().equals("answerquestionimage")) {
            answerQuestionImage = (AnswerQuestionImage) this.dao2._queryBean("select * from " + q.getTableName() + " where examPaperNum={examPaperNum} and questionNum={questionNum}", AnswerQuestionImage.class, args);
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------查询answerQuestionImage数据--------试卷编号：【" + q.getExamPaperNum() + "】------题号（questionNum）：" + q.getQuestionNum());
            String delsql = "delete from " + q.getTableName() + " where examPaperNum={examPaperNum} and questionNum={questionNum}";
            this.dao2._execute(delsql, q);
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------删除answerQuestionImage数据--------试卷编号：【" + q.getExamPaperNum() + "】------题号（questionNum）：" + q.getQuestionNum());
            String img = "qietu/daan/" + q.getQuestionNum() + ".txt";
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------路径：【" + img + "】");
            this.dao2.insert(q.getTableName(), new String[]{"examPaperNum", "img", "questionNum", "insertUser", "insertDate", "page", "width", "height", "imgpath", "description"}, new Object[]{q.getExamPaperNum(), img, q.getQuestionNum(), q.getInsertUser(), q.getInsertDate(), Integer.valueOf(q.getPage()), Integer.valueOf(q.getWidth()), Integer.valueOf(q.getHeight()), Integer.valueOf(q.getImgpath()), "dmj"});
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------添加新切图的answerQuestionImage数据");
        } else {
            exampaperQuestionimage = (ExampaperQuestionimage) this.dao2._queryBean("select * from " + q.getTableName() + " where examPaperNum={examPaperNum} and questionNum={questionNum}", ExampaperQuestionimage.class, args);
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------查询answerQuestionImage数据--------试卷编号：【" + q.getExamPaperNum() + "】------题号（questionNum）：" + q.getQuestionNum());
            String delsql2 = "delete from " + q.getTableName() + " where examPaperNum={examPaperNum} and questionNum={questionNum}";
            this.dao2._execute(delsql2, q);
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------删除answerQuestionImage数据--------试卷编号：【" + q.getExamPaperNum() + "】------题号（questionNum）：" + q.getQuestionNum());
            String img2 = "qietu/yuanti/" + q.getQuestionNum() + ".txt";
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------路径：【" + img2 + "】");
            this.dao2.insert(q.getTableName(), new String[]{"examPaperNum", "img", "questionNum", "insertUser", "insertDate", "page", "width", "height", "imgpath", "description"}, new Object[]{q.getExamPaperNum(), img2, q.getQuestionNum(), q.getInsertUser(), q.getInsertDate(), Integer.valueOf(q.getPage()), Integer.valueOf(q.getWidth()), Integer.valueOf(q.getHeight()), Integer.valueOf(q.getImgpath()), "dmj"});
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------添加新切图的exampaperQuestionimage数据");
        }
        try {
            FileUtils.writeByteArrayToFile(new File(array.get("location") + "/" + q.getImg()), q.getFile());
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------添加新切图的answerQuestionImage数据");
            if (answerQuestionImage != null) {
                this.log.info("【保存小切图】---------" + q.getTableName() + "------------------开始删除原来的文件和图片");
                answerQuestionImage.setImg(array.get("location") + "/" + array.get("filename") + "/" + answerQuestionImage.getImg());
                this.log.info("【保存小切图】---------" + q.getTableName() + "------------------原来图片路径：【" + answerQuestionImage.getImg() + "】");
                AnswerQuestionImage fill = answerQuestionImage.fill();
                this.log.info(Boolean.valueOf(new StringBuilder().append("【保存小切图】---------").append(q.getTableName()).append("------------------查询的原来的TXT文件中是否有HTML数据（fill.getHtmlJson()）：【").append(fill.getHtmlJson()).toString() != new StringBuilder().append((Object) null).append("】").toString()));
                if (fill.getHtmlJson() != null) {
                    String s = fill.getHtmlJson().split("img=", fill.getHtmlJson().length() - 3)[1];
                    String delimgPath = s.substring(0, s.length() - 3);
                    this.log.info("【保存小切图】---------" + q.getTableName() + "------------------有数据-------删除路径：【" + array.get("location") + "/" + delimgPath + "】");
                    FileUtil.Delete(array.get("location") + "/" + delimgPath);
                }
                this.log.info("【保存小切图】---------" + q.getTableName() + "------------------删除原来的图片成功！！");
                FileUtil.Delete(array.get("location") + "/" + answerQuestionImage.getImg().toString());
                this.log.info("【保存小切图】---------" + q.getTableName() + "------------------删除原来的TXT文件成功！！！");
            } else if (exampaperQuestionimage != null) {
                exampaperQuestionimage.setImg(array.get("location") + "/" + array.get("filename") + "/" + exampaperQuestionimage.getImg());
                this.log.info("【保存小切图】---------" + q.getTableName() + "------------------原来图片路径：【" + exampaperQuestionimage.getImg() + "】");
                ExampaperQuestionimage fill2 = exampaperQuestionimage.fill();
                this.log.info(Boolean.valueOf(new StringBuilder().append("【保存小切图】---------").append(q.getTableName()).append("------------------查询的原来的TXT文件中是否有HTML数据（fill.getHtmlJson()）：【").append(fill2.getHtmlJson()).toString() != new StringBuilder().append((Object) null).append("】").toString()));
                if (fill2.getHtmlJson() != null) {
                    String s2 = fill2.getHtmlJson().split("img=", fill2.getHtmlJson().length() - 3)[1];
                    String delimgPath2 = s2.substring(0, s2.length() - 3);
                    this.log.info("【保存小切图】---------" + q.getTableName() + "------------------有数据-------删除路径：【" + array.get("location") + "/" + delimgPath2 + "】");
                    FileUtil.Delete(array.get("location") + "/" + delimgPath2);
                }
                this.log.info("【保存小切图】---------" + q.getTableName() + "------------------删除原来的图片成功！！");
                FileUtil.Delete(array.get("location") + "/" + array.get("filename") + "/" + exampaperQuestionimage.getImg().toString());
                this.log.info("【保存小切图】---------" + q.getTableName() + "------------------删除原来的TXT文件成功！！！");
            }
            String imgpath = StrUtil.format("{}/{}/qietu", new Object[]{array.get("location"), array.get("filename")});
            String fileName = StrUtil.format("{}.txt", new Object[]{q.getQuestionNum()});
            String html = StrUtil.format("<img id='{}' class='bigImg' style='width:98%;height:auto;margin-left: 1%;' @onerror='changeSrc()' src='imageAction!getImage2.action?examNum={}&img={}'/>", new Object[]{q.getImg(), q.getExamNum(), q.getImg()});
            if (q.getTableName().equals("answerquestionimage")) {
                File = StrUtil.format("{}/daan/{}", new Object[]{imgpath, fileName});
            } else {
                File = StrUtil.format("{}/yuanti/{}", new Object[]{imgpath, fileName});
            }
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------添加路径TXT文件路径：【" + File + "】");
            this.log.info("【保存小切图】---------" + q.getTableName() + "------------------添加路径TXT文件：【" + html + "】");
            FileUtil.writeString(html, File, CharsetUtil.CHARSET_UTF_8);
            this.log.info("【保存小切图】---------" + q.getTableName() + "--------------------------写入TXT文件成功！！！");
            this.log.info("【保存小切图】---------------------------end-----------------");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("SaveQuestionOrAnswerImage:" + e.getMessage());
        }
    }

    public List<Object[]> GetQuestionListByQuestionAnswer(String examPaperNum) {
        return this.dao2._queryArrayList("call getquestionlistbyquestionanswer({examPaperNum})", StreamMap.create().put("examPaperNum", (Object) examPaperNum));
    }

    public List<String> GetFinishedIntoDbQuestionOrAnswerList(String examPaperNum, String table) {
        String sql = StrUtil.format("SELECT questionNum from {} WHERE examPaperNum={examPaperNum}", new Object[]{table});
        return this.dao2._queryColList(sql, String.class, StreamMap.create().put("examPaperNum", (Object) examPaperNum));
    }

    public String GetMissingStuList(String testingCentreId, String gradeNum, String subjectNum, String examNum, String examRoomNum) {
        Map<String, String> cache;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("examRoomNum", (Object) examRoomNum).put("testingCentreId", (Object) testingCentreId);
        Object[] array = this.dao2._queryArray("SELECT examPaperNum ,totalPage from exampaper WHERE examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} LIMIT 1;", args);
        if (CsUtils.IsNullOrEmpty(array)) {
            return "";
        }
        String paperNum = Convert.toStr(array[0]);
        args.put("paperNum", paperNum);
        int pageCount = Convert.toInt(array[1]).intValue();
        String examRoomNumStr = "";
        if (StrUtil.isNotEmpty(examRoomNum) && !"-1".equals(examRoomNum)) {
            examRoomNumStr = " and examinationRoomNum={examRoomNum} ";
        }
        StringBuffer innerSbf = new StringBuffer();
        innerSbf.append(StrUtil.format("SELECT studentId,{} page,gradeNum,testingCentreId,examineeNum,examinationRoomNum,subjectNum,seatNum from examinationnum ", new Object[]{1}));
        innerSbf.append(" WHERE examNum={examNum} AND testingCentreId={testingCentreId} AND gradeNum={gradeNum} AND subjectNum={subjectNum} ");
        innerSbf.append(examRoomNumStr);
        for (int i = 2; i <= pageCount; i++) {
            innerSbf.append(" UNION ALL ");
            innerSbf.append(StrUtil.format("SELECT studentId,{} page,gradeNum,testingCentreId,examineeNum,examinationRoomNum,subjectNum,seatNum from examinationnum ", new Object[]{Integer.valueOf(i)}));
            innerSbf.append(" WHERE examNum={examNum} AND testingCentreId={testingCentreId} AND gradeNum={gradeNum} AND subjectNum={subjectNum} ");
        }
        StringBuffer outerSbf = new StringBuffer();
        outerSbf.append("SELECT s.studentName,IFNULL(sl.shortname,sl.schoolName) schoolName,c.className,s.studentId,n.studentId id ,n.examineeNum ,m.examinationRoomName,n.page,n.seatNum ");
        outerSbf.append("from( ");
        outerSbf.append(innerSbf);
        outerSbf.append(" ) n ");
        outerSbf.append(" left join regexaminee r on r.studentId=n.studentId and r.page=n.page and r.testingCentreId=n.testingCentreId and r.examPaperNum={paperNum} ");
        outerSbf.append(" LEFT JOIN student s on n.studentId=s.id LEFT JOIN examinationroom m on m.id=n.examinationRoomNum and m.subjectNum=n.subjectNum LEFT JOIN class c ON c.id=s.classNum ");
        outerSbf.append(" left join school sl on s.schoolNum=sl.id WHERE r.studentId is NULL and s.id is not null order by s.id,n.page limit 100 ");
        List<Map<String, Object>> list = this.dao2._queryMapList(outerSbf.toString(), TypeEnum.StringObject, args);
        if (CollUtil.isNotEmpty(list) && (cache = ScanProcessCache.get(examNum, gradeNum)) != null) {
            list.forEach(m -> {
                String key = m.get("id").toString();
                if (cache.containsKey(key)) {
                    m.put("exist", "1");
                }
            });
        }
        return JSONObject.toJSONString(list);
    }

    public Map<String, Object> GetOneMissStuImageInfo(String examNum, String gradeNum, String subjectNum, String examRoomNum, String testingCentreId, int curPage) {
        Object examPaperNum = GetExamPaperNum(examNum, gradeNum, subjectNum).toString();
        Map args = StreamMap.create().put("examRoomNum", (Object) examRoomNum).put("examPaperNum", examPaperNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        String wh2 = "";
        if (!examRoomNum.equals("-1")) {
            wh2 = " and examinationRoomNum={examRoomNum} ";
        }
        String sql = "select count(1) from( SELECT studentId,page from scanrecord  WHERE  examPaperNum={examPaperNum} and testingCentreId={testingCentreId} " + wh2 + ") r left join  ( SELECT studentId from examinationnum  WHERE  examNum={examNum} and  testingCentreId={testingCentreId} and gradeNum={gradeNum}  and subjectNum={subjectNum} " + wh2 + ") n on  n.studentId=r.studentId WHERE n.studentId is NULL or r.page=0 ";
        Object count = this.dao2._queryObject(sql, args);
        if (count == null) {
            return null;
        }
        int totalNum = Integer.valueOf(count.toString()).intValue();
        if (curPage > totalNum) {
            curPage = totalNum;
        }
        if (curPage < 0) {
            curPage = 0;
        }
        String sql2 = "select r.id,r.studentId,r.cNum,s.img,n.examineeNum  from( SELECT studentId,id,cNum,page,type from scanrecord  WHERE examPaperNum={examPaperNum} and testingCentreId={testingCentreId} " + wh2 + ") r left join  ( SELECT studentId,examineeNum from examinationnum  WHERE  examNum={examNum} and  testingCentreId={testingCentreId} and gradeNum={gradeNum}  and subjectNum={subjectNum} " + wh2 + ") n on  n.studentId=r.studentId LEFT JOIN studentpaperimage s on s.regId=r.id  WHERE n.studentId is NULL  or r.page=0  ORDER BY r.cNum,r.page,r.type limit " + curPage + " ,1";
        Map<String, Object> map = this.dao2._querySimpleMap(sql2, args);
        if (map != null) {
            map.put("examPaperNum", examPaperNum);
            map.put("totalNum", Integer.valueOf(totalNum));
            map.put("curPage", Integer.valueOf(curPage));
            Map<String, Object> array = GetImgPathConfigDictionary(examNum);
            try {
                map.put("file", FileUtils.readFileToByteArray(new File(array.get("location") + "/" + map.get("img"))));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("GetOneMissStuImageInfo:" + e.getMessage());
            }
        }
        return map;
    }

    public String GetStudentId(String testingCentreId, String examNum, String gradeNum, String examineeNum) {
        return this.dao2._queryStr("SELECT studentId FROM examinationnum WHERE testingCentreId={testingCentreId} and examNum={examNum} and gradeNum={gradeNum} and examineeNum={examineeNum}", StreamMap.create().put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("examineeNum", (Object) examineeNum));
    }

    public boolean UpDateStudentId(String studentId, String cNum, String regId, String loginUser, String page) {
        List<String> sqls = new ArrayList<>();
        sqls.add(" UPDATE scanrecord set insertUser={loginUser},insertDate={insertDate}, studentId={studentId} WHERE cNum={cNum}");
        sqls.add(" UPDATE scanrecord set insertUser={loginUser},insertDate={insertDate}, page={page} WHERE id ={regId}");
        sqls.add(" UPDATE scanrecord set insertUser={loginUser},insertDate={insertDate}, page=0 WHERE cNum={cNum} and page={page} and id !={regId}");
        this.dao2._batchExecute(sqls, StreamMap.create().put("loginUser", (Object) loginUser).put("insertDate", (Object) DateUtil.getCurrentTime()).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("cNum", (Object) cNum).put("page", (Object) page).put("regId", (Object) regId));
        return true;
    }

    public boolean IsAb(String curPaperNum) {
        String abtype = this.dao2._queryStr("select abtype from exampaper  where examPaperNum={curPaperNum} limit 1", StreamMap.create().put("curPaperNum", (Object) curPaperNum));
        return "1".equals(abtype) || "true".equals(abtype.toLowerCase());
    }

    public boolean IsAb(String examNum, String gradeNum, String subjectNum) {
        String abtype = this.dao2._queryStr("select abtype from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} limit 1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
        return "1".equals(abtype) || "true".equals(abtype.toLowerCase());
    }

    public boolean equalsAb(String abtype) {
        return "1".equals(abtype) || "true".equals(abtype.toLowerCase());
    }

    public String GetAbType(String cNum) {
        Object[] oo = this.dao2._queryArray("SELECT i.exampaperType,c.id from regexaminee r LEFT JOIN illegal i on i.regId=r.id LEFT JOIN cantrecognized c on r.id=c.regId WHERE r.cNum={cNum} and r.page=1 limit 1", StreamMap.create().put("cNum", (Object) cNum));
        if (oo == null) {
            return null;
        }
        if (oo[1] != null) {
            return Const.sample_error_reRecognized;
        }
        if (oo[0] != null) {
            return oo[0].toString();
        }
        return null;
    }

    public Object[] GetExamPaperNumInfo(String examPaperNum) {
        return this.dao2._queryArray("select e.type,e.scanType,e.templateType,e.paintMode,d.name,0,e.totalPage,e.abtype from exampaper e LEFT join data d ON d.value=e.paperSize where e.examPaperNum={examPaperNum} limit 1", StreamMap.create().put("examPaperNum", (Object) examPaperNum));
    }

    public int GetReClipCount(String examPaperNum, String testingCentreId, String examRoomNum, String page) {
        String examRoomStr = "";
        String testingCentreIdStr = "";
        String pageStr = "";
        if (!"-1".equals(examRoomNum)) {
            examRoomStr = " and c.examinationRoomNum={examRoomNum} ";
        }
        if (!"-1".equals(testingCentreId)) {
            testingCentreIdStr = " and c.testingCentreId={testingCentreId} ";
        }
        if (StrUtil.isNotEmpty(page)) {
            pageStr = " and r.page={page} ";
        }
        String sql = " SELECT count(1) from cantrecognized c LEFT JOIN regexaminee r on r.id=c.regId  where c.examPaperNum={examPaperNum} " + examRoomStr + testingCentreIdStr + pageStr;
        Object cc = this.dao2._queryObject(sql, StreamMap.create().put("examRoomNum", (Object) examRoomNum).put("testingCentreId", (Object) testingCentreId).put("examPaperNum", (Object) examPaperNum).put("page", (Object) page));
        if (cc == null) {
            return 0;
        }
        return Integer.parseInt(cc.toString());
    }

    public Map<String, Object> GetOrUpdateOneClipRecord(String exampaperNum, String testingCentreId, String examRoomNum, String page, String regid, String type, String id) {
        Object ccid;
        String exampaperNum2 = exampaperNum == null ? "" : exampaperNum;
        String testingCentreId2 = testingCentreId == null ? "" : testingCentreId;
        String examRoomNum2 = examRoomNum == null ? "" : examRoomNum;
        String regid2 = regid == null ? "" : regid;
        String type2 = type == null ? "" : type;
        String id2 = id == null ? "" : id;
        String order = "order by c.orderNo, c.id limit 1";
        Object cid = null;
        Map args = new HashMap();
        args.put("regid", regid2);
        args.put("id", id2);
        args.put("insertDate", DateUtil.getCurrentTime());
        switch (Integer.valueOf(type2).intValue()) {
            case -100:
            case 1:
            case 100:
                if (StrUtil.isNotEmpty(id2)) {
                    this.dao2._execute("update cantrecognized set status='0'  where id={id}", args);
                    if (Integer.valueOf(type2).intValue() == -100) {
                        order = " and c.id<{id} order by c.orderNo, c.id desc limit 1 ";
                    } else if (Integer.valueOf(type2).intValue() == 100) {
                        order = " and c.id>{id} order by c.orderNo,c.id limit 1 ";
                    } else if (Integer.valueOf(type2).intValue() == 1) {
                        cid = this.dao2._queryObject("SELECT id from cantrecognized where id={id}", args);
                        if (cid != null) {
                            order = " and c.id = {id} limit 1 ";
                        } else {
                            Object next = this.dao2._queryObject("SELECT id from cantrecognized where id>{id} order by orderNo,id limit 1 ", args);
                            if (next != null) {
                                args.put("next", next);
                                order = " and c.id = {next} limit 1 ";
                            }
                        }
                    }
                } else {
                    this.dao2._execute("update cantrecognized set status='0' where regid={regid}", args);
                }
                String sql = "SELECT c.id, c.regId,s.img,c.examPaperNum ,r.batch,r.cNum ,r.type,r.page,r.examinationRoomNum as examinationRoomNum ,c.stat,i.exampaperType ,r.testingCentreId,r.studentId,c.reason,r.uploadScannerNum,  CONCAT('姓名：',st.studentName,'  ID号：',st.studentId,'  学校：',sl.shortname,'  班级：',cls.className,'  考场：',eroom.examinationRoomName,'  考号：',n.examineeNum ,'  座位号：',n.seatNum) as stuInfo,c.insertDate  from cantrecognized c  LEFT JOIN regexaminee r on r.id=c.regId  LEFT JOIN regexaminee r2 on r2.cNum=r.cNum and r2.page=1  LEFT JOIN illegal i on i.regId=r2.id  LEFT JOIN studentpaperimage s on s.regId=c.regId  LEFT JOIN student st on st.id=r.studentId  LEFT JOIN class cls on cls.id=st.classNum  LEFT JOIN examinationroom eroom on eroom.id=r.examinationRoomNum  left JOIN exampaper p on p.examPaperNum = c.examPaperNum \tleft JOIN examinationnum n on n.examNum= p.examNum and n.gradeNum=p.gradeNum and n.subjectNum=p.subjectNum and n.testingCentreId=r.testingCentreId and n.studentId = r.studentId  LEFT JOIN school sl on sl.id=r.schoolNum  where c.examPaperNum={exampaperNum} " + (StrUtil.isEmpty(page) ? "" : " and r.page={page} ") + (testingCentreId2.equals("-1") ? "" : " and c.testingCentreId={testingCentreId} ") + (examRoomNum2.equals("-1") ? "" : " and c.examinationRoomNum={examRoomNum} ") + " and c.status=0  " + order;
                args.put("exampaperNum", exampaperNum2);
                args.put("testingCentreId", testingCentreId2);
                args.put("examRoomNum", examRoomNum2);
                args.put("cid", cid);
                args.put("page", page);
                Map<String, Object> map = this.dao2._querySimpleMap(sql, args);
                if (map != null && !map.isEmpty()) {
                    this.dao2._execute("update cantrecognized set status='1'  where id={id}", StreamMap.create().put("id", map.get("id")));
                    return map;
                }
                return null;
            case 2:
                if (regid2 != null && !regid2.equals("")) {
                    this.dao2._execute("update cantrecognized set status='0'  where regid={regid}", args);
                }
                Map<String, Object> map2 = this.dao2._querySimpleMap("SELECT c.id, c.regId,s.img,c.examPaperNum ,r.batch,r.cNum ,r.type,r.page,r.examinationRoomNum,c.stat,i.exampaperType ,r.testingCentreId,r.studentId,c.reason,r.uploadScannerNum  from cantrecognized c  LEFT JOIN regexaminee r on r.id=c.regId  LEFT JOIN regexaminee r2 on r2.cNum=r.cNum and r2.page=1  LEFT JOIN illegal i on i.regId=r2.id  LEFT JOIN studentpaperimage s on s.regId=c.regId  where c.id={id} and c.status=0 limit 1", args);
                if (map2 != null && !map2.isEmpty()) {
                    this.dao2._execute("update cantrecognized set status='1'  where id={id}", args);
                    return map2;
                }
                return null;
            case 4:
                if (regid2 != null && !regid2.equals("")) {
                    this.dao2._execute("update cantrecognized set status='0',insertDate={insertDate} where regid={regid}", args);
                    return null;
                }
                return null;
            default:
                if (regid2 != null && !regid2.equals("") && (ccid = this.dao2._queryObject("SELECT id from cantrecognized where regid={regid}", args)) != null) {
                    this.dao2._execute("update cantrecognized set status='0',orderNo=UUID_SHORT() where id={ccid}", StreamMap.create().put("ccid", ccid));
                    return null;
                }
                return null;
        }
    }

    public String GetCantrecognizedId(String regId) {
        return this.dao2._queryStr("SELECT id from cantrecognized where regid={regId}", StreamMap.create().put("regId", (Object) regId));
    }

    public boolean RecoveryCantrecognized(String gradeNum, String subjectNum, String testingCentreId, String examRoomNum) {
        String examRoomStr = "";
        String testingCentreIdStr = "";
        if (!"-1".equals(examRoomNum)) {
            examRoomStr = " and c.examinationRoomNum={examRoomNum} ";
        }
        if (!"-1".equals(testingCentreId)) {
            testingCentreIdStr = " and c.testingCentreId={testingCentreId} ";
        }
        String sql = "update cantrecognized c  LEFT JOIN regexaminee r on r.id=c.regId LEFT JOIN exampaper ep on ep.examPaperNum=c.examPaperNum set c.status='0'  where ep.gradeNum={gradeNum} and ep.subjectNum={subjectNum} and c.status='1'" + testingCentreIdStr + examRoomStr;
        this.dao2._execute(sql, StreamMap.create().put("examRoomNum", (Object) examRoomNum).put("testingCentreId", (Object) testingCentreId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
        return true;
    }

    public Map<String, Map<String, Object>> GetQuestionSingleOrDoubleMarkInfo(String examPaperNum) {
        return this.dao2._query2Map("SELECT qq.questionNum,qq.groupNum,qs.makType from questiongroup_question qq LEFT JOIN questiongroup_mark_setting qs on qq.exampaperNum=qs.exampaperNum and qq.groupNum=qs.groupNum  WHERE qq.exampaperNum={examPaperNum}", "questionNum", StreamMap.create().put("examPaperNum", (Object) examPaperNum));
    }

    public String GetPath(String id) {
        return String.valueOf(this.dao2._queryObject("SELECT CONCAT(s.id,g.gradeNum,st.subjectNum,'/',u.scannernum,'/',batchNum) from uploadpicrecord u  LEFT JOIN exampaper ep on ep.examPaperNum=u.exampapernum LEFT JOIN testingcentre s on u.testingCentreId=s.id  LEFT JOIN basegrade g on g.gradeNum=ep.gradeNum LEFT JOIN subject st on st.subjectNum=ep.subjectNum where u.id={id}", StreamMap.create().put("id", (Object) id)));
    }

    public void UpdaeFilesCount(String id, int Count) {
        Map args = StreamMap.create().put("id", (Object) id).put("Count", (Object) Integer.valueOf(Count));
        if (Count == 0) {
            this.dao2._execute("delete from uploadpicrecord  where id={id}", args);
        } else {
            this.dao2._execute("UPDATE uploadpicrecord SET count={Count} where id={id}", args);
        }
    }

    public List<Object> GetReClipIdList(String examPaperNum, String testingCentreId, String examRoomNum, String page) {
        String examRoomStr = "";
        String schoolStr = "";
        String pageStr = "";
        if (!"-1".equals(examRoomNum)) {
            examRoomStr = " and c.examinationRoomNum={examRoomNum} ";
        }
        if (!"-1".equals(testingCentreId)) {
            schoolStr = " and c.testingCentreId={testingCentreId} ";
        }
        if (StrUtil.isNotEmpty(page)) {
            pageStr = "and r.page={page} ";
        }
        String sql = " SELECT c.id from cantrecognized c LEFT JOIN regexaminee r on r.id=c.regId  where c.examPaperNum={examPaperNum} " + examRoomStr + schoolStr + pageStr + " order by r.page , c.insertDate ";
        return this.dao2._queryColList(sql, StreamMap.create().put("examRoomNum", (Object) examRoomNum).put("testingCentreId", (Object) testingCentreId).put("examPaperNum", (Object) examPaperNum).put("page", (Object) page));
    }

    public List<Object[]> GetChooseIndex(int length, String pnum, String defineid, String studentId, int page) {
        return this.dao2._queryArrayList("call getchooseindex({length}, {pnum}, {defineid}, {studentId},{page})", StreamMap.create().put("length", (Object) Integer.valueOf(length)).put("pnum", (Object) pnum).put("defineid", (Object) defineid).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("page", (Object) Integer.valueOf(page)));
    }

    public boolean UpdateQuestionGroupTotalCount(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        List<Object> groupNumList = this.dao2._queryColList("SELECT groupNum from questiongroup where examPaperNum={examPaperNum}", args);
        if (null == groupNumList) {
            return false;
        }
        for (Object groupNum : groupNumList) {
            Map arg = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("groupNum", groupNum);
            Object count = this.dao2._queryObject("SELECT count(1) from task where examPaperNum={examPaperNum} and groupNum={groupNum}", arg);
            arg.put("count", count);
            if (null != count) {
                this.dao2._execute(" update questiongroup set totalnum={count} where examPaperNum={examPaperNum} and groupNum={groupNum}", arg);
            }
        }
        return true;
    }

    public boolean ResetCorrectstatus(String examNum, String testingCentreId, String gradeNum, String subjectNum, String examRoomNum, boolean scanType) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("testingCentreId", (Object) testingCentreId).put("examRoomNum", (Object) examRoomNum);
        String correctStatusSql = "SELECT id from correctstatus where examNum={examNum} and subjectNum={subjectNum} and  gradeNum={gradeNum} and testingCentreId={testingCentreId} " + (scanType ? " and  examnitionRoom={examRoomNum}" : "") + " limit 0,1";
        Object o = this.dao2._queryObject(correctStatusSql, args);
        if (o != null && !o.toString().equals("")) {
            args.put("id", o);
            this.dao2._execute("update correctstatus set status='0' where id={id}", args);
            return true;
        }
        return true;
    }

    public boolean modifyObjectieveSwithAB(String epNum, String stuId, String cur_page, String reg_id) {
        this.dao2._execute("call modifyobjectieveswithab({epNum}, {stuId}, {cur_page},{reg_id})", StreamMap.create().put("epNum", (Object) epNum).put("stuId", (Object) stuId).put("cur_page", (Object) cur_page).put("reg_id", (Object) reg_id));
        return true;
    }

    public Map<String, Object> GetStudentInfoByExamineeNum(String infoMapJsonStr) {
        Map<String, Object> infoMap = (Map) JSON.parse(infoMapJsonStr);
        String examineeNum = infoMap.get("examineeNum").toString();
        String _examNum = infoMap.get(Const.EXPORTREPORT_examNum).toString();
        String testingCentreId = infoMap.get("testingCentreId").toString();
        String _gradeNum = infoMap.get(Const.EXPORTREPORT_gradeNum).toString();
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT e.studentId,s.classNum,e.examinationRoomNum,e.schoolNum from (SELECT studentId,schoolNum,gradeNum,examinationRoomNum  from examinationnum where examineeNum={examineeNum} and  examNum={_examNum} and testingCentreId={testingCentreId}  and  gradeNum ={_gradeNum} limit 1) e LEFT join student s on s.id=e.studentId and s.schoolNum=e.schoolNum and s.gradeNum=e.gradeNum", StreamMap.create().put("examineeNum", (Object) examineeNum).put("_examNum", (Object) _examNum).put("testingCentreId", (Object) testingCentreId).put("_gradeNum", (Object) _gradeNum));
        return map;
    }

    public String GetGroupNum(String examPaperNum, String studentId, int pageNo) {
        return this.dao2._queryStr("SELECT cNum FROM regexaminee where examPaperNum={examPaperNum}  and studentId={studentId} and page={pageNo} limit 1", StreamMap.create().put("examPaperNum", (Object) examPaperNum).put(Const.EXPORTREPORT_studentId, (Object) studentId).put("pageNo", (Object) Integer.valueOf(pageNo)));
    }

    public String[] UpdateStudentId(String infoMapJsonStr) {
        Map<String, Object> infoMap = (Map) JSON.parse(infoMapJsonStr);
        String examineeNum = infoMap.get("examineeNum").toString();
        String _examNum = infoMap.get(Const.EXPORTREPORT_examNum).toString();
        String _schoolNum = infoMap.get(Const.EXPORTREPORT_schoolNum).toString();
        String _gradeNum = infoMap.get(Const.EXPORTREPORT_gradeNum).toString();
        String _subjectNum = infoMap.get(Const.EXPORTREPORT_subjectNum).toString();
        String _examPaperNum = infoMap.get("examPaperNum").toString();
        CsUtils.getInt(infoMap.get("pageNo"));
        String _groupNum = CsUtils.getString(infoMap.get("groupNum"));
        String _regid = infoMap.get("regid").toString();
        infoMap.get("oldstudentId").toString();
        String username = infoMap.get("username").toString();
        String studentId = infoMap.get(Const.EXPORTREPORT_studentId).toString();
        String examinationRoomNum = CsUtils.getString(infoMap.get("examinationRoomNum"));
        String classNum = CsUtils.getString(infoMap.get(Const.EXPORTREPORT_classNum));
        Map args0 = StreamMap.create().put("_groupNum", (Object) _groupNum).put("_regid", (Object) _regid);
        List<Object[]> existRegList = new ArrayList<>();
        if (!CsUtils.IsNullOrEmpty(_groupNum)) {
            existRegList = this.dao2._queryArrayList("SELECT id,page,studentId,cNum from regexaminee where cNum={_groupNum} and id!={_regid}", args0);
        }
        new ArrayList();
        List<Object[]> regList = this.dao2._queryArrayList("SELECT id,page,studentId,cNum from regexaminee where cNum=(SELECT cnum from regexaminee where id={_regid} LIMIT 1)", args0);
        if (null == regList) {
            return null;
        }
        List<RowArg> rowArgList = new ArrayList<>();
        String studentId0 = GUID.getGUIDStr();
        for (int i = 0; i < existRegList.size(); i++) {
            String _regid2 = existRegList.get(i)[0].toString();
            int _pageNo = CsUtils.getInt(existRegList.get(i)[1]);
            String _oldstudentId = CsUtils.getString(existRegList.get(i)[2]);
            String cnum = CsUtils.getString(existRegList.get(i)[3]);
            Map args1 = StreamMap.create().put("studentId0", (Object) studentId0).put("examinationRoomNum", (Object) examinationRoomNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("_schoolNum", (Object) _schoolNum).put("_regid", (Object) _regid2);
            rowArgList.add(new RowArg("UPDATE regexaminee SET studentId={studentId0},examinationRoomNum={examinationRoomNum},classNum={classNum},schoolNum={_schoolNum}  WHERE id={_regid}", args1));
            rowArgList.add(new RowArg("UPDATE score SET studentId={studentId0},examinationRoomNum={examinationRoomNum},classNum={classNum},schoolNum={_schoolNum}  WHERE regId={_regid}", args1));
            rowArgList.add(new RowArg("UPDATE objectivescore SET studentId={studentId0},examinationRoomNum={examinationRoomNum},classNum={classNum},schoolNum={_schoolNum}  WHERE regId={_regid}", args1));
            rowArgList.add(new RowArg("UPDATE cantrecognized  SET examinationRoomNum={examinationRoomNum},schoolNum={_schoolNum}  WHERE regId={_regid}", args1));
            List<Object> list = this.dao2._queryColList("select id from score where regid = {_regid}", args1);
            if (list != null) {
                for (int k = 0; k < list.size(); k++) {
                    Map args2 = StreamMap.create().put("studentId0", (Object) studentId0).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("_schoolNum", (Object) _schoolNum).put("scoreid", list.get(k));
                    rowArgList.add(new RowArg("UPDATE tag SET studentId={studentId0},classNum={classNum},schoolNum={_schoolNum} where scoreid={scoreid}", args2));
                    rowArgList.add(new RowArg("UPDATE task SET studentId={studentId0} where scoreid={scoreid}", args2));
                }
            }
            Object oo = this.dao2._queryObject("select batch from examineenumerror where regId={_regid}", args1);
            if (null == oo) {
                String[] fileds1 = {"regId", "examPaperNum", Const.EXPORTREPORT_schoolNum, Const.EXPORTREPORT_studentId, "errorType", "examinationRoomNum", "examineeNum", "groupNum", "page", "insertUser", "insertDate", "isDelete", "batch"};
                Object[] values1 = new Object[13];
                values1[0] = _regid2;
                values1[1] = _examPaperNum;
                values1[2] = _schoolNum;
                values1[3] = studentId;
                values1[4] = "1";
                values1[5] = examinationRoomNum;
                values1[6] = examineeNum == null ? studentId : examineeNum;
                values1[7] = cnum;
                values1[8] = Integer.valueOf(_pageNo);
                values1[9] = username;
                values1[10] = DateUtil.getCurrentTime();
                values1[11] = "F";
                values1[12] = oo;
                this.dao2.insert("examineenumerror", fileds1, values1);
            }
            if (_pageNo == 1) {
                Map args3 = StreamMap.create().put("studentId0", (Object) studentId0).put("examinationRoomNum", (Object) examinationRoomNum).put("_schoolNum", (Object) _schoolNum).put("examPaperNum", (Object) _examPaperNum).put("_oldstudentId", (Object) _oldstudentId);
                rowArgList.add(new RowArg("UPDATE illegal SET studentId={studentId0},examinationRoomNum={examinationRoomNum}, schoolNum={_schoolNum} WHERE examPaperNum={examPaperNum} AND studentId={_oldstudentId}", args3));
            }
        }
        this.dao2._batchExecute(rowArgList);
        rowArgList.clear();
        for (int i2 = 0; i2 < regList.size(); i2++) {
            String _regid3 = regList.get(i2)[0].toString();
            int _pageNo2 = CsUtils.getInt(regList.get(i2)[1]);
            String _oldstudentId2 = regList.get(i2)[2].toString();
            Map args4 = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put("examinationRoomNum", (Object) examinationRoomNum).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("_schoolNum", (Object) _schoolNum).put("_regid", (Object) _regid3);
            rowArgList.add(new RowArg("UPDATE regexaminee SET studentId={studentId},examinationRoomNum={examinationRoomNum}, classNum={classNum},schoolNum={_schoolNum}  WHERE id={_regid}", args4));
            rowArgList.add(new RowArg("UPDATE score SET studentId={studentId},examinationRoomNum={examinationRoomNum},classNum={classNum},schoolNum={_schoolNum}  WHERE regId={_regid}", args4));
            rowArgList.add(new RowArg("UPDATE objectivescore SET studentId={studentId},examinationRoomNum={examinationRoomNum},classNum='" + classNum + "',schoolNum={_schoolNum}  WHERE regId={_regid}", args4));
            rowArgList.add(new RowArg("UPDATE cantrecognized  SET examinationRoomNum={examinationRoomNum},schoolNum={_schoolNum}  WHERE regId={_regid}", args4));
            rowArgList.add(new RowArg("DELETE FROM examineenumerror where regId={_regid}", args4));
            List<Object> list2 = this.dao2._queryColList("select id from score where regid = {_regid}", args4);
            if (list2 != null) {
                for (int k2 = 0; k2 < list2.size(); k2++) {
                    Map args5 = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put(Const.EXPORTREPORT_classNum, (Object) classNum).put("_schoolNum", (Object) _schoolNum).put("scoreid", list2.get(k2));
                    rowArgList.add(new RowArg("UPDATE tag SET studentId={studentId},classNum={classNum},schoolNum={_schoolNum} where scoreid={scoreid}", args5));
                }
            }
            Map args6 = StreamMap.create().put(Const.EXPORTREPORT_studentId, (Object) studentId).put("examinationRoomNum", (Object) examinationRoomNum).put("_schoolNum", (Object) _schoolNum).put("_examPaperNum", (Object) _examPaperNum).put("_oldstudentId", (Object) _oldstudentId2);
            if (_pageNo2 == 1) {
                rowArgList.add(new RowArg("UPDATE illegal SET studentId={studentId},examinationRoomNum={examinationRoomNum},schoolNum={_schoolNum} WHERE examPaperNum={_examPaperNum} AND studentId={_oldstudentId}", args6));
            }
        }
        if (existRegList.size() > 0) {
            Object[] array = this.dao2._queryArray("select scanType,type from exampaper where examPaperNum={_examPaperNum}", StreamMap.create().put("_examPaperNum", (Object) _examPaperNum));
            boolean scanType = array[0] == null || array[0].toString().equals("0");
            ResetCorrectstatus(_examNum, _schoolNum, _gradeNum, _subjectNum, examinationRoomNum, scanType);
        }
        this.dao2._batchExecute(rowArgList);
        this.dao2._execute("call updatechoosequestionbystudentid({examPaperNum},{studentId})", StreamMap.create().put("examPaperNum", infoMap.get("examPaperNum")).put(Const.EXPORTREPORT_studentId, infoMap.get(Const.EXPORTREPORT_studentId)));
        return null;
    }

    public Object[] GetScanTest(String examNum, String gradeNum, String subjectNum, String scannerNo, String testingCentreId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("scannerNo", (Object) scannerNo);
        Object[] array = this.dao2._queryArray("select s.status,s.description,s.id from scan_test s INNER JOIN exampaper p on s.exampaperNum=p.examPaperNum where p.examNum={examNum} and p.gradeNum={gradeNum} and p.subjectNum={subjectNum} and s.scannerNum={scannerNo}  ORDER BY s.id DESC LIMIT 1", args);
        if (array == null || array.length == 0) {
            return null;
        }
        Object exampaperNum = this.dao2._queryObject("select examPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} LIMIT 1", args);
        if (exampaperNum != null && ScanComplated(exampaperNum.toString()) && StaticClassResources.EdeiInfo.getMoreSchool()) {
            array[0] = 4;
        }
        return array;
    }

    public boolean ScanComplated(String exampaperNum) {
        Integer value = this.dao2._queryInt("SELECT scancompleted from questiongroup where exampaperNum={exampaperNum} LIMIT 1", StreamMap.create().put("exampaperNum", (Object) exampaperNum));
        return 1 == value;
    }

    public boolean IfScanComplated(String exampaperNum) {
        boolean flag = ScanComplated(exampaperNum);
        return flag && StaticClassResources.EdeiInfo.getMoreSchool();
    }

    public String GetOneUploadPicRecordId(String paperNum, String testingCentreId, String scannerNum) {
        return this.dao2._queryStr("select id from uploadpicrecord where exampapernum={paperNum} and testingCentreId={testingCentreId} and scannernum={scannerNum}", StreamMap.create().put("paperNum", (Object) paperNum).put("testingCentreId", (Object) testingCentreId).put("scannerNum", (Object) scannerNum));
    }

    public Map<String, Object> GetExamSchoolStuCountByEachSubject(String examPaperNum) {
        return this.dao2._queryOrderMap("SELECT em.testingCentreId,count(em.studentId)  from examinationnum em   LEFT JOIN exampaper ep on ep.examNum=em.examNum and em.gradeNum=ep.gradeNum and ep.subjectNum=em.subjectNum where ep.examPaperNum={examPaperNum} GROUP BY em.testingCentreId", TypeEnum.StringObject, StreamMap.create().put("examPaperNum", (Object) examPaperNum));
    }

    public List<Object[]> GetUploadListGroupByTestCenter(String examNum, String gradeNum, String subjectNum) {
        return this.dao2._queryArrayList("SELECT u.id ,t.testingCentreName,u.ip,u.testingCentreId,p.gradeNum,p.subjectNum,u.scannernum,u.count from uploadpicrecord u  LEFT JOIN exampaper p on u.exampapernum=p.examPaperNum  LEFT JOIN testingcentre t on t.id=u.testingCentreId where p.examNum={examNum} and p.gradeNum={gradeNum} and p.subjectNum={subjectNum}", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    public boolean SaveOneUploadPicRecord(HashMap<String, Object> ht) {
        String[] fields = new String[ht.size()];
        Object[] values = new Object[ht.size()];
        int i = 0;
        for (Map.Entry<String, Object> item : ht.entrySet()) {
            String key = item.getKey();
            Object o = ht.get(key);
            fields[i] = key;
            values[i] = o;
            i++;
        }
        this.dao2.insert("uploadpicrecord", fields, values);
        return true;
    }

    public boolean SaveScanTest(HashMap<String, Object> ht) throws Throwable {
        String testScanTemplateRecordNum = GUID.getGUIDStr();
        ht.put("testScanTemplateRecordNum", testScanTemplateRecordNum);
        String[] fields = new String[ht.size() - 2];
        Object[] values = new Object[ht.size() - 2];
        String location = CsUtils.getString(ht.get("location"));
        String filename = CsUtils.getString(ht.get("filename"));
        ht.remove("location");
        ht.remove("filename");
        int i = 0;
        for (Map.Entry<String, Object> item : ht.entrySet()) {
            String key = item.getKey();
            Object o = ht.get(key);
            fields[i] = key;
            values[i] = o;
            i++;
        }
        this.dao2.insert("scan_test", fields, values);
        Map<String, Object> map = this.dao2._querySimpleMap("select abtype,examNum,gradeNum,subjectNum from exampaper where examPaperNum={examPaperNum}", StreamMap.create().put("examPaperNum", ht.get("exampapernum")));
        boolean isAb = equalsAb(CsUtils.getString(map.get("abtype")));
        String examNum = CsUtils.getString(map.get(Const.EXPORTREPORT_examNum));
        String gradeNum = CsUtils.getString(map.get(Const.EXPORTREPORT_gradeNum));
        String subjectNum = CsUtils.getString(map.get(Const.EXPORTREPORT_subjectNum));
        String scannerNum = CsUtils.getString(ht.get("scannerNum"));
        String scannerName = CsUtils.getString(ht.get("scannerName"));
        String uploadUser = CsUtils.getString(ht.get("insertUser"));
        String uploadDate = CsUtils.getString(ht.get("insertDate"));
        String testPicUrl = CsUtils.getString(ht.get("img"));
        TemplateRecord t = new TemplateRecord();
        t.setExamNum(examNum);
        t.setGradeNum(gradeNum);
        t.setSubjectNum(subjectNum);
        t.setUploadScannerNum(scannerNum);
        t.setUploadScannerName(scannerName);
        t.setUploadUser(uploadUser);
        t.setUploadDate(uploadDate);
        t.setTemplateStatus(1);
        t.setTestScanTemplateRecordNum(testScanTemplateRecordNum);
        if (isAb) {
            t.setAbType("A");
            saveTemplateByScanTest(t, location, filename, testPicUrl);
            t.setAbType("B");
            saveTemplateByScanTest(t, location, filename, testPicUrl);
        } else {
            t.setAbType("N");
            saveTemplateByScanTest(t, location, filename, testPicUrl);
        }
        this.dao2._execute("update templaterecord set templateStatus=4 where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and uploadScannerNum={uploadScannerNum}", t);
        return true;
    }

    public boolean SaveTemplateByScanTest(String id) throws Throwable {
        StringBuffer sbf = new StringBuffer();
        sbf.append("select p.abtype,p.examNum,p.gradeNum,p.subjectNum,s.scannerNum,s.scannerName,s.insertUser,s.img,i.location,i.filename,s.testScanTemplateRecordNum ");
        sbf.append("from scan_test s ");
        sbf.append("LEFT JOIN exampaper p on s.exampaperNum=p.examPaperNum ");
        sbf.append("LEFT JOIN imgpath i on i.examNum=p.examNum and i.selected=1 ");
        sbf.append("WHERE s.id={id}");
        Map<String, Object> map = this.dao2._querySimpleMap(sbf.toString(), StreamMap.create().put("id", (Object) id));
        String location = CsUtils.getString(map.get("location"));
        String filename = CsUtils.getString(map.get("filename"));
        boolean isAb = equalsAb(CsUtils.getString(map.get("abtype")));
        String examNum = CsUtils.getString(map.get(Const.EXPORTREPORT_examNum));
        String gradeNum = CsUtils.getString(map.get(Const.EXPORTREPORT_gradeNum));
        String subjectNum = CsUtils.getString(map.get(Const.EXPORTREPORT_subjectNum));
        String scannerNum = CsUtils.getString(map.get("scannerNum"));
        String scannerName = CsUtils.getString(map.get("scannerName"));
        String uploadUser = CsUtils.getString(map.get("insertUser"));
        String testPicUrl = CsUtils.getString(map.get("img"));
        String testScanTemplateRecordNum = CsUtils.getString(map.get("testScanTemplateRecordNum"));
        TemplateRecord t = new TemplateRecord();
        t.setExamNum(examNum);
        t.setGradeNum(gradeNum);
        t.setSubjectNum(subjectNum);
        t.setUploadScannerNum(scannerNum);
        t.setUploadScannerName(scannerName);
        t.setUploadUser(uploadUser);
        t.setUploadDate(DateUtil.getCurrentTime());
        t.setTemplateStatus(1);
        t.setTestScanTemplateRecordNum(testScanTemplateRecordNum);
        if (isAb) {
            t.setAbType("A");
            saveTemplateByScanTest(t, location, filename, testPicUrl);
            t.setAbType("B");
            saveTemplateByScanTest(t, location, filename, testPicUrl);
            return true;
        }
        t.setAbType("N");
        saveTemplateByScanTest(t, location, filename, testPicUrl);
        return true;
    }

    public void delExistMotherTemplate(Template t) {
        String examNum = t.getExamNum();
        String gradeNum = t.getGradeNum();
        String subjectNum = t.getSubjectNum();
        String abType = t.getAbType();
        String location = t.getLocation();
        List<Object[]> oo = this.dao2._queryArrayList("SELECT id,uploadPicUrl from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and templateStatus={templateStatus} and mother=0", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("abType", (Object) abType).put("templateStatus", (Object) 3));
        for (Object[] arr : oo) {
            if (oo != null) {
                this.dao2._execute("DELETE FROM templaterecord WHERE id = {id}", StreamMap.create().put("id", arr[0]));
                try {
                    FileUtils.deleteDirectory(new File(location + "/" + arr[1]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveTemplateByScanTest(TemplateRecord t, String location, String filename, String testPicUrl) throws Throwable {
        String gradeNum = t.getGradeNum();
        String subjectNum = t.getSubjectNum();
        String scannerNum = t.getUploadScannerNum();
        if (this.dao2._queryObject("SELECT id from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and uploadScannerNum={uploadScannerNum} and templateStatus=3 and mother={mother}", t) != null) {
            return;
        }
        String uploadPicUrl = filename + "/17/" + gradeNum + "-" + subjectNum + "/" + scannerNum + "/" + t.getAbType() + "/" + GUID.getGUIDStr();
        t.setUploadPicUrl(uploadPicUrl);
        FileUtils.copyDirectory(new File(location + "/" + testPicUrl), new File(location + "/" + uploadPicUrl));
        save(t, location, "SELECT id,uploadPicUrl,templatePicUrl,xmlUrl,createUser from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and uploadScannerNum={uploadScannerNum} and templateStatus<3 and mother={mother}", t);
    }

    public String GetScoreModel(String examNum) {
        return this.dao2._queryStr("select scoreModel from exam where examNum={examNum}", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum));
    }

    public List<Object[]> GetScoreList(String examPaperNum, String regId) {
        return this.dao2._queryArrayList("SELECT s.id,q.scoreImg,CAST(s.regScore AS char),CAST(d.fullScore AS char),d.questionNum,s.isException from scoreimage q right JOIN score s on s.id=q.scoreId LEFT join (SELECT id,fullScore,questionNum,orderNum FROM define WHERE examPaperNum={examPaperNum} UNION ALL SELECT id,fullScore,questionNum,orderNum FROM subdefine WHERE examPaperNum={examPaperNum}  ) d on d.id=s.questionNum where s.regId={regId} and q.scoreImg is not null order by d.questionNum,d.orderNum desc", StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("regId", (Object) regId));
    }

    public boolean UpdateScore(String scoreId, String regScore, String questionScore, String isException) {
        this.dao2._execute("UPDATE score set regScore={regScore} , questionScore={questionScore},isException={isException} where id={scoreId}", StreamMap.create().put("regScore", (Object) regScore).put("questionScore", (Object) questionScore).put("isException", (Object) isException).put("scoreId", (Object) scoreId));
        return true;
    }

    public boolean SaveBigImages(Map<String, String> bigImageInfoMap) {
        Map<String, Object> array = null;
        Object examNum = null;
        List<String> html = new ArrayList<>();
        String imgType = "";
        String examPaperNum = "";
        String img = "";
        String imgPath = "";
        String insertUser = "";
        Map args = null;
        this.log.info("【保存原题答案大图】---------------------------------start---------------------------------");
        for (Map.Entry<String, String> entry : bigImageInfoMap.entrySet()) {
            JSONObject parseObject = JSON.parseObject(entry.getValue());
            examPaperNum = parseObject.get("examPaperNum").toString();
            imgType = parseObject.get("imgtype").toString();
            imgPath = parseObject.get("imgpath").toString();
            insertUser = parseObject.get("insertUser").toString();
            args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("imgtype", parseObject.get("imgtype"));
            if (array == null) {
                examNum = this.dao2._queryObject("select examNum from exampaper where examPaperNum={examPaperNum}", args);
                array = GetImgPathConfigDictionary(examNum.toString());
            }
            String[] imgs = parseObject.get("img").toString().split("/");
            String imgName = imgs[imgs.length - 1].substring(0, imgs[imgs.length - 1].length() - 4);
            if (imgType.equals("1")) {
                img = "datu/yuanti/" + imgName + ".txt";
            } else {
                img = "datu/daan/" + imgName + ".txt";
            }
            html.add(StrUtil.format("<img data='{}' id='{}' class='bigImg' style='width:98%;height:auto;margin-left: 1%;' @onerror='changeSrc()' src='imageAction!getImage2.action?examNum={}&img={}'/>", new Object[]{parseObject.get("imgtype"), parseObject.get("img"), examNum.toString(), parseObject.get("img")}).toString());
            try {
                FileUtils.writeByteArrayToFile(new File(array.get("location") + "/" + parseObject.get("img")), IOUtils.decodeBase64(parseObject.get("file").toString()));
                this.log.info("【保存原题答案大图】---------------------------------保存图片成功！！");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("SaveBigImages:" + e.getMessage());
            }
        }
        this.log.info("【保存原题答案大图】---------------------------------需要保存的TXT文件路径：【" + img + "】");
        this.log.info("【保存原题答案大图】---------------------------------需要保存的TXT文件内容：【" + html + "】");
        Answerexampaperimage qpath = (Answerexampaperimage) this.dao2._queryBean("select  * from answerexampaperimage where examPaperNum={examPaperNum}  and imgtype={imgtype}", Answerexampaperimage.class, args);
        this.log.info("【保存原题答案大图】---------------------------------查询原题答案大图存储数据----试卷编号：【" + examPaperNum + "】---答案还是原题：【" + imgType + "】");
        this.dao2._execute("delete from answerexampaperimage where examPaperNum={examPaperNum}  and imgtype={imgtype}", args);
        this.log.info("【保存原题答案大图】---------------------------------删除原来存储的数据成功！！！");
        this.dao2.insert("answerexampaperimage", new String[]{"examPaperNum", "img", "imgtype", "insertUser", "insertDate", "page", "width", "height", "imgpath", "description", "isDelete"}, new Object[]{examPaperNum, img, imgType, insertUser, DateUtil.getCurrentTime(), 0, 0, 0, imgPath, "dmj", "F"});
        this.log.info("【保存原题答案大图】---------------------------------添加存储的数据成功！！！");
        this.log.info(Boolean.valueOf(new StringBuilder().append("【保存原题答案大图】---------------------------------是否需要删除原来的文件和图片：【").append(qpath).toString() != new StringBuilder().append((Object) null).append("】").toString()));
        if (qpath != null) {
            qpath.setImg(array.get("location") + "/" + array.get("filename") + "/" + qpath.getImg());
            Answerexampaperimage fill = qpath.fill();
            this.log.info(Boolean.valueOf(new StringBuilder().append("【保存原题答案大图】---------------------------------需要删除的TXT文件中是否有html：").append(fill.getHtmlList()).toString() != null));
            if (fill.getHtmlList() != null) {
                List<String> htmlList = fill.getHtmlList();
                for (int i = 0; i < htmlList.size(); i++) {
                    String split = htmlList.get(i).split("img=", htmlList.get(i).length() - 3)[1];
                    FileUtil.Delete(array.get("location") + "/" + split.substring(0, split.length() - 3));
                    this.log.info("【保存原题答案大图】---------------------------------删除原来图片路径：【" + array.get("location") + "/" + split.substring(0, split.length() - 3) + "】");
                }
            }
            this.log.info("【保存原题答案大图】---------------------------------删除原来图片成功！！！");
            FileUtil.Delete(qpath.getImg().toString());
            this.log.info("【保存原题答案大图】---------------------------------删除原来TXT文件成功！！！");
        }
        String imgpath = StrUtil.format("{}/{}", new Object[]{array.get("location"), array.get("filename")});
        String fileName = img;
        String File = StrUtil.format("{}/{}", new Object[]{imgpath, fileName});
        FileUtil.writeString(JSON.toJSONString(html), File, CharsetUtil.CHARSET_UTF_8);
        return true;
    }

    public boolean IsExistPageInfoInTemplate(String examPaperNum, int selfType, int extPageType) {
        Node n;
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        Object tem = this.dao2._queryObject("SELECT templateInfo from template WHERE examPaperNum={examPaperNum} ORDER BY id DESC LIMIT 1", args);
        if (tem == null) {
            throw new RuntimeException("Dbutil ==> IsExistPageInfoInTemplate: Template does not exist !");
        }
        Object isself = this.dao2._queryObject("SELECT templateType FROM exampaper WHERE examPaperNum = {examPaperNum}", args);
        boolean self = false;
        if (isself == null || isself.toString().equals("0")) {
            self = true;
        }
        System.currentTimeMillis();
        InputStream in = null;
        try {
            try {
                in = new ByteArrayInputStream((byte[]) tem);
                SAXReader reader = new SAXReader();
                Document doc = reader.read(in);
                if (self) {
                    n = doc.selectSingleNode("/RecogTemplate/Page/Block[@Block_Type=" + selfType + "]");
                } else {
                    n = doc.selectSingleNode("/RecogTemplate/Page/MyRectangle[@RectangleType=" + extPageType + "]");
                }
                if (n != null) {
                    return true;
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            } catch (Exception e2) {
                e2.printStackTrace();
                throw new RuntimeException("Dbutil ==> IsExistPageInfoInTemplate: Template read error !" + e2.getMessage());
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public boolean SubmitTemplateRequest(HashMap<String, Object> ht) {
        this.dao2._execute("update templaterecord SET createUser={uploadUser},createDate={uploadDate},templateStatus=5,description='有新的申请，此次申请设为过期' where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and uploadScannerNum={uploadScannerNum} and abType={abType} and templateStatus=1", ht);
        ht.put("testScanTemplateRecordNum", GUID.getGUIDStr());
        this.dao2.save("templateRecord", ht);
        return true;
    }

    public Map<String, Object> GetTempateStatus(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum) {
        return this.dao2._querySimpleMap("SELECT * from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and uploadScannerNum={uploadScannerNum} and templateStatus<5 and mother=0 ORDER BY IFNULL(createDate,uploadDate) desc limit 1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("abType", (Object) ABNNum).put("uploadScannerNum", (Object) scannerNo));
    }

    public TemplateRecord GetTempate(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum, int mother) {
        String sql;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("ABNNum", (Object) ABNNum).put("scannerNo", (Object) scannerNo).put("templateStatus", (Object) 3).put("mother", (Object) Integer.valueOf(mother));
        if (mother == 0) {
            sql = "SELECT * from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={ABNNum} and uploadScannerNum={scannerNo} and templateStatus={templateStatus} and mother={mother} ORDER BY IFNULL(createDate,uploadDate) desc limit 1";
        } else {
            sql = "SELECT * from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={ABNNum} and templateStatus={templateStatus} and mother={mother} ORDER BY IFNULL(createDate,uploadDate) desc limit 1";
        }
        return (TemplateRecord) this.dao2._queryBean(sql, TemplateRecord.class, args);
    }

    public boolean DeleteTemplate(String examNum, String gradeNum, String subjectNum, String scannerNo, String ABNNum) {
        try {
            Object id = this.dao2._queryObject("SELECT id from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={ABNNum} and uploadScannerNum={scannerNo} and templateStatus<{templateStatus} and mother=0 ORDER BY IFNULL(createDate,uploadDate) desc limit 1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("ABNNum", (Object) ABNNum).put("scannerNo", (Object) scannerNo).put("templateStatus", (Object) 3));
            if (id != null) {
                this.dao2._execute("DELETE FROM templaterecord WHERE id = {id}", StreamMap.create().put("id", id));
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Map<String, Object>> GetUploadTemplateRecordList(String examNum, String scannerNo) {
        return this.dao2._queryMapList("SELECT t.id,g.gradeName,s.subjectName,t.templateStatus,t.abType from templaterecord t LEFT JOIN basegrade g on g.gradeNum=t.gradeNum LEFT JOIN `subject` s on s.subjectNum=t.subjectNum WHERE t.examNum={examNum} and t.uploadScannerNum={scannerNo} and mother=0 and t.templateStatus<4", TypeEnum.StringObject, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("scannerNo", (Object) scannerNo));
    }

    public ZTreeNode GetMother(String examNum, String gradeNum, String subjectNum, String abType) {
        return (ZTreeNode) this.dao2._queryBean("select t.*,p.totalPage from templaterecord t LEFT JOIN exampaper p on t.examNum=p.examNum and t.gradeNum=p.gradeNum and t.subjectNum=p.subjectNum WHERE t.examNum={examNum} and t.gradeNum={gradeNum} and t.subjectNum={subjectNum} and t.abType={abType} and t.mother=1 and t.templateStatus<5 order by t.createDate desc limit 1", ZTreeNode.class, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("abType", (Object) abType));
    }

    public String GetTemplateTreeJson(String examNum, String user) {
        int count = this.dao2.queryInt("SELECT count(1) from examschool where examNum=?", new Object[]{examNum}).intValue();
        StringBuffer sf = new StringBuffer();
        sf.append("SELECT " + (count == 1 ? "1" : "0") + " onlyOneSchool, g.gradeNum,g.gradeName,s.subjectNum,s.subjectName,t.uploadScannerNum,if(t.uploadScannerName=t.uploadScannerNum or t.uploadScannerName='达美嘉',u2.realname,t.uploadScannerName) uploadScannerName,t.scannerNum,t.scannerName,t.abType,t.mother,t.templateStatus,t.uploadPicUrl,t.templatePicUrl,t.xmlUrl,t.id id,t.waittingUpdateTemplate,");
        sf.append("t.uploadUser,t.uploadDate,t.createUser,t.createDate,");
        sf.append("p.abtype ab,p.templateType,p.totalPage,u.realname,t.testScanTemplateRecordNum from exampaper p ");
        sf.append("LEFT JOIN basegrade g on g.gradeNum=p.gradeNum ");
        sf.append("LEFT JOIN `subject` s on s.subjectNum=p.subjectNum ");
        sf.append("LEFT JOIN templaterecord t on t.examNum=p.examNum and t.gradeNum=p.gradeNum and t.subjectNum=p.subjectNum and t.templateStatus < 4 ");
        sf.append("LEFT JOIN scan_test st on st.testScanTemplateRecordNum=t.testScanTemplateRecordNum and st.examPaperNum=p.examPaperNum and st.scannerNum=t.uploadScannerNum and st.`status`=1 ");
        sf.append("LEFT JOIN user u on u.id=t.createUser ");
        sf.append("LEFT JOIN user u2 on u2.id=t.uploadUser ");
        sf.append("WHERE p.examNum={examNum} and p.isHidden='F' ");
        sf.append("ORDER BY p.gradeNum,s.orderNum,t.mother desc,t.uploadScannerNum,t.abType ");
        List<?> _queryBeanList = this.dao2._queryBeanList(sf.toString(), ZTreeNode.class, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum));
        if (_queryBeanList == null || _queryBeanList.isEmpty()) {
            return null;
        }
        sf.setLength(0);
        Map args = StreamMap.create().put("user", (Object) user).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        sf.append("SELECT type from schoolscanpermission  where userNum={user}");
        String type = this.dao2._queryStr(sf.toString(), args);
        String userScanpermission = "-1";
        if (!"-1".equals(user) && !"1".equals(type)) {
            sf.setLength(0);
            sf.append(" SELECT GROUP_CONCAT(userNum) from (");
            sf.append(" SELECT s1.userNum from schoolscanpermission s1 INNER JOIN schoolscanpermission s2 on s1.type=s2.type and s1.schoolNum=s2.schoolNum");
            sf.append(" where s2.userNum = {user} and s2.type=2 GROUP BY s1.userNum");
            sf.append(" ) s");
            userScanpermission = this.dao2._queryStr(sf.toString(), args);
        }
        String finalUserScanpermission = userScanpermission;
        List<String> tempGradeNumList = new ArrayList<>();
        List<String> tempSubjectNumList = new ArrayList<>();
        Supplier<Stream<ZTreeNode>> streamSupplier = () -> {
            return _queryBeanList.stream();
        };
        List<ZTreeNode> gradeZTreeNodeList = new ArrayList<>();
        streamSupplier.get().forEach(g -> {
            boolean gradeflag = !tempGradeNumList.contains(g.getGradeNum());
            tempGradeNumList.add(g.getGradeNum());
            if (gradeflag) {
                ZTreeNode gradeNode = g.m10clone();
                gradeNode.clearGradeNodeUnnecessaryInfo();
                tempSubjectNumList.clear();
                List<ZTreeNode> subjectNodeList = new ArrayList<>();
                ((Stream) streamSupplier.get()).forEach(s -> {
                    if (!s.getGradeNum().equals(gradeNode.getGradeNum())) {
                        return;
                    }
                    String subjectKey = s.getSubjectNum();
                    boolean exist = tempSubjectNumList.contains(subjectKey);
                    tempSubjectNumList.add(subjectKey);
                    boolean subjectFlag = (exist || s.getGradeNum() == null || !s.getGradeNum().equals(gradeNode.getGradeNum())) ? false : true;
                    if (subjectFlag) {
                        ZTreeNode subjectNode = s.m10clone();
                        subjectNode.clearSubjectNodeUnnecessaryInfo();
                        List<ZTreeNode> scannerNodeList = new ArrayList<>();
                        Map<String, ZTreeNode> scannerMotherNodeMap = new HashMap<>();
                        ((Stream) streamSupplier.get()).forEach(sn -> {
                            boolean scannerFlag = (sn.getGradeNum() == null || !sn.getGradeNum().equals(gradeNode.getGradeNum()) || sn.getSubjectNum() == null || !sn.getSubjectNum().equals(subjectNode.getSubjectNum()) || sn.getTemplateStatus() == null) ? false : true;
                            if (scannerFlag) {
                                ZTreeNode scannerNode = sn.m10clone();
                                scannerNode.clearScannerNodeUnnecessaryInfo();
                                if (!checkScanpermission(finalUserScanpermission, scannerNode)) {
                                    return;
                                }
                                scannerNodeList.add(scannerNode);
                                if (scannerNode.isMother()) {
                                    scannerMotherNodeMap.put(scannerNode.getAbType(), scannerNode);
                                }
                            }
                        });
                        if (s.isAb()) {
                            if (!scannerMotherNodeMap.containsKey("B")) {
                                ZTreeNode motherNode = ZTreeNode.creatMotherSubjectNode(s, "B");
                                scannerNodeList.add(0, motherNode);
                            }
                            if (!scannerMotherNodeMap.containsKey("A")) {
                                ZTreeNode motherNode2 = ZTreeNode.creatMotherSubjectNode(s, "A");
                                scannerNodeList.add(0, motherNode2);
                            }
                        } else if (!scannerMotherNodeMap.containsKey("N")) {
                            ZTreeNode motherNode3 = ZTreeNode.creatMotherSubjectNode(s, "N");
                            scannerNodeList.add(0, motherNode3);
                        }
                        Map<String, List<ZTreeNode>> ZTreeNodeGroupMap = (Map) scannerNodeList.stream().collect(Collectors.groupingBy((v0) -> {
                            return v0.getName();
                        }));
                        ZTreeNodeGroupMap.entrySet().stream().forEach(item -> {
                            List<ZTreeNode> groupList = (List) item.getValue();
                            if (groupList.size() > 1) {
                                int i = 0;
                                while (i < groupList.size()) {
                                    ZTreeNode node = groupList.get(i);
                                    int i2 = i + 1;
                                    node.setName(node.getUploadScannerName() + "（" + i2 + "）" + (node.getAbType().equals("N") ? "" : "-" + node.getAbType() + "卷"));
                                    i = i2 + 1;
                                }
                            }
                        });
                        subjectNode.setChildren(scannerNodeList);
                        subjectNodeList.add(subjectNode);
                    }
                });
                gradeNode.setChildren(subjectNodeList);
                gradeZTreeNodeList.add(gradeNode);
            }
        });
        return JSON.toJSONString(gradeZTreeNodeList, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
    }

    private boolean inBackList(List<Map<String, Object>> list, ZTreeNode node) {
        if (CollUtil.isEmpty(list)) {
            return false;
        }
        return CollUtil.contains(list, map -> {
            if (node.getGradeNum().equals(map.get(Const.EXPORTREPORT_gradeNum).toString()) && node.getSubjectNum().equals(map.get(Const.EXPORTREPORT_subjectNum).toString()) && map.get("scannerNum").toString().equals(node.getUploadScannerNum()) && cn.hutool.core.date.DateUtil.compare(Convert.toDate(node.getUploadDate()), Convert.toDate(map.get("insertDate"))) <= 0) {
                return true;
            }
            return false;
        });
    }

    public boolean checkScanpermission(String userScanpermission, ZTreeNode node) {
        if ("-1".equals(userScanpermission) || node.isMother()) {
            return true;
        }
        if (StrUtil.isEmpty(userScanpermission)) {
            return false;
        }
        return StrUtil.split(userScanpermission, ',', true, false).contains(node.getUploadUser());
    }

    public boolean UpdateTemplateStatus(String id, String status, String description, String createUser) {
        String description2;
        if (String.valueOf(6).equals(status)) {
            description2 = "在模板制作里已删除！";
        } else {
            description2 = description + "#2#";
        }
        Long testScanTemplateRecordNum = this.dao2._queryLong("SELECT testScanTemplateRecordNum from templaterecord where id ={id}", StreamMap.create().put("id", (Object) id));
        String date = DateUtil.getCurrentTime();
        Object[] objArr = {createUser, date, status, description2, testScanTemplateRecordNum};
        String finalDescription = description2;
        this.dao2._execute("update templaterecord SET createUser={createUser},createDate={date},templateStatus={status},description={description} where testScanTemplateRecordNum ={testScanTemplateRecordNum}", StreamMap.create().put("createUser", (Object) createUser).put("date", (Object) date).put(Const.CORRECT_SCORECORRECT, (Object) status).put("description", (Object) finalDescription).put("testScanTemplateRecordNum", (Object) testScanTemplateRecordNum));
        Object[] objArr2 = {2, description2, createUser, date, testScanTemplateRecordNum};
        this.dao2._execute("update scan_test set `status`={status},description={description},updateUser={createUser},updateDate={date} where testScanTemplateRecordNum={testScanTemplateRecordNum}", StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) 2).put("description", (Object) finalDescription).put("createUser", (Object) createUser).put("date", (Object) date).put("testScanTemplateRecordNum", (Object) testScanTemplateRecordNum));
        return true;
    }

    public boolean UpdateTemplate(Template template) throws Throwable {
        String location = template.getLocation();
        String filename = template.getFilename();
        String abType = template.getAbType();
        String templatePicUrl = filename + "/18/" + template.getGradeNum() + "-" + template.getSubjectNum() + "/" + template.getScannerNum() + "/" + abType + "/" + GUID.getGUIDStr();
        String templateXmlUrl = filename + "/19/" + template.getGradeNum() + "-" + template.getSubjectNum() + "/" + template.getScannerNum() + "/" + abType + "/" + GUID.getGUIDStr();
        Map<String, byte[]> temImageMap = template.getTemImageMap();
        for (Map.Entry<String, byte[]> item : temImageMap.entrySet()) {
            String pNum = item.getKey();
            byte[] imgBytes = item.getValue();
            CsUtils.writeByteArrayToFile(location + "/" + templatePicUrl + "/" + pNum + ".png", imgBytes);
        }
        CsUtils.writeByteArrayToFile(location + "/" + templateXmlUrl + "/1.xml", template.getXml());
        saveClipPageMark(template.getClipPageMarkImgMap(), template.getExamNum(), template.getGradeNum(), template.getSubjectNum(), template.getAbType(), template.getUploadScannerNum(), template.isMother(), location, filename, template.getImgPath(), template.getCreateUser());
        TemplateRecord t = new TemplateRecord();
        PropertyUtils.copyProperties(t, template);
        t.setTemplatePicUrl(templatePicUrl);
        t.setTemplateStatus(3);
        t.setXmlUrl(templateXmlUrl);
        t.setCreateDate(DateUtil.getCurrentTime());
        this.dao2.update((BaseDaoImpl2<?, ?, ?>) t, false);
        return true;
    }

    public boolean CopyTemplate(String id, String motherId, String createUser, String location, String filename) throws Throwable {
        List<?> _queryBeanList;
        TemplateRecord t = (TemplateRecord) this.dao2._queryBean("select * from templaterecord WHERE id ={id} ", TemplateRecord.class, StreamMap.create().put("id", (Object) id));
        TemplateRecord mother = (TemplateRecord) this.dao2._queryBean("select * from templaterecord WHERE id ={motherId} ", TemplateRecord.class, StreamMap.create().put("motherId", (Object) motherId));
        Integer pageCount = this.dao2._queryInt("select totalPage from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} limit 1", mother);
        if (pageCount != null && pageCount.intValue() > 0 && (_queryBeanList = this.dao2._queryBeanList("select * from clippagemarksample WHERE examNum = {examNum} and gradeNum= {gradeNum} and subjectNum = {subjectNum} and abType = {abType} order by insertDate desc limit {pageCount}", ClipPageMarkSample.class, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) mother.getExamNum()).put(Const.EXPORTREPORT_gradeNum, (Object) mother.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) mother.getSubjectNum()).put("abType", (Object) mother.getAbType()).put("pageCount", (Object) pageCount))) != null && _queryBeanList.size() > 0) {
            String time = DateUtil.getCurrentTime();
            _queryBeanList.forEach(cpms -> {
                cpms.setId(GUID.getGUIDStr());
                cpms.setMother(false);
                cpms.setScannerNum(t.getUploadScannerNum());
                cpms.setInsertDate(time);
                cpms.setUpdateDate(time);
                cpms.setInsertUser(createUser);
                cpms.setUpdateUser(createUser);
                this.dao2.save(cpms);
            });
        }
        String mTemplatePicUrl = mother.getTemplatePicUrl();
        String mXmlUrl = mother.getXmlUrl();
        String templatePicUrl = filename + "/18/" + t.getGradeNum() + "-" + t.getSubjectNum() + "/" + t.getUploadScannerNum() + "/" + t.getAbType() + "/" + GUID.getGUIDStr();
        String templateXmlUrl = filename + "/19/" + t.getGradeNum() + "-" + t.getSubjectNum() + "/" + t.getUploadScannerNum() + "/" + t.getAbType() + "/" + GUID.getGUIDStr();
        FileUtils.copyDirectory(new File(location + "/" + mTemplatePicUrl), new File(location + "/" + templatePicUrl));
        FileUtils.copyDirectory(new File(location + "/" + mXmlUrl), new File(location + "/" + templateXmlUrl));
        this.dao2._execute("update templaterecord set scannerNum={scannerNum},scannerName={scannerName},templatePicUrl={templatePicUrl},xmlUrl={xmlUrl},createUser={createUser},createDate={createDate},uploadUser={uploadUser},templateStatus={templateStatus},testScanTemplateRecordNum={testScanTemplateRecordNum} where id={id}", StreamMap.create().put("scannerNum", (Object) mother.getScannerNum()).put("scannerName", (Object) mother.getScannerName()).put("templatePicUrl", (Object) templatePicUrl).put("xmlUrl", (Object) templateXmlUrl).put("createUser", (Object) createUser).put("createDate", (Object) DateUtil.getCurrentTime()).put("uploadUser", (Object) (StrUtil.isEmpty(t.getUploadUser()) ? createUser : t.getUploadUser())).put("templateStatus", (Object) 3).put("testScanTemplateRecordNum", (Object) GUID.getGUIDStr()));
        return true;
    }

    public boolean CreatLocalTemplate(String motherId, String createUser, String location, String filename, String scannerNum, String scannerName) throws Throwable {
        List<?> _queryBeanList;
        TemplateRecord mother = (TemplateRecord) this.dao2._queryBean("select * from templaterecord WHERE id = {motherId}", TemplateRecord.class, StreamMap.create().put("motherId", (Object) motherId));
        Integer pageCount = this.dao2._queryInt("select totalPage from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} limit 1", mother);
        if (pageCount != null && pageCount.intValue() > 0 && (_queryBeanList = this.dao2._queryBeanList("select * from clippagemarksample WHERE examNum = {examNum} and gradeNum= {gradeNum} and subjectNum = {subjectNum} and abType = {abType} order by insertDate desc  limit {pageCount}", ClipPageMarkSample.class, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) mother.getExamNum()).put(Const.EXPORTREPORT_gradeNum, (Object) mother.getGradeNum()).put(Const.EXPORTREPORT_subjectNum, (Object) mother.getSubjectNum()).put("abType", (Object) mother.getAbType()).put("pageCount", (Object) pageCount))) != null && _queryBeanList.size() > 0) {
            String time = DateUtil.getCurrentTime();
            _queryBeanList.forEach(cpms -> {
                cpms.setId(GUID.getGUIDStr());
                cpms.setMother(false);
                cpms.setScannerNum(scannerNum);
                cpms.setInsertDate(time);
                cpms.setUpdateDate(time);
                cpms.setInsertUser(createUser);
                cpms.setUpdateUser(createUser);
                this.dao2.save(cpms);
            });
        }
        String mTemplatePicUrl = mother.getTemplatePicUrl();
        String mXmlUrl = mother.getXmlUrl();
        String templatePicUrl = filename + "/18/" + mother.getGradeNum() + "-" + mother.getSubjectNum() + "/" + scannerNum + "/" + mother.getAbType() + "/" + GUID.getGUIDStr();
        String uploadPicUrl = filename + "/17/" + mother.getGradeNum() + "-" + mother.getSubjectNum() + "/" + scannerNum + "/" + mother.getAbType() + "/" + GUID.getGUIDStr();
        String templateXmlUrl = filename + "/19/" + mother.getGradeNum() + "-" + mother.getSubjectNum() + "/" + mother.getScannerNum() + "/" + mother.getAbType() + "/" + GUID.getGUIDStr();
        FileUtils.copyDirectory(new File(location + "/" + mTemplatePicUrl), new File(location + "/" + uploadPicUrl));
        FileUtils.copyDirectory(new File(location + "/" + mTemplatePicUrl), new File(location + "/" + templatePicUrl));
        FileUtils.copyDirectory(new File(location + "/" + mXmlUrl), new File(location + "/" + templateXmlUrl));
        String time2 = DateUtil.getCurrentTime();
        TemplateRecord t = mother.m9clone();
        t.setCreateUser(createUser);
        t.setCreateDate(DateUtil.getCurrentTime());
        t.setId(null);
        t.setMother(false);
        t.setUploadPicUrl(uploadPicUrl);
        t.setUploadDate(time2);
        t.setUploadScannerNum(scannerNum);
        t.setUploadScannerName(scannerName);
        t.setTemplatePicUrl(templatePicUrl);
        t.setXmlUrl(templateXmlUrl);
        t.setScannerNum(scannerNum);
        t.setScannerName(scannerName);
        t.setTemplateStatus(3);
        t.setUploadUser(createUser);
        t.setTestScanTemplateRecordNum(GUID.getGUIDStr());
        save(t, location, "SELECT id,uploadPicUrl,templatePicUrl,xmlUrl,createUser from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and uploadScannerNum={uploadScannerNum} and templateStatus<5 and mother={mother}", t);
        return true;
    }

    public boolean UpDateTemplateXmlOrCreate(String id, byte[] bytes, String location, String filename, String scannerNum, String scannerName, String updateUser) throws Throwable {
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT * from templaterecord where id = {id}", StreamMap.create().put("id", (Object) id));
        boolean isMother = Convert.toBool(map.get("mother")).booleanValue();
        String examNum = map.get(Const.EXPORTREPORT_examNum).toString();
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum).toString();
        String subjectNum = map.get(Const.EXPORTREPORT_subjectNum).toString();
        String abType = map.get("abType").toString();
        String updateId = id;
        if (isMother) {
            DeleteTemplate(examNum, gradeNum, subjectNum, scannerNum, abType);
            CreatLocalTemplate(id, updateUser, location, filename, scannerNum, scannerName);
            TemplateRecord templateRecord = GetTempate(examNum, gradeNum, subjectNum, scannerNum, abType, 0);
            updateId = Convert.toStr(templateRecord.getId());
        }
        return UpDateTemplateXml(updateId, bytes, location, updateUser);
    }

    public boolean UpDateTemplateXml(String id, byte[] bytes, String location, String updateUser) throws Throwable {
        Map<String, Object> map = this.dao2._querySimpleMap("SELECT * from templaterecord where id = {id}", StreamMap.create().put("id", (Object) id));
        boolean isMother = Convert.toBool(map.get("mother")).booleanValue();
        Object xmlDir = map.get("xmlUrl");
        File xml = new File(location + "\\" + xmlDir + "\\1.xml");
        FileUtils.writeByteArrayToFile(xml, bytes);
        this.dao2._execute("update templaterecord set createUser={createUser},createDate={createDate},templateStatus=3 where id = {id}", StreamMap.create().put("createUser", (Object) updateUser).put("createDate", (Object) DateUtil.getCurrentTime()).put("id", (Object) id));
        org.w3c.dom.Document docResult = XmlUtil.readXML(xml);
        boolean self = false;
        NodeList nodes = (NodeList) XmlUtil.getByXPath("//MyRectangle[@RectangleType='1001']", docResult, XPathConstants.NODESET);
        if (nodes == null || nodes.getLength() == 0) {
            self = true;
        }
        NodeList pageNodes = (NodeList) XmlUtil.getByXPath("//Page[@Big_Img!='']", docResult, XPathConstants.NODESET);
        Map<String, Object> examMap = GetImgPathConfigDictionary(map.get(Const.EXPORTREPORT_examNum).toString());
        if (pageNodes != null && pageNodes.getLength() > 0) {
            for (int i = 0; i < pageNodes.getLength(); i++) {
                org.w3c.dom.Node pageNode = pageNodes.item(i);
                String page = pageNode.getAttributes().getNamedItem(self ? "Page_num" : "No").getTextContent();
                String imgBase64 = pageNode.getAttributes().getNamedItem("Big_Img").getTextContent();
                String bigImg = examMap.get("location") + "/" + map.get("templatePicUrl") + "/" + page + ".png";
                FileUtils.writeByteArrayToFile(new File(bigImg), IOUtils.decodeBase64(imgBase64));
                pageNode.getAttributes().removeNamedItem("Big_Img");
                if (!self) {
                    String alignInfo = pageNode.getAttributes().getNamedItem("Align_Info").getTextContent();
                    String[] alignInfoArray = alignInfo.split(Const.STRING_SEPERATOR);
                    String base64 = "";
                    for (int k = 0; k < alignInfoArray.length; k += 4) {
                        File cut = File.createTempFile("cut", ".png");
                        int x = Integer.parseInt(alignInfoArray[k]);
                        int y = Integer.parseInt(alignInfoArray[k + 1]);
                        int w = Integer.parseInt(alignInfoArray[k + 2]);
                        int h = Integer.parseInt(alignInfoArray[k + 3]);
                        ImgUtil.cut(new File(bigImg), cut, new Rectangle(x, y, w, h));
                        BufferedImage markImg = ImgUtil.read(cut);
                        base64 = base64 + Const.STRING_SEPERATOR + ImgUtil.toBase64(markImg, "png");
                        markImg.flush();
                        cut.delete();
                    }
                    pageNode.getAttributes().getNamedItem("Align_Image_Info").setTextContent(base64.substring(1));
                }
            }
            XmlUtil.toFile(docResult, xml.getAbsolutePath());
        }
        HashMap<Integer, byte[]> clipPageMarkImgMap = new HashMap<>();
        for (int i2 = 0; i2 < nodes.getLength(); i2++) {
            org.w3c.dom.Node node = nodes.item(i2);
            String page2 = node.getParentNode().getAttributes().getNamedItem("No").getTextContent();
            NamedNodeMap np = node.getAttributes();
            int x2 = Integer.valueOf(np.getNamedItem("X").getTextContent()).intValue();
            int y2 = Integer.valueOf(np.getNamedItem("Y").getTextContent()).intValue();
            int w2 = Integer.valueOf(np.getNamedItem("Width").getTextContent()).intValue();
            int h2 = Integer.valueOf(np.getNamedItem("Height").getTextContent()).intValue();
            String bigImg2 = examMap.get("location") + "/" + map.get("templatePicUrl") + "/" + page2 + ".png";
            File cut2 = File.createTempFile("cut", ".png");
            ImgUtil.cut(new File(bigImg2), cut2, new Rectangle(x2, y2, w2, h2));
            File scale = File.createTempFile("scale", ".png");
            ImgUtil.scale(cut2, scale, 1.0f);
            clipPageMarkImgMap.put(Integer.valueOf(page2), FileUtil.readBytes(scale));
            cut2.delete();
            scale.delete();
        }
        saveClipPageMark(clipPageMarkImgMap, map.get(Const.EXPORTREPORT_examNum).toString(), map.get(Const.EXPORTREPORT_gradeNum).toString(), map.get(Const.EXPORTREPORT_subjectNum).toString(), map.get("abType").toString(), Convert.toStr(map.get("uploadScannerNum"), (String) null), isMother, location, examMap.get("filename").toString(), ((Integer) examMap.get("id")).intValue(), updateUser);
        return true;
    }

    public void save(TemplateRecord t, String location, String sql, TemplateRecord args) {
        t.setWaittingUpdateTemplate(0);
        List<Object[]> oo = this.dao2._queryArrayList(sql, args);
        List<String> delDirist = new ArrayList<>();
        for (Object[] arr : oo) {
            if (oo != null) {
                String createUser = String.valueOf(arr[4]);
                if ("-1".equals(t.getCreateUser()) && !CsUtils.IsNullOrEmpty(createUser) && !"-1".equals(createUser)) {
                    t.setCreateUser(createUser);
                }
                this.dao2._execute("DELETE FROM templaterecord WHERE id = {id}", StreamMap.create().put("id", arr[0]));
                String xmlPicUrl = String.valueOf(arr[3]);
                if (!CsUtils.IsNullOrEmpty(xmlPicUrl)) {
                    delDirist.add(location + "/" + xmlPicUrl);
                }
                String temPicUrl = String.valueOf(arr[3]);
                if (!CsUtils.IsNullOrEmpty(temPicUrl)) {
                    delDirist.add(location + "/" + temPicUrl);
                }
                String uploadPicUrl = String.valueOf(arr[2]);
                if (!CsUtils.IsNullOrEmpty(uploadPicUrl)) {
                    delDirist.add(location + "/" + uploadPicUrl);
                }
            }
        }
        this.dao2.save(t);
        delDirist.stream().forEach(f -> {
            try {
                FileUtils.deleteDirectory(new File(f));
            } catch (Exception e) {
            }
        });
    }

    public boolean CheckTemplate(String examNum, String gradeNum, String subjectNum, String scannerNum) {
        boolean isAb = IsAb(examNum, gradeNum, subjectNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("uploadScannerNum", (Object) scannerNum).put("templateStatus", (Object) 3);
        if (isAb) {
            args.put("abType", "A");
            if (this.dao2._queryObject("SELECT id from templaterecord where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and uploadScannerNum={uploadScannerNum} and templateStatus={templateStatus} and mother=0 limit 1", args) == null) {
                return false;
            }
            args.put("abType", "B");
            if (this.dao2._queryObject("SELECT id from templaterecord where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and uploadScannerNum={uploadScannerNum} and templateStatus={templateStatus} and mother=0 limit 1", args) == null) {
                return false;
            }
            return true;
        }
        args.put("abType", "N");
        if (this.dao2._queryObject("SELECT id from templaterecord where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and uploadScannerNum={uploadScannerNum} and templateStatus={templateStatus} and mother=0 limit 1", args) == null) {
            return false;
        }
        return true;
    }

    public boolean CheckMotherTemplate(String examNum, String gradeNum, String subjectNum) {
        boolean isAb = IsAb(examNum, gradeNum, subjectNum);
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("templateStatus", (Object) 3);
        if (isAb) {
            args.put("abType", "A");
            if (this.dao2._queryObject("SELECT id from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and templateStatus={templateStatus} and mother=1 ORDER BY IFNULL(createDate,uploadDate) desc limit 1", args) == null) {
                return false;
            }
            args.put("abType", "B");
            if (this.dao2._queryObject("SELECT id from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and templateStatus={templateStatus} and mother=1 ORDER BY IFNULL(createDate,uploadDate) desc limit 1", args) == null) {
                return false;
            }
            return true;
        }
        args.put("abType", "N");
        if (this.dao2._queryObject("SELECT id from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and templateStatus={templateStatus} and mother=1 ORDER BY IFNULL(createDate,uploadDate) desc limit 1", args) == null) {
            return false;
        }
        return true;
    }

    public boolean SaveUploadPicFileList(List<UploadPicFile> list) {
        this.dao2.batchSave(list);
        return true;
    }

    public List<UploadPicFile> GetUploadPicFileList(String batchNum) {
        return this.dao2._queryBeanList("select * from uploadpicfile where batchNum={batchNum}", UploadPicFile.class, StreamMap.create().put("batchNum", (Object) batchNum));
    }

    public List<Map<String, Object>> GetExamStuList(String testingCenterId, String gradeNum, String subjectNum) {
        StringBuffer sbf = new StringBuffer();
        sbf.append(" SELECT st.studentId,st.studentName,sl.schoolName,c.className,en.examineeNum,em.examinationRoomName from examinationnum en ");
        sbf.append(" LEFT JOIN student st on st.id=en.studentId ");
        sbf.append(" LEFT JOIN school sl on sl.id=en.schoolNum ");
        sbf.append(" LEFT JOIN class c on c.id=en.classNum ");
        sbf.append(" LEFT JOIN examinationroom em on em.id=en.examinationRoomNum ");
        sbf.append(" WHERE en.testingCentreId={testingCenterId} and en.gradeNum={gradeNum} and en.subjectNum={subjectNum} ");
        return this.dao2._queryMapList(sbf.toString(), TypeEnum.StringObject, StreamMap.create().put("testingCenterId", (Object) testingCenterId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
    }

    public List<Map<String, Object>> GetExamStuWithoutUploadedList(String examNum, String testingCenterId, String gradeNum, String subjectNum) {
        Integer kaoHaoJiaoDuiZhuangTai = getKaoHaoJiaoDuiZhuangTai(examNum, gradeNum, subjectNum, testingCenterId);
        if (kaoHaoJiaoDuiZhuangTai != null && 2 == kaoHaoJiaoDuiZhuangTai.intValue()) {
            return GetMissingExamStuListAfterCompleteExamStuNoCheck(examNum, testingCenterId, gradeNum, subjectNum);
        }
        return GetMissingExamStuListWithoutUploadedList(examNum, testingCenterId, gradeNum, subjectNum);
    }

    public List<Map<String, Object>> GetMissingExamStuListWithoutUploadedList(String examNum, String testingCenterId, String gradeNum, String subjectNum) {
        Map<String, String> cache;
        StringBuffer sbf = new StringBuffer();
        sbf.append(" SELECT 0 exist,'' regIds,st.id,st.studentId,st.studentName,sl.schoolName,c.className,en2.examineeNum,em.examinationRoomName,en2.seatNum from ( ");
        sbf.append("      SELECT * FROM\t(  ");
        sbf.append("           SELECT examineeNum,classNum,studentId,schoolNum,examinationRoomNum,seatNum FROM\texaminationnum  ");
        sbf.append("                WHERE examNum ={examNum}\tAND testingCentreId = {testingCenterId}\tAND gradeNum ={gradeNum} AND subjectNum = {subjectNum} ");
        sbf.append("           ) en  ");
        sbf.append("           LEFT JOIN (  ");
        sbf.append("                SELECT\tpf.stuBarCodeNum FROM\tuploadpicfile pf  ");
        sbf.append("                LEFT JOIN uploadpicrecord pd ON pf.batchNum = pd.batchNum\tAND pd.testingCentreId =  {testingCenterId}  ");
        sbf.append("                LEFT JOIN exampaper p ON pd.exampapernum = p.examPaperNum  ");
        sbf.append("                WHERE\tp.examNum = {examNum}\tAND p.gradeNum = {gradeNum}\tAND p.subjectNum = {subjectNum} ");
        sbf.append("                GROUP BY\tpf.stuBarCodeNum  ");
        sbf.append("           ) pf2 ON en.examineeNum = pf2.stuBarCodeNum  ");
        sbf.append("           WHERE\tpf2.stuBarCodeNum IS NULL  ");
        sbf.append(" \t) en2  ");
        sbf.append(" \tLEFT JOIN  ");
        sbf.append(" \t( ");
        sbf.append(" \t\tSELECT count(1) c, p.examPaperNum,r.testingCentreId,r.studentId,p.totalPage from regexaminee r  ");
        sbf.append(" \t\tLEFT JOIN exampaper p on p.examPaperNum=r.exampapernum  ");
        sbf.append(" \t\twhere p.examNum={examNum} and p.gradeNum={gradeNum} and p.subjectNum={subjectNum} and r.testingCentreId= {testingCenterId}  ");
        sbf.append(" \t\tGROUP BY studentId  ");
        sbf.append(" \t) r on r.studentId=en2.studentId and c=r.totalPage ");
        sbf.append(" \tLEFT JOIN student st on st.id=en2.studentId ");
        sbf.append(" \tLEFT JOIN school sl on sl.id=en2.schoolNum ");
        sbf.append(" \tLEFT JOIN class c on c.id=en2.classNum ");
        sbf.append(" \tLEFT JOIN examinationroom em on em.id=en2.examinationRoomNum ");
        sbf.append(" \tWHERE r.studentId is null ");
        List<Map<String, Object>> list = this.dao2._queryMapList(sbf.toString(), TypeEnum.StringObject, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCenterId", (Object) testingCenterId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
        if (CollUtil.isNotEmpty(list) && (cache = ScanProcessCache.get(examNum, gradeNum)) != null) {
            list.forEach(m -> {
                String key = m.get("id").toString();
                if (cache.containsKey(key)) {
                    m.put("exist", "1");
                    m.put("regIds", cache.get(key));
                }
            });
        }
        return list;
    }

    public List<Map<String, Object>> GetMissingExamStuListAfterCompleteExamStuNoCheck(String examNum, String testingCenterId, String gradeNum, String subjectNum) {
        Map<String, String> cache;
        StringBuffer sbf = new StringBuffer();
        sbf.append(" SELECT 0 exist,'' regIds,st.id,st.studentId,st.studentName,sl.schoolName,c.className,en2.examineeNum,em.examinationRoomName,en2.seatNum from ( ");
        sbf.append("           SELECT examineeNum,classNum,studentId,schoolNum,examinationRoomNum,seatNum FROM\texaminationnum  ");
        sbf.append("                WHERE examNum ={examNum}\tAND testingCentreId = {testingCenterId}\tAND gradeNum ={gradeNum} AND subjectNum = {subjectNum} ");
        sbf.append(" \t) en2  ");
        sbf.append(" \tLEFT JOIN  ");
        sbf.append(" \t( ");
        sbf.append(" \t\tSELECT count(1) c, p.examPaperNum,r.testingCentreId,r.studentId,p.totalPage from regexaminee r  ");
        sbf.append(" \t\tLEFT JOIN exampaper p on p.examPaperNum=r.exampapernum  ");
        sbf.append(" \t\twhere p.examNum={examNum} and p.gradeNum={gradeNum} and p.subjectNum={subjectNum} and r.testingCentreId= {testingCenterId}  ");
        sbf.append(" \t\tGROUP BY studentId  ");
        sbf.append(" \t) r on r.studentId=en2.studentId and c=r.totalPage ");
        sbf.append(" \tLEFT JOIN student st on st.id=en2.studentId ");
        sbf.append(" \tLEFT JOIN school sl on sl.id=en2.schoolNum ");
        sbf.append(" \tLEFT JOIN class c on c.id=en2.classNum ");
        sbf.append(" \tLEFT JOIN examinationroom em on em.id=en2.examinationRoomNum ");
        sbf.append(" \tWHERE r.studentId is null ");
        List<Map<String, Object>> list = this.dao2._queryMapList(sbf.toString(), TypeEnum.StringObject, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("testingCenterId", (Object) testingCenterId).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum));
        if (CollUtil.isNotEmpty(list) && (cache = ScanProcessCache.get(examNum, gradeNum)) != null) {
            list.forEach(m -> {
                String key = m.get("id").toString();
                if (cache.containsKey(key)) {
                    m.put("exist", "1");
                    m.put("regIds", cache.get(key));
                }
            });
        }
        return list;
    }

    public List<Map<String, Object>> getStudentPaperImageByRedId(String regIds) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("SELECT r.id,st.subjectName,r.page,ip.location,simg.img from ( ");
        stringBuffer.append("SELECT id,examPaperNum,page from regexaminee where id in ({regIds[]}) ");
        stringBuffer.append(") r LEFT JOIN studentpaperimage simg on r.id = simg.regId ");
        stringBuffer.append("LEFT JOIN exampaper p on p.examPaperNum=r.examPaperNum ");
        stringBuffer.append("LEFT JOIN subject st on st.subjectNum = p.subjectNum ");
        stringBuffer.append("LEFT JOIN imgpath ip on ip.examNum=p.examNum and ip.selected=1 ");
        stringBuffer.append("ORDER BY st.orderNum,r.page ");
        return this.dao2._queryMapList(stringBuffer.toString(), TypeEnum.StringObject, StreamMap.create().put("regIds", (Object) regIds));
    }

    public boolean IsConfirmedMissingPaper(String examNum, String testingCenterId, String gradeNum, String subjectNum) {
        Object oo = this.dao2._queryObject("SELECT id from missingpaper WHERE testingCenterId={testingCenterId} and examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and isDelete={isDelete} LIMIT 1", StreamMap.create().put("testingCenterId", (Object) testingCenterId).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("isDelete", (Object) 0));
        if (oo == null || "0".equals(oo.toString())) {
            return false;
        }
        return true;
    }

    public boolean SaveMissingPaper(MissingPaper missingPaper) {
        this.dao2._execute("update missingpaper set isDelete=1 WHERE testingCenterId={testingCenterId} and examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and isDelete=0", missingPaper);
        String path = missingPaper.getLocation() + "/" + missingPaper.getPath();
        String jsonStr = missingPaper.getJsonStr();
        try {
            FileUtils.writeStringToFile(new File(path), jsonStr, "utf-8");
            this.dao2.save(missingPaper);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getImgLocation(String regid) {
        Object oo = this.dao2._queryObject("SELECT location from imgpath where examNum = (select examNum from exampaper where exampaperNum = (SELECT exampaperNum from regexaminee where id = {regid} LIMIT 1) LIMIT 1) and selected = 1", StreamMap.create().put("regid", (Object) regid));
        return oo == null ? "" : oo.toString();
    }

    public Map<String, Object> GetClipPageMarkSampleIdMap(String examNum, String gradeNum, String subjectNum, String scannerNum, String abType, int pageCount) {
        Map<String, Object> args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("scannerNum", (Object) scannerNum).put("abType", (Object) abType).put("pageCount", (Object) Integer.valueOf(pageCount));
        Map<String, Object> map = this.dao2._queryOrderMap("SELECT page,id from clippagemarksample WHERE examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and scannerNum ={scannerNum} and abType={abType} and isMother=0 ORDER BY insertDate desc limit {pageCount}", TypeEnum.StringObject, args);
        if (map == null || map.size() == 0) {
            map = this.dao2._queryOrderMap("SELECT page,id from clippagemarksample WHERE examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and isMother=1 ORDER BY insertDate desc limit {pageCount}", TypeEnum.StringObject, args);
        }
        return map;
    }

    public boolean canDelete(String examPaperNum, String testingCentreId) {
        Object oo;
        if (!"1".equals(Configuration.getInstance().getTestingcentredis()) && (oo = this.dao2._queryObject("SELECT isDis from testingcentredis where examPaperNum={examPaperNum} and testingCentreId={testingCentreId}", StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCentreId))) != null && Convert.toInt(oo).intValue() == 1) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws DocumentException {
        File file = new File("c:\\a.xml");
        SAXReader reader = new SAXReader();
        Document doc = reader.read(file);
        if (0 != 0) {
            doc.getRootElement().selectSingleNode("/Page/Block[@Block_Type='9']");
        } else {
            doc.selectSingleNode("/RecogTemplate/Page/MyRectangle[@RectangleType=901]");
        }
    }

    public List<CsDefine> getAllCsDefineList(String examPaperNum) {
        List<CsDefine> csDefineList = new ArrayList<>();
        List<?> _queryBeanList = this.dao2._queryBeanList("select *,questionNum questionName from define where examPaperNum={examPaperNum} order by orderNum", CsDefine.class, StreamMap.create().put("examPaperNum", (Object) examPaperNum));
        _queryBeanList.forEach(define -> {
            csDefineList.add(define);
            if (define.ifChoose()) {
                List<CsDefine> subChooseCsDefineList = getSubChooseCsDefineList(define, _queryBeanList);
                define.setChildrens(subChooseCsDefineList);
            } else if (define.HasChildren()) {
                List<CsDefine> subList = getSubCsDefineList(define.getId());
                define.setChildrens(subList);
                csDefineList.addAll(subList);
            }
        });
        return csDefineList;
    }

    public void setChooseQuestionName(CsDefine define) {
        List<CsDefine> childrens = define.getChildrens();
        String questionName = (String) childrens.stream().map(s -> {
            return s.getQuestionNum();
        }).collect(Collectors.joining("或"));
        for (CsDefine s2 : childrens) {
            s2.setQuestionName(questionName);
            s2.setItems(childrens);
        }
    }

    public void setChooseSonQuestionName(CsDefine define) {
        if (!define.HasChildren()) {
            return;
        }
        CsDefine parent = define.getParent();
        List<CsDefine> brothers = parent.getChildrens();
        List<CsDefine> allChooseChildrens = new ArrayList<>();
        Iterator<CsDefine> it = brothers.iterator();
        while (it.hasNext()) {
            allChooseChildrens.addAll(it.next().getChildrens());
        }
        Map<Integer, List<CsDefine>> map = (Map) allChooseChildrens.stream().collect(Collectors.groupingBy((v0) -> {
            return v0.getOrderNum();
        }));
        if (define.ifMerge()) {
            String questionName = parent.getQuestionName() + "合并";
            for (CsDefine brother : brothers) {
                for (int i = 0; i < brother.getChildrens().size(); i++) {
                    CsDefine s = brother.getChildrens().get(i);
                    s.setQuestionName(questionName);
                    s.setItems(allChooseChildrens);
                }
            }
            return;
        }
        String questionName2 = parent.getQuestionName();
        for (int i2 = 0; i2 < define.getChildrens().size(); i2++) {
            CsDefine s2 = define.getChildrens().get(i2);
            s2.setQuestionName(questionName2 + "第（" + (i2 + 1) + "）小题");
            s2.setItems(map.get(Integer.valueOf(s2.getOrderNum())));
        }
    }

    public void setCommonQuestionName(CsDefine define) {
        List<CsDefine> childrens = define.getChildrens();
        if (define.ifMerge()) {
            String questionName = define.getQuestionName() + "合并";
            for (CsDefine s : childrens) {
                s.setQuestionName(questionName);
                s.setItems(childrens);
            }
        }
    }

    public void setQuestionName(CsDefine define) {
        if (define.ifChoose()) {
            setChooseQuestionName(define);
        } else if (define.ifChooseSon()) {
            setChooseSonQuestionName(define);
        } else {
            setCommonQuestionName(define);
        }
    }

    public List<CsDefine> getTemplateNeedCsDefineList(String examPaperNum) {
        List<CsDefine> allCsDefineList = getAllCsDefineList(examPaperNum);
        allCsDefineList.forEach(d -> {
            setQuestionName(d);
        });
        List<CsDefine> needCsDefineList = (List) allCsDefineList.stream().filter(csDefine -> {
            return !csDefine.HasChildren();
        }).collect(Collectors.toList());
        return needCsDefineList;
    }

    public Map<String, CsDefine> getTemplateNeedCsDefineMap(String examPaperNum) {
        List<CsDefine> needCsDefineList = getTemplateNeedCsDefineList(examPaperNum);
        Set<String> questionNameSet = new HashSet<>();
        new HashMap();
        return (Map) needCsDefineList.stream().filter(d -> {
            return questionNameSet.add(d.getQuestionName());
        }).collect(Collectors.toMap(define -> {
            return define.getQuestionName();
        }, define2 -> {
            return define2;
        }));
    }

    public static <T> T copy(T tsource) {
        BeanUtil.copyProperties(tsource, (Object) null, new String[0]);
        return null;
    }

    public List<CsDefine> getSubChooseCsDefineList(CsDefine chooseCsDefine, List<CsDefine> list) {
        return (List) list.stream().filter(d -> {
            return d.getChoosename().equals(String.valueOf(chooseCsDefine.getId()));
        }).collect(Collectors.toList());
    }

    public List<CsDefine> getSubCsDefineList(long id) {
        return this.dao2._queryBeanList("SELECT *,questionNum questionName  from subdefine where pid={id}", CsDefine.class, StreamMap.create().put("id", (Object) Long.valueOf(id)));
    }

    public void resetPage(Template template) {
        String examPaperNum = template.getExamPaperNum();
        boolean isDouble = template.isDouble();
        int pageCount = template.getPageCount();
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("pageCount", (Object) Integer.valueOf(pageCount)).put("paperSize", (Object) template.getPaperSize());
        this.dao2._execute("update define set page=null,cross_page='F'  where examPaperNum={examPaperNum}", args);
        this.dao2._execute("update subdefine set page=null,cross_page='F'  where examPaperNum={examPaperNum}", args);
        this.dao2._execute("update exampaper set totalPage={pageCount},doubleFaced='" + (isDouble ? "T" : "F") + "',paperSize={paperSize}  where examPaperNum={examPaperNum}", args);
    }

    public void updateDefinePage(List<Map<String, Object>> list, Map<String, CsDefine> questionDictionaryMap) {
        Map<String, CsDefine> questitonNoDefineMap = new HashMap<>();
        for (Map<String, Object> node : list) {
            int page = Integer.valueOf(node.get("No").toString()).intValue();
            String qNum = node.get("QuestionNo").toString();
            CsDefine define = questionDictionaryMap.get(qNum);
            if (questitonNoDefineMap.containsKey(qNum)) {
                CsDefine cacheDefine = questitonNoDefineMap.get(qNum);
                if (cacheDefine.getPage() != page) {
                    cacheDefine.setCross_page("T");
                }
            } else {
                define.setPage(page);
                questitonNoDefineMap.put(qNum, define);
            }
        }
        new ArrayList();
        List<RowArg> rowArgList = new ArrayList<>();
        String sql = "update define set Cross_page={crossPage},page={page} where id={id}";
        String subSql = "update subdefine set Cross_page={crossPage},page={page} where id={id}";
        questitonNoDefineMap.forEach((qNum2, define2) -> {
            String crossPage = define2.getCross_page();
            int page2 = define2.getPage();
            if (define2.ifLeaf()) {
                if (define2.ifChooseGrandson() || define2.getParent().ifMerge()) {
                    define2.getItems().forEach(d -> {
                        Map args0 = StreamMap.create().put("crossPage", (Object) crossPage).put("page", (Object) Integer.valueOf(page2)).put("id", (Object) Long.valueOf(d.getId()));
                        rowArgList.add(new RowArg(subSql, args0));
                        Map args1 = StreamMap.create().put("crossPage", (Object) crossPage).put("page", (Object) Integer.valueOf(page2)).put("id", (Object) Long.valueOf(d.getPid()));
                        rowArgList.add(new RowArg(sql, args1));
                    });
                    return;
                }
                Map args0 = StreamMap.create().put("crossPage", (Object) crossPage).put("page", (Object) Integer.valueOf(page2)).put("id", (Object) Long.valueOf(define2.getId()));
                rowArgList.add(new RowArg(subSql, args0));
                Map args1 = StreamMap.create().put("crossPage", (Object) crossPage).put("page", (Object) Integer.valueOf(page2)).put("id", (Object) Long.valueOf(define2.getPid()));
                rowArgList.add(new RowArg(sql, args1));
                return;
            }
            if (define2.ifChooseSon()) {
                define2.getItems().forEach(d2 -> {
                    Map args02 = StreamMap.create().put("crossPage", (Object) crossPage).put("page", (Object) Integer.valueOf(page2)).put("id", (Object) Long.valueOf(d2.getId()));
                    rowArgList.add(new RowArg(sql, args02));
                });
            } else {
                Map args12 = StreamMap.create().put("crossPage", (Object) crossPage).put("page", (Object) Integer.valueOf(page2)).put("id", (Object) Long.valueOf(define2.getId()));
                rowArgList.add(new RowArg(sql, args12));
            }
        });
        this.dao2._batchExecute(rowArgList);
    }

    public void updateDefineCache(Template template) {
        Map<String, CsDefine> QuestionDictionaryMap = DefineCache.get(template.getExamPaperNum(), false);
        List<Map<String, Object>> list = template.getQuestionInfo();
        boolean removeCache = list.stream().filter(m -> {
            return !QuestionDictionaryMap.containsKey(m.get("QuestionNo").toString());
        }).findAny().isPresent();
        if (removeCache) {
            DefineCache.remove(template.getExamPaperNum());
        }
    }

    public boolean SaveExtTemplate(Template template) throws Throwable {
        String abtype = template.getAbType();
        updateDefineCache(template);
        Map<String, CsDefine> QuestionDictionaryMap = DefineCache.get(template.getExamPaperNum(), false);
        List<Map<String, Object>> list = template.getQuestionInfo();
        Map<String, byte[]> temImageList = template.getTemImageMap();
        resetPage(template);
        updateDefinePage(list, QuestionDictionaryMap);
        String location = template.getLocation();
        String filename = template.getFilename();
        String templatePicUrl = filename + "/18/" + template.getGradeNum() + "-" + template.getSubjectNum() + "/" + template.getScannerNum() + "/" + abtype + "/" + GUID.getGUIDStr();
        String templateXmlUrl = filename + "/19/" + template.getGradeNum() + "-" + template.getSubjectNum() + "/" + template.getScannerNum() + "/" + abtype + "/" + GUID.getGUIDStr();
        for (Map.Entry<String, byte[]> item : temImageList.entrySet()) {
            String pNum = item.getKey();
            byte[] imgBytes = temImageList.get(pNum);
            CsUtils.writeByteArrayToFile(location + "/" + templatePicUrl + "/" + pNum + ".png", imgBytes);
        }
        CsUtils.writeByteArrayToFile(location + "/" + templateXmlUrl + "/1.xml", template.getXml());
        saveClipPageMark(template.getClipPageMarkImgMap(), template.getExamNum(), template.getGradeNum(), template.getSubjectNum(), template.getAbType(), template.getUploadScannerNum(), template.isMother(), location, filename, template.getImgPath(), template.getCreateUser());
        TemplateRecord t = new TemplateRecord();
        PropertyUtils.copyProperties(t, template);
        t.setTemplatePicUrl(templatePicUrl);
        t.setTemplateStatus(3);
        t.setXmlUrl(templateXmlUrl);
        t.setCreateDate(DateUtil.getCurrentTime());
        t.setTestScanTemplateRecordNum(StrUtil.isEmpty(t.getTestScanTemplateRecordNum()) ? GUID.getGUIDStr() : t.getTestScanTemplateRecordNum());
        if (template.isMother()) {
            save(t, location, "SELECT id,uploadPicUrl,templatePicUrl,xmlUrl,createUser from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and abType={abType} and mother={mother}", t);
        } else if (t.getId() == null) {
            save(t, location, "SELECT id,uploadPicUrl,templatePicUrl,xmlUrl,createUser from templaterecord where examNum={examNum} AND gradeNum={gradeNum} and subjectNum={subjectNum} and uploadScannerNum={scannerNum} and abType={abType} and mother={mother}", t);
        } else {
            t.setWaittingUpdateTemplate(0);
            this.dao2.update((BaseDaoImpl2<?, ?, ?>) t, false);
        }
        DefineCache.remove(template.getExamPaperNum());
        SingleDoubleMarkingCache.remove(template.getExamPaperNum());
        return true;
    }

    public Map<String, Object> getReClipWaitUpdatePageList(String examPaperNum, String testingCentreId, String examinationRoomNum, String page, String checkPage) {
        StringBuffer sf = new StringBuffer();
        sf.append("SELECT {} from cantrecognized c");
        sf.append(" INNER JOIN regexaminee r on r.id=c.regId");
        sf.append(" INNER JOIN studentpaperimage s on s.regId=r.id");
        sf.append(" INNER JOIN imgpath i on i.id = s.imgpath ");
        sf.append(" where r.examPaperNum={examPaperNum} and r.testingCentreId={testingCentreId} ");
        if (StrUtil.isNotEmpty(examinationRoomNum) && !examinationRoomNum.equals("-1")) {
            sf.append(" and r.examinationRoomNum={examinationRoomNum} ");
        }
        if (StrUtil.isNotEmpty(page)) {
            sf.append(" and r.page={page} ");
        }
        if (StrUtil.isNotEmpty(checkPage)) {
            sf.append(" and r.checkPage={checkPage} ");
        }
        String countSql = StrUtil.format(sf.toString(), new Object[]{" count(1) "});
        String contentSql = StrUtil.format(sf.toString() + " ORDER BY r.cnum,r.page LIMIT 100 ", new Object[]{" r.id,r.cnum,r.page,r.checkPage,i.location,s.img "});
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCentreId).put("examinationRoomNum", (Object) examinationRoomNum).put("page", (Object) page).put("checkPage", (Object) checkPage);
        CompletableFuture<Long> countFuture = CompletableFuture.supplyAsync(() -> {
            long c = ((Long) this.dao2._queryObject(countSql, args)).longValue();
            return Long.valueOf(c);
        });
        CompletableFuture<List<Map<String, Object>>> listFuture = CompletableFuture.supplyAsync(() -> {
            List<Map<String, Object>> list = this.dao2._queryMapList(contentSql, TypeEnum.StringObject, args);
            return list;
        });
        try {
            Map<String, Object> map = StreamMap.create().put("count", (Object) countFuture.get()).put("list", (Object) listFuture.get());
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean changePage(String regId, String cNum, String totalPage, String newPage, boolean updateOther) {
        List<String> sqls = new ArrayList<>();
        sqls.add("update regexaminee set page={newPage},checkPage=1 WHERE id={regId}");
        sqls.add("update scanrecord set page={newPage},checkPage=1 WHERE id={regId}");
        int otherPage = 0;
        if (updateOther) {
            int page = Integer.valueOf(newPage).intValue();
            otherPage = page % 2 == 0 ? page - 1 : page + 1;
            if (otherPage <= Integer.valueOf(totalPage).intValue()) {
                sqls.add("update regexaminee set page={otherPage},checkPage=1 WHERE cNum={cNum} and id <> {regId}");
                sqls.add("update scanrecord set page={otherPage}  WHERE cNum={cNum} and id <> {regId}");
            }
        }
        Map args = StreamMap.create().put("newPage", (Object) newPage).put("regId", (Object) regId).put("otherPage", (Object) Integer.valueOf(otherPage)).put("cNum", (Object) cNum);
        this.dao2._batchExecute(sqls, args);
        return true;
    }

    public boolean batchChangePage(Object[][] args) {
        this.dao2.batchExecute("update regexaminee set page=?,checkPage=1 WHERE id=?", args);
        this.dao2.batchExecute("update scanrecord set page=? WHERE id=?", args);
        return true;
    }

    public Map<String, Object> getOneUploadRecord(String examNum, String user, String machineGuid) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("machineGuid", (Object) machineGuid).put("user", (Object) user);
        Object o = this.dao2._queryObject("SELECT id from uploadpicrecord u INNER JOIN exampaper p on p.examPaperNum=u.exampapernum where p.examNum={examNum} and state=3 and done=0 and (unlocked=0 or unlocked is null)  and downloadscannernum={machineGuid} LIMIT 1", args);
        if (o != null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT up.id,up.testingCentreId,up.batchNum,up.state,up.downloadscannernum,up.scannernum,up.count");
        sb.append(" ,p.examNum,p.gradeNum,p.subjectNum,p.abtype,t.templateStatus,t.waittingUpdateTemplate,if(p.abtype=0,'N','AB') groupAbType");
        sb.append(" from uploadpicrecord up");
        sb.append(" LEFT JOIN exampaper p on up.exampapernum=p.examPaperNum");
        sb.append(" inner JOIN (SELECT count(1),GROUP_CONCAT(abType ORDER BY abType) groupAbType,examNum,gradeNum,subjectNum,uploadScannerNum,templateStatus,waittingUpdateTemplate from templaterecord where examNum={examNum} and templateStatus=3 and mother=0 and waittingUpdateTemplate=0 GROUP BY examNum,gradeNum,subjectNum,uploadScannerNum) t ");
        sb.append(" on t.examNum=p.examNum and t.gradeNum=p.gradeNum and t.subjectNum=p.subjectNum and t.uploadScannerNum=up.scannernum and t.groupAbType = groupAbType");
        if (!"-1".equals(user)) {
            sb.append(" join ( ");
            sb.append("    SELECT t.id,t.testingCentreName,ts.examNum from testingcentre_school ts ");
            sb.append("    JOIN examschool es on es.schoolNum = ts.schoolNum and ts.examNum=es.examNum ");
            sb.append("    JOIN schoolscanpermission ss on ((ss.type=2 and es.schoolNum=ss.schoolNum) or ss.type=1) ");
            sb.append("    JOIN testingcentre t on t.id = ts.testingCentreId ");
            sb.append("    where ts.examNum={examNum} ");
            sb.append(user.equals("-1") ? "" : " and ss.userNum={user}");
            sb.append(" ) ts on up.testingCentreId=ts.id");
            sb.append(" where p.examNum={examNum} and up.state=1 and up.done=0 and (up.unlocked=0 or up.unlocked is null) ORDER BY up.insertdate limit 1");
        } else {
            sb.append(" where p.examNum={examNum} and up.state=1 and up.done=0 and (up.unlocked=0 or up.unlocked is null) ORDER BY up.insertdate limit 1");
        }
        Map<String, Object> map = this.dao2._querySimpleMap(sb.toString(), args);
        if (map != null) {
            UpdateOneUploadPicRecord(map.get("id").toString(), "2", machineGuid, null, user);
        }
        if (map == null) {
            map = getOneUploadRecordWithMotherTemplate(examNum, user, machineGuid);
        }
        return map;
    }

    public Map<String, Object> getOneUploadRecordWithMotherTemplate(String examNum, String user, String machineGuid) {
        boolean forceMakeSelfTemplate = "1".equals(Configuration.getInstance().getForceMakeSelfTemplate());
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("machineGuid", (Object) machineGuid).put("user", (Object) user);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT up.id,up.testingCentreId,up.batchNum,up.state,up.downloadscannernum,up.scannernum,up.count");
        sb.append(" ,p.examNum,p.gradeNum,p.subjectNum,p.abtype,t.templateStatus,t.waittingUpdateTemplate,if(p.abtype=0,'N','AB') groupAbType");
        sb.append(" from uploadpicrecord up");
        sb.append(" LEFT JOIN exampaper p on up.exampapernum=p.examPaperNum");
        sb.append(" inner JOIN (SELECT count(1),GROUP_CONCAT(abType ORDER BY abType) groupAbType,examNum,gradeNum,subjectNum,uploadScannerNum,templateStatus,waittingUpdateTemplate from templaterecord where examNum={examNum} and templateStatus=3 and mother=1 and waittingUpdateTemplate=0 GROUP BY examNum,gradeNum,subjectNum,uploadScannerNum) t ");
        sb.append(" on t.examNum=p.examNum and t.gradeNum=p.gradeNum and t.subjectNum=p.subjectNum and t.groupAbType = groupAbType");
        if (!"-1".equals(user)) {
            sb.append(" join ( ");
            sb.append("    SELECT t.id,t.testingCentreName,ts.examNum from testingcentre_school ts ");
            sb.append("    JOIN examschool es on es.schoolNum = ts.schoolNum and ts.examNum=es.examNum ");
            sb.append("    JOIN schoolscanpermission ss on ((ss.type=2 and es.schoolNum=ss.schoolNum) or ss.type=1) ");
            sb.append("    JOIN testingcentre t on t.id = ts.testingCentreId ");
            sb.append("    where ts.examNum={examNum} ");
            sb.append(user.equals("-1") ? "" : " and ss.userNum={user}");
            sb.append(" ) ts on up.testingCentreId=ts.id");
            if (forceMakeSelfTemplate) {
                sb.append(" JOIN (SELECT id, examNum,ismusttemplate from testingcentre where examNum={examNum} and ismusttemplate=1 ) tr on t.examNum = tr.examNum and tr.id= up.testingCentreId ");
            }
            sb.append(" where p.examNum={examNum} and up.state=1 and up.done=0 and (up.unlocked=0 or up.unlocked is null) ORDER BY up.insertdate limit 1");
        } else {
            if (forceMakeSelfTemplate) {
                sb.append(" JOIN (SELECT id, examNum,ismusttemplate from testingcentre where examNum={examNum} and ismusttemplate=1 ) tr on t.examNum = tr.examNum and tr.id= up.testingCentreId ");
            }
            sb.append(" where p.examNum={examNum} and up.state=1 and up.done=0 and (up.unlocked=0 or up.unlocked is null) ORDER BY up.insertdate limit 1");
        }
        Map<String, Object> map = this.dao2._querySimpleMap(sb.toString(), args);
        if (map != null) {
            UpdateOneUploadPicRecord(map.get("id").toString(), "2", machineGuid, null, user);
        }
        return map;
    }

    public Map<String, Object> getOneUploadRecordById(String id, String examNum, String user, String machineGuid) {
        Map args = StreamMap.create().put("id", (Object) id).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("machineGuid", (Object) machineGuid).put("user", (Object) user);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT up.id,up.testingCentreId,up.batchNum,up.state,up.downloadscannernum,up.scannernum,up.count");
        sb.append(" ,p.examNum,p.gradeNum,p.subjectNum,p.abtype,t.templateStatus,t.waittingUpdateTemplate,if(p.abtype=0,'N','AB') groupAbType");
        sb.append(" from uploadpicrecord up");
        sb.append(" LEFT JOIN exampaper p on up.exampapernum=p.examPaperNum");
        sb.append(" inner JOIN (SELECT count(1),GROUP_CONCAT(abType ORDER BY abType) groupAbType,examNum,gradeNum,subjectNum,uploadScannerNum,templateStatus,waittingUpdateTemplate from templaterecord where examNum={examNum} and templateStatus=3 and mother=0 and waittingUpdateTemplate=0 GROUP BY examNum,gradeNum,subjectNum,uploadScannerNum) t ");
        sb.append(" on t.examNum=p.examNum and t.gradeNum=p.gradeNum and t.subjectNum=p.subjectNum and t.uploadScannerNum=up.scannernum and t.groupAbType = groupAbType");
        sb.append(" where up.id={id} and p.examNum={examNum} and up.state=1 and up.done=0 and (up.unlocked=0 or up.unlocked is null) ");
        Map<String, Object> map = this.dao2._querySimpleMap(sb.toString(), args);
        if (map != null) {
            UpdateOneUploadPicRecord(map.get("id").toString(), "2", machineGuid, null, user);
        }
        if (map == null && "0".equals(Configuration.getInstance().getForceMakeSelfTemplate())) {
            map = getOneUploadRecordWithMotherTemplateById(id, examNum, user, machineGuid);
        }
        return map;
    }

    public Map<String, Object> getOneUploadRecordWithMotherTemplateById(String id, String examNum, String user, String machineGuid) {
        Map args = StreamMap.create().put("id", (Object) id).put(Const.EXPORTREPORT_examNum, (Object) examNum).put("machineGuid", (Object) machineGuid).put("user", (Object) user);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT up.id,up.testingCentreId,up.batchNum,up.state,up.downloadscannernum,up.scannernum,up.count");
        sb.append(" ,p.examNum,p.gradeNum,p.subjectNum,p.abtype,t.templateStatus,t.waittingUpdateTemplate,if(p.abtype=0,'N','AB') groupAbType");
        sb.append(" from uploadpicrecord up");
        sb.append(" LEFT JOIN exampaper p on up.exampapernum=p.examPaperNum");
        sb.append(" inner JOIN (SELECT count(1),GROUP_CONCAT(abType ORDER BY abType) groupAbType,examNum,gradeNum,subjectNum,uploadScannerNum,templateStatus,waittingUpdateTemplate from templaterecord where examNum={examNum} and templateStatus=3 and mother=1 and waittingUpdateTemplate=0 GROUP BY examNum,gradeNum,subjectNum,uploadScannerNum) t ");
        sb.append(" on t.examNum=p.examNum and t.gradeNum=p.gradeNum and t.subjectNum=p.subjectNum and t.groupAbType = groupAbType");
        sb.append(" where up.id={id} and p.examNum={examNum} and up.state=1 and up.done=0 and (up.unlocked=0 or up.unlocked is null) ORDER BY up.insertdate limit 1");
        Map<String, Object> map = this.dao2._querySimpleMap(sb.toString(), args);
        if (map != null) {
            UpdateOneUploadPicRecord(map.get("id").toString(), "2", machineGuid, null, user);
        }
        return map;
    }

    public List<Map<String, Object>> getReClipRecordList(String examPaperNum, String testingCenterId, String examRoomNum) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT c.id,c.regId, CONCAT(i.location,'/',s.img) img,r.cNum,r.batch,r.uploadScannerNum from cantrecognized c");
        sb.append(" LEFT JOIN regexaminee r on r.id=c.regId");
        sb.append(" LEFT JOIN studentpaperimage s on s.regId=r.id");
        sb.append(" LEFT JOIN imgpath i on i.id=s.imgpath");
        sb.append(" WHERE c.examPaperNum={examPaperNum} and c.testingCentreId={testingCentreId}");
        if (StrUtil.isNotEmpty(examRoomNum) && !"-1".equals(examRoomNum)) {
            sb.append(" and c.examinationRoomNum={examRoomNum}");
        }
        return this.dao2._queryMapList(sb.toString(), TypeEnum.StringObject, StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCenterId).put("examRoomNum", (Object) examRoomNum));
    }

    public List<Map<String, Object>> getAllReClipImageList(String examPaperNum, String testingCenterId, String examRoomNum) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT c.id,c.regId, CONCAT(i.location,'/',s.img) img,CONCAT(r2.cNum,'-',r2.type) filename,IF(c.regId=r2.id,0,1) dengdaidahui,r2.cNum,r2.type,r2.id from cantrecognized c");
        sb.append(" LEFT JOIN regexaminee r on r.id=c.regId");
        sb.append(" RIGHT JOIN regexaminee r2 on r.cNum=r2.cNum");
        sb.append(" LEFT JOIN studentpaperimage s on s.regId=r2.id");
        sb.append(" LEFT JOIN imgpath i on i.id=s.imgpath");
        sb.append(" WHERE c.examPaperNum={examPaperNum} and c.testingCentreId={testingCentreId}");
        if (StrUtil.isNotEmpty(examRoomNum) && !"-1".equals(examRoomNum)) {
            sb.append(" and c.examinationRoomNum={examRoomNum}");
        }
        sb.append(" GROUP BY r2.id");
        return this.dao2._queryMapList(sb.toString(), TypeEnum.StringObject, StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("testingCentreId", (Object) testingCenterId).put("examRoomNum", (Object) examRoomNum));
    }

    public Map<String, Object> AutoVerifyTestScanImage(String examPaperNum, String testingCenterId, String user, String machineGuid) {
        return StreamMap.create().put("verify", (Object) false).put("dpi", (Object) 200).put("width", (Object) 210).put("height", (Object) 297);
    }

    public Integer getKaoHaoJiaoDuiZhuangTai(String examNum, String gradeNum, String subjectNum, String testingCentreId) {
        Integer numStatus = this.dao2._queryInt("SELECT numStatus from correctstatus where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and testingCentreId={testingCentreId}", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("testingCentreId", (Object) testingCentreId));
        return numStatus;
    }

    public boolean MoveSubject(String examNum, String testingCentreId, String gradeNum, String subjectNum, String batch, String cNum, String user) {
        List<Map<String, Object>> regList = this.dao2._queryMapList("SELECT * from regexaminee where cNum={cNum}", TypeEnum.StringObject, StreamMap.init("cNum", cNum));
        if (CollUtil.isNotEmpty(regList)) {
            Object examPaperNum = GetExamPaperNum(examNum, gradeNum, subjectNum);
            String studentId = GUID.getGUIDStr();
            String insertDate = DateUtil.getCurrentTime();
            Object[] scannerNum = {""};
            regList.forEach(reg -> {
                String regId = reg.get("id").toString();
                scannerNum[0] = reg.get("uploadScannerNum");
                DeleteByOneRegid(regId, true, true, false);
                Map<String, Object> args = StreamMap.create();
                args.put("examPaperNum", examPaperNum);
                args.put(Const.EXPORTREPORT_schoolNum, 0);
                args.put(Const.EXPORTREPORT_studentId, studentId);
                args.put("examinationRoomNum", -1);
                args.put(Const.EXPORTREPORT_classNum, -1);
                args.put("insertUser", user);
                args.put("insertDate", insertDate);
                args.put("isModify", "F");
                args.put("checkPage", "0");
                args.put("testingCentreId", testingCentreId);
                args.put("batch", batch);
                args.put("regId", regId);
                this.dao2._execute("update regexaminee set examPaperNum={examPaperNum},schoolNum={schoolNum},studentId={studentId},examinationRoomNum={examinationRoomNum},classNum={classNum},insertUser={insertUser},insertDate={insertDate},isModify={isModify},checkPage={checkPage},testingCentreId={testingCentreId},batch={batch} where id={regId}", args);
                this.dao2._execute("update scanrecord set examPaperNum={examPaperNum},schoolNum={schoolNum},studentId={studentId},examinationRoomNum={examinationRoomNum},classNum={classNum},insertUser={insertUser},insertDate={insertDate},isModify={isModify},checkPage={checkPage},testingCentreId={testingCentreId},batch={batch} where id={regId}", args);
                this.dao2._execute("DELETE from cantrecognized where regId={regId}", StreamMap.init("regId", regId));
                String[] fields = {"regid", "examPaperNum", Const.EXPORTREPORT_schoolNum, "examinationRoomNum", "insertUser", "insertDate", "isDelete", Const.CORRECT_SCORECORRECT, "stat", "testingCentreId", "reason"};
                Object[] values = {regId, examPaperNum, "0", -1, user, insertDate, "F", "0", 0, testingCentreId, "来源：移动科目"};
                this.dao2.insert("cantrecognized", fields, values);
            });
            AddScanBatch(examNum, gradeNum, testingCentreId, subjectNum, scannerNum[0].toString(), batch, user, "10000");
            InsertCorrectStatus(examNum, examPaperNum.toString(), gradeNum, subjectNum, testingCentreId, user, insertDate);
            return true;
        }
        return true;
    }

    public boolean InsertCorrectStatus(String examNum, String examPaperNum, String gradeNum, String subjectNum, String testingCentreId, String user, String time) {
        Object o = this.dao2._queryObject("SELECT id from correctstatus where examNum={examNum} and subjectNum={subjectNum} and  gradeNum={gradeNum} and testingCentreId={testingCentreId} limit 0,1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("testingCentreId", (Object) testingCentreId));
        if (o == null || o.toString().equals("")) {
            String[] fileds = {Const.EXPORTREPORT_examNum, Const.EXPORTREPORT_subjectNum, Const.EXPORTREPORT_gradeNum, "examnitionRoom", "examPaperNum", "testingCentreId", "insertUser", "insertDate", Const.CORRECT_SCORECORRECT, Const.CORRECT_NUMCORRECT, Const.CORRECT_APPENDCORRECT, Const.CORRECT_NUMERRORCORRECT};
            Object[] values = {examNum, subjectNum, gradeNum, "-1", examPaperNum, testingCentreId, user, time, "0", "0", "0", "0"};
            this.dao2.insert("correctstatus", fileds, values);
            return true;
        }
        this.dao2._execute("update correctstatus set status={status} , appendStatus={appendStatus},numStatus={numStatus},numErrorStatus={numErrorStatus} where id={o}", StreamMap.create().put(Const.CORRECT_SCORECORRECT, (Object) "0").put(Const.CORRECT_APPENDCORRECT, (Object) "0").put(Const.CORRECT_NUMCORRECT, (Object) "0").put(Const.CORRECT_NUMERRORCORRECT, (Object) "0").put("o", o));
        return true;
    }

    public boolean AutomaticAuditTestScanImage(String examNum, String gradeNum, String subjectNum, String testingCenterId) {
        Object object = this.dao2._queryObject("SELECT subjects from automaticauditsetting where examNum={examNum} and gradeNum={gradeNum} and testingCentreId={testingCentreId} LIMIT 1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("testingCentreId", (Object) testingCenterId));
        String subjectsJson = Convert.toStr(object, "");
        if (StrUtil.isNotEmpty(subjectsJson)) {
            String[] subjects = (String[]) Jsons.parseObject(subjectsJson, JsonType.Array1_String);
            return Arrays.asList(subjects).contains(subjectNum);
        }
        return false;
    }

    public Map<String, String> AutomaticAuditTestScanImageSetting(String examNum, String gradeNum, String subjectNum, String testingCenterId) {
        Object object = this.dao2._queryObject("SELECT subjects from automaticauditsetting where examNum={examNum} and gradeNum={gradeNum} and testingCentreId={testingCentreId} LIMIT 1", StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put("testingCentreId", (Object) testingCenterId));
        String subjectsJson = Convert.toStr(object, "");
        if (StrUtil.isNotEmpty(subjectsJson)) {
            List<Map<String, String>> subjects = (List) Jsons.parseObject(subjectsJson, JsonType.List2Map1_StringString);
            Optional<Map<String, String>> subjectNumOptional = subjects.stream().filter(m -> {
                return ((String) m.get(Const.EXPORTREPORT_subjectNum)).equals(subjectNum);
            }).findFirst();
            if (subjectNumOptional.isPresent()) {
                return subjectNumOptional.get();
            }
        }
        return new HashMap();
    }

    public boolean IfCloseTestingCenterSecondaryPositioning(String testingCenterId) {
        return Convert.toBool(this.dao2._queryObject("SELECT closeSecondaryPositioning from testingcentre where id={testingcenterId}", StreamMap.init("testingcenterId", testingCenterId)), false).booleanValue();
    }

    public List<Long> GetBatchReClipIdList(String examPaperNum, List<String> testingCenterIdList) {
        return this.dao2._queryColList(" SELECT c.id from cantrecognized c LEFT JOIN regexaminee r on r.id=c.regId  where c.examPaperNum={examPaperNum} and c.testingCentreId in {testingCenterIdList[]}", Long.class, StreamMap.create().put("testingCenterIdList", (Object) testingCenterIdList).put("examPaperNum", (Object) examPaperNum));
    }

    public Map<String, String> getScanProcess(String examNum, String gradeNum) {
        List<Object> examPaperNumList = this.dao2._queryColList("select examPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum}", StreamMap.init(Const.EXPORTREPORT_examNum, examNum, Const.EXPORTREPORT_gradeNum, gradeNum));
        Map<String, String> allSubjectMap = new HashMap<>();
        examPaperNumList.forEach(examPaperNum -> {
            Map<String, String> oneSubjectMap = this.dao2._queryOrderMap("SELECT studentId,GROUP_CONCAT(id) FROM regexaminee where examPaperNum={examPaperNum} GROUP BY studentId", TypeEnum.StringString, StreamMap.init("examPaperNum", examPaperNum));
            if (CollUtil.isNotEmpty(oneSubjectMap)) {
                oneSubjectMap.forEach((studentId, regIds) -> {
                    String value = (String) allSubjectMap.getOrDefault(studentId, "");
                    if (StrUtil.isNotEmpty(value)) {
                        allSubjectMap.put(studentId, value + Const.STRING_SEPERATOR + regIds);
                    } else {
                        allSubjectMap.put(studentId, regIds);
                    }
                });
                ThreadUtil.sleep(1L);
            }
        });
        return allSubjectMap;
    }

    public boolean DeleteScanTest(String id) {
        this.dao2._execute("delete from templaterecord where testScanTemplateRecordNum in (select testScanTemplateRecordNum from scan_test where id={id})", StreamMap.init("id", id));
        this.dao2._execute("delete from scan_test where id={id}", StreamMap.init("id", id));
        return true;
    }

    public int getPairingErrorRecord(String batch) {
        return Convert.toInt(this.dao2._queryObject("SELECT count(1) from regexaminee where batch={batch}", StreamMap.init("batch", batch)), 0).intValue();
    }

    public List<Map<String, Object>> doPairingErrorRecord(String batch, List<Map<String, Object>> list) {
        list.forEach(m -> {
            m.put("filename", ((JSONObject) m.get("File")).get("OriginalPath"));
            m.put("cNum", m.get("GroupNum"));
            m.put(Const.EXPORTREPORT_studentId, m.get("GroupNum"));
            m.put("type", m.get("Page"));
            m.put("batch", batch);
        });
        this.dao2._batchExecute("update regexaminee set cNum={cNum},studentId={studentId},type={type} where batch={batch} and scantime={filename}", list);
        this.dao2._batchExecute("update scanrecord set cNum={cNum},studentId={studentId},type={type} where batch={batch} and scantime={filename}", list);
        this.dao2._execute("DELETE e from examineenumerror e JOIN regexaminee r on r.id=e.regid where r.batch = {batch}", StreamMap.init("batch", batch));
        this.dao2._execute("INSERT INTO examineenumerror (regId, examPaperNum, schoolNum, studentId, errorType, examinationRoomNum, examineeNum, groupNum, page, insertUser, insertDate, isDelete, batch, testingCentreId) SELECT id , examPaperNum, schoolNum, studentId, 0 , examinationRoomNum, ifnull(examineeNum,''), cNum , page, insertUser, insertDate,'F' isDelete, batch, testingCentreId from regexaminee WHERE batch= {batch}", StreamMap.init("batch", batch));
        List<Map<String, Object>> maps = this.dao2._queryMapList("SELECT GROUP_CONCAT(CONCAT(r.page,'@@@',p.location,'/',i.img)) img from examinationnumimg i JOIN regexaminee r ON i.regId = r.id JOIN imgpath p on p.id=i.imgpath and p.selected=1 where r.batch = {batch} GROUP BY r.cNum", TypeEnum.StringObject, StreamMap.init("batch", batch));
        return maps;
    }

    public boolean ForceMakeSelfTemplate(String testingCenterId) {
        Object id = this.dao2._queryObject("select ismusttemplate from testingcentre where id={id}", StreamMap.init("id", testingCenterId));
        return 0 == Convert.toInt(id, 0).intValue();
    }
}
