package com.dmj.daoimpl.tagManage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.Data;
import com.dmj.domain.Questionimage;
import com.dmj.domain.Score;
import com.dmj.domain.Tag;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.dmj.util.JsonType;
import com.google.common.collect.Lists;
import common.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/* loaded from: TagManageDAOImpl.class */
public class TagManageDAOImpl {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    Logger logger = Logger.getLogger(getClass());

    public List getImgList(String examPaperNum, String gradeNum, String schoolNum, String classNum, String questionNum) {
        Map args = new HashMap();
        args.put("questionNum", questionNum);
        args.put("questionType", "1");
        args.put("QUESTION_TYPE_SUBJECTIVE", "1");
        args.put("examPaperNum", examPaperNum);
        String sql = "SELECT sc.examPaperNum,sc.id,sc.studentId,stu.studentName,sch.schoolName,def.questionNum questionName,def.id questionNum,sc.questionScore,sc.schoolNum,sc.gradeNum,sc.classNum,sc.examinationRoomNum,def.fullScore FROM (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " AND id={questionNum} ";
        }
        String sql2 = sql + " AND questionType={questionType} UNION ALL SELECT DISTINCT examPaperNum,questionNum,{QUESTION_TYPE_SUBJECTIVE},fullScore,id FROM subdefine WHERE 1=1  AND examPaperNum={examPaperNum}  ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql2 = sql2 + " AND id={questionNum} ";
        }
        String sql3 = sql2 + ") def  LEFT JOIN (SELECT examPaperNum,studentId,questionNum,questionScore,schoolNum,gradeNum,classNum,examinationRoomNum,id FROM score WHERE 1=1 AND examPaperNum={examPaperNum}  ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql3 = sql3 + " AND questionNum={questionNum}  ";
        }
        if (null != schoolNum && !"-1".equals(schoolNum) && !schoolNum.equals("")) {
            sql3 = sql3 + "AND schoolNum={schoolNum}  ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != classNum && !"-1".equals(classNum) && !classNum.equals("")) {
            sql3 = sql3 + "AND classNum={classNum} ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != gradeNum && !"-1".equals(gradeNum) && !gradeNum.equals("")) {
            sql3 = sql3 + "AND gradeNum={gradeNum}  ";
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        }
        return this.dao._queryBeanList(sql3 + " ORDER BY classNum) sc ON def.examPaperNum=sc.examPaperNum AND def.id=sc.questionNum LEFT JOIN student stu ON sc.studentId=stu.id LEFT JOIN school sch ON sc.studentId=sch.id   WHERE sc.studentId IS NOT NULL", Questionimage.class, args);
    }

    public List getPaging(String examPaperNum, String gradeNum, String schoolNum, String classNum, String questionNum, String currenpage, int pageSize, int order_rank, String studentType) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("questionType", "1");
        args.put("QUESTION_TYPE_SUBJECTIVE", "1");
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("currenpage", currenpage);
        args.put("pageSize", Integer.valueOf(pageSize));
        String sql = "SELECT DISTINCT sc.examPaperNum,sc.id scoreId,sc.studentId,stu.studentName,sch.schoolName,def.id ext1,def.questionNum questionName,def.id questionNum,sc.questionScore,sc.schoolNum,sc.gradeNum,g.gradeName,cla.classNum,sc.classNum ext3,cla.className,sc.examinationRoomNum,def.fullScore FROM (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " AND id={questionNum}  ";
        }
        String sql2 = sql + "  AND questionType={questionType} UNION ALL  SELECT DISTINCT examPaperNum,questionNum,{QUESTION_TYPE_SUBJECTIVE},fullScore,id FROM subdefine WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql2 = sql2 + " AND id={questionNum} ";
        }
        String sql3 = sql2 + ")def  LEFT JOIN (SELECT examPaperNum,studentId,questionNum,questionScore,schoolNum,gradeNum,classNum,examinationRoomNum,id FROM score WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql3 = sql3 + " AND questionNum={questionNum} ";
        }
        if (null != schoolNum && !"allschool".equals(schoolNum) && !schoolNum.equals("")) {
            sql3 = sql3 + "AND schoolNum={schoolNum}  ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != classNum && !"-1".equals(classNum) && !classNum.equals("")) {
            sql3 = sql3 + "AND classNum={classNum}  ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        String sql4 = sql3 + "AND gradeNum={gradeNum}  AND continued='F'  ORDER BY classNum) sc ON def.examPaperNum=sc.examPaperNum AND def.id=sc.questionNum LEFT JOIN student stu ON sc.studentId=stu.id LEFT JOIN class cla ON sc.classNum = cla.id LEFT JOIN basegrade g ON sc.gradeNum = g.gradeNum LEFT JOIN school sch ON  sc.schoolNum=sch.id  WHERE cla.studentType={studentType}  AND sc.studentId IS NOT NULL AND stu.studentName IS NOT NULL ";
        if (order_rank == 1) {
            sql4 = sql4 + "ORDER BY sc.questionScore DESC ";
        } else if (order_rank == 2) {
            sql4 = sql4 + "ORDER BY sc.questionScore ASC ";
        } else if (order_rank == 3) {
            sql4 = sql4 + "ORDER BY sc.studentId ";
        }
        return this.dao._queryBeanList(sql4 + "LIMIT {currenpage},{pageSize}", Questionimage.class, args);
    }

    public List getPagingAllLevel(String examPaperNum, String gradeNum, String schoolNum, String classNum, String subjectNum, String questionNum, String currenpage, int pageSize, int order_rank, String studentType) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("questionType", "1");
        args.put("QUESTION_TYPE_SUBJECTIVE", "1");
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        args.put("currenpage", currenpage);
        args.put("pageSize", Integer.valueOf(pageSize));
        String sql = "SELECT s.exampaperNum,s.id scoreId,sub.subjectName,s.studentId,t.schoolNum,def.id ext1,def.questionNum questionName,stu.studentName,sch.schoolName,s.questionScore,t.gradeNum,g.gradeName,t.classNum ext3,s.classNum ,lc.className,s.examinationRoomNum,def.fullScore FROM levelstudent t LEFT JOIN score s ON s.studentId = t.sid  AND s.exampaperNum={examPaperNum}  LEFT JOIN `subject` sub ON t.subjectNum=sub.subjectNum LEFT JOIN student stu ON t.sid=stu.id LEFT JOIN school sch ON t.schoolNum=sch.id LEFT JOIN basegrade g ON t.gradeNum = g.gradeNum LEFT JOIN levelclass lc ON t.classNum=lc.id LEFT JOIN (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " AND id={questionNum}  ";
        }
        String sql2 = sql + "  AND questionType={questionType}  UNION ALL  SELECT DISTINCT examPaperNum,questionNum,{QUESTION_TYPE_SUBJECTIVE},fullScore,id FROM subdefine WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql2 = sql2 + " AND id={questionNum}  ";
        }
        String sql3 = sql2 + ")def ON s.questionNum=def.id WHERE t.subjectNum={subjectNum}  AND lc.studentType={studentType}";
        if (null != classNum && !"-1".equals(classNum) && !classNum.equals("") && !classNum.equals(Const.class_grade)) {
            sql3 = sql3 + " AND t.classNum={classNum}  ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql3 = sql3 + " AND s.questionNum ={questionNum}  ";
        }
        String sql4 = sql3 + " AND s.studentId IS NOT NULL ";
        if (order_rank == 1) {
            sql4 = sql4 + "ORDER BY s.questionScore DESC ";
        } else if (order_rank == 2) {
            sql4 = sql4 + "ORDER BY s.questionScore ASC ";
        } else if (order_rank == 3) {
            sql4 = sql4 + "ORDER BY s.studentId ";
        }
        return this.dao._queryBeanList(sql4 + "LIMIT {currenpage},{pageSize}", Questionimage.class, args);
    }

    public List getPagingAllLevel_no(String examPaperNum, String gradeNum, String schoolNum, String classNum, String subjectNum, String questionNum, int order_rank, String studentType) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("questionType", "1");
        args.put("QUESTION_TYPE_SUBJECTIVE", "1");
        args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        String sql = "SELECT s.exampaperNum,s.id scoreId,sub.subjectName,s.studentId,t.schoolNum,def.id ext1,def.questionNum questionName,stu.studentName,sch.schoolName,s.questionScore,t.gradeNum,g.gradeName,t.classNum ext3,s.classNum ,lc.className,s.examinationRoomNum,def.fullScore FROM levelstudent t LEFT JOIN score s ON s.studentId = t.sid  AND s.exampaperNum={examPaperNum} LEFT JOIN `subject` sub ON t.subjectNum=sub.subjectNum LEFT JOIN student stu ON t.sid=stu.id LEFT JOIN school sch ON t.schoolNum=sch.id LEFT JOIN basegrade g ON t.gradeNum = g.gradeNum LEFT JOIN levelclass lc ON t.classNum=lc.id LEFT JOIN (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " AND id={questionNum} ";
        }
        String sql2 = sql + "  AND questionType={questionType}  UNION ALL  SELECT DISTINCT examPaperNum,questionNum,{QUESTION_TYPE_SUBJECTIVE} ,fullScore,id FROM subdefine WHERE 1=1  AND examPaperNum={examPaperNum}  ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql2 = sql2 + " AND id={questionNum} ";
        }
        String sql3 = sql2 + ")def ON s.questionNum=def.id WHERE t.subjectNum={subjectNum}  AND lc.studentType={studentType} ";
        if (null != classNum && !"-1".equals(classNum) && !classNum.equals("") && !classNum.equals(Const.class_grade)) {
            sql3 = sql3 + " AND t.classNum={classNum}  ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql3 = sql3 + " AND s.questionNum = {questionNum} ";
        }
        String sql4 = sql3 + " AND s.studentId IS NOT NULL ";
        if (order_rank == 1) {
            sql4 = sql4 + "ORDER BY s.questionScore DESC ";
        } else if (order_rank == 2) {
            sql4 = sql4 + "ORDER BY s.questionScore ASC ";
        } else if (order_rank == 3) {
            sql4 = sql4 + "ORDER BY s.studentId ";
        }
        return this.dao._queryBeanList(sql4, Questionimage.class, args);
    }

    public List getPaging_no(String examPaperNum, String gradeNum, String schoolNum, String classNum, String questionNum, int order_rank, String studentType) {
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("questionType", "1");
        args.put("QUESTION_TYPE_SUBJECTIVE", "1");
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        String sql = "SELECT DISTINCT sc.examPaperNum,sc.id scoreId,sc.studentId,stu.studentName,sch.schoolName,def.id ext1,def.questionNum questionName,def.id questionNum,sc.questionScore,sc.schoolNum,sc.gradeNum,g.gradeName,cla.classNum,sc.classNum ext3,cla.className,sc.examinationRoomNum,def.fullScore FROM (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " AND id={questionNum} ";
        }
        String sql2 = sql + "  AND questionType={questionType} UNION ALL  SELECT DISTINCT examPaperNum,questionNum,{QUESTION_TYPE_SUBJECTIVE} ,fullScore,id FROM subdefine WHERE 1=1  AND examPaperNum={examPaperNum}  ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql2 = sql2 + " AND id={questionNum}  ";
        }
        String sql3 = sql2 + ")def  LEFT JOIN (SELECT examPaperNum,studentId,questionNum,questionScore,schoolNum,gradeNum,classNum,examinationRoomNum,id FROM score WHERE 1=1  AND examPaperNum={examPaperNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql3 = sql3 + " AND questionNum={questionNum}  ";
        }
        if (null != schoolNum && !"allschool".equals(schoolNum) && !schoolNum.equals("")) {
            sql3 = sql3 + "AND schoolNum={schoolNum}  ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != classNum && !Const.class_grade.equals(classNum) && !classNum.equals("")) {
            sql3 = sql3 + "AND classNum={classNum}  ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        String sql4 = sql3 + "AND gradeNum={gradeNum} AND continued='F'  ORDER BY classNum) sc ON def.examPaperNum=sc.examPaperNum AND def.id=sc.questionNum LEFT JOIN student stu ON sc.studentId=stu.id LEFT JOIN class cla ON sc.classNum = cla.id LEFT JOIN basegrade g ON sc.gradeNum = g.gradeNum LEFT JOIN school sch ON  sc.schoolNum=sch.id  WHERE cla.studentType={studentType} AND sc.studentId IS NOT NULL AND stu.studentName IS NOT NULL ";
        if (order_rank != 0) {
            if (order_rank == 1) {
                sql4 = sql4 + "ORDER BY sc.questionScore DESC ";
            } else if (order_rank == 2) {
                sql4 = sql4 + "ORDER BY sc.questionScore ASC ";
            } else if (order_rank == 3) {
                sql4 = sql4 + "ORDER BY sc.studentId ";
            }
        }
        return this.dao._queryBeanList(sql4, Questionimage.class, args);
    }

    public int getSel_all_img(String examPaperNum, String gradeNum, String schoolNum, String classNum, String questionNum, String subjectNum, String level, String studentType) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put("questionType", "1");
        args.put("QUESTION_TYPE_SUBJECTIVE", "1");
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put(Const.EXPORTREPORT_studentType, studentType);
        if (level.equals("F")) {
            String sql2 = "SELECT COUNT(sc.id) FROM (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1  AND examPaperNum={examPaperNum} ";
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql2 = sql2 + " AND id={questionNum}  ";
            }
            String sql3 = sql2 + "  AND questionType={questionType}  UNION ALL  SELECT DISTINCT examPaperNum,questionNum,{QUESTION_TYPE_SUBJECTIVE} ,fullScore,id FROM subdefine WHERE 1=1  AND examPaperNum={examPaperNum}  ";
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql3 = sql3 + " AND id={questionNum} ";
            }
            String sql4 = sql3 + ")def  LEFT JOIN (SELECT examPaperNum,studentId,questionNum,questionScore,schoolNum,gradeNum,classNum,examinationRoomNum,id FROM score WHERE 1=1  AND examPaperNum={examPaperNum} ";
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql4 = sql4 + " AND questionNum={questionNum} ";
            }
            if (null != schoolNum && !"allschool".equals(schoolNum) && !schoolNum.equals("")) {
                sql4 = sql4 + "AND schoolNum={schoolNum} ";
            }
            if (null != classNum && !"-1".equals(classNum) && !classNum.equals("") && !classNum.equals(Const.class_grade)) {
                sql4 = sql4 + "AND classNum={classNum} ";
            }
            sql = sql4 + "AND gradeNum={gradeNum} AND continued='F'  ORDER BY classNum) sc ON def.examPaperNum=sc.examPaperNum AND def.id=sc.questionNum LEFT JOIN student stu ON sc.studentId=stu.id LEFT JOIN class cla ON sc.classNum = cla.id LEFT JOIN basegrade g ON sc.gradeNum = g.gradeNum LEFT JOIN school sch ON  sc.schoolNum=sch.id  WHERE cla.studentType={studentType}  AND sc.studentId IS NOT NULL AND stu.studentName IS NOT NULL ";
        } else {
            String sql5 = "SELECT  COUNT(s.id) FROM levelstudent t LEFT JOIN score s ON s.studentId = t.sid  AND s.exampaperNum={examPaperNum} LEFT JOIN `subject` sub ON t.subjectNum=sub.subjectNum LEFT JOIN student stu ON t.sid=stu.id LEFT JOIN school sch ON t.schoolNum=sch.id LEFT JOIN basegrade g ON t.gradeNum=g.gradeNum LEFT JOIN levelclass lc ON t.classNum=lc.id LEFT JOIN (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1  AND examPaperNum={examPaperNum} ";
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql5 = sql5 + " AND id={questionNum}  ";
            }
            String sql6 = sql5 + "  AND questionType={questionType}  UNION ALL  SELECT DISTINCT examPaperNum,questionNum, 1,fullScore,id FROM subdefine WHERE 1=1  AND examPaperNum={examPaperNum}  ";
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql6 = sql6 + " AND id={questionNum} ";
            }
            sql = sql6 + ")def ON s.questionNum=def.id WHERE  t.subjectNum={subjectNum}  AND lc.studentType={studentType} ";
            args.put(Const.EXPORTREPORT_subjectNum, subjectNum);
            if (null != classNum && !"-1".equals(classNum) && !classNum.equals("") && !classNum.equals(Const.class_grade)) {
                sql = sql + " AND t.classNum={classNum}  ";
            }
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql = sql + " AND s.questionNum={questionNum}  ";
            }
        }
        return this.dao._queryInt(sql, args).intValue();
    }

    public List getBiaoji(String examPaperNum, String questionNum, String schoolNum, String classNum, String grade, int order_rank, String isLevel, String studentType) {
        String sql;
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put("questionType", "1");
        if (isLevel.equals("F")) {
            String sql2 = "SELECT DISTINCT rs1.orderNum,rs1.studentId,rs1.examPaperNum,rs1.questionNum ext1,d.questionNum questionName,rs1.schoolNum,cla.classNum,cla.id ext3,cla.className,rs1.gradeNum,g.gradeName,stu.studentName ,sc.questionScore,sc.examinationRoomNum, d.fullScore,s.schoolName,sc.id scoreId FROM (SELECT DISTINCT orderNum,studentId,examPaperNum,questionNum,schoolNum,classNum,gradeNum FROM tag WHERE 1=1 ";
            if (null != examPaperNum && !"-1".equals(examPaperNum) && !examPaperNum.equals("") && !examPaperNum.equals("null")) {
                sql2 = sql2 + " AND examPaperNum={examPaperNum} ";
            }
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql2 = sql2 + " AND questionNum={questionNum}  ";
            }
            if (null != schoolNum && !"allschool".equals(schoolNum) && !schoolNum.equals("")) {
                sql2 = sql2 + " AND schoolNum={schoolNum} ";
            }
            if (null != classNum && !Const.class_grade.equals(classNum) && !classNum.equals("")) {
                sql2 = sql2 + " AND classNum={classNum}  ";
            }
            String sql3 = sql2 + " ORDER BY orderNum ASC ) rs1 LEFT JOIN student stu ON rs1.studentId=stu.id LEFT JOIN class cla ON rs1.classNum = cla.id LEFT JOIN basegrade g ON rs1.gradeNum = g.gradeNum LEFT JOIN (SELECT examPaperNum,studentId,questionNum,questionScore,schoolNum,gradeNum,classNum,examinationRoomNum,id FROM score WHERE 1=1  AND examPaperNum={examPaperNum} ";
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql3 = sql3 + " AND questionNum={questionNum} ";
            }
            if (null != schoolNum && !"allschool".equals(schoolNum) && !schoolNum.equals("")) {
                sql3 = sql3 + "AND schoolNum={schoolNum} ";
            }
            if (null != classNum && !Const.class_grade.equals(classNum) && !classNum.equals("")) {
                sql3 = sql3 + "AND classNum={classNum}  ";
            }
            String sql4 = sql3 + "AND gradeNum={grade} AND continued='F'  ORDER BY classNum) sc ON rs1.questionNum=sc.questionNum and rs1.examPaperNum=sc.examPaperNum and rs1.schoolNum=sc.schoolNum and rs1.classNum=sc.classNum and rs1.gradeNum=sc.gradeNum and rs1.studentId=sc.studentId LEFT JOIN (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1 ";
            args.put("grade", grade);
            if (null != examPaperNum && !"-1".equals(examPaperNum) && !examPaperNum.equals("")) {
                sql4 = sql4 + " AND examPaperNum={examPaperNum}  ";
            }
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql4 = sql4 + " AND id={questionNum}  ";
            }
            String sql5 = sql4 + "AND questionType={questionType}  UNION ALL SELECT DISTINCT examPaperNum,questionNum,1,fullScore,id FROM subdefine WHERE 1=1  ";
            if (null != examPaperNum && !"-1".equals(examPaperNum) && !examPaperNum.equals("")) {
                sql5 = sql5 + " AND examPaperNum={examPaperNum} ";
            }
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql5 = sql5 + " AND id={questionNum}  ";
            }
            sql = sql5 + " )d  ON d.id=rs1.questionNum LEFT JOIN school s ON s.id=sc.schoolNum WHERE cla.studentType={studentType}  ORDER BY rs1.orderNum asc;";
            args.put(Const.EXPORTREPORT_studentType, studentType);
        } else {
            String sql6 = "SELECT DISTINCT rs1.orderNum,rs1.studentId,rs1.examPaperNum,rs1.questionNum ext1,def.questionNum questionName,rs1.schoolNum,cla.id classNum,cla.id ext3,cla.className,rs1.gradeNum,g.gradeName,s.studentName,sc.questionScore,sc.examinationRoomNum, def.fullScore,sch.schoolName,sc.id scoreId FROM (SELECT DISTINCT orderNum,studentId,examPaperNum,questionNum,schoolNum,classNum,gradeNum FROM tag WHERE 1=1 ";
            if (null != examPaperNum && !"-1".equals(examPaperNum) && !examPaperNum.equals("")) {
                sql6 = sql6 + " AND examPaperNum={examPaperNum}  ";
            }
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql6 = sql6 + " AND questionNum={questionNum}  ";
            }
            if (null != schoolNum && !"allschool".equals(schoolNum) && !schoolNum.equals("")) {
                sql6 = sql6 + " AND schoolNum={schoolNum}  ";
            }
            if (null != classNum && !Const.class_grade.equals(classNum) && !classNum.equals("")) {
                sql6 = sql6 + " AND classNum={classNum} ";
            }
            String sql7 = (sql6 + " ORDER BY orderNum ASC ) rs1  LEFT JOIN score sc ON  sc.studentId = rs1.studentId AND sc.examPaperNum={examPaperNum}  AND continued='F' AND rs1.questionNum=sc.questionNum ") + "LEFT JOIN (SELECT DISTINCT examPaperNum,questionNum,questionType,fullScore,id FROM define WHERE 1=1 ";
            if (null != examPaperNum && !"-1".equals(examPaperNum) && !examPaperNum.equals("")) {
                sql7 = sql7 + " AND examPaperNum={examPaperNum}  ";
            }
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql7 = sql7 + " AND id={questionNum} ";
            }
            String sql8 = sql7 + " AND questionType={questionType} UNION ALL  SELECT DISTINCT examPaperNum,questionNum,{QUESTION_TYPE_SUBJECTIVE} ,fullScore,id FROM subdefine WHERE 1=1";
            args.put("QUESTION_TYPE_SUBJECTIVE", "1");
            if (null != examPaperNum && !"-1".equals(examPaperNum) && !examPaperNum.equals("")) {
                sql8 = sql8 + " AND examPaperNum={examPaperNum}  ";
            }
            if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
                sql8 = sql8 + " AND id={questionNum}  ";
            }
            sql = sql8 + " )def  ON def.id=rs1.questionNum LEFT JOIN levelstudent stu ON rs1.studentId=stu.sid LEFT JOIN student s ON stu.sid=s.id LEFT JOIN levelclass cla ON  rs1.classNum=cla.id LEFT JOIN basegrade g ON rs1.gradeNum = g.gradeNum LEFT JOIN school sch ON sch.id=sc.schoolNum WHERE s.studentName IS NOT NULL ORDER BY rs1.orderNum asc";
        }
        return this.dao._queryBeanList(sql, Questionimage.class, args);
    }

    public List getBJName(String examPaperNum, String questionNum, String schoolNum, String classNum, String studentId) {
        Map args = new HashMap();
        args.put("type", Const.Data_Tag_type);
        String sql = "select DISTINCT t.tag,d.name from (select examPaperNum,questionNum,schoolNum,classNum,tag from tag ";
        if (null != examPaperNum && !"-1".equals(examPaperNum) && !examPaperNum.equals("")) {
            sql = sql + " where examPaperNum={examPaperNum} ";
            args.put("examPaperNum", examPaperNum);
        }
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " and questionNum={questionNum} ";
            args.put("questionNum", questionNum);
        }
        if (null != schoolNum && !"allschool".equals(schoolNum) && !schoolNum.equals("")) {
            sql = sql + " and schoolNum={schoolNum} ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != classNum && !Const.class_grade.equals(classNum) && !classNum.equals("")) {
            sql = sql + " and classNum={classNum} ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != studentId && !"-1".equals(studentId) && !studentId.equals("")) {
            sql = sql + " and studentId={studentId} ";
            args.put(Const.EXPORTREPORT_studentId, studentId);
        }
        return this.dao._queryBeanList(sql + " )t LEFT JOIN `data` d ON d.type={type} and d.value=t.tag ", Questionimage.class, args);
    }

    public List getTag(String type) {
        Map args = new HashMap();
        args.put("type", Const.Data_Tag_type);
        return this.dao._queryBeanList("SELECT isDefault,type,name,value FROM data WHERE type={type} order by value*1 asc", Data.class, args);
    }

    public void updateTag(String tag, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum) {
        Map args = new HashMap();
        args.put("tag", tag);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        this.dao._execute("update tag set tag={tag} where questionNum={questionNum}  and studentId={studentId}  and schoolNum={schoolNum} and classNum={classNum}  and gradeNum={gradeNum} ", args);
    }

    public Integer save(List t) {
        Integer returnCode = null;
        for (Object obj : t) {
            Tag tag = (Tag) obj;
            returnCode = Integer.valueOf(this.dao.save(tag));
        }
        return returnCode;
    }

    public Integer deleteTag(Integer exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        String sql = "delete from tag where exampaperNum={exampaperNum}  AND";
        if (null != questionNum && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " questionNum={questionNum} and";
            args.put("questionNum", questionNum);
        }
        if (null != studentId && !studentId.equals("")) {
            sql = sql + "  studentId={studentId} and ";
            args.put(Const.EXPORTREPORT_studentId, studentId);
        }
        if (null != schoolNum && !schoolNum.equals("")) {
            sql = sql + "schoolNum={schoolNum}  and ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != classNum && !classNum.equals("")) {
            sql = sql + " classNum={classNum} and ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != gradeNum && !gradeNum.equals("")) {
            sql = sql + " gradeNum={gradeNum} ";
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        }
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    public Integer delTag(String questionNum, String studentId, String schoolNum, String classNum, String gradeNum, String tag) {
        Map args = new HashMap();
        String sql = "delete from tag where ";
        if (null != questionNum && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " questionNum={questionNum}  and ";
            args.put("questionNum", questionNum);
        }
        if (null != studentId && !studentId.equals("")) {
            sql = sql + "  studentId={studentId} and ";
            args.put(Const.EXPORTREPORT_studentId, studentId);
        }
        if (null != schoolNum && !schoolNum.equals("")) {
            sql = sql + " schoolNum={schoolNum}  and ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != classNum && !classNum.equals("")) {
            sql = sql + " classNum={classNum}  and ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != gradeNum && !gradeNum.equals("")) {
            sql = sql + " gradeNum={gradeNum} and ";
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        }
        if (null != tag && !tag.equals("")) {
            sql = sql + " tag={tag} ";
            args.put("tag", tag);
        }
        return Integer.valueOf(this.dao._execute(sql, args));
    }

    public Integer getCountNumTag(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum, String tag) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("tag", tag);
        Object obj = this.dao._queryObject("select COUNT(questionNum) from tag where exampaperNum={exampaperNum} and questionNum={questionNum} and studentId={studentId} and schoolNum={schoolNum} and classNum={classNum} and gradeNum={gradeNum} and tag={tag} ", args);
        if (obj != null) {
            return Integer.valueOf(Integer.parseInt(obj.toString()));
        }
        return 0;
    }

    public Integer getCountNum(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        Object obj = this.dao._queryObject("select COUNT(questionNum) from tag where exampaperNum={exampaperNum} and questionNum={questionNum} and studentId={studentId}  and schoolNum={schoolNum}  and classNum={classNum} and gradeNum={gradeNum} ", args);
        if (obj != null) {
            return Integer.valueOf(Integer.parseInt(obj.toString()));
        }
        return 0;
    }

    public String getOrderNum(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionNum", questionNum);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String sql = ((("select DISTINCT orderNum from tag where  examPaperNum={exampaperNum}  and ") + "  questionNum={questionNum} and ") + "  studentId={studentId} and ") + "  schoolNum={schoolNum}  and ";
        if (null != classNum && !"-1".equals(classNum) && "" != classNum && "null" != classNum) {
            sql = sql + "  classNum={classNum} and ";
        }
        return this.dao._queryStr(sql + "  gradeNum={gradeNum} ", args);
    }

    public List getMaximum(String exampaperNum, String schoolNum, String gradeNum, String classNum, String questionNum) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        String sql = "select DISTINCT orderNum from tag WHERE exampaperNum={exampaperNum}  AND schoolNum={schoolNum}  AND gradeNum={gradeNum} ";
        if (null != classNum && !"-1".equals(classNum) && !classNum.equals("") && !classNum.equals("null") && !classNum.equals(Const.class_grade)) {
            sql = sql + " AND classNum={classNum} ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " AND questionNum={questionNum} ";
            args.put("questionNum", questionNum);
        }
        return this.dao._queryBeanList(sql + " ORDER BY orderNum DESC", Tag.class, args);
    }

    public void upTag(String pre_order, String pre_questionNum, String pre_studentId, String pre_classNum, String this_order, String this_questionNum, String this_studentId, String this_classNum, String examPaperNum, String schoolNum, String gradeNum) {
        Map args = new HashMap();
        args.put("this_order", this_order);
        args.put("examPaperNum", examPaperNum);
        args.put("pre_studentId", pre_studentId);
        args.put("pre_questionNum", pre_questionNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put("pre_classNum", pre_classNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("pre_order", pre_order);
        args.put("this_studentId", this_studentId);
        args.put("this_questionNum", this_questionNum);
        args.put("this_classNum", this_classNum);
        this.dao._execute("update tag set orderNum={this_order}  where examPaperNum={examPaperNum}  and studentId={pre_studentId} and questionNum={pre_questionNum}  and schoolNum={schoolNum}  and classNum={pre_classNum} and gradeNum={gradeNum} ", args);
        this.dao._execute("update tag set orderNum={pre_order} where examPaperNum={examPaperNum} and studentId={this_studentId} and questionNum={this_questionNum} and schoolNum={schoolNum} and classNum={this_classNum} and gradeNum={gradeNum}  ", args);
    }

    public List tagType(String exampaperNum, String schoolNum, String gradeNum, String classNum, String questionNum, String type_tag, String studentId) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        args.put("tag", type_tag);
        args.put(Const.EXPORTREPORT_studentId, studentId);
        args.put(Const.EXPORTREPORT_classNum, classNum);
        String sql = "select tag from tag where exampaperNum={exampaperNum}  and schoolNum={schoolNum} and gradeNum={gradeNum}  and classNum={classNum} ";
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("")) {
            sql = sql + " and questionNum={questionNum}  ";
            args.put("questionNum", questionNum);
        }
        return this.dao._queryBeanList(sql + "and tag={tag}  and studentId={studentId} ", Tag.class, args);
    }

    public List orderNumNull(String exampaperNum, String questionNum, String studentId, String schoolNum, String classNum, String gradeNum) {
        Map args = new HashMap();
        String sql = "select studentId,questionNum,classNum from tag where 1=1 ";
        if (null != exampaperNum && !"-1".equals(exampaperNum) && !exampaperNum.equals("")) {
            sql = sql + " and  examPaperNum={exampaperNum}   ";
            args.put("exampaperNum", exampaperNum);
        }
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("")) {
            sql = sql + " and questionNum={questionNum} ";
            args.put("questionNum", questionNum);
        }
        if (null != schoolNum && !"-1".equals(schoolNum) && !schoolNum.equals("")) {
            sql = sql + " and schoolNum={schoolNum} ";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != classNum && !"-1".equals(classNum) && !classNum.equals("") && !classNum.equals(Const.class_grade)) {
            sql = sql + "  and classNum={classNum}  ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != gradeNum && !"-1".equals(gradeNum) && !gradeNum.equals("")) {
            sql = sql + " and gradeNum={gradeNum}  ";
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        }
        return this.dao._queryBeanList(sql + " and orderNum is NULL", Tag.class, args);
    }

    public void updateOrderNum(int orderNum, String examPaperNum, String schoolNum, String gradeNum, String classNum, String studentId, String questionNum) {
        Map args = new HashMap();
        args.put("orderNum", Integer.valueOf(orderNum));
        String sql = "update tag set orderNum={orderNum}  where ";
        if (null != examPaperNum && !"-1".equals(examPaperNum) && !examPaperNum.equals("")) {
            sql = sql + "  examPaperNum={examPaperNum} ";
            args.put("examPaperNum", examPaperNum);
        }
        if (null != schoolNum && !"-1".equals(schoolNum) && !schoolNum.equals("")) {
            sql = sql + "  and schoolNum={schoolNum}";
            args.put(Const.EXPORTREPORT_schoolNum, schoolNum);
        }
        if (null != gradeNum && !"-1".equals(gradeNum) && !gradeNum.equals("")) {
            sql = sql + " and gradeNum={gradeNum}  ";
            args.put(Const.EXPORTREPORT_gradeNum, gradeNum);
        }
        if (null != classNum && !"-1".equals(classNum) && !classNum.equals("")) {
            sql = sql + "  and classNum={classNum} ";
            args.put(Const.EXPORTREPORT_classNum, classNum);
        }
        if (null != questionNum && !"-1".equals(questionNum) && !questionNum.equals("") && !questionNum.equals("null")) {
            sql = sql + " and questionNum={questionNum}  ";
            args.put("questionNum", questionNum);
        }
        if (null != studentId && !"-1".equals(studentId) && !studentId.equals("")) {
            sql = sql + "  and studentId={studentId} ";
            args.put(Const.EXPORTREPORT_studentId, studentId);
        }
        this.dao._execute(sql, args);
    }

    public String getDefineInfo_cross_page(String exampaperNum, String questionId) {
        Map args = new HashMap();
        args.put("exampaperNum", exampaperNum);
        args.put("questionId", questionId);
        return this.dao._queryStr("SELECT cross_page FROM define WHERE examPaperNum={exampaperNum}  AND id={questionId} UNION SELECT cross_page FROM subdefine WHERE examPaperNum={exampaperNum}  AND id={questionId} ", args);
    }

    public void saveTag(JSONObject jsonObject, String shiPinTF) {
        String taskListJson = jsonObject.getString("tasklist");
        String markListJson = jsonObject.getString("taskjson");
        List<Map<String, String>> tagList = searchTag(taskListJson, markListJson);
        for (Map<String, String> tagMap : tagList) {
            List<String> marks = Lists.newArrayList(StrUtil.split(tagMap.get(Const.SYSTEM_TYPE), ',', true, false));
            boolean review = "1".equals(tagMap.get("checkObj_value"));
            if (review || marks.size() <= 0) {
                Map args = new HashMap();
                args.put("scoreId", tagMap.get("scoreId"));
                args.put("insertUser", tagMap.get("insertUser"));
                Object tagId = this.dao._queryObject("select id from tag where scoreId={scoreId} and  insertUser={insertUser}", args);
                if (null != tagId) {
                    this.dao._execute("DELETE from tag where scoreId={scoreId} and  insertUser={insertUser}", args);
                }
            }
            if (marks.size() > 0) {
                Map args2 = new HashMap();
                args2.put("id", tagMap.get("scoreId"));
                Score score = (Score) this.dao._queryBean("SELECT schoolNum,gradeNum,classNum,studentId from score where id = {id}", Score.class, args2);
                this.dao.batchSave((List) marks.stream().map(mark -> {
                    Tag tag = new Tag();
                    tag.setScoreId((String) tagMap.get("scoreId"));
                    tag.setExampaperNum(Integer.valueOf((String) tagMap.get("exampaperNum")));
                    tag.setQuestionNum((String) tagMap.get("questionNum"));
                    tag.setSchoolNum(score.getSchoolNum());
                    tag.setClassNum(score.getClassNum());
                    tag.setGradeNum(score.getGradeNum());
                    tag.setTag(mark);
                    tag.setOrderNum(0);
                    tag.setTestMark(shiPinTF);
                    tag.setIsDelete("F");
                    tag.setInsertUser((String) tagMap.get("insertUser"));
                    tag.setInsertDate(DateUtil.getCurrentTime());
                    tag.setStudentId(score.getStudentId());
                    return tag;
                }).collect(Collectors.toList()));
            }
        }
    }

    public List<Map<String, String>> searchTag(String taskListJson, String json) {
        List<Map<String, String>> taskList = (List) JSON.parseObject(taskListJson, JsonType.List2Map1_StringString, new Feature[0]);
        List<Map<String, String>> markList = (List) JSON.parseObject(json, JsonType.List2Map1_StringString, new Feature[0]);
        List<List<Map<String, String>>> groupList = (List) ((LinkedHashMap) markList.stream().collect(Collectors.groupingBy(m -> {
            return (String) m.get("group");
        }, LinkedHashMap::new, Collectors.toList()))).values().stream().collect(Collectors.toList());
        for (int i = 0; i < taskList.size(); i++) {
            Map<String, String> task = taskList.get(i);
            Set<Integer> oneTaskMarkList = new HashSet<>();
            if (groupList.size() > i) {
                List<Map<String, String>> oneGroup = groupList.get(i);
                for (Map<String, String> mark : oneGroup) {
                    String svg = mark.get("svg");
                    if (!StrUtil.isEmpty(svg)) {
                        if (svg.indexOf("youxiudaan.svg") > 0) {
                            oneTaskMarkList.add(0);
                        } else if (svg.indexOf("dudaojianjie.svg") > 0) {
                            oneTaskMarkList.add(1);
                        } else if (svg.indexOf("dianxingcuowu.svg") > 0) {
                            oneTaskMarkList.add(2);
                        } else if (svg.indexOf("pubiancuowu.svg") > 0) {
                            oneTaskMarkList.add(3);
                        } else if (svg.indexOf("yihuncuowu.svg") > 0) {
                            oneTaskMarkList.add(4);
                        }
                    }
                }
            }
            if (CollUtil.isNotEmpty(oneTaskMarkList)) {
                task.put(Const.SYSTEM_TYPE, CollUtil.join(oneTaskMarkList, Const.STRING_SEPERATOR));
            } else {
                task.put(Const.SYSTEM_TYPE, null);
            }
        }
        return taskList;
    }
}
