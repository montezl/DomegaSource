package com.dmj.serviceimpl.astrict;

import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.examManagement.ExamDAOImpl;
import com.dmj.domain.Astrict;
import com.dmj.service.astrict.AstrictService;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.zht.db.StreamMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: AstrictServiceImpl.class */
public class AstrictServiceImpl implements AstrictService {
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    ExamDAOImpl examDao = new ExamDAOImpl();

    @Override // com.dmj.service.astrict.AstrictService
    public boolean isAstrictByPartNum(String partType) {
        Map args = new HashMap();
        args.put("partType", partType);
        Object obj = this.dao2._queryObject("select id from astrict where partType = {partType} and (now()<startTime or now() > endTime) limit 1", args);
        if (null == obj || "".equals(obj)) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.astrict.AstrictService
    public Boolean isAstrictExam(String examNum, String partType) {
        Map args = new HashMap();
        args.put("partType", partType);
        args.put(Const.EXPORTREPORT_examNum, examNum);
        Object obj = this.dao2._queryObject("select id from astrict where partType = {partType} and (now()<startTime or endTime < now()) and examNum = {examNum} ", args);
        if (null == obj || "".equals(obj)) {
            return false;
        }
        return true;
    }

    @Override // com.dmj.service.astrict.AstrictService
    public boolean isCoreManager(String userId) {
        if ("-2".equals(userId) || "-1".equals(userId)) {
            return true;
        }
        Map args = new HashMap();
        args.put("userId", userId);
        StringBuffer sb = new StringBuffer();
        sb.append("select schoolNum from(");
        sb.append(" select up.schoolNum from userposition up where up.type = '0' and up.userNum = {userId} ");
        sb.append(" union ");
        sb.append(" select smg.schoolNum from schauthormanage smg left join userposition up on up.userNum = smg.userId where up.type = '0' and up.userNum ={userId} ");
        sb.append(") as t group by t.schoolNum");
        List list = this.dao2._queryColList(sb.toString(), args);
        if (null == list || "".equals(list) || list.size() == 0) {
            return false;
        }
        List listSchool = this.dao2._queryColList("select id from school where isDelete='F'", args);
        boolean flag = true;
        int i = 0;
        while (true) {
            if (i >= listSchool.size()) {
                break;
            }
            boolean tempflag = false;
            int j = 0;
            while (true) {
                if (j >= list.size()) {
                    break;
                }
                if (!listSchool.get(i).toString().equals(list.get(j).toString())) {
                    j++;
                } else {
                    tempflag = true;
                    break;
                }
            }
            if (tempflag) {
                i++;
            } else {
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<Astrict> getDataByPartType(String partType) {
        String sql = "";
        if ("1".equals(partType) || "2".equals(partType)) {
            sql = "select a.id,g.gradeNum,g.gradeName,a.partType,a.startTime,a.endTime from (select gradeNum,gradeName from grade where isDelete = 'F' group by gradeNum) as g left join (select id,gradeNum,partType,startTime,endTime from astrict where partType = {partType}  ) as a on g.gradeNum = a.gradeNum";
        } else if ("3".equals(partType)) {
            sql = "select a.id,e.examNum,e.examName,a.partType,a.startTime,a.endTime from (select examNum,examName,examDate from exam where isDelete = 'F' and status != 9) as e left join (select * from astrict where partType = {partType}  ) as a on e.examNum = a.examNum order by e.examDate desc ";
        } else if ("4".equals(partType)) {
            sql = "select id,partType,startTime,endTime from astrict where partType = {partType}  ";
        } else if ("5".equals(partType)) {
            sql = "select a.id,e.examNum,e.examName,g.gradeNum,g.gradeName,sb.subjectNum,sb.subjectName, a.partType,a.startTime from   (select examNum,examName,examDate from exam where isDelete = 'F' and status != 9) as e LEFT JOIN (SELECT examNum,gradeNum,subjectNum from exampaper) as ep ON e.examNum=ep.examNum LEFT JOIN  `subject` sb ON ep.subjectNum=sb.subjectNum LEFT JOIN  grade g ON ep.gradeNum=g.gradeNum left join  (select * from astrict where partType = 6  ) as a on e.examNum = a.examNum AND ep.gradeNum=a.gradeNum and ep.subjectNum=a.subjectNum  GROUP BY ep.examNum,ep.gradeNum,ep.subjectNum order by e.examDate desc,ep.gradeNum,ep.subjectNum ";
        }
        Map args = new HashMap();
        args.put("partType", partType);
        return this.dao2._queryBeanList(sql, Astrict.class, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<Astrict> getDataByPartType1(String partType) {
        String sql = "";
        if ("1".equals(partType) || "2".equals(partType)) {
            sql = "select a.id,g.gradeNum,g.gradeName,a.partType,a.startTime,a.endTime from (select gradeNum,gradeName from grade where isDelete = 'F' group by gradeNum) as g inner join (select id,gradeNum,partType,startTime,endTime from astrict where partType = {partType} and (endTime < now() or startTime>now())) as a on g.gradeNum = a.gradeNum";
        } else if ("3".equals(partType)) {
            sql = "select a.id,e.examNum,e.examName,a.partType,a.startTime,a.endTime from (select examNum,examName from exam where isDelete = 'F' and status != 9 ) as e inner join (select * from astrict where partType = {partType} and (endTime < now() or startTime>now())) as a on e.examNum = a.examNum ";
        } else if ("4".equals(partType)) {
            sql = "select id,partType,startTime,endTime from astrict where partType = {partType} and (endTime < now() or startTime>now())";
        }
        Map args = new HashMap();
        args.put("partType", partType);
        return this.dao2._queryBeanList(sql, Astrict.class, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<String> getAstrictGradeDataNowByPartType(String partType) {
        Map args = StreamMap.create().put("partType", (Object) partType);
        return this.dao2._queryColList("select gradeNum from astrict where partType = {partType} and (startTime > now() or endTime < now())", String.class, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<String> getAstrictExamDataNowByPartType(String partType) {
        Map args = new HashMap();
        args.put("partType", partType);
        return this.dao2._queryColList("select examNum from astrict where partType = {partType} and (startTime > now() or endTime < now())", String.class, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<Map<String, Object>> getAstrictLoginDataNowByPartType(String partType) {
        return this.dao2.queryMapList("select startTime,endTime from astrict where partType = 4 and startTime < now() and endTime > now()");
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<Map<String, Object>> getAstrictLoginDataNowByPartType2(String partType, String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        List<Map<String, Object>> list = this.dao2._queryMapList("SELECT s.schoolNum,sg.islogin,sg.starttime,sg.endtime,sg.shuoming from userparent up LEFT JOIN student s LEFT JOIN schoollogin sg ON sg.schoolNum=s.schoolNum ON up.userid=s.id WHERE up.username={userNum} and sg.islogin='1' and sg.starttime < now() and sg.endtime > now() ", null, args);
        if (null == list || list.size() == 0) {
            list = this.dao2._queryMapList("SELECT s.schoolNum,sg.islogin,sg.starttime,sg.endtime,sg.shuoming from userparent up LEFT JOIN student s LEFT JOIN schoollogin sg ON sg.schoolNum=s.schoolNum ON up.userid=s.id WHERE up.username={userNum} and sg.islogin='1' and sg.starttime='' and sg.endtime ='' ", null, args);
            if (null == list || list.size() == 0) {
                return null;
            }
        }
        return list;
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<Map<String, Object>> getAstrictLoginDataNowByPartType3(String partType, String userNum) {
        Map args = new HashMap();
        args.put("userNum", userNum);
        List<Map<String, Object>> list = this.dao2._queryMapList("SELECT s.schoolNum,sg.islogin,sg.shuoming from student s LEFT JOIN schoollogin sg ON sg.schoolNum=s.schoolNum  WHERE s.studentId={userNum} and sg.islogin='1' and sg.starttime < now() and sg.endtime > now() ", null, args);
        if (null == list || list.size() == 0) {
            list = this.dao2._queryMapList("SELECT s.schoolNum,sg.islogin,sg.shuoming from student s LEFT JOIN schoollogin sg ON sg.schoolNum=s.schoolNum  WHERE s.studentId={userNum} and sg.islogin='1' and sg.starttime ='' and sg.endtime ='' ", null, args);
            if (null == list || list.size() == 0) {
                return null;
            }
        }
        return list;
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<String> getAstrictGradeDataByPartType(String partType) {
        Map args = StreamMap.create().put("partType", (Object) partType);
        return this.dao2._queryColList("select gradeNum from astrict where partType = {partType} and (startTime > now() or endTime < now())", String.class, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<Map<String, Object>> getAstrictDataByPartType(String partType) {
        Map args = StreamMap.create().put("partType", (Object) partType);
        return this.dao2._queryMapList("select examNum,gradeNum,subjectNum from astrict where partType = {partType} and (startTime > now() or endTime < now())", null, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<String> getAstrictExamDataByPartType(String partType) {
        Map args = StreamMap.create().put("partType", (Object) partType);
        return this.dao2._queryColList("select examNum from astrict where partType = {partType} and (startTime > now() or endTime < now())", String.class, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<Map<String, Object>> getAstrictLoginDataByPartType(String partType) {
        return this.dao2.queryMapList("select startTime,endTime from astrict where partType = 4 and (startTime > now() or endTime < now())");
    }

    @Override // com.dmj.service.astrict.AstrictService
    public void submitAstrict(String userId, String partType, List<Map<String, Object>> paramList) {
        List<String> addList = new ArrayList<>();
        List<String> editList = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String date = DateUtil.getCurrentTime();
        Map args = new HashMap();
        args.put("date", date);
        args.put("userId", userId);
        for (int i = 0; i < paramList.size(); i++) {
            String gradeNum = Const.EXPORTREPORT_gradeNum + i;
            String examNum = Const.EXPORTREPORT_examNum + i;
            String startTime = "startTime" + i;
            String endTime = "endTime" + i;
            String id = "id" + i;
            args.put(startTime, paramList.get(i).get("startTime").toString());
            args.put(endTime, paramList.get(i).get("endTime").toString());
            args.put(id, paramList.get(i).get("id").toString());
            if (null == paramList.get(i).get("id") || "".equals(paramList.get(i).get("id"))) {
                StringBuffer sbAdd = new StringBuffer();
                sbAdd.append("insert into astrict (partType,");
                if ("1".equals(partType) || "2".equals(partType)) {
                    sbAdd.append("gradeNum,");
                    args.put(gradeNum, paramList.get(i).get(Const.EXPORTREPORT_gradeNum).toString());
                } else if ("3".equals(partType)) {
                    sbAdd.append("examNum,");
                    args.put(examNum, paramList.get(i).get(Const.EXPORTREPORT_examNum).toString());
                }
                sbAdd.append("startTime,endTime,insertUser,insertDate,updateUser,updateDate)");
                sbAdd.append("values(");
                sbAdd.append(partType);
                sbAdd.append(Const.STRING_SEPERATOR);
                if ("1".equals(partType) || "2".equals(partType)) {
                    sbAdd.append("{" + gradeNum + "}");
                    sbAdd.append(Const.STRING_SEPERATOR);
                } else if ("3".equals(partType)) {
                    sbAdd.append("{" + examNum + "}");
                    sbAdd.append(Const.STRING_SEPERATOR);
                }
                sbAdd.append("{" + startTime + "}");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {" + endTime + "} ");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {userId} ");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {date} ");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {userId} ");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {date} ");
                sbAdd.append(")");
                addList.add(sbAdd.toString());
            } else if (null != paramList.get(i).get("startTime") && !"".equals(paramList.get(i).get("startTime")) && null != paramList.get(i).get("endTime") && !"".equals(paramList.get(i).get("endTime"))) {
                StringBuffer sbEdit = new StringBuffer();
                sbEdit.append(" update astrict set startTime=  ");
                sbEdit.append(" {" + startTime + "}");
                sbEdit.append("  ,endTime =  ");
                sbEdit.append(" {" + endTime + "}");
                sbEdit.append("  where id =  ");
                sbEdit.append(" {" + id + "} ");
                editList.add(sbEdit.toString());
            } else {
                StringBuffer sbDel = new StringBuffer();
                sbDel.append("delete from astrict where id = ");
                sbDel.append("{id}");
                delList.add(sbDel.toString());
            }
        }
        this.dao2._batchExecute(addList, args);
        this.dao2._batchExecute(editList, args);
        this.dao2._batchExecute(delList, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public void submitAstrict2(String userId, String partType, List<Map<String, Object>> paramList) {
        List<String> addList = new ArrayList<>();
        List<String> editList = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String date = DateUtil.getCurrentTime();
        Map args = new HashMap();
        args.put("date", date);
        args.put("userId", userId);
        for (int i = 0; i < paramList.size(); i++) {
            String gradeNum = Const.EXPORTREPORT_gradeNum + i;
            String examNum = Const.EXPORTREPORT_examNum + i;
            String subjectNum = Const.EXPORTREPORT_subjectNum + i;
            String startTime = "startTime" + i;
            String id = "id" + i;
            args.put(startTime, paramList.get(i).get("startTime").toString());
            args.put(id, paramList.get(i).get("id").toString());
            if (null == paramList.get(i).get("id") || "".equals(paramList.get(i).get("id"))) {
                StringBuffer sbAdd = new StringBuffer();
                sbAdd.append("insert into astrict (partType,");
                sbAdd.append("gradeNum,");
                args.put(gradeNum, paramList.get(i).get(Const.EXPORTREPORT_gradeNum).toString());
                sbAdd.append("examNum,");
                args.put(examNum, paramList.get(i).get(Const.EXPORTREPORT_examNum).toString());
                sbAdd.append("subjectNum,");
                args.put(subjectNum, paramList.get(i).get(Const.EXPORTREPORT_subjectNum).toString());
                sbAdd.append("startTime,insertUser,insertDate,updateUser,updateDate)");
                sbAdd.append("values(");
                sbAdd.append(partType);
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append("{" + gradeNum + "}");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append("{" + examNum + "}");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append("{" + subjectNum + "}");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append("{" + startTime + "}");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {userId} ");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {date} ");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {userId} ");
                sbAdd.append(Const.STRING_SEPERATOR);
                sbAdd.append(" {date} ");
                sbAdd.append(")");
                addList.add(sbAdd.toString());
            } else if (null != paramList.get(i).get("startTime") && !"".equals(paramList.get(i).get("startTime"))) {
                StringBuffer sbEdit = new StringBuffer();
                sbEdit.append(" update astrict set startTime=  ");
                sbEdit.append(" {" + startTime + "}");
                sbEdit.append("  where id =  ");
                sbEdit.append(" {" + id + "} ");
                editList.add(sbEdit.toString());
            } else {
                StringBuffer sbDel = new StringBuffer();
                sbDel.append("delete from astrict where id = ");
                sbDel.append("{id}");
                delList.add(sbDel.toString());
            }
        }
        this.dao2._batchExecute(addList, args);
        this.dao2._batchExecute(editList, args);
        this.dao2._batchExecute(delList, args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public int delAstrictById(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        return this.dao2._execute("delete from astrict where id= {id} ", args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public List<Map<String, Object>> getAllLimitedGrade() {
        List<Map<String, Object>> list = this.dao2.queryMapList("select g.gradeNum,g.gradeName from (select * from astrict where partType = '1' and (now()<startTime or now()>endTime)) as a left join (select gradeNum,gradeName from grade where isDelete = 'F' group by gradeNum) as g on g.gradeNum = a.gradeNum");
        return list;
    }

    @Override // com.dmj.service.astrict.AstrictService
    public Object getUnfinishedExamByGrade(String gradeNum) {
        StringBuffer epSql = new StringBuffer();
        epSql.append("select distinct examNum from exampaper ");
        epSql.append("where gradeNum={gradeNum} ");
        StringBuffer sql = new StringBuffer();
        sql.append("select e.examNum from exam e ");
        sql.append("inner join (");
        sql.append(epSql);
        sql.append(") ep on ep.examNum=e.examNum ");
        sql.append("where e.status<>'9' limit 1");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryObject(sql.toString(), args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public String getGradeLastUpdateDate(String gradeNum) {
        StringBuffer sql = new StringBuffer();
        sql.append("select updateDate from grade ");
        sql.append("where gradeNum= {gradeNum}  and isDelete='F' ");
        sql.append("order by updateDate desc limit 1");
        Map args = StreamMap.create().put(Const.EXPORTREPORT_gradeNum, (Object) gradeNum);
        return this.dao2._queryStr(sql.toString(), args);
    }

    @Override // com.dmj.service.astrict.AstrictService
    public String updatestudent(String examNum, String userNum) {
        Map args = new HashMap();
        args.put(Const.EXPORTREPORT_examNum, examNum);
        List<Map<String, Object>> list = this.dao2._queryMapList("SELECT DISTINCT s.studentId,s.schoolNum,s.classNum from  examinationnum en LEFT JOIN student s ON en.studentId=s.id WHERE en.examnum={examNum} and (en.schoolNum !=s.schoolNum or en.classNum !=s.classNum) ", null, args);
        for (int i = 0; i < list.size(); i++) {
            String studentId = list.get(i).get(Const.EXPORTREPORT_studentId).toString();
            String schoolNum = list.get(i).get(Const.EXPORTREPORT_schoolNum).toString();
            String classNum = list.get(i).get(Const.EXPORTREPORT_classNum).toString();
            this.examDao.editExamineeNum(examNum, studentId, schoolNum, classNum);
        }
        return "1";
    }
}
