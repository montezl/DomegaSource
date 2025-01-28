package com.dmj.serviceimpl.test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dmj.auth.bean.License;
import com.dmj.cs.util.CsUtils;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.examManagement.ExamDAOImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Define;
import com.dmj.domain.Illegal;
import com.dmj.domain.Score;
import com.dmj.domain.Subdefine;
import com.dmj.service.test.StestService;
import com.dmj.util.Const;
import com.dmj.util.Util;
import com.zht.db.StreamMap;
import com.zht.db.TypeEnum;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/* loaded from: StestServiceimpl.class */
public class StestServiceimpl implements StestService {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    ExamDAOImpl examDao = new ExamDAOImpl();

    @Override // com.dmj.service.test.StestService
    public List<Score> list(String qtype, int examPaperNum, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String ocount, String ovalue, String otype, String examroomornot, String qNum, String isExceptiontype, String lim, String isModify, String examplace) {
        String schoolStr;
        String orderStr;
        String sql;
        String examroom = "";
        String examplace1 = "";
        Map args = new HashMap();
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and cc.examinationRoomNum={examinationRoomNum} ";
            args.put("examinationRoomNum", examinationRoomNum);
        }
        if (null != examplace && !examplace.equals("-1")) {
            examplace1 = " and s.testingCentreId={examplace} ";
            args.put("examplace", examplace);
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = " AND cc.schoolNum={school} ";
            args.put(License.SCHOOL, school);
        }
        String ocountStr = "";
        if (!ocount.equals("-1")) {
            ocountStr = " AND Length(s.answer)={ocount} ";
            args.put("ocount", ocount);
        }
        String otypeStr = "";
        if (!otype.equals("-1")) {
            if ("2".equals(otype)) {
                otypeStr = " AND Length(s.answer)>1 AND d.multiple='0' ";
            } else {
                otypeStr = " AND d.multiple={otype}";
                args.put("otype", otype);
            }
        }
        String isModifyStr = "";
        if (isModify.equals("T")) {
            isModifyStr = " AND s.isModify='T' ";
        }
        String ovalueStr = "";
        if (!ovalue.equals("-1")) {
            if (ovalue.equals("0")) {
                ovalueStr = " AND s.answer=''";
            } else {
                ovalueStr = " AND s.answer={ovalue} ";
                args.put("ovalue", ovalue);
            }
        }
        String qNumStr = "";
        if (!qNum.equals("-1")) {
            qNumStr = " AND d.id={qNum} ";
            args.put("qNum", qNum);
        }
        String str = "";
        if (!otype.equals("-1") || !ovalue.equals("-1") || !ocount.equals("-1")) {
            str = ovalueStr + otypeStr + ocountStr;
        }
        String isExpStr = "";
        if (!isExceptiontype.equals("-1")) {
            if (isExceptiontype.equals("3")) {
                isExpStr = " AND ( s.isException={IsException_normal} OR  s.isException={IsException_check} ) ";
                args.put("IsException_normal", "1");
                args.put("IsException_check", "2");
            } else {
                isExpStr = " AND s.isException={isExceptiontype}  ";
                args.put("isExceptiontype", isExceptiontype);
            }
        }
        if (isExceptiontype.equals("0")) {
            orderStr = " s.description*1 desc";
        } else {
            orderStr = " s.description*1 asc ";
        }
        String limStr = "";
        if (lim.equals("0")) {
            limStr = "  LIMIT {pagestart},{pagesize} ";
        }
        if (!studentName.equals("")) {
            sql = "SELECT s.id ext3, s.studentId ext8,st.studentName studentName,d.qNum questionNum,d.id ext2,s.answer answer, CASE WHEN cc.exampaperType='B' THEN d.answer_b  ELSE  d.answer  END AS yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum, s.examinationRoomNum examinationRoomNum,d.fullScore ext1,s.regId regId,s.page page,s.schoolNum ext5 ,d.optionCount ext4,s.regScore ext6 ,cl.className,d.multiple ext7  FROM   objectivescore  s   LEFT JOIN  illegal  cc  ON   s.studentId=cc.studentId   AND s.examPaperNum=cc.examPaperNum  LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore, CASE WHEN d.isParent ='0' THEN d.answer ELSE s.answer END AS answer, CASE WHEN d.isParent ='0' THEN d.answer_b ELSE s.answer_b END AS answer_b, CASE WHEN d.isParent ='0' THEN d.optionCount ELSE s.optionCount END AS optionCount, CASE WHEN d.isParent ='0' THEN d.multiple ELSE s.multiple END AS multiple FROM define d LEFT JOIN subdefine s ON d.id=s.pid  WHERE d.examPaperNum={examPaperNum} )d  ON d.id = s.questionNum  LEFT JOIN student st ON st.id = s.studentId  LEFT JOIN class cl ON st.classNum=cl.id where s.examPaperNum={examPaperNum} " + examplace1 + " AND  cc.examPaperNum={examPaperNum} AND st.studentName={studentName} " + isModifyStr + qNumStr + schoolStr + str + isExpStr + " order by  s.answer ASC,d.optionCount*1," + orderStr + ",cc.exampaperType " + limStr;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("studentName", studentName);
        } else {
            sql = "SELECT s.id as ext3, s.studentId  as ext8,st.studentName  as studentName,d.qNum as  questionNum,d.id as  ext2,s.answer  as answer, CASE WHEN cc.exampaperType='B' THEN d.answer_b  ELSE  d.answer  END AS yanswer, s.questionScore as  questionScore,s.examPaperNum as examPaperNum,s.examinationRoomNum as  examinationRoomNum, d.fullScore as  ext1,s.regId as  regId,s.page  as page,s.schoolNum  as ext5,d.optionCount  as ext4,s.regScore ext6 ,cl.className,d.multiple ext7   FROM objectivescore s  LEFT JOIN  illegal cc\tON  s.studentId=cc.studentId  AND s.examPaperNum=cc.examPaperNum    LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore, CASE WHEN d.isParent ='0' THEN d.answer ELSE s.answer END AS answer, CASE WHEN d.isParent ='0' THEN d.answer_b ELSE s.answer_b END AS answer_b, CASE WHEN d.isParent ='0' THEN d.optionCount ELSE s.optionCount END AS optionCount, CASE WHEN d.isParent ='0' THEN d.multiple ELSE s.multiple END AS multiple FROM define d LEFT JOIN subdefine s ON d.id=s.pid  WHERE d.examPaperNum={examPaperNum} )d ON d.id = s.questionNum   LEFT JOIN student st ON st.id = s.studentId   LEFT JOIN class cl ON st.classNum=cl.id  WHERE s.examPaperNum={examPaperNum}  AND  cc.examPaperNum={examPaperNum} " + examplace1 + isModifyStr + schoolStr + examroom + qNumStr + str + isExpStr + " order by  s.answer ASC , d.optionCount*1," + orderStr + ",cc.exampaperType " + limStr;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
        }
        return this.dao._queryBeanList(sql, Score.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<Score> list_auth(String qtype, int examPaperNum, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String ocount, String ovalue, String otype, String examroomornot, String qNum, String isExceptiontype, String lim, String isModify, String examplace, String userId) {
        String schoolStr;
        String orderStr;
        String sql;
        String examroom = "";
        Map args = new HashMap();
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and cc.examinationRoomNum={examinationRoomNum} ";
            args.put("examinationRoomNum", examinationRoomNum);
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND cc.schoolNum={school} ";
            args.put(License.SCHOOL, school);
        }
        String ocountStr = "";
        if (!ocount.equals("-1")) {
            ocountStr = " AND Length(s.answer)={ocount} ";
            args.put("ocount", ocount);
        }
        String otypeStr = "";
        if (!otype.equals("-1")) {
            if ("2".equals(otype)) {
                otypeStr = " AND Length(s.answer)>1 AND d.multiple='0' ";
            } else {
                otypeStr = " AND d.multiple={otype} ";
                args.put("otype", otype);
            }
        }
        String isModifyStr = "";
        if (isModify.equals("T")) {
            isModifyStr = " AND s.isModify='T' ";
        }
        String ovalueStr = "";
        if (!ovalue.equals("-1")) {
            if (ovalue.equals("0")) {
                ovalueStr = " AND s.answer=''";
            } else {
                ovalueStr = " AND s.answer={ovalue} ";
                args.put("ovalue", ovalue);
            }
        }
        String qNumStr = "";
        if (!qNum.equals("-1")) {
            qNumStr = " AND d.id={qNum} ";
            args.put("qNum", qNum);
        }
        String str = "";
        if (!otype.equals("-1") || !ovalue.equals("-1") || !ocount.equals("-1")) {
            str = ovalueStr + otypeStr + ocountStr;
        }
        String isExpStr = "";
        if (!isExceptiontype.equals("-1")) {
            if (isExceptiontype.equals("3")) {
                isExpStr = " AND ( s.isException={IsException_normal}   OR  s.isException={IsException_check} ) ";
                args.put("IsException_normal", "1");
                args.put("IsException_check", "2");
            } else {
                isExpStr = " AND s.isException={isExceptiontype}  ";
                args.put("isExceptiontype", isExceptiontype);
            }
        }
        if (isExceptiontype.equals("0")) {
            orderStr = " s.description*1 desc";
        } else {
            orderStr = " s.description*1 asc ";
        }
        String limStr = "";
        if (lim.equals("0")) {
            limStr = "  LIMIT {pagestart},{pagesize}";
            args.put("pagestart", Integer.valueOf(pagestart));
            args.put("pagesize", Integer.valueOf(pagesize));
        }
        if (!studentName.equals("")) {
            sql = "SELECT s.id ext3, s.studentId ext8,st.studentName studentName,d.qNum questionNum,d.id ext2,s.answer answer, CASE WHEN cc.exampaperType='B' THEN d.answer_b  ELSE  d.answer  END AS yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum, s.examinationRoomNum examinationRoomNum,d.fullScore ext1,s.regId regId,s.page page,s.schoolNum ext5 ,d.optionCount ext4,s.regScore ext6 ,cl.className,d.multiple ext7  FROM   objectivescore  s   LEFT JOIN scanpermission scanp on scanp.testingCentreId = s.testingCentreId  LEFT JOIN  illegal  cc  ON   s.studentId=cc.studentId   AND s.examPaperNum=cc.examPaperNum  LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore, CASE WHEN d.isParent ='0' THEN d.answer ELSE s.answer END AS answer, CASE WHEN d.isParent ='0' THEN d.answer_b ELSE s.answer_b END AS answer_b, CASE WHEN d.isParent ='0' THEN d.optionCount ELSE s.optionCount END AS optionCount, CASE WHEN d.isParent ='0' THEN d.multiple ELSE s.multiple END AS multiple FROM define d LEFT JOIN subdefine s ON d.id=s.pid  WHERE d.examPaperNum={examPaperNum} )d  ON d.id = s.questionNum  LEFT JOIN student st ON st.id = s.studentId  LEFT JOIN class cl ON st.classNum=cl.id  where s.examPaperNum={examPaperNum}  and scanp.userNum = {userId} AND  cc.examPaperNum={examPaperNum} AND st.studentName={studentName} " + isModifyStr + qNumStr + schoolStr + str + isExpStr + " order by  s.answer ASC,d.optionCount*1," + orderStr + ",cc.exampaperType " + limStr;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("userId", userId);
            args.put("studentName", studentName);
        } else {
            sql = "SELECT s.id as ext3, s.studentId  as ext8,st.studentName  as studentName,d.qNum as  questionNum,d.id as  ext2,s.answer  as answer, CASE WHEN cc.exampaperType='B' THEN d.answer_b  ELSE  d.answer  END AS yanswer, s.questionScore as  questionScore,s.examPaperNum as examPaperNum,s.examinationRoomNum as  examinationRoomNum, d.fullScore as  ext1,s.regId as  regId,s.page  as page,s.schoolNum  as ext5,d.optionCount  as ext4,s.regScore ext6 ,cl.className,d.multiple ext7   FROM objectivescore s  LEFT JOIN scanpermission scanp on scanp.testingCentreId = s.testingCentreId  LEFT JOIN  illegal cc\tON  s.studentId=cc.studentId  AND s.examPaperNum=cc.examPaperNum    LEFT JOIN (SELECT  CASE WHEN d.isParent ='0' THEN d.questionNum ELSE s.questionNum END AS qNum, CASE WHEN d.isParent ='0' THEN d.id ELSE s.id END AS id, CASE WHEN d.isParent ='0' THEN d.fullScore ELSE s.fullScore END AS fullScore, CASE WHEN d.isParent ='0' THEN d.answer ELSE s.answer END AS answer, CASE WHEN d.isParent ='0' THEN d.answer_b ELSE s.answer_b END AS answer_b, CASE WHEN d.isParent ='0' THEN d.optionCount ELSE s.optionCount END AS optionCount, CASE WHEN d.isParent ='0' THEN d.multiple ELSE s.multiple END AS multiple FROM define d LEFT JOIN subdefine s ON d.id=s.pid  WHERE d.examPaperNum={examPaperNum} )d ON d.id = s.questionNum   LEFT JOIN student st ON st.id = s.studentId   LEFT JOIN class cl ON st.classNum=cl.id  WHERE s.examPaperNum={examPaperNum} AND  cc.examPaperNum={examPaperNum}  and scanp.userNum = {userId} " + isModifyStr + schoolStr + examroom + qNumStr + str + isExpStr + " order by  s.answer ASC , d.optionCount*1," + orderStr + ",cc.exampaperType " + limStr;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("userId", userId);
        }
        return this.dao._queryBeanList(sql, Score.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<Map<String, Object>> list2(String qtype, int examPaperNum, String examplace, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String ocount, String ovalue, String otype, String examroomornot, String qNum, String isExceptiontype, String lim, String isModify, String isyincangQuekao, String proofreadingStatus, String selectedOptionIndex) {
        String schoolStr;
        String orderByStr;
        String sql;
        String examroom = "";
        Map<String, Object> args = new HashMap<>();
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and cc.examinationRoomNum={examinationRoomNum} ";
            args.put("examinationRoomNum", examinationRoomNum);
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND cc.schoolNum={school} ";
            args.put(License.SCHOOL, school);
        }
        String ocountStr = "";
        if (!ocount.equals("-1")) {
            ocountStr = " AND Length(s.answer)={ocount} ";
            args.put("ocount", ocount);
        }
        args.put("otype", otype);
        String otypeStr = "";
        if (!otype.equals("-1")) {
            otypeStr = " AND (d.multiple={otype} or subd.multiple={otype}) ";
        }
        String isModifyStr = "";
        if (isModify.equals("T")) {
            isModifyStr = " AND s.isModify='T' ";
        }
        String yincangQuekaoStr1 = "";
        if (isyincangQuekao.equals("T")) {
            yincangQuekaoStr1 = " and cc.`type`<>0 ";
        }
        String ovalueStr = "";
        if (!ovalue.equals("-1")) {
            if (ovalue.equals("0")) {
                ovalueStr = " AND s.answer=''";
            } else {
                ovalueStr = " AND s.answer={ovalue} ";
                args.put("ovalue", ovalue);
            }
        }
        String qNumStr = "";
        if (!qNum.equals("-1")) {
            qNumStr = " AND (d.id={qNum} or subd.id={qNum}) ";
            args.put("qNum", qNum);
        }
        String str = "";
        if (!otype.equals("-1") || !ovalue.equals("-1") || !ocount.equals("-1")) {
            str = ovalueStr + otypeStr + "" + ocountStr;
        }
        String isExpStr = "";
        if (!isExceptiontype.equals("-1")) {
            if ("-2".equals(isExceptiontype)) {
                isExpStr = " AND Length(s.answer)>1 AND IFNULL(d.multiple,subd.multiple)={otype} ";
            } else if (isExceptiontype.equals("3")) {
                isExpStr = " AND ( s.isException={IsException_normal}  OR  s.isException={IsException_check}) ";
                args.put("IsException_normal", "1");
                args.put("IsException_check", "2");
            } else {
                isExpStr = " AND s.isException={isExceptiontype}";
                args.put("isExceptiontype", isExceptiontype);
            }
        }
        String limStr = "";
        if (lim.equals("0")) {
            limStr = "  LIMIT {pagestart},{pagesize}";
            args.put("pagestart", Integer.valueOf(pagestart));
            args.put("pagesize", Integer.valueOf(pagesize));
        }
        String epString = "";
        if (null != examplace && !examplace.equals("-1")) {
            epString = "  and s.testingCentreId={examplace} ";
            args.put("examplace", examplace);
        }
        String psStr = "";
        if ("0".equals(proofreadingStatus)) {
            psStr = " and s.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            psStr = " and s.proofreadingStatus1 = '1' ";
        }
        if ("0".equals(isExceptiontype)) {
            orderByStr = " order by  s.description*1 desc,IFNULL(d.optionCount,subd.optionCount)*1,cc.exampaperType " + limStr;
        } else if ("-2".equals(isExceptiontype)) {
            orderByStr = " order by  s.regMinToMax asc,s.regMax desc " + limStr;
        } else {
            orderByStr = " order by  s.answer ASC,IFNULL(d.optionCount,subd.optionCount)*1,s.description*1 asc,cc.exampaperType " + limStr;
        }
        if (!studentName.equals("")) {
            sql = "SELECT s.id ext3, s.studentId ext8,st.studentName studentName,IFNULL(d.questionNum,subd.questionNum) questionNum,IFNULL(d.id,subd.id) ext2,s.answer answer, CASE WHEN cc.exampaperType='B' THEN IFNULL(d.answer_b,subd.answer_b)  ELSE  IFNULL(d.answer,subd.answer)  END AS yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum,IFNULL(d.fullScore,subd.fullScore) ext1,s.regId regId,s.page page,s.schoolNum ext5,sch.schoolName ,IFNULL(d.optionCount,subd.optionCount) ext4,s.regScore ext6 ,cl.className,IFNULL(d.multiple,subd.multiple) ext7,s.regResult,s.description,IFNULL(ip.location,'') location,IFNULL(qi.img,'') img,IFNULL(qi.multiRects,qi.questionW) questionW,qi.questionH  FROM   objectivescore  s   LEFT JOIN  illegal  cc  ON   s.studentId=cc.studentId   AND s.examPaperNum=cc.examPaperNum  left join define d on d.id=s.questionNum  left join subdefine subd on subd.id=s.questionNum  LEFT JOIN student st ON st.id = s.studentId  LEFT JOIN class cl ON cl.id=s.classNum LEFT JOIN school sch ON sch.id=s.schoolNum left join questionimage qi on qi.scoreId=s.id left join imgpath ip on ip.id=qi.imgpath where s.examPaperNum={examPaperNum}  " + epString + psStr + "  AND  cc.examPaperNum={examPaperNum}  AND st.studentName={studentName}   " + isModifyStr + qNumStr + schoolStr + str + isExpStr + yincangQuekaoStr1 + orderByStr;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("studentName", studentName);
        } else {
            sql = "SELECT s.id as ext3, s.studentId  as ext8,st.studentName  as studentName,IFNULL(d.questionNum,subd.questionNum) as  questionNum,IFNULL(d.id,subd.id) as  ext2,s.answer  as answer, CASE WHEN cc.exampaperType='B' THEN IFNULL(d.answer_b,subd.answer_b)  ELSE  IFNULL(d.answer,subd.answer)  END AS yanswer, s.questionScore as  questionScore,s.examPaperNum as examPaperNum,s.examinationRoomNum as  examinationRoomNum, IFNULL(d.fullScore,subd.fullScore) as  ext1,s.regId as  regId,s.page  as page,s.schoolNum  as ext5,sch.schoolName,IFNULL(d.optionCount,subd.optionCount)  as ext4,s.regScore ext6 ,cl.className,IFNULL(d.multiple,subd.multiple) ext7,s.regResult,s.description  ,IFNULL(ip.location,'') location,IFNULL(qi.img,'') img,IFNULL(qi.multiRects,qi.questionW) questionW,qi.questionH  FROM objectivescore s  LEFT JOIN  illegal cc\tON  s.studentId=cc.studentId  AND s.examPaperNum=cc.examPaperNum    left join define d on d.id=s.questionNum  left join subdefine subd on subd.id=s.questionNum  LEFT JOIN student st ON st.id = s.studentId  LEFT JOIN class cl ON cl.id=s.classNum LEFT JOIN school sch ON sch.id=s.schoolNum left join questionimage qi on qi.scoreId=s.id left join imgpath ip on ip.id=qi.imgpath  WHERE s.examPaperNum={examPaperNum} " + epString + psStr + "  AND  cc.examPaperNum={examPaperNum}  " + isModifyStr + schoolStr + examroom + qNumStr + str + isExpStr + yincangQuekaoStr1 + orderByStr;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
        }
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<Map<String, Object>> list2_auth(String qtype, int examPaperNum, String examplace, String examinationRoomNum, int pagestart, int pagesize, String studentName, String school, String ocount, String ovalue, String otype, String examroomornot, String qNum, String isExceptiontype, String lim, String isModify, String isyincangQuekao, String userId, String proofreadingStatus, String selectedOptionIndex) {
        String schoolStr;
        String orderByStr;
        String sql;
        String examroom = "";
        Map args = new HashMap();
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and cc.examinationRoomNum={examinationRoomNum} ";
            args.put("examinationRoomNum", examinationRoomNum);
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND cc.schoolNum={school} ";
            args.put(License.SCHOOL, school);
        }
        String ocountStr = "";
        if (!ocount.equals("-1")) {
            ocountStr = " AND Length(s.answer)={ocount} ";
            args.put("ocount", ocount);
        }
        args.put("otype", otype);
        String otypeStr = "";
        if (!otype.equals("-1")) {
            otypeStr = " AND (d.multiple={otype} or subd.multiple={otype}) ";
        }
        String isModifyStr = "";
        if (isModify.equals("T")) {
            isModifyStr = " AND s.isModify='T' ";
        }
        String yincangQuekaoStr1 = "";
        if (isyincangQuekao.equals("T")) {
            yincangQuekaoStr1 = " and cc.`type`<>0 ";
        }
        String ovalueStr = "";
        if (!ovalue.equals("-1")) {
            if (ovalue.equals("0")) {
                ovalueStr = " AND s.answer=''";
            } else {
                ovalueStr = " AND s.answer={ovalue} ";
                args.put("ovalue", ovalue);
            }
        }
        String qNumStr = "";
        if (!qNum.equals("-1")) {
            qNumStr = " AND (d.id={qNum} or subd.id={qNum}) ";
            args.put("qNum", qNum);
        }
        String str = "";
        if (!otype.equals("-1") || !ovalue.equals("-1") || !ocount.equals("-1")) {
            str = ovalueStr + otypeStr + "" + ocountStr;
        }
        String isExpStr = "";
        if (!isExceptiontype.equals("-1")) {
            if ("-2".equals(isExceptiontype)) {
                isExpStr = " AND Length(s.answer)>1 AND IFNULL(d.multiple,subd.multiple)={otype} ";
            } else if (isExceptiontype.equals("3")) {
                isExpStr = " AND ( s.isException={IsException_normal} OR  s.isException={IsException_check} ) ";
                args.put("IsException_normal", "1");
                args.put("IsException_check", "2");
            } else {
                isExpStr = " AND s.isException={isExceptiontype} ";
                args.put("isExceptiontype", isExceptiontype);
            }
        }
        if ("0".equals(isExceptiontype)) {
            orderByStr = " order by  s.description*1 desc,IFNULL(d.optionCount,subd.optionCount)*1,cc.exampaperType ";
        } else if ("-2".equals(isExceptiontype)) {
            orderByStr = " order by  s.regMinToMax asc,s.regMax desc ";
        } else {
            orderByStr = " order by  s.answer ASC,IFNULL(d.optionCount,subd.optionCount)*1,s.description*1 asc,cc.exampaperType ";
        }
        String limStr = "";
        if (lim.equals("0")) {
            limStr = "  LIMIT {pagestart},{pagesize}";
            args.put("pagestart", Integer.valueOf(pagestart));
            args.put("pagesize", Integer.valueOf(pagesize));
        }
        String psStr = "";
        if ("0".equals(proofreadingStatus)) {
            psStr = " and s.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            psStr = " and s.proofreadingStatus1 = '1' ";
        }
        if (!studentName.equals("")) {
            sql = "SELECT s.id ext3, s.studentId ext8,st.studentName studentName,IFNULL(d.questionNum,subd.questionNum) questionNum,IFNULL(d.id,subd.id) ext2,s.answer answer, CASE WHEN cc.exampaperType='B' THEN IFNULL(d.answer_b,subd.answer_b)  ELSE  IFNULL(d.answer,subd.answer)  END AS yanswer,s.questionScore questionScore,s.examPaperNum examPaperNum,s.examinationRoomNum examinationRoomNum,IFNULL(d.fullScore,subd.fullScore) ext1,s.regId regId,s.page page,s.schoolNum ext5,sch.schoolName ,IFNULL(d.optionCount,subd.optionCount) ext4,s.regScore ext6 ,cl.className,IFNULL(d.multiple,subd.multiple) ext7,s.regResult,s.description ,IFNULL(ip.location,'') location,IFNULL(qi.img,'') img,IFNULL(qi.multiRects,qi.questionW) questionW,qi.questionH  FROM   objectivescore  s   LEFT JOIN schoolscanpermission scanp on scanp.testingCentreId = s.schoolNum  LEFT JOIN  illegal  cc  ON   s.studentId=cc.studentId   AND s.examPaperNum=cc.examPaperNum  left join define d on d.id=s.questionNum  left join subdefine subd on subd.id=s.questionNum  LEFT JOIN student st ON st.id = s.studentId  LEFT JOIN class cl ON cl.id=s.classNum LEFT JOIN school sch ON sch.id=s.schoolNum left join questionimage qi on qi.scoreId=s.id left join imgpath ip on ip.id=qi.imgpath  where s.examPaperNum={examPaperNum}  " + psStr + " and scanp.userNum = {userId}   AND  cc.examPaperNum={examPaperNum} AND st.studentName={studentName}  " + isModifyStr + qNumStr + schoolStr + str + isExpStr + yincangQuekaoStr1 + orderByStr + limStr;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("userId", userId);
            args.put("studentName", studentName);
        } else {
            sql = "SELECT s.id as ext3, s.studentId  as ext8,st.studentName  as studentName,IFNULL(d.questionNum,subd.questionNum) as  questionNum,IFNULL(d.id,subd.id) as  ext2,s.answer  as answer, CASE WHEN cc.exampaperType='B' THEN IFNULL(d.answer_b,subd.answer_b)  ELSE  IFNULL(d.answer,subd.answer)  END AS yanswer, s.questionScore as  questionScore,s.examPaperNum as examPaperNum,s.examinationRoomNum as  examinationRoomNum, IFNULL(d.fullScore,subd.fullScore) as  ext1,s.regId as  regId,s.page  as page,s.schoolNum  as ext5,sch.schoolName,IFNULL(d.optionCount,subd.optionCount)  as ext4,s.regScore ext6 ,cl.className,IFNULL(d.multiple,subd.multiple) ext7,s.regResult,s.description  ,IFNULL(ip.location,'') location,IFNULL(qi.img,'') img,IFNULL(qi.multiRects,qi.questionW) questionW,qi.questionH  FROM objectivescore s  LEFT JOIN schoolscanpermission scanp on scanp.schoolNum = s.schoolNum  LEFT JOIN  illegal cc\tON  s.studentId=cc.studentId  AND s.examPaperNum=cc.examPaperNum    left join define d on d.id=s.questionNum  left join subdefine subd on subd.id=s.questionNum  LEFT JOIN student st ON st.id = s.studentId  LEFT JOIN class cl ON cl.id=s.classNum LEFT JOIN school sch ON sch.id=s.schoolNum left join questionimage qi on qi.scoreId=s.id left join imgpath ip on ip.id=qi.imgpath  WHERE s.examPaperNum={examPaperNum} " + psStr + " and scanp.userNum = {userId}  AND  cc.examPaperNum={examPaperNum} " + isModifyStr + schoolStr + examroom + qNumStr + str + isExpStr + yincangQuekaoStr1 + orderByStr + limStr;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("userId", userId);
        }
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.test.StestService
    public void che(String id) {
        Map args = StreamMap.create().put("isModify", (Object) "T").put("id", (Object) id);
        this.dao._execute("UPDATE objectivescore set isModify ={isModify}  where id ={id} ", args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer co(int examPaperNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) Integer.valueOf(examPaperNum)).put("isModify", (Object) "T");
        return this.dao._queryInt("select count(1) from objectivescore where  examPaperNum={examPaperNum}  and isModify={isModify} ", args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer zco(String examinationRoomNum, int examPaperNum, String studentName, String school, String grade, String ocount, String ovalue, String otype, String examroomornot, String qNum, String isExceptiontype, String isModify, String isyincangQuekao, String examplace, String proofreadingStatus, String selectedOptionIndex) {
        String schoolStr;
        String sql;
        String examroom = "";
        Map args = new HashMap();
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and examinationRoomNum={examinationRoomNum} ";
            args.put("examinationRoomNum", examinationRoomNum);
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND schoolNum={school} ";
            args.put(License.SCHOOL, school);
        }
        String ocountStr = "";
        if (!ocount.equals("-1")) {
            ocountStr = " AND Length(s.answer)={ocount} ";
            args.put("ocount", ocount);
        }
        args.put("otype", otype);
        String otypeStr = "";
        String moreLenOPtion = "";
        if (!otype.equals("-1")) {
            otypeStr = " AND (d.multiple={otype} or subd.multiple={otype}) ";
        }
        String ovalueStr = "";
        if (!ovalue.equals("-1")) {
            if (ovalue.equals("0")) {
                ovalueStr = " AND s.answer=''";
            } else {
                ovalueStr = " AND s.answer={ovalue} ";
                args.put("ovalue", ovalue);
            }
        }
        String qNumStr = "";
        if (!qNum.equals("-1")) {
            qNumStr = " AND s.questionNum={qNum} AND (d.id={qNum} or subd.id={qNum})  ";
            args.put("qNum", qNum);
        }
        String isExpStr = "";
        if (!isExceptiontype.equals("-1")) {
            if ("-2".equals(isExceptiontype)) {
                moreLenOPtion = " AND Length(s.answer)>1 AND IFNULL(d.multiple,subd.multiple)={otype} ";
            } else if (isExceptiontype.equals("3")) {
                isExpStr = " AND ( s.isException={IsException_normal} OR  s.isException={IsException_check} ) ";
                args.put("IsException_normal", "1");
                args.put("IsException_check", "2");
            } else {
                isExpStr = " AND s.isException={isExceptiontype}  ";
                args.put("isExceptiontype", isExceptiontype);
            }
        }
        String isModifyStr = "";
        if (isModify.equals("T")) {
            isModifyStr = " AND isModify='T' ";
        }
        String yincangQuekaoStr1 = "";
        String yincangQuekaoStr2 = "";
        if (isyincangQuekao.equals("T")) {
            yincangQuekaoStr1 = " left join illegal ill on s.regId=ill.regId ";
            yincangQuekaoStr2 = " and ill.`type`<>0 ";
        }
        String examPlaceString = "";
        if (null != examplace && !examplace.equals("-1")) {
            examPlaceString = " and testingCentreId={examplace} ";
            args.put("examplace", examplace);
        }
        String psStr = "";
        if ("0".equals(proofreadingStatus)) {
            psStr = " and proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            psStr = " and proofreadingStatus1 = '1' ";
        }
        if (!studentName.equals("")) {
            sql = "SELECT count(1) FROM (SELECT regId,examPaperNum,studentId,questionNum,answer,isException from objectivescore WHERE  examPaperNum={examPaperNum}  " + examPlaceString + " " + examroom + schoolStr + isModifyStr + psStr + ") s  left join define d on d.id=s.questionNum  left join subdefine subd on subd.id=s.questionNum " + yincangQuekaoStr1 + "LEFT JOIN student st ON st.id = s.studentId  where st.studentName={studentName} " + qNumStr + ovalueStr + otypeStr + moreLenOPtion + ocountStr + isExpStr + yincangQuekaoStr2;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("studentName", studentName);
        } else {
            sql = "select count(1) from   (SELECT regId,examPaperNum,studentId,questionNum,answer,isException from objectivescore WHERE  examPaperNum={examPaperNum} " + examPlaceString + examroom + schoolStr + isModifyStr + psStr + ") s left join define d on d.id=s.questionNum  left join subdefine subd on subd.id=s.questionNum " + yincangQuekaoStr1 + " where  1=1 " + qNumStr + ovalueStr + otypeStr + moreLenOPtion + ocountStr + isExpStr + yincangQuekaoStr2;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
        }
        return Convert.toInt(this.dao._queryInt(sql, args));
    }

    @Override // com.dmj.service.test.StestService
    public Integer zco_auth(String examinationRoomNum, int examPaperNum, String studentName, String school, String grade, String ocount, String ovalue, String otype, String examroomornot, String qNum, String isExceptiontype, String isModify, String isyincangQuekao, String examplace, String userId, String proofreadingStatus, String selectedOptionIndex) {
        String schoolStr;
        String sql;
        String examroom = "";
        Map args = new HashMap();
        if (examinationRoomNum.equals("-1")) {
            examroom = "";
        } else if (!examinationRoomNum.equals("-1") && examroomornot.equals("0")) {
            examroom = " and examinationRoomNum={examinationRoomNum} ";
            args.put("examinationRoomNum", examinationRoomNum);
        }
        if (school.equals("-1")) {
            schoolStr = "";
        } else {
            schoolStr = "AND schoolNum={school} ";
            args.put(License.SCHOOL, school);
        }
        String ocountStr = "";
        if (!ocount.equals("-1")) {
            ocountStr = " AND Length(s.answer)={ocount} ";
            args.put("ocount", ocount);
        }
        args.put("otype", otype);
        String otypeStr = "";
        String moreLenOPtion = "";
        if (!otype.equals("-1")) {
            otypeStr = " AND (d.multiple={otype} or subd.multiple={otype}) ";
        }
        String ovalueStr = "";
        if (!ovalue.equals("-1")) {
            if (ovalue.equals("0")) {
                ovalueStr = " AND s.answer=''";
            } else {
                ovalueStr = " AND s.answer={ovalue} ";
                args.put("ovalue", ovalue);
            }
        }
        String qNumStr = "";
        if (!qNum.equals("-1")) {
            qNumStr = " AND s.questionNum={qNum}  AND (d.id={qNum} or subd.id={qNum}) ";
            args.put("qNum", qNum);
        }
        String isExpStr = "";
        if (!isExceptiontype.equals("-1")) {
            if ("-2".equals(isExceptiontype)) {
                moreLenOPtion = " AND Length(s.answer)>1 AND IFNULL(d.multiple,subd.multiple)={otype} ";
            } else if (isExceptiontype.equals("3")) {
                isExpStr = " AND ( s.isException={IsException_normal}  OR  s.isException={IsException_check} ) ";
                args.put("IsException_normal", "1");
                args.put("IsException_check", "2");
            } else {
                isExpStr = " AND s.isException={isExceptiontype} ";
                args.put("isExceptiontype", isExceptiontype);
            }
        }
        String isModifyStr = "";
        if (isModify.equals("T")) {
            isModifyStr = " AND isModify='T' ";
        }
        String yincangQuekaoStr1 = "";
        String yincangQuekaoStr2 = "";
        if (isyincangQuekao.equals("T")) {
            yincangQuekaoStr1 = " left join illegal ill on s.regId=ill.regId ";
            yincangQuekaoStr2 = " and ill.`type`<>0 ";
        }
        String psStr = "";
        if ("0".equals(proofreadingStatus)) {
            psStr = " and proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            psStr = " and proofreadingStatus1 = '1' ";
        }
        if (!studentName.equals("")) {
            sql = "SELECT count(1) FROM  (SELECT regId,examPaperNum,studentId,questionNum,answer,isException,testingCentreId from objectivescore WHERE  examPaperNum={examPaperNum} " + examroom + schoolStr + isModifyStr + psStr + ") s  LEFT JOIN scanpermission scanp on scanp.testingCentreId = s.testingCentreId  left join define d on d.id=s.questionNum  left join subdefine subd on subd.id=s.questionNum " + yincangQuekaoStr1 + " LEFT JOIN student st ON st.id = s.studentId  where scanp.userNum = {userId} and st.studentName={studentName} " + qNumStr + ovalueStr + otypeStr + moreLenOPtion + ocountStr + isExpStr + yincangQuekaoStr2;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("userId", userId);
            args.put("studentName", studentName);
        } else {
            sql = "select count(1) from  (SELECT regId,examPaperNum,studentId,questionNum,answer,isException,testingCentreId from objectivescore WHERE  examPaperNum={examPaperNum} " + examroom + schoolStr + isModifyStr + psStr + ") s  LEFT JOIN scanpermission scanp on scanp.testingCentreId = s.testingCentreId  left join define d on d.id=s.questionNum  left join subdefine subd on subd.id=s.questionNum " + yincangQuekaoStr1 + " where  scanp.userNum = {userId} " + qNumStr + ovalueStr + otypeStr + moreLenOPtion + ocountStr + isExpStr + yincangQuekaoStr2;
            args.put("examPaperNum", Integer.valueOf(examPaperNum));
            args.put("userId", userId);
        }
        return Convert.toInt(this.dao._queryInt(sql, args), 0);
    }

    @Override // com.dmj.service.test.StestService
    public int getexampapernum(String examNum, String subjectNum, String gradeNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put(Const.EXPORTREPORT_subjectNum, (Object) subjectNum).put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        int aa = 0;
        if (null != this.dao._queryStr("select examPaperNum  from exampaper where examNum={examNum} and subjectNum={subjectNum} and gradeNum={gradeNum} ", args) && !"".equals(this.dao._queryStr("select examPaperNum  from exampaper where examNum={examNum} and subjectNum={subjectNum} and gradeNum={gradeNum} ", args)) && !"null".equals(this.dao._queryStr("select examPaperNum  from exampaper where examNum={examNum} and subjectNum={subjectNum} and gradeNum={gradeNum} ", args))) {
            aa = this.dao._queryInt("select examPaperNum  from exampaper where examNum={examNum} and subjectNum={subjectNum} and gradeNum={gradeNum} ", args).intValue();
        }
        return aa;
    }

    @Override // com.dmj.service.test.StestService
    public void createImg(String path, List<byte[]> imgs) throws Exception {
        if (null == imgs) {
            return;
        }
        for (int i = 0; i < imgs.size(); i++) {
            byte[] bs = imgs.get(i);
            File file = new File(path + File.separator + i + ".jpg");
            OutputStream outputImage = new FileOutputStream(file);
            outputImage.write(bs);
        }
    }

    @Override // com.dmj.service.test.StestService
    public List<byte[]> allimg(String examroom) {
        Map args = new HashMap();
        args.put("examroom", examroom);
        args.put("QUESTION_TYPE_OBJECTIVE", "0");
        return this.dao._queryColList("select img from questionimage where studentId=any(select DISTINCT studentid from score where examinationRoomNum={examroom} ) and questionType={QUESTION_TYPE_OBJECTIVE} ", byte[].class, args);
    }

    public static void main(String[] args) {
        String snew = "数学（理）".replace("（", "(");
        snew.replace("）", ")");
    }

    @Override // com.dmj.service.test.StestService
    public List getoptionList(int examPaperNum, String ocount, String otype, String qNum, String examplace) {
        String ocountStr = "";
        if (!ocount.equals("-1")) {
            ocountStr = " AND Length(answer)={ocount} ";
        }
        String otypeStr = "";
        String moreLenOPtion = "";
        if (!otype.equals("-1")) {
            if ("2".equals(otype)) {
                otypeStr = " AND multiple='0' ";
                moreLenOPtion = " and LENGTH(answer)>1 ";
            } else {
                otypeStr = " AND multiple={otype} ";
            }
        }
        String qNumStr = "";
        if (!qNum.equals("-1")) {
            qNumStr = " AND id={qNum} ";
        }
        String tcStr = "-1".equals(examplace) ? "" : " and testingCentreId={examplace}  ";
        String sql = "SELECT DISTINCT o.answer  FROM (SELECT examPaperNum,questionNum,answer from objectivescore where examPaperNum={examPaperNum} " + tcStr + " and answer <> '' and answer IS NOT NULL " + moreLenOPtion + ocountStr + " GROUP BY questionNum,answer) o inner JOIN (SELECT id FROM define WHERE examPaperNum={examPaperNum}  " + qNumStr + otypeStr + " UNION ALL  SELECT id FROM subdefine WHERE examPaperNum={examPaperNum} " + qNumStr + otypeStr + ") d ON d.id = o.questionNum ORDER BY o.answer";
        Map args = new HashMap();
        args.put("ocount", ocount);
        args.put("otype", otype);
        args.put("qNum", qNum);
        args.put("examplace", examplace);
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        return this.dao._queryColList(sql, args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer getoptionCount(int examPaperNum, String otype, String qNum) {
        String otypeStr = ("0".equals(otype) || "1".equals(otype)) ? " AND multiple={otype} " : "";
        String qNumStr = !"-1".equals(qNum) ? " AND id={qNum} " : "";
        String sql = "SELECT MAX(dd.optionCount) FROM (select distinct IFNULL(optionCount,0) optionCount from define WHERE examPaperNum={examPaperNum} and questionType='0' " + qNumStr + otypeStr + " union select distinct IFNULL(optionCount,0) optionCount from subdefine WHERE examPaperNum={examPaperNum}  and questionType='0' " + qNumStr + otypeStr + ") dd ";
        Map args = new HashMap();
        args.put("qNum", qNum);
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        Object res = this.dao._queryObject(sql, args);
        if (null == res) {
            return 0;
        }
        return Integer.valueOf(res.toString());
    }

    @Override // com.dmj.service.test.StestService
    public List<Define> getqNumList(int exampaperNum, String otype) {
        String otypeStr = "";
        if (!otype.equals("-1")) {
            if ("0".equals(otype) || "2".equals(otype)) {
                otypeStr = "AND multiple='0'";
            } else {
                otypeStr = "AND multiple='1'";
            }
        }
        String sql = "SELECT id,questionNum ext1 FROM define WHERE examPaperNum={exampaperNum}  AND questionType='0' AND choosename!='T'  AND isParent!='1'" + otypeStr + " UNION ALL SELECT id,questionNum   FROM subdefine WHERE examPaperNum={exampaperNum} AND questionType='0'" + otypeStr + " ORDER BY REPLACE(ext1,'_','.')*1";
        Map args = StreamMap.create().put("exampaperNum", (Object) Integer.valueOf(exampaperNum));
        return this.dao._queryBeanList(sql, Define.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<Subdefine> getChooseQuestionList(int exampaperNum, String qtype) {
        Map args = new HashMap();
        args.put("exampaperNum", Integer.valueOf(exampaperNum));
        return this.dao._queryBeanList("SELECT d.id pid,x.id id,x.fullScore,x.questionNum  FROM(SELECT questionNum,id,fullScore,chooseName FROM define WHERE examPaperNum = {exampaperNum} \t AND chooseName='T') d  LEFT JOIN (SELECT questionNum,id,fullScore,chooseName,questionType,orderNum FROM define WHERE examPaperNum = {exampaperNum} AND LENGTH(choosename)>2 )  x ON x.chooseName = CAST(d.id AS CHAR)   ORDER BY x.orderNum asc ", Subdefine.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public String getNewQusetionNum(String regId, int optionval, String examPaperNum, String chooseName, String oldQuestionNum) {
        String newQusetionNum = "";
        Map args = new HashMap();
        args.put("regId", regId);
        String regStr = this.dao._queryStr("select regStr from choosenamerecord where regId= {regId} ", args);
        int t = regStr.length() / 2;
        Map args2 = new HashMap();
        args2.put("examPaperNum", examPaperNum);
        args2.put("chooseName", chooseName);
        List<?> _queryBeanList = this.dao._queryBeanList("SELECT id FROM define WHERE examPaperNum= {examPaperNum} AND choosename={chooseName} ORDER BY orderNum", Define.class, args2);
        int val = 0;
        for (int i = 0; i < t; i++) {
            int a = Integer.parseInt(regStr.substring(i * 2, (i * 2) + 2));
            if (a >= optionval && a > val) {
                newQusetionNum = ((Define) _queryBeanList.get(i)).getId();
                val = a;
            }
        }
        if (val == 0) {
            newQusetionNum = ((Define) _queryBeanList.get(0)).getId();
        }
        if (oldQuestionNum.equals(newQusetionNum)) {
            return "";
        }
        return newQusetionNum;
    }

    @Override // com.dmj.service.test.StestService
    public List<Score> getChooseQuestionDetailList(Map<String, String> map) {
        String epNum = map.get("epNum");
        String questionNum = map.get("questionNum");
        String studentName = map.get("studentName");
        String examroom = map.get("examroom");
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String pageStart = map.get("pageStart");
        String limit = map.get("limit");
        String questionType = map.get("questionType");
        String isModify = map.get("isModify");
        String examplace = map.get("examplace");
        String proofreadingStatus = map.get("proofreadingStatus");
        String psStr = "";
        if ("0".equals(proofreadingStatus)) {
            psStr = " and cd.proofreadingStatus is null ";
        } else if ("1".equals(proofreadingStatus)) {
            psStr = " and cd.proofreadingStatus = '1' ";
        }
        if (Integer.parseInt(pageStart) < 0) {
            pageStart = "0";
        }
        Map args = new HashMap();
        String sql = "SELECT cd.id,d.id questionNum,d.questionNum questionName,st.studentName,r.schoolNum,sch.schoolName,r.examinationRoomNum,c.id classNum,c.className,r.id regId,r.studentId,r.page,r.examPaperNum,d.chooseName,d.isParent,'" + questionType + "' qtype,cd.max tag,cd.proofreadingSource ext8 FROM ( SELECT x.id,d.fullScore,x.questionNum,x.chooseName,x.isParent ,x.id pid FROM(SELECT questionNum,id,fullScore,chooseName FROM define WHERE examPaperNum = {epNum} \t AND chooseName='T') d LEFT JOIN (SELECT questionNum,id,fullScore,chooseName,isParent,questionType,`merge` FROM define WHERE examPaperNum = {epNum}\t AND LENGTH(choosename)>2  )  x ON x.chooseName = CAST(d.id AS CHAR) ";
        args.put("epNum", epNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examroom", examroom);
        args.put("questionNum", questionNum);
        args.put("examplace", examplace);
        args.put("studentName", "%" + studentName + "%");
        args.put("pageStart", pageStart);
        args.put("limit", limit);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        if (null != questionNum && !questionNum.equals("") && !questionNum.equals("-1")) {
            sql = sql + "WHERE x.id = {questionNum} ";
        }
        String sql2 = (sql + ") d  ") + "LEFT JOIN regexaminee r ON  r.exampaperNum={epNum} ";
        if (!"-1".equals(examplace)) {
            sql2 = sql2 + " and r.testingCentreId={examplace} ";
        }
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql2 = sql2 + "AND r.schoolNum={schoolNum}  ";
        }
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            sql2 = sql2 + "AND r.examinationRoomNum={examroom}  ";
        }
        String sql3 = (sql2 + "LEFT JOIN choosenamerecord  cd ON   cd.regId = r.id  AND cd.questionNum=d.id ") + "LEFT JOIN student st ON st.gradeNum={gradeNum} and  st.id = r.studentid ";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql3 = sql3 + "AND st.schoolNum={schoolNum} ";
        }
        String sql4 = sql3 + "LEFT JOIN class c ON c.gradeNum={gradeNum}  and c.id=st.classNum ";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql4 = sql4 + "AND c.schoolNum={schoolNum}  ";
        }
        String sql5 = sql4 + "LEFT JOIN  school sch ON r.schoolNum = sch.id WHERE    cd.questionNum=d.pid ";
        if (null != studentName && !studentName.equals("")) {
            sql5 = sql5 + "AND st.studentName LIKE {studentName} ";
        }
        if (null != isModify && isModify.equals("T")) {
            sql5 = sql5 + "AND cd.isModify=1 ";
        }
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql5 = sql5 + "AND sch.id={schoolNum}  ";
        }
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            sql5 = sql5 + "AND r.examinationRoomNum={examroom} ";
        }
        String sql6 = sql5 + psStr + "ORDER BY cd.questionNum, cd.max ";
        if (!limit.equals("limitless")) {
            sql6 = sql6 + " limit {pageStart},{limit}";
        }
        return this.dao._queryBeanList(sql6, Score.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<Score> getAuthChooseQuestionDetailList(Map<String, String> map) {
        String epNum = map.get("epNum");
        String questionNum = map.get("questionNum");
        String studentName = map.get("studentName");
        String examroom = map.get("examroom");
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String pageStart = map.get("pageStart");
        String limit = map.get("limit");
        String questionType = map.get("questionType");
        String isModify = map.get("isModify");
        String userId = map.get("userId");
        String proofreadingStatus = map.get("proofreadingStatus");
        String psStr = "";
        if ("0".equals(proofreadingStatus)) {
            psStr = " and cd.proofreadingStatus is null ";
        } else if ("1".equals(proofreadingStatus)) {
            psStr = " and cd.proofreadingStatus = '1' ";
        }
        if (Integer.parseInt(pageStart) < 0) {
            pageStart = "0";
        }
        String sql = "SELECT cd.id,d.id questionNum,d.questionNum questionName,st.studentName,r.schoolNum,sch.schoolName,r.examinationRoomNum,c.id classNum,c.className,r.id regId,r.studentId,r.page,r.examPaperNum,d.chooseName,d.isParent,'" + questionType + "' qtype,cd.max tag,cd.proofreadingSource ext8 FROM ( SELECT x.id,d.fullScore,x.questionNum,x.chooseName,x.isParent ,x.id pid FROM(SELECT questionNum,id,fullScore,chooseName FROM define WHERE examPaperNum = {epNum} \t AND chooseName='T') d LEFT JOIN (SELECT questionNum,id,fullScore,chooseName,isParent,questionType,`merge` FROM define WHERE examPaperNum = {epNum} \t AND LENGTH(choosename)>2  )  x ON x.chooseName = CAST(d.id AS CHAR) ";
        Map args = new HashMap();
        args.put("epNum", epNum);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examroom", examroom);
        args.put("userId", userId);
        args.put("studentName", "%" + studentName + "%");
        args.put("pageStart", pageStart);
        args.put("limit", limit);
        if (null != questionNum && !questionNum.equals("") && !questionNum.equals("-1")) {
            sql = sql + "WHERE x.id =  {questionNum} ";
        }
        String sql2 = (sql + ") d  ") + "LEFT JOIN regexaminee r ON  r.exampaperNum= {epNum} LEFT JOIN scanpermission scanp on scanp.testingCentreId = r.testingCentreId ";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql2 = sql2 + "AND r.schoolNum={schoolNum}  ";
        }
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            sql2 = sql2 + "AND r.examinationRoomNum={examroom}  ";
        }
        String sql3 = (sql2 + "LEFT JOIN choosenamerecord  cd ON   cd.regId = r.id  AND cd.questionNum=d.id ") + "LEFT JOIN student st ON st.gradeNum={gradeNum}  and  st.id = r.studentid ";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql3 = sql3 + "AND st.schoolNum={schoolNum} ";
        }
        String sql4 = sql3 + "LEFT JOIN class c ON c.gradeNum=" + gradeNum + "  and c.id=st.classNum ";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql4 = sql4 + "AND c.schoolNum={schoolNum} ";
        }
        String sql5 = sql4 + "LEFT JOIN  school sch ON r.schoolNum = sch.id WHERE  scanp.userNum = {userId}  and  cd.questionNum=d.pid ";
        if (null != studentName && !studentName.equals("")) {
            sql5 = sql5 + "AND st.studentName LIKE {studentName} ";
        }
        if (null != isModify && isModify.equals("T")) {
            sql5 = sql5 + "AND cd.isModify=1 ";
        }
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql5 = sql5 + "AND sch.id={schoolNum} ";
        }
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            sql5 = sql5 + "AND r.examinationRoomNum={examroom} ";
        }
        String sql6 = sql5 + psStr + "ORDER BY cd.questionNum, cd.max ";
        if (!limit.equals("limitless")) {
            sql6 = sql6 + " limit {pageStart},{limit} ";
        }
        return this.dao._queryBeanList(sql6, Score.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer getChooseQuestionDetailNum(Map<String, String> map) {
        String epNum = map.get("epNum");
        String questionNum = map.get("questionNum");
        String studentName = map.get("studentName");
        String examroom = map.get("examroom");
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        map.get("questionType");
        String isModify = map.get("isModify");
        String examplace = map.get("examplace");
        String proofreadingStatus = map.get("proofreadingStatus");
        String psStr = "";
        if ("0".equals(proofreadingStatus)) {
            psStr = " and cd.proofreadingStatus is null ";
        } else if ("1".equals(proofreadingStatus)) {
            psStr = " and cd.proofreadingStatus = '1' ";
        }
        String sql = "SELECT count(1) FROM ( SELECT x.id,d.fullScore,x.questionNum,x.chooseName,x.isParent ,x.id pid FROM(SELECT questionNum,id,fullScore,chooseName FROM define WHERE examPaperNum ={epNum}\t AND chooseName='T') d LEFT JOIN (SELECT questionNum,id,fullScore,chooseName,isParent,questionType FROM define WHERE examPaperNum = {epNum}\t AND LENGTH(choosename)>2  )  x ON x.chooseName = CAST(d.id AS CHAR) ";
        Map args = new HashMap();
        args.put("epNum", epNum);
        args.put("questionNum", questionNum);
        args.put("examplace", examplace);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examroom", examroom);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("studentName", "%" + studentName + "%");
        if (null != questionNum && !questionNum.equals("") && !questionNum.equals("-1")) {
            sql = sql + "WHERE x.id =  {questionNum} ";
        }
        String sql2 = (sql + ") d  ") + "LEFT JOIN regexaminee r ON  r.exampaperNum={epNum}  ";
        if (null != examplace && !examplace.equals("-1")) {
            sql2 = sql2 + " and r.testingCentreId={examplace} ";
        }
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql2 = sql2 + "AND r.schoolNum={schoolNum}  ";
        }
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            sql2 = sql2 + "AND r.examinationRoomNum={examroom}  ";
        }
        String sql3 = (sql2 + "LEFT JOIN choosenamerecord  cd ON   cd.regId = r.id  AND cd.questionNum=d.id ") + "LEFT JOIN student st ON st.gradeNum={gradeNum} and  st.id = r.studentid ";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql3 = sql3 + "AND st.schoolNum={schoolNum}  ";
        }
        if (null != studentName && !studentName.equals("")) {
            sql3 = sql3 + "AND st.studentName LIKE {studentName} ";
        }
        String sql4 = sql3 + "LEFT JOIN  school sch ON r.schoolNum = sch.id  WHERE   cd.questionNum=d.pid ";
        if (null != studentName && !studentName.equals("")) {
            sql4 = sql4 + "AND st.studentName LIKE {studentName} ";
        }
        if (null != isModify && isModify.equals("T")) {
            sql4 = sql4 + "AND cd.isModify=1 ";
        }
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql4 = sql4 + "AND sch.id={schoolNum}  ";
        }
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            sql4 = sql4 + "AND r.examinationRoomNum={examroom}  ";
        }
        return this.dao._queryInt(sql4 + psStr, args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer getAuthChooseQuestionDetailNum(Map<String, String> map) {
        String epNum = map.get("epNum");
        String questionNum = map.get("questionNum");
        String studentName = map.get("studentName");
        String examroom = map.get("examroom");
        String gradeNum = map.get(Const.EXPORTREPORT_gradeNum);
        String schoolNum = map.get(Const.EXPORTREPORT_schoolNum);
        String isModify = map.get("isModify");
        String userId = map.get("userId");
        String proofreadingStatus = map.get("proofreadingStatus");
        String psStr = "";
        if ("0".equals(proofreadingStatus)) {
            psStr = " and cd.proofreadingStatus is null ";
        } else if ("1".equals(proofreadingStatus)) {
            psStr = " and cd.proofreadingStatus = '1' ";
        }
        String sql = "SELECT count(1) FROM ( SELECT x.id,d.fullScore,x.questionNum,x.chooseName,x.isParent ,x.id pid FROM(SELECT questionNum,id,fullScore,chooseName FROM define WHERE examPaperNum = {epNum} \t AND chooseName='T') d LEFT JOIN (SELECT questionNum,id,fullScore,chooseName,isParent,questionType FROM define WHERE examPaperNum = {epNum} \t AND LENGTH(choosename)>2  )  x ON x.chooseName = CAST(d.id AS CHAR) ";
        Map args = new HashMap();
        args.put("epNum", epNum);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("examroom", examroom);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("userId", userId);
        args.put("studentName", "%" + studentName + "%");
        if (null != questionNum && !questionNum.equals("") && !questionNum.equals("-1")) {
            sql = sql + "WHERE x.id =  {questionNum}  ";
        }
        String sql2 = (sql + ") d  ") + "LEFT JOIN regexaminee r ON  r.exampaperNum={epNum}   LEFT JOIN scanpermission scanp on scanp.testingCentreId = r.testingCentreId ";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql2 = sql2 + "AND r.schoolNum={schoolNum}  ";
        }
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            sql2 = sql2 + "AND r.examinationRoomNum={examroom}  ";
        }
        String sql3 = (sql2 + "LEFT JOIN choosenamerecord  cd ON   cd.regId = r.id  AND cd.questionNum=d.id ") + "LEFT JOIN student st ON st.gradeNum={gradeNum} and  st.id = r.studentid ";
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql3 = sql3 + "AND st.schoolNum={schoolNum} ";
        }
        String sql4 = sql3 + "LEFT JOIN  school sch ON r.schoolNum = sch.id  WHERE scanp.userNum = {userId}  and  cd.questionNum=d.pid ";
        if (null != studentName && !studentName.equals("")) {
            sql4 = sql4 + "AND st.studentName LIKE {studentName}  ";
        }
        if (null != isModify && isModify.equals("T")) {
            sql4 = sql4 + "AND cd.isModify=1 ";
        }
        if (null != schoolNum && !schoolNum.equals("") && !schoolNum.equals("-1")) {
            sql4 = sql4 + "AND sch.id=" + schoolNum + " ";
        }
        if (null != examroom && !examroom.equals("") && !examroom.equals("-1")) {
            sql4 = sql4 + "AND r.examinationRoomNum={examroom}  ";
        }
        return this.dao._queryInt(sql4 + psStr, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<AjaxData> getChooseModifyItem(String chooseName, String epNum) {
        Map args = StreamMap.create().put("epNum", (Object) epNum).put("chooseName", (Object) chooseName);
        return this.dao._queryBeanList("SELECT id num,questionNum name FROM define WHERE examPaperNum={epNum} AND choosename={chooseName}  ORDER BY orderNum", AjaxData.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public void updateCdProofreadingStatus(String cdId) {
        Map args = StreamMap.create().put("cdId", (Object) cdId);
        if (null == this.dao._queryObject("select id from choosenamerecord where id = {cdId}  and proofreadingStatus = '1'", args)) {
            this.dao._execute("update choosenamerecord set proofreadingStatus = '1' where id = {cdId} ", args);
        }
    }

    @Override // com.dmj.service.test.StestService
    public void updateObjsProofreadingStatus(String objSId) {
        Map args = StreamMap.create().put("objSId", (Object) objSId);
        if (null == this.dao._queryObject("select id from objectivescore where id = {objSId} and proofreadingStatus1 = '1'", args)) {
            this.dao._execute("update objectivescore set proofreadingStatus1 = '1' where id = {objSId} ", args);
        }
    }

    @Override // com.dmj.service.test.StestService
    public String updateExamplaceProofreadingStatus(String exam, String subject, String grade, String examplace) {
        String msg;
        Map args = new HashMap();
        args.put("exam", exam);
        args.put("subject", subject);
        args.put("grade", grade);
        args.put("examplace", examplace);
        Integer count = this.dao._queryInt("select  count(1) from illegal where examPaperNum=(select  examPaperNum  from examPaper where examnum={exam} and subjectnum={subject} and gradenum={grade})  and testingCentreId={examplace} and proofreadingStatus1 is null  and schoolNum in(select schoolNum from testingcentre_school where testingCentreId=(select  id from testingcentre where examNum={exam} and id={examplace}))", args);
        if (count.intValue() > 0) {
            this.dao._execute("update illegal set proofreadingStatus1 = '1' where examPaperNum=(select  examPaperNum  from examPaper where examnum={exam} and subjectnum={subject} and gradenum={grade})  and testingCentreId={examplace} and proofreadingStatus1 is null  and schoolNum in(select schoolNum from testingcentre_school where testingCentreId=(select  id from testingcentre where examNum={exam} and id={examplace}))", args);
            msg = "1";
        } else {
            msg = "0";
        }
        return msg;
    }

    @Override // com.dmj.service.test.StestService
    public void updateIllProofreadingStatus1(String illId) {
        Map args = StreamMap.create().put("illId", (Object) illId);
        if (null == this.dao._queryObject("select id from illegal where id = {illId}  and proofreadingStatus1 = '1'", args)) {
            this.dao._execute("update illegal set proofreadingStatus1 = '1' where id = {illId} ", args);
        }
    }

    @Override // com.dmj.service.test.StestService
    public int getIllegalCollateCount(String exam, String subject, String grade, String examplace, String examroom, String school, String illType, String studentName, String proofreadingStatus) {
        String exampaperNum = this.examDao.getExampaperNum(exam, subject, grade);
        String examplaceSql = "-1".equals(examplace) ? "" : " and ill.testingCentreId = {examplace} ";
        String examroomSql = "-1".equals(examroom) ? "" : " and ill.examinationRoomNum = {examroom}  ";
        String schoolSql = "-1".equals(school) ? "" : " and ill.schoolNum = {school} ";
        String illTypeSql = "-1".equals(illType) ? "" : " and ill.type ={illType}   ";
        String studentNameSql = "".equals(studentName) ? "" : " and stu.studentName like {studentName} ";
        Map args = new HashMap();
        args.put("examplace", examplace);
        args.put("examroom", examroom);
        args.put(License.SCHOOL, school);
        args.put("illType", illType);
        args.put("studentName", "%" + studentName + "%");
        String proofreadingStatusSql = "";
        if ("0".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 = '1' ";
        }
        String sql = "SELECT count(distinct ill.regId) FROM illegal ill LEFT JOIN student stu ON stu.id = ill.studentId WHERE ill.examPaperNum ={exampaperNum}  " + examroomSql + examplaceSql + schoolSql + illTypeSql + studentNameSql + proofreadingStatusSql;
        args.put("exampaperNum", exampaperNum);
        return this.dao._queryInt(sql, args).intValue();
    }

    @Override // com.dmj.service.test.StestService
    public int getIllegalCollateCountall(String exam, String grade, String examplace, String examroom, String school, String illType, String studentName, String proofreadingStatus) {
        String examplaceSql = "-1".equals(examplace) ? "" : " and ill.testingCentreId = {examplace} ";
        String examroomSql = "-1".equals(examroom) ? "" : " and ill.examinationRoomNum = {examroom}  ";
        String schoolSql = "-1".equals(school) ? "" : " and ill.schoolNum = {school} ";
        String illTypeSql = "-1".equals(illType) ? "" : " and ill.type ={illType}   ";
        String studentNameSql = "".equals(studentName) ? "" : " and stu.studentName like {studentName} ";
        Map args = new HashMap();
        args.put("examplace", examplace);
        args.put("examroom", examroom);
        args.put(License.SCHOOL, school);
        args.put("illType", illType);
        args.put(Const.EXPORTREPORT_examNum, exam);
        args.put(Const.EXPORTREPORT_gradeNum, grade);
        args.put("studentName", "%" + studentName + "%");
        String proofreadingStatusSql = "";
        if ("0".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 = '1' ";
        }
        String sql = "SELECT count(distinct ill.regId) FROM illegal ill LEFT JOIN student stu ON stu.id = ill.studentId LEFT JOIN exampaper ep ON ep.examPaperNum = ill.examPaperNum WHERE ep.examNum={examNum} and ep.gradeNum={gradeNum}  " + examroomSql + examplaceSql + schoolSql + illTypeSql + studentNameSql + proofreadingStatusSql;
        return this.dao._queryInt(sql, args).intValue();
    }

    @Override // com.dmj.service.test.StestService
    public int getAuthIllegalCollateCount(String exam, String subject, String grade, String examplace, String examroom, String school, String illType, String studentName, String proofreadingStatus, String userId) {
        String exampaperNum = this.examDao.getExampaperNum(exam, subject, grade);
        String examplaceSql = "-1".equals(examplace) ? "" : " and ill.testingCentreId = {examplace} ";
        String examroomSql = "-1".equals(examroom) ? "" : " and ill.examinationRoomNum = {examroom}  ";
        String schoolSql = "-1".equals(school) ? "" : " and ill.schoolNum ={school}  ";
        String illTypeSql = "-1".equals(illType) ? "" : " and ill.type = {illType}  ";
        String studentNameSql = "".equals(studentName) ? "" : " and stu.studentName like {studentName} ";
        Map args = new HashMap();
        args.put("examplace", examplace);
        args.put("examroom", examroom);
        args.put(License.SCHOOL, school);
        args.put("illType", illType);
        args.put("studentName", "%" + studentName + "%");
        String proofreadingStatusSql = "";
        if ("0".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 = '1' ";
        }
        String sql = "SELECT count(distinct ill.regId) FROM illegal ill LEFT JOIN scanpermission scanp on scanp.testingCentreId = ill.testingCentreId LEFT JOIN student stu ON stu.id = ill.studentId WHERE scanp.userNum ={userId}  and ill.examPaperNum = {exampaperNum} " + examroomSql + examplaceSql + schoolSql + illTypeSql + studentNameSql + proofreadingStatusSql;
        args.put("userId", userId);
        args.put("exampaperNum", exampaperNum);
        return this.dao._queryInt(sql, args).intValue();
    }

    @Override // com.dmj.service.test.StestService
    public int getAuthIllegalCollateCountall(String exam, String grade, String examplace, String examroom, String school, String illType, String studentName, String proofreadingStatus, String userId) {
        String examplaceSql = "-1".equals(examplace) ? "" : " and ill.testingCentreId = {examplace} ";
        String examroomSql = "-1".equals(examroom) ? "" : " and ill.examinationRoomNum = {examroom}  ";
        String schoolSql = "-1".equals(school) ? "" : " and ill.schoolNum ={school}  ";
        String illTypeSql = "-1".equals(illType) ? "" : " and ill.type = {illType}  ";
        String studentNameSql = "".equals(studentName) ? "" : " and stu.studentName like {studentName} ";
        Map args = new HashMap();
        args.put("examplace", examplace);
        args.put("examroom", examroom);
        args.put(License.SCHOOL, school);
        args.put("illType", illType);
        args.put(Const.EXPORTREPORT_examNum, exam);
        args.put(Const.EXPORTREPORT_gradeNum, grade);
        args.put("studentName", "%" + studentName + "%");
        String proofreadingStatusSql = "";
        if ("0".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 = '1' ";
        }
        String sql = "SELECT count(distinct ill.regId) FROM illegal ill LEFT JOIN scanpermission scanp on scanp.testingCentreId = ill.testingCentreId LEFT JOIN student stu ON stu.id = ill.studentId LEFT JOIN exampaper ep ON ep.examPaperNum = ill.examPaperNum WHERE scanp.userNum ={userId}  and ep.examNum={examNum} and ep.gradeNum={gradeNum}  " + examroomSql + examplaceSql + schoolSql + illTypeSql + studentNameSql + proofreadingStatusSql;
        args.put("userId", userId);
        return this.dao._queryInt(sql, args).intValue();
    }

    @Override // com.dmj.service.test.StestService
    public List<Illegal> getIllegalCollateData(String exam, String subject, String grade, String examplace, String examroom, String school, String illType, String studentName, String proofreadingStatus, int pagestart, int pageSize) {
        String exampaperNum = this.examDao.getExampaperNum(exam, subject, grade);
        String examplaceSql = "-1".equals(examplace) ? "" : " and ill.testingCentreId = {examplace} ";
        String examroomSql = "-1".equals(examroom) ? "" : " and ill.examinationRoomNum = {examroom} ";
        String schoolSql = "-1".equals(school) ? "" : " and ill.schoolNum = {school}  ";
        String illTypeSql = "-1".equals(illType) ? "" : " and ill.type = {illType}  ";
        String studentNameSql = "".equals(studentName) ? "" : " and stu.studentName like {studentName} ";
        Map args = new HashMap();
        args.put("examplace", examplace);
        args.put("examroom", examroom);
        args.put(License.SCHOOL, school);
        args.put("illType", illType);
        args.put("studentName", "%" + studentName + "%");
        String proofreadingStatusSql = "";
        if ("0".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 = '1' ";
        }
        String sql = "SELECT ill.id,ill.regId,ill.examPaperNum,ill.studentId,ill.examinationRoomNum,sch.schoolName ext1,cla.className,stu.studentId ext2,stu.studentName,ill.type FROM illegal ill LEFT JOIN school sch ON sch.id = ill.schoolNum LEFT JOIN student stu ON stu.id = ill.studentId LEFT JOIN class cla ON cla.id = stu.classNum WHERE ill.examPaperNum = {exampaperNum}  " + examroomSql + examplaceSql + schoolSql + illTypeSql + studentNameSql + proofreadingStatusSql + "ORDER BY ill.illegalVal desc,ill.type,sch.schoolNum,cla.classNum,stu.studentId Limit {pagestart},{pageSize}";
        args.put("exampaperNum", exampaperNum);
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao._queryBeanList(sql, Illegal.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<Illegal> getIllegalCollateDataall(String exam, String grade, String examplace, String examroom, String school, String illType, String studentName, String proofreadingStatus, int pagestart, int pageSize) {
        String examplaceSql = "-1".equals(examplace) ? "" : " and ill.testingCentreId = {examplace} ";
        String examroomSql = "-1".equals(examroom) ? "" : " and ill.examinationRoomNum = {examroom} ";
        String schoolSql = "-1".equals(school) ? "" : " and ill.schoolNum = {school}  ";
        String illTypeSql = "-1".equals(illType) ? "" : " and ill.type = {illType}  ";
        String studentNameSql = "".equals(studentName) ? "" : " and stu.studentName like {studentName} ";
        Map args = new HashMap();
        args.put("examplace", examplace);
        args.put("examroom", examroom);
        args.put(License.SCHOOL, school);
        args.put("illType", illType);
        args.put(Const.EXPORTREPORT_examNum, exam);
        args.put(Const.EXPORTREPORT_gradeNum, grade);
        args.put("studentName", "%" + studentName + "%");
        String proofreadingStatusSql = "";
        if ("0".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 = '1' ";
        }
        String sql = "SELECT ill.id,ill.regId,ill.examPaperNum,ill.studentId,ill.examinationRoomNum,sch.schoolName ext1,cla.className,stu.studentId ext2,stu.studentName,ill.type,s.subjectNum,s.subjectName FROM illegal ill LEFT JOIN school sch ON sch.id = ill.schoolNum LEFT JOIN student stu ON stu.id = ill.studentId LEFT JOIN class cla ON cla.id = stu.classNum LEFT JOIN exampaper ep ON ep.examPaperNum = ill.examPaperNum LEFT JOIN subject s ON s.subjectNum = ep.subjectNum WHERE ep.examNum={examNum} and ep.gradeNum={gradeNum}  " + examroomSql + examplaceSql + schoolSql + illTypeSql + studentNameSql + proofreadingStatusSql + "ORDER BY sch.schoolNum,cla.classNum,stu.studentId,s.subjectNum*1 Limit {pagestart},{pageSize}";
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao._queryBeanList(sql, Illegal.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<Illegal> getAuthIllegalCollateData(String exam, String subject, String grade, String examplace, String examroom, String school, String illType, String studentName, String proofreadingStatus, int pagestart, int pageSize, String userId) {
        String exampaperNum = this.examDao.getExampaperNum(exam, subject, grade);
        String examplaceSql = "-1".equals(examplace) ? "" : " and ill.testingCentreId = {examplace}  ";
        String examroomSql = "-1".equals(examroom) ? "" : " and ill.examinationRoomNum = {examroom}  ";
        String schoolSql = "-1".equals(school) ? "" : " and ill.schoolNum = {school} ";
        String illTypeSql = "-1".equals(illType) ? "" : " and ill.type = {illType}  ";
        String studentNameSql = "".equals(studentName) ? "" : " and stu.studentName like {studentName} ";
        Map args = new HashMap();
        args.put("examplace", examplace);
        args.put("examroom", examroom);
        args.put(License.SCHOOL, school);
        args.put("illType", illType);
        args.put("studentName", "%" + studentName + "%");
        String proofreadingStatusSql = "";
        if ("0".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 = '1' ";
        }
        String sql = "SELECT ill.id,ill.regId,ill.examPaperNum,ill.studentId,ill.examinationRoomNum,sch.schoolName ext1,cla.className,stu.studentId ext2,stu.studentName,ill.type FROM illegal ill LEFT JOIN scanpermission scanp on scanp.testingCentreId = ill.testingCentreId LEFT JOIN school sch ON sch.id = ill.schoolNum LEFT JOIN student stu ON stu.id = ill.studentId LEFT JOIN class cla ON cla.id = stu.classNum WHERE scanp.userNum = {userId}  and ill.examPaperNum = {exampaperNum}  " + examroomSql + examplaceSql + schoolSql + illTypeSql + studentNameSql + proofreadingStatusSql + "ORDER BY ill.illegalVal desc,ill.type,sch.schoolNum,cla.classNum,stu.studentId Limit {pagestart},{pageSize} ";
        args.put("userId", userId);
        args.put("exampaperNum", exampaperNum);
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao._queryBeanList(sql, Illegal.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public List<Illegal> getAuthIllegalCollateDataall(String exam, String grade, String examplace, String examroom, String school, String illType, String studentName, String proofreadingStatus, int pagestart, int pageSize, String userId) {
        String examplaceSql = "-1".equals(examplace) ? "" : " and ill.testingCentreId = {examplace}  ";
        String examroomSql = "-1".equals(examroom) ? "" : " and ill.examinationRoomNum = {examroom}  ";
        String schoolSql = "-1".equals(school) ? "" : " and ill.schoolNum = {school} ";
        String illTypeSql = "-1".equals(illType) ? "" : " and ill.type = {illType}  ";
        String studentNameSql = "".equals(studentName) ? "" : " and stu.studentName like {studentName} ";
        Map args = new HashMap();
        args.put("examplace", examplace);
        args.put("examroom", examroom);
        args.put(License.SCHOOL, school);
        args.put("illType", illType);
        args.put(Const.EXPORTREPORT_examNum, exam);
        args.put(Const.EXPORTREPORT_gradeNum, grade);
        args.put("studentName", "%" + studentName + "%");
        String proofreadingStatusSql = "";
        if ("0".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 is null ";
        } else if ("1".equals(proofreadingStatus)) {
            proofreadingStatusSql = " and ill.proofreadingStatus1 = '1' ";
        }
        String sql = "SELECT ill.id,ill.regId,ill.examPaperNum,ill.studentId,ill.examinationRoomNum,sch.schoolName ext1,cla.className,stu.studentId ext2,stu.studentName,ill.type,s.subjectNum,s.subjectName FROM illegal ill LEFT JOIN scanpermission scanp on scanp.testingCentreId = ill.testingCentreId LEFT JOIN school sch ON sch.id = ill.schoolNum LEFT JOIN student stu ON stu.id = ill.studentId LEFT JOIN class cla ON cla.id = stu.classNum LEFT JOIN exampaper ep ON ep.examPaperNum = ill.examPaperNum LEFT JOIN subject s ON s.subjectNum = ep.subjectNum WHERE scanp.userNum = {userId}  and ep.examNum={examNum} and ep.gradeNum={gradeNum}  " + examroomSql + examplaceSql + schoolSql + illTypeSql + studentNameSql + proofreadingStatusSql + "ORDER BY sch.schoolNum,cla.classNum,stu.studentId ,s.subjectNum*1 Limit {pagestart},{pageSize} ";
        args.put("userId", userId);
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pageSize", Integer.valueOf(pageSize));
        return this.dao._queryBeanList(sql, Illegal.class, args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer getObjitemImgCount(int examPaperNum, String qNum, String examroom, String selected, String optionvalue, String examplace) {
        Map args = new HashMap();
        args.put("examroom", examroom);
        args.put("qNum", qNum);
        args.put("examplace", examplace);
        args.put("optionvalue", optionvalue);
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("selected", selected);
        String examroomStr = !"-1".equals(examroom) ? " and ob.examinationRoomNum={examroom} " : "";
        String qNumStr = !"-1".equals(qNum) ? " and ob.questionNum={qNum} " : "";
        String tcStr = !"-1".equals(examplace) ? " and ob.testingCentreId={examplace} " : "";
        String optionvalueStr = !"-1".equals(optionvalue) ? " and ob.item={optionvalue} " : "";
        String sql = "SELECT count(1) FROM objitem ob LEFT JOIN define d on d.id=ob.questionNum LEFT JOIN subdefine subd on subd.id=ob.questionNum  right join objectivescore o on ob.scoreId=o.id WHERE ob.examPaperNum={examPaperNum} " + examroomStr + qNumStr + tcStr + optionvalueStr + " and ob.selected={selected} and IFNULL(d.multiple,subd.multiple)='1' and  o.proofreadingStatus1 is  null";
        return Convert.toInt(this.dao._queryObject(sql, args), 0);
    }

    @Override // com.dmj.service.test.StestService
    public List<Map<String, Object>> getObjitemImglist(int examPaperNum, String qNum, String examroom, String selected, String optionvalue, int pagestart, int pagesize, String examplace) {
        Map args = new HashMap();
        args.put("examroom", examroom);
        args.put("qNum", qNum);
        args.put("examplace", examplace);
        args.put("optionvalue", optionvalue);
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("selected", selected);
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pagesize", Integer.valueOf(pagesize));
        String examroomStr = (!StrUtil.isNotEmpty(examroom) || "-1".equals(examroom)) ? "" : " and ob.examinationRoomNum={examroom} ";
        String qNumStr = (!StrUtil.isNotEmpty(qNum) || "-1".equals(qNum)) ? "" : " and ob.questionNum={qNum} ";
        String tcStr = (!StrUtil.isNotEmpty(examplace) || "-1".equals(examplace)) ? "" : " and ob.testingCentreId={examplace} ";
        String optionvalueStr = (!StrUtil.isNotEmpty(optionvalue) || "-1".equals(optionvalue)) ? "" : " and ob.item={optionvalue} ";
        String orderStr = "";
        String limStr = "";
        if (pagesize > 0) {
            if ("0".equals(selected)) {
                orderStr = " ORDER BY ob.val*1 DESC ";
            } else {
                orderStr = " ORDER BY ob.val*1 ASC ";
            }
            limStr = " LIMIT {pagestart},{pagesize} ";
        }
        String sql = "SELECT im.location,ob.img,ob.val ext8,IFNULL(d.questionNum,subd.questionNum) questionName,ob.item,ob.regId,ob.scoreid scoreId,ob.examPaperNum,ob.id FROM objitem ob LEFT JOIN define d on d.id=ob.questionNum LEFT JOIN subdefine subd on subd.id=ob.questionNum LEFT JOIN imgpath im ON im.id=ob.imgpath  right join objectivescore o on ob.scoreId=o.id WHERE ob.examPaperNum={examPaperNum} " + examroomStr + qNumStr + tcStr + optionvalueStr + " and ob.selected={selected}  and  o.proofreadingStatus1 is null and IFNULL(d.multiple,subd.multiple)='1' " + orderStr + limStr;
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer getObjitemImgCount_auth(int examPaperNum, String qNum, String examroom, String selected, String optionvalue, String examplace, String userId) {
        Map args = new HashMap();
        args.put("examroom", examroom);
        args.put("qNum", qNum);
        args.put("examplace", examplace);
        args.put("optionvalue", optionvalue);
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("selected", selected);
        args.put("userId", userId);
        String examroomStr = !"-1".equals(examroom) ? " and ob.examinationRoomNum={examroom} " : "";
        String qNumStr = !"-1".equals(qNum) ? " and ob.questionNum={qNum} " : "";
        String tcStr = !"-1".equals(examplace) ? " and ob.testingCentreId={examplace} " : "";
        String optionvalueStr = !"-1".equals(optionvalue) ? " and ob.item={optionvalue} " : "";
        String sql = "SELECT count(1) FROM objitem ob LEFT JOIN scanpermission scanp on scanp.testingCentreId=ob.testingCentreId LEFT JOIN define d on d.id=ob.questionNum LEFT JOIN subdefine subd on subd.id=ob.questionNum  right join objectivescore o on ob.scoreId=o.id WHERE ob.examPaperNum={examPaperNum} " + examroomStr + qNumStr + tcStr + optionvalueStr + " and ob.selected={selected} and scanp.userNum={userId}  and  o.proofreadingStatus1 is null and IFNULL(d.multiple,subd.multiple)='1' ";
        return Convert.toInt(this.dao._queryObject(sql, args), 0);
    }

    @Override // com.dmj.service.test.StestService
    public List<Map<String, Object>> getObjitemImglist_auth(int examPaperNum, String qNum, String examroom, String selected, String optionvalue, int pagestart, int pagesize, String examplace, String userId) {
        Map args = new HashMap();
        args.put("examroom", examroom);
        args.put("qNum", qNum);
        args.put("examplace", examplace);
        args.put("optionvalue", optionvalue);
        args.put("examPaperNum", Integer.valueOf(examPaperNum));
        args.put("selected", selected);
        args.put("pagestart", Integer.valueOf(pagestart));
        args.put("pagesize", Integer.valueOf(pagesize));
        args.put("userId", userId);
        String examroomStr = (!StrUtil.isNotEmpty(examroom) || "-1".equals(examroom)) ? "" : " and ob.examinationRoomNum={examroom} ";
        String qNumStr = (!StrUtil.isNotEmpty(qNum) || "-1".equals(qNum)) ? "" : " and ob.questionNum={qNum} ";
        String tcStr = (!StrUtil.isNotEmpty(examplace) || "-1".equals(examplace)) ? "" : " and ob.testingCentreId={examplace} ";
        String optionvalueStr = (!StrUtil.isNotEmpty(optionvalue) || "-1".equals(optionvalue)) ? "" : " and ob.item={optionvalue} ";
        String orderStr = "";
        String limStr = "";
        if (pagesize > 0) {
            if ("0".equals(selected)) {
                orderStr = " ORDER BY ob.val*1 DESC ";
            } else {
                orderStr = " ORDER BY ob.val*1 ASC ";
            }
            limStr = " LIMIT {pagestart},{pagesize} ";
        }
        String sql = "SELECT im.location,ob.img,ob.val ext8,IFNULL(d.questionNum,subd.questionNum) questionName,ob.item,ob.regId,ob.scoreid scoreId,ob.examPaperNum,ob.id FROM objitem ob LEFT JOIN scanpermission scanp on scanp.testingCentreId=ob.testingCentreId LEFT JOIN define d on d.id=ob.questionNum LEFT JOIN subdefine subd on subd.id=ob.questionNum LEFT JOIN imgpath im ON im.id=ob.imgpath  right join objectivescore o on ob.scoreId=o.id WHERE ob.examPaperNum={examPaperNum} " + examroomStr + qNumStr + tcStr + optionvalueStr + " and ob.selected={selected} and scanp.userNum={userId}  and  o.proofreadingStatus1 is  nulland IFNULL(d.multiple,subd.multiple)='1' " + orderStr + limStr;
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    public List<Map<String, Object>> getObjitemDataList(String examPaperNum, String qNum, String examroom, String selected, String optionvalue, String examplace, String userId, String minVal) {
        Map args = new HashMap();
        args.put("examroom", examroom);
        args.put("qNum", qNum);
        args.put("examplace", examplace);
        args.put("optionvalue", optionvalue);
        args.put("minVal", minVal);
        args.put("userId", userId);
        args.put("examPaperNum", examPaperNum);
        args.put("selected", selected);
        String examroomStr = (!StrUtil.isNotEmpty(examroom) || "-1".equals(examroom)) ? "" : " and ob.examinationRoomNum={examroom} ";
        String qNumStr = (!StrUtil.isNotEmpty(qNum) || "-1".equals(qNum)) ? "" : " and ob.questionNum={qNum} ";
        String tcStr = (!StrUtil.isNotEmpty(examplace) || "-1".equals(examplace)) ? "" : " and ob.testingCentreId={examplace} ";
        String optionvalueStr = (!StrUtil.isNotEmpty(optionvalue) || "-1".equals(optionvalue)) ? "" : " and ob.item={optionvalue} ";
        String valStr = "0".equals(selected) ? " and ob.val>={minVal} " : " and ob.val<{minVal} ";
        String authStr = "";
        String authStr2 = "";
        if (!this.examDao.isManager(userId) && "-1".equals(examplace)) {
            authStr = " LEFT JOIN scanpermission scanp on scanp.testingCentreId=ob.testingCentreId ";
            authStr2 = " and scanp.userNum={userId} ";
        }
        String sql = "SELECT ob.scoreid scoreId,ob.questionNum,GROUP_CONCAT(distinct ob.item ORDER BY ob.item SEPARATOR '' ) items,objs.answer,objs.regResult,GROUP_CONCAT(distinct ob.id) ids,objs.isException FROM objitem ob " + authStr + "LEFT JOIN define d on d.id=ob.questionNum LEFT JOIN subdefine subd on subd.id=ob.questionNum LEFT JOIN objectivescore objs on objs.id=ob.scoreid WHERE ob.examPaperNum={examPaperNum} " + examroomStr + qNumStr + tcStr + optionvalueStr + " and ob.selected={selected} " + valStr + authStr2 + "and IFNULL(d.multiple,subd.multiple)='1' group by ob.scoreid ";
        return this.dao._queryMapList(sql, TypeEnum.StringObject, args);
    }

    public Map<String, Object> getOneScoreIdData(String scoreId) {
        Map args = new HashMap();
        args.put("scoreId", scoreId);
        return this.dao._querySimpleMap("SELECT objs.id scoreId,objs.questionNum,objs.answer,objs.regResult,objs.isException FROM objectivescore objs WHERE objs.id={scoreId} ", args);
    }

    @Override // com.dmj.service.test.StestService
    public void updateSelected(String examPaperNum, String qNum, String examroom, String selected, String optionvalue, String examplace, String userId, String minVal) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        List<Map<String, Object>> obDataList = getObjitemDataList(examPaperNum, qNum, examroom, selected, optionvalue, examplace, userId, minVal);
        List<Map<String, Object>> defineMapList = this.dao._queryMapList("select id,lengout,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26,deduction,fullScore,hasErrorSection,inspectionlevel,multiple,answer from define where examPaperNum={examPaperNum} union all select id,lengout,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26,deduction,fullScore,hasErrorSection,inspectionlevel,multiple,answer from subdefine where examPaperNum={examPaperNum} ", TypeEnum.StringObject, args);
        List<Map<String, Object>> paramList1 = new ArrayList<>();
        List<Map<String, Object>> paramList2 = new ArrayList<>();
        if ("0".equals(selected)) {
            obDataList.forEach(oneScoreIdObj -> {
                String answer_old = Convert.toStr(oneScoreIdObj.get("answer"), "");
                String items = Convert.toStr(oneScoreIdObj.get("items"), "");
                String answer_new = sortStr(answer_old + items);
                double questionScore = 0.0d;
                Optional<Map<String, Object>> res = defineMapList.stream().filter(m -> {
                    return oneScoreIdObj.get("questionNum").equals(m.get("id"));
                }).findAny();
                if (res.isPresent()) {
                    Map<String, Object> defineMap = res.get();
                    questionScore = Util.suitAllObjSingleJudge(answer_new, defineMap);
                }
                int[] regMinMax = CsUtils.getRegMinOrMaxResult(answer_new, Convert.toStr(oneScoreIdObj.get("regResult"), ""));
                int regMin = regMinMax[0];
                int regMax = regMinMax[1];
                BigDecimal regMinToMax_new = BigDecimal.valueOf(0L);
                if (regMax > 0) {
                    regMinToMax_new = Convert.toBigDecimal(Integer.valueOf(regMin)).divide(Convert.toBigDecimal(Integer.valueOf(regMax)), 2, RoundingMode.HALF_UP);
                }
                String[] ids = Convert.toStr(oneScoreIdObj.get("ids"), "").split(Const.STRING_SEPERATOR);
                for (String obId : ids) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("selected", "1");
                    hashMap.put("obId", obId);
                    paramList1.add(hashMap);
                }
                HashMap hashMap2 = new HashMap();
                hashMap2.put("questionScore", Double.valueOf(questionScore));
                hashMap2.put("answer_new", answer_new);
                String isException_old = Convert.toStr(oneScoreIdObj.get("isException"));
                String isException_new = "0".equals(isException_old) ? "1" : isException_old;
                hashMap2.put("isException_new", isException_new);
                hashMap2.put("regMinToMax_new", regMinToMax_new);
                hashMap2.put("scoreId", oneScoreIdObj.get("scoreId"));
                paramList2.add(hashMap2);
            });
        } else {
            obDataList.forEach(oneScoreIdObj2 -> {
                String answer_old = Convert.toStr(oneScoreIdObj2.get("answer"), "");
                String items = Convert.toStr(oneScoreIdObj2.get("items"), "");
                String answer_new = removeStr(answer_old, items);
                double questionScore = 0.0d;
                Optional<Map<String, Object>> res = defineMapList.stream().filter(m -> {
                    return oneScoreIdObj2.get("questionNum").equals(m.get("id"));
                }).findAny();
                if (res.isPresent()) {
                    Map<String, Object> defineMap = res.get();
                    questionScore = Util.suitAllObjSingleJudge(answer_new, defineMap);
                }
                int[] regMinMax = CsUtils.getRegMinOrMaxResult(answer_new, Convert.toStr(oneScoreIdObj2.get("regResult"), ""));
                int regMin = regMinMax[0];
                int regMax = regMinMax[1];
                BigDecimal regMinToMax_new = BigDecimal.valueOf(0L);
                if (regMax > 0) {
                    regMinToMax_new = Convert.toBigDecimal(Integer.valueOf(regMin)).divide(Convert.toBigDecimal(Integer.valueOf(regMax)), 2, RoundingMode.HALF_UP);
                }
                String[] ids = Convert.toStr(oneScoreIdObj2.get("ids"), "").split(Const.STRING_SEPERATOR);
                for (String obId : ids) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("selected", "0");
                    hashMap.put("obId", obId);
                    paramList1.add(hashMap);
                }
                HashMap hashMap2 = new HashMap();
                hashMap2.put("questionScore", Double.valueOf(questionScore));
                hashMap2.put("answer_new", answer_new);
                String isException_old = Convert.toStr(oneScoreIdObj2.get("isException"));
                String isException_new = "".equals(answer_new) ? "0" : isException_old;
                hashMap2.put("isException_new", isException_new);
                hashMap2.put("regMinToMax_new", regMinToMax_new);
                hashMap2.put("scoreId", oneScoreIdObj2.get("scoreId"));
                paramList2.add(hashMap2);
            });
        }
        if (CollUtil.isNotEmpty(paramList1)) {
            this.dao._batchExecute("update objitem set isModify=1,selected={selected} where id={obId}", paramList1);
        }
        if (CollUtil.isNotEmpty(paramList2)) {
            this.dao._batchExecute("update objectivescore set questionScore={questionScore},answer={answer_new},isException={isException_new},regMinToMax={regMinToMax_new} where id={scoreId}", paramList2);
        }
    }

    @Override // com.dmj.service.test.StestService
    public void updateSelectedOfItem(String examPaperNum, List<Map<String, Object>> itemDataList, String selected, String userId) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        Map<String, List<Map<String, Object>>> scoreIdItemsMap = (Map) itemDataList.stream().collect(Collectors.groupingBy(m -> {
            return Convert.toStr(m.get("scoreId"));
        }));
        List<Map<String, Object>> obDataList = new ArrayList<>();
        scoreIdItemsMap.forEach((scoreId, oneScoreIdItemsList) -> {
            Map<String, Object> obDataMap = getOneScoreIdData(scoreId);
            String items = (String) oneScoreIdItemsList.stream().map(m2 -> {
                return Convert.toStr(m2.get("item"), "");
            }).collect(Collectors.joining(""));
            obDataMap.put("items", items);
            String obIds = (String) oneScoreIdItemsList.stream().map(m3 -> {
                return Convert.toStr(m3.get("obId"), "");
            }).collect(Collectors.joining(Const.STRING_SEPERATOR));
            obDataMap.put("ids", obIds);
            obDataList.add(obDataMap);
        });
        List<Map<String, Object>> defineMapList = this.dao._queryMapList("select id,lengout,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26,deduction,fullScore,hasErrorSection,inspectionlevel,multiple,answer from define where examPaperNum={examPaperNum} union all select id,lengout,one1,one2,one3,one4,one5,one6,one7,one8,one9,one10,one11,one12,one13,one14,one15,one16,one17,one18,one19,one20,one21,one22,one23,one24,one25,one26,deduction,fullScore,hasErrorSection,inspectionlevel,multiple,answer from subdefine where examPaperNum={examPaperNum} ", TypeEnum.StringObject, args);
        List<Map<String, Object>> paramList1 = new ArrayList<>();
        List<Map<String, Object>> paramList2 = new ArrayList<>();
        if ("0".equals(selected)) {
            obDataList.forEach(oneScoreIdObj -> {
                String answer_old = Convert.toStr(oneScoreIdObj.get("answer"), "");
                String items = Convert.toStr(oneScoreIdObj.get("items"), "");
                String answer_new = sortStr(answer_old + items);
                double questionScore = 0.0d;
                Optional<Map<String, Object>> res = defineMapList.stream().filter(m2 -> {
                    return oneScoreIdObj.get("questionNum").equals(m2.get("id"));
                }).findAny();
                if (res.isPresent()) {
                    Map<String, Object> defineMap = res.get();
                    questionScore = Util.suitAllObjSingleJudge(answer_new, defineMap);
                }
                int[] regMinMax = CsUtils.getRegMinOrMaxResult(answer_new, Convert.toStr(oneScoreIdObj.get("regResult"), ""));
                int regMin = regMinMax[0];
                int regMax = regMinMax[1];
                BigDecimal regMinToMax_new = BigDecimal.valueOf(0L);
                if (regMax > 0) {
                    regMinToMax_new = Convert.toBigDecimal(Integer.valueOf(regMin)).divide(Convert.toBigDecimal(Integer.valueOf(regMax)), 2, RoundingMode.HALF_UP);
                }
                String[] ids = Convert.toStr(oneScoreIdObj.get("ids"), "").split(Const.STRING_SEPERATOR);
                for (String obId : ids) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("selected", "1");
                    hashMap.put("obId", obId);
                    paramList1.add(hashMap);
                }
                HashMap hashMap2 = new HashMap();
                hashMap2.put("questionScore", Double.valueOf(questionScore));
                hashMap2.put("answer_new", answer_new);
                String isException_old = Convert.toStr(oneScoreIdObj.get("isException"));
                String isException_new = "0".equals(isException_old) ? "1" : isException_old;
                hashMap2.put("isException_new", isException_new);
                hashMap2.put("regMinToMax_new", regMinToMax_new);
                hashMap2.put("scoreId", oneScoreIdObj.get("scoreId"));
                paramList2.add(hashMap2);
            });
        } else {
            obDataList.forEach(oneScoreIdObj2 -> {
                String answer_old = Convert.toStr(oneScoreIdObj2.get("answer"), "");
                String items = Convert.toStr(oneScoreIdObj2.get("items"), "");
                String answer_new = removeStr(answer_old, items);
                double questionScore = 0.0d;
                Optional<Map<String, Object>> res = defineMapList.stream().filter(m2 -> {
                    return oneScoreIdObj2.get("questionNum").equals(m2.get("id"));
                }).findAny();
                if (res.isPresent()) {
                    Map<String, Object> defineMap = res.get();
                    questionScore = Util.suitAllObjSingleJudge(answer_new, defineMap);
                }
                int[] regMinMax = CsUtils.getRegMinOrMaxResult(answer_new, Convert.toStr(oneScoreIdObj2.get("regResult"), ""));
                int regMin = regMinMax[0];
                int regMax = regMinMax[1];
                BigDecimal regMinToMax_new = BigDecimal.valueOf(0L);
                if (regMax > 0) {
                    regMinToMax_new = Convert.toBigDecimal(Integer.valueOf(regMin)).divide(Convert.toBigDecimal(Integer.valueOf(regMax)), 2, RoundingMode.HALF_UP);
                }
                String[] ids = Convert.toStr(oneScoreIdObj2.get("ids"), "").split(Const.STRING_SEPERATOR);
                for (String obId : ids) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("selected", "0");
                    hashMap.put("obId", obId);
                    paramList1.add(hashMap);
                }
                HashMap hashMap2 = new HashMap();
                hashMap2.put("questionScore", Double.valueOf(questionScore));
                hashMap2.put("answer_new", answer_new);
                String isException_old = Convert.toStr(oneScoreIdObj2.get("isException"));
                String isException_new = "".equals(answer_new) ? "0" : isException_old;
                hashMap2.put("isException_new", isException_new);
                hashMap2.put("regMinToMax_new", regMinToMax_new);
                hashMap2.put("scoreId", oneScoreIdObj2.get("scoreId"));
                paramList2.add(hashMap2);
            });
        }
        if (CollUtil.isNotEmpty(paramList1)) {
            this.dao._batchExecute("update objitem set isModify=1,selected={selected} where id={obId}", paramList1);
        }
        if (CollUtil.isNotEmpty(paramList2)) {
            this.dao._batchExecute("update objectivescore set questionScore={questionScore},answer={answer_new},isException={isException_new},regMinToMax={regMinToMax_new} where id={scoreId}", paramList2);
        }
    }

    public String sortStr(String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        List<String> sortList = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            String item = str.substring(i, i + 1);
            if (!sortList.contains(item)) {
                sortList.add(item);
            }
        }
        List<String> sortList2 = ListUtil.sortByPinyin(sortList);
        StringBuffer sortStr = new StringBuffer();
        Iterator<String> it = sortList2.iterator();
        while (it.hasNext()) {
            sortStr.append(it.next());
        }
        return sortStr.toString();
    }

    public String removeStr(String str, String items) {
        if (StrUtil.isEmpty(str) || StrUtil.isEmpty(items)) {
            return str;
        }
        List<String> sortList = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            String item = str.substring(i, i + 1);
            if (!items.contains(item)) {
                sortList.add(item);
            }
        }
        List<String> sortList2 = ListUtil.sortByPinyin(sortList);
        StringBuffer sortStr = new StringBuffer();
        Iterator<String> it = sortList2.iterator();
        while (it.hasNext()) {
            sortStr.append(it.next());
        }
        return sortStr.toString();
    }

    @Override // com.dmj.service.test.StestService
    public Integer getillegalCollateIsproofreading(String exampaperNum, String examplace) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examplace", examplace);
        return this.dao._queryInt("select count(1)count from illegal where examPaperNum={exampaperNum}  and testingCentreId={examplace} and (proofreadingStatus1=0 or proofreadingStatus1 is null) ", args);
    }

    @Override // com.dmj.service.test.StestService
    public void updateillegalCollateIsproofreading(String exampaperNum, String examplace) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examplace", examplace);
        this.dao._execute("update illegal set proofreadingStatus1=1 where exampaperNum={exampaperNum} and testingCentreId={examplace}", args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer getChooseQuestionIsproofreading(String exampaperNum, String examplace) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examplace", examplace);
        return this.dao._queryInt("select count(1)count from ( SELECT x.id FROM(SELECT id,chooseName FROM define WHERE examPaperNum = {exampaperNum}  AND chooseName='T') d  LEFT JOIN (SELECT id,chooseName FROM define WHERE examPaperNum = {exampaperNum} AND LENGTH(choosename)>2  ) x ON x.chooseName = CAST(d.id AS CHAR)  ) d  left join choosenamerecord cd  on cd.questionNum=d.id  left join regexaminee r on cd.regId = r.id  where exampaperNum={exampaperNum}  and testingCentreId={examplace} and  (cd.proofreadingStatus is null or cd.proofreadingStatus=0)", args);
    }

    @Override // com.dmj.service.test.StestService
    public void updateChooseQuestionIsproofreading(String exampaperNum, String examplace) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examplace", examplace);
        this.dao._execute("update choosenamerecord cho RIGHT JOIN  ( SELECT x.id pid FROM(SELECT id,chooseName FROM define WHERE examPaperNum = {exampaperNum}  AND chooseName='T') d  LEFT JOIN (SELECT id,chooseName FROM define WHERE examPaperNum = {exampaperNum} AND LENGTH(choosename)>2  ) x ON x.chooseName = CAST(d.id AS CHAR)  ) d on d.pid=cho.questionNum  left JOIN regexaminee reg on  cho.regId=reg.id  set cho.proofreadingStatus=1  where reg.exampaperNum={exampaperNum} and reg.testingCentreId={examplace} and (cho.proofreadingStatus is null or cho.proofreadingStatus=0)", args);
    }

    @Override // com.dmj.service.test.StestService
    public Integer getXuanZeTiIsproofreading(String exampaperNum, String optionType, String examplace, String isExceptiontype) {
        String sql = "";
        if (optionType.equals("0")) {
            if (isExceptiontype.equals("0")) {
                sql = "select  IFNULL(sum(objectiveCount),0)objectiveCount from (  select d.id,count(1)objectiveCount from objectivescore ob  left join ( select d.examPaperNum,d.id from  define d  where d.examPaperNum ={exampaperNum} and d.questionType=0  and d.multiple=0  UNION ALL select sd.examPaperNum,sd.id from  subdefine sd  where sd.examPaperNum ={exampaperNum} and sd.questionType=0  and sd.multiple=0  ) d on ob.examPaperNum=d.examPaperNum and ob.questionNum=d.id  where ob.examPaperNum ={exampaperNum}  and ob.testingCentreId ={examplace} and ob.isException =0 and  ob.ProofreadingStatus1 is null  ) c where c.id is not null ";
            } else if (isExceptiontype.equals("-2")) {
                sql = "select  IFNULL(sum(objectiveCount),0)objectiveCount from (  select d.id,count(1)objectiveCount from objectivescore ob  left join    (  select d.examPaperNum,d.id from  define d  where d.examPaperNum ={exampaperNum} and d.questionType=0  and d.multiple=0  UNION ALL select sd.examPaperNum,sd.id from  subdefine sd  where sd.examPaperNum ={exampaperNum} and sd.questionType=0  and sd.multiple=0   ) d on ob.examPaperNum=d.examPaperNum and ob.questionNum=d.id  where ob.examPaperNum ={exampaperNum}  and ob.testingCentreId ={examplace} and length( ob.answer )> 1   and  ob.ProofreadingStatus1 is null  ) c where c.id is not null ";
            }
        } else if (optionType.equals("1")) {
            if (isExceptiontype.equals("0")) {
                sql = "select  IFNULL(sum(objectiveCount),0)objectiveCount from (  select d.id,count(1)objectiveCount from objectivescore ob  left join  ( select d.examPaperNum,d.id from  define d  where d.examPaperNum ={exampaperNum} and d.questionType=0  and d.multiple=1  UNION ALL select sd.examPaperNum,sd.id from  subdefine sd  where sd.examPaperNum ={exampaperNum} and sd.questionType=0  and sd.multiple=1  ) d on ob.examPaperNum=d.examPaperNum and ob.questionNum=d.id  where ob.examPaperNum ={exampaperNum}  and ob.testingCentreId ={examplace} and ob.isException =0  and  ob.ProofreadingStatus1 is null  ) c where c.id is not null ";
            } else if (isExceptiontype.equals("-2")) {
                sql = "select  IFNULL(sum(objectiveCount),0)objectiveCount from (  select d.id,count(1)objectiveCount from objectivescore ob  left join  (  select d.examPaperNum,d.id from  define d  where d.examPaperNum ={exampaperNum} and d.questionType=0  and d.multiple=1  UNION ALL select sd.examPaperNum,sd.id from  subdefine sd  where sd.examPaperNum ={exampaperNum} and sd.questionType=0  and sd.multiple=1  ) d on ob.examPaperNum=d.examPaperNum and ob.questionNum=d.id  where ob.examPaperNum  ={exampaperNum}  and ob.testingCentreId ={examplace}  and LENGTH(ob.answer)>1 and  ob.ProofreadingStatus1 is null  ) c where c.id is not null ";
            }
        }
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examplace", examplace);
        return this.dao._queryInt(sql, args);
    }

    @Override // com.dmj.service.test.StestService
    public void updateXuanZeTiIsproofreadings(String exampaperNum, String optionType, String examplace, String isExceptiontype) {
        String sql = "";
        if (optionType.equals("0")) {
            if (isExceptiontype.equals("0")) {
                sql = "update objectivescore ob  left join define d on ob.examPaperNum=d.examPaperNum and ob.questionNum=d.id  set ProofreadingStatus1=1 where ob.examPaperNum ={exampaperNum}  and testingCentreId ={examplace} and isException =0 and questionType=0 and multiple=0 and ProofreadingStatus1 is null";
            } else if (isExceptiontype.equals("-2")) {
                sql = "update objectivescore ob left join define d on ob.examPaperNum=d.examPaperNum and ob.questionNum=d.id LEFT JOIN subdefine subd ON subd.id = ob.questionNum set ProofreadingStatus1=1 where ob.examPaperNum ={exampaperNum} and testingCentreId ={examplace} and  IFNULL( d.multiple, subd.multiple )= '0' and length( ob.answer )> 1  and d.questionType=0 and  ProofreadingStatus1 is null ";
            }
        } else if (optionType.equals("1")) {
            if (isExceptiontype.equals("0")) {
                sql = "update objectivescore ob  left join define d on ob.examPaperNum=d.examPaperNum and ob.questionNum=d.id  set ProofreadingStatus1=1  where ob.examPaperNum ={exampaperNum}  and testingCentreId ={examplace} and questionType=0   and multiple=1 and isException =0  and  ProofreadingStatus1 is null ";
            } else if (isExceptiontype.equals("-2")) {
                sql = "update objectivescore ob  left join define d on ob.examPaperNum=d.examPaperNum and ob.questionNum=d.id  set ProofreadingStatus1=1  where ob.examPaperNum  ={exampaperNum}  and testingCentreId ={examplace}  and questionType=0   and multiple=1  and LENGTH(ob.answer)>1 and  ProofreadingStatus1 is null ";
            }
        }
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("examplace", examplace);
        this.dao._execute(sql, args);
    }
}
