package com.dmj.serviceimpl.questionGroup;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.StyleSet;
import com.dmj.auth.bean.License;
import com.dmj.daoimpl.awardPoint.AwardPointDaoImpl;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.questionGroup.QuestionGroupDaoImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Define;
import com.dmj.domain.Exampaper;
import com.dmj.domain.Extragroupuser;
import com.dmj.domain.Grade;
import com.dmj.domain.PersonWorkRecord;
import com.dmj.domain.Role;
import com.dmj.domain.School;
import com.dmj.domain.Schoolgroup;
import com.dmj.domain.Subject;
import com.dmj.domain.Task;
import com.dmj.domain.User;
import com.dmj.domain.Userrole;
import com.dmj.domain.vo.QuestionGroup;
import com.dmj.domain.vo.QuestionGroup_question;
import com.dmj.domain.vo.QuestionGroup_user;
import com.dmj.domain.vo.Questiongroup_mark_setting;
import com.dmj.domain.vo.TestingcentreDis;
import com.dmj.service.awardPoint.AwardPointService;
import com.dmj.service.examManagement.ExamService;
import com.dmj.service.examManagement.UtilSystemService;
import com.dmj.service.questionGroup.QuestionGroupService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.awardPoint.AwardPointServiceImpl;
import com.dmj.serviceimpl.examManagement.ExamServiceImpl;
import com.dmj.serviceimpl.examManagement.UtilSystemServiceimpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.ChineseCharacterUtil;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.GUID;
import com.dmj.util.msg.RspMsg;
import com.zht.db.DbUtils;
import com.zht.db.RowArg;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import com.zht.db.SubException;
import com.zht.db.TypeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;

/* loaded from: QuestionGroupImpl.class */
public class QuestionGroupImpl implements QuestionGroupService {
    QuestionGroupDaoImpl gd = new QuestionGroupDaoImpl();
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    UtilSystemService uss = (UtilSystemService) ServiceFactory.getObject(new UtilSystemServiceimpl());
    Logger log = Logger.getLogger(getClass());
    private AwardPointService awardPointService = (AwardPointService) ServiceFactory.getObject(new AwardPointServiceImpl());
    public static ExamService examService = (ExamService) ServiceFactory.getObject(new ExamServiceImpl());
    public static SystemService systemService = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<AjaxData> getExam(String userId, String oneOrMore) {
        return this.gd.getExam(userId, oneOrMore);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<AjaxData> getGrade(String exam) {
        return this.gd.getGrade(exam);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<AjaxData> getSubject(String exam, String grade) {
        return this.gd.getSubject(exam, grade);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<AjaxData> getSubject1(String exam, String grade) {
        return this.gd.getSubject1(exam, grade);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Exampaper getexamPaperNum(String exam, String grade, String subject, String jie) {
        return this.gd.getexamPaperNum(exam, grade, subject, jie);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void createGroup(QuestionGroup qg, List<String> add, QuestionGroup_question qgq) {
        this.gd.createGroup(qg, add, qgq);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getGroup(String examPaperNum, String tab) {
        return this.gd.getGroup(examPaperNum, tab);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getAllQuestion(String examPaperNum, String insertUser) {
        return this.gd.getAllQuestion(examPaperNum, insertUser);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public int insertTestingDis(String examPaperNum, String insertUser, String school_limit) {
        return this.gd.insertTestingDis(examPaperNum, insertUser, school_limit);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getQuestionNum(QuestionGroup_question qq) {
        return this.gd.getQuestionNum(qq);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getXztQuestionNum(QuestionGroup_question qq) {
        return this.gd.getXztQuestionNum(qq);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List restQuestion(String examPaperNum, String tab, String type, String userId, String schoolNum, String schoolGroupNum) {
        return this.gd.restQuestion(examPaperNum, tab, type, userId, schoolNum, schoolGroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getYuZhiList(String examPaperNum, String groupNum, String groupType, String userId, String isParent) {
        return this.gd.getYuZhiList(examPaperNum, groupNum, groupType, userId, isParent);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<TestingcentreDis> getTestingcentreDis(String examPaperNum) {
        return this.gd.getTestingcentreDis(examPaperNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public TestingcentreDis getTestingcentreDisOne(String examPaperNum, String testingCentreId, String school_limit, String insertUser) {
        return this.gd.getTestingcentreDisOne(examPaperNum, testingCentreId, school_limit, insertUser);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void resetSaoMiaoStatus(String examPaperNum) {
        this.gd.resetSaoMiaoStatus(examPaperNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String updateTestingcentreDis(String examPaperNum, String testingCentreId, String isDis, String userId) {
        return this.gd.updateTestingcentreDis(examPaperNum, testingCentreId, isDis, userId);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateProofreadingStatus(String examNum, String gradeNum, String subjectNum, String testingCentreId, String userId) {
        Map args = new HashMap();
        args.put("userId", userId);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("testingCentreId", testingCentreId);
        this.dao._execute("update clippagemark set proofreadingStatus = '1',updateUser = {userId} ,updateDate = now() where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and testingCentreId={testingCentreId}", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateChoose(String examPaperNum) {
        ServletContext context = ServletActionContext.getServletContext();
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        List<?> _queryBeanList = this.dao._queryBeanList("SELECT q.groupNum,q.groupName name,d.ext2,d.choosename ext1 from(    \tSELECT groupNum,groupName from questiongroup where exampaperNum={examPaperNum}  )q LEFT JOIN (     \tSELECT id,choosename,0 ext2 from define where exampaperNum={examPaperNum}  and choosename<>'s' and choosename<>'T'   \t\tUNION     \tSELECT sb.id,sb.pid choosename,1 ext2 from define d     \tLEFT JOIN subdefine sb on sb.pid=d.id  \twhere d.examPaperNum={examPaperNum} \t\tand d.choosename<>'s' and d.choosename<>'T'    )d on q.groupNum=d.id where d.id is not null ", QuestionGroup.class, args);
        for (int i = 0; i < _queryBeanList.size(); i++) {
            long start = new Date().getTime();
            this.awardPointService.getQuestionGroupInfo(String.valueOf(examPaperNum), ((QuestionGroup) _queryBeanList.get(i)).getGroupNum(), context);
            long tt = new Date().getTime() - start;
            this.log.info("【选做题工作量更新】" + ((QuestionGroup) _queryBeanList.get(i)).getGroupNum() + ":" + tt);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List errorRateandfullsore(String groupNum) {
        return this.gd.errorRateandfullsore(groupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getrestQuestionNum(String examPaperNum, String groupNum, int tab) {
        return this.gd.getrestQuestionNum(examPaperNum, groupNum, tab);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public int updateThreeWarn(String groupNum, String value) {
        return this.gd.updateThreeWarn(groupNum, value);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getgroupDownCount(String examPaperNum, String groupNum, String type) {
        return this.gd.getgroupDownCount(examPaperNum, groupNum, type);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer updateGroupQuestion(QuestionGroup qg, QuestionGroup_question qq, String loginName) {
        return this.gd.updateGroupQuestion(qg, qq, loginName);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer removeGroupQuestion(Integer examPaperNum, String questionNum, String groupNum, String questionName, String loginName) {
        return this.gd.removeGroupQuestion(examPaperNum, questionNum, groupNum, questionName, loginName);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer moveRestQuestion(QuestionGroup qg, QuestionGroup_question qq) {
        if (null != qg.getGroupName() && !"".equals(qg.getGroupName())) {
            this.dao.save(qg);
            createGroupfz_mark_setting(qg.getExampaperNum(), qg.getGroupNum(), qg.getInsertUser());
        }
        this.gd.deleteGroupfz(qq.getExampaperNum(), qq.getQuestionNum(), "", "1", "");
        this.dao.save(qq);
        Map args = new HashMap();
        args.put("groupNum", qq.getGroupNum());
        args.put("examPaperNum", qq.getExampaperNum());
        args.put("questionNum", qq.getQuestionNum());
        this.dao._execute("update task set groupNum={groupNum}  where examPaperNum={examPaperNum} and groupNum={questionNum}  ", args);
        String markval = this.dao._queryStr("select makType from questiongroup_mark_setting  where examPaperNum={examPaperNum}  and groupNum={groupNum} ", args);
        if ("0".equals(markval)) {
            this.dao._execute(" delete from task where examPaperNum={examPaperNum}  and groupNum={groupNum} and usernum!='1' ", args);
            this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum} and groupNum={groupNum} and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
            this.dao._execute("delete from remark where examPaperNum={examPaperNum} and questionNum={questionNum} ", args);
        } else if (markval.equals("1")) {
            this.dao._execute("update score  s left join  (SELECT scoreid,questionScore from task where examPaperNum={examPaperNum} and groupNum={groupNum} and status='T' and usernum='1')t on s.id=t.scoreid  set s.questionScore=t.questionScore where s.id=t.scoreid", args);
            this.dao._execute("delete from remark where examPaperNum={examPaperNum} and questionNum={groupNum} ", args);
            this.dao._execute(" delete from task where examPaperNum={examPaperNum}   and groupNum={groupNum}  and usernum!='1' ", args);
            this.dao._execute(" INSERT INTO task(id,scoreId,examPaperNum,questionNum,insertDate,updateTime,isException,isDelete ,status ,groupNum, rownum,insertuser,testingCentreId,userNum)SELECT r.*,'2' FROM  \t(  SELECT  UUID_SHORT() id,scoreId,examPaperNum,questionNum,now() insertDate,now() updateTime,'F' isException, isDelete ,'F' status,groupNum,0 rownum,-1 insertuser,testingCentreId  from  task  where examPaperNum={examPaperNum}   and groupNum={groupNum} and usernum='1')r ", args);
        }
        String counttask2 = this.dao._queryStr(" select count(1) from task where examPaperNum={examPaperNum} and groupNum={groupNum} ", args);
        args.put("counttask2", counttask2);
        this.dao._execute(" update questiongroup set totalnum={counttask2} where examPaperNum={examPaperNum} and groupNum={groupNum} ", args);
        return null;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer deletegroup(QuestionGroup_question qq) {
        return this.gd.deletegroup(qq);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getGroupByName(Integer examPaperNum, String name) {
        return this.gd.getGroupByName(examPaperNum, name);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getPjUserList(String examNum, String examPaperNum, String roleNum, String realName, String roleType, String schoolNum, String note, String ext1, String category, String newGroupNum, String type, String schoolGroupNum) {
        return this.gd.getPjUserList(examNum, examPaperNum, roleNum, realName, roleType, schoolNum, note, ext1, category, newGroupNum, type, schoolGroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getPjUserList2(String examNum, String examPaperNum, String roleNum, String realName, String roleType, String schoolNum, String note, String ext1, String subjectNum) {
        return this.gd.getPjUserList2(examNum, examPaperNum, roleNum, realName, roleType, schoolNum, note, ext1, subjectNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<User> getUsersByRoleNum(String examNum, String roleNum) {
        return this.gd.getUsersByRoleNum(examNum, roleNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer addAndDeleteUser(List<String> add, List<String> delete, QuestionGroup_user qu) {
        if (null == qu || null == qu.getGroupNum() || qu.getGroupNum().equals("")) {
            return null;
        }
        if (add != null && add.size() != 0) {
            for (String uNum : add) {
                QuestionGroup_user qu2 = new QuestionGroup_user();
                qu2.setId(GUID.getGUIDStr());
                qu2.setExampaperNum(qu.getExampaperNum());
                qu2.setInsertDate(DateUtil.getCurrentDay());
                qu2.setInsertUser(qu.getInsertUser());
                qu2.setGroupNum(qu.getGroupNum());
                qu2.setUserNum(uNum);
                qu2.setUserType("0");
                qu2.setIsFinished("0");
                Integer.valueOf(this.dao.save(qu2));
            }
        }
        if (null != delete && delete.size() != 0) {
            List<Map> argsMap = new ArrayList<>();
            for (String uNum2 : delete) {
                deletetask(qu.getExampaperNum(), qu.getGroupNum(), uNum2, "1", "");
                Map args = StreamMap.create().put("exampaperNum", (Object) qu.getExampaperNum()).put("userNum", (Object) qu.getUserNum()).put("groupNum", (Object) qu.getGroupNum());
                argsMap.add(args);
            }
            this.dao._batchUpdate("delete from QuestionGroup_user where exampaperNum={exampaperNum} and userNum ={userNum} and groupNum = {groupNum} ", argsMap);
        }
        return 1;
    }

    public void setUpchooseCount(String examPapernum, String groupNum, String userNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        String totalNum = this.dao._queryStr("SELECT totalnum from questiongroup where groupNum={groupNum} ", args);
        this.dao._queryBeanList("SELECT * from questiongroup_user where groupNum={groupNum} ", QuestionGroup_user.class, args);
        String p_num = this.dao._queryStr("SELECT * from questiongroup_user where groupNum={groupNum} ", args);
        int max = Integer.parseInt(totalNum) / Integer.parseInt(p_num);
        if (Integer.parseInt(totalNum) % Integer.parseInt(p_num) != 0) {
            int i = max + 1;
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer addAndDeleteUserNew(List<String> addAnddelete, QuestionGroup_user qu, String operatetype) {
        String pexampaperNum;
        String sunExampaperNum;
        List<Object> sunExampaperNumlist;
        if (null == qu || null == qu.getGroupNum() || qu.getGroupNum().equals("")) {
            return null;
        }
        String[] groupNumArr = qu.getGroupNum().split("_");
        for (int i = 0; i < groupNumArr.length; i++) {
            int finalI = i;
            Map args = new HashMap();
            args.put("exampaperNum", qu.getExampaperNum());
            args.put("groupNum", groupNumArr[finalI]);
            args.put("insertUser", qu.getInsertUser());
            if (addAnddelete != null && addAnddelete.size() != 0 && (operatetype.equals("add") || operatetype.equals("allgroup") || operatetype.equals("addAssister"))) {
                for (String uNum : addAnddelete) {
                    if (!StrUtil.isEmpty(uNum)) {
                        args.put("userNum", uNum);
                        String isadd = "1";
                        if (operatetype.equals("allgroup") && null != (sunExampaperNum = this.dao._queryStr("SELECT IFNULL(d.category,sd.category) category from define d LEFT JOIN subdefine sd ON d.id=sd.pid  WHERE d.examPaperNum={exampaperNum} AND (d.id={groupNum} OR sd.id={groupNum})", args)) && !sunExampaperNum.equals("") && null != (sunExampaperNumlist = this.dao._queryColList("SELECT usb.examPaperNum from userrole_sub usb LEFT JOIN exampaper ep ON usb.examPaperNum=ep.examPaperNum WHERE ep.pexamPaperNum={exampaperNum} AND usb.userNum={userNum}", args)) && sunExampaperNumlist.size() > 0) {
                            isadd = "0";
                            for (int j = 0; j < sunExampaperNumlist.size(); j++) {
                                String sunExampaperNum2 = Convert.toStr(sunExampaperNumlist.get(j));
                                if (sunExampaperNum.equals(sunExampaperNum2)) {
                                    isadd = "1";
                                }
                            }
                        }
                        Object countobj = this.dao._queryObject("select count(1) from  questiongroup_user where exampaperNum={exampaperNum}  and groupNum = {groupNum} and userType <>2 and userNum={userNum} ", args);
                        String countstr = String.valueOf(countobj);
                        if ("0".equals(countstr) && isadd.equals("1")) {
                            QuestionGroup_user qu2 = new QuestionGroup_user();
                            qu2.setId(GUID.getGUIDStr());
                            qu2.setExampaperNum(qu.getExampaperNum());
                            qu2.setInsertDate(DateUtil.getCurrentDay());
                            qu2.setInsertUser(qu.getInsertUser());
                            qu2.setGroupNum(groupNumArr[i]);
                            qu2.setUserNum(uNum);
                            qu2.setUserType("0");
                            qu2.setIsFinished("0");
                            Integer.valueOf(this.dao.save(qu2));
                            args.put("caozuo", "增加阅卷教师");
                            this.dao._execute("INSERT INTO quotalog(`exampaperNum`, `groupNum`, `caozuo`,`usernum`, `updateUser`, `updateDate`)  VALUES ({exampaperNum}, {groupNum}, {caozuo},{userNum}, {insertUser}, NOW()); ", args);
                        }
                        List<Map<String, Object>> sublist = this.dao._queryMapList("SELECT subjectNum,isHidden,examPaperNum,pexamPaperNum,examNum from exampaper WHERE examPaperNum={exampaperNum}", null, args);
                        String isHidden = sublist.get(0).get("isHidden").toString();
                        String examNum = sublist.get(0).get(Const.EXPORTREPORT_examNum).toString();
                        args.put(Const.EXPORTREPORT_examNum, examNum);
                        if (isHidden.equals("T")) {
                            pexampaperNum = sublist.get(0).get("pexamPaperNum").toString();
                            args.put("sunexampaperNum", sublist.get(0).get("examPaperNum").toString());
                        } else {
                            pexampaperNum = sublist.get(0).get("examPaperNum").toString();
                        }
                        args.put("pexampaperNum", pexampaperNum);
                        List<Map<String, Object>> rlist = this.dao._queryMapList("SELECT r.roleNum,ep.examPaperNum,ep.pexamPaperNum,ep.isHidden from role r LEFT JOIN exampaper ep on r.examPaperNum=ep.examPaperNum WHERE r.type='4' and ep.examPaperNum={pexampaperNum}", null, args);
                        String id = GUID.getGUIDStr();
                        args.put("id", id);
                        if (rlist.size() == 0) {
                            this.dao._execute("INSERT INTO role(roleNum, roleName, schoolNum, examNum, examPaperNum, type, insertUser, insertDate, isDelete) VALUES ({id}, '阅卷员', '0', {examNum}, {pexampaperNum}, '4', {insertUser}, SUBSTR(NOW(), 1, 10), 'F')", args);
                            args.put("roleNum", id);
                        } else {
                            String roleNum = rlist.get(0).get("roleNum").toString();
                            args.put("roleNum", roleNum);
                        }
                        String ucount = this.dao._queryStr("SELECT count(1) from userrole WHERE userNum={userNum} AND roleNum={roleNum}", args);
                        if (ucount.equals("0")) {
                            this.dao._execute("INSERT INTO userrole( userNum, roleNum, insertUser, insertDate, isDelete) VALUES ( {userNum}, {roleNum}, {insertUser}, NOW(), 'F')", args);
                        }
                        String ucount2 = this.dao._queryStr("SELECT count(1) from userrole WHERE userNum={userNum} and roleNum='4'", args);
                        if (ucount2.equals("0")) {
                            this.dao._execute("INSERT INTO userrole( `userNum`, `roleNum`, `insertUser`, `insertDate`, `isDelete`) VALUES ({userNum}, 4,{insertUser} , now(), 'F')", args);
                        }
                        if (isHidden.equals("T")) {
                            String uscount = this.dao._queryStr("SELECT count(1) from userrole_sub WHERE userNum={userNum} AND examPaperNum={sunexampaperNum}", args);
                            if (uscount.equals("0")) {
                                this.dao._execute("INSERT INTO  userrole_sub (exampaperNum, userNum) VALUES ({sunexampaperNum}, {userNum})", args);
                            }
                        }
                        if (operatetype.equals("addAssister")) {
                            String count = this.dao._queryStr("select ifnull(count(1),0) from quota where examPaperNum={exampaperNum} and groupNum={groupNum} and insertUser={userNum} ", args);
                            if ("0".equals(count)) {
                                this.dao._execute("insert into quota(exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{groupNum} ,0,{userNum},now())", args);
                            }
                            String count2 = this.dao._queryStr("select ifnull(count(1),0) from assistyuejuan where examPaperNum={exampaperNum} and groupNum={groupNum} and assister={userNum} ", args);
                            if ("0".equals(count2)) {
                                args.put("insertUser", qu.getInsertUser());
                                this.dao._execute("insert into assistyuejuan(exampaperNum,examNum,gradeNum,subjectNum,groupNum,assister,updateUser,updateDate,status) SELECT examPaperNum,examNum,gradeNum,subjectNum,{groupNum},{userNum},{insertUser},now(),1 from exampaper where examPaperNum={exampaperNum} ", args);
                                args.put("caozuo", "增加协助阅卷员");
                                this.dao._execute("INSERT INTO quotalog(`exampaperNum`, `groupNum`, `caozuo`,`usernum`, `updateUser`, `updateDate`)  VALUES ({exampaperNum}, {groupNum}, {caozuo},{userNum}, {insertUser}, NOW()); ", args);
                            } else {
                                this.dao._execute("update assistyuejuan set status=1 where examPaperNum={exampaperNum} and groupNum={groupNum} and assister={userNum} ", args);
                            }
                        }
                        args.remove("userNum");
                    }
                }
            }
            if (null != addAnddelete && addAnddelete.size() != 0 && operatetype.equals("clear")) {
                List<Map> maps = new ArrayList<>();
                for (String uNum2 : addAnddelete) {
                    if (!StrUtil.isEmpty(uNum2)) {
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("userNum", uNum2);
                        map2.put("exampaperNum", args.get("exampaperNum"));
                        map2.put("groupNum", groupNumArr[finalI]);
                        map2.put("caozuo", "删除阅卷教师");
                        map2.put("insertUser", qu.getInsertUser());
                        maps.add(map2);
                    }
                }
                this.dao._batchUpdate("delete from questiongroup_user where exampaperNum={exampaperNum} and userNum = {userNum} and groupNum = {groupNum} ", maps);
                this.dao._batchUpdate("delete from assistyuejuan where examPaperNum={exampaperNum}  and assister = {userNum} and groupNum ={groupNum} ", maps);
                this.dao._batchUpdate("delete from quota where groupNum={groupNum} and insertUser={userNum} ", maps);
                this.dao._batchUpdate("INSERT INTO quotalog(`exampaperNum`, `groupNum`, `caozuo`,`usernum`, `updateUser`, `updateDate`)  VALUES ({exampaperNum}, {groupNum}, {caozuo},{userNum}, {insertUser}, NOW()) ", maps);
            }
        }
        return 1;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String ishavenum(String questiongroupNum, String usernum) {
        String flag = "1";
        Map args = new HashMap();
        args.put("groupNum", questiongroupNum);
        args.put("userNum", usernum);
        Integer num = this.dao._queryInt("SELECT num from quota where groupNum = {groupNum} and insertUser={userNum}", args);
        if (null != num && num.intValue() > 0) {
            flag = "0";
        }
        return flag;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<User> getUsersByGroupNum(String examPaperNum, String groupNum, String realName, String schoolNum, String type, String loginNum, String schoolGroupNum) {
        return this.gd.getUsersByGroupNum(examPaperNum, groupNum, realName, schoolNum, type, loginNum, schoolGroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<User> querygroupuserz(String examPaperNum, String groupNum, String realName, String type, String loginNum) {
        return this.gd.querygroupuserz(examPaperNum, groupNum, realName, type, loginNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<User> querygroupuserzByGroup(String examPaperNum, String groupNum, String realName, String schoolGroupNum) {
        return this.gd.querygroupuserzByGroup(examPaperNum, groupNum, realName, schoolGroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List queryQuestionUser(QuestionGroup_user qgu) {
        return this.gd.queryQuestionUser(qgu);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Map querychooselist(QuestionGroup_user qgu) {
        return this.gd.querychooselist(qgu);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer removeGroupUser(String examPaperNum, String groupNum, String userNum) {
        return this.gd.removeGroupUser(examPaperNum, groupNum, userNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer updateGroupUser(QuestionGroup_user qu, String oldgroupNum) {
        return this.gd.updateGroupUser(qu, oldgroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void questionLeader(String examPaperNum, String groupNum, String userNum, String usertype, String loginUserId) {
        this.gd.questionLeader(examPaperNum, groupNum, userNum, usertype, loginUserId);
    }

    public void updateUserRole(String roleNum, String newUserNum, String oldUserNum, String loginName) {
        Map args = new HashMap();
        args.put("roleNum", roleNum);
        args.put("newUserNum", newUserNum);
        Userrole urBean = (Userrole) this.dao._queryBean("select id,userNum,roleNum from userrole where roleNum={roleNum}  and userNum={newUserNum}  ", Userrole.class, args);
        args.put("oldUserNum", oldUserNum);
        String oldUserNum2 = this.dao._queryStr("select userNum from QuestionGroup_user where userNum={oldUserNum} and  userType='2' ", args);
        Userrole ur = new Userrole();
        if (urBean == null) {
            ur.setRoleNum(roleNum);
            ur.setUserNum(newUserNum);
            ur.setInsertUser(loginName);
            ur.setInsertDate(DateUtil.getCurrentDay());
            this.dao.save(ur);
            if (StrUtil.isEmpty(oldUserNum2)) {
                this.dao._execute("delete from userrole where userNum={oldUserNum} and roleNum={roleNum} ", args);
                return;
            }
            return;
        }
        if (StrUtil.isEmpty(oldUserNum2)) {
            this.dao._execute("delete from userrole where userNum={oldUserNum} and roleNum={roleNum} ", args);
        }
    }

    public void updateUserRole2(String examNum, String examPaperNum, String newUserNum, String oldUserNum, String loginName) {
        String roleNum1 = GUID.getGUIDStr();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("examPaperNum", examPaperNum);
        String roleNum = this.dao._queryStr("SELECT roleNum FROM  role WHERE type='3' and examNum={examNum} and examPaperNum={examPaperNum} ", args);
        if (StrUtil.isEmpty(roleNum)) {
            Role role = new Role();
            role.setRoleNum(String.valueOf(roleNum1));
            role.setRoleName("科目组长");
            role.setExamNum(Integer.valueOf(examNum));
            role.setExamPaperNum(Integer.valueOf(examPaperNum));
            role.setSchoolNum(0);
            role.setInsertUser(loginName);
            role.setType("3");
            role.setIsDelete("F");
            role.setInsertDate(DateUtil.getCurrentDay());
            this.dao.save(role);
            Userrole ur = new Userrole();
            ur.setRoleNum(String.valueOf(roleNum1));
            ur.setUserNum(newUserNum);
            ur.setInsertUser(loginName);
            ur.setInsertDate(DateUtil.getCurrentDay());
            this.dao.save(ur);
            return;
        }
        args.put("newUserNum", newUserNum);
        args.put("roleNum", roleNum);
        this.dao._execute("update userrole set userNum={newUserNum}  where userNum={newUserNum}  and roleNum={roleNum} ", args);
    }

    public Integer subjectLeaderOld(String examPaperNum, String examNum, String newUserNum, String id, String loginName) {
        Integer i;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        String oldUserNum = this.dao._queryStr("select userNum from QuestionGroup_user where examPaperNum={examPaperNum} and userType='2' ", args);
        if (oldUserNum == null || oldUserNum.equals("") || oldUserNum.equals("null")) {
            QuestionGroup_user qu2 = new QuestionGroup_user();
            qu2.setId(GUID.getGUIDStr());
            qu2.setExampaperNum(Integer.valueOf(examPaperNum));
            qu2.setInsertDate(DateUtil.getCurrentDay());
            qu2.setInsertUser(loginName);
            qu2.setUserNum(newUserNum);
            qu2.setUserType("2");
            qu2.setIsFinished("0");
            i = Integer.valueOf(this.dao.save(qu2));
            updateUserRole("3", newUserNum, newUserNum, loginName);
            updateUserRole2(examNum, examPaperNum, newUserNum, newUserNum, loginName);
        } else {
            updateUserRole2(examNum, examPaperNum, newUserNum, oldUserNum, loginName);
            String sql = "update QuestionGroup_user set userNum='" + newUserNum + "' where examPaperNum={examPaperNum} and userType='2' ";
            i = Integer.valueOf(this.dao._execute(sql, args));
            updateUserRole("3", newUserNum, oldUserNum, loginName);
        }
        return i;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer subjectLeader(String examPaperNum, String examNum, String newUserNum, String id, String loginName) {
        Integer i;
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("newUserNum", (Object) newUserNum);
        String oldUserNum = this.dao._queryStr("select userNum from QuestionGroup_user where examPaperNum={examPaperNum} and userType='2' and userNum ={newUserNum}  ", args);
        if (oldUserNum == null || oldUserNum.equals("") || oldUserNum.equals("null")) {
            QuestionGroup_user qu2 = new QuestionGroup_user();
            qu2.setId(GUID.getGUIDStr());
            qu2.setExampaperNum(Integer.valueOf(examPaperNum));
            qu2.setInsertDate(DateUtil.getCurrentDay());
            qu2.setInsertUser(loginName);
            qu2.setUserNum(newUserNum);
            qu2.setUserType("2");
            qu2.setIsFinished("0");
            i = Integer.valueOf(this.dao.save(qu2));
            updateUserRole("3", newUserNum, newUserNum, loginName);
            updateUserRole2(examNum, examPaperNum, newUserNum, newUserNum, loginName);
        } else {
            updateUserRole2(examNum, examPaperNum, newUserNum, oldUserNum, loginName);
            i = Integer.valueOf(this.dao._execute("update QuestionGroup_user set userNum={newUserNum} where examPaperNum={examPaperNum} and userType='2' ", args));
            updateUserRole("3", newUserNum, oldUserNum, loginName);
        }
        return i;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getSchAndUserList(String groupNum, String schoolNum, String type, String loginNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("loginNum", loginNum);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={loginNum} and type=1 limit 1", args);
        String[] groupNumArr = groupNum.split("_");
        String newGroupNum = "";
        for (String str : groupNumArr) {
            newGroupNum = newGroupNum + Const.STRING_SEPERATOR + str;
        }
        newGroupNum.substring(1, newGroupNum.length());
        String schStr = "";
        String schStr1 = "";
        String headSchoolGroupNameStr = "CONCAT(IFNULL(slg.schoolGroupName,'未分组'),'-',s.schoolName) ";
        if ("teacher".equals(type) && !"-1".equals(loginNum) && !"-2".equals(loginNum) && null == map) {
            String schoolStr = (StrUtil.isEmpty(schoolNum) || "-1".equals(schoolNum)) ? "" : " and schoolnum={schoolNum} ";
            schStr = ((schStr + " inner join(") + " SELECT schoolNum from user where id={loginNum}  " + schoolStr + " UNION  SELECT schoolNum from schoolscanpermission where userNum={loginNum}  " + schoolStr) + " )st on cast(sq.schoolNum as char)=cast(st.schoolNum as char) ";
            schStr1 = ((schStr1 + " inner join(") + " SELECT schoolNum from user where id={loginNum} " + schoolStr + " UNION  SELECT schoolNum from schoolscanpermission where userNum={loginNum} " + schoolStr) + " )st on cast(u.schoolNum as char)=cast(st.schoolNum as char) ";
            headSchoolGroupNameStr = "CONCAT(s.schoolName) ";
        }
        args.put("newGroupNum", "in({newGroupNum})");
        String exampaperNum = this.dao._queryStr("select examPaperNum from questiongroup where groupNum={groupNum} ", args);
        String sql = "SELECT * from((SELECT '" + groupNum + "' groupNum,if(p.fenzuyuejuan=0,s.schoolName," + headSchoolGroupNameStr + ") name,s.id,'-1' pid,ifnull(sq.num,'') num from schoolquota sq INNER JOIN school s on sq.schoolNum=s.id  LEFT JOIN exampaper p on p.examPaperNum={exampaperNum} LEFT JOIN schoolgroup slg on sq.schoolNum=slg.schoolNum " + schStr + " where sq.groupNum ={groupNum} ) union (SELECT qu.groupNum,CONCAT(u.realname,'-',u.username) name,qu.userNum id,u.schoolnum pid,ifnull(qo.num,'') num from questiongroup_user qu LEFT JOIN user u on qu.userNum=u.id LEFT JOIN quota qo on qu.groupNum=qo.groupNum and qu.userNum=qo.insertUser " + schStr1 + "where qu.groupNum ={groupNum} and qu.userType<>2) ";
        String sql2 = sql + " )r ORDER BY CONVERT(r.name USING gbk) asc";
        Map argss = StreamMap.create().put("loginNum", (Object) loginNum).put("exampaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum).put("groupNum", (Object) groupNumArr[0]);
        return this.dao._queryBeanList(sql2, QuestionGroup_user.class, argss);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getGroupAndUserList(String groupNum, String schoolGroupNum, String schoolNum) {
        String sgWStr;
        List<Map<String, Object>> res;
        String[] groupNumArr = groupNum.split("_");
        Map args = StreamMap.create().put("groupNum", (Object) groupNumArr[0]).put("schoolGroupNum", (Object) schoolGroupNum).put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        new ArrayList();
        if ("-2".equals(schoolGroupNum)) {
            String schstr = "";
            if (!schoolNum.equals("-1")) {
                schstr = " and u.schoolnum={schoolNum}";
            }
            String userSql = "SELECT qu.exampaperNum,qu.groupNum,CONCAT(sch.schoolName,'-',u.realname,'-',u.username) name,qu.userNum id,-2 pid,ifnull(qo.num,'') num,count(distinct t.studentId) num_yipan,sch.schoolName from questiongroup_user qu LEFT JOIN user u on qu.userNum=u.id LEFT JOIN school sch on u.schoolnum=sch.id LEFT JOIN quota qo on qu.groupNum=qo.groupNum and qu.userNum=qo.insertUser LEFT JOIN task t on qu.groupNum=t.groupNum and qu.userNum=t.updateUser and t.status='T' where qu.groupNum ={groupNum} and qu.userType<>2 " + schstr + " GROUP BY qu.userNum ";
            res = this.dao._queryMapList(userSql, TypeEnum.StringObject, args);
        } else {
            String schstr2 = "";
            if ("-1".equals(schoolGroupNum)) {
                sgWStr = " and sg.schoolGroupNum is null ";
            } else {
                sgWStr = " and sg.schoolGroupNum={schoolGroupNum} ";
            }
            if (!schoolNum.equals("-1")) {
                schstr2 = " and sg.schoolNum={schoolNum}";
            }
            String userSql2 = "SELECT qu.exampaperNum,qu.groupNum,CONCAT(sch.schoolName,'-',u.realname,'-',u.username) name,qu.userNum id,IFNULL(sg.schoolGroupNum,-1) pid,ifnull(qo.num,'') num,count(distinct t.studentId) num_yipan,sch.schoolName from questiongroup_user qu LEFT JOIN user u on qu.userNum=u.id LEFT JOIN school sch on u.schoolnum=sch.id left join schoolgroup sg on sg.schoolNum=u.schoolnum LEFT JOIN quota qo on qu.groupNum=qo.groupNum and qu.userNum=qo.insertUser LEFT JOIN task t on qu.groupNum=t.groupNum and qu.userNum=t.updateUser and t.status='T' where qu.groupNum ={groupNum} and qu.userType<>2 " + sgWStr + schstr2 + " GROUP BY qu.userNum ";
            res = this.dao._queryMapList(userSql2, TypeEnum.StringObject, args);
        }
        if (CollUtil.isNotEmpty(res)) {
            args.put("exampaperNum", res.get(0).get("exampaperNum"));
            List<Object> otherGroupUserList = this.dao._queryColList("select userNum from questiongroup_user where exampaperNum={exampaperNum} and groupNum<>{groupNum} and userType<>2", args);
            res.forEach(oneTeaMap -> {
                if (otherGroupUserList.contains(oneTeaMap.get("id"))) {
                    oneTeaMap.put("isKuati", 1);
                } else {
                    oneTeaMap.put("isKuati", 0);
                }
            });
            Comparator<Map<String, Object>> order1 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
            Comparator<Map<String, Object>> order2 = Comparator.comparing(m -> {
                return Convert.toInt(m.get("num_yipan"), 0);
            });
            Comparator<Map<String, Object>> order3 = Comparator.comparing(m2 -> {
                return Convert.toInt(m2.get("isKuati"), 0);
            });
            res = (List) res.stream().sorted(order1.thenComparing(order2).thenComparing(order3)).collect(Collectors.toList());
        }
        return res;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getMarkTypeByGroupNum(String groupNum) {
        String[] groupNumArr = groupNum.split("_");
        Map args = StreamMap.create().put("groupNum", (Object) groupNumArr[0]);
        Map<String, Object> qms = this.dao._querySimpleMap("SELECT makType,judgetype from questiongroup_mark_setting WHERE groupNum={groupNum} ", args);
        if (CollUtil.isEmpty(qms)) {
            return "0_0_0";
        }
        String makType = "1".equals(qms.get("makType")) ? "1" : "0";
        String judgetype = Convert.toStr(qms.get("judgetype"), "0");
        String yuguSanpinglv = "0";
        if ("1".equals(makType) && ("1".equals(judgetype) || "2".equals(judgetype))) {
            Map<String, Object> auto = this.dao._querySimpleMap("select round(IFNULL(yuguSanpinglv,0.1),2) yuguSanpinglv from distributeAuto where groupNum={groupNum} ", args);
            yuguSanpinglv = CollUtil.isEmpty(auto) ? "0.1" : Convert.toStr(auto.get("yuguSanpinglv"), "0.1");
        }
        return makType + "_" + judgetype + "_" + yuguSanpinglv;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer addSubjectLeader(String examPaperNum, String examNum, String userNum, String loginName) {
        Integer i = null;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("userNum", userNum);
        String id = this.dao._queryStr("select id from QuestionGroup_user where examPaperNum={examPaperNum} and userType='2' and userNum = {userNum} ", args);
        if (id == null || id.equals("") || id.equals("null")) {
            QuestionGroup_user qu = new QuestionGroup_user();
            qu.setId(GUID.getGUIDStr());
            qu.setExampaperNum(Integer.valueOf(examPaperNum));
            qu.setInsertDate(DateUtil.getCurrentTime());
            qu.setInsertUser(loginName);
            qu.setUserNum(userNum);
            qu.setUserType("2");
            qu.setIsFinished("0");
            i = Integer.valueOf(this.dao.save(qu));
        }
        Userrole urBean = (Userrole) this.dao._queryBean("select id,userNum,roleNum from userrole where roleNum='3' and userNum={userNum}", Userrole.class, args);
        if (urBean == null) {
            Userrole ur = new Userrole();
            ur.setRoleNum("3");
            ur.setUserNum(userNum);
            ur.setInsertUser(loginName);
            ur.setInsertDate(DateUtil.getCurrentTime());
            this.dao.save(ur);
        }
        String roleNum1 = GUID.getGUIDStr();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String roleNum = this.dao._queryStr("SELECT roleNum FROM  role WHERE type='3' and examNum={examNum} and examPaperNum={examPaperNum} ", args);
        if (StrUtil.isEmpty(roleNum)) {
            Role role = new Role();
            role.setRoleNum(String.valueOf(roleNum1));
            role.setRoleName("科目组长");
            role.setExamNum(Integer.valueOf(examNum));
            role.setExamPaperNum(Integer.valueOf(examPaperNum));
            role.setSchoolNum(0);
            role.setInsertUser(loginName);
            role.setType("3");
            role.setIsDelete("F");
            role.setInsertDate(DateUtil.getCurrentTime());
            this.dao.save(role);
            Userrole ur2 = new Userrole();
            ur2.setRoleNum(String.valueOf(roleNum1));
            ur2.setUserNum(userNum);
            ur2.setInsertUser(loginName);
            ur2.setInsertDate(DateUtil.getCurrentTime());
            this.dao.save(ur2);
        } else {
            args.put("roleNum", roleNum);
            Userrole urBean1 = (Userrole) this.dao._queryBean("select id,userNum,roleNum from userrole where roleNum={roleNum}  and userNum={userNum} ", Userrole.class, args);
            if (urBean1 == null) {
                Userrole ur3 = new Userrole();
                ur3.setRoleNum(roleNum);
                ur3.setUserNum(userNum);
                ur3.setInsertUser(loginName);
                ur3.setInsertDate(DateUtil.getCurrentTime());
                this.dao.save(ur3);
            }
        }
        return i;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public QuestionGroup_user querySubjectLeader(String examPaperNum, String userType) {
        return this.gd.querySubjectLeader(examPaperNum, userType);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List queryQuestionLeader(String examPaperNum, String groupNum, String userNum, String type) {
        return this.gd.queryQuestionLeader(examPaperNum, groupNum, userNum, type);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer insertQuestiongroup_user(String examPaperNum, String groupNum, String userNum) {
        return this.gd.insertQuestiongroup_user(examPaperNum, groupNum, userNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List fetchTiNum(Integer examPaperNum) {
        return this.gd.fetchTiNum(examPaperNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Map<String, Map<String, String>> fetchTiNumchoose(String[] qNumArr) {
        return this.gd.fetchTiNumchoose(qNumArr);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String switchChoose(Map<String, String> paramMap, String userNum) {
        return this.gd.switchChoose(paramMap, userNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer updateMarkType(String examPaperNum, String groupNum, String makType, String makType2, String qmsType, String groupType, String loginNum) {
        return this.gd.updateMarkType(examPaperNum, groupNum, makType, makType2, qmsType, groupType, loginNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer updateAssess(String examPaperNum, String groupNum, String makType, String makType2, String qmsType, String groupType, String loginNum) {
        return this.gd.updateAssess(examPaperNum, groupNum, makType, makType2, qmsType, groupType, loginNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer createqms(Integer examPaperNum, String groupNum, String qmsType, String loginName) {
        return this.gd.createqms(examPaperNum, groupNum, qmsType, loginName);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List queryQuestionLeader2(String examPaperNum, String groupNum, String userNum, String type) {
        return this.gd.queryQuestionLeader2(examPaperNum, groupNum, userNum, type);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List queryQuestionLeader2ByGroup(String examPaperNum, String groupNum, String schoolGroupNum) {
        return this.gd.queryQuestionLeader2ByGroup(examPaperNum, groupNum, schoolGroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer createGroupfz(Integer examPaperNum, String questionNum, String questionName, String loginName, String groupType) {
        return this.gd.createGroupfz(examPaperNum, questionNum, questionName, loginName, groupType);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer deleteGroupfz(Integer examPaperNum, String questionNum, String type, String groupType, String shuang) {
        return this.gd.deleteGroupfz(examPaperNum, questionNum, type, groupType, shuang);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String queryMoveOne(Integer examPaperNum, String groupNum, String userNum) {
        return this.gd.queryMoveOne(examPaperNum, groupNum, userNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getGroupCount(String examPaperNum, String tab) {
        return this.gd.getGroupCount(examPaperNum, tab);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getGroupNumber(String examPaperNum, String groupNum) {
        return this.gd.getGroupNumber(examPaperNum, groupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getTaskNumber(String examPaperNum, String groupNum, String userId) {
        return this.gd.getTaskNumber(examPaperNum, groupNum, userId);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer updateGroupfz(Integer examPaperNum, String questionNum, String questionName) {
        return this.gd.updateGroupfz(examPaperNum, questionNum, questionName);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void deletetask(Integer examPaperNum, String questionNum, String userNum, String type, String shuang) {
        this.gd.deletetask(examPaperNum, questionNum, userNum, type, shuang);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String gettaskCount(Integer examPaperNum, String groupNum, String userNum) {
        return this.gd.gettaskCount(examPaperNum, groupNum, userNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer deleteGroupfz_user(Integer examPaperNum, String questionNum, String type, String userNum) {
        return this.gd.deleteGroupfz_user(examPaperNum, questionNum, type, userNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getkgQuestionNum(String examPaperNum) {
        return this.gd.getkgQuestionNum(examPaperNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getGCount(String examPaperNum) {
        return this.gd.getGCount(examPaperNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getTeacher(String teacherName) {
        return this.gd.getTeacher(teacherName);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getSubjectCount(String exam, String grade) {
        return this.gd.getSubjectCount(exam, grade);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateGroupName(String examPaperNum, String groupNum, String groupName) {
        this.gd.updateGroupName(examPaperNum, groupNum, groupName);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String queryUpdateGroupName(String examPaperNum, String groupNum) {
        return this.gd.queryUpdateGroupName(examPaperNum, groupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getfpzt(String exam, String examPaperNum) {
        return this.gd.getfpzt(exam, examPaperNum);
    }

    public Integer createGroupfz_mark_setting(Integer examPaperNum, String questionNum, String loginName) {
        Questiongroup_mark_setting qms = new Questiongroup_mark_setting();
        qms.setId(GUID.getGUIDStr());
        qms.setExampaperNum(examPaperNum);
        qms.setGroupNum(questionNum);
        qms.setMakType("0");
        qms.setType("1");
        qms.setInsertUser(loginName);
        qms.setInsertDate(DateUtil.getCurrentDay());
        return Integer.valueOf(this.dao.save(qms));
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getexampaperList(String examNum) {
        return this.gd.getexampaperList(examNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getsubjetTeacher(Integer subjectNum) {
        return this.gd.getsubjetTeacher(subjectNum);
    }

    public String selectuserRole(String teacherNum) {
        Map args = StreamMap.create().put("teacherNum", (Object) teacherNum);
        return this.dao._queryStr("select count(1) from userrole where  userrole.roleNum='4' and userNum={teacherNum} ", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getGroupNumZ(String exampaperNum) {
        return this.gd.getGroupNumZ(exampaperNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List bfExportQuestionUser(String exam, String grade) {
        return this.gd.bfExportQuestionUser(exam, grade);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getMaxTitle(Integer exa, Integer gradeNum, String type) {
        return this.gd.getMaxTitle(exa, gradeNum, type);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<QuestionGroup_question> getQroupTeacherList(String examNum, String gradeNum, String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryBeanList("select g.gradeName ext1,IFNULL(s2.subjectName,s.subjectName) ext2,qr.groupName,t.teacherNum ext3,sch.schoolName ext4,t.teacherName ext5,qu.userType ext8,q.num ext6,qu.note ext7,IFNULL(sd.choosename,_d.choosename) ext9,IFNULL( sd_d.questionNum, _dd.questionNum ) questionName,subd.orderNum ext10   ,if(IFNULL(sd.choosename,_d.choosename)<>'s' and position(',' in group_concat(qu.groupNum))=0,qr.groupName,null) ext11    FROM ( SELECT q.exampaperNum,q.groupNum,q.userNum,if(q.userType=1,'Y','') userType,'' note from questiongroup_user q LEFT JOIN exampaper e on q.exampaperNum=e.exampaperNum where e.examNum={examNum} and e.gradeNum={gradeNum} and e.subjectNum={subjectNum} and q.userType<>2  \t\tUNION   \t\tSELECT e.exampaperNum,'' groupNum,ex.userNum,'' userType,ex.note from extragroupuser ex       LEFT JOIN exampaper e on ex.examNum=e.examNum and ex.gradeNum=e.gradeNum and ex.subjectNum=e.subjectNum \t\tLEFT JOIN questiongroup_user q on e.examPaperNum=q.examPaperNum and ex.userNum=q.userNum \t\twhere ex.examNum={examNum} and ex.gradeNum={gradeNum} and ex.subjectNum={subjectNum} and q.userType<>2 and q.id is NULL ) qu    INNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum   LEFT JOIN basegrade g on g.gradeNum=e.gradeNum   LEFT JOIN `subject` s on e.subjectNum=s.subjectNum   LEFT JOIN questiongroup qr on qu.groupNum=qr.groupNum   LEFT JOIN user u on qu.userNum=u.id   LEFT JOIN teacher t on u.userid=t.id   LEFT JOIN school sch on t.schoolNum=sch.id   LEFT JOIN quota q on qu.groupNum=q.groupNum   and qu.userNum=q.insertUser  LEFT JOIN subdefine subd ON subd.id = qr.groupNum LEFT JOIN define sd ON sd.id = subd.pid LEFT JOIN define sd_d ON sd_d.id = sd.choosename LEFT JOIN define _d ON _d.id = qr.groupNum LEFT JOIN define _dd ON _dd.id = _d.choosename LEFT JOIN exampaper ep3 ON ep3.examPaperNum=IFNULL(subd.category,_d.category) LEFT JOIN `subject` s2 ON ep3.subjectNum = s2.subjectNum where e.examNum={examNum} and e.gradeNum={gradeNum} and e.subjectNum={subjectNum} GROUP BY IF(ext9 = 's',qr.groupNum,CONCAT(ext9,IFNULL( subd.orderNum, '' ))) ,ext3 ORDER BY IFNULL( _d.orderNum, sd.orderNum ), subd.orderNum,convert(sch.schoolName using gbk)", QuestionGroup_question.class, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<QuestionGroup_question> getQroupSchoolList(String examNum, String gradeNum, String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryBeanList("SELECT bg.gradeName ext1,sj.subjectName ext2,sl.schoolName ext3,GROUP_CONCAT(qp.groupNum,'-',sq.num SEPARATOR ':') ext4 from schoolquota sq  LEFT JOIN questiongroup qp on sq.groupNum=qp.groupNum  LEFT JOIN exampaper e on sq.exampaperNum=e.examPaperNum  INNER JOIN basegrade bg on e.gradeNum=bg.gradeNum  INNER JOIN subject sj on e.subjectNum=sj.subjectNum  INNER JOIN school sl on sq.schoolNum=sl.id  where e.examNum={examNum} and e.gradeNum={gradeNum} and e.subjectNum={subjectNum}  GROUP BY sl.schoolName ", QuestionGroup_question.class, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Map getQuestionGroupList(String examNum, String gradeNum, String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        return this.dao._queryOrderMap("select groupNum,groupName from questiongroup qp LEFT JOIN exampaper e on qp.exampaperNum=e.examPaperNum where e.examNum={examNum} and e.gradeNum={gradeNum}  and e.subjectNum={subjectNum} ", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List getGradeM(String gradeNum) {
        return this.gd.getGradeM(gradeNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void importG(File Excel, String username, String filename) {
        if (filename == null) {
            throwException("请选择Excel表之后再导入...");
        }
        int ind = filename.lastIndexOf(".");
        boolean flag1 = "xls".equalsIgnoreCase(filename.substring(ind + 1));
        boolean flag2 = "xlsx".equalsIgnoreCase(filename.substring(ind + 1));
        XSSFWorkbook xb = null;
        InputStream input = null;
        new ArrayList();
        new ArrayList();
        try {
            input = new FileInputStream(Excel);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        if (flag2) {
            try {
                xb = new XSSFWorkbook(input);
            } catch (Exception e) {
            }
            XSSFSheet xsheet = xb.getSheetAt(0);
            XSSFRow xRow = xsheet.getRow(0);
            xsheet.getPhysicalNumberOfRows();
            xRow.getPhysicalNumberOfCells();
        }
        if (flag1) {
            POIFSFileSystem fs = null;
            try {
                fs = new POIFSFileSystem(input);
            } catch (IOException e12) {
                e12.printStackTrace();
            }
            HSSFWorkbook wb = null;
            try {
                wb = new HSSFWorkbook(fs);
            } catch (IOException e13) {
                e13.printStackTrace();
            }
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow hssfrow = sheet.getRow(0);
            try {
                new XSSFWorkbook(input);
            } catch (Exception e2) {
            }
            int rows = sheet.getPhysicalNumberOfRows();
            hssfrow.getPhysicalNumberOfCells();
            for (int i = 1; i < rows; i++) {
                sheet.getRow(i);
                sheet.getRow(i).getRowNum();
            }
        }
        try {
            input.close();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    public static String getXCellType(XSSFCell cell) {
        String o = "";
        if (cell != null) {
            CellType cellType = cell.getCellType();
            switch (1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
                case 1:
                    o = "";
                    break;
                case 2:
                    break;
                case Const.Pic_Score /* 3 */:
                    o = "Bad value!";
                    break;
                case 4:
                    cell.setCellType(CellType.STRING);
                    o = cell.getStringCellValue().trim().replaceAll("\u3000", "").replace(" ", "");
                    break;
                case Const.Pic_AbPage /* 5 */:
                    o = cell.getCellFormula();
                    break;
                default:
                    o = cell.getStringCellValue().trim().replaceAll("\u3000", "").replace(" ", "");
                    break;
            }
        }
        return o;
    }

    public static String getHCellType(HSSFCell cell) {
        String o = "";
        if (cell != null) {
            CellType cellType = cell.getCellType();
            switch (1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
                case 1:
                    o = "";
                    break;
                case 2:
                    break;
                case Const.Pic_Score /* 3 */:
                    o = "Bad value!";
                    break;
                case 4:
                    cell.setCellType(CellType.STRING);
                    o = cell.getStringCellValue().trim().replaceAll("\u3000", "").replace(" ", "");
                    break;
                case Const.Pic_AbPage /* 5 */:
                    o = cell.getCellFormula();
                    break;
                default:
                    o = cell.getStringCellValue().trim().replaceAll("\u3000", "").replace(" ", "");
                    break;
            }
        }
        return o;
    }

    public static String getCellType(Cell cell) {
        String o = "";
        if (cell != null) {
            CellType cellType = cell.getCellType();
            switch (1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
                case 1:
                    o = "";
                    break;
                case 2:
                    break;
                case Const.Pic_Score /* 3 */:
                    o = "Bad value!";
                    break;
                case 4:
                    cell.setCellType(CellType.STRING);
                    o = cell.getStringCellValue().trim().replaceAll("\u3000", "").replace(" ", "");
                    break;
                case Const.Pic_AbPage /* 5 */:
                    o = cell.getCellFormula();
                    break;
                default:
                    o = cell.getStringCellValue().trim().replaceAll("\u3000", "").replace(" ", "");
                    break;
            }
        }
        return o;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String checkTaskHasCount(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        Object count = this.dao._queryObject("select id from task where examPaperNum={examPaperNum} and status='T' limit 1", args);
        if (count == null || "".equals(count)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Schoolgroup> assignRecordCheck(String examPaperNum) {
        String sql;
        String enforce = systemService.fenzu(examPaperNum);
        if ("0".equals(enforce)) {
            sql = "SELECT q1.groupNum ext1,q.groupName ext2,'-1' schoolGroupNum,'' schoolGroupName,q3.sumcount ext3,qms.makType ext4 from(  SELECT qu.groupNum,count(1) count from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id  where qu.exampaperNum={examPaperNum}  GROUP BY qu.groupNum )q1 INNER JOIN(  SELECT qu.groupNum,count(1) count1 from quota qu INNER JOIN user u on qu.insertUser=u.id   where qu.exampaperNum={examPaperNum} GROUP BY qu.groupNum )q2 on q1.groupNum=q2.groupNum and q1.count=q2.count1 INNER JOIN(  SELECT qu.groupNum,SUM(qu.num) sumcount from quota qu INNER JOIN user u on qu.insertUser=u.id   where qu.exampaperNum={examPaperNum}  GROUP BY qu.groupNum )q3 on q1.groupNum=q3.groupNum LEFT JOIN questiongroup q on q1.groupNum=q.groupNum LEFT JOIN questiongroup_mark_setting qms on q.groupNum=qms.groupNum";
        } else {
            sql = "SELECT q1.groupNum ext1,q.groupName ext2,q1.schoolGroupNum,q1.schoolGroupName,q3.sumcount ext3,qms.makType ext4 from(  SELECT qu.groupNum,IFNULL(slg.schoolGroupNum,'-2') schoolGroupNum,IFNULL(slg.schoolGroupName,'未分组') schoolGroupName,count(1) count from questiongroup_user qu INNER JOIN user u on qu.userNum=u.id left JOIN schoolgroup slg on u.schoolNum=slg.schoolNum  where qu.exampaperNum={examPaperNum}  GROUP BY qu.groupNum,slg.schoolGroupNum )q1 INNER JOIN(  SELECT qu.groupNum,IFNULL(slg.schoolGroupNum,'-2') schoolGroupNum,count(1) count1 from quota qu INNER JOIN user u on qu.insertUser=u.id left JOIN schoolgroup slg on u.schoolNum=slg.schoolNum  where qu.exampaperNum={examPaperNum} GROUP BY qu.groupNum,slg.schoolGroupNum )q2 on q1.groupNum=q2.groupNum and q1.schoolGroupNum=q2.schoolGroupNum and q1.count=q2.count1 INNER JOIN(  SELECT qu.groupNum,IFNULL(slg.schoolGroupNum,'-2') schoolGroupNum,SUM(qu.num) sumcount from quota qu INNER JOIN user u on qu.insertUser=u.id left JOIN schoolgroup slg on u.schoolNum=slg.schoolNum  where qu.exampaperNum={examPaperNum}  GROUP BY qu.groupNum,slg.schoolGroupNum )q3 on q1.groupNum=q3.groupNum and q1.schoolGroupNum=q3.schoolGroupNum LEFT JOIN questiongroup q on q1.groupNum=q.groupNum LEFT JOIN questiongroup_mark_setting qms on q.groupNum=qms.groupNum ";
        }
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._queryBeanList(sql, Schoolgroup.class, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void deleteDistribute(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        String roleNum = this.dao._queryStr("select roleNum from role where examPaperNum ={examPaperNum} and type = '4'", args);
        args.put("roleNum", roleNum);
        List<?> _queryBeanList = this.dao._queryBeanList("select userNum from userrole where roleNum={roleNum} ", Userrole.class, args);
        List<Map> maps = new ArrayList<>();
        for (int i = 0; i < _queryBeanList.size(); i++) {
            String userNum = ((Userrole) _queryBeanList.get(i)).getUserNum();
            Map argss = StreamMap.create().put("userNum", (Object) userNum);
            maps.add(argss);
        }
        this.dao._batchUpdate("delete from userrole where userNum={userNum} and roleNum=-4", maps);
        List<String> sqls = new ArrayList<>();
        sqls.add("delete from questiongroup_user where examPaperNum={examPaperNum} and userType!='2' ");
        sqls.add("delete from quota where examPaperNum={examPaperNum} ");
        sqls.add("update task set insertuser='-1',porder=0,fenfaDate=0 where examPaperNum={examPaperNum} and insertUser<>'-1'");
        long start = new Date().getTime();
        this.dao._batchExecute(sqls, args);
        this.log.info("【一键删除阅卷任务分配】" + (System.currentTimeMillis() - start));
    }

    public static String getCellFormatValue(Cell cell) {
        String cellValue = "";
        if (cell != null) {
            CellType cellType = cell.getCellType();
            switch (1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
                case 1:
                    cellValue = "";
                    break;
                case 2:
                    break;
                case Const.Pic_Score /* 3 */:
                    cellValue = "Bad value!";
                    break;
                case 4:
                    cell.setCellType(CellType.STRING);
                    cellValue = cell.getStringCellValue();
                    break;
                case Const.Pic_AbPage /* 5 */:
                    try {
                        cellValue = String.valueOf(cell.getNumericCellValue());
                        break;
                    } catch (IllegalStateException e) {
                        cellValue = String.valueOf(cell.getRichStringCellValue());
                        break;
                    }
                default:
                    cellValue = cell.getStringCellValue();
                    break;
            }
            if (!"".equals(cellValue) && cellValue.indexOf(" ") != -1) {
                cellValue = replaceBlank(cellValue);
            }
        }
        return cellValue;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*| |\r| ");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public Cell getExcelCell(Sheet sheet, int hang, int lie) {
        Cell hf = sheet.getRow(hang).getCell(lie);
        return hf;
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x011c, code lost:
    
        r22 = "当前文件的 第1行 第" + (1 + r28) + "列 的题号不存在，请检查！";
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x017b, code lost:
    
        r22 = "当前文件的 第" + (1 + r26) + "行 第" + (1 + r28) + "列 的年级不存在，请检查！";
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x029d, code lost:
    
        r22 = "当前文件的 第" + (1 + r26) + "行 第" + (1 + r28) + "列 的学校不存在，请检查！";
     */
    @Override // com.dmj.service.questionGroup.QuestionGroupService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String uploadQuSchool(java.lang.String r6, java.lang.String r7, java.lang.String r8, java.lang.String r9, java.io.File r10, java.lang.String r11, java.lang.String r12) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 924
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dmj.serviceimpl.questionGroup.QuestionGroupImpl.uploadQuSchool(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.io.File, java.lang.String, java.lang.String):java.lang.String");
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x0264, code lost:
    
        r29 = "当前文件的 第" + (r30 + 1) + "行 第3列 的题号" + r0 + "不对，请检查！";
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0545, code lost:
    
        r29 = "当前文件的 第" + (r30 + 1) + "行 教师姓名加学校与教师编号不符，请检查！";
     */
    @Override // com.dmj.service.questionGroup.QuestionGroupService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String uploadQuUser(java.lang.String r6, java.lang.String r7, java.lang.String r8, java.lang.String r9, java.io.File r10, java.lang.String r11, java.lang.String r12) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 3120
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dmj.serviceimpl.questionGroup.QuestionGroupImpl.uploadQuUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.io.File, java.lang.String, java.lang.String):java.lang.String");
    }

    public String uploadQuUser1(String exam, String subject, String grade, String uid, File file, String success, String filename) throws FileNotFoundException {
        String upload_hint = "";
        int i = 0;
        DateUtil.getCurrentTime();
        String gradeExamNum = examService.getExampaperNumBySubjectAndGradeAndExam(exam, subject, grade);
        if (null == gradeExamNum) {
            return "0";
        }
        if (null == file) {
            return "当前文件为null！请重试！";
        }
        int ind = filename.lastIndexOf(".");
        boolean flag1 = "xls".equalsIgnoreCase(filename.substring(ind + 1));
        boolean flag2 = "xlsx".equalsIgnoreCase(filename.substring(ind + 1));
        XSSFWorkbook xb = null;
        new ArrayList();
        new ArrayList();
        InputStream input = new FileInputStream(file);
        if (flag2) {
            try {
                xb = new XSSFWorkbook(input);
            } catch (Exception e) {
            }
            XSSFSheet xsheet = xb.getSheetAt(0);
            XSSFRow xRow = xsheet.getRow(0);
            int rows = xsheet.getPhysicalNumberOfRows();
            int cols = xRow.getPhysicalNumberOfCells();
            List questionId = new ArrayList();
            for (int j = 5; j < cols; j++) {
                XSSFCell cell = xsheet.getRow(0).getCell(j);
                if (cell.getCellType() != CellType.STRING) {
                    throwException("当前文件的 首行 第" + (1 + j) + "列 的题号不是文本格式，请检查！");
                }
                String questionNum = xsheet.getRow(0).getCell(j).toString();
                Map args = StreamMap.create().put("gradeExamNum", (Object) gradeExamNum).put("questionNum", (Object) questionNum);
                new ArrayList();
                List<?> _queryBeanList = this.dao._queryBeanList("select id,questionNum from define where examPaperNum = {gradeExamNum}  and questionNum = {questionNum}UNION select id,questionNum from subdefine where examPaperNum = {gradeExamNum} and questionNum = {questionNum} ", Define.class, args);
                if (null != _queryBeanList && _queryBeanList.size() > 0) {
                    Iterator<?> it = _queryBeanList.iterator();
                    while (it.hasNext()) {
                        Define define = (Define) it.next();
                        questionId.add(define.getId());
                    }
                } else {
                    throwException("当前文件的 第" + (0 + 1) + "行 第" + (j + 1) + "列 的题号不对，请检查！");
                }
            }
            if (questionId.size() == 0) {
                throwException("当前科目 没有制作双向细目表 或者 选择的excel文件有误(并不是对应的本考试本科目), 请先制作双向细目表或者重新选择正确的文件再导入。");
            }
            String roleNum = "";
            Map args2 = new HashMap();
            args2.put("exam", exam);
            args2.put("gradeExamNum", gradeExamNum);
            try {
                roleNum = this.dao._queryStr("select roleNum from role where examNum = {exam}  and examPaperNum = {gradeExamNum} and type = '4' ", args2);
                deleteQuestionUser(roleNum, gradeExamNum);
            } catch (Exception e2) {
                throwException("没有查到阅卷员，请确定进行了人员设置！");
            }
            for (int i2 = 1; i2 < rows; i2++) {
                Map args22 = new HashMap();
                args22.put("subjectName", xsheet.getRow(i2).getCell(0).toString());
                String subjectNum = this.dao._queryStr("select subjectNum from subject where subjectName ={subjectName} ", args22);
                if (!subjectNum.equals(subject)) {
                    throwException("当前文件的 第" + (i2 + 1) + "行 第1列 的学科与界面学科不匹配，请检查！");
                }
                args22.put("teacherNum", xsheet.getRow(i2).getCell(1).toString());
                args22.put("teacherName", xsheet.getRow(i2).getCell(2).toString());
                String teacherId = this.dao._queryStr("select id from teacher where teacherNum ={teacherNum} and teacherName={teacherName} ", args22);
                if (teacherId == null || teacherId.equals("") || teacherId.equals("null")) {
                    throwException("当前文件的 第" + (i2 + 1) + "行 的教师工号与教师姓名不匹配，请检查！");
                }
                args22.put("gradeName", xsheet.getRow(i2).getCell(4).toString());
                String gradeNum = this.dao._queryStr("select gradeNum from basegrade where gradeName = {gradeName} ", args22);
                if (!gradeNum.equals(grade)) {
                    throwException("当前文件的 第" + (i2 + 1) + "行 第5列 的年级与界面年级不匹配，请检查！");
                }
            }
            i = 1;
            while (i < rows) {
                Map args3 = new HashMap();
                args3.put("teacherNum", xsheet.getRow(i).getCell(1).toString());
                args3.put("teacherName", xsheet.getRow(i).getCell(2).toString());
                String userNum = this.dao._queryStr("select id from user where userid = (select id from teacher where teacherNum = {teacherNum} and teacherName ={teacherName} )", args3);
                Userrole ur1 = new Userrole();
                ur1.setUserNum(userNum);
                ur1.setRoleNum(roleNum);
                ur1.setInsertUser(uid);
                ur1.setInsertDate(DateUtil.getCurrentDay());
                ur1.setIsDelete("F");
                this.dao.save(ur1);
                for (int j2 = 5; j2 < cols; j2++) {
                    Map args4 = new HashMap();
                    if (!xsheet.getRow(i).getCell(j2).toString().equals("")) {
                        args4.put("id", GUID.getGUIDStr());
                        args4.put("gradeExamNum", gradeExamNum);
                        args4.put("groupNum", questionId.get(j2 - 5));
                        args4.put("userNum", userNum);
                        args4.put("uid", uid);
                        args4.put("insertDate", DateUtil.getCurrentDay());
                        this.dao._execute("insert into questiongroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate,isFinished) values ({id},{gradeExamNum},{groupNum},'0',{userNum},{uid},{insertDate},0)", args4);
                        if (!xsheet.getRow(i).getCell(j2).toString().equals("Y")) {
                            args4.put("num", xsheet.getRow(i).getCell(j2).toString());
                            this.dao._execute("insert into quota (exampaperNum,groupNum,num,insertUser,insertDate) values ({gradeExamNum} ,{groupNum} ,{num} ,{userNum} ,{insertDate})", args4);
                        }
                    }
                }
                i++;
            }
            upload_hint = "上传题组用户信息成功";
        }
        if (flag1) {
            POIFSFileSystem fs = null;
            try {
                fs = new POIFSFileSystem(input);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            HSSFWorkbook wb = null;
            try {
                wb = new HSSFWorkbook(fs);
            } catch (IOException e12) {
                e12.printStackTrace();
            }
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow hssfrow = sheet.getRow(0);
            int rows2 = sheet.getPhysicalNumberOfRows();
            int cols2 = hssfrow.getPhysicalNumberOfCells();
            List questionId2 = new ArrayList();
            for (int j3 = 5; j3 < cols2; j3++) {
                HSSFCell cell2 = sheet.getRow(0).getCell(j3);
                if (cell2.getCellType() != CellType.STRING) {
                    throwException("当前文件的 首行 第" + (1 + j3) + "列 的题号不是文本格式，请检查！");
                }
                String questionNum2 = sheet.getRow(0).getCell(j3).toString();
                Map args5 = new HashMap();
                args5.put("gradeExamNum", gradeExamNum);
                args5.put("questionNum", questionNum2);
                new ArrayList();
                List<?> _queryBeanList2 = this.dao._queryBeanList("select id,questionNum from define where examPaperNum ={gradeExamNum} and questionNum = {questionNum} UNION select id,questionNum from subdefine where examPaperNum = {gradeExamNum}  and questionNum = {questionNum} ", Define.class, args5);
                if (null != _queryBeanList2 && _queryBeanList2.size() > 0) {
                    Iterator<?> it2 = _queryBeanList2.iterator();
                    while (it2.hasNext()) {
                        Define define2 = (Define) it2.next();
                        questionId2.add(define2.getId());
                    }
                } else {
                    throwException("当前文件的 第" + (i + 1) + "行 第" + (j3 + 1) + "列 的题号不对，请检查！");
                }
            }
            if (questionId2.size() == 0) {
                throwException("当前科目 没有制作双向细目表 或者 选择的excel文件有误(并不是对应的本考试本科目), 请先制作双向细目表或者重新选择正确的文件再导入。");
            }
            String roleNum2 = "";
            Map args6 = new HashMap();
            args6.put("exam", exam);
            args6.put("gradeExamNum", gradeExamNum);
            try {
                roleNum2 = this.dao._queryStr("select roleNum from role where examNum = {exam} and examPaperNum = {gradeExamNum}  and type = '4' ", args6);
                deleteQuestionUser(roleNum2, gradeExamNum);
            } catch (Exception e3) {
                throwException("没有查到阅卷员，请确定进行了人员设置！");
            }
            for (int i3 = 1; i3 < rows2; i3++) {
                Map args23 = new HashMap();
                args23.put("subjectName", sheet.getRow(i3).getCell(0).toString());
                String subjectNum2 = this.dao._queryStr("select subjectNum from subject where subjectName = {subjectName}", args23);
                if (!subjectNum2.equals(subject)) {
                    throwException("当前文件的 第" + (i3 + 1) + "行 第1列 的学科与界面学科不匹配，请检查！");
                }
                args23.put("teacherNum", sheet.getRow(i3).getCell(1).toString());
                args23.put("teacherName", sheet.getRow(i3).getCell(2).toString());
                String teacherId2 = this.dao._queryStr("select id from teacher where teacherNum = {teacherNum}  and teacherName={teacherName} ", args23);
                if (teacherId2 == null || teacherId2.equals("") || teacherId2.equals("null")) {
                    throwException("当前文件的 第" + (i3 + 1) + "行 的教师工号与教师姓名不匹配，请检查！");
                }
                args23.put("gradeName", sheet.getRow(i3).getCell(4).toString());
                String gradeNum2 = this.dao._queryStr("select gradeNum from basegrade where gradeName ={gradeName} ", args23);
                if (!gradeNum2.equals(grade)) {
                    throwException("当前文件的 第" + (i3 + 1) + "行 第5列 的年级与界面年级不匹配，请检查！");
                }
            }
            for (int i4 = 1; i4 < rows2; i4++) {
                Map args32 = new HashMap();
                args32.put("teacherNum", sheet.getRow(i4).getCell(1).toString());
                args32.put("teacherName", sheet.getRow(i4).getCell(2).toString());
                String userNum2 = this.dao._queryStr("select id from user where userid = (select id from teacher where teacherNum = {teacherNum} and teacherName ={teacherName} )", args32);
                Userrole ur12 = new Userrole();
                ur12.setUserNum(userNum2);
                ur12.setRoleNum(roleNum2);
                ur12.setInsertUser(uid);
                ur12.setInsertDate(DateUtil.getCurrentDay());
                ur12.setIsDelete("F");
                this.dao.save(ur12);
                for (int j4 = 5; j4 < cols2; j4++) {
                    Map args42 = new HashMap();
                    if (!sheet.getRow(i4).getCell(j4).toString().equals("")) {
                        args42.put("id", GUID.getGUIDStr());
                        args42.put("gradeExamNum", gradeExamNum);
                        args42.put("groupNum", questionId2.get(j4 - 5));
                        args42.put("userNum", userNum2);
                        args42.put("uid", uid);
                        args42.put("insertDate", DateUtil.getCurrentDay());
                        this.dao._execute("insert into questiongroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate,isFinished) values ({id},{gradeExamNum} ,{groupNum} ,'0',{userNum} ,{uid} ,{insertDate},0)", args42);
                        if (!sheet.getRow(i4).getCell(j4).toString().equals("Y")) {
                            args42.put("num", sheet.getRow(i4).getCell(j4).toString());
                            this.dao._execute("insert into quota (exampaperNum,groupNum,num,insertUser,insertDate) values ({gradeExamNum} ,{groupNum} ,{num} ,{userNum} ,{insertDate})", args42);
                        }
                    }
                }
            }
            upload_hint = "上传题组用户信息成功";
        }
        return upload_hint;
    }

    private void deleteQuestionUser(String roleNum, String gradeExamNum) {
        Map args = StreamMap.create().put("roleNum", (Object) roleNum).put("gradeExamNum", (Object) gradeExamNum);
        this.dao._execute("delete from userrole where roleNum = {roleNum} ", args);
        this.dao._execute("delete from questiongroup_user where exampaperNum ={gradeExamNum}  ", args);
        this.dao._execute("delete from quota where exampaperNum ={gradeExamNum} ", args);
    }

    private void throwException(String e) {
        throw new SubException(e);
    }

    private void throwException(String e, jxl.Cell cell) {
        throwException("第 " + (cell.getRow() + 2) + " 行 " + (cell.getColumn() + 1) + "列" + e);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updatequestiontotalnum(Integer exampaperNum, String groupNum, String questionNum, String page, String type) {
        this.gd.updatequestiontotalnum(exampaperNum, groupNum, questionNum, page, type);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String queryquestionNameone(Integer examPaperNum, String groupNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("groupNum", (Object) groupNum);
        return this.dao._queryStr("select a.questionNum from ((select id,questionnum from define where exampaperNum={examPaperNum}  and id={groupNum} ) union all (select id,questionnum  from subdefine WHERE exampaperNum={examPaperNum}  and id={groupNum}))a", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String querySchoolcount() {
        return this.dao.queryStr("select count(1) from school", null);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void deleteteacherimport(String schoolNum) {
        String schoolNumStr = "";
        String schoolNumStr2 = "";
        String schoolNumStr3 = "";
        if (!schoolNum.equals("-1")) {
            schoolNumStr = " WHERE schoolNum={schoolNum} ";
            schoolNumStr2 = " and u.schoolNum={schoolNum} ";
            schoolNumStr3 = " AND schoolNum={schoolNum}";
        }
        String deleteteacher = "DELETE FROM teacher " + schoolNumStr;
        String deleteuser = "delete from user WHERE usertype='1' " + schoolNumStr3;
        String sql3 = "DELETE FROM userposition " + schoolNumStr;
        String deleteuserrole = "DELETE ur FROM  userrole ur left JOIN (select id,schoolNum FROM user where usertype='1') u ON u.id=ur.userNum  where u.id=ur.userNum " + schoolNumStr2;
        String updatetask = "Update task t left JOIN (select id,schoolNum FROM user where usertype='1') u ON u.id=t.insertUser  set t.insertUser='-1',t.porder=0,t.fenfaDate=0   where t.status='F' " + schoolNumStr2;
        String groupuser = "DELETE qu FROM  QuestionGroup_user qu left JOIN (select id,schoolNum FROM user where usertype='1') u ON u.id=qu.userNum  where u.id=qu.userNum " + schoolNumStr2;
        Map args = StreamMap.create().put(Const.EXPORTREPORT_schoolNum, (Object) schoolNum);
        this.dao._execute(deleteuserrole, args);
        this.dao._execute(groupuser, args);
        this.dao._execute(updatetask, args);
        this.dao._execute(deleteteacher, args);
        this.dao._execute(deleteuser, args);
        this.dao._execute(sql3, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void deletesessiontimetask(String userNum) {
        Map args = StreamMap.create().put("userNum", (Object) userNum);
        List queryidlist = this.dao._queryColList("select id from task  where insertUser={userNum} and status='F' and isException != 'C' ", args);
        List<Map<String, Object>> argList = new ArrayList<>();
        for (int i = 0; i < queryidlist.size(); i++) {
            int finalI = i;
            Map args2 = StreamMap.create().put("id", queryidlist.get(finalI));
            argList.add(args2);
        }
        this.dao._batchExecute(" update task set insertUser='-1',status='F',isException='F',porder=0,fenfaDate=0 where id={id} ", argList);
        this.gd.addLog("", "session过期调用回收task", userNum, "");
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void xxthb(Integer examPaperNum, String groupNum, String questionName, String loginName, String groupType, String exam, String subject, String grade) {
        this.gd.xxthb(examPaperNum, groupNum, questionName, loginName, groupType);
        updateTempleteDisabled(exam, grade, subject, 1);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void qxhb(Integer examPaperNum, String groupNum, String questionName, String loginName, String groupType, String exam, String subject, String grade) {
        this.gd.qxhb(examPaperNum, groupNum, questionName, loginName, groupType);
        updateTempleteDisabled(exam, grade, subject, 1);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void daoruxxt(Integer examPaperNum, String groupNum, String questionNum, String questionName, String loginName, String groupType) {
        this.gd.daoruxxt(examPaperNum, groupNum, questionNum, questionName, loginName, groupType);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void recoverytask(String examPaperNum, String groupNum, String questionNum, String userNum) {
        this.gd.recoverytask(examPaperNum, groupNum, questionNum, userNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void resetPanfenByTi(String examPaperNum, String groupNum) {
        this.gd.resetPanfenByTi(examPaperNum, groupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void szpanfencount(String exampaperNum, String groupNum, String num, String insertUser, String insertDate) {
        String[] groupNumArr = groupNum.split("_");
        for (String groupNum2 : groupNumArr) {
            Map args = new HashMap();
            args.put("groupNum", groupNum2);
            args.put("insertUser", insertUser);
            if (num.length() == 0) {
                new ArrayList();
                List numList = this.dao._queryArrayList("select * from quota where groupNum = {groupNum} and insertUser = {insertUser} ", args);
                if (numList.size() != 0) {
                    this.dao._execute("delete from quota where groupNum = {groupNum} and insertUser = {insertUser} ", args);
                }
            } else {
                args.put("exampaperNum", exampaperNum);
                args.put("num", num);
                args.put("insertDate", insertDate);
                this.dao._execute("insert into   quota (exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{groupNum},{num},{insertUser},{insertDate})  ON DUPLICATE KEY UPDATE num={num}", args);
            }
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public int szpanfencount1(String exampaperNum, String groupNum, String num, String makType, String insertUser, String insertDate) {
        int a = 1;
        if ("1".equals(makType)) {
            Map args = new HashMap();
            args.put("groupNum", groupNum);
            args.put("insertUser", insertUser);
            String count = this.dao._queryStr("select * from quota where groupNum = {groupNum} and insertUser = {insertUser} ", args);
            if ("null".equals(count) || null == count || "".equals(count)) {
                args.put("exampaperNum", exampaperNum);
                args.put("num", num);
                args.put("insertDate", insertDate);
                a = this.dao._execute("insert into   quota (exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{groupNum},{num},{insertUser},{insertDate})", args);
            }
        }
        return a;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<QuestionGroup> getChooseQueandteacher(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._queryBeanList("SELECT q.groupNum,GROUP_CONCAT(q.userNum) ext1 from( \tSELECT * from questiongroup_user where exampaperNum={examPaperNum} \t)q INNER JOIN define d on q.examPaperNum=d.examPaperNum and q.groupNum=d.id \twhere LENGTH(d.choosename)>1 GROUP BY  q.groupNum", QuestionGroup.class, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateChooseQuota(String exampaperNum, String groupNum, String user, String num) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) groupNum).put("num", (Object) num).put("user", (Object) user);
        this.dao._execute("insert into   quota (exampaperNum,groupNum,num,insertUser,insertDate) values({exampaperNum},{groupNum},{num},{user},now()  ON DUPLICATE KEY UPDATE num={num}   ", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void deleteChooseQuota(String exampaperNum, String groupNum, String user, String num) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) groupNum).put("user", (Object) user);
        this.dao._execute("delete from quota where exampapernum={exampaperNum} and groupNum={groupNum} and insertUser={user} ", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateUpperFloat(String examPaperNum, String groupNum, String upperFloat, String insertUser, String insertDate) {
        Map args = StreamMap.create().put("upperFloat", (Object) upperFloat).put("examPaperNum", (Object) examPaperNum).put("groupNum", (Object) groupNum);
        this.dao._execute("update questionGroup set upperFloat={upperFloat} where examPaperNum={examPaperNum} and groupNum={groupNum}  ", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List checkTotalNum(String groupNum) {
        Map args = StreamMap.create().put("groupNum", (Object) groupNum);
        return this.dao._queryArrayList("select count(1) tiCount,sum(num) manualTask from quota where groupNum = {groupNum} ", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List queryschool(String schoolNum, String type, String loginUserId) {
        Map args = new HashMap();
        String schStr = "";
        if (!"".equals(schoolNum) && schoolNum != null && !"null".equals(schoolNum)) {
            schStr = schStr + " and s.id={schoolNum} ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        String authStr = "";
        if ("authority".equals(type)) {
            authStr = authStr + "INNER JOIN(  SELECT schoolnum from user where id={loginUserId}  UNION   SELECT schoolnum from schauthormanage where userId={loginUserId} )sa on s.id=sa.schoolnum";
            args.put("loginUserId", loginUserId);
        }
        String sql = "select id,schoolName from school s " + authStr + " where 1=1 " + schStr + " ORDER BY CONVERT(s.schoolName USING gbk) asc ";
        return this.dao._queryBeanList(sql, School.class, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List queryschoolByGroup(String schoolGroupNum) {
        if ("-2".equals(schoolGroupNum)) {
            return this.dao._queryBeanList("select sch.id,sch.schoolName from school sch order by convert(sch.schoolName using gbk)", School.class, null);
        }
        String sgWStr = "-1".equals(schoolGroupNum) ? " where sg.schoolGroupNum is null " : " where sg.schoolGroupNum={schoolGroupNum} ";
        String sql = "select sch.id,sch.schoolName from school sch left join schoolgroup sg on sg.schoolNum=sch.id " + sgWStr + "order by convert(sch.schoolName using gbk)";
        Map args = new HashMap();
        args.put("schoolGroupNum", schoolGroupNum);
        return this.dao._queryBeanList(sql, School.class, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, String>> querySubject(String examPaperNum) {
        String sql;
        Map<String, String> args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        if (this.dao._queryInt("select count(1) from exampaper where pexamPaperNum={examPaperNum}", args).intValue() > 1) {
            sql = "select e.subjectNum subjectNum,sub.subjectName subjectName from exampaper e left join subject sub on e.subjectNum=sub.subjectNum where pexamPaperNum={examPaperNum} and exampaperNum!=pexampaperNum";
        } else {
            sql = "select e.subjectNum subjectNum,sub.subjectName subjectName from exampaper e left join subject sub on e.subjectNum=sub.subjectNum where pexamPaperNum={examPaperNum} ";
        }
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List querygrade() {
        return this.dao.queryBeanList("select distinct gradeNum,gradeName from grade order by gradeNum ", Grade.class);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List querysubject() {
        return this.dao.queryBeanList("select subjectNum,subjectName from subject ", Subject.class);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Extragroupuser> querynote(String examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._queryBeanList("select DISTINCT et.note from extragroupuser et   INNER JOIN exampaper e on et.examNum=e.examNum and et.subjectNum=e.subjectNum and et.gradeNum=e.gradeNum   where e.examPaperNum={examPaperNum} ", Extragroupuser.class, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateforbidden(String exampaperNum, String groupNum, String stat, String alltype) {
        this.gd.updateforbidden(exampaperNum, groupNum, stat, alltype);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, String>> updateShensuStatus(String exampaperNum, String questionNum, String stat, String alltype) {
        return this.gd.updateShensuStatus(exampaperNum, questionNum, stat, alltype);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateautoCommitforbidden(String exampaperNum, String groupNum, String stat, String alltype) {
        this.gd.updateautoCommitforbidden(exampaperNum, groupNum, stat, alltype);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateautoCommitdefault(String exampaperNum, String groupNum, String stat, String alltype) {
        this.gd.updateautoCommitdefault(exampaperNum, groupNum, stat, alltype);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updatecorrectForbid(String exampaperNum, String groupNum, String stat, String alltype) {
        this.gd.updatecorrectForbid(exampaperNum, groupNum, stat, alltype);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateYuejuanWay(String examPaperNum, String groupNum, String checkedArry) {
        this.gd.updateYuejuanWay(examPaperNum, groupNum, checkedArry);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updatejudgetype(String groupNum, String judgetype) {
        this.gd.updatejudgetype(groupNum, judgetype);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updatejudgetype2(String groupNum, String judgetype) {
        this.gd.updatejudgetype2(groupNum, judgetype);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer saomiao(String exampaperNum, String scancompleted) {
        return this.gd.saomiao(exampaperNum, scancompleted);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Object huixiansaomiao(String exampaperNum) {
        return this.gd.huixiansaomiao(exampaperNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<School> getLeftSchool(String leftInputStr, String leiceng) {
        return this.gd.getLeftSchool(leftInputStr, leiceng);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public int addGroupSchool(Schoolgroup sg) {
        return this.gd.addGroupSchool(sg);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public int delGroupSchool(Schoolgroup sg) {
        return this.gd.delGroupSchool(sg);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Schoolgroup> getSchoolGroupData() {
        return this.gd.getSchoolGroupData();
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<School> getGroupSchoolData(String schoolGroupNum, String rightInputStr, String leiceng) {
        return this.gd.getGroupSchoolData(schoolGroupNum, rightInputStr, leiceng);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public int deleteSchoolGroup(String schoolGroupNum) {
        return this.gd.deleteSchoolGroup(schoolGroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Object isExistSchGroupName(String schoolGroupName, String schoolGroupNum) {
        return this.gd.isExistSchGroupName(schoolGroupName, schoolGroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public int updateSchGroupName(String schoolGroupName, String schoolGroupNum) {
        return this.gd.updateSchGroupName(schoolGroupName, schoolGroupNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, List<Map<String, Object>>>> getYuejuan(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Object exampaperNum = this.dao._queryObject("select exampaperNum from exampaper where exampaperNum=pexampaperNum and  examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        List<Map<String, Object>> list1 = this.dao._queryMapList("select distinct count(u.id) ucount ,sg.schoolGroupNum,sg.schoolGroupName from schoolGroup sg left join user u on sg.schoolNum = u.schoolNum where u.usertype='1' and u.isDelete='F' group by sg.schoolGroupNum order by ucount", null, args);
        List<Map<String, List<Map<String, Object>>>> list = new ArrayList<>();
        if (exampaperNum != null && !exampaperNum.equals("")) {
            if (list1.size() > 0) {
                String sql = "select ex.examPaperNum,ur.userNum from exampaper ex LEFT JOIN role r on ex.examPaperNum = r.examPaperNum LEFT JOIN userrole ur on r.roleNum = ur.roleNum LEFT JOIN user u on ur.userNum = u.id and u.usertype='1' LEFT JOIN schoolGroup sg on u.schoolNum = sg.schoolNum ";
                String sql2 = sql + " where r.type='4' and ex.examNum= {examNum} and ex.gradeNum= {gradeNum} and ex.subjectNum={subjectNum} ";
                for (Map<String, Object> map : list1) {
                    String sql22 = sql2 + " and sg.schoolGroupNum={schoolGroupNum} order by rand()";
                    Map args2 = StreamMap.create().put("schoolGroupNum", map.get("schoolGroupNum"));
                    Map<String, List<Map<String, Object>>> resMap = new HashMap<>();
                    resMap.put(String.valueOf(map.get("schoolGroupName")), this.dao._queryMapList(sql22, null, args2));
                    list.add(resMap);
                }
            } else {
                String sql3 = "select ex.examPaperNum,ur.userNum from exampaper ex LEFT JOIN role r on ex.examPaperNum = r.examPaperNum LEFT JOIN userrole ur on r.roleNum = ur.roleNum LEFT JOIN user u on ur.userNum = u.id and u.usertype='1' where r.type='4' and ex.examNum= {examNum}  and ex.gradeNum= {gradeNum} and ex.subjectNum={subjectNum} order by rand()";
                Map<String, List<Map<String, Object>>> resMap2 = new HashMap<>();
                resMap2.put("-1", this.dao._queryMapList(sql3, null, args));
                list.add(resMap2);
            }
        } else if (list1.size() > 0) {
            String sql4 = "select ex.examPaperNum,ur.userNum from exampaper ex  LEFT JOIN userrole_sub ur on ex.exampaperNum = ur.exampaperNum LEFT JOIN user u on ur.userNum = u.id and u.usertype='1' LEFT JOIN schoolGroup sg on u.schoolNum = sg.schoolNum ";
            String sql5 = sql4 + "where ex.examNum= {examNum} and ex.gradeNum= {gradeNum}  and ex.subjectNum={subjectNum} ";
            for (Map<String, Object> map2 : list1) {
                String sql23 = sql5 + " and sg.schoolGroupNum={schoolGroupNum} order by rand()";
                Map args22 = StreamMap.create().put("schoolGroupNum", map2.get("schoolGroupNum"));
                Map<String, List<Map<String, Object>>> resMap3 = new HashMap<>();
                resMap3.put(String.valueOf(map2.get("schoolGroupName")), this.dao._queryMapList(sql23, null, args22));
                list.add(resMap3);
            }
        } else {
            String sql6 = "select ex.examPaperNum,ur.userNum from exampaper ex  LEFT JOIN userrole_sub ur on ex.exampaperNum = ur.exampaperNum LEFT JOIN user u on ur.userNum = u.id and u.usertype='1' where ur.userNum is not null and ex.examNum= {examNum} and ex.gradeNum= {gradeNum} and ex.subjectNum={subjectNum} order by rand()";
            Map<String, List<Map<String, Object>>> resMap4 = new HashMap<>();
            resMap4.put("-1", this.dao._queryMapList(sql6, null, args));
            list.add(resMap4);
        }
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getQuestionGroupSetList(String examNum, String gradeNum, String subjectNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        List<Map<String, Object>> list = this.dao._queryMapList("select distinct ex.exampaperNum,if(d.merge='1',qg.groupNum,qg.questionNum) groupNum,ifnull(d.questionNum,sub.questionNum) groupName,ifnull(if(qms.makType='0',en.w_count,en.w_count*2),0)w_count,if(qms.makType='0','单评','双评') makType,if(qms.makType='0','1','2.05') makTypeCount,ifnull(da.scoreTime,'')scoreTime,ifnull(da.member,'')'member',da.questionCount_per,da.type, ifnull(group_concat(da.schoolNum),'') schoolNum,ifnull(group_concat(s.schoolName),'') schoolName from questiongroup_question qg LEFT JOIN examPaper ex on ex.pexamPaperNum = qg.exampaperNum LEFT JOIN (select count(1) w_count,examNum,gradeNum,subjectNum from examinationnum GROUP BY examNum,gradeNum,subjectNum) en  on ex.examNum = en.examNum and ex.gradeNum = en.gradeNum and ex.subjectNum = en.subjectNum LEFT JOIN questiongroup_mark_setting qms on ex.pexamPaperNum = qms.exampaperNum and qg.groupNum = qms.groupNum LEFT JOIN distributeAuto da ON ex.pexamPaperNum = da.exampaperNum and qg.groupNum = da.groupNum LEFT JOIN school s ON da.schoolNum = s.id LEFT JOIN define d ON qg.groupNum = d.id  and ex.exampaperNum = d.category LEFT JOIN subdefine sub ON qg.questionNum = sub.id  and ex.exampaperNum = sub.category where ex.examNum = {examNum} and ex.gradeNum ={gradeNum} and ex.subjectNum ={subjectNum} and (d.category is not null or sub.category is not null)group by qg.questionNum ", null, args);
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String autoDistribute(String parmStr, String totalCount, String totalTime, String type, String questionCountPer, String yueJuanCount, String examNum, String gradeNum, String subjectNum, String user) {
        String sql_distributeAuto;
        int questionGroup_member;
        String flag = "T";
        String[] params = parmStr.split("&");
        List<Map<String, List<Map<String, Object>>>> list_yuejuanyuan = getYuejuan(examNum, gradeNum, subjectNum);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        String exampaperNum = this.dao._queryStr("select pexampaperNum from exampaper where examnum = {examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        new ArrayList();
        List<Map<String, Object>> list_map = new ArrayList<>();
        String msg = "";
        if (list_yuejuanyuan.size() > 0) {
            for (Map<String, List<Map<String, Object>>> listMap : list_yuejuanyuan) {
                String mapKey = listMap.keySet().stream().findAny().orElse("");
                if (listMap.get(mapKey).size() == 0) {
                    flag = "F1";
                    msg = msg + mapKey + " ";
                } else {
                    List<String> list_userIds = new ArrayList<>();
                    Map<String, Object> group_map = new HashMap<>();
                    for (Map<String, Object> map2 : listMap.get(mapKey)) {
                        list_userIds.add(map2.get("userNum") + "");
                    }
                    group_map.put("userIds", list_userIds);
                    group_map.put("groupCount", Integer.valueOf(list_userIds.size()));
                    group_map.put("yifenpei", 0);
                    list_map.add(group_map);
                }
            }
            if (!"T".equals(flag)) {
                return flag + "_" + msg;
            }
        }
        new ArrayList();
        List<Map> argList = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            String[] paps = params[i].split(Const.STRING_SEPERATOR);
            if (type.equals("3")) {
                paps = params[i].split("=");
            }
            String[] finalPaps = paps;
            Map args2 = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) finalPaps[0]);
            argList.add(args2);
        }
        this.dao._batchExecute("delete from distributeAuto where exampaperNum={exampaperNum}  and groupNum={groupNum} ", argList);
        this.dao._batchExecute("delete from questionGroup_user where userType<>2 and exampaperNum={exampaperNum}  and groupNum={groupNum}", argList);
        this.dao._batchExecute("delete from quota where exampaperNum={exampaperNum}  and groupNum={groupNum}", argList);
        this.dao._batchExecute("update task set insertUser='-1',porder=0,fenfaDate=0 where exampaperNum={exampaperNum}  and groupNum={groupNum} and status='F' ", argList);
        new ArrayList();
        new ArrayList();
        int groupCount = Integer.valueOf(list_map.get(0).get("groupCount") + "").intValue();
        int weifenpeiAllCount = 0;
        List<Map> argList2 = new ArrayList<>();
        List<Map> argList3 = new ArrayList<>();
        if (type.equals("1")) {
            sql_distributeAuto = "insert into distributeAuto (id,type,exampaperNum,subjectNum,groupNum,scoreTime,questionCount_per,insertUser,insertDate) values({uuid} ,{type} ,{exampaperNum} ,{subjectNum},{groupNum},{scoreTime} ,{questionCountPer} ,{user},now())";
            if (groupCount * Integer.valueOf(questionCountPer).intValue() < params.length) {
                questionCountPer = ((params.length / groupCount) + 1) + "";
            }
            int totalQuestionGroup_member = Integer.parseInt(yueJuanCount) * Integer.parseInt(questionCountPer);
            int yifenbeiQuestionGroup_member = 0;
            for (int i2 = 0; i2 < params.length; i2++) {
                String[] param = params[i2].split(Const.STRING_SEPERATOR);
                long uuid = DbUtils.getUuid();
                String finalQuestionCountPer = questionCountPer;
                Map args22 = StreamMap.create().put("uuid", (Object) Long.valueOf(uuid)).put("type", (Object) type).put("exampaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("groupNum", (Object) param[0]).put("scoreTime", (Object) param[2]).put("questionCountPer", (Object) finalQuestionCountPer).put("user", (Object) user);
                argList2.add(args22);
                if (i2 < params.length - 1) {
                    float a = (((Float.parseFloat(param[3]) * ((float) Long.parseLong(param[2]))) * Float.valueOf(yueJuanCount).floatValue()) * ((float) Long.parseLong(questionCountPer))) / Float.parseFloat(totalTime);
                    questionGroup_member = Math.round(a);
                    if (questionGroup_member == 0) {
                        questionGroup_member = 1;
                    }
                    yifenbeiQuestionGroup_member += questionGroup_member;
                } else {
                    questionGroup_member = (totalQuestionGroup_member - yifenbeiQuestionGroup_member) + weifenpeiAllCount;
                }
                int weifenpeiAllCount_per = questionGroup_member;
                int yifenpeiCount_group = 0;
                for (int m = 0; m < list_map.size(); m++) {
                    List<String> yuejuanyuan = (List) list_map.get(m).get("userIds");
                    int groupCount2 = Integer.valueOf(list_map.get(m).get("groupCount") + "").intValue();
                    int yifenpeiCount = Integer.valueOf(list_map.get(m).get("yifenpei") + "").intValue();
                    int questionGroup_member_group = Math.round((groupCount2 / Float.valueOf(yueJuanCount).floatValue()) * weifenpeiAllCount_per);
                    if (questionGroup_member_group == 0) {
                        questionGroup_member_group = 1;
                    }
                    if (m == list_map.size() - 1) {
                        questionGroup_member_group = questionGroup_member - yifenpeiCount_group;
                    }
                    if (questionGroup_member_group > groupCount2) {
                        questionGroup_member_group = groupCount2;
                    }
                    weifenpeiAllCount_per -= questionGroup_member_group;
                    yifenpeiCount_group += questionGroup_member_group;
                    for (int j = 0; j < questionGroup_member_group; j++) {
                        String randomUserId = yuejuanyuan.get(yifenpeiCount);
                        long uuid2 = DbUtils.getUuid();
                        argList3.add(StreamMap.create().put("uuid2", (Object) Long.valueOf(uuid2)).put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) param[0]).put("randomUserId", (Object) randomUserId).put("user", (Object) user));
                        yifenpeiCount++;
                        if (yifenpeiCount == groupCount2) {
                            yifenpeiCount = 0;
                        }
                        list_map.get(m).put("yifenpei", Integer.valueOf(yifenpeiCount));
                    }
                }
                weifenpeiAllCount += weifenpeiAllCount_per;
            }
        } else if (type.equals("2")) {
            sql_distributeAuto = "insert into distributeAuto (id,type,exampaperNum,subjectNum,groupNum,`member`,insertUser,insertDate) values({uuid},{type} ,{exampaperNum} ,{subjectNum},{groupNum} ,{member},{user},now())";
            String str = (Integer.parseInt(totalCount) / Integer.parseInt(yueJuanCount)) + "";
            for (String str2 : params) {
                String[] param2 = str2.split(Const.STRING_SEPERATOR);
                long uuid3 = DbUtils.getUuid();
                Map args23 = StreamMap.create().put("uuid", (Object) Long.valueOf(uuid3)).put("type", (Object) type).put("exampaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("groupNum", (Object) param2[0]).put("member", (Object) param2[2]).put("user", (Object) user);
                argList2.add(args23);
                int yifenpeiCount_group2 = 0;
                for (int m2 = 0; m2 < list_map.size(); m2++) {
                    List<String> yuejuanyuan2 = (List) list_map.get(m2).get("userIds");
                    int groupCount3 = Integer.valueOf(list_map.get(m2).get("groupCount") + "").intValue();
                    int yifenpeiCount2 = Integer.valueOf(list_map.get(m2).get("yifenpei") + "").intValue();
                    int questionGroup_member_group2 = Math.round((groupCount3 / Float.valueOf(yueJuanCount).floatValue()) * Integer.parseInt(param2[2]));
                    if (m2 == list_map.size() - 1) {
                        questionGroup_member_group2 = Integer.parseInt(param2[2]) - yifenpeiCount_group2;
                    }
                    yifenpeiCount_group2 += questionGroup_member_group2;
                    for (int j2 = 0; j2 < questionGroup_member_group2; j2++) {
                        String randomUserId2 = yuejuanyuan2.get(yifenpeiCount2);
                        long uuid22 = DbUtils.getUuid();
                        argList3.add(StreamMap.create().put("uuid2", (Object) Long.valueOf(uuid22)).put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) param2[0]).put("randomUserId", (Object) randomUserId2).put("user", (Object) user));
                        yifenpeiCount2++;
                        if (yifenpeiCount2 == groupCount3) {
                            yifenpeiCount2 = 0;
                        }
                    }
                    list_map.get(m2).put("yifenpei", Integer.valueOf(yifenpeiCount2));
                }
            }
        } else {
            sql_distributeAuto = "insert into distributeAuto (id,type,exampaperNum,subjectNum,groupNum,schoolNum,insertUser,insertDate) values({uuid} ,{type} ,{exampaperNum} ,{subjectNum} ,{groupNum} ,{school},{user},now())";
            for (String str3 : params) {
                String[] param3 = str3.split("=");
                String[] schoolNums = param3[1].split(Const.STRING_SEPERATOR);
                String str4 = param3[0];
                List<Map> maps = new ArrayList<>();
                for (String school : schoolNums) {
                    long uuid4 = DbUtils.getUuid();
                    Map args24 = StreamMap.create().put("uuid", (Object) Long.valueOf(uuid4)).put("type", (Object) type).put("exampaperNum", (Object) exampaperNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("groupNum", (Object) param3[0]).put(License.SCHOOL, (Object) school).put("user", (Object) user);
                    argList2.add(args24);
                    Map args3 = StreamMap.create().put(License.SCHOOL, (Object) school).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
                    maps.add(args3);
                    Iterator<?> it = this.dao._queryBeanList("select ur.userNum id from (select r.exampaperNum,ur.userNum from role r LEFT JOIN userrole ur ON r.roleNum = ur.roleNum where r.type='4' union select exampaperNum,userNum from userrole_sub us ) ur LEFT JOIN user u ON ur.userNum = u.id left join exampaper ex on ur.exampaperNum=ex.exampaperNum where u.schoolNum={school}  and ex.examNum={examNum}  and subjectNum={subjectNum} and gradeNum={gradeNum}", User.class, args3).iterator();
                    while (it.hasNext()) {
                        User user2 = (User) it.next();
                        String userId = user2.getId();
                        long uuid23 = DbUtils.getUuid();
                        Map args4 = StreamMap.create().put("uuid2", (Object) Long.valueOf(uuid23)).put("exampaperNum", (Object) exampaperNum).put("groupNum", (Object) param3[0]).put("userId", (Object) userId).put("user", (Object) user);
                        argList3.add(args4);
                    }
                }
            }
        }
        this.dao._batchExecute(sql_distributeAuto, argList2);
        this.dao._batchExecute("insert into questionGroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate,isFinished) values ({uuid2},{exampaperNum} ,{groupNum} ,0,{randomUserId},{user},now(),0)", argList3);
        return flag;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getExamListInfo(String examNum, String user) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("user", (Object) user);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={user} and type=1 limit 1", args);
        String sql = "select ex.exampaperNum,ex.examNum,e.examName,ex.gradeNum,g.gradeName,ex.subjectNum,s.subjectName,ifnull(ur.c_user,0) c_user,IFNULL(rs.reserveNum,0)reserveNum from exampaper ex LEFT JOIN exam e on ex.examNum = e.examNum LEFT JOIN basegrade g ON ex.gradeNum = g.gradeNum LEFT JOIN `subject` s ON ex.subjectNum = s.subjectNum LEFT JOIN (select count(ur.userNum) c_user,r.exampaperNum from userrole ur left join role r on ur.roleNum=r.roleNum where r.type='4' group by r.exampaperNum union select count(userNum),exampaperNum from userrole_sub group by exampaperNum) ur on ex.exampaperNum = ur.exampaperNum LEFT JOIN reserveyuejuannum rs ON ex.examNum = rs.examNum AND ex.gradeNum = rs.gradeNum AND ex.subjectNum = rs.subjectNum AND rs.rootId=rs.ItemId LEFT JOIN (select distinct pexampaperNum from exampaper where exampaperNum!=pexampaperNum) t ON ex.exampaperNum=t.pexampaperNum WHERE t.pexampaperNum is null and ex.examNum = {examNum} GROUP BY ex.examPaperNum ORDER BY ex.gradeNum ,ex.subjectNum ";
        if (user != null && !user.equals("-1") && !user.equals("-2") && null == map) {
            sql = "select ex.exampaperNum,ex.examNum,e.examName,ex.gradeNum,g.gradeName,ex.subjectNum,s.subjectName,ifnull(ur.c_user,0) c_user,sum(ifnull(rs.reserveNum,0))reserveNum from exampaper ex LEFT JOIN exam e on ex.examNum = e.examNum LEFT JOIN basegrade g ON ex.gradeNum = g.gradeNum LEFT JOIN `subject` s ON ex.subjectNum = s.subjectNum LEFT JOIN (select count(ur.userNum) c_user,r.exampaperNum from userrole ur left join role r on ur.roleNum=r.roleNum left join user u on ur.userNum = u.id inner JOIN (SELECT schoolNum from (select DISTINCT schoolNum FROM schoolscanpermission where userNum={user} UNION all select schoolNum from user where id={user} ) s GROUP BY schoolNum) t on u.schoolnum = t.schoolNum  where r.type='4' and t.schoolNum is not null group by r.exampaperNum union select count(us.userNum),us.exampaperNum from userrole_sub us left join user u on us.userNum = u.id inner JOIN (SELECT schoolNum from (select DISTINCT schoolNum FROM schoolscanpermission where userNum={user} UNION all select schoolNum from user where id={user} ) s GROUP BY schoolNum) t on u.schoolnum = t.schoolNum  where t.schoolNum is not null group by exampaperNum) ur on ex.exampaperNum = ur.exampaperNum left join (select rs.examNum,rs.gradeNum,rs.subjectNum,rs.itemid,rs.reserveNum from reserveyuejuannum rs left join (SELECT t.pItemId FROM (select pItemId,pItemName,sItemId,sItemName from statisticitem_school ss where statisticItem='01' GROUP BY pItemId,sItemId) t INNER JOIN school sch on sch.id=t.sItemId and sch.isDelete='F' LEFT JOIN (select schoolNum from schauthormanage where userid={user} ) sc ON t.sItemId = sc.schoolNum GROUP BY t.pItemId HAVING SUM(IF(sc.schoolNum is null ,1,0))=0 UNION SELECT sc.schoolNum FROM (select pItemId,pItemName,sItemId,sItemName from statisticitem_school ss where statisticItem='01' GROUP BY pItemId,sItemId) t INNER JOIN school sch on sch.id=t.sItemId and sch.isDelete='F' inner JOIN(select schoolNum from schoolscanpermission where userNum={user} ) sc ON cast(t.sItemId as char) = cast(sc.schoolNum as char) LEFT join (SELECT t.pItemId FROM (select pItemId,pItemName,sItemId,sItemName from statisticitem_school ss where statisticItem='01' GROUP BY pItemId,sItemId) t INNER JOIN school sch on sch.id=t.sItemId and sch.isDelete='F' inner JOIN (select schoolNum from schoolscanpermission where userNum={user} ) sc ON cast(t.sItemId as char) = cast(sc.schoolNum as char) GROUP BY t.pItemId HAVING SUM(IF(sc.schoolNum is null ,1,0))=0 )t2 ON  t.pitemid = t2.pitemid where t2.pitemid is null and sc.schoolNum is not null) t on rs.itemId=t.pitemid  where t.pitemid is not null )rs ON ex.examNum = rs.examNum AND ex.gradeNum = rs.gradeNum AND ex.subjectNum = rs.subjectNum LEFT JOIN (select distinct pexampaperNum from exampaper where exampaperNum!=pexampaperNum) t1 ON ex.exampaperNum=t1.pexampaperNum WHERE t1.pexampaperNum is null and ex.examNum = {examNum} GROUP BY ex.examPaperNum ORDER BY ex.gradeNum ,ex.subjectNum ";
        }
        List<Map<String, Object>> list = this.dao._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getYuejuan(String exampaperNum, String user) {
        String sql;
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        if (user != null && !user.equals("-1") && !user.equals("-2")) {
            sql = "select DISTINCT ur.userNum,u.realname,s.schoolName,u.username from (select userNum,exampaperNum from userrole ur left join role r on ur.roleNum=r.roleNum where r.type='4' union all select userNum,exampaperNum from userrole_sub ) ur LEFT JOIN user u ON ur.userNum = u.id and u.usertype = '1' LEFT JOIN (SELECT schoolNum from (select DISTINCT schoolNum FROM schauthormanage where userId={user} UNION all select schoolNum from user where id={user} ) s GROUP BY schoolNum) t on u.schoolnum = t.schoolNum LEFT JOIN school s on u.schoolNum = s.id and s.isDelete='F' where ur.exampaperNum={exampaperNum} and t.schoolNum is not null  ORDER BY s.schoolName ,u.realname";
            args.put("user", user);
        } else {
            sql = "select DISTINCT ur.userNum,u.realname,s.schoolName,u.username from (select userNum,exampaperNum from userrole ur left join role r on ur.roleNum=r.roleNum where r.type='4' union select userNum,exampaperNum from userrole_sub ) ur LEFT JOIN user u ON ur.userNum = u.id and u.usertype = '1' LEFT JOIN school s on u.schoolNum = s.id and s.isDelete='F' where ur.exampaperNum={exampaperNum} ORDER BY s.schoolName ,u.realname ";
        }
        List<Map<String, Object>> list = this.dao._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<String> changeYuejuanyuan(String examNum, String gradeNum, String subjectNum, String userNum, String exampaperNum, String loginUser) {
        List<String> msg = new ArrayList<>();
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("userNum", userNum);
        args.put("loginUser", loginUser);
        String id = this.dao._queryStr("select distinct qu.id from questionGroup_user qu left join exampaper ex on qu.exampaperNum = ex.exampaperNum where ex.exampaperNum=(select pexampaperNum from exampaper where exampaperNum={exampaperNum} ) and qu.userNum={userNum} ", args);
        if (id != null && !id.equals("") && !id.equals("null")) {
            msg.add("F1");
            return msg;
        }
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        String id2 = this.dao._queryStr("select distinct ur.userNum from (select userNum,exampaperNum from userrole ur left join role r on ur.roleNum=r.roleNum where r.type='4' union select userNum,exampaperNum from userrole_sub ) ur left join exampaper ex on ur.exampaperNum = ex.exampaperNum where ex.examNum={examNum}  and ex.gradeNum={gradeNum} and ex.subjectNum={subjectNum} and ur.userNum={userNum} ", args);
        if (id2 != null && !id2.equals("") && !id2.equals("null")) {
            msg.add("F2");
            return msg;
        }
        String pexampaperNum = this.dao._queryStr("select pexampaperNum from exampaper where exampaperNum={exampaperNum} ", args);
        String roleNum = this.dao._queryStr("select roleNum from role where exampaperNum={exampaperNum} ", args);
        args.put("roleNum", roleNum);
        if (roleNum != null && !roleNum.equals("") && !roleNum.equals("null")) {
            this.dao._execute("delete from userrole where userNum={userNum}  and roleNum={roleNum} ", args);
        } else {
            this.dao._execute("delete from userrole_sub where userNum={userNum} and exampaperNum={exampaperNum} ", args);
            args.put("pexampaperNum", pexampaperNum);
            String id_userNum = this.dao._queryStr("select ur.userNum from userrole_sub ur left join (select distinct exampaperNum from exampaper where pexampaperNum={pexampaperNum} ) ex on ur.exampaperNum=ex.exampaperNum where ex.exampaperNum is not null and ur.exampaperNum!={exampaperNum}  and ur.userNum={userNum} ", args);
            if (id_userNum == null || id_userNum.equals("") || id_userNum.equals("null")) {
                this.dao._queryStr("select roleNum from role where exampaperNum={pexampaperNum} ", args);
                String sql3 = "delete from userrole where roleNum={roleNum}  and userNum={userNum} ";
                this.dao._execute(sql3, args);
            }
        }
        StringBuffer deleteLogSql = new StringBuffer();
        deleteLogSql.append("insert into userrole_log (examNum,examPaperNum,userNum,roleNum,roleName,insertUser,insertDate,operationType) values ({examNum},{exampaperNum},{userNum},{roleNum},'阅卷员',{loginUser},now(),'delete_move') ");
        this.dao._execute(deleteLogSql.toString(), args);
        AjaxData data = (AjaxData) this.dao._queryBean("select ex.exampaperNum ext1 from exampaper ex where ex.examNum={examNum}  and ex.gradeNum ={gradeNum} and ex.subjectNum={subjectNum} ", AjaxData.class, args);
        String newExamPaperNum = data.getExt1();
        args.put("newExamPaperNum", newExamPaperNum);
        String pexampaperNum2 = this.dao._queryStr("select pexampaperNum from exampaper where exampaperNum={newExamPaperNum} ", args);
        args.put("pexampaperNum", pexampaperNum2);
        Object newRoleNum = this.dao._queryObject("select roleNum from role where exampaperNum={pexampaperNum} ", args);
        if (newRoleNum == null || newRoleNum.equals("")) {
            String roleNum1 = GUID.getGUIDStr();
            Role role = new Role();
            role.setRoleNum(String.valueOf(roleNum1));
            role.setRoleName("阅卷员");
            role.setExamNum(Integer.valueOf(examNum));
            role.setSchoolNum(0);
            role.setInsertUser(loginUser);
            role.setType("4");
            role.setIsDelete("F");
            role.setInsertDate(DateUtil.getCurrentDay());
            role.setExamPaperNum(Integer.valueOf(pexampaperNum2));
            this.dao.save(role);
            newRoleNum = roleNum1;
        }
        args.put("newRoleNum", newRoleNum);
        if (!pexampaperNum2.equals(newExamPaperNum)) {
            this.dao._execute("insert into userrole_sub (userNum,exampaperNum) values({userNum},{newExamPaperNum})", args);
            Object role_id = this.dao._queryObject("select id from userrole where userNum={userNum} and roleNum={newRoleNum} ", args);
            if (role_id == null || role_id.equals("")) {
                this.dao._execute("insert into userrole (userNum,roleNum,insertUser,insertDate,isDelete) values({userNum} ,{newRoleNum},{loginUser},now(),'F')", args);
            }
        } else {
            this.dao._execute("insert into userrole (userNum,roleNum,insertUser,insertDate,isDelete) values({userNum},{newRoleNum},{loginUser},now(),'F')", args);
        }
        StringBuffer addLogSql = new StringBuffer();
        addLogSql.append("insert into userrole_log (examNum,examPaperNum,userNum,roleNum,roleName,insertUser,insertDate,operationType) values ({examNum},{examNum},{examNum},{examNum},'阅卷员',{loginUser},now(),'insert_move')");
        this.dao._execute(addLogSql.toString(), args);
        msg.add("T");
        msg.add(newExamPaperNum);
        return msg;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String deleteMember(String exampaperNum, String userNum, String examNum, String loginUserId) {
        String sql_delete;
        String msg = "T";
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("userNum", userNum);
        String id = this.dao._queryStr("select distinct qu.id from questionGroup_user qu where qu.exampaperNum=(select pexampaperNum from exampaper where exampaperNum={exampaperNum}) and qu.userNum={userNum} ", args);
        if (id != null && !id.equals("") && !id.equals("null")) {
            return "F1";
        }
        String roleNum = this.dao._queryStr("select roleNum from role where exampaperNum={exampaperNum} and type='4' ", args);
        args.put("roleNum", roleNum);
        if (roleNum != null && !roleNum.equals("") && !roleNum.equals("null")) {
            sql_delete = "delete from userrole where roleNum={roleNum} and userNum={userNum} ";
        } else {
            sql_delete = "delete from userrole_sub where userNum={userNum} and exampaperNum={exampaperNum} ";
            Object pexampaperNum = this.dao._queryObject("select distinct pexampaperNum from exampaper where exampaperNum={exampaperNum} ", args);
            args.put("pexampaperNum", pexampaperNum);
            String id_userNum = this.dao._queryStr("select ur.userNum from userrole_sub ur left join (select distinct exampaperNum from exampaper where pexampaperNum={pexampaperNum} ) ex on ur.exampaperNum=ex.exampaperNum where ex.exampaperNum is not null and ur.exampaperNum!={exampaperNum} and ur.userNum={userNum} ", args);
            if (id_userNum == null || id_userNum.equals("") || id_userNum.equals("null")) {
                String roleNum2 = this.dao._queryStr("select roleNum from role where exampaperNum={pexampaperNum} ", args);
                String sql3 = "delete from userrole where roleNum={roleNum}  and userNum={userNum} " + userNum;
                args.put("roleNum", roleNum2);
                this.dao._execute(sql3, args);
            }
        }
        int i = this.dao._execute(sql_delete, args);
        if (i < 1) {
            msg = "F";
        }
        StringBuffer logSql = new StringBuffer();
        logSql.append("insert into userrole_log (examNum,examPaperNum,userNum,roleNum,roleName,insertUser,insertDate,operationType) values ({examNum},{exampaperNum},{userNum},{roleNum},'阅卷员',{loginUserId},now(),'delete')");
        args.put("loginUserId", loginUserId);
        this.dao._execute(logSql.toString(), args);
        return msg;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getMemberSetTeacher(String schoolNum, String gradeNum, String subjectNum, String leicengId, String user, String exampaperNum, String type, String leftTeacherNameInput) {
        String examStr;
        String poistionStr1;
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        String examNum = this.dao._queryStr("select examNum from exampaper where exampaperNum={exampaperNum} ", args);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("leicengId", leicengId);
        args.put("user", user);
        args.put("leftTeacherNameInput", leftTeacherNameInput);
        if (type != null && type.equals("2")) {
            examStr = " and r.examNum={examNum} ";
        } else {
            examStr = " and r.exampaperNum={exampaperNum} ";
        }
        String schoolNumStr = "";
        if (schoolNum != null && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            schoolNumStr = " and te.schoolNum={schoolNum} ";
        }
        String userpostionStr = "";
        String gradeStr = "";
        if (gradeNum != null && !gradeNum.equals("") && !gradeNum.equals("-1")) {
            userpostionStr = "LEFT JOIN userposition up on u.id = up.userNum ";
            gradeStr = " and up.gradeNum={gradeNum} ";
        }
        String subjectStr = "";
        if (subjectNum != null && !subjectNum.equals("") && !subjectNum.equals("-1")) {
            userpostionStr = "LEFT JOIN userposition up on u.id = up.userNum ";
            subjectStr = " and up.subjectNum={subjectNum} ";
        }
        String leicengStr = "";
        String leicengConditionStr = "";
        if (schoolNum.equals("-1") && leicengId != null && !leicengId.equals("") && !leicengId.equals("-1")) {
            leicengStr = "LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leicengId} and statisticItem='01') tt on u.schoolNum = tt.schoolNum ";
            leicengConditionStr = " and tt.schoolNum is not null ";
        }
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={user} and type=1 limit 1", args);
        if (schoolNum.equals("-1") && user != null && !user.equals("-1") && !user.equals("-2") && null == map) {
            poistionStr1 = "inner JOIN (select schoolNum FROM schoolscanpermission where userNum={user} UNION select schoolNum from user where id={user} ) t on cast(u.schoolnum as char) = cast(t.schoolNum as char) LEFT JOIN school s on cast(t.schoolNum as char) = cast(s.id as char) ";
        } else {
            poistionStr1 = "inner JOIN  school s on u.schoolNum  = s.id ";
        }
        StringBuffer teacherNameStr = new StringBuffer();
        if (!"".equals(leftTeacherNameInput)) {
            teacherNameStr.append(StrUtil.format(" and (te.teacherName like '%{}%' or te.teacherNum like '%{}%') ", new Object[]{leftTeacherNameInput, leftTeacherNameInput}));
        }
        String sql = "select DISTINCT u.id,s.id schoolNum,s.schoolName,te.teacherNum,te.teacherName from teacher te  INNER JOIN user u on te.id = u.userid and u.usertype='1' " + userpostionStr + poistionStr1 + " LEFT JOIN(select ur.userNum from userrole ur LEFT JOIN role r on ur.roleNum = r.roleNum where r.type='4' " + examStr + "  union select userNum from userrole_sub where exampaperNum={exampaperNum} ) ur on cast(u.id as char) = cast(ur.userNum as char)   " + leicengStr + " where  1=1 " + leicengConditionStr + schoolNumStr + gradeStr + subjectStr + ((Object) teacherNameStr) + " order by CONVERT(schoolName USING gbk) ";
        List<Map<String, Object>> list = this.dao._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String addOrRemoveMember(String exampaperNum, String userNumStr, String flag, String user) {
        String[] userNums = new String[0];
        if (userNumStr != null && !userNumStr.equals("")) {
            userNums = userNumStr.split(Const.STRING_SEPERATOR);
        }
        String msg = "";
        new ArrayList();
        List<RowArg> rowArgList = new ArrayList<>();
        String currentDate = DateUtil.getCurrentTime();
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum);
        String sub_exampaperNum = this.dao._queryStr("select exampaperNum from exampaper where pexampaperNum={exampaperNum} ", args);
        if (sub_exampaperNum != null && !sub_exampaperNum.equals("") && !sub_exampaperNum.equals("null")) {
            String roleNum = this.dao._queryStr("select roleNum from role where exampaperNum={exampaperNum} and type=4 ", args);
            String examNum = this.dao._queryStr("select examNum from exampaper where exampaperNum={exampaperNum} ", args);
            if (roleNum == null || roleNum.equals("") || roleNum.equals("null")) {
                String roleNum1 = GUID.getGUIDStr();
                Role role = new Role();
                role.setRoleNum(String.valueOf(roleNum1));
                role.setRoleName("阅卷员");
                role.setExamNum(Integer.valueOf(examNum));
                role.setSchoolNum(0);
                role.setInsertUser(user);
                role.setType("4");
                role.setIsDelete("F");
                role.setInsertDate(DateUtil.getCurrentDay());
                role.setExamPaperNum(Integer.valueOf(exampaperNum));
                this.dao.save(role);
                roleNum = roleNum1;
            }
            if (flag.equals("add")) {
                for (String userNum : userNums) {
                    Map args2 = new HashMap();
                    args2.put("userNum", userNum);
                    args2.put("user", user);
                    args2.put("currentDate", currentDate);
                    Object id = this.dao._queryObject("select id from userrole where roleNum='4' and userNum={userNum} ", args2);
                    if (id == null || id.equals("")) {
                        rowArgList.add(new RowArg("insert into userrole(userNum,roleNum,insertUser,insertDate) values({userNum},'4',{user},{currentDate})", args2));
                    }
                    String sql = "insert into userrole(userNum,roleNum,insertUser,insertDate) values({userNum},{roleNum},{user},{currentDate})";
                    rowArgList.add(new RowArg(sql, args2));
                    args2.put(Const.EXPORTREPORT_examNum, examNum);
                    args2.put("exampaperNum", exampaperNum);
                    args2.put("userNum", userNum);
                    args2.put("roleNum", roleNum);
                    rowArgList.add(new RowArg("insert into userrole_log (examNum,examPaperNum,userNum,roleNum,roleName,insertUser,insertDate,operationType) values ({examNum},{exampaperNum},{userNum},{roleNum},'阅卷员',{user},{currentDate},'insert')", args2));
                }
                msg = "T";
            } else {
                int i = 0;
                for (String userNum2 : userNums) {
                    Map args3 = new HashMap();
                    args3.put("exampaperNum", exampaperNum);
                    args3.put("userNum", userNum2);
                    AjaxData data = (AjaxData) this.dao._queryBean("select distinct qu.id num,u.realName name,u.id ext1 from questionGroup_user qu left join user u on qu.userNum = u.id where qu.exampaperNum={exampaperNum} and qu.usernum={userNum} ", AjaxData.class, args3);
                    if (data != null && !data.getNum().equals("")) {
                        msg = msg + data.getExt1() + Const.STRING_SEPERATOR;
                        i++;
                    } else {
                        args3.put("roleNum", roleNum);
                        rowArgList.add(new RowArg("delete from userrole where roleNum={roleNum} and userNum={userNum}", args3));
                        args3.put(Const.EXPORTREPORT_examNum, examNum);
                        args3.put("user", user);
                        args3.put("currentDate", currentDate);
                        rowArgList.add(new RowArg("insert into userrole_log (examNum,examPaperNum,userNum,roleNum,roleName,insertUser,insertDate,operationType) values ({examNum},{exampaperNum},{userNum},{roleNum},'阅卷员',{user},{currentDate},'delete')", args3));
                    }
                }
                if (!msg.equals("")) {
                    msg = msg.substring(0, msg.length() - 1);
                }
            }
        } else {
            Map args4 = new HashMap();
            args4.put("exampaperNum", exampaperNum);
            Object[] res = this.dao._queryArray("select pexampaperNum,examNum from exampaper where exampaperNum={exampaperNum} ", args4);
            String pexampaperNum = String.valueOf(res[0]);
            String examNum2 = String.valueOf(res[1]);
            args4.put("pexampaperNum", pexampaperNum);
            String roleNum2 = this.dao._queryStr("select roleNum from role where exampaperNum={pexampaperNum} and type=4", args4);
            if (roleNum2 == null || roleNum2.equals("") || roleNum2.equals("null")) {
                String roleNum12 = GUID.getGUIDStr();
                Role role2 = new Role();
                role2.setRoleNum(String.valueOf(roleNum12));
                role2.setRoleName("阅卷员");
                role2.setExamNum(Integer.valueOf(examNum2));
                role2.setSchoolNum(0);
                role2.setInsertUser(user);
                role2.setType("4");
                role2.setIsDelete("F");
                role2.setInsertDate(DateUtil.getCurrentDay());
                role2.setExamPaperNum(Integer.valueOf(pexampaperNum));
                this.dao.save(role2);
                roleNum2 = roleNum12;
            }
            if (flag.equals("add")) {
                for (String userNum3 : userNums) {
                    Map args5 = new HashMap();
                    args5.put("userNum", userNum3);
                    args5.put("exampaperNum", exampaperNum);
                    args5.put("user", user);
                    args5.put("currentDate", currentDate);
                    rowArgList.add(new RowArg("insert into userrole_sub(userNum,exampaperNum) values({userNum} ,{exampaperNum} )", args5));
                    args5.put("roleNum", roleNum2);
                    String role_id = this.dao._queryStr("select id from userrole where userNum={userNum} and roleNum={roleNum} ", args5);
                    if (role_id == null || role_id.equals("") || role_id.equals("null")) {
                        Object id2 = this.dao._queryObject("select id from userrole where roleNum=4 and userNum={userNum} ", args5);
                        if (id2 == null || id2.equals("")) {
                            rowArgList.add(new RowArg("insert into userrole(userNum,roleNum,insertUser,insertDate) values({userNum} ,'4',{user} ,{currentDate} )", args5));
                        }
                        rowArgList.add(new RowArg("insert into userrole(userNum,roleNum,insertUser,insertDate) values({userNum},{roleNum},{user} ,{currentDate})", args5));
                    }
                    rowArgList.add(new RowArg("insert into userrole_log (examNum,examPaperNum,userNum,roleNum,roleName,insertUser,insertDate,operationType)  values ({examNum},{exampaperNum} ,{userNum},{roleNum},'阅卷员',{user},{currentDate},'insert')", args5));
                }
                msg = "T";
            } else {
                for (String userNum4 : userNums) {
                    Map args32 = new HashMap();
                    args32.put("pexampaperNum", pexampaperNum);
                    args32.put("userNum", userNum4);
                    AjaxData data2 = (AjaxData) this.dao._queryBean("select distinct qu.id num,u.realName name,u.id ext1 from questionGroup_user qu left join user u on qu.userNum = u.id where qu.exampaperNum={pexampaperNum} and qu.usernum={userNum} ", AjaxData.class, args32);
                    if (data2 != null && !data2.getNum().equals("")) {
                        msg = msg + data2.getExt1() + Const.STRING_SEPERATOR;
                    } else {
                        rowArgList.add(new RowArg("delete from userrole_sub where userNum={userNum}  and exampaperNum={exampaperNum} ", args32));
                        args32.put("exampaperNum", exampaperNum);
                        String id_userNum = this.dao._queryStr("select ur.userNum from userrole_sub ur left join(select distinct exampaperNum from exampaper where pexampaperNum={pexampaperNum} ) ex on ur.exampaperNum=ex.exampaperNum where ex.exampaperNum is not null and ur.exampaperNum!={exampaperNum} and ur.userNum={userNum} ", args32);
                        if (id_userNum == null || id_userNum.equals("") || id_userNum.equals("null")) {
                            args32.put("roleNum", roleNum2);
                            rowArgList.add(new RowArg("delete from userrole where roleNum={roleNum}  and userNum={userNum} ", args32));
                        }
                        args32.put("user", user);
                        args32.put("currentDate", currentDate);
                        rowArgList.add(new RowArg("insert into userrole_log (examNum,examPaperNum,userNum,roleNum,roleName,insertUser,insertDate,operationType) values ({examNum},{exampaperNum},{userNum},{roleNum},'阅卷员',{user},{currentDate},'delete')", args32));
                    }
                }
                if (!msg.equals("")) {
                    msg = msg.substring(0, msg.length() - 1);
                }
            }
        }
        this.dao._batchExecute(rowArgList);
        return msg;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getMemberSetTeacher(String exampaperNum, String user, String rightTeacherNameInput) {
        String poistionStr1 = "";
        String poistionStr2 = "";
        Map args = new HashMap();
        args.put("user", user);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={user} and type=1 limit 1", args);
        if (user != null && !user.equals("-1") && !user.equals("-2") && null == map) {
            poistionStr1 = "LEFT JOIN (select DISTINCT schoolNum FROM schoolscanpermission where userNum={user}  UNION select schoolNum from user where id={user} ) t on u.schoolnum = t.schoolNum ";
            poistionStr2 = " and t.schoolNum is not null ";
        }
        StringBuffer teacherNameStr = new StringBuffer();
        if (!"".equals(rightTeacherNameInput)) {
            teacherNameStr.append(" and (te.teacherName like {teacherName} or te.teacherNum like {teacherNum}) ");
            args.put("teacherNum", "%" + rightTeacherNameInput + "%");
            args.put("teacherName", "%" + rightTeacherNameInput + "%");
        }
        String sql = "select u.schoolNum,s.schoolName,te.teacherNum,te.teacherName,u.id from (select ur.userNum,r.exampaperNum from userrole ur LEFT JOIN role r on ur.roleNum = r.roleNum where r.type='4'  union all select userNum,exampaperNum from userrole_sub ) ur LEFT JOIN user u on ur.userNum = u.id LEFT JOIN teacher te on u.userid=te.id LEFT JOIN school s on u.schoolNum = s.id " + poistionStr1 + "where ur.exampaperNum= {exampaperNum} " + poistionStr2 + ((Object) teacherNameStr) + " group by ur.userNum order by u.id";
        args.put("exampaperNum", exampaperNum);
        return this.dao._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getGradeBySchool(String school, String user) {
        String schoolStr = "";
        Map args = new HashMap();
        args.put(License.SCHOOL, school);
        if (school != null && !school.equals("") && !school.equals("-1")) {
            schoolStr = " and g.schoolNum={school} ";
        }
        String poistionStr1 = "";
        String poistionStr2 = "";
        if (school.equals("-1") && user != null && !user.equals("-1") && !user.equals("-2")) {
            poistionStr1 = "LEFT JOIN (select DISTINCT schoolNum FROM schauthormanage where userId={user} UNION select schoolNum from user where id={user}) t on g.schoolnum = t.schoolNum ";
            poistionStr2 = " and t.schoolNum is not null ";
            args.put("user", user);
        }
        String sql = "select distinct g.gradeNum,g.gradeName from grade g " + poistionStr1 + " where g.isDelete='F' and g.islevel='0' " + schoolStr + poistionStr2 + "  order by g.gradeNum ";
        List<Map<String, Object>> list = this.dao._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getSubjectByGrade(String school, String grade, String user) {
        Map args = new HashMap();
        String schoolStr = "";
        if (school != null && !school.equals("") && !school.equals("-1")) {
            schoolStr = " and up.schoolNum={school} ";
            args.put(License.SCHOOL, school);
        }
        String gradeStr = "";
        if (grade != null && !grade.equals("") && !grade.equals("-1")) {
            gradeStr = " and up.gradeNum ={grade} ";
            args.put("grade", grade);
        }
        String poistionStr1 = "";
        String poistionStr2 = "";
        if (school.equals("-1") && user != null && !user.equals("-1") && !user.equals("-2")) {
            poistionStr1 = "LEFT JOIN (select DISTINCT schoolNum FROM schauthormanage where userId={user} UNION select schoolNum from user where id={user} ) t on up.schoolnum = t.schoolNum ";
            poistionStr2 = " and t.schoolNum is not null ";
            args.put("user", user);
        }
        String sql = "select distinct up.subjectNum,s.subjectName from userposition up left join subject s on up.subjectNum = s.subjectNum " + poistionStr1 + " where (up.type='1' or up.type='4') and s.isDelete ='F' " + schoolStr + gradeStr + poistionStr2 + " order by up.subjectNum ";
        List<Map<String, Object>> list = this.dao._queryMapList(sql, null, args);
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer cancelSubjectLeader(String examPaperNum, String examNum, String id) throws Exception {
        Map args = new HashMap();
        args.put("id", id);
        Object userNum = this.dao._queryObject("select userNum from questionGroup_user where id={id} ", args);
        this.dao._execute("delete from questionGroup_user where id={id} ", args);
        args.put("userNum", userNum);
        Object qid = this.dao._queryObject("select id from questionGroup_user where usertype='2' and userNum={userNum} ", args);
        int i = 0;
        if (qid == null || qid.equals("")) {
            i = this.dao._execute("delete from userrole where rolenum='3' and usernum={userNum} ", args);
        }
        return Integer.valueOf(i);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer delSubjectLeader(String examPaperNum, String examNum, String userNum) throws Exception {
        List<String> list_sql = new ArrayList<>();
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        String roleNum = this.dao._queryStr("select roleNum from role where examPaperNum={examPaperNum}  and type=3 and examNum={examNum} ", args);
        args.put("userNum", userNum);
        this.dao._execute("delete from questionGroup_user where examPaperNum = {examPaperNum} and userType=2 and userNum={userNum} ", args);
        args.put("roleNum", roleNum);
        this.dao._execute("delete from userrole where rolenum={roleNum} and usernum={userNum} ", args);
        list_sql.add("delete from questionGroup_user where examPaperNum = {examPaperNum} and userType=2 and userNum={userNum} ");
        list_sql.add("delete from userrole where rolenum={roleNum} and usernum={userNum} ");
        return 0;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Map<String, Object> getSchoolBeixuan(String groupNum, String school, String leiceng, String subjectNum) {
        int i = this.dao.queryInt("select count(1) from schoolGroup ", null).intValue();
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        String examPaperNum = this.dao._queryStr("select exampaperNum from questiongroup where groupNum={groupNum} ", args);
        args.put("examPaperNum", examPaperNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        String sub_exampaperNum = this.dao._queryStr("select exampaperNum from exampaper where pexampaperNum={examPaperNum} and subjectNum={subjectNum} ", args);
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null && !leiceng.equals("")) {
            leicengStr1 = " LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leiceng} and statisticItem='01' ) t ON u.schoolNum = t.schoolNum ";
            leicengStr2 = " and t.schoolNum is not null ";
            args.put("leiceng", leiceng);
        }
        String schoolStr = "";
        if (school != null && !school.equals("")) {
            schoolStr = " and s.schoolname like {school} ";
            args.put(License.SCHOOL, "%" + school + "%");
        }
        String sql = "select u.schoolNum,s.schoolName from role r LEFT JOIN userrole ur on r.roleNum = ur.roleNum LEFT JOIN `user` u ON ur.userNum = u.id LEFT JOIN school s ON u.schoolNum = s.id " + leicengStr1 + "where s.isDelete='F'  and r.exampaperNum=" + examPaperNum + leicengStr2 + schoolStr;
        if (i > 0) {
            sql = "select distinct u.schoolNum,s.schoolName from role r LEFT JOIN userrole ur on r.roleNum = ur.roleNum LEFT JOIN `user` u ON ur.userNum = u.id  LEFT JOIN schoolGroup sg ON u.schoolNum = sg.schoolNum left join school s on u.schoolNum = s.id " + leicengStr1 + "where sg.schoolNum is not null  and r.exampaperNum=" + examPaperNum + leicengStr2 + schoolStr;
        }
        if (!examPaperNum.equals(sub_exampaperNum)) {
            sql = "select u.schoolNum,s.schoolName from userrole_sub ur LEFT JOIN `user` u ON ur.userNum = u.id LEFT JOIN school s ON u.schoolNum = s.id " + leicengStr1 + "where s.isDelete='F'  and ur.exampaperNum=" + sub_exampaperNum + leicengStr2 + schoolStr;
            if (i > 0) {
                sql = "select distinct u.schoolNum,s.schoolName from userrole_sub ur LEFT JOIN `user` u ON ur.userNum = u.id  LEFT JOIN schoolGroup sg ON u.schoolNum = sg.schoolNum left join school s on u.schoolNum = s.id " + leicengStr1 + "where sg.schoolNum is not null  and ur.exampaperNum=" + sub_exampaperNum + leicengStr2 + schoolStr;
            }
        }
        return this.dao._queryOrderMap(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getSchoolByConditions(String school, String leiceng) {
        Map args = new HashMap();
        if (leiceng != null && !leiceng.equals("")) {
            args.put("leiceng", leiceng);
        }
        return this.dao._queryMapList("select DISTINCT u.schoolNum ,s.schoolName from questiongroup_user qu LEFT JOIN `user` u on qu.userNum = u.id LEFT JOIN school s on u.schoolnum = s.id where groupNum=", null, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getAutoSubjectInfo(String examNum, String gradeNum, String subjectNum) {
        return this.gd.getAutoSubjectInfo(examNum, gradeNum, subjectNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public AjaxData getLimitTime(String examNum, String partType) {
        Map args = StreamMap.create().put("partType", (Object) partType).put(Const.EXPORTREPORT_examNum, (Object) examNum);
        AjaxData obj = (AjaxData) this.dao._queryBean("select starttime ext1,endtime ext2 from astrict where partType ={partType} and examNum ={examNum}", AjaxData.class, args);
        return obj;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getAutoDistribuitType(String examNum, String gradeNum, String subjectNum) {
        return this.gd.getAutoDistribuitType(examNum, gradeNum, subjectNum);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String yuchuliAutoDistribiutInfo(String examNum, String gradeNum, String subjectNum, String type) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Object exampaperNum = this.dao._queryObject("select pexampaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        new ArrayList();
        List<RowArg> rowArgList = new ArrayList<>();
        if (type.equals("1")) {
            List<Map<String, Object>> list = this.dao._queryMapList("select d.exampaperNum,d.groupNum,ifnull(t.c_u,0) c_u FROM distributeauto d LEFT JOIN (select ex.exampaperNum,qu.groupNum,count(qu.userNum) c_u FROM questiongroup_user qu LEFT JOIN exampaper ex ON qu.exampaperNum = ex.examPaperNum where ex.exampaperNum={exampaperNum} GROUP BY qu.groupNum) t on d.exampaperNum = t.exampaperNum and d.groupNum = t.groupNum where d.subjectNum= {subjectNum}  ", null, args);
            for (Map<String, Object> map : list) {
                if (Integer.parseInt(map.get("c_u") + "") == 0) {
                    Map args2 = new HashMap();
                    args2.put("exampaperNum", map.get("exampaperNum"));
                    args2.put("groupNum", map.get("groupNum"));
                    rowArgList.add(new RowArg("delete from distributeauto where exampaperNum={exampaperNum} and groupNum={groupNum}", args2));
                }
            }
        } else if (type.equals("2")) {
            List<Map<String, Object>> list2 = this.dao._queryMapList("select d.exampaperNum,d.groupNum,ifnull(t.c_u,0) c_u FROM distributeauto d LEFT JOIN (select ex.exampaperNum,qu.groupNum,count(qu.userNum) c_u FROM questiongroup_user qu LEFT JOIN exampaper ex ON qu.exampaperNum = ex.examPaperNum where ex.exampaperNum={exampaperNum}  GROUP BY qu.groupNum) t on d.exampaperNum = t.exampaperNum and d.groupNum = t.groupNum where d.subjectNum= {subjectNum} ", null, args);
            for (Map<String, Object> map2 : list2) {
                if (Integer.parseInt(map2.get("c_u") + "") == 0) {
                    Map args22 = new HashMap();
                    args22.put("exampaperNum", map2.get("exampaperNum"));
                    args22.put("groupNum", map2.get("groupNum"));
                    rowArgList.add(new RowArg("delete from distributeauto where exampaperNum={exampaperNum}  and groupNum={groupNum} ", args22));
                } else {
                    Map args23 = new HashMap();
                    args23.put("exampaperNum", map2.get("exampaperNum"));
                    args23.put("groupNum", map2.get("groupNum"));
                    args23.put("member", map2.get("c_u"));
                    rowArgList.add(new RowArg("update distributeauto set member={member} where exampaperNum={exampaperNum}  and groupNum={groupNum} ", args23));
                }
            }
        } else if (type.equals("3")) {
            args.put("exampaperNum", exampaperNum);
            List<Map<String, Object>> list3 = this.dao._queryMapList("select d.exampaperNum,d.schoolNum,d.groupNum,ifnull(t.c_u,0) c_u FROM distributeauto d LEFT JOIN (select ex.exampaperNum,u.schoolnum,qu.groupNum,count(qu.userNum) c_u FROM questiongroup_user qu LEFT JOIN user u ON qu.userNum=u.id and u.usertype='1' LEFT JOIN exampaper ex ON qu.exampaperNum = ex.examPaperNum where ex.exampaperNum={exampaperNum} GROUP BY qu.groupNum,u.schoolNum) t on d.exampaperNum = t.exampaperNum and d.groupNum = t.groupNum and d.schoolNum = t.schoolNum where d.subjectNum= {subjectNum} ", null, args);
            for (Map<String, Object> map3 : list3) {
                if (Integer.parseInt(map3.get("c_u") + "") == 0) {
                    Map args24 = new HashMap();
                    args24.put("exampaperNum", map3.get("exampaperNum"));
                    args24.put("groupNum", map3.get("groupNum"));
                    args24.put(Const.EXPORTREPORT_schoolNum, map3.get(Const.EXPORTREPORT_schoolNum));
                    rowArgList.add(new RowArg("delete from distributeauto where exampaperNum={exampaperNum} and groupNum={groupNum}  and schoolNum={schoolNum} ", args24));
                }
            }
        }
        this.dao._batchExecute(rowArgList);
        return "T";
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String rewrightCount(String exampaperNum, String user) {
        String poistionStr1 = "";
        String poistionStr2 = "";
        Map args = new HashMap();
        if (user != null && !user.equals("-1") && !user.equals("-2")) {
            poistionStr1 = "LEFT JOIN (select DISTINCT schoolNum FROM schauthormanage where userId={user} UNION select schoolNum from user where id={user} ) t1 on u.schoolnum = t1.schoolNum ";
            poistionStr2 = " where t1.schoolNum is not null ";
            args.put("user", user);
        }
        String sql = "select ifnull(ur.c_u,0) from (select r.exampaperNum,count(ur.userNum) c_u from userrole ur left join role r on ur.roleNum=r.roleNum and r.type='4' left join user u on ur.userNum=u.id " + poistionStr1 + poistionStr2 + "group by r.exampaperNum union select us.exampaperNum,count(us.userNum) from userrole_sub us left join user u on us.userNum = u.id " + poistionStr1 + poistionStr2 + "group by exampaperNum ) ur where ur.exampaperNum={exampaperNum} ";
        args.put("exampaperNum", exampaperNum);
        String c_u = this.dao._queryStr(sql, args);
        if (c_u == null || c_u.equals("null") || c_u.equals("")) {
            c_u = "0";
        }
        return c_u;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getYifenpeiSchoolInfo(String leiceng, String user, String exam) {
        String poistionStr1 = "";
        String poistionStr2 = "";
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("userNum", user);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userNum} and type=1 limit 1", args);
        if (user != null && !user.equals("-1") && !user.equals("-2") && null == map) {
            poistionStr1 = "LEFT JOIN (select DISTINCT schoolNum FROM schoolscanpermission where userNum={userNum} UNION select schoolNum from user where id={userNum} ) t1 on cast(u.schoolnum as char) = cast(t1.schoolNum  as char) right join examschool es on es.examNum={exam} and cast(es.schoolNum as char)=cast(t1.schoolNum as char) ";
            poistionStr2 = " and t1.schoolNum is not null ";
            args.put("user", user);
        }
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null && !leiceng.equals("")) {
            leicengStr1 = " LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leiceng}  and statisticItem='01' ) t2 ON u.schoolNum = t2.schoolNum ";
            leicengStr2 = " and t2.schoolNum is not null ";
            args.put("leiceng", leiceng);
        }
        String sql = "select DISTINCT s.id,s.schoolName from userrole ur LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN school s ON u.schoolNum = s.id AND s.isDelete='F' " + poistionStr1 + leicengStr1 + "where s.id is not null " + poistionStr2 + leicengStr2 + " order by s.schoolNum ";
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getYifenpeiListInfo(String exam, String grade, String subject, String leiceng, String school, String type, String user) {
        Map args = new HashMap();
        String gradeStr = "";
        if (grade != null && !grade.equals("") && !grade.equals("-1")) {
            gradeStr = " and ex.gradeNum={grade} ";
            args.put("grade", grade);
        }
        String subjectStr = "";
        if (subject != null && !subject.equals("") && !subject.equals("-1")) {
            subjectStr = " and ex.subjectNum={subject}  ";
            args.put("subject", subject);
        }
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null && !leiceng.equals("") && (school == null || school.equals("-1") || school.equals(""))) {
            leicengStr1 = " LEFT JOIN (select DISTINCT sItemId schoolNum from statisticitem_school where topItemId={leiceng}  and statisticItem='01' ) t2 ON u.schoolNum = t2.schoolNum ";
            leicengStr2 = " and t2.schoolNum is not null ";
            args.put("leiceng", leiceng);
        }
        String schoolStr = "";
        if (school != null && !school.equals("") && !school.equals("-1")) {
            schoolStr = " and u.schoolNum={school} ";
            args.put(License.SCHOOL, school);
        }
        String poistionStr1 = "";
        if (user != null && !user.equals("-1") && !user.equals("-2")) {
            poistionStr1 = "INNER JOIN (select DISTINCT schoolNum FROM schauthormanage where userId={user}  UNION select schoolNum from user where id={user} ) t1 on u.schoolnum = t1.schoolNum ";
            args.put("user", user);
        }
        String sql = type.equals("0") ? "select IFNULL(s.schoolName,'')schoolName,IFNULL(u.realname,'')realname,sj.subjectName,IFNULL(g.gradeName,'')gradeName,IFNULL(u.username,'') username FROM (select ur.userNum,r.exampaperNum from userrole ur LEFT JOIN role r on ur.roleNum = r.roleNum where r.type='4'  union select userNum,exampaperNum from userrole_sub ) ur LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN exampaper ex on ur.examPaperNum = ex.examPaperNum LEFT JOIN school s ON u.schoolnum = s.id LEFT JOIN `subject` sj ON ex.subjectNum = sj.subjectNum  LEFT JOIN basegrade g on ex.gradeNum = g.gradeNum LEFT JOIN (select pexamPaperNum examPaperNum,COUNT(pexamPaperNum) FROM exampaper GROUP BY pexamPaperNum HAVING COUNT(pexamPaperNum)>1) t ON ex.examPaperNum = t.examPaperNum " + leicengStr1 + poistionStr1 + "where t.examPaperNum is null AND sj.isDelete='F' AND g.isDelete='F' and ex.examNum=" + exam + gradeStr + subjectStr + leicengStr2 + schoolStr + " order by s.schoolName DESC,u.realname,g.gradeNum DESC,sj.orderNum " : "select sj.subjectName,IFNULL(s.schoolName,'')schoolName,IFNULL(u.realname,'')realname,IFNULL(g.gradeName,'')gradeName,IFNULL(u.username,'') username FROM (select ur.userNum,r.exampaperNum from userrole ur LEFT JOIN role r on ur.roleNum = r.roleNum where r.type='4'  union select userNum,exampaperNum from userrole_sub ) ur LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN exampaper ex on ur.examPaperNum = ex.examPaperNum LEFT JOIN school s ON u.schoolnum = s.id LEFT JOIN `subject` sj ON ex.subjectNum = sj.subjectNum LEFT JOIN basegrade g on ex.gradeNum = g.gradeNum LEFT JOIN (select pexamPaperNum examPaperNum,COUNT(pexamPaperNum) FROM exampaper GROUP BY pexamPaperNum HAVING COUNT(pexamPaperNum)>1) t ON ex.examPaperNum = t.examPaperNum " + leicengStr1 + poistionStr1 + "where t.examPaperNum is null AND sj.isDelete='F' AND g.isDelete='F' and ex.examNum=" + exam + gradeStr + subjectStr + leicengStr2 + schoolStr + " order by sj.orderNum,schoolName DESC,g.gradeNum DESC";
        return this.dao._queryMapList(sql, null, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getLeicengInfo(String user) {
        String sql1;
        new ArrayList();
        Map args = new HashMap();
        args.put("user", user);
        Object oo = this.dao._queryObject("select SUM(if(sc.schoolNum is null,1,0)) s_schoolNum from (select DISTINCT sItemId from statisticitem_school  where statisticItem = '01' ) ss  inner join school sch on sch.id=ss.sItemId and sch.isDelete='F' LEFT JOIN schoolscanpermission sc ON ss.sItemId = sc.schoolNum AND sc.userNum={user} ", args);
        String schoolCount = oo == null ? "0" : oo.toString();
        if (!user.equals("-2") && !user.equals("-1") && Integer.parseInt(schoolCount) > 0) {
            sql1 = "select ss.pItemId id,ss.topItemId pId,ss.pItemName name, statisticitem from (select * from statisticitem_school where `Level`='1') ss LEFT JOIN schoolscanpermission sc ON ss.sItemId = sc.schoolNum AND sc.userNum= {user} GROUP BY ss.pItemId having SUM(if(sc.schoolNum is null,1,0))=0 ";
        } else {
            sql1 = "select ss.pItemId id,ss.topItemId pId,ss.pItemName name, statisticitem  from statisticitem_school ss where `Level`='1' GROUP BY ss.pItemId UNION select pItemId,topItemId,pItemName,statisticitem FROM statisticitem_school where topItemId = rootId  GROUP BY topItemId  ";
        }
        List<Map<String, Object>> list = this.dao._queryMapList(sql1, null, args);
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Object> getYuejuanCountList(String exam, String grade, String leicengid, String level) {
        Map args = new HashMap();
        String sql = "select ss.topItemId id,ss.topItemName name,ifnull(en.s_s,'')s_s,exp.subjectNum,s.subjectName,ifnull(rs.reserveNum,'')reserveNum,IFNULL(rs.bili,'')bili,COUNT(DISTINCT u.id) c_s,t2.statisticItem FROM statisticitem_school ss LEFT JOIN exampaper exp ON 1=1 LEFT JOIN (select ur.userNum,r.examPaperNum from userrole ur  LEFT JOIN role r ON ur.roleNum = r.roleNum  where r.type=4 UNION select userNum,exampaperNum from userrole_sub) ur ON ur.examPaperNum = exp.examPaperNum LEFT JOIN `user` u ON ur.userNum = u.id AND u.schoolnum = ss.sItemId LEFT JOIN `subject` s ON exp.subjectNum = s.subjectNum LEFT JOIN (select ss.topItemId,ss.topItemName,COUNT(en.studentId) s_s,en.subjectNum,e.examPaperNum FROM examinationnum en LEFT JOIN exampaper e ON en.gradeNum = e.gradeNum and en.examNum = e.examNum and en.subjectNum = e.subjectNum inner JOIN statisticitem_school ss ON en.schoolNum = ss.sItemId where en.examNum={exam} and en.gradeNum={grade} GROUP BY ss.topItemId,en.subjectNum) en ON ss.topItemId = en.topItemId  AND exp.pexamPaperNum = en.examPaperNum LEFT JOIN (select ItemId,reserveNum,bili,examNum,gradeNum,subjectNum,statisticitem FROM reserveyuejuannum where examNum={exam} and gradeNum={grade} AND itemid <> rootid UNION select pItemId,SUM(reserveNum)reserveNum,bili,examNum,gradeNum,subjectNum,statisticitem FROM reserveyuejuannum where itemid <> rootid GROUP BY pItemid,examNum,gradeNum,subjectNum) rs ON exp.examNum = rs.examNum and exp.gradeNum = rs.gradeNum and exp.subjectNum = rs.subjectNum AND ss.topItemId = rs.ItemId  AND rs.statisticitem= {level} LEFT JOIN (select pexamPaperNum from exampaper where examPaperNum <> pexamPaperNum ) pexp ON exp.examPaperNum=pexp.pexamPaperNum LEFT JOIN (select pItemId,pItemName FROM statisticitem_school where topItemId={leicengid} AND `Level`<2 GROUP BY pItemId) t ON ss.topItemId = t.pItemId LEFT JOIN (select statisticitem, sitemid FROM statisticitem_school GROUP BY sItemId ) t2 ON ss.topItemId = t2.sitemid where exp.examNum={exam} and exp.gradeNum={grade} and ss.statisticItem='01' AND pexp.pexamPaperNum is null AND t.pItemId is not null GROUP BY ss.topItemId,exp.subjectNum UNION ";
        args.put("exam", exam);
        args.put("grade", grade);
        args.put("level", level);
        args.put("leicengid", leicengid);
        List<Map<String, Object>> list = this.dao._queryMapList(sql + "select ss.sItemId id ,ss.sItemName name,ifnull(en.s_s,'')s_s,exp.subjectNum,s.subjectName,ifnull(rs.reserveNum,'')reserveNum,IFNULL(rs.bili,'')bili,COUNT(DISTINCT u.id) c_s,t2.statisticItem FROM statisticitem_school ss LEFT JOIN exampaper exp ON 1=1 LEFT JOIN (select ur.userNum,r.examPaperNum from userrole ur LEFT JOIN role r ON ur.roleNum = r.roleNum where r.type=4 UNION select userNum,exampaperNum from userrole_sub) ur ON ur.examPaperNum = exp.examPaperNum LEFT JOIN `user` u ON ur.userNum = u.id AND u.schoolnum = ss.sItemId LEFT JOIN `subject` s ON exp.subjectNum = s.subjectNum LEFT JOIN (select COUNT(en.studentId) s_s,en.schoolnum,e.examPaperNum FROM examinationnum en LEFT JOIN exampaper e ON en.gradeNum = e.gradeNum and en.examNum = e.examNum and en.subjectNum = e.subjectNum where en.examNum={exam} and en.gradeNum={grade} GROUP BY en.schoolnum,en.subjectNum) en ON ss.sItemId = en.schoolNum  AND exp.pexamPaperNum = en.examPaperNum LEFT JOIN (select ItemId,reserveNum,bili,examNum,gradeNum,subjectNum FROM reserveyuejuannum where pItemId={leicengid} UNION select pItemId,SUM(reserveNum)reserveNum,bili,examNum,gradeNum,subjectNum FROM reserveyuejuannum where pItemId={leicengid} GROUP BY pItemid) rs ON exp.examNum = rs.examNum and exp.gradeNum = rs.gradeNum and exp.subjectNum = rs.subjectNum AND ss.sItemId = rs.ItemId  LEFT JOIN (select pexamPaperNum from exampaper where examPaperNum <> pexamPaperNum ) pexp ON exp.examPaperNum=pexp.pexamPaperNum LEFT JOIN (select sItemId,sItemName FROM statisticitem_school where pItemId={leicengid} GROUP BY sItemId) t ON ss.sItemId = t.sItemId LEFT JOIN (select statisticitem, sitemid FROM statisticitem_school GROUP BY sItemId ) t2 ON ss.sItemId = t2.sitemid where exp.examNum={exam} and exp.gradeNum={grade} and ss.statisticItem='01' AND pexp.pexamPaperNum is null  AND t.sItemId  is not null GROUP BY ss.sItemId,exp.subjectNum ", null, args);
        List<Map<String, Object>> schlist = this.dao._queryMapList("SELECT sch.id,IFNULL(s.num,0) num from school sch  LEFT JOIN (SELECT id,gradeNum,schoolNum,count(1) num from  student where isDelete='F' AND gradeNum={grade}  GROUP BY schoolNum ) s ON s.schoolNum=sch.id WHERE sch.isDelete='F' AND IFNULL(s.num,0)=0 ", null, args);
        for (Map<String, Object> map : schlist) {
            String num = Convert.toStr(map.get("id"));
            list.removeIf(o -> {
                return num.equals(Convert.toStr(o.get("id"))) && "01".equals(Convert.toStr(o.get("statisticItem")));
            });
        }
        String flag = "F";
        List<Map<String, Object>> list_rootId = this.dao._queryMapList("select rootId from statisticitem_school group by rootId ", TypeEnum.StringObject, args);
        for (Map<String, Object> map2 : list_rootId) {
            if (map2.get("rootId").equals(leicengid)) {
                flag = "T";
            }
        }
        Object list2 = this.dao._queryMapList("select subjectNum , reserveNum FROM reserveyuejuannum where examNum={exam} AND gradeNum={grade} AND ItemId={leicengid}  ", TypeEnum.StringObject, args);
        List<Object> list1 = new ArrayList<>();
        list1.add(list);
        if (!flag.equals("T")) {
            list1.add(list2);
        }
        return list1;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String setYuding(String exam, String gradeNum, String subjectNum, String yuding, String leiceng, String pid, String statisticitem, String bili, String loginName, String total) {
        Map args = new HashMap();
        String flag = "F";
        args.put("exam", exam);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("leiceng", leiceng);
        args.put("loginName", loginName);
        args.put(Const.score_total, total);
        this.dao._execute("delete from reserveyuejuannum where examNum={exam} and gradeNum={gradeNum} and subjectNum={subjectNum} and (ItemId={leiceng} or pItemId={leiceng} )", args);
        args.put("pid", pid);
        String rootId = this.dao._queryStr("select rootId from statisticitem_school where topitemId={pid} ", args);
        try {
            if (rootId.equals(pid) && !total.equals("0")) {
                this.dao._execute("delete from reserveyuejuannum where examNum={exam} and gradeNum={gradeNum} and subjectNum={subjectNum} and itemId={pid} ", args);
                this.dao._execute("insert into reserveyuejuannum(rootId,pitemId,itemId,statisticItem,examNum,gradeNum,subjectNum,reserveNum,insertUser,insertDate) value ({pid},{pid} ,{pid} ,'00',{exam},{gradeNum},{subjectNum},{total},{loginName},now())", args);
            }
            if (!yuding.equals("")) {
                args.put("rootId", rootId);
                args.put("statisticitem", statisticitem);
                args.put("yuding", yuding);
                args.put("bili", bili);
                this.dao._execute("insert into reserveyuejuannum(rootId,pitemId,itemId,statisticItem,examNum,gradeNum,subjectNum,reserveNum,bili,insertUser,insertDate) value({rootId},{pid},{leiceng},{statisticitem},{exam},{gradeNum},{subjectNum},{yuding},{bili},{loginName},now() )", args);
            }
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String autoSetYuding(String exam, String gradeNum, String subjectNum, String leiceng, String statisticitem, String bili, String loginName, String total) {
        Map args = new HashMap();
        String flag = "F";
        String deleteSql = "delete from reserveyuejuannum where examNum={exam} and gradeNum={gradeNum} and subjectNum={subjectNum} ";
        args.put("exam", exam);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        if (statisticitem.equals("01")) {
            deleteSql = deleteSql + " and statisticItem='01' and pitemId={leiceng} ";
        }
        this.dao._execute(deleteSql, args);
        args.put("leiceng", leiceng);
        String rootId = this.dao._queryStr("select rootId from statisticitem_school where topitemId={leiceng}", args);
        try {
            if (rootId.equals(leiceng) && !total.equals("0")) {
                this.dao._execute("delete from reserveyuejuannum where examNum={exam} and gradeNum={gradeNum} and subjectNum={subjectNum} and itemId={leiceng} ", args);
                args.put(Const.score_total, total);
                args.put("loginName", loginName);
                this.dao._execute("insert into reserveyuejuannum(rootId,pitemId,itemId,statisticItem,examNum,gradeNum,subjectNum,reserveNum,insertUser,insertDate) value ({leiceng} ,{leiceng},{leiceng},'00',{exam},{gradeNum},{subjectNum},{total},{loginName},now())", args);
            }
            if (!bili.equals("0")) {
                String sql = "insert into reserveyuejuannum(rootId,pitemId,itemId,statisticItem,examNum,gradeNum,subjectNum,reserveNum,bili,insertUser,insertDate) select  '" + rootId + "','" + leiceng + "',ss.sItemId,'" + statisticitem + "','" + exam + "','" + gradeNum + "','" + subjectNum + "',ceil(COUNT(en.studentId)/" + bili + ") ,'" + bili + "'," + loginName + ",now() FROM examinationnum en LEFT JOIN exampaper e on en.examNum = e.examNum AND en.gradeNum = e.gradeNum and en.subjectNum = e.subjectNum left JOIN exampaper e2 ON e.examPaperNum = e2.pexamPaperNum  LEFT JOIN (select pexamPaperNum expNum from exampaper where examPaperNum<> pexamPaperNum and examNum ={exam}   AND gradeNum = {gradeNum} AND subjectNum = {subjectNum} ) p ON e2.examPaperNum = p.expNum inner JOIN statisticitem_school ss ON en.schoolNum = ss.sItemId where ss.statisticItem='01' and p.expNum is null and e2.examNum={exam} and e2.gradeNum={gradeNum} and e2.subjectNum = {subjectNum} and ss.topItemId = {leiceng} and ss.topItemId=ss.pitemId GROUP BY ss.sItemId ";
                this.dao._execute(sql, args);
                String sql2 = "insert into reserveyuejuannum(rootId,pitemId,itemId,statisticItem,examNum,gradeNum,subjectNum,reserveNum,bili,insertUser,insertDate) select   '" + rootId + "','" + leiceng + "',ss.topItemId,'" + statisticitem + "','" + exam + "','" + gradeNum + "','" + subjectNum + "',ceil(COUNT(en.studentId)/" + bili + ") ,'" + bili + "'," + loginName + ",now() FROM examinationnum en LEFT JOIN exampaper e on en.examNum = e.examNum AND en.gradeNum = e.gradeNum and en.subjectNum = e.subjectNum left JOIN exampaper e2 ON e.examPaperNum = e2.pexamPaperNum  LEFT JOIN (select pexamPaperNum expNum from exampaper where examPaperNum<> pexamPaperNum and examNum = {exam} AND gradeNum = {gradeNum}  AND subjectNum ={subjectNum}   ) p ON e2.examPaperNum = p.expNum inner JOIN (select ss2.* from statisticitem_school ss LEFT JOIN statisticitem_school ss2 ON ss.sItemId = ss2.topItemId where ss.topItemId = ss.pItemId and ss.topItemId={leiceng} and ss.statisticItem='00' AND ss2.statisticItem='01') ss ON en.schoolNum = ss.sItemId where p.expNum is null and e2.examNum={exam}  and e2.gradeNum={gradeNum} and e2.subjectNum = {subjectNum}  GROUP BY ss.topItemId ";
                this.dao._execute(sql2, args);
            }
            flag = "T";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String exportYuejuanCountList(String exam, String gradeNum, String leiceng, String level, String dirPath, String uid, String examName, String gradeName, String leicengName) {
        String excelName = "阅卷员人数设定_" + uid;
        File excelFile = getRptExcelFile(excelName, dirPath, "ExportFolder/YueJuanCountSet");
        String filePath = "ExportFolder/YueJuanCountSet/" + excelName + ".xls";
        List<Object> list1 = getYuejuanCountList(exam, gradeNum, leiceng, level);
        List<Map<String, Object>> list = (List) list1.get(0);
        try {
            WritableWorkbook wwBook = Workbook.createWorkbook(excelFile);
            WritableFont titles = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat title = new WritableCellFormat(titles);
            title.setAlignment(Alignment.LEFT);
            title.setVerticalAlignment(VerticalAlignment.CENTRE);
            title.setWrap(true);
            WritableFont font1 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormat1 = new WritableCellFormat(font1);
            cFormat1.setAlignment(Alignment.CENTRE);
            cFormat1.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            cFormat1.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            cFormat1.setBorder(Border.LEFT, BorderLineStyle.THIN);
            cFormat1.setBorder(Border.TOP, BorderLineStyle.THIN);
            cFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);
            cFormat1.setWrap(true);
            WritableFont font2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormat2 = new WritableCellFormat(font2);
            cFormat2.setAlignment(Alignment.CENTRE);
            cFormat2.setVerticalAlignment(VerticalAlignment.CENTRE);
            cFormat2.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            cFormat2.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            cFormat2.setBorder(Border.LEFT, BorderLineStyle.THIN);
            cFormat2.setBorder(Border.TOP, BorderLineStyle.THIN);
            cFormat2.setWrap(true);
            WritableFont font3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat cFormat3 = new WritableCellFormat(font3);
            cFormat3.setAlignment(Alignment.CENTRE);
            cFormat3.setVerticalAlignment(VerticalAlignment.CENTRE);
            cFormat3.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            cFormat3.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            cFormat3.setBorder(Border.LEFT, BorderLineStyle.THIN);
            cFormat3.setBorder(Border.TOP, BorderLineStyle.THIN);
            cFormat3.setWrap(true);
            try {
                WritableSheet sheet = wwBook.createSheet("阅卷员人数设定", 0);
                sheet.getSettings().setShowGridLines(false);
                sheet.setColumnView(0, 17);
                sheet.setColumnView(1, 15);
                sheet.setColumnView(2, 15);
                sheet.setColumnView(3, 15);
                sheet.setColumnView(4, 15);
                sheet.setColumnView(5, 15);
                sheet.setColumnView(6, 15);
                sheet.setColumnView(7, 15);
                sheet.setColumnView(8, 15);
                sheet.setColumnView(9, 15);
                sheet.setColumnView(10, 15);
                sheet.setColumnView(11, 15);
                if (list.size() > 0) {
                    String titleName = "考试：" + examName + " ；年级：" + gradeName + " ；类层：" + leicengName;
                    sheet.mergeCells(0, 0, 6, 0);
                    Label xinxi = new Label(0, 0, titleName, title);
                    sheet.addCell(xinxi);
                    Label gradeTitle = new Label(0, 1, "类层", cFormat1);
                    sheet.addCell(gradeTitle);
                    sheet.mergeCells(0, 1, 0, 3);
                    Label subjectTitle = new Label(1, 1, "参考人数", cFormat1);
                    sheet.addCell(subjectTitle);
                    sheet.mergeCells(1, 1, 1, 3);
                    String biliFlag = list.get(0).get("id") + "";
                    int subjectCount = 0;
                    int i = 0;
                    while (true) {
                        if (i >= list.size()) {
                            break;
                        }
                        Map<String, Object> map = list.get(i);
                        if (biliFlag.equals(map.get("id"))) {
                            Label subject = new Label(2 + (i * 2), 1, map.get("subjectName") + "", cFormat1);
                            sheet.addCell(subject);
                            sheet.mergeCells(2 + (i * 2), 1, 3 + (i * 2), 1);
                            if (!map.get("bili").equals("")) {
                                Label bili = new Label(2 + (i * 2), 2, "每人阅" + map.get("bili") + "人", cFormat1);
                                sheet.addCell(bili);
                            } else {
                                Label bili2 = new Label(2 + (i * 2), 2, "每人阅-人", cFormat1);
                                sheet.addCell(bili2);
                            }
                            sheet.mergeCells(2 + (i * 2), 2, 3 + (i * 2), 2);
                            Label yuding = new Label(2 + (i * 2), 3, "预定", cFormat1);
                            sheet.addCell(yuding);
                            Label shiji = new Label(3 + (i * 2), 3, "实际", cFormat1);
                            sheet.addCell(shiji);
                            i++;
                        } else {
                            subjectCount = i;
                            break;
                        }
                    }
                    int rows = 3;
                    int c_l = 0;
                    int maxRow = (list.size() / subjectCount) + 3;
                    String biliFlag2 = "";
                    for (int i2 = 0; i2 < list.size(); i2++) {
                        Map<String, Object> map2 = list.get(i2);
                        if (i2 % 2 == 0) {
                            if (map2.get("id").equals(leiceng)) {
                                if (!biliFlag2.equals(map2.get("id"))) {
                                    c_l++;
                                    Label name = new Label(0, maxRow, "合计", cFormat2);
                                    sheet.addCell(name);
                                    Label numOfStudent = new Label(1, maxRow, map2.get("s_s") + "", cFormat2);
                                    sheet.addCell(numOfStudent);
                                }
                                Number yuding2 = new Number(2 + ((i2 - ((c_l - 1) * subjectCount)) * 2), maxRow, map2.get("reserveNum").equals("") ? 0.0d : Integer.parseInt(map2.get("reserveNum") + ""), cFormat2);
                                sheet.addCell(yuding2);
                                Number shiji2 = new Number(3 + ((i2 - ((c_l - 1) * subjectCount)) * 2), maxRow, map2.get("c_s").equals("") ? 0.0d : Integer.parseInt(map2.get("c_s") + ""), cFormat2);
                                sheet.addCell(shiji2);
                            } else {
                                if (!biliFlag2.equals(map2.get("id"))) {
                                    rows++;
                                    c_l++;
                                    Label name2 = new Label(0, rows, map2.get("name") + "", cFormat2);
                                    sheet.addCell(name2);
                                    Label numOfStudent2 = new Label(1, rows, map2.get("s_s") + "", cFormat2);
                                    sheet.addCell(numOfStudent2);
                                }
                                Number yuding3 = new Number(2 + ((i2 - ((c_l - 1) * subjectCount)) * 2), rows, map2.get("reserveNum").equals("") ? 0.0d : Integer.parseInt(map2.get("reserveNum") + ""), cFormat2);
                                sheet.addCell(yuding3);
                                Number shiji3 = new Number(3 + ((i2 - ((c_l - 1) * subjectCount)) * 2), rows, map2.get("c_s").equals("") ? 0.0d : Integer.parseInt(map2.get("c_s") + ""), cFormat2);
                                sheet.addCell(shiji3);
                            }
                        } else if (map2.get("id").equals(leiceng)) {
                            if (!biliFlag2.equals(map2.get("id"))) {
                                c_l++;
                                Label name3 = new Label(0, maxRow, "合计", cFormat3);
                                sheet.addCell(name3);
                                Label numOfStudent3 = new Label(1, maxRow, map2.get("s_s") + "", cFormat3);
                                sheet.addCell(numOfStudent3);
                            }
                            Number yuding4 = new Number(2 + ((i2 - ((c_l - 1) * subjectCount)) * 2), maxRow, map2.get("reserveNum").equals("") ? 0.0d : Integer.parseInt(map2.get("reserveNum") + ""), cFormat3);
                            sheet.addCell(yuding4);
                            Number shiji4 = new Number(3 + ((i2 - ((c_l - 1) * subjectCount)) * 2), maxRow, map2.get("c_s").equals("") ? 0.0d : Integer.parseInt(map2.get("c_s") + ""), cFormat3);
                            sheet.addCell(shiji4);
                        } else {
                            if (!biliFlag2.equals(map2.get("id"))) {
                                rows++;
                                c_l++;
                                Label name4 = new Label(0, rows, map2.get("name") + "", cFormat3);
                                sheet.addCell(name4);
                                Label numOfStudent4 = new Label(1, rows, map2.get("s_s") + "", cFormat3);
                                sheet.addCell(numOfStudent4);
                            }
                            Number yuding5 = new Number(2 + ((i2 - ((c_l - 1) * subjectCount)) * 2), rows, map2.get("reserveNum").equals("") ? 0.0d : Integer.parseInt(map2.get("reserveNum") + ""), cFormat3);
                            sheet.addCell(yuding5);
                            Number shiji5 = new Number(3 + ((i2 - ((c_l - 1) * subjectCount)) * 2), rows, map2.get("c_s").equals("") ? 0.0d : Integer.parseInt(map2.get("c_s") + ""), cFormat3);
                            sheet.addCell(shiji5);
                        }
                        biliFlag2 = map2.get("id") + "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            wwBook.write();
            wwBook.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return filePath;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getAllSubjects(String exam, String grade) {
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade);
        List<Map<String, Object>> list = this.dao._queryMapList("select e1.subjectNum,sub.subjectName from exampaper e1 left join (select pexampaperNum from exampaper where exampaperNum <> pexampaperNum) e2 on e1.exampaperNum=e2.pexampaperNum left join subject sub on sub.subjectNum=e1.subjectNum where e2.pexampaperNum is null and e1.examNum={exam} and e1.gradeNum={grade} order by sub.orderNum", TypeEnum.StringObject, args);
        return list;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public File getRptExcelFile(String rpt_name, String root, String folderPath) {
        if (null != folderPath && !folderPath.equals("")) {
            root = root + folderPath + File.separator;
        }
        File dir = new File(root);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String path = dir + File.separator + rpt_name + ".xls";
        File excelFile = new File(path);
        try {
            if (!excelFile.exists()) {
                excelFile.createNewFile();
            } else {
                excelFile.delete();
                excelFile.createNewFile();
            }
        } catch (Exception e) {
            this.log.info("##---create文件.file.", e);
        }
        return excelFile;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getLeicengInfoByLeiceng(String leiceng) {
        Map args = StreamMap.create().put("leiceng", (Object) leiceng);
        return this.dao._queryMapList("select topItemId id,topItemName name,statisticItem ss FROM statisticitem_school where statisticItem='01' AND level=0  and topItemId={leiceng} GROUP BY topItemId UNION select topItemId,topItemName,statisticItem FROM statisticitem_school where statisticItem='00' AND level=0  and topItemId={leiceng}  GROUP BY topItemId ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getPLeicengYudingCount(String exam, String grade, String leicengid, String subjectNum) {
        List<Map<String, Object>> list = this.dao.queryMapList("select rootId from statisticitem_school group by rootId ");
        for (Map<String, Object> map : list) {
            if (map.get("rootId").equals(leicengid)) {
                return "-1";
            }
        }
        Map args = StreamMap.create().put("exam", (Object) exam).put("grade", (Object) grade).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put("leicengid", (Object) leicengid);
        String reserveNum = this.dao._queryStr("select ifnull(rs.reserveNum,0)reserveNum from reserveyuejuannum rs  left join (select rootId from statisticitem_school group by rootId) t on rs.itemId = t.rootId where t.rootId is null and rs.examNum={exam}  and rs.gradeNum={grade}  and rs.subjectNum={subjectNum}  and rs.itemid={leicengid} ", args);
        return reserveNum;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String getPLeicengYuding(String exam, String grade, String leicengid, String subjectNum) {
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("grade", grade);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("leicengid", leicengid);
        String pid = this.dao._queryStr("select pitemid from reserveyuejuannum rs where rs.examNum={exam} and rs.gradeNum={grade} and rs.subjectNum={subjectNum} and rs.itemid={leicengid} ", args);
        if ("null".equals(pid) || null == pid || leicengid.equals(pid)) {
            return "-1";
        }
        String bili = this.dao._queryStr("select ifnull(rs.bili,0)bili from reserveyuejuannum rs  where  rs.examNum={exam} and rs.gradeNum={grade} and rs.subjectNum={subjectNum} and rs.itemid={leicengid} ", args);
        return bili;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String rewrightCount2(String school, String leiceng, String exampaperNum, String user) {
        String reserveNum;
        Map args = new HashMap();
        args.put(License.SCHOOL, school);
        args.put("leiceng", leiceng);
        args.put("user", user);
        String schoolStr = "";
        if (!school.equals("-1")) {
            schoolStr = " and u.schoolNum={school} ";
        }
        String leicengStr1 = "";
        String leicengStr2 = "";
        if (leiceng != null && school.equals("-1")) {
            leicengStr1 = "left join (select sItemId schoolNum,sItemName schoolName from statisticitem where topItemId={leiceng} AND statisticItem='01') t on u.schoolNum= t.schoolNum ";
            leicengStr2 = " and t.schoolNum is not null ";
        }
        String poistionStr1 = "";
        String poistionStr2 = "";
        if (user != null && !user.equals("-1") && !user.equals("-2")) {
            poistionStr1 = "LEFT JOIN (select DISTINCT schoolNum FROM schauthormanage where userId={user} UNION select schoolNum from user where id={user} ) t1 on u.schoolnum = t1.schoolNum ";
            poistionStr2 = " and t1.schoolNum is not null ";
        }
        String sql = "select ifnull(ur.c_u,0) from (select r.exampaperNum,count(ur.userNum) c_u from userrole ur left join role r on ur.roleNum=r.roleNum left join user u on ur.userNum=u.id " + poistionStr1 + leicengStr1 + "where 1=1 and r.type='4' " + poistionStr2 + leicengStr2 + schoolStr + "group by r.exampaperNum union select us.exampaperNum,count(us.userNum) from userrole_sub us left join user u on us.userNum = u.id " + poistionStr1 + leicengStr1 + "where 1=1 " + poistionStr2 + leicengStr2 + schoolStr + "group by exampaperNum ) ur where ur.exampaperNum={exampaperNum} ";
        args.put("exampaperNum", exampaperNum);
        String c_u = this.dao._queryStr(sql, args);
        if (c_u == null || c_u.equals("null") || c_u.equals("")) {
            c_u = "0";
        }
        String sql2 = "select ifnull(sum(reserveNum),0)reserveNum from reserveyuejuannum u LEFT JOIN exampaper exp ON u.examNum = exp.examNum AND u.gradeNum = exp.gradeNum AND u.subjectNum = exp.subjectNum  ";
        if (user != null && !user.equals("-1") && !user.equals("-2")) {
            if (!school.equals("-1")) {
                reserveNum = this.dao._queryStr((sql2 + "where  exp.exampaperNum={exampaperNum} ") + " and u.itemId={school}  and u.statisticitem='01' ", args);
            } else {
                String str = "";
                if (leiceng != null) {
                    sql2 = sql2 + "left join (select sItemId from statisticitem_school ss where topItemId = {leiceng} union select '" + leiceng + "' )t1 on u.itemid=t1.sItemId ";
                    str = " and t1.sItemId = t.pitemid ";
                }
                reserveNum = this.dao._queryStr(sql2 + "left join (SELECT t.pItemId FROM (select pItemId,pItemName,sItemId,sItemName from statisticitem_school ss where statisticItem='01' GROUP BY pItemId,sItemId) t INNER JOIN school sch on sch.id=t.sItemId and sch.isDelete='F' LEFT JOIN (select schoolNum from schauthormanage where userid={user} ) sc ON t.sItemId = sc.schoolNum GROUP BY t.pItemId HAVING SUM(IF(sc.schoolNum is null ,1,0))=0 UNION SELECT sc.schoolNum FROM (select pItemId,pItemName,sItemId,sItemName from statisticitem_school ss where statisticItem='01' GROUP BY pItemId,sItemId) t INNER JOIN school sch on sch.id=t.sItemId and sch.isDelete='F' LEFT JOIN(select schoolNum from schauthormanage where userid={user} ) sc ON t.sItemId = sc.schoolNum LEFT join (SELECT t.pItemId FROM (select pItemId,pItemName,sItemId,sItemName from statisticitem_school ss where statisticItem='01' GROUP BY pItemId,sItemId) t INNER JOIN school sch on sch.id=t.sItemId and sch.isDelete='F' LEFT JOIN (select schoolNum from schauthormanage where userid={user} ) sc ON t.sItemId = sc.schoolNum GROUP BY t.pItemId HAVING SUM(IF(sc.schoolNum is null ,1,0))=0 )t2 ON  t.pitemid = t2.pitemid where t2.pitemid is null and sc.schoolNum is not null) t on u.itemId=t.pitemid " + str + "where t.pitemid is not null and exp.exampaperNum={exampaperNum} ", args);
            }
        } else {
            String sql22 = sql2 + "where  exp.exampaperNum=" + exampaperNum;
            if (!school.equals("-1")) {
                reserveNum = this.dao._queryStr(sql22 + " and u.itemId={school} and u.statisticitem='01' ", args);
            } else if (leiceng != null) {
                String sql3 = sql22 + " and u.pitemId={leiceng}  ";
                Integer reserveNum1 = this.dao._queryInt(sql3, args);
                String sql4 = sql22 + " and u.itemId={leiceng} and u.statisticitem='00' ";
                Integer reserveNum2 = this.dao._queryInt(sql4, args);
                reserveNum = (reserveNum1.intValue() > reserveNum2.intValue() ? reserveNum1 : reserveNum2) + "";
            } else {
                reserveNum = this.dao.queryStr(sql22 + " and u.statisticitem='00' ", null);
            }
        }
        return c_u + "/" + reserveNum;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String isManYuan(String userNumStr, String exampaperNum) {
        String[] userNums = new String[0];
        if (userNumStr != null && !userNumStr.equals("")) {
            userNums = userNumStr.split(Const.STRING_SEPERATOR);
        }
        String str1 = "";
        int count1 = 0;
        Map<String, Integer> map = new HashMap<>();
        for (String str : userNums) {
            Map args = new HashMap();
            args.put("id", str);
            String schoolNum = this.dao._queryStr("SELECT schoolNum from user where id= {id} ", args);
            if (map.containsKey(schoolNum)) {
                map.put(schoolNum, Integer.valueOf(map.get(schoolNum).intValue() + 1));
            } else {
                map.put(schoolNum, 1);
            }
        }
        Set<String> set = map.keySet();
        for (String object : set) {
            Map args2 = new HashMap();
            args2.put("exampaperNum", exampaperNum);
            args2.put("object", object);
            AjaxData data = (AjaxData) this.dao._queryBean("SELECT ifnull(rs.reserveNum,'--') ext3,(ifnull(rs.reserveNum,0)-t.c_u) ext1,t.schoolNum ext2,s.schoolname name FROM ( select u.schoolnum,COUNT(ur.userNum) c_u,exp.subjectNum,exp.gradeNum,exp.examNum FROM userrole ur LEFT JOIN role r ON ur.roleNum = r.roleNum and r.type='4' LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN exampaper exp ON r.exampaperNum = exp.exampaperNum where r.examPaperNum ={exampaperNum} AND u.schoolnum={object}  GROUP BY u.schoolnum UNION select u.schoolnum,COUNT(ur.userNum) c_u,exp.subjectNum,exp.gradeNum,exp.examNum FROM userrole_sub ur LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN exampaper exp ON ur.exampaperNum = exp.exampaperNum where ur.exampaperNum = {exampaperNum}  AND u.schoolnum={object} GROUP BY u.schoolnum ) t LEFT JOIN reserveyuejuannum rs ON t.schoolNum = rs.itemid and rs.statisticItem='01' and t.examNum = rs.examNum and t.gradeNum = rs.gradeNum and t.subjectNum = rs.subjectNum LEFT JOIN school s ON t.schoolNum = s.id  ", AjaxData.class, args2);
            if (data != null && data.getExt3() != null && !"--".equals(data.getExt3())) {
                Integer ext1 = Integer.valueOf(Integer.parseInt(data.getExt1()));
                String schoolNum2 = data.getExt2();
                if (count1 < 3 && map.get(schoolNum2).intValue() > ext1.intValue()) {
                    count1++;
                    str1 = str1 + data.getName() + "、";
                } else if (count1 == 3 && map.get(schoolNum2).intValue() > ext1.intValue()) {
                    str1 = str1.substring(0, str1.length() - 1) + "...、";
                }
            }
        }
        if (!str1.equals("")) {
            str1 = str1.substring(0, str1.length() - 1);
        }
        return str1;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String isManYuan2(String exampaperNum, String user) {
        Map args = new HashMap();
        String sql = "SELECT group_concat(s.schoolname) name FROM ( select u.schoolnum,COUNT(ur.userNum) c_u,exp.subjectNum,exp.gradeNum,exp.examNum,exp.exampaperNum  FROM userrole ur LEFT JOIN role r ON ur.roleNum = r.roleNum and r.type='4' LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN exampaper exp ON r.exampaperNum = exp.exampaperNum LEFT JOIN (select DISTINCT schoolNum FROM schauthormanage where userId={user} UNION select schoolNum from user where id={user}) t on u.schoolnum = t.schoolNum  where r.examPaperNum ={exampaperNum} and t.schoolNum is not null GROUP BY u.schoolnum UNION select u.schoolnum,COUNT(ur.userNum) c_u,exp.subjectNum,exp.gradeNum,exp.examNum,exp.exampaperNum FROM userrole_sub ur LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN exampaper exp ON ur.exampaperNum = exp.exampaperNum LEFT JOIN (select DISTINCT schoolNum FROM schauthormanage where userId={user}  UNION select schoolNum from user where id={user} ) t on u.schoolnum = t.schoolNum  where ur.exampaperNum = {exampaperNum} and t.schoolNum is not null GROUP BY u.schoolnum ) t LEFT JOIN reserveyuejuannum rs ON t.schoolNum = rs.itemid and rs.statisticItem='01' and t.examNum = rs.examNum and t.gradeNum = rs.gradeNum and t.subjectNum = rs.subjectNum LEFT JOIN school s ON t.schoolNum = s.id  where t.c_u>rs.reserveNum GROUP BY t.exampaperNum ";
        args.put("user", user);
        args.put("exampaperNum", exampaperNum);
        if (user.equals("-1") || user.equals("-2")) {
            sql = "SELECT group_concat(s.schoolname) name FROM ( select u.schoolnum,COUNT(ur.userNum) c_u,exp.subjectNum,exp.gradeNum,exp.examNum,exp.exampaperNum  FROM userrole ur LEFT JOIN role r ON ur.roleNum = r.roleNum and r.type='4' LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN exampaper exp ON r.exampaperNum = exp.exampaperNum where r.examPaperNum ={exampaperNum}  GROUP BY u.schoolnum UNION select u.schoolnum,COUNT(ur.userNum) c_u,exp.subjectNum,exp.gradeNum,exp.examNum,exp.exampaperNum FROM userrole_sub ur LEFT JOIN user u ON ur.userNum = u.id LEFT JOIN exampaper exp ON ur.exampaperNum = exp.exampaperNum where ur.exampaperNum ={exampaperNum}  GROUP BY u.schoolnum ) t LEFT JOIN reserveyuejuannum rs ON t.schoolNum = rs.itemid and rs.statisticItem='01' and t.examNum = rs.examNum and t.gradeNum = rs.gradeNum and t.subjectNum = rs.subjectNum LEFT JOIN school s ON t.schoolNum = s.id  where t.c_u>rs.reserveNum GROUP BY t.exampaperNum ";
            args.put("exampaperNum", exampaperNum);
        }
        return this.dao._queryStr(sql, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Map<String, String> getTaskCountByUser(Map<String, String> map, String userId) {
        String userNum = map.get("userNumStr");
        String examPaperNum = map.get("examPaperNum");
        String groupNum = map.get("groupNum");
        if ("".equals(groupNum) || null != groupNum) {
        }
        Map args = StreamMap.create().put("groupNum", (Object) groupNum).put("examPaperNum", (Object) examPaperNum).put("userNum", (Object) userNum);
        return this.dao._queryOrderMap("SELECT u.username,t.count from ( \tSELECT insertUser,count(1) count from task where exampaperNum={examPaperNum} and insertUser in ({userNum[]}) and `status`='T' GROUP BY insertUser\t)t INNER JOIN user u on t.insertUser=u.id", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Map<String, Map<String, String>> getTaskCountByUser1(Map<String, String> map, String userId) {
        String userNum = map.get("userNumStr");
        String examPaperNum = map.get("examPaperNum");
        String groupNum = map.get("groupNum");
        String groupNumStr = "";
        String groupNumPara = "";
        if (!"".equals(groupNum) && null != groupNum) {
            if (groupNum.indexOf("_") != -1) {
                groupNumPara = groupNum.replace("_", Const.STRING_SEPERATOR);
                groupNumStr = " and groupNum in ({groupNum[]}) ";
            } else {
                groupNumStr = " and groupNum={groupNum} ";
                groupNumPara = groupNum;
            }
        }
        Map<String, Map<String, String>> tMap = new HashMap<>();
        String[] userNumArr = userNum.split(Const.STRING_SEPERATOR);
        for (String insertUser : userNumArr) {
            Map<String, String> pMap = new HashMap<>();
            Map args2 = new HashMap();
            args2.put("insertUser", insertUser);
            args2.put("groupNum", groupNumPara);
            String username = this.dao._queryStr("SELECT username from user where id={insertUser} ", args2);
            pMap.put("username", username);
            String sql = "SELECT GROUP_CONCAT(DISTINCT STATUS) from task where exampaperNum={examPaperNum} and insertUser={insertUser} " + groupNumStr;
            args2.put("examPaperNum", examPaperNum);
            String status = this.dao._queryStr(sql, args2);
            if (null != status && status.indexOf("T") != -1) {
                pMap.put(Const.CORRECT_SCORECORRECT, "T");
                String sql2 = "SELECT count(1) count from task where exampaperNum={examPaperNum} and insertUser={insertUser} " + groupNumStr + " and `status`='T' ";
                String count = this.dao._queryStr(sql2, args2);
                pMap.put("num", count);
            } else {
                pMap.put(Const.CORRECT_SCORECORRECT, "F");
                pMap.put("reMoveuserNum", insertUser);
                String sql3 = "select id from task where examPaperNum={examPaperNum} and `status`='F' and insertUser={insertUser} " + groupNumStr;
                List<?> _queryBeanList = this.dao._queryBeanList(sql3, Task.class, args2);
                List<Map> maplist = new ArrayList<>();
                for (int j = 0; j < _queryBeanList.size(); j++) {
                    String id = ((Task) _queryBeanList.get(j)).getId();
                    Map args3 = StreamMap.create().put("id", (Object) id);
                    maplist.add(args3);
                }
                if (CollUtil.isNotEmpty(maplist)) {
                    this.dao._batchExecute("update task set insertUser='-1',porder=0,fenfaDate=0,updateTime='',updateUser='-1', xuankaoqufen=1 where id={id} ", maplist);
                }
                this.log.info("【移除题组用户-回收】考试--" + examPaperNum + "--题组【" + groupNum + "】--教师【" + insertUser + "】--操作人--" + userId + "--共回收了【" + _queryBeanList.size() + "】条");
                String sql4 = "select id from task where examPaperNum={examPaperNum} " + groupNumStr + " and status='T' and insertUser<>updateUser ";
                List<?> _queryBeanList2 = this.dao._queryBeanList(sql4, Task.class, args2);
                List<Map> maplist2 = new ArrayList<>();
                for (int j2 = 0; j2 < _queryBeanList2.size(); j2++) {
                    String id2 = ((Task) _queryBeanList2.get(j2)).getId();
                    Map args32 = StreamMap.create().put("id", (Object) id2);
                    maplist2.add(args32);
                }
                if (CollUtil.isNotEmpty(maplist2)) {
                    this.dao._batchExecute("update task set insertUser='-1',porder=0,fenfaDate=0,updateTime='',updateUser='-1', xuankaoqufen=1 where id={id}", maplist2);
                }
                this.log.info("【移除题组用户-回收过程中的异常数据】考试--" + examPaperNum + "--题组【" + groupNum + "】--教师【" + insertUser + "】--操作人--" + userId + "--共处理了【" + _queryBeanList2.size() + "】条");
            }
            tMap.put(insertUser, pMap);
        }
        return tMap;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<AjaxData> querySubjectLeaderList(String examPaperNum, String userType) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        return this.dao._queryBeanList("SELECT t.subjectNum num,t.subjectName name,t.examPaperNum ext1,t1.ext2 from( \tSELECT s.subjectNum,s.subjectName,s.orderNum,e.examPaperNum from exampaper e  \t\tINNER JOIN `subject` s on e.subjectNum=s.subjectNum  \t\twhere pexampaperNum= {examPaperNum} \t)t left JOIN( \t\tSELECT qu.exampaperNum,GROUP_CONCAT(qu.exampaperNum,'--',qu.userNum,'--',u.userName,'--',u.realName) ext2 from questionGroup_user qu \t\tINNER JOIN user u on qu.userNum=u.id \t\tINNER JOIN exampaper e on qu.exampaperNum=e.examPaperNum \t\twhere e.pexamPaperNum={examPaperNum} and qu.userType='2'  \t\tGROUP BY qu.exampaperNum \t)t1 on t.exampaperNum=t1.exampaperNum ORDER BY t.orderNum ASC", AjaxData.class, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public RspMsg existYuejuanyuan(String examPaperNum, String groupNum, String userNumStr) {
        String[] groupNumArr = groupNum.split("_");
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("groupNum", (Object) groupNumArr[0]).put("userNum", (Object) userNumStr);
        if (null == this.dao._queryObject("select id from  questiongroup_user where exampaperNum={examPaperNum} and groupNum ={groupNum}  limit 1", args)) {
            return new RspMsg(404, "此题尚未安排阅卷教师", null);
        }
        if (null == this.dao._queryObject("select id from  questiongroup_user where exampaperNum={examPaperNum} and groupNum ={groupNum}  and userNum in ({userNum}) ", args)) {
            return new RspMsg(200, "此题已安排阅卷教师", null);
        }
        return new RspMsg(40404, "所选教师已经是此题组阅卷员,请不要重复添加", null);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateShensuYuzhi(String questionNum, String shensuYuzhi) {
        Map args = StreamMap.create().put("shensuYuzhi", (Object) shensuYuzhi).put("questionNum", (Object) questionNum);
        this.dao._execute("update questiongroup_question set shensuYuzhi={shensuYuzhi} where questionNum={questionNum} ".toString(), args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Map<String, String> getSjtAndGrade(String examNum, String userId) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        return this.dao._queryOrderMap("SELECT bg.gradeName,GROUP_CONCAT(ep.subjectNum,'-',s.subjectName,'-',ep.examPaperNum) from exampaper ep  INNER JOIN exam e on ep.examNum=e.examNum INNER JOIN subject s on ep.subjectNum=s.subjectNum INNER JOIN basegrade bg on ep.gradeNum=bg.gradeNum where e.examNum={examNum}  and ep.type='0' AND ep.isHidden='F' GROUP BY ep.gradeNum ORDER BY ep.gradeNum ", TypeEnum.StringString, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void resetAllSjt(String examPaperNumArr, String userId) {
        Map<String, PersonWorkRecord> personWorkRecordMap = AwardPointDaoImpl.personWorkRecordMap;
        String[] examPArr = examPaperNumArr.split(Const.STRING_SEPERATOR);
        List<Map> mapList = new ArrayList<>();
        for (String examPaperNum : examPArr) {
            long start1 = System.currentTimeMillis();
            Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
            mapList.add(args);
            this.log.info("【重置所有科目判分】--userId:" + userId + "--examPaperNum:" + examPaperNum + "---时间：" + (System.currentTimeMillis() - start1));
            Map<String, String> map = this.dao._queryOrderMap("SELECT groupNum,userNum from questiongroup_user where exampaperNum={examPaperNum} and userType<>2 ", TypeEnum.StringString, args);
            for (String groupNum : map.keySet()) {
                String userNum = String.valueOf(map.get(groupNum));
                personWorkRecordMap.remove(groupNum + "-" + userNum);
            }
        }
        this.dao._batchExecute("delete from task where examPaperNum={examPaperNum} and userNum=3", mapList);
        this.dao._batchExecute("update task set insertUser='-1',status='F',isException='F',porder=0,fenfaDate=0 where examPaperNum={examPaperNum} ", mapList);
        this.dao._batchExecute("delete from questionstepscore where examPaperNum={examPaperNum} ", mapList);
        this.dao._batchExecute("delete from remark where examPaperNum={examPaperNum} ", mapList);
        this.dao._batchExecute("delete from markerror where examPaperNum={examPaperNum} ", mapList);
        this.dao._batchExecute("DELETE q FROM remarkimg q  inner JOIN (  select id scoreId from score where examPaperNum={examPaperNum}  )t  on t.scoreId=q.scoreid ", mapList);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void exportYifenpeiYueJuan(String exam, String grade, String subject, String leiceng, String school, String type, String user, String dirPath, String examName) {
        FileUtil.del(dirPath);
        List<Map<String, Object>> data = getYifenpeiListInfo(exam, grade, subject, leiceng, school, type, user);
        BigExcelWriter writer = ExcelUtil.getBigWriter(dirPath);
        if (data.size() > 0) {
            if ("0".equals(type)) {
                writer.addHeaderAlias("schoolName", "学校");
                writer.addHeaderAlias("username", "教师工号");
                writer.addHeaderAlias("realname", "教师姓名");
                writer.addHeaderAlias("gradeName", "年级");
                writer.addHeaderAlias("subjectName", "科目");
            } else {
                writer.addHeaderAlias("subjectName", "科目");
                writer.addHeaderAlias("gradeName", "年级");
                writer.addHeaderAlias("schoolName", "学校");
                writer.addHeaderAlias("username", "教师工号");
                writer.addHeaderAlias("realname", "教师姓名");
            }
            int[] columnWidths = {20, 15, 20, 15, 15};
            for (int i = 0; i < columnWidths.length; i++) {
                writer.setColumnWidth(i, columnWidths[i]);
            }
            writer.getSheet().setDisplayGridlines(false);
            writer.passRows(1);
            Font font_examName = writer.createFont();
            font_examName.setFontName("宋体");
            CellStyle cellStyle_examName = writer.createCellStyle();
            cellStyle_examName.setFont(font_examName);
            StyleSet style = writer.getStyleSet();
            style.setBorder(BorderStyle.NONE, IndexedColors.WHITE);
            writer.merge(0, 0, 0, 4, "考试名称：" + examName, false);
            writer.getCell(0, 0).setCellStyle(cellStyle_examName);
            style.setBorder(BorderStyle.THIN, IndexedColors.BLACK);
            writer.setOnlyAlias(true);
            writer.write(data, true);
        } else {
            List listhead = new ArrayList();
            if ("0".equals(type)) {
                listhead.add("学校");
                listhead.add("教师工号");
                listhead.add("教师姓名");
                listhead.add("年级");
                listhead.add("科目");
            } else {
                listhead.add("科目");
                listhead.add("年级");
                listhead.add("学校");
                listhead.add("教师工号");
                listhead.add("教师姓名");
            }
            int[] columnWidths2 = {20, 15, 20, 15, 15};
            for (int i2 = 0; i2 < columnWidths2.length; i2++) {
                writer.setColumnWidth(i2, columnWidths2[i2]);
            }
            writer.getSheet().setDisplayGridlines(false);
            writer.passRows(1);
            Font font_examName2 = writer.createFont();
            font_examName2.setFontName("宋体");
            CellStyle cellStyle_examName2 = writer.createCellStyle();
            cellStyle_examName2.setFont(font_examName2);
            writer.getStyleSet().setBorder(BorderStyle.NONE, IndexedColors.WHITE);
            writer.merge(0, 0, 0, 4, "考试名称：" + examName, false);
            writer.getCell(0, 0).setCellStyle(cellStyle_examName2);
            writer.writeHeadRow(listhead);
        }
        writer.close();
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateTempleteDisabled(String examNum, String gradeNum, String subjectNum, int waittingUpdateTemplate) {
        Map args = StreamMap.create().put("waittingUpdateTemplate", (Object) Integer.valueOf(waittingUpdateTemplate)).put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum);
        this.dao._execute("update templaterecord set waittingUpdateTemplate={waittingUpdateTemplate} where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}".toString(), args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getSchoollevelData(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryMapList("select sl.examNum,sch.id schoolNum,sch.schoolName,IFNULL(sg.schoolGroupName,'未分组') schoolGroupName,sl.schoolLevelNum,sl.schoolLevelName from school sch LEFT JOIN schoolgroup sg on sg.schoolNum=sch.id LEFT JOIN schoollevel sl on sl.schoolNum=sch.id and sl.examNum={examNum} WHERE sch.isDelete='F' GROUP BY sch.id ORDER BY IF(sg.id is null,1,0),CONVERT(sg.schoolGroupName USING GBK),IF(sl.id is null,1,0),sl.schoolLevelNum,CONVERT(sch.schoolName USING GBK)", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void insertInitSchoollevelData(String examNum, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("insertUser", userId);
        args.put("insertDate", currentTime);
        this.dao._execute("insert into schoollevel(id,examNum,schoolNum,schoolLevelNum,schoolLevelName,insertUser,insertDate) select UUID_SHORT(),{examNum},sch.id,2,'中',{insertUser},{insertDate} from school sch left join schoollevel sl on sl.schoolNum=sch.id and sl.examNum={examNum} where sch.isDelete='F' and sl.id is null group by sch.id", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void submitSchoollevelData(String examNum, String userId, List<Map<String, Object>> dataList) {
        Map args_selSql = new HashMap();
        args_selSql.put(Const.EXPORTREPORT_examNum, examNum);
        Object existId = this.dao._queryObject("select id from schoollevel where examNum={examNum} limit 1", args_selSql);
        if (null != existId) {
            this.dao._execute("delete from schoollevel where examNum={examNum}", args_selSql);
        }
        List<Map<String, Object>> insertParams = new ArrayList<>();
        String currentTime = DateUtil.getCurrentTime();
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> sl = dataList.get(i);
            sl.put("id", Long.valueOf(GUID.getGUID()));
            sl.put(Const.EXPORTREPORT_examNum, examNum);
            sl.put("insertUser", userId);
            sl.put("insertDate", currentTime);
            String schoolLevelNum = Convert.toStr(sl.get("schoolLevelNum"), "3");
            if ("1".equals(schoolLevelNum)) {
                sl.put("schoolLevelName", "强");
            } else if ("2".equals(schoolLevelNum)) {
                sl.put("schoolLevelName", "中");
            } else {
                sl.put("schoolLevelNum", 3);
                sl.put("schoolLevelName", "弱");
            }
            insertParams.add(sl);
        }
        if (CollUtil.isNotEmpty(insertParams)) {
            this.dao._batchExecute("insert into schoollevel(id,examNum,schoolNum,schoolLevelNum,schoolLevelName,insertUser,insertDate) values ({id},{examNum},{schoolNum},{schoolLevelNum},{schoolLevelName},{insertUser},{insertDate})", insertParams);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getSchoollevelExamData(String examNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        return this.dao._queryMapList("select distinct e.examNum,e.examName from schoollevel sl inner join exam e on e.examNum=sl.examNum where sl.examNum<>{examNum} order by e.examDate desc,e.insertDate desc", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void quoteSchoollevelData(String examNum, String assignedExamNum, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put("userId", userId);
        args.put("currentTime", currentTime);
        args.put("assignedExamNum", assignedExamNum);
        this.dao._execute("delete from schoollevel where examNum={examNum}", args);
        this.dao._execute("insert into schoollevel (id,examNum,schoolNum,schoolLevelNum,schoolLevelName,insertUser,insertDate) select UUID_SHORT(),{examNum},sl.schoolNum,sl.schoolLevelNum,sl.schoolLevelName,{userId},{currentTime} from schoollevel sl inner join school sch on sch.id=sl.schoolNum and sch.isDelete='F' where sl.examNum={assignedExamNum}", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void insertInitBuyuejuanschoolData(String examNum, String gradeNum, String subjectNum, String parent_subjectNum, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("parent_subjectNum", parent_subjectNum);
        args.put("insertUser", userId);
        args.put("insertDate", currentTime);
        List<Map<String, Object>> byjsList = this.dao._queryMapList("select schoolNum,fenpeiRenshu,insertUser,insertDate from buyuejuanschool where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", TypeEnum.StringObject, args);
        Map<String, Object> epMap = this.dao._querySimpleMap("select examPaperNum,pexamPaperNum,isHidden from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("pexamPaperNum", epMap.get("pexamPaperNum"));
        args.put("examPaperNum", epMap.get("examPaperNum"));
        String ur_subStr = "";
        if ("T".equals(Convert.toStr(epMap.get("isHidden")))) {
            ur_subStr = " inner join userrole_sub urs on urs.userNum=ur.userNum and urs.exampaperNum={examPaperNum} ";
        }
        String enSql = "select UUID_SHORT() id,{examNum} examNum,{gradeNum} gradeNum,{subjectNum} subjectNum,en.schoolNum,en.cankaoRenshu fenpeiRenshu,{insertUser} insertUser,{insertDate} insertDate from (select allsch.schoolNum,max(allsch.cankaoRenshu) cankaoRenshu from (select schoolNum,count(1) cankaoRenshu from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={parent_subjectNum} group by schoolNum union all select distinct u.schoolnum,0 cankaoRenshu from userrole ur " + ur_subStr + "LEFT JOIN role r on r.roleNum=ur.roleNum LEFT JOIN `user` u on u.id=ur.userNum where r.examPaperNum={pexamPaperNum} and r.type='4' and u.id is not null ) allsch group by allsch.schoolNum) en left join school sch on sch.id=en.schoolNum where sch.isDelete='F' ";
        List<Map<String, Object>> enList = this.dao._queryMapList(enSql, TypeEnum.StringObject, args);
        if (CollUtil.isNotEmpty(byjsList)) {
            byjsList.forEach(byjs -> {
                enList.stream().filter(en -> {
                    return Convert.toStr(byjs.get(Const.EXPORTREPORT_schoolNum)).equals(Convert.toStr(en.get(Const.EXPORTREPORT_schoolNum))) && StrUtil.isNotEmpty(Convert.toStr(byjs.get("fenpeiRenshu")));
                }).forEach(en2 -> {
                    en2.put("fenpeiRenshu", byjs.get("fenpeiRenshu"));
                    en2.put("insertUser", byjs.get("insertUser"));
                    en2.put("insertDate", byjs.get("insertDate"));
                });
            });
            this.dao._execute("delete from buyuejuanschool where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        }
        if (CollUtil.isNotEmpty(enList)) {
            this.dao.batchSave("buyuejuanschool", enList);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getBuyuejuanschoolData(String examNum, String gradeNum, String subjectNum, String parent_subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("parent_subjectNum", parent_subjectNum);
        Map<String, Object> epMap = this.dao._querySimpleMap("select examPaperNum,pexamPaperNum,isHidden from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("pexamPaperNum", epMap.get("pexamPaperNum"));
        args.put("examPaperNum", epMap.get("examPaperNum"));
        String ur_subStr = "";
        if ("T".equals(Convert.toStr(epMap.get("isHidden")))) {
            ur_subStr = " inner join userrole_sub urs on urs.userNum=ur.userNum and urs.exampaperNum={examPaperNum} ";
        }
        String sql = "select byjs.schoolNum,sch.schoolName,IFNULL(sg.schoolGroupName,'未分组') schoolGroupName,IFNULL(en.cankaoRenshu,0) cankaoRenshu,IFNULL(ur.teaRenshu,0) teaRenshu,byjs.fenpeiRenshu,byjs.fenpeiRenshu-IFNULL(en.cankaoRenshu,0) tiaozhengRenshu from (select schoolNum,fenpeiRenshu from buyuejuanschool where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}) byjs left join (select schoolNum,count(1) cankaoRenshu from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={parent_subjectNum} group by schoolNum) en on en.schoolNum=byjs.schoolNum LEFT JOIN (select u.schoolnum,count(distinct ur.userNum) teaRenshu from userrole ur " + ur_subStr + "LEFT JOIN role r on r.roleNum=ur.roleNum LEFT JOIN `user` u on u.id=ur.userNum where r.examPaperNum={pexamPaperNum} and r.type='4' and u.id is not null group by u.schoolNum ) ur on ur.schoolnum=byjs.schoolNum LEFT JOIN schoolgroup sg on sg.schoolNum=byjs.schoolNum LEFT JOIN school sch on sch.id=byjs.schoolNum WHERE sch.isDelete='F' GROUP BY byjs.schoolNum ORDER BY IF(sg.id is null,1,0),CONVERT(sg.schoolGroupName USING GBK),cankaoRenshu asc,CONVERT(sch.schoolName USING GBK)";
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void submitBuyuejuanschoolData(String examNum, String gradeNum, String subjectNum, String userId, List<Map<String, Object>> dataList) {
        Map args_selSql = new HashMap();
        args_selSql.put(Const.EXPORTREPORT_examNum, examNum);
        args_selSql.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args_selSql.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Object existId = this.dao._queryObject("select id from buyuejuanschool where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} limit 1", args_selSql);
        if (null != existId) {
            this.dao._execute("delete from buyuejuanschool where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", args_selSql);
        }
        String currentTime = DateUtil.getCurrentTime();
        List<Map<String, Object>> dataList2 = (List) dataList.stream().filter(data -> {
            return !"-1".equals(Convert.toStr(data.get(Const.EXPORTREPORT_schoolNum)));
        }).collect(Collectors.toList());
        dataList2.forEach(byjs -> {
            byjs.put("id", Long.valueOf(GUID.getGUID()));
            byjs.put(Const.EXPORTREPORT_examNum, examNum);
            byjs.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            byjs.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            byjs.put("insertUser", userId);
            byjs.put("insertDate", currentTime);
            int cankaoRenshu = Convert.toInt(byjs.get("cankaoRenshu"), 0).intValue();
            int tiaozhengRenshu = Convert.toInt(byjs.get("tiaozhengRenshu"), 0).intValue();
            byjs.put("fenpeiRenshu", Integer.valueOf(cankaoRenshu + tiaozhengRenshu));
        });
        if (CollUtil.isNotEmpty(dataList2)) {
            this.dao.batchSave("buyuejuanschool", dataList2);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getWorkReduceTableData(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Map<String, Object> epMap = this.dao._querySimpleMap("select examPaperNum,pexamPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("exampaperNum", epMap.get("examPaperNum"));
        args.put("pexamPaperNum", epMap.get("pexamPaperNum"));
        return this.dao._queryMapList("SELECT qgu.id,IFNULL(sg.schoolGroupNum,-1) schoolGroupNum,IFNULL(sg.schoolGroupName,'未分组') schoolGroupName,sch.id schoolNum,sch.schoolName,u.id userId,u.username,u.realname,cast(round(IFNULL(qgu.reduceRate,1)*100,2) as real) reduceRate FROM questiongroup_user qgu LEFT JOIN `user` u on u.id=qgu.userNum LEFT JOIN school sch on sch.id=u.schoolNum LEFT JOIN schoolgroup sg on sg.schoolNum=sch.id LEFT JOIN userrole ur on ur.userNum=u.id LEFT JOIN role r on r.roleNum=ur.roleNum WHERE qgu.exampaperNum={exampaperNum} and qgu.userType='2' and r.examPaperNum={pexamPaperNum} and r.type='4' and sch.isDelete='F' and u.id is not null group by u.id ORDER BY IF(sg.id is null,1,0),CONVERT(sg.schoolGroupName USING GBK) ,CONVERT(sch.schoolName USING GBK),CONVERT(u.realname USING GBK)", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void submitWorkReduceTableData(String userId, List<Map<String, Object>> dataList) {
        List<Map<String, Object>> updateParams = new ArrayList<>();
        String currentTime = DateUtil.getCurrentTime();
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> qgu = dataList.get(i);
            String reduceRate = Convert.toStr(qgu.get("reduceRate"));
            if (StrUtil.isEmpty(reduceRate)) {
                qgu.put("reduceRate", null);
            } else {
                qgu.put("reduceRate", Convert.toBigDecimal(reduceRate).divide(BigDecimal.valueOf(100L)));
            }
            qgu.put("reduceRate", Convert.toBigDecimal(qgu.get("reduceRate"), (BigDecimal) null));
            qgu.put("insertUser", userId);
            qgu.put("insertDate", currentTime);
            updateParams.add(qgu);
        }
        if (CollUtil.isNotEmpty(updateParams)) {
            this.dao._batchExecute("update questiongroup_user set reduceRate={reduceRate},insertUser={insertUser},insertDate={insertDate}where id={id}", updateParams);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String isOpenYuejuan(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Map<String, Object> ep = this.dao._querySimpleMap("select examPaperNum,pexamPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", args);
        args.put("category", ep.get("examPaperNum"));
        List<Object> defineIdList = this.dao._queryColList("select id from define where category={category} union all select id from define where category={category}", args);
        args.put("pexamPaperNum", ep.get("pexamPaperNum"));
        List<Object> taskQuestionNumList = this.dao._queryColList("select distinct questionNum from task where exampaperNum={pexamPaperNum} and `status`='T'", args);
        Object obj = taskQuestionNumList.stream().filter(t -> {
            return defineIdList.contains(t);
        }).findAny().orElse(null);
        if (null == obj) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getQuestionGroupTableData(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        Map<String, Object> epMap = this.dao._querySimpleMap("select examPaperNum,pexamPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("exampaperNum", epMap.get("examPaperNum"));
        args.put("pexamPaperNum", epMap.get("pexamPaperNum"));
        return this.dao._queryMapList("SELECT aaa.exampaperNum, GROUP_CONCAT( DISTINCT aaa.groupNum ORDER BY IFNULL( _d.orderNum, sd.orderNum ), subd.orderNum SEPARATOR '_' ) groupNum,aaa.groupName,aaa.groupType,qms.makType,qms.judgetype,aaa.choosename,IFNULL( sd_d.questionNum, _dd.questionNum ) mainName,subd.orderNum,cast(IFNULL( _d.fullScore, subd.fullScore) as real) fullScore,IF(qms.makType='1'&&qms.judgetype!='0',cast(round(IFNULL(da.yuguSanpinglv*100,10),2) as real),'') yuguSanpinglv,IFNULL(cast(round(da.gongzuoliangZhanbi*100,2) as real),'') gongzuoliangZhanbi FROM (SELECT q.exampaperNum, q.groupNum, q.groupName, q.groupType, dd.choosename FROM (SELECT q1.exampaperNum, q1.groupNum, q1.groupName, q1.groupType FROM questiongroup q1 LEFT JOIN questiongroup_question qgq1 ON q1.exampaperNum = qgq1.exampaperNum AND q1.groupNum = qgq1.groupNum WHERE q1.exampaperNum ={pexamPaperNum} AND q1.groupType != '0' GROUP BY q1.groupNum ) q INNER JOIN (SELECT d.id, d.choosename FROM ( SELECT d.id, d.orderNum, d.choosename FROM define d WHERE d.category ={exampaperNum} UNION SELECT sb.id, sb.orderNum, d.choosename FROM define d LEFT JOIN subdefine sb ON sb.pid = d.id WHERE sb.category = {exampaperNum}) d ORDER BY d.orderNum ) dd ON q.groupNum = dd.id ) aaa LEFT JOIN subdefine subd ON subd.id = aaa.groupNum LEFT JOIN define sd ON sd.id = subd.pid LEFT JOIN define sd_d ON sd_d.id = sd.choosename LEFT JOIN define _d ON _d.id = aaa.groupNum LEFT JOIN define _dd ON _dd.id = _d.choosename LEFT JOIN questiongroup_mark_setting qms on aaa.examPaperNum = qms.exampaperNum and aaa.groupNum = qms.groupNum LEFT JOIN distributeAuto da ON aaa.examPaperNum = da.exampaperNum and aaa.groupNum = da.groupNum GROUP BY IF(aaa.choosename = 's',aaa.groupNum,CONCAT(aaa.choosename,IFNULL( subd.orderNum, '' ))) ORDER BY IFNULL( _d.orderNum, sd.orderNum ),subd.orderNum", TypeEnum.StringObject, args);
    }

    public Object getSonExampaperNum(String pexamPaperNum, String sonSubjectNum) {
        Map args = new HashMap();
        args.put("pexamPaperNum", pexamPaperNum);
        args.put("sonSubjectNum", sonSubjectNum);
        return this.dao._queryObject("select examPaperNum from exampaper where pexamPaperNum={pexamPaperNum} and subjectNum={sonSubjectNum}", args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void submitQuestionGroupTableData(String subjectNum, List<Map<String, Object>> questionGroupDataList, String userId) {
        getSonExampaperNum(Convert.toStr(questionGroupDataList.get(0).get("exampaperNum")), subjectNum);
        List<Map<String, Object>> paramsList = new ArrayList<>();
        String currentTime = DateUtil.getCurrentTime();
        questionGroupDataList.forEach(obj -> {
            String[] groupNums = Convert.toStr(obj.get("groupNum"), "").split("_");
            for (String groupNum : groupNums) {
                HashMap hashMap = new HashMap();
                hashMap.put("id", Long.valueOf(GUID.getGUID()));
                hashMap.put("pexamPaperNum", obj.get("exampaperNum"));
                hashMap.put("sonSubjectNum", subjectNum);
                hashMap.put("groupNum", groupNum);
                String yuguSanpinglv = Convert.toStr(obj.get("yuguSanpinglv"));
                if (StrUtil.isEmpty(yuguSanpinglv)) {
                    hashMap.put("yuguSanpinglv", null);
                } else {
                    hashMap.put("yuguSanpinglv", Convert.toBigDecimal(yuguSanpinglv).divide(BigDecimal.valueOf(100L)));
                }
                String gongzuoliangZhanbi = Convert.toStr(obj.get("gongzuoliangZhanbi"));
                if (StrUtil.isEmpty(gongzuoliangZhanbi)) {
                    hashMap.put("gongzuoliangZhanbi", null);
                } else {
                    hashMap.put("gongzuoliangZhanbi", Convert.toBigDecimal(gongzuoliangZhanbi).divide(BigDecimal.valueOf(100L)));
                }
                hashMap.put("insertUser", userId);
                hashMap.put("insertDate", currentTime);
                paramsList.add(hashMap);
            }
        });
        if (CollUtil.isNotEmpty(paramsList)) {
            this.dao._batchExecute("delete from distributeauto where exampaperNum={pexamPaperNum} and groupNum={groupNum}", paramsList);
            this.dao._batchExecute("insert into distributeauto (id,type,exampaperNum,subjectNum,groupNum,yuguSanpinglv,gongzuoliangZhanbi,insertUser,insertDate) values ({id},'1',{pexamPaperNum},{sonSubjectNum},{groupNum},{yuguSanpinglv},{gongzuoliangZhanbi},{insertUser},{insertDate})", paramsList);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String autoDistributeTeacherWork(String examNum, String gradeNum, String subjectNum, String parentSubjectNum, List<Map<String, Object>> questionGroupDataList, List<Map<String, Object>> examSchoolgroupSetTableDataList, String loginUserId, double tiaozhengXishu, String quesOrderType1, String quesOrderType2, String currentSchoolGroupNum) throws Exception {
        String columnStr;
        String deleteQGUSql;
        String deleteQuotaSql;
        String deleteSchoolquotaSql;
        String sgStr;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("parentSubjectNum", parentSubjectNum);
        Map<String, Object> notSchoolgroupMap = examSchoolgroupSetTableDataList.stream().filter(obj -> {
            return "-1".equals(Convert.toStr(obj.get("schoolGroupNum")));
        }).findAny().orElse(null);
        args.put("notSchoolgroup_isAutoDistribute", null == notSchoolgroupMap ? "1" : notSchoolgroupMap.get("isAutoDistribute"));
        args.put("notSchoolgroup_isToSchool", null == notSchoolgroupMap ? "1" : notSchoolgroupMap.get("isToSchool"));
        args.put("notSchoolgroup_isSetTizuzhang", null == notSchoolgroupMap ? "1" : notSchoolgroupMap.get("isSetTizuzhang"));
        Map<String, Object> epMap = this.dao._querySimpleMap("select examPaperNum,pexamPaperNum,isHidden from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("pexamPaperNum", epMap.get("pexamPaperNum"));
        args.put("examPaperNum", epMap.get("examPaperNum"));
        String ur_subStr = "";
        if ("T".equals(Convert.toStr(epMap.get("isHidden")))) {
            ur_subStr = " inner join userrole_sub urs on urs.userNum=ur.userNum and urs.exampaperNum={examPaperNum} ";
        }
        String fenzuyuejuan = isFenzuyuejuan(examNum, gradeNum, parentSubjectNum);
        if ("1".equals(fenzuyuejuan)) {
            columnStr = " IFNULL(sg.schoolGroupNum,-1) schoolGroupNum, ";
        } else {
            columnStr = " -1 schoolGroupNum, ";
        }
        String teaSql = "select " + columnStr + "IFNULL(sg.schoolGroupName,'未分组') schoolGroupName,sl.id,sl.schoolLevelName,en.schoolNum,sch.schoolName,ur.realname,ur.userNum,IF(up.id is null,0,1) beikeZuzhang,IF(qgu.id is not null && IFNULL(qgu.reduceRate,1)>0,1,0) isSubjectLeader,IFNULL(qgu.reduceRate,1) reduceRate,IFNULL(esgs.isToSchool,{notSchoolgroup_isToSchool}) isToSchool,IFNULL(esgs.isSetTizuzhang,{notSchoolgroup_isSetTizuzhang}) isSetTizuzhang,en.stuNumber,IFNULL(sl.schoolLevelNum,2) schLevel_order,sch.rownum sch_order from (select schoolNum,fenpeiRenshu stuNumber from buyuejuanschool where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} GROUP BY schoolNum) en LEFT JOIN (select u.schoolnum,ur.userNum,u.realname from userrole ur " + ur_subStr + "LEFT JOIN role r on r.roleNum=ur.roleNum LEFT JOIN `user` u on u.id=ur.userNum where r.examPaperNum={pexamPaperNum} and r.type='4' and u.id is not null ) ur on ur.schoolnum=en.schoolNum LEFT JOIN schoolgroup sg on sg.schoolNum=en.schoolnum LEFT JOIN examschoolgroupsetting esgs on esgs.schoolGroupNum=sg.schoolGroupNum and esgs.examNum={examNum} and esgs.gradeNum={gradeNum} and esgs.subjectNum={subjectNum} LEFT JOIN schoollevel sl on sl.schoolNum=en.schoolnum and sl.examNum={examNum} LEFT JOIN userposition up on up.userNum=ur.userNum and up.type={type} and up.gradeNum={gradeNum} and up.subjectNum={subjectNum}LEFT JOIN questiongroup_user qgu on qgu.userNum=ur.userNum and qgu.userType='2' and qgu.exampaperNum={examPaperNum} LEFT JOIN (select @rownum:=@rownum+1 AS rownum,s.id,s.schoolName from (select id,schoolName from school order by rand()) s,(SELECT @rownum:=0) r ) sch on sch.id=en.schoolNum where IFNULL(esgs.isAutoDistribute,{notSchoolgroup_isAutoDistribute})='1' GROUP BY IFNULL(ur.userNum,en.schoolNum) ";
        args.put("type", "-2");
        List<Map<String, Object>> allTeaList = this.dao._queryMapList(teaSql, TypeEnum.StringObject, args);
        List<Map<String, Object>> groupSchStuNumberList = new ArrayList<>();
        Map<Object, List<Map<String, Object>>> groupSchStuNumberListMap = (Map) allTeaList.stream().collect(Collectors.groupingBy(tea -> {
            return tea.get(Const.EXPORTREPORT_schoolNum);
        }));
        groupSchStuNumberListMap.forEach((sch, teaList) -> {
            Map<String, Object> firstTea = (Map) teaList.get(0);
            groupSchStuNumberList.add(firstTea);
        });
        Map<Object, Integer> groupAllStuNumberMap = (Map) groupSchStuNumberList.stream().collect(Collectors.groupingBy(m -> {
            return m.get("schoolGroupNum");
        }, Collectors.summingInt(m2 -> {
            return Convert.toInt(m2.get("stuNumber")).intValue();
        })));
        Map<Object, Integer> groupTeaNumberMap = (Map) allTeaList.stream().collect(Collectors.groupingBy(m3 -> {
            return m3.get("schoolGroupNum");
        }, Collectors.summingInt(m22 -> {
            if (StrUtil.isEmpty(Convert.toStr(m22.get("userNum")))) {
                return 0;
            }
            return 1;
        })));
        StringBuffer msg = new StringBuffer();
        Map<Object, Long> schTeaNumberMap = (Map) allTeaList.stream().collect(Collectors.groupingBy(m4 -> {
            return m4.get(Const.EXPORTREPORT_schoolNum);
        }, Collectors.counting()));
        Map<Object, BigDecimal> schTeaAveWorkNumberMap = new HashMap<>();
        schTeaNumberMap.forEach((key, value) -> {
            Map<String, Object> obj2 = (Map) groupSchStuNumberList.stream().filter(m5 -> {
                return Convert.toStr(key).equals(Convert.toStr(m5.get(Const.EXPORTREPORT_schoolNum)));
            }).findFirst().orElse(null);
            if (CollUtil.isEmpty(obj2)) {
                schTeaAveWorkNumberMap.put(key, new BigDecimal(0));
                return;
            }
            int stuCount = Convert.toInt(obj2.get("stuNumber"), 0).intValue();
            Long teaCount = value;
            if (value.longValue() == 1) {
                Map<String, Object> teaObj = (Map) allTeaList.stream().filter(m6 -> {
                    return Convert.toStr(key).equals(Convert.toStr(m6.get(Const.EXPORTREPORT_schoolNum)));
                }).findFirst().orElse(null);
                if (CollUtil.isNotEmpty(teaObj) && StrUtil.isEmpty(Convert.toStr(teaObj.get("userNum")))) {
                    teaCount = 0L;
                }
            }
            if ((teaCount.longValue() > 0 && stuCount <= 0) || (teaCount.longValue() <= 0 && stuCount > 0)) {
                msg.append(StrUtil.format("【{}-{}】\n\t已上报阅卷教师数：{}，实际参与分配学生数：{}\n\n", new Object[]{obj2.get("schoolGroupName"), obj2.get("schoolName"), teaCount, Integer.valueOf(stuCount)}));
            }
            if ("1".equals(Convert.toStr(obj2.get("isToSchool"))) && stuCount <= 0) {
                schTeaAveWorkNumberMap.put(key, new BigDecimal(0));
                return;
            }
            int allStuNumber = ((Integer) groupAllStuNumberMap.get(obj2.get("schoolGroupNum"))).intValue();
            if ("0".equals(Convert.toStr(obj2.get("isToSchool")))) {
                BigDecimal groupStuNumber = Convert.toBigDecimal(Integer.valueOf(allStuNumber));
                BigDecimal groupTeaNumber = Convert.toBigDecimal(groupTeaNumberMap.get(obj2.get("schoolGroupNum")), BigDecimal.valueOf(1L));
                if (groupTeaNumber.compareTo(BigDecimal.valueOf(0L)) > 0) {
                    BigDecimal schTeaAveWorkNumber = groupStuNumber.divide(groupTeaNumber, 4, 0);
                    schTeaAveWorkNumberMap.put(key, schTeaAveWorkNumber);
                    return;
                } else {
                    schTeaAveWorkNumberMap.put(key, new BigDecimal(0));
                    msg.append(StrUtil.format("【组别-{}】\n\t已上报阅卷教师数：{}，实际参与分配学生数：{}\n\n", new Object[]{obj2.get("schoolGroupName"), groupTeaNumber, groupStuNumber}));
                    return;
                }
            }
            BigDecimal schStuNumber = null == obj2 ? BigDecimal.valueOf(0L) : Convert.toBigDecimal(obj2.get("stuNumber"), BigDecimal.valueOf(0L));
            BigDecimal schTeaAveWorkNumber2 = schStuNumber.divide(Convert.toBigDecimal(value, BigDecimal.valueOf(1L)), 4, 0);
            schTeaAveWorkNumberMap.put(key, schTeaAveWorkNumber2);
        });
        List<Map<String, Object>> reduceLeaderList = (List) allTeaList.stream().filter(tea2 -> {
            return "1".equals(Convert.toStr(tea2.get("isSubjectLeader")));
        }).collect(Collectors.toList());
        reduceLeaderList.forEach(leader -> {
            BigDecimal schTeaAveWorkNumber = (BigDecimal) schTeaAveWorkNumberMap.get(leader.get(Const.EXPORTREPORT_schoolNum));
            BigDecimal reduceRate = Convert.toBigDecimal(leader.get("reduceRate"));
            BigDecimal reduceStuNumber = Convert.toBigDecimal(schTeaAveWorkNumber, BigDecimal.valueOf(0L)).multiply(reduceRate).setScale(4, 0);
            leader.put("schTeaAveWorkNumber_before", schTeaAveWorkNumber);
            leader.put("reduceStuNumber", reduceStuNumber);
            leader.put("schTeaAveWorkNumber_after", schTeaAveWorkNumber.subtract(reduceStuNumber));
        });
        LinkedHashMap<Object, List<Map<String, Object>>> groupTeaListMap = (LinkedHashMap) allTeaList.stream().collect(Collectors.groupingBy(m5 -> {
            return m5.get("schoolGroupNum");
        }, LinkedHashMap::new, Collectors.toList()));
        if ("all".equals(currentSchoolGroupNum) || !"1".equals(fenzuyuejuan)) {
            deleteQGUSql = "delete from questiongroup_user where exampaperNum={pexamPaperNum} and groupNum={groupNum} and userType!='2'";
            deleteQuotaSql = "delete from quota where exampaperNum={pexamPaperNum} and groupNum={groupNum}";
            deleteSchoolquotaSql = "delete from schoolquota where exampaperNum={pexamPaperNum} and groupNum={groupNum}";
        } else {
            if ("-1".equals(currentSchoolGroupNum)) {
                sgStr = " and sg.id is null ";
            } else {
                sgStr = " and sg.schoolGroupNum={currentSchoolGroupNum} ";
            }
            deleteQGUSql = "delete qgu from questiongroup_user qgu left join user u on u.id=qgu.userNum left join schoolgroup sg on sg.schoolNum=u.schoolnum where qgu.exampaperNum={pexamPaperNum} and qgu.groupNum={groupNum} and qgu.userType!='2' and u.usertype='1' " + sgStr;
            deleteQuotaSql = "delete qt from quota qt left join user u on u.id=qt.insertUser left join schoolgroup sg on sg.schoolNum=u.schoolnum where qt.exampaperNum={pexamPaperNum} and qt.groupNum={groupNum} and u.usertype='1' " + sgStr;
            deleteSchoolquotaSql = "delete sqt from schoolquota sqt left join schoolgroup sg on sg.schoolNum=sqt.schoolNum where sqt.exampaperNum={pexamPaperNum} and sqt.groupNum={groupNum} " + sgStr;
        }
        List<Map<String, Object>> deleteParamsList = new ArrayList<>();
        questionGroupDataList.forEach(ques -> {
            Object currentGroupNum = ques.get("groupNum");
            String[] currentGroupNums = Convert.toStr(currentGroupNum).split("_");
            for (String groupNum : currentGroupNums) {
                HashMap hashMap = new HashMap();
                hashMap.put("pexamPaperNum", ques.get("exampaperNum"));
                hashMap.put("groupNum", groupNum);
                hashMap.put("currentSchoolGroupNum", currentSchoolGroupNum);
                deleteParamsList.add(hashMap);
            }
        });
        if (CollUtil.isNotEmpty(deleteParamsList)) {
            this.dao._batchExecute(deleteQGUSql, deleteParamsList);
            this.dao._batchExecute(deleteQuotaSql, deleteParamsList);
            this.dao._batchExecute(deleteSchoolquotaSql, deleteParamsList);
        }
        String insertQGUSql = "insert into questiongroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate,isFinished) values ({id},{pexamPaperNum},{groupNum},{userType},{userNum},{insertUser},{insertDate},0)";
        String updateQGUSql = "update questiongroup_user set userType={userType} where id={id}";
        String insertUserroleSql = "insert into userrole (userNum,roleNum,insertUser,insertDate,isDelete) values ({userNum},{ROLE_TIZUZHANG},{insertUser},{insertDate},'F')";
        String insertQuotaSql = "insert into quota (exampaperNum,groupNum,num,insertUser,insertDate) values ({pexamPaperNum},{groupNum},{num},{insertUser},{insertDate})";
        String insertSchoolquotaSql = "insert into schoolquota (id,exampaperNum,groupNum,schoolNum,\tnum,insertUser,insertDate,updateUser,updateDate) values ({id},{pexamPaperNum},{groupNum},{schoolNum},{num},{insertUser},{insertDate},{updateUser},{updateDate})";
        String currentTime = DateUtil.getCurrentTime();
        AtomicReference<Double> tiaozhengXishu_new = new AtomicReference<>(0);
        groupTeaListMap.forEach((oneGroup, teaData) -> {
            if (!"all".equals(currentSchoolGroupNum) && !Convert.toStr(oneGroup).equals(currentSchoolGroupNum)) {
                return;
            }
            List<Map<String, Object>> qguParamsList = new ArrayList<>();
            List<Map<String, Object>> qguUpdateParamsList = new ArrayList<>();
            List<Map<String, Object>> urInsertParamsList = new ArrayList<>();
            List<Map<String, Object>> quotaParamsList = new ArrayList<>();
            List<Map<String, Object>> schoolquotaParamsList = new ArrayList<>();
            String isSetTizuzhang_currentGroup = Convert.toStr(((Map) teaData.get(0)).get("isSetTizuzhang"));
            List<Map<String, Object>> currentGroupReduceLeaderList = (List) reduceLeaderList.stream().filter(leader2 -> {
                return Convert.toStr(oneGroup).equals(Convert.toStr(leader2.get("schoolGroupNum")));
            }).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(currentGroupReduceLeaderList)) {
                BigDecimal reduceStuNumber_total = new BigDecimal(0);
                BigDecimal leaderAveWorkNumber_before_total = new BigDecimal(0);
                for (int ll = 0; ll < currentGroupReduceLeaderList.size(); ll++) {
                    Map<String, Object> leader3 = currentGroupReduceLeaderList.get(ll);
                    reduceStuNumber_total = reduceStuNumber_total.add(Convert.toBigDecimal(leader3.get("reduceStuNumber")));
                    leaderAveWorkNumber_before_total = leaderAveWorkNumber_before_total.add(Convert.toBigDecimal(leader3.get("schTeaAveWorkNumber_before")));
                }
                if (reduceStuNumber_total.compareTo(BigDecimal.valueOf(0L)) == 1) {
                    BigDecimal shengyuTeaWorkSum = Convert.toBigDecimal(groupAllStuNumberMap.get(oneGroup)).subtract(leaderAveWorkNumber_before_total);
                    List<Object> schList = (List) groupSchStuNumberList.stream().filter(m6 -> {
                        return Convert.toStr(oneGroup).equals(Convert.toStr(m6.get("schoolGroupNum")));
                    }).map(m23 -> {
                        return m23.get(Const.EXPORTREPORT_schoolNum);
                    }).distinct().collect(Collectors.toList());
                    BigDecimal finalReduceStuNumber_total = reduceStuNumber_total;
                    schList.forEach(schoolNum -> {
                        BigDecimal teaWork = (BigDecimal) schTeaAveWorkNumberMap.get(schoolNum);
                        BigDecimal addWorkNumber = finalReduceStuNumber_total.multiply(teaWork).divide(shengyuTeaWorkSum, 4, 0);
                        schTeaAveWorkNumberMap.put(schoolNum, teaWork.add(addWorkNumber));
                    });
                }
            }
            Map<Object, Integer> allQuestionGroupAssignedWorkMap = new HashMap<>();
            Map<Object, BigDecimal> allTeacherAssignedWorkMap = new HashMap<>();
            Comparator<Map<String, Object>> cp_makType = Comparator.comparing(m1 -> {
                return Convert.toInt(m1.get("makType"), 0);
            });
            Comparator<Map<String, Object>> cp_gongzuoliangZhanbi = Comparator.comparing(m24 -> {
                return Convert.toBigDecimal(m24.get("gongzuoliangZhanbi"), BigDecimal.valueOf(0L));
            });
            Comparator<Map<String, Object>> c1 = getComparator(quesOrderType1, cp_gongzuoliangZhanbi, cp_makType);
            Comparator<Map<String, Object>> c2 = getComparator(quesOrderType2, cp_gongzuoliangZhanbi, cp_makType);
            List list = questionGroupDataList;
            if (null != c1 && null != c2) {
                list = (List) questionGroupDataList.stream().sorted(c1.thenComparing(c2)).collect(Collectors.toList());
            } else if (null != c1) {
                list = (List) questionGroupDataList.stream().sorted(c1).collect(Collectors.toList());
            } else if (null != c2) {
                list = (List) questionGroupDataList.stream().sorted(c2).collect(Collectors.toList());
            }
            int i = 0;
            while (true) {
                if (i >= list.size()) {
                    break;
                }
                AtomicInteger assignedTeaCount_oneSchGroupOneQuesGroup = new AtomicInteger();
                Map<String, Object> oneQuestionGroup = (Map) list.get(i);
                Object currentGroupNum = oneQuestionGroup.get("groupNum");
                String[] currentGroupNums = Convert.toStr(currentGroupNum).split("_");
                String currentGroupName = Convert.toStr(oneQuestionGroup.get("groupName"), "");
                if (!"s".equals(Convert.toStr(oneQuestionGroup.get("choosename")))) {
                    currentGroupName = Convert.toStr(oneQuestionGroup.get("mainName"), "");
                    String orderNum = Convert.toStr(oneQuestionGroup.get("orderNum"), "");
                    if (StrUtil.isNotEmpty(orderNum)) {
                        currentGroupName = currentGroupName + "_" + orderNum;
                    }
                }
                String makType = Convert.toStr(oneQuestionGroup.get("makType"), "0");
                String judgetype = Convert.toStr(oneQuestionGroup.get("judgetype"), "");
                if ("1".equals(makType)) {
                    tiaozhengXishu_new.set(Double.valueOf(0.0d));
                } else {
                    tiaozhengXishu_new.set(Double.valueOf(tiaozhengXishu));
                }
                int oneQuestionGroupAllWork_tishu = Convert.toInt(groupAllStuNumberMap.get(oneGroup), 0).intValue();
                BigDecimal gongzuoliangZhanbi = Convert.toBigDecimal(oneQuestionGroup.get("gongzuoliangZhanbi"), BigDecimal.valueOf(100L));
                if (gongzuoliangZhanbi.compareTo(BigDecimal.valueOf(0L)) > 0) {
                    BigDecimal gongzuoliangZhanbi2 = gongzuoliangZhanbi.divide(BigDecimal.valueOf(100L));
                    BigDecimal yuguSanpinglv = Convert.toBigDecimal(oneQuestionGroup.get("yuguSanpinglv"), BigDecimal.valueOf(0L)).divide(BigDecimal.valueOf(100L));
                    BigDecimal quesZhanbi = BigDecimal.valueOf(2L).add(yuguSanpinglv);
                    if ("1".equals(makType)) {
                        oneQuestionGroupAllWork_tishu = Convert.toInt(BigDecimal.valueOf(oneQuestionGroupAllWork_tishu).multiply(quesZhanbi).setScale(0, 0)).intValue();
                        gongzuoliangZhanbi2 = gongzuoliangZhanbi2.divide(quesZhanbi, 4, 4);
                    }
                    BigDecimal finalGongzuoliangZhanbi = gongzuoliangZhanbi2;
                    int finalOneQuestionGroupAllWork_tishu = oneQuestionGroupAllWork_tishu;
                    int currentQuestionGroupMaxassignableWork_tishu = finalOneQuestionGroupAllWork_tishu;
                    if ("1".equals(makType)) {
                        if ("0".equals(judgetype)) {
                            currentQuestionGroupMaxassignableWork_tishu = NumberUtil.ceilDiv(finalOneQuestionGroupAllWork_tishu, 2);
                        } else {
                            currentQuestionGroupMaxassignableWork_tishu = Convert.toBigDecimal(Integer.valueOf(finalOneQuestionGroupAllWork_tishu)).divide(quesZhanbi, 0, 0).intValue();
                        }
                    }
                    if (i == list.size() - 1) {
                        List<Map<String, Object>> unfinishedTeaData = new ArrayList<>();
                        BigDecimal allUnassignedWork_stu = new BigDecimal(0);
                        for (int t = 0; t < teaData.size(); t++) {
                            Map<String, Object> oneTeacher = (Map) teaData.get(t);
                            String userId = Convert.toStr(oneTeacher.get("userNum"));
                            if (!"0".equals(Convert.toStr(oneTeacher.get("isToSchool"))) || !StrUtil.isEmpty(userId)) {
                                String userId2 = StrUtil.isEmpty(userId) ? "--" : userId;
                                String schoolNum2 = Convert.toStr(oneTeacher.get(Const.EXPORTREPORT_schoolNum));
                                BigDecimal currentTeacherAllWork_stu = (BigDecimal) schTeaAveWorkNumberMap.get(oneTeacher.get(Const.EXPORTREPORT_schoolNum));
                                if ("1".equals(Convert.toStr(oneTeacher.get("isSubjectLeader")))) {
                                    Map<String, Object> leaderMap = (Map) reduceLeaderList.stream().filter(leader4 -> {
                                        return userId2.equals(Convert.toStr(leader4.get("userNum")));
                                    }).findFirst().orElse(null);
                                    currentTeacherAllWork_stu = Convert.toBigDecimal(leaderMap.get("schTeaAveWorkNumber_after"));
                                }
                                BigDecimal currentTeacherAssignedWork_stu = Convert.toBigDecimal(allTeacherAssignedWorkMap.get(schoolNum2 + "_" + userId2), BigDecimal.valueOf(0L));
                                BigDecimal currentTeacherUnassignedWork_stu = currentTeacherAllWork_stu.subtract(currentTeacherAssignedWork_stu);
                                allUnassignedWork_stu = allUnassignedWork_stu.add(currentTeacherUnassignedWork_stu);
                                if (currentTeacherUnassignedWork_stu.compareTo(BigDecimal.valueOf(0L)) == 1) {
                                    oneTeacher.put("currentTeacherUnassignedWork_stu", currentTeacherUnassignedWork_stu);
                                    unfinishedTeaData.add(oneTeacher);
                                }
                            }
                        }
                        BigDecimal finalAllUnassignedWork_stu = allUnassignedWork_stu;
                        unfinishedTeaData.forEach(oneTeacher2 -> {
                            String userId3 = Convert.toStr(oneTeacher2.get("userNum"));
                            String userId4 = StrUtil.isEmpty(userId3) ? "--" : userId3;
                            String schoolNum3 = Convert.toStr(oneTeacher2.get(Const.EXPORTREPORT_schoolNum));
                            BigDecimal currentTeacherUnassignedWork_stu2 = Convert.toBigDecimal(oneTeacher2.get("currentTeacherUnassignedWork_stu"));
                            int workNumber_tishu = Convert.toInt(Convert.toBigDecimal(Integer.valueOf(finalOneQuestionGroupAllWork_tishu)).multiply(currentTeacherUnassignedWork_stu2).divide(finalAllUnassignedWork_stu, 0, 0)).intValue();
                            int gLen = currentGroupNums.length;
                            for (int g = 0; g < gLen; g++) {
                                String groupNum = currentGroupNums[g];
                                if (!"--".equals(userId4)) {
                                    if (g == 0) {
                                        assignedTeaCount_oneSchGroupOneQuesGroup.getAndIncrement();
                                    }
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("id", Long.valueOf(GUID.getGUID()));
                                    hashMap.put("pexamPaperNum", oneQuestionGroup.get("exampaperNum"));
                                    hashMap.put("groupNum", groupNum);
                                    hashMap.put("userNum", userId4);
                                    hashMap.put(Const.EXPORTREPORT_schoolNum, schoolNum3);
                                    hashMap.put("insertUser", loginUserId);
                                    hashMap.put("insertDate", currentTime);
                                    if ("0".equals(isSetTizuzhang_currentGroup)) {
                                        hashMap.put("userType", "0");
                                    } else {
                                        String userType = Convert.toStr(oneTeacher2.get("beikeZuzhang"), "0");
                                        if ("1".equals(userType)) {
                                            Optional res = urInsertParamsList.stream().filter(m7 -> {
                                                return groupNum.equals(Convert.toStr(m7.get("groupNum"))) && schoolNum3.equals(Convert.toStr(m7.get(Const.EXPORTREPORT_schoolNum)));
                                            }).findAny();
                                            if (res.isPresent()) {
                                                hashMap.put("userType", "0");
                                            } else {
                                                hashMap.put("userType", userType);
                                                hashMap.put("ROLE_TIZUZHANG", Const.ROLE_TIZUZHANG);
                                                urInsertParamsList.add(hashMap);
                                            }
                                        } else {
                                            hashMap.put("userType", "0");
                                        }
                                    }
                                    qguParamsList.add(hashMap);
                                    HashMap hashMap2 = new HashMap();
                                    hashMap2.put("pexamPaperNum", oneQuestionGroup.get("exampaperNum"));
                                    hashMap2.put("groupNum", groupNum);
                                    hashMap2.put("num", Integer.valueOf(workNumber_tishu));
                                    hashMap2.put("insertUser", userId4);
                                    hashMap2.put(Const.EXPORTREPORT_schoolNum, schoolNum3);
                                    hashMap2.put("insertDate", currentTime);
                                    quotaParamsList.add(hashMap2);
                                } else {
                                    HashMap hashMap3 = new HashMap();
                                    hashMap3.put("id", Long.valueOf(GUID.getGUID()));
                                    hashMap3.put("pexamPaperNum", oneQuestionGroup.get("exampaperNum"));
                                    hashMap3.put("groupNum", groupNum);
                                    hashMap3.put(Const.EXPORTREPORT_schoolNum, schoolNum3);
                                    hashMap3.put("num", Integer.valueOf(workNumber_tishu));
                                    hashMap3.put("insertUser", loginUserId);
                                    hashMap3.put("insertDate", currentTime);
                                    hashMap3.put("updateUser", loginUserId);
                                    hashMap3.put("updateDate", currentTime);
                                    schoolquotaParamsList.add(hashMap3);
                                }
                            }
                        });
                        if ("1".equals(makType)) {
                            if ("0".equals(judgetype) && assignedTeaCount_oneSchGroupOneQuesGroup.get() < 2) {
                                msg.append(StrUtil.format("【组别-{}】\n\t【题号-{}】分配阅卷员不足（应至少分配2个，请检查！）\n\n", new Object[]{((Map) teaData.get(0)).get("schoolGroupName"), currentGroupName}));
                            } else if (assignedTeaCount_oneSchGroupOneQuesGroup.get() < 3) {
                                msg.append(StrUtil.format("【组别-{}】\n\t【题号-{}】分配阅卷员不足（应至少分配3个，请检查！）\n\n", new Object[]{((Map) teaData.get(0)).get("schoolGroupName"), currentGroupName}));
                            }
                        }
                    } else {
                        int finalCurrentQuestionGroupMaxassignableWork_tishu = currentQuestionGroupMaxassignableWork_tishu;
                        teaData.forEach(oneTeacher3 -> {
                            int randomOrderNum = RandomUtil.randomInt();
                            oneTeacher3.put("randomOrderNum", Integer.valueOf(randomOrderNum));
                            String userId3 = Convert.toStr(oneTeacher3.get("userNum"));
                            String userId4 = StrUtil.isEmpty(userId3) ? "--" : userId3;
                            String schoolNum3 = Convert.toStr(oneTeacher3.get(Const.EXPORTREPORT_schoolNum));
                            BigDecimal currentTeacherAllWork_stu2 = (BigDecimal) schTeaAveWorkNumberMap.get(oneTeacher3.get(Const.EXPORTREPORT_schoolNum));
                            if ("1".equals(Convert.toStr(oneTeacher3.get("isSubjectLeader")))) {
                                Map<String, Object> leaderMap2 = (Map) reduceLeaderList.stream().filter(leader5 -> {
                                    return userId4.equals(Convert.toStr(leader5.get("userNum")));
                                }).findFirst().orElse(null);
                                currentTeacherAllWork_stu2 = Convert.toBigDecimal(leaderMap2.get("schTeaAveWorkNumber_after"));
                            }
                            BigDecimal currentTeacherAssignedWork_stu2 = Convert.toBigDecimal(allTeacherAssignedWorkMap.get(schoolNum3 + "_" + userId4), BigDecimal.valueOf(0L));
                            oneTeacher3.put("currentTeacherAssignedWork_stu_order", currentTeacherAssignedWork_stu2);
                            oneTeacher3.put("currentTeacherIsAssigned", Integer.valueOf(currentTeacherAssignedWork_stu2.compareTo(BigDecimal.valueOf(0L)) == 1 ? 1 : 0));
                            BigDecimal currentTeacherUnassignedWork_stu2 = currentTeacherAllWork_stu2.subtract(currentTeacherAssignedWork_stu2);
                            oneTeacher3.put("currentTeacherUnassignedWork_stu_order", currentTeacherUnassignedWork_stu2);
                        });
                        Comparator<Map<String, Object>> t1 = Comparator.comparing(m12 -> {
                            return Convert.toInt(m12.get("schLevel_order"));
                        });
                        Comparator<Map<String, Object>> t2_1 = Comparator.comparing(m13 -> {
                            return Convert.toInt(m13.get("currentTeacherIsAssigned"), 0);
                        });
                        Comparator<Map<String, Object>> t2_2 = Comparator.comparing(m14 -> {
                            return Convert.toBigDecimal(m14.get("currentTeacherUnassignedWork_stu_order"), BigDecimal.valueOf(0L));
                        });
                        Comparator<Map<String, Object>> t3 = Comparator.comparing(m15 -> {
                            return Convert.toInt(m15.get("sch_order"));
                        });
                        Comparator<Map<String, Object>> t4 = Comparator.comparing(m16 -> {
                            return Convert.toInt(m16.get("beikeZuzhang"));
                        });
                        Comparator<Map<String, Object>> t5 = Comparator.comparing(m17 -> {
                            return Convert.toInt(m17.get("randomOrderNum"), 0);
                        });
                        List<Map<String, Object>> teaData_sort = (List) teaData.stream().sorted(t1.reversed().thenComparing(t2_1.reversed()).thenComparing(t2_2.reversed()).thenComparing(t3).thenComparing(t4.reversed()).thenComparing(t5)).collect(Collectors.toList());
                        teaData_sort.forEach(oneTeacher4 -> {
                            int workNumber_tishu;
                            String userId3 = Convert.toStr(oneTeacher4.get("userNum"));
                            if ("0".equals(Convert.toStr(oneTeacher4.get("isToSchool"))) && StrUtil.isEmpty(userId3)) {
                                return;
                            }
                            String userId4 = StrUtil.isEmpty(userId3) ? "--" : userId3;
                            String schoolNum3 = Convert.toStr(oneTeacher4.get(Const.EXPORTREPORT_schoolNum));
                            BigDecimal currentTeacherAllWork_stu2 = (BigDecimal) schTeaAveWorkNumberMap.get(oneTeacher4.get(Const.EXPORTREPORT_schoolNum));
                            if ("1".equals(Convert.toStr(oneTeacher4.get("isSubjectLeader")))) {
                                Map<String, Object> leaderMap2 = (Map) reduceLeaderList.stream().filter(leader5 -> {
                                    return userId4.equals(Convert.toStr(leader5.get("userNum")));
                                }).findFirst().orElse(null);
                                currentTeacherAllWork_stu2 = Convert.toBigDecimal(leaderMap2.get("schTeaAveWorkNumber_after"));
                            }
                            if (currentTeacherAllWork_stu2.compareTo(BigDecimal.valueOf(0L)) < 1) {
                                return;
                            }
                            Convert.toInt(currentTeacherAllWork_stu2.divide(finalGongzuoliangZhanbi, 0, 0)).intValue();
                            int currentQuestionGroupAassignedWork_tishu = Convert.toInt(allQuestionGroupAssignedWorkMap.get(currentGroupNum), 0).intValue();
                            int currentQuestionGroupUnassignedWork_tishu = finalOneQuestionGroupAllWork_tishu - currentQuestionGroupAassignedWork_tishu;
                            BigDecimal currentTeacherAssignedWork_stu2 = Convert.toBigDecimal(allTeacherAssignedWorkMap.get(schoolNum3 + "_" + userId4), BigDecimal.valueOf(0L));
                            BigDecimal currentTeacherUnassignedWork_stu2 = currentTeacherAllWork_stu2.subtract(currentTeacherAssignedWork_stu2);
                            if (currentQuestionGroupUnassignedWork_tishu > 0 && currentTeacherUnassignedWork_stu2.compareTo(BigDecimal.valueOf(0L)) == 1) {
                                int currentTeacherUnassignedWork_tishu = Convert.toInt(currentTeacherUnassignedWork_stu2.divide(finalGongzuoliangZhanbi, 0, 0)).intValue();
                                if (currentTeacherUnassignedWork_tishu >= currentQuestionGroupUnassignedWork_tishu) {
                                    workNumber_tishu = currentQuestionGroupUnassignedWork_tishu;
                                    if (workNumber_tishu > finalCurrentQuestionGroupMaxassignableWork_tishu) {
                                        workNumber_tishu = finalCurrentQuestionGroupMaxassignableWork_tishu;
                                    }
                                    int currentTeacherUnassignedWork_tishu2 = currentTeacherUnassignedWork_tishu - workNumber_tishu;
                                    if ((currentQuestionGroupUnassignedWork_tishu <= finalOneQuestionGroupAllWork_tishu * ((Double) tiaozhengXishu_new.get()).doubleValue() || currentTeacherUnassignedWork_tishu2 <= finalOneQuestionGroupAllWork_tishu * ((Double) tiaozhengXishu_new.get()).doubleValue()) && currentTeacherUnassignedWork_tishu2 >= workNumber_tishu) {
                                        for (String groupNum : currentGroupNums) {
                                            quotaParamsList.stream().filter(m7 -> {
                                                return groupNum.equals(Convert.toStr(m7.get("groupNum")));
                                            }).forEach(m25 -> {
                                                int num_assigned = Convert.toInt(m25.get("num"), 0).intValue();
                                                int num_add = NumberUtil.ceilDiv(currentQuestionGroupUnassignedWork_tishu * num_assigned, currentQuestionGroupAassignedWork_tishu);
                                                m25.put("num", Integer.valueOf(num_assigned + num_add));
                                            });
                                            schoolquotaParamsList.stream().filter(m8 -> {
                                                return groupNum.equals(Convert.toStr(m8.get("groupNum")));
                                            }).forEach(m26 -> {
                                                int num_assigned = Convert.toInt(m26.get("num"), 0).intValue();
                                                int num_add = NumberUtil.ceilDiv(currentQuestionGroupUnassignedWork_tishu * num_assigned, currentQuestionGroupAassignedWork_tishu);
                                                m26.put("num", Integer.valueOf(num_assigned + num_add));
                                            });
                                        }
                                        allQuestionGroupAssignedWorkMap.put(currentGroupNum, Integer.valueOf(currentQuestionGroupAassignedWork_tishu + workNumber_tishu));
                                        return;
                                    }
                                } else {
                                    workNumber_tishu = currentTeacherUnassignedWork_tishu;
                                    if (workNumber_tishu > finalCurrentQuestionGroupMaxassignableWork_tishu) {
                                        workNumber_tishu = finalCurrentQuestionGroupMaxassignableWork_tishu;
                                    }
                                }
                                allQuestionGroupAssignedWorkMap.put(currentGroupNum, Integer.valueOf(currentQuestionGroupAassignedWork_tishu + workNumber_tishu + 0));
                                BigDecimal workNumber_stu = BigDecimal.valueOf(workNumber_tishu + 0).multiply(finalGongzuoliangZhanbi).setScale(4, 1);
                                allTeacherAssignedWorkMap.put(schoolNum3 + "_" + userId4, currentTeacherAssignedWork_stu2.add(workNumber_stu));
                                int gLen = currentGroupNums.length;
                                for (int g = 0; g < gLen; g++) {
                                    String groupNum2 = currentGroupNums[g];
                                    if (!"--".equals(userId4)) {
                                        if (g == 0) {
                                            assignedTeaCount_oneSchGroupOneQuesGroup.getAndIncrement();
                                        }
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("id", Long.valueOf(GUID.getGUID()));
                                        hashMap.put("pexamPaperNum", oneQuestionGroup.get("exampaperNum"));
                                        hashMap.put("groupNum", groupNum2);
                                        hashMap.put("userNum", userId4);
                                        hashMap.put(Const.EXPORTREPORT_schoolNum, schoolNum3);
                                        hashMap.put("insertUser", loginUserId);
                                        hashMap.put("insertDate", currentTime);
                                        if ("0".equals(isSetTizuzhang_currentGroup)) {
                                            hashMap.put("userType", "0");
                                        } else {
                                            String userType = Convert.toStr(oneTeacher4.get("beikeZuzhang"), "0");
                                            if ("1".equals(userType)) {
                                                Optional res = urInsertParamsList.stream().filter(m9 -> {
                                                    return groupNum2.equals(Convert.toStr(m9.get("groupNum"))) && schoolNum3.equals(Convert.toStr(m9.get(Const.EXPORTREPORT_schoolNum)));
                                                }).findAny();
                                                if (res.isPresent()) {
                                                    hashMap.put("userType", "0");
                                                } else {
                                                    hashMap.put("userType", userType);
                                                    hashMap.put("ROLE_TIZUZHANG", Const.ROLE_TIZUZHANG);
                                                    urInsertParamsList.add(hashMap);
                                                }
                                            } else {
                                                hashMap.put("userType", "0");
                                            }
                                        }
                                        qguParamsList.add(hashMap);
                                        HashMap hashMap2 = new HashMap();
                                        hashMap2.put("pexamPaperNum", oneQuestionGroup.get("exampaperNum"));
                                        hashMap2.put("groupNum", groupNum2);
                                        hashMap2.put("num", Integer.valueOf(workNumber_tishu));
                                        hashMap2.put("insertUser", userId4);
                                        hashMap2.put(Const.EXPORTREPORT_schoolNum, schoolNum3);
                                        hashMap2.put("insertDate", currentTime);
                                        quotaParamsList.add(hashMap2);
                                    } else {
                                        HashMap hashMap3 = new HashMap();
                                        hashMap3.put("id", Long.valueOf(GUID.getGUID()));
                                        hashMap3.put("pexamPaperNum", oneQuestionGroup.get("exampaperNum"));
                                        hashMap3.put("groupNum", groupNum2);
                                        hashMap3.put(Const.EXPORTREPORT_schoolNum, schoolNum3);
                                        hashMap3.put("num", Integer.valueOf(workNumber_tishu));
                                        hashMap3.put("insertUser", loginUserId);
                                        hashMap3.put("insertDate", currentTime);
                                        hashMap3.put("updateUser", loginUserId);
                                        hashMap3.put("updateDate", currentTime);
                                        schoolquotaParamsList.add(hashMap3);
                                    }
                                }
                            }
                        });
                        if ("1".equals(makType)) {
                            if ("0".equals(judgetype) && assignedTeaCount_oneSchGroupOneQuesGroup.get() < 2) {
                                msg.append(StrUtil.format("【组别-{}】\n\t【题号-{}】分配阅卷员不足（应至少分配2个，请检查！）\n\n", new Object[]{((Map) teaData.get(0)).get("schoolGroupName"), currentGroupName}));
                            } else if (assignedTeaCount_oneSchGroupOneQuesGroup.get() < 3) {
                                msg.append(StrUtil.format("【组别-{}】\n\t【题号-{}】分配阅卷员不足（应至少分配3个，请检查！）\n\n", new Object[]{((Map) teaData.get(0)).get("schoolGroupName"), currentGroupName}));
                            }
                        }
                    }
                }
                i++;
            }
            if (CollUtil.isNotEmpty(qguParamsList)) {
                if (!"0".equals(isSetTizuzhang_currentGroup)) {
                    Map<Object, List<Map<String, Object>>> groupLeaderMap = (Map) qguParamsList.stream().collect(Collectors.groupingBy(m25 -> {
                        return m25.get("groupNum") + "_" + m25.get(Const.EXPORTREPORT_schoolNum);
                    }));
                    groupLeaderMap.forEach((key2, dataList) -> {
                        boolean isLeader = dataList.stream().filter(m7 -> {
                            return "1".equals(Convert.toStr(m7.get("userType")));
                        }).findAny().isPresent();
                        if (isLeader) {
                            return;
                        }
                        Optional<Map<String, Object>> num_max_objOpt = quotaParamsList.stream().filter(m8 -> {
                            return key2.equals(m8.get("groupNum") + "_" + m8.get(Const.EXPORTREPORT_schoolNum));
                        }).max(Comparator.comparingInt(m26 -> {
                            return Convert.toInt(m26.get("num"), 0).intValue();
                        }));
                        if (num_max_objOpt.isPresent()) {
                            Map<String, Object> num_max_obj = num_max_objOpt.get();
                            Optional<Map<String, Object>> res = dataList.stream().filter(m9 -> {
                                return Convert.toStr(num_max_obj.get("insertUser"), "").equals(Convert.toStr(m9.get("userNum")));
                            }).findAny();
                            if (res.isPresent()) {
                                Map<String, Object> firstTea = res.get();
                                firstTea.put("userType", "1");
                                qguUpdateParamsList.add(firstTea);
                                firstTea.put("ROLE_TIZUZHANG", Const.ROLE_TIZUZHANG);
                                urInsertParamsList.add(firstTea);
                            }
                        }
                    });
                }
                this.dao._batchExecute(insertQGUSql, qguParamsList, 300);
                if (CollUtil.isNotEmpty(qguUpdateParamsList)) {
                    this.dao._batchExecute(updateQGUSql, qguUpdateParamsList, 300);
                }
                List<Map<String, Object>> urInsertParamsList_new = new ArrayList<>();
                String selSql = "select id from userrole where userNum={userNum} and roleNum={ROLE_TIZUZHANG} limit 1";
                List<Map<String, Object>> tiZuzhangList = (List) urInsertParamsList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> {
                    return new TreeSet(Comparator.comparing(m7 -> {
                        return Convert.toStr(m7.get("userNum"));
                    }));
                }), (v1) -> {
                    return new ArrayList(v1);
                }));
                tiZuzhangList.forEach(ur -> {
                    Object urObj = this.dao._queryObject(selSql, ur);
                    if (null == urObj) {
                        urInsertParamsList_new.add(ur);
                    }
                });
                if (CollUtil.isNotEmpty(urInsertParamsList_new)) {
                    this.dao._batchExecute(insertUserroleSql, urInsertParamsList_new, 300);
                }
            }
            if (CollUtil.isNotEmpty(quotaParamsList)) {
                quotaParamsList.forEach(qo -> {
                    int num_assigned = Convert.toInt(qo.get("num"), 0).intValue();
                    int num_add = NumberUtil.ceilDiv(num_assigned, 1000);
                    qo.put("num", Integer.valueOf(num_assigned + num_add));
                });
                this.dao._batchExecute(insertQuotaSql, quotaParamsList, 300);
                Map<String, Integer> groupSchoolAllNumberMap = (Map) quotaParamsList.stream().collect(Collectors.groupingBy(m7 -> {
                    return m7.get("groupNum") + "_" + m7.get(Const.EXPORTREPORT_schoolNum);
                }, Collectors.summingInt(m26 -> {
                    return Convert.toInt(m26.get("num")).intValue();
                })));
                groupSchoolAllNumberMap.forEach((key3, value2) -> {
                    String[] keyStr = key3.split("_");
                    HashMap hashMap = new HashMap();
                    hashMap.put("id", Long.valueOf(GUID.getGUID()));
                    hashMap.put("pexamPaperNum", epMap.get("pexamPaperNum"));
                    hashMap.put("groupNum", keyStr[0]);
                    hashMap.put(Const.EXPORTREPORT_schoolNum, keyStr[1]);
                    hashMap.put("num", value2);
                    hashMap.put("insertUser", loginUserId);
                    hashMap.put("insertDate", currentTime);
                    hashMap.put("updateUser", loginUserId);
                    hashMap.put("updateDate", currentTime);
                    schoolquotaParamsList.add(hashMap);
                });
            }
            if (CollUtil.isNotEmpty(schoolquotaParamsList)) {
                this.dao._batchExecute(insertSchoolquotaSql, schoolquotaParamsList, 300);
            }
        });
        this.dao._execute("delete ur from userrole ur LEFT JOIN (select userNum from questiongroup_user where userType=1 GROUP BY userNum) qgu on qgu.userNum=ur.userNum where qgu.userNum is null and ur.roleNum='-4'", null);
        return msg.toString();
    }

    public Comparator<Map<String, Object>> getComparator(String quesOrderType, Comparator<Map<String, Object>> cp_gongzuoliangZhanbi, Comparator<Map<String, Object>> cp_makType) {
        if ("0".equals(quesOrderType) || "1".equals(quesOrderType)) {
            return null;
        }
        if ("2".equals(quesOrderType)) {
            return cp_gongzuoliangZhanbi;
        }
        if ("3".equals(quesOrderType)) {
            return cp_gongzuoliangZhanbi.reversed();
        }
        if ("4".equals(quesOrderType)) {
            return cp_makType;
        }
        if ("5".equals(quesOrderType)) {
            return cp_makType.reversed();
        }
        return null;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void adjustSchoolTeacherWork(String examNum, String gradeNum, String subjectNum, String schoolNum, String parentSubjectNum, List<Map<String, Object>> questionGroupDataList, String loginUserId) {
        int workNumber_tishu;
        int workNumber_tishu2;
        Object currentTime = DateUtil.getCurrentTime();
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("parentSubjectNum", parentSubjectNum);
        Map<String, Object> epMap = this.dao._querySimpleMap("select examPaperNum,pexamPaperNum,fenzuyuejuan from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("pexamPaperNum", epMap.get("pexamPaperNum"));
        args.put("examPaperNum", epMap.get("examPaperNum"));
        List<Map<String, Object>> deleteParamsList = new ArrayList<>();
        List<Map<String, Object>> qguParamsList = new ArrayList<>();
        List<Map<String, Object>> qguUpdateParamsList = new ArrayList<>();
        List<Map<String, Object>> urInsertParamsList = new ArrayList<>();
        List<Map<String, Object>> quotaParamsList = new ArrayList<>();
        BigDecimal schoolAllWork_stu = new BigDecimal(0);
        for (int i = 0; i < questionGroupDataList.size(); i++) {
            Map<String, Object> oneQuestionGroup = questionGroupDataList.get(i);
            String str = Convert.toStr(oneQuestionGroup.get("groupNum")).split("_")[0];
            Object makType = oneQuestionGroup.get("makType");
            int oneQuestionGroupAllWork_tishu = Convert.toInt(oneQuestionGroup.get("schoolNumber"), 0).intValue();
            BigDecimal gongzuoliangZhanbi = Convert.toBigDecimal(oneQuestionGroup.get("gongzuoliangZhanbi"), BigDecimal.valueOf(100L)).divide(BigDecimal.valueOf(100L));
            if ("1".equals(makType)) {
                BigDecimal yuguSanpinglv = Convert.toBigDecimal(oneQuestionGroup.get("yuguSanpinglv"), BigDecimal.valueOf(0L)).divide(BigDecimal.valueOf(100L));
                BigDecimal quesZhanbi = BigDecimal.valueOf(2L).add(yuguSanpinglv);
                gongzuoliangZhanbi = gongzuoliangZhanbi.divide(quesZhanbi, 4, 4);
            }
            oneQuestionGroup.put("gongzuoliangZhanbi_final", gongzuoliangZhanbi);
            schoolAllWork_stu = schoolAllWork_stu.add(Convert.toBigDecimal(Integer.valueOf(oneQuestionGroupAllWork_tishu)).multiply(gongzuoliangZhanbi).setScale(4, 0));
        }
        args.put("type", "-2");
        List<Map<String, Object>> allTeaList = this.dao._queryMapList("select ur.userNum,IF(qgu.reduceRate is not null && qgu.reduceRate>0,1,0) isSubjectLeader,qgu.reduceRate from userrole ur LEFT JOIN role r on r.roleNum=ur.roleNum LEFT JOIN `user` u on u.id=ur.userNum LEFT JOIN questiongroup_user qgu on qgu.userNum=u.id and qgu.userType='2' and qgu.exampaperNum={examPaperNum} LEFT JOIN userposition up on up.userNum=u.id and up.type={type} and up.gradeNum={gradeNum} and up.subjectNum={subjectNum}where r.examPaperNum={pexamPaperNum} and r.type='4' and u.schoolNum={schoolNum} GROUP BY u.id ORDER BY IF(up.id is null,1,0),CONVERT(u.realname USING GBK)", TypeEnum.StringObject, args);
        int allTeaCount = CollUtil.isNotEmpty(allTeaList) ? allTeaList.size() : 1;
        BigDecimal schTeaAveWorkNumber = schoolAllWork_stu.divide(Convert.toBigDecimal(Integer.valueOf(allTeaCount)), 4, 0);
        List<Map<String, Object>> reduceLeaderList = (List) allTeaList.stream().filter(tea -> {
            return "1".equals(Convert.toStr(tea.get("isSubjectLeader")));
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(reduceLeaderList)) {
            BigDecimal reduceStuNumber_total = new BigDecimal(0);
            BigDecimal leaderAveWorkNumber_before_total = new BigDecimal(0);
            for (int ll = 0; ll < reduceLeaderList.size(); ll++) {
                Map<String, Object> leader = reduceLeaderList.get(ll);
                BigDecimal reduceRate = Convert.toBigDecimal(leader.get("reduceRate"));
                BigDecimal reduceStuNumber = Convert.toBigDecimal(schTeaAveWorkNumber, BigDecimal.valueOf(0L)).multiply(reduceRate).setScale(4, 0);
                reduceStuNumber_total = reduceStuNumber_total.add(Convert.toBigDecimal(reduceStuNumber));
                leaderAveWorkNumber_before_total = leaderAveWorkNumber_before_total.add(Convert.toBigDecimal(schTeaAveWorkNumber));
                leader.put("schTeaAveWorkNumber_after", schTeaAveWorkNumber.subtract(reduceStuNumber));
            }
            if (reduceStuNumber_total.compareTo(BigDecimal.valueOf(0L)) == 1) {
                BigDecimal shengyuTeaWorkSum = schoolAllWork_stu.subtract(leaderAveWorkNumber_before_total);
                BigDecimal addWorkNumber = reduceStuNumber_total.multiply(schTeaAveWorkNumber).divide(shengyuTeaWorkSum, 4, 0);
                schTeaAveWorkNumber = schTeaAveWorkNumber.add(addWorkNumber);
            }
        }
        Map<Object, Integer> allQuestionGroupAssignedWorkMap = new HashMap<>();
        Map<Object, BigDecimal> allTeacherAssignedWorkMap = new HashMap<>();
        Set<String> yipanUserSet = new HashSet<>();
        for (int i2 = 0; i2 < questionGroupDataList.size(); i2++) {
            Map<String, Object> oneQuestionGroup2 = questionGroupDataList.get(i2);
            String groupNums = Convert.toStr(oneQuestionGroup2.get("groupNum"));
            String[] currentGroupNums = groupNums.split("_");
            String currentGroupNum = currentGroupNums[0];
            oneQuestionGroup2.get("makType");
            String choosename = Convert.toStr(oneQuestionGroup2.get("choosename"));
            BigDecimal gongzuoliangZhanbi_final = Convert.toBigDecimal(oneQuestionGroup2.get("gongzuoliangZhanbi_final"));
            for (String groupNum : currentGroupNums) {
                Map args_deleteSql = new HashMap();
                args_deleteSql.put("pexamPaperNum", epMap.get("pexamPaperNum"));
                args_deleteSql.put("groupNum", groupNum);
                deleteParamsList.add(args_deleteSql);
            }
            String groupNumStr = " groupNum={currentGroupNum} ";
            if (!"s".equals(choosename)) {
                groupNumStr = " groupNum in ({groupNums[]}) ";
            }
            String teaSql = "select t.updateUser userNum,t.yipanNumber,IF(up.id is null,0,1) beikeZuzhang from (select updateUser,count(DISTINCT studentId) yipanNumber from task where exampaperNum={pexamPaperNum} and " + groupNumStr + " and `status`='T' GROUP BY updateUser) t left join `user` u on u.id=t.updateUser LEFT JOIN userposition up on up.userNum=u.id and up.type={type} and up.gradeNum={gradeNum} and up.subjectNum={subjectNum} where u.schoolNum={schoolNum} GROUP BY u.id ORDER BY t.yipanNumber desc,IF(up.id is null,1,0),CONVERT(u.realname USING GBK)";
            Map args_teaSql = new HashMap();
            args_teaSql.put("currentGroupNum", currentGroupNum);
            args_teaSql.put("groupNums", groupNums);
            args_teaSql.put("pexamPaperNum", epMap.get("pexamPaperNum"));
            args_teaSql.put("type", "-2");
            args_teaSql.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            args_teaSql.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args_teaSql.put(Const.EXPORTREPORT_schoolNum, schoolNum);
            List<Map<String, Object>> teaData = this.dao._queryMapList(teaSql, TypeEnum.StringObject, args);
            int oneQuestionGroupAllWork_tishu2 = Convert.toInt(oneQuestionGroup2.get("schoolNumber"), 0).intValue();
            int currentQuestionGroupAassignedWork_tishu = ((Integer) teaData.stream().collect(Collectors.summingInt(m -> {
                return Convert.toInt(m.get("yipanNumber"), 0).intValue();
            }))).intValue();
            for (int j = 0; j < teaData.size(); j++) {
                Map<String, Object> oneTeacher = teaData.get(j);
                Object userId = oneTeacher.get("userNum");
                yipanUserSet.add(Convert.toStr(userId));
                int oneTeaYipanNumber = Convert.toInt(oneTeacher.get("yipanNumber")).intValue();
                BigDecimal oneTeaYipanNumber_stu = BigDecimal.valueOf(oneTeaYipanNumber).multiply(gongzuoliangZhanbi_final).setScale(4, 1);
                BigDecimal currentTeacherAllWork_stu = schTeaAveWorkNumber;
                Map<String, Object> leaderMap = reduceLeaderList.stream().filter(leader2 -> {
                    return Convert.toStr(userId).equals(Convert.toStr(leader2.get("userNum")));
                }).findFirst().orElse(null);
                if (CollUtil.isNotEmpty(leaderMap)) {
                    currentTeacherAllWork_stu = Convert.toBigDecimal(leaderMap.get("schTeaAveWorkNumber_after"));
                }
                BigDecimal currentTeacherAssignedWork_stu = Convert.toBigDecimal(allTeacherAssignedWorkMap.get(userId), BigDecimal.valueOf(0L)).add(Convert.toBigDecimal(oneTeaYipanNumber_stu));
                BigDecimal currentTeacherUnassignedWork_stu = currentTeacherAllWork_stu.subtract(currentTeacherAssignedWork_stu);
                int currentQuestionGroupUnassignedWork_tishu = oneQuestionGroupAllWork_tishu2 - currentQuestionGroupAassignedWork_tishu;
                if (currentQuestionGroupUnassignedWork_tishu > 0) {
                    if (currentTeacherUnassignedWork_stu.compareTo(BigDecimal.valueOf(0L)) == 1) {
                        int currentTeacherUnassignedWork_tishu = Convert.toInt(currentTeacherUnassignedWork_stu.divide(gongzuoliangZhanbi_final, 0, 0)).intValue();
                        if (currentTeacherUnassignedWork_tishu >= currentQuestionGroupUnassignedWork_tishu) {
                            workNumber_tishu2 = currentQuestionGroupUnassignedWork_tishu;
                        } else {
                            workNumber_tishu2 = currentTeacherUnassignedWork_tishu;
                        }
                    } else {
                        workNumber_tishu2 = oneTeaYipanNumber;
                    }
                } else {
                    workNumber_tishu2 = oneTeaYipanNumber;
                }
                currentQuestionGroupAassignedWork_tishu += workNumber_tishu2;
                BigDecimal workNumber_stu = BigDecimal.valueOf(workNumber_tishu2).multiply(gongzuoliangZhanbi_final).setScale(4, 1);
                allTeacherAssignedWorkMap.put(userId, currentTeacherAssignedWork_stu.add(workNumber_stu));
                for (String groupNum2 : currentGroupNums) {
                    Map args_insertQGUSql = new HashMap();
                    args_insertQGUSql.put("id", Long.valueOf(GUID.getGUID()));
                    args_insertQGUSql.put("pexamPaperNum", epMap.get("pexamPaperNum"));
                    args_insertQGUSql.put("groupNum", groupNum2);
                    args_insertQGUSql.put("userNum", userId);
                    args_insertQGUSql.put("insertUser", loginUserId);
                    args_insertQGUSql.put("insertDate", currentTime);
                    String userType = Convert.toStr(oneTeacher.get("beikeZuzhang"), "0");
                    args_insertQGUSql.put("userType", userType);
                    if ("1".equals(userType)) {
                        args_insertQGUSql.put("ROLE_TIZUZHANG", Const.ROLE_TIZUZHANG);
                        urInsertParamsList.add(args_insertQGUSql);
                    }
                    qguParamsList.add(args_insertQGUSql);
                    Map args_insertQuotaSql = new HashMap();
                    args_insertQuotaSql.put("pexamPaperNum", epMap.get("pexamPaperNum"));
                    args_insertQuotaSql.put("groupNum", groupNum2);
                    args_insertQuotaSql.put("num", Integer.valueOf(workNumber_tishu2));
                    args_insertQuotaSql.put("insertUser", userId);
                    args_insertQuotaSql.put("insertDate", currentTime);
                    quotaParamsList.add(args_insertQuotaSql);
                }
            }
            allQuestionGroupAssignedWorkMap.put(currentGroupNum, Integer.valueOf(currentQuestionGroupAassignedWork_tishu));
        }
        for (int i3 = 0; i3 < questionGroupDataList.size(); i3++) {
            Map<String, Object> oneQuestionGroup3 = questionGroupDataList.get(i3);
            String[] currentGroupNums2 = Convert.toStr(oneQuestionGroup3.get("groupNum")).split("_");
            String currentGroupNum2 = currentGroupNums2[0];
            oneQuestionGroup3.get("makType");
            Convert.toStr(oneQuestionGroup3.get("choosename"));
            BigDecimal gongzuoliangZhanbi_final2 = Convert.toBigDecimal(oneQuestionGroup3.get("gongzuoliangZhanbi_final"));
            int oneQuestionGroupAllWork_tishu3 = Convert.toInt(oneQuestionGroup3.get("schoolNumber"), 0).intValue();
            int currentQuestionGroupAassignedWork_tishu2 = Convert.toInt(allQuestionGroupAssignedWorkMap.get(currentGroupNum2), 0).intValue();
            for (int j2 = 0; j2 < allTeaList.size(); j2++) {
                Map<String, Object> oneTeacher2 = allTeaList.get(j2);
                Object userId2 = oneTeacher2.get("userNum");
                if (!yipanUserSet.contains(Convert.toStr(userId2))) {
                    BigDecimal currentTeacherAllWork_stu2 = schTeaAveWorkNumber;
                    Map<String, Object> leaderMap2 = reduceLeaderList.stream().filter(leader3 -> {
                        return Convert.toStr(userId2).equals(Convert.toStr(leader3.get("userNum")));
                    }).findFirst().orElse(null);
                    if (CollUtil.isNotEmpty(leaderMap2)) {
                        currentTeacherAllWork_stu2 = Convert.toBigDecimal(leaderMap2.get("schTeaAveWorkNumber_after"));
                    }
                    BigDecimal currentTeacherAssignedWork_stu2 = Convert.toBigDecimal(allTeacherAssignedWorkMap.get(userId2), BigDecimal.valueOf(0L));
                    BigDecimal currentTeacherUnassignedWork_stu2 = currentTeacherAllWork_stu2.subtract(currentTeacherAssignedWork_stu2);
                    int currentQuestionGroupUnassignedWork_tishu2 = oneQuestionGroupAllWork_tishu3 - currentQuestionGroupAassignedWork_tishu2;
                    if (currentQuestionGroupUnassignedWork_tishu2 <= 0) {
                        break;
                    }
                    if (currentTeacherUnassignedWork_stu2.compareTo(BigDecimal.valueOf(0L)) >= 1) {
                        int currentTeacherUnassignedWork_tishu2 = Convert.toInt(currentTeacherUnassignedWork_stu2.divide(gongzuoliangZhanbi_final2, 0, 0)).intValue();
                        if (currentTeacherUnassignedWork_tishu2 >= currentQuestionGroupUnassignedWork_tishu2) {
                            workNumber_tishu = currentQuestionGroupUnassignedWork_tishu2;
                        } else {
                            workNumber_tishu = currentTeacherUnassignedWork_tishu2;
                        }
                        currentQuestionGroupAassignedWork_tishu2 += workNumber_tishu;
                        BigDecimal workNumber_stu2 = BigDecimal.valueOf(workNumber_tishu).multiply(gongzuoliangZhanbi_final2).setScale(4, 1);
                        allTeacherAssignedWorkMap.put(userId2, currentTeacherAssignedWork_stu2.add(workNumber_stu2));
                        for (String groupNum3 : currentGroupNums2) {
                            Map args_insertQGUSql2 = new HashMap();
                            args_insertQGUSql2.put("id", Long.valueOf(GUID.getGUID()));
                            args_insertQGUSql2.put("pexamPaperNum", epMap.get("pexamPaperNum"));
                            args_insertQGUSql2.put("groupNum", groupNum3);
                            args_insertQGUSql2.put("userNum", userId2);
                            args_insertQGUSql2.put("insertUser", loginUserId);
                            args_insertQGUSql2.put("insertDate", currentTime);
                            String userType2 = Convert.toStr(oneTeacher2.get("beikeZuzhang"), "0");
                            args_insertQGUSql2.put("userType", userType2);
                            if ("1".equals(userType2)) {
                                args_insertQGUSql2.put("ROLE_TIZUZHANG", Const.ROLE_TIZUZHANG);
                                urInsertParamsList.add(args_insertQGUSql2);
                            }
                            qguParamsList.add(args_insertQGUSql2);
                            Map args_insertQuotaSql2 = new HashMap();
                            args_insertQuotaSql2.put("pexamPaperNum", epMap.get("pexamPaperNum"));
                            args_insertQuotaSql2.put("groupNum", groupNum3);
                            args_insertQuotaSql2.put("num", Integer.valueOf(workNumber_tishu));
                            args_insertQuotaSql2.put("insertUser", userId2);
                            args_insertQuotaSql2.put("insertDate", currentTime);
                            quotaParamsList.add(args_insertQuotaSql2);
                        }
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(deleteParamsList)) {
            this.dao._batchExecute("delete from questiongroup_user where exampaperNum={pexamPaperNum} and groupNum={groupNum} and userType!='2'", deleteParamsList);
            this.dao._batchExecute("delete from quota where exampaperNum={pexamPaperNum} and groupNum={groupNum}", deleteParamsList);
        }
        if (CollUtil.isNotEmpty(qguParamsList)) {
            Map<Object, List<Map<String, Object>>> groupLeaderMap = (Map) qguParamsList.stream().filter(m2 -> {
                return "0".equals(Convert.toStr(m2.get("userType")));
            }).collect(Collectors.groupingBy(m22 -> {
                return m22.get("groupNum");
            }));
            groupLeaderMap.forEach((key, dataList) -> {
                Map<String, Object> firstTea = (Map) dataList.get(0);
                firstTea.put("userType", "1");
                qguUpdateParamsList.add(firstTea);
                firstTea.put("ROLE_TIZUZHANG", Const.ROLE_TIZUZHANG);
                urInsertParamsList.add(firstTea);
            });
            this.dao._batchExecute("insert into questiongroup_user (id,exampaperNum,groupNum,userType,userNum,insertUser,insertDate,isFinished) values ({id},{pexamPaperNum},{groupNum},{userType},{userNum},{insertUser},{insertDate},0)", qguParamsList);
            if (CollUtil.isNotEmpty(qguUpdateParamsList)) {
                this.dao._batchExecute("update questiongroup_user set userType={userType} where id={id}", qguUpdateParamsList);
            }
            List<Map<String, Object>> urInsertParamsList_new = new ArrayList<>();
            String selSql = "select id from userrole where userNum={userNum} and roleNum={ROLE_TIZUZHANG} limit 1";
            List<Map<String, Object>> tiZuzhangList = (List) urInsertParamsList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> {
                return new TreeSet(Comparator.comparing(m3 -> {
                    return Convert.toStr(m3.get("userNum"));
                }));
            }), (v1) -> {
                return new ArrayList(v1);
            }));
            tiZuzhangList.forEach(ur -> {
                Object urObj = this.dao._queryObject(selSql, ur);
                if (null == urObj) {
                    urInsertParamsList_new.add(ur);
                }
            });
            this.dao._batchExecute("insert into userrole (userNum,roleNum,insertUser,insertDate,isDelete) values ({userNum},{ROLE_TIZUZHANG},{insertUser},{insertDate},'F')", urInsertParamsList_new);
            this.dao._execute("delete ur from userrole ur LEFT JOIN (select userNum from questiongroup_user where userType=1 GROUP BY userNum) qgu on qgu.userNum=ur.userNum where qgu.userNum is null and ur.roleNum='-4'", null);
        }
        if (CollUtil.isNotEmpty(quotaParamsList)) {
            this.dao._batchExecute("insert into quota (exampaperNum,groupNum,num,insertUser,insertDate) values ({pexamPaperNum},{groupNum},{num},{insertUser},{insertDate})", quotaParamsList);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getAdjustSchoolList(String examNum, String gradeNum, String sonSubjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, sonSubjectNum);
        Object examPaperNum = this.dao._queryObject("select examPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("examPaperNum", examPaperNum);
        return this.dao._queryMapList("select sq.schoolNum,sch.schoolName from schoolquota sq inner join (select id from define where category={examPaperNum} union select id from subdefine where category={examPaperNum}) d on d.id=sq.groupNum inner join school sch on sch.id=sq.schoolNum group by sq.schoolNum order by convert(sch.schoolName using gbk)", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getSchoolQuestionGroupTableData(String examNum, String gradeNum, String subjectNum, String schoolNum) {
        return null;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> ajaxSchool(String examNum, String gradeNum, String subjectNum, String userId) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("userId", userId);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        Object pexamPaperNum = this.dao._queryObject("select pexamPaperNum from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("pexamPaperNum", pexamPaperNum);
        String schStr = "";
        if (!"-1".equals(userId) && !"-2".equals(userId) && null == map) {
            schStr = " inner join (  SELECT schoolnum schoolNum from user where id={userId}  UNION   SELECT schoolNum from schoolscanpermission where userNum={userId}  )s on s.schoolNum = sq.schoolNum  right join examschool es on es.examNum={examNum} and es.schoolNum=s.schoolNum ";
        }
        String sql = "select distinct sch.id schoolNum,sch.schoolName from schoolquota sq inner join school sch on sch.id=sq.schoolNum " + schStr + "where sq.exampaperNum={pexamPaperNum} order by convert(sch.schoolName using gbk)";
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> ajaxSchoolGroup(String examNum, String gradeNum, String subjectNum, String userId) {
        String sql2;
        String sql22;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("userId", userId);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        Map<String, Object> epMap = this.dao._querySimpleMap("select pexamPaperNum,fenzuyuejuan from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        Object pexamPaperNum = epMap.get("pexamPaperNum");
        args.put("pexamPaperNum", pexamPaperNum);
        if ("1".equals(Convert.toStr(epMap.get("fenzuyuejuan")))) {
            List<Map<String, Object>> assignedGroupList = this.dao._queryMapList("select distinct IFNULL(sg.schoolGroupNum,'-1') schoolGroupNum,IFNULL(sg.schoolGroupName,'未分组') schoolGroupName from schoolquota sq inner join school sch on sch.id=sq.schoolNum left join schoolgroup sg on sg.schoolNum=sq.schoolNum where sq.exampaperNum={pexamPaperNum} order by IF(sg.schoolGroupNum is null,1,0),convert(sg.schoolGroupName using gbk)", TypeEnum.StringObject, args);
            if ("-1".equals(userId) || "-2".equals(userId)) {
                return assignedGroupList;
            }
            List<Map<String, Object>> groupSchoolList = this.dao._queryMapList("select IFNULL(sg.schoolGroupNum,'-1') schoolGroupNum,sch.id schoolNum from (select distinct schoolNum from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}) en inner join school sch on sch.id=en.schoolNum left join schoolgroup sg on sg.schoolNum=sch.id ", TypeEnum.StringObject, args);
            if ("-1".equals(userId) && !"-2".equals(userId) && null == map) {
                sql22 = " SELECT schoolNum from schoolscanpermission where userNum={userId} ";
            } else {
                sql22 = "SELECT id schoolNum from school  ";
            }
            List<Object> schoolList = this.dao._queryColList(sql22, Object.class, args);
            List<Map<String, Object>> newList = new ArrayList<>();
            assignedGroupList.forEach(oneGroup -> {
                String schoolGroupNum = Convert.toStr(oneGroup.get("schoolGroupNum"));
                List<Map<String, Object>> oneGroupSchoolList = (List) groupSchoolList.stream().filter(g -> {
                    return schoolGroupNum.equals(g.get("schoolGroupNum"));
                }).collect(Collectors.toList());
                int addFlag = 1;
                int i = 0;
                while (true) {
                    if (i >= oneGroupSchoolList.size()) {
                        break;
                    }
                    Map<String, Object> sch = oneGroupSchoolList.get(i);
                    if (schoolList.contains(sch.get(Const.EXPORTREPORT_schoolNum))) {
                        i++;
                    } else {
                        addFlag = 0;
                        break;
                    }
                }
                if (addFlag == 1) {
                    newList.add(oneGroup);
                }
            });
            return newList;
        }
        List<Map<String, Object>> assignedGroupList2 = new ArrayList<>();
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put("schoolGroupNum", "-2");
        groupMap.put("schoolGroupName", "全部");
        assignedGroupList2.add(groupMap);
        if ("-1".equals(userId) || "-2".equals(userId)) {
            return assignedGroupList2;
        }
        Collection<?> groupSchoolList2 = this.dao._queryColList("select sch.id schoolNum from (select distinct schoolNum from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}) en inner join school sch on sch.id=en.schoolNum ", Object.class, args);
        if ("-1".equals(userId) && !"-2".equals(userId) && null == map) {
            sql2 = "SELECT schoolnum schoolNum from user where id={userId} ";
        } else {
            sql2 = " SELECT schoolNum from schoolscanpermission where userNum={userId} ";
        }
        List<Object> schoolList2 = this.dao._queryColList(sql2, Object.class, args);
        if (schoolList2.containsAll(groupSchoolList2)) {
            return assignedGroupList2;
        }
        return null;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> ajaxSchoolList(String examNum, String gradeNum, String subjectNum, String groupNum, String userId) {
        String userSql;
        Exampaper exampaper = getexamPaperNum(examNum, gradeNum, subjectNum, null);
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("schoolGroupNum", groupNum);
        args.put("examPaperNum", exampaper.getExamPaperNum());
        args.put("userId", userId);
        Map<String, Object> map = this.dao._querySimpleMap("SELECT id from schoolscanpermission WHERE userNum={userId} and type=1 limit 1", args);
        if (!"-1".equals(userId) && !"-2".equals(userId) && null == map) {
            userSql = "inner join (select schoolNum from schoolscanpermission where userNum={userId}  union select  schoolNum from user where id={userId} ) s on CAST(u.schoolNum as char)=CAST(s.schoolNum  as char)  left join school sch on sch.id=s.schoolNum ";
        } else {
            userSql = "inner join school sch on sch.id=u.schoolNum ";
        }
        if ("-2".equals(groupNum)) {
            List<Map<String, Object>> allList = new ArrayList<>();
            String sql = "select '-2' schoolGroupNum,sch.id schoolNum,sch.schoolName from  (select distinct schoolNum from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}) u  " + userSql;
            List<Map<String, Object>> list1 = this.dao._queryMapList(sql, null, args);
            if (CollUtil.isNotEmpty(list1)) {
                allList.addAll(list1);
            }
            String sql2 = "select '-2' schoolGroupNum,sch.id schoolNum,sch.schoolName from  (select distinct userNum from questiongroup_user where exampaperNum={examPaperNum} and userType<>2 ) qgu  inner join user u on u.id=qgu.userNum " + userSql;
            List<Map<String, Object>> list2 = this.dao._queryMapList(sql2, null, args);
            if (CollUtil.isNotEmpty(list2)) {
                allList.addAll(list2);
            }
            if (CollUtil.isNotEmpty(allList)) {
                Comparator<Map<String, Object>> order = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
                allList = (List) allList.stream().distinct().sorted(order).collect(Collectors.toList());
            }
            return allList;
        }
        List<Map<String, Object>> allList2 = new ArrayList<>();
        String sgWStr = "-1".equals(groupNum) ? " sg.schoolGroupNum is null " : " sg.schoolGroupNum={schoolGroupNum} ";
        String sql3 = "select IFNULL(sg.schoolGroupNum,'-1') schoolGroupNum,sch.id schoolNum,sch.schoolName from  (select distinct schoolNum from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}) u  " + userSql + "left join schoolgroup sg on sg.schoolNum=sch.id WHERE " + sgWStr;
        List<Map<String, Object>> list12 = this.dao._queryMapList(sql3, null, args);
        if (CollUtil.isNotEmpty(list12)) {
            allList2.addAll(list12);
        }
        String sql22 = "select IFNULL(sg.schoolGroupNum,'-1') schoolGroupNum,sch.id schoolNum,sch.schoolName from  (select distinct userNum from questiongroup_user where exampaperNum={examPaperNum} and userType<>2 ) qgu  inner join user u on u.id=qgu.userNum " + userSql + "left join schoolgroup sg on sg.schoolNum=sch.id WHERE " + sgWStr;
        List<Map<String, Object>> list22 = this.dao._queryMapList(sql22, null, args);
        if (CollUtil.isNotEmpty(list22)) {
            allList2.addAll(list22);
        }
        if (CollUtil.isNotEmpty(allList2)) {
            Comparator<Map<String, Object>> order2 = ChineseCharacterUtil.sortByPinyinOfMap("schoolName");
            allList2 = (List) allList2.stream().distinct().sorted(order2).collect(Collectors.toList());
        }
        return allList2;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public int ajaxSchoolGroupStudent(String examNum, String gradeNum, String subjectNum, String schoolGroupNum) {
        String sql;
        Map args = new HashMap();
        args.put("schoolGroupNum", schoolGroupNum);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        if ("-2".equals(schoolGroupNum)) {
            sql = "select count(distinct studentId) groupStuCount from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}";
        } else {
            String sgWStr = "-1".equals(schoolGroupNum) ? " where sg.schoolGroupNum is null " : " where sg.schoolGroupNum={schoolGroupNum} ";
            sql = "select sum(en.schCount) groupStuCount from (select schoolNum,count(distinct studentId) schCount from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} group by schoolNum) en left join schoolgroup sg on sg.schoolNum=en.schoolNum " + sgWStr + "group by sg.schoolGroupNum";
        }
        return Convert.toInt(this.dao._queryObject(sql, args), 0).intValue();
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getExamSchoolgroupSetTableData(String examNum, String gradeNum, String subjectNum, String parent_subjectNum, String isFenzuyuejuan) {
        String sql;
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put("parent_subjectNum", parent_subjectNum);
        Map<String, Object> epMap = this.dao._querySimpleMap("select examPaperNum,pexamPaperNum,isHidden from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} ", args);
        args.put("pexamPaperNum", epMap.get("pexamPaperNum"));
        args.put("examPaperNum", epMap.get("examPaperNum"));
        String ur_subStr = "";
        if ("T".equals(Convert.toStr(epMap.get("isHidden")))) {
            ur_subStr = " inner join userrole_sub urs on urs.userNum=ur.userNum and urs.exampaperNum={examPaperNum} ";
        }
        if ("1".equals(isFenzuyuejuan)) {
            sql = "select distinct IFNULL(sg.schoolGroupNum,-1) schoolGroupNum,IFNULL(sg.schoolGroupName,'未分组') schoolGroupName,IFNULL(esgs.isAutoDistribute,'1') isAutoDistribute,IFNULL(esgs.isToSchool,'1') isToSchool,esgs.isSetTizuzhang from (select distinct schoolNum from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={parent_subjectNum} group by schoolNum union all select distinct u.schoolnum from userrole ur " + ur_subStr + "LEFT JOIN role r on r.roleNum=ur.roleNum LEFT JOIN `user` u on u.id=ur.userNum where r.examPaperNum={pexamPaperNum} and r.type='4' and u.id is not null ) en LEFT JOIN schoolgroup sg on sg.schoolNum=en.schoolNum LEFT JOIN (select schoolGroupNum,isAutoDistribute,isToSchool,isSetTizuzhang from examschoolgroupsetting where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}) esgs on esgs.schoolGroupNum=sg.schoolGroupNum LEFT JOIN school sch on sch.id=en.schoolNum WHERE sch.isDelete='F' GROUP BY IFNULL(sg.schoolGroupNum,-1) ORDER BY IF(sg.id is null,1,0),CONVERT(sg.schoolGroupName USING GBK)";
        } else {
            sql = "select distinct schoolGroupNum,'全部' schoolGroupName,isAutoDistribute,isToSchool,isSetTizuzhang from examschoolgroupsetting where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and schoolGroupNum=-1";
        }
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String isFenzuyuejuan(String examNum, String gradeNum, String parentSubjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("parentSubjectNum", parentSubjectNum);
        String num = this.dao._queryStr("select fenzuyuejuan from exampaper where examNum={examNum} and gradeNum={gradeNum} and subjectNum={parentSubjectNum} ", args);
        if ("1".equals(num)) {
            String sgId = this.dao._queryStr("select sg.id from schoolgroup sg left join school sch on sch.id=sg.schoolNum where sch.isDelete='F' limit 1", null);
            if (StrUtil.isEmpty(sgId)) {
                num = "0";
            }
        }
        return num;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void submitExamSchoolgroupSetTableData(String examNum, String gradeNum, String subjectNum, List<Map<String, Object>> examSchoolgroupSetTableDataList, String userId) {
        String currentTime = DateUtil.getCurrentTime();
        examSchoolgroupSetTableDataList.forEach(obj -> {
            obj.put("id", Long.valueOf(GUID.getGUID()));
            obj.put(Const.EXPORTREPORT_examNum, examNum);
            obj.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            obj.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            obj.put("insertUser", userId);
            obj.put("insertDate", currentTime);
        });
        if (CollUtil.isNotEmpty(examSchoolgroupSetTableDataList)) {
            this.dao._batchExecute("delete from examschoolgroupsetting where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", examSchoolgroupSetTableDataList);
            this.dao._batchExecute("insert into examschoolgroupsetting (id,examNum,gradeNum,subjectNum,schoolGroupNum,isAutoDistribute,isToSchool,isSetTizuzhang,insertUser,insertDate) values ({id},{examNum},{gradeNum},{subjectNum},{schoolGroupNum},{isAutoDistribute},{isToSchool},{isSetTizuzhang},{insertUser},{insertDate})", examSchoolgroupSetTableDataList);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getLeiCengSchool(String topItemId, String sousuo) {
        String str;
        if (topItemId.equals("0")) {
            str = " 1=1";
        } else {
            str = " topItemId={topItemId}";
        }
        String sql = "select DISTINCT sItemId,sItemName from statisticitem_school where " + str + " and statisticItem='01' and sItemName like {sousuo}";
        Map args = new HashMap();
        args.put("topItemId", topItemId);
        args.put("sousuo", "%" + sousuo + "%");
        List<Map<String, Object>> leiCengSchoolMapList = this.dao._queryMapList(sql, TypeEnum.StringObject, args);
        return leiCengSchoolMapList;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getTeacherLeiCengSchool(String topItemId, String sousuo, String tid) {
        String str;
        if (topItemId.equals("0")) {
            str = " 1=1";
        } else {
            str = " topItemId={topItemId}";
        }
        String sql = "select DISTINCT sItemId,sItemName  from schauthormanage s inner join school c on c.id=s.schoolNum left join statisticitem si on c.id=si.sItemId where teacherId={tid} and " + str + " and statisticItem='01' and sItemName like {sousuo}";
        Map args = new HashMap();
        args.put("tid", tid);
        args.put("topItemId", topItemId);
        args.put("sousuo", "%" + sousuo + "%");
        List<Map<String, Object>> teacherLeiCengSchoolMapList = this.dao._queryMapList(sql, TypeEnum.StringObject, args);
        return teacherLeiCengSchoolMapList;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getTeacherLeiCengSchoolScan(String topItemId, String sousuo, String tid, String loginUser) {
        String str;
        if (topItemId.equals("0")) {
            str = " 1=1";
        } else {
            str = " topItemId={topItemId}";
        }
        String sql = "select DISTINCT sItemId,sItemName  from schoolscanpermission s inner join school c on c.id=s.schoolNum left join statisticitem si on c.id=si.sItemId where s.userNum={tid} and " + str + " and statisticItem='01' and sItemName like {sousuo}";
        Map args = new HashMap();
        args.put("tid", tid);
        args.put("topItemId", topItemId);
        args.put("sousuo", "%" + sousuo + "%");
        List<Map<String, Object>> teacherLeiCengSchoolMapList = this.dao._queryMapList(sql, TypeEnum.StringObject, args);
        return teacherLeiCengSchoolMapList;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String importyuejuanteacher(List<List<Object>> list, String examNum, String loginUserNum) {
        String rsql;
        String insertrole;
        String msg = "导入成功";
        for (int i = 0; i < list.size(); i++) {
            Object subjectName = list.get(i).get(0).toString();
            Object gradeName = list.get(i).get(1).toString();
            String schoolName = list.get(i).get(2).toString();
            Object teacherNum = list.get(i).get(3).toString();
            Object teacherName = list.get(i).get(4).toString();
            Map<String, Object> rowmap = new HashMap<>();
            rowmap.put("subjectName", subjectName);
            rowmap.put("gradeName", gradeName);
            rowmap.put("schoolName", schoolName);
            rowmap.put("teacherNum", teacherNum);
            rowmap.put("teacherName", teacherName);
            rowmap.put(Const.EXPORTREPORT_examNum, examNum);
            rowmap.put("loginUserNum", loginUserNum);
            List<Map<String, Object>> schoollist = this.dao._queryMapList("select DISTINCT schoolNum FROM schauthormanage where userId={loginUserNum} UNION select schoolNum from user where id={loginUserNum} ", null, rowmap);
            List<Map<String, Object>> gralist = this.dao._queryMapList("SELECT gradeNum from exampaper WHERE gradeNum=(SELECT DISTINCT gradeNum from grade WHERE gradeName={gradeName}) and examNum={examNum}", null, rowmap);
            if (gralist.size() > 0) {
                String gradeNum = gralist.get(0).get(Const.EXPORTREPORT_gradeNum).toString();
                rowmap.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            } else {
                msg = "当前文件第" + (i + 2) + "行，第2列年级不正确";
                throwException(msg);
            }
            List<Map<String, Object>> sublist = this.dao._queryMapList("SELECT subjectNum,isHidden,examPaperNum,pexamPaperNum from exampaper WHERE subjectNum=(SELECT subjectNum from `subject` WHERE subjectName={subjectName}) and examNum={examNum}  and gradeNum={gradeNum}", null, rowmap);
            String isHidden = "";
            if (sublist.size() > 0) {
                String subjectNum = sublist.get(0).get(Const.EXPORTREPORT_subjectNum).toString();
                isHidden = sublist.get(0).get("isHidden").toString();
                String examPaperNum = sublist.get(0).get("examPaperNum").toString();
                String pexamPaperNum = sublist.get(0).get("pexamPaperNum").toString();
                rowmap.put(Const.EXPORTREPORT_subjectNum, subjectNum);
                rowmap.put("exampaperNum", examPaperNum);
                rowmap.put("pexampaperNum", pexamPaperNum);
            } else {
                msg = "当前文件第" + (i + 2) + "行，第1列科目不正确";
                throwException(msg);
            }
            List<Map<String, Object>> urlist = this.dao._queryMapList("SELECT id userNum,schoolnum from `user` WHERE userId=(SELECT id from teacher WHERE teacherName={teacherName} AND teacherNum={teacherNum} ) and usertype='1' ", null, rowmap);
            String schoolNum = "";
            if (urlist.size() > 0) {
                String userNum = urlist.get(0).get("userNum").toString();
                schoolNum = urlist.get(0).get(Const.EXPORTREPORT_schoolNum).toString();
                rowmap.put("userNum", userNum);
                rowmap.put(Const.EXPORTREPORT_schoolNum, schoolNum);
            } else {
                msg = "当前文件第" + (i + 2) + "行的教师工号和教师姓名不存在";
                throwException(msg);
            }
            List<Map<String, Object>> schlist = this.dao._queryMapList("SELECT id from school WHERE schoolName={schoolName}", null, rowmap);
            int schflag = 0;
            for (int j = 0; j < schoollist.size(); j++) {
                if (schoollist.get(j).get(Const.EXPORTREPORT_schoolNum).toString().equals(schoolNum)) {
                    schflag = 1;
                }
            }
            if (schflag == 0 && !loginUserNum.equals("-2") && !loginUserNum.equals("-1")) {
                msg = "您暂无当前文件第" + (i + 2) + "行" + schoolName + "的权限";
                throwException(msg);
            }
            if (schlist.size() > 0) {
                if (!schlist.get(0).get("id").toString().equals(schoolNum)) {
                    msg = "当前文件第" + (i + 2) + "行第3列的学校与教师信息不匹配";
                    throwException(msg);
                }
            } else {
                msg = "当前文件第" + (i + 2) + "行第3列的学校不存在";
                throwException(msg);
            }
            if (isHidden.equals("F")) {
                rsql = "SELECT r.roleNum,ep.examPaperNum,ep.pexamPaperNum,ep.isHidden from role r LEFT JOIN exampaper ep on r.examPaperNum=ep.examPaperNum WHERE r.type='4' and ep.examPaperNum={exampaperNum}";
            } else {
                rsql = "SELECT r.roleNum,ep.examPaperNum,ep.pexamPaperNum,ep.isHidden from role r LEFT JOIN exampaper ep on r.examPaperNum=ep.examPaperNum WHERE r.type='4' and ep.examPaperNum={pexampaperNum}";
            }
            List<Map<String, Object>> rolelist = this.dao._queryMapList(rsql, null, rowmap);
            if (rolelist.size() == 0) {
                String id = GUID.getGUIDStr();
                rowmap.put("id", id);
                if (isHidden.equals("F")) {
                    insertrole = "INSERT INTO role(roleNum, roleName, schoolNum, examNum, examPaperNum, type, insertUser, insertDate, isDelete) VALUES ({id}, '阅卷员', {schoolNum}, {examNum}, {exampaperNum}, '4', {loginUserNum}, SUBSTR(NOW(), 1, 10), 'F')";
                } else {
                    insertrole = "INSERT INTO role(roleNum, roleName, schoolNum, examNum, examPaperNum, type, insertUser, insertDate, isDelete) VALUES ({id}, '阅卷员', {schoolNum}, {examNum}, {pexampaperNum}, '4', {loginUserNum}, SUBSTR(NOW(), 1, 10), 'F')";
                }
                this.dao._execute(insertrole, rowmap);
                List<Map<String, Object>> rolelist2 = this.dao._queryMapList(rsql, null, rowmap);
                String roleNum = rolelist2.get(0).get("roleNum").toString();
                rowmap.put("roleNum", roleNum);
            } else {
                String roleNum2 = rolelist.get(0).get("roleNum").toString();
                rowmap.put("roleNum", roleNum2);
            }
            int count = this.dao._queryInt("SELECT count(1) from userrole WHERE userNum={userNum} AND roleNum={roleNum}", rowmap).intValue();
            if (count <= 0) {
                this.dao._execute("INSERT INTO userrole( userNum, roleNum, insertUser, insertDate, isDelete) VALUES ( {userNum}, {roleNum}, {loginUserNum}, NOW(), 'F')", rowmap);
                int rolecount = this.dao._queryInt("SELECT count(1) from userrole WHERE userNum={userNum} and roleNum='4'", rowmap).intValue();
                if (rolecount == 0) {
                    this.dao._execute("INSERT INTO userrole( `userNum`, `roleNum`, `insertUser`, `insertDate`, `isDelete`) VALUES ({userNum}, 4,{loginUserNum} , now(), 'F')", rowmap);
                }
            }
            if (!isHidden.equals("F")) {
                int count1 = this.dao._queryInt("SELECT count(1) from userrole_sub WHERE userNum={userNum} AND examPaperNum={exampaperNum}", rowmap).intValue();
                if (count1 <= 0) {
                    this.dao._execute("INSERT INTO  userrole_sub (exampaperNum, userNum) VALUES ({exampaperNum}, {userNum})", rowmap);
                }
            }
        }
        return msg;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public void updateGroupWorknum(String examNum, String gradeNum, String parentSubjectNum, String pexamPaperNum) {
        String isFenzu;
        String selSql;
        if (StrUtil.isEmpty(pexamPaperNum)) {
            Exampaper ep = getexamPaperNum(examNum, gradeNum, parentSubjectNum, null);
            pexamPaperNum = Convert.toStr(ep.getPexamPaperNum());
            isFenzu = isFenzuyuejuan(examNum, gradeNum, parentSubjectNum);
        } else {
            isFenzu = systemService.fenzu(pexamPaperNum);
        }
        if ("0".equals(isFenzu)) {
            selSql = "select groupNum,-1 schoolGroupNum,schoolNum,num from schoolquota where exampaperNum={pexamPaperNum} ";
        } else {
            selSql = "select sq.groupNum,IFNULL(sg.schoolGroupNum,-2) schoolGroupNum,sq.schoolNum,sq.num from schoolquota sq left join schoolgroup sg on sg.schoolNum=sq.schoolNum where sq.exampaperNum={pexamPaperNum} ";
        }
        Map args = new HashMap();
        args.put("pexamPaperNum", pexamPaperNum);
        List<Map<String, Object>> sqList = this.dao._queryMapList(selSql, TypeEnum.StringObject, args);
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<Object, List<Map<String, Object>>> tiMap = (Map) sqList.stream().collect(Collectors.groupingBy(sq -> {
            return sq.get("groupNum");
        }));
        tiMap.forEach((ti, oneTiList) -> {
            Map<Object, List<Map<String, Object>>> groupMap = (Map) oneTiList.stream().collect(Collectors.groupingBy(sg -> {
                return sg.get("schoolGroupNum");
            }));
            groupMap.forEach((group, oneGroupList) -> {
                int zurenwuliang = ((Integer) oneGroupList.stream().collect(Collectors.summingInt(s -> {
                    return Convert.toInt(s.get("num"), 0).intValue();
                }))).intValue();
                oneGroupList.forEach(sch -> {
                    HashMap hashMap = new HashMap();
                    hashMap.put("schoolGroupNum", group);
                    hashMap.put("zurenwuliang", Integer.valueOf(zurenwuliang));
                    hashMap.put("groupNum", ti);
                    hashMap.put(Const.EXPORTREPORT_schoolNum, sch.get(Const.EXPORTREPORT_schoolNum));
                    paramList.add(hashMap);
                });
            });
        });
        if (CollUtil.isNotEmpty(paramList)) {
            this.dao._batchExecute("update schoolquota set schoolGroupNum={schoolGroupNum},zurenwuliang={zurenwuliang} where groupNum={groupNum} and schoolNum={schoolNum} ", paramList);
        }
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getSubjectLeaderList(int examPaperNum, int pexamPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("pexamPaperNum", Integer.valueOf(pexamPaperNum));
        return this.dao._queryMapList("SELECT sch.schoolName,u.username,u.realname FROM questiongroup_user qgu LEFT JOIN `user` u on u.id=qgu.userNum LEFT JOIN school sch on sch.id=u.schoolNum WHERE qgu.exampaperNum={examPaperNum} and qgu.userType='2' and sch.isDelete='F' and u.id is not null group by u.id ORDER BY CONVERT(sch.schoolName USING GBK)", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getQuestionGroupDataList(int examPaperNum, int pexamPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("pexamPaperNum", Integer.valueOf(pexamPaperNum));
        return this.dao._queryMapList("SELECT aaa.exampaperNum,GROUP_CONCAT( DISTINCT aaa.groupNum ORDER BY IFNULL( _d.orderNum, sd.orderNum ), subd.orderNum SEPARATOR '_' ) groupNum,aaa.groupName,aaa.groupType,aaa.choosename,IFNULL( sd_d.questionNum, _dd.questionNum ) mainName,subd.orderNum,aaa.category FROM (SELECT q.exampaperNum, q.groupNum, q.groupName, q.groupType, dd.choosename,dd.category FROM (SELECT q1.exampaperNum, q1.groupNum, q1.groupName, q1.groupType FROM questiongroup q1 LEFT JOIN questiongroup_question qgq1 ON q1.exampaperNum = qgq1.exampaperNum AND q1.groupNum = qgq1.groupNum WHERE q1.exampaperNum ={pexamPaperNum} AND q1.groupType != '0' GROUP BY q1.groupNum ) q INNER JOIN (SELECT d.id, d.choosename,d.category FROM ( SELECT d.id, d.orderNum, d.choosename,d.category FROM define d WHERE d.examPaperNum={examPaperNum} or d.category ={examPaperNum} UNION SELECT sb.id, sb.orderNum, d.choosename,sb.category FROM define d LEFT JOIN subdefine sb ON sb.pid = d.id WHERE sb.examPaperNum={examPaperNum} or sb.category = {examPaperNum}) d ORDER BY d.orderNum ) dd ON q.groupNum = dd.id ) aaa LEFT JOIN subdefine subd ON subd.id = aaa.groupNum LEFT JOIN define sd ON sd.id = subd.pid LEFT JOIN define sd_d ON sd_d.id = sd.choosename LEFT JOIN define _d ON _d.id = aaa.groupNum LEFT JOIN define _dd ON _dd.id = _d.choosename GROUP BY IF(aaa.choosename = 's',aaa.groupNum,CONCAT(aaa.choosename,IFNULL( subd.orderNum, '' ))) ORDER BY IFNULL( _d.orderNum, sd.orderNum ),subd.orderNum", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getQuestionLeaderList(String groupNum, String category) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("category", category);
        args.put("type", "-2");
        return this.dao._queryMapList("SELECT sg.schoolGroupNum,sch.schoolName,u.username,u.realname,q.schoolQuotaNum FROM questiongroup_user qgu LEFT JOIN `user` u on u.id=qgu.userNum LEFT JOIN  ( select exampaperNum,groupNum,schoolNum,sum(q.num)schoolQuotaNum from quota q  left join `user` u on u.id=q.insertUser where groupNum={groupNum} group by schoolnum  ) q on q.groupNum=qgu.groupNum and q.schoolNum=u.schoolNum LEFT JOIN exampaper ep on ep.examPaperNum={category} LEFT JOIN userposition up on up.userNum=qgu.userNum and up.type={type} and up.gradeNum=ep.gradeNum and up.subjectNum=ep.subjectNum LEFT JOIN school sch on sch.id=u.schoolNum LEFT JOIN schoolgroup sg on sg.schoolNum=sch.id LEFT JOIN schoollevel sl on sl.schoolNum=sch.id WHERE qgu.groupNum={groupNum} and qgu.userType='1' and sch.isDelete='F' and u.id is not null group by u.id ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public List<Map<String, Object>> getQuestionLeaderListOfSchool(String groupNum, String category, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put("groupNum", groupNum);
        args.put("category", category);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("type", "-2");
        return this.dao._queryMapList("SELECT sg.schoolGroupNum,sch.schoolName,q.schoolQuotaNum,up.userNum,up.realName,up.userName FROM questiongroup_user qgu LEFT JOIN `user` u on u.id=qgu.userNum LEFT JOIN  ( select exampaperNum,groupNum,schoolNum,sum(q.num)schoolQuotaNum from quota q  left join `user` u on u.id=q.insertUser where groupNum={groupNum} group by schoolnum  ) q on q.groupNum=qgu.groupNum and q.schoolNum=u.schoolNum LEFT JOIN school sch on sch.id=u.schoolNum LEFT JOIN schoolgroup sg on sg.schoolNum=sch.id  left join  (  select upId,schoolNum,userNum,realName,username from (  select up.id upId,up.schoolNum,up.userNum,u.id,u.username,u.realName,description from userposition up  left join user u on up.schoolNum=u.schoolNum and up.userNum=u.id where type={type} and gradeNum={gradeNum}  and subjectNum={subjectNum}  order by up.schoolNum,up.userNum  )a group by schoolNum  ) up on sch.id=up.schoolNum WHERE qgu.groupNum={groupNum} and qgu.userType='1' and sch.isDelete='F' and u.id is not null group by u.schoolNum ", TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public RspMsg getBestgroupInfoMap(String examNum, String gradeNum, String subjectNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        List<Object> schoolNumList = this.dao._queryColList("select distinct schoolNum from examinationnum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum}", args);
        if (CollUtil.isEmpty(schoolNumList)) {
            return new RspMsg(404, "当前科目还未分配考号！", null);
        }
        List<Map<String, Object>> schoolgroupList = this.dao._queryMapList("select sg.schoolGroupNum,sg.schoolGroupName,sg.schoolNum from schoolgroup sg inner join school sch on sch.id=sg.schoolNum and sch.isDelete='F' order by convert(sg.schoolGroupName using gbk)", TypeEnum.StringObject, args);
        int len = schoolgroupList.size();
        for (int i = 0; i < len; i++) {
            Map<String, Object> sgMap = schoolgroupList.get(i);
            if (schoolNumList.contains(sgMap.get(Const.EXPORTREPORT_schoolNum))) {
                return new RspMsg(200, "查询考号最高组信息！", sgMap);
            }
        }
        return new RspMsg(404, "未查询到考号最高组信息！", null);
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String existBsetgroupLeader(String groupNum, String schoolGroupNum) {
        Map args = new HashMap();
        args.put("schoolGroupNum", schoolGroupNum);
        args.put("groupNum", groupNum);
        if (null == this.dao._queryObject("SELECT qgu.id FROM questiongroup_user qgu INNER JOIN `user` u on u.id=qgu.userNum INNER JOIN school sch on sch.id=u.schoolNum and sch.isDelete='F' INNER JOIN schoolgroup sg on sg.schoolNum=sch.id and sg.schoolGroupNum={schoolGroupNum} WHERE qgu.groupNum={groupNum} and qgu.userType='1' limit 1", args)) {
            return "0";
        }
        return "1";
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public Integer getTestingcentre(String examPaperNum) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        Integer row = this.dao._queryInt("SELECT count(1) from testingcentreDis WHERE examPaperNum={examPaperNum}", args);
        return row;
    }

    @Override // com.dmj.service.questionGroup.QuestionGroupService
    public String importYuejuanCountList(String examNum, String gradeNum, String loginuser, String leiceng, List<List<Object>> list) {
        String log = "导入成功";
        for (int i = 0; i < list.size(); i++) {
            String schoolName = Convert.toStr(list.get(i).get(0));
            Map args = new HashMap();
            args.put("schoolName", schoolName);
            args.put("leiceng", leiceng);
            Map<String, Object> schmap = this.dao._querySimpleMap("SELECT rootId,pItemId,sItemId,statisticItem from statisticitem_school WHERE sItemName ={schoolName} AND topItemId={leiceng} ", args);
            if (null == schmap || schmap.size() == 0) {
                log = "第" + (i + 2) + "行的类层不存在，请检查后重新导入";
                throwException(log);
            }
            String subjectName = Convert.toStr(list.get(i).get(1));
            args.put("subjectName", subjectName);
            String subjectNum = this.dao._queryStr("SELECT subjectNum from `subject` WHERE subjectName={subjectName}", args);
            if (null == subjectNum || subjectNum.equals("")) {
                log = "第" + (i + 2) + "行的科目不存在，请检查后重新导入";
                throwException(log);
            }
            String num = Convert.toStr(list.get(i).get(2));
            args.put(Const.EXPORTREPORT_examNum, examNum);
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            args.put("rootId", Convert.toStr(schmap.get("rootId")));
            args.put("pid", Convert.toStr(schmap.get("pItemId")));
            args.put("statisticitem", Convert.toStr(schmap.get("statisticItem")));
            args.put("sItemId", Convert.toStr(schmap.get("sItemId")));
            args.put("yuding", num);
            args.put(Const.LOGIN_USER, loginuser);
            this.dao._execute("delete from reserveyuejuannum where examNum={examNum} and gradeNum={gradeNum} and subjectNum={subjectNum} and itemId={sItemId} ", args);
            this.dao._execute("insert into reserveyuejuannum(rootId,pitemId,itemId,statisticItem,examNum,gradeNum,subjectNum,reserveNum,bili,insertUser,insertDate) value({rootId},{pid},{sItemId},{statisticitem},{examNum},{gradeNum},{subjectNum},{yuding},null,{loginuser},now() )", args);
        }
        return log;
    }
}
