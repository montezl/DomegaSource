package com.dmj.serviceimpl.historyTable;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.domain.AjaxData;
import com.dmj.domain.Exampaper;
import com.dmj.domain.config;
import com.dmj.domain.vo.Imgpath;
import com.dmj.service.examManagement.ScheduleService;
import com.dmj.service.historyTable.HistoryTableService;
import com.dmj.serviceimpl.examManagement.ScheduleServiceImpl;
import com.dmj.util.CommonUtil;
import com.dmj.util.Conffig;
import com.dmj.util.Const;
import com.dmj.util.DateUtil;
import com.websocket.WebSocket;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

/* loaded from: HistoryTableServiceImpl.class */
public class HistoryTableServiceImpl implements HistoryTableService {
    BaseDaoImpl2<?, ?, ?> dao = new BaseDaoImpl2<>();
    private Conffig conff = new Conffig();
    Logger log = Logger.getLogger(getClass());
    private ScheduleService ssi = (ScheduleService) ServiceFactory.getObject(new ScheduleServiceImpl());
    private Conffig cof = new Conffig();
    public HttpServletRequest request;

    @Override // com.dmj.service.historyTable.HistoryTableService
    public void createHistoryTable(Long counts, String examNum) {
        Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum);
        List jielist = this.dao._queryBeanList("select jie from exampaper where examNum={examNum}  group by jie", Exampaper.class, args);
        for (int jienum = 0; jienum < jielist.size(); jienum++) {
            new Exampaper();
            Exampaper e = (Exampaper) jielist.get(jienum);
            String jieStr = e.getJie().substring(2);
            for (int tableNum = 0; tableNum < counts.longValue(); tableNum++) {
                String scoresql = "  create table if not exists his" + jieStr + "_score" + tableNum + "(   id                   bigint not null,   regId                bigint not null,   questionNum     bigint(20)      not null,   questionScore        decimal(4,1) default 0.0,   questionType          char(19) not null comment '0 客观 1 主观题',   studentId            bigint not null,   examPaperNum         int,   schoolNum            int,   classNum             int,   gradeNum             int,   insertUser           int not null,   insertDate           char(19) not null,   examinationRoomNum   int,   page                 smallint(2),   continued            char(1),   jie                  smallint,   answer               char(6) ) charset = UTF8";
                this.dao.execute(scoresql);
                String scoreindexsql = " create index his_score_schoolNum on his" + jieStr + "_score" + tableNum + "(   schoolNum)";
                this.dao.execute(scoreindexsql);
                String knowlegesql = " create table if not exists his" + jieStr + "_knowlege_mid" + tableNum + "(   id                   bigint not null auto_increment,   examPaperNum         int not null,   examNum              int,   schoolNum            int,   gradeNum             int not null,   jie                  smallint,   classNum             int not null,   subjectNum           smallint,   konwNum              int not null,   totalScore           decimal(4,1),   score                decimal(4,1),   studentId            int,   insertUser           int not null,   insertDate           datetime not null,   isDelete             char(1) default 'F',   studentType          varchar(2) comment '文科 or 理科 or 普通类',   statisticType        char(1),   source               char(1) comment '对应data表字段',   primary key (id) )charset = UTF8";
                this.dao.execute(knowlegesql);
                String indexsql = "create unique index his_knowledgel_mid_index on his" + jieStr + "_knowlege_mid" + tableNum + "(   schoolNum,   gradeNum,   classNum,   studentId,   examNum,   konwNum,   examPaperNum,   studentType,   statisticType,   source)";
                this.dao.execute(indexsql);
            }
        }
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public void moveHistory(String examNum, String tablecount) throws Exception {
        this.request = ServletActionContext.getRequest();
        String loginId = new CommonUtil().getLoginUserNum(this.request);
        moveHistoryone(examNum);
        try {
            String path = ServletActionContext.getServletContext().getRealPath("");
            Conffig conffig = this.cof;
            String days = Conffig.getParameter(path, Const.deleteexammessage);
            this.ssi.deleteExamMessage(Integer.valueOf(days), Integer.valueOf(examNum));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("tablecount", (Object) tablecount);
            this.dao._execute("CALL inserthistorytable({examNum},{tablecount})", args);
            addExamLog(Integer.valueOf(examNum), "移动单次考试到历史表", loginId, "");
        } catch (Exception e2) {
            throw e2;
        }
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public List<AjaxData> queryExam() {
        return this.dao.queryBeanList("SELECT examNum num,examName name   from  exam where status='9' and isDelete='F'    ORDER BY insertDate DESC", AjaxData.class);
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public String tableNum(String studentId, String type) {
        String tablecount = this.dao.queryStr("select para from config where type='1'", null);
        String jiestr = studentId.substring(0, 2);
        String tablenum = String.valueOf(Math.round(Integer.valueOf(studentId.substring(2)).intValue() % Integer.valueOf(tablecount).intValue()));
        String tableName = "";
        if (type.equals("0")) {
            tableName = "his" + jiestr + "_score" + tablenum;
        } else if (type.equals("1")) {
            tableName = "his" + jiestr + "_knowlege_mid" + tablenum;
        }
        return tableName;
    }

    public List<String> getOutDateOfExam() {
        Map args = StreamMap.create().put("OUTDATE_EXAM", (Object) Const.OUTDATE_EXAM);
        this.log.info("getOutDateOfExam 获取达到移动到历史表的考试条件的考试编号  sql : select DISTINCT examNum from exam  where (UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(updateDate))/(24*60*60) > {OUTDATE_EXAM}  and isDelete != 'T' ");
        return this.dao._queryColList("select DISTINCT examNum from exam  where (UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(updateDate))/(24*60*60) > {OUTDATE_EXAM}  and isDelete != 'T' ", String.class, args);
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public Integer moveData(String tablecount, String path) throws Exception {
        List<String> examNums = getOutDateOfExam();
        if (null == examNums || examNums.size() <= 0) {
            this.log.info("定时线程：【历史表数据】   没有过期的考试数据需要移动！ ");
            return null;
        }
        for (Object examNum : examNums) {
            this.log.info("过期的考试num ：" + examNum);
        }
        moveHistoryfor(path);
        return null;
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public void inserthistablecount(Long tablecount, String loginNum) {
        this.dao.execute("delete from config where type='1'");
        Map args = StreamMap.create().put("tablecount", (Object) tablecount).put("loginNum", (Object) loginNum).put("insertDate", (Object) DateUtil.getCurrentDay());
        this.dao._execute("insert into config(type,para,insertUser,insertDate,description)values(1,{tablecount},{loginNum},{insertDate},'历史表总个数')", args);
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public String queryhisexam(String examNum) {
        return this.dao.queryStr("select count(1) from his_exam ", null);
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public void updatetablename(String tablecount, String ncount, String loginnum) throws Exception {
        Integer counts = Integer.valueOf(tablecount);
        for (int i = 0; i < counts.intValue(); i++) {
            String sql = "alter table his_score" + i + " rename to his_score" + i + "b";
            this.dao.execute(sql);
            String sql2 = "alter table his_knowlege_mid" + i + " rename to his_knowlege_mid" + i + "b";
            this.dao.execute(sql2);
        }
        String path = ServletActionContext.getServletContext().getRealPath("");
        Conffig conffig = this.conff;
        String historytable_M = Conffig.getParameter(path, "historytable");
        Double counts2 = Double.valueOf(Integer.valueOf(ncount).intValue() % Integer.valueOf(historytable_M).intValue() == 0 ? Integer.valueOf(ncount).intValue() / Integer.valueOf(historytable_M).intValue() : Math.floor(Integer.valueOf(ncount).intValue() / Integer.valueOf(historytable_M).intValue()) + 1.0d);
        Conffig conffig2 = this.conff;
        Conffig.setParameter(path, "tablecount", Math.round(counts2.doubleValue()) + "");
        inserthistablecount(Long.valueOf(Math.round(counts2.doubleValue())), loginnum);
        moveHistory2(tablecount, Math.round(counts2.doubleValue()) + "");
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public void moveHistory2(String tablecount, String newtablecount) throws Exception {
        try {
            Map args = StreamMap.create().put("tablecount", (Object) tablecount).put("newtablecount", (Object) newtablecount);
            this.dao._execute("CALL inserthistorytable2({tablecount},{newtablecount})", args);
        } catch (Exception e) {
            throw e;
        }
    }

    public void moveHistoryfor(String path) throws Exception {
        Conffig conffig = this.cof;
        String days = Conffig.getParameter(path, Const.moveexam);
        try {
            Map args = StreamMap.create().put("days", (Object) days);
            this.dao._execute("CALL moveExamfor({days})", args);
            addExamLog(null, "移动多次考试到历史表", "-3", "");
        } catch (Exception e) {
            throw e;
        }
    }

    public void moveHistoryone(String examnum) throws Exception {
        try {
            Map args = new HashMap();
            args.put("examnum", examnum);
            this.dao._execute("CALL moveExam({examnum})", args);
        } catch (Exception e) {
            throw e;
        }
    }

    public void addExamLog(Integer examNum, String name, String userid, String desc) {
        if (examNum != null) {
            Map args = StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum).put("userid", (Object) userid).put("insertDate", (Object) DateUtil.getCurrentTime()).put("name", (Object) name);
            this.dao._execute("insert into examlog(examNum,insertUser,insertDate,operate)values({examNum},{userid},{insertDate},{name})", args);
        } else {
            Map args2 = StreamMap.create().put("userid", (Object) userid).put("insertDate", (Object) DateUtil.getCurrentTime()).put("name", (Object) name);
            this.dao._execute("insert into examlog(insertUser,insertDate,operate)values({userid},{insertDate},{name})", args2);
        }
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public String imgsavehuixian() {
        Object omax = this.dao.queryObject("select para from config where type='2' ORDER BY operate desc LIMIT 1");
        if (omax == null) {
            return "";
        }
        String omaxint = omax.toString();
        return omaxint;
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public List configimglist(String type) {
        String ordersql = "";
        if (type.equals("2")) {
            ordersql = " order by id desc";
        }
        String sql = "select para,operate from config where type='2' " + ordersql + "";
        return this.dao.queryBeanList(sql, config.class);
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public void inithistablecount(String filePath) {
        Conffig conffig = this.conff;
        String tablecount = Conffig.getParameter(filePath, "tablecount");
        if (null == tablecount || "".equals(tablecount)) {
            Conffig conffig2 = this.conff;
            String historytable_M = Conffig.getParameter(filePath, "historytable");
            Conffig conffig3 = this.conff;
            String ncount = Conffig.getParameter(filePath, "ncount");
            Double counts = Double.valueOf(Integer.valueOf(ncount).intValue() % Integer.valueOf(historytable_M).intValue() == 0 ? Integer.valueOf(ncount).intValue() / Integer.valueOf(historytable_M).intValue() : Math.floor(Integer.valueOf(ncount).intValue() / Integer.valueOf(historytable_M).intValue()) + 1.0d);
            tablecount = String.valueOf(Math.round(counts.doubleValue()));
            Conffig conffig4 = this.conff;
            Conffig.setParameter(filePath, "tablecount", tablecount);
        }
        this.dao.execute("delete from config where type='1' and description='历史表总个数'");
        String finalTablecount = tablecount;
        Map args = StreamMap.create().put("tablecount", (Object) finalTablecount).put("insertDate", (Object) DateUtil.getCurrentTime());
        this.dao._execute("insert into config(type,para,insertUser,insertDate,description)values(1,{tablecount} ,'-2',{insertDate},'历史表总个数')", args);
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public Imgpath GetImgpath(String exams) {
        Map args = StreamMap.create().put("exams", (Object) exams);
        return (Imgpath) this.dao._queryBean("select location locationurl,filename tableimg  from imgpath where examNum={exams} and selected='1'", Imgpath.class, args);
    }

    @Override // com.dmj.service.historyTable.HistoryTableService
    public void convertImgToHtml() {
        String type;
        WebSocket.send("开始转换原题小切图");
        List<Map<String, Object>> list1 = this.dao.queryMapList("SELECT img.id,p.examNum,img.questionNum,img.img,path.location,path.filename from exampaperquestionimage img LEFT JOIN exampaper p on p.examPaperNum =img.examPaperNum LEFT JOIN imgpath path on path.id=img.imgpath  where img LIKE '%.png'");
        List<Object[]> args1 = new ArrayList<>();
        if (CollUtil.isNotEmpty(list1)) {
            list1.forEach(m -> {
                String imgpath = StrUtil.format("{}/{}/qietu", new Object[]{m.get("location"), m.get("filename")});
                String fileName = StrUtil.format("{}.txt", new Object[]{m.get("questionNum")});
                String html = StrUtil.format("<img style='width:98%;height:auto;margin-left: 1%;' @onerror='changeSrc()' src='imageAction!getImage2.action?examNum={}&img={}'/>", new Object[]{m.get(Const.EXPORTREPORT_examNum), m.get("img")});
                String textFile = StrUtil.format("{}/yuanti/{}", new Object[]{imgpath, fileName});
                String txtPath = "qietu/yuanti/" + fileName;
                FileUtil.writeString(html, textFile, CharsetUtil.CHARSET_UTF_8);
                args1.add(new Object[]{txtPath, m.get("id")});
            });
            this.dao.batchExecute("update exampaperquestionimage set img=? where id=?", (Object[][]) args1.toArray(new Object[args1.size()]));
        }
        WebSocket.send("转换原题小切图完毕");
        WebSocket.send("开始转换答案小切图");
        List<Map<String, Object>> list2 = this.dao.queryMapList("SELECT img.id,p.examNum,img.questionNum,img.img,path.location,path.filename from answerquestionimage img LEFT JOIN exampaper p on p.examPaperNum =img.examPaperNum LEFT JOIN imgpath path on path.id=img.imgpath  where img LIKE '%.png'");
        List<Object[]> args2 = new ArrayList<>();
        if (CollUtil.isNotEmpty(list2)) {
            list2.forEach(m2 -> {
                String imgpath = StrUtil.format("{}/{}/qietu", new Object[]{m2.get("location"), m2.get("filename")});
                String fileName = StrUtil.format("{}.txt", new Object[]{m2.get("questionNum")});
                String html = StrUtil.format("<img style='width:98%;height:auto;margin-left: 1%;' @onerror='changeSrc()' src='imageAction!getImage2.action?examNum={}&img={}'/>", new Object[]{m2.get(Const.EXPORTREPORT_examNum), m2.get("img")});
                String textFile = StrUtil.format("{}/daan/{}", new Object[]{imgpath, fileName});
                String txtPath = "qietu/daan/" + fileName;
                FileUtil.writeString(html, textFile, CharsetUtil.CHARSET_UTF_8);
                args2.add(new Object[]{txtPath, m2.get("id")});
            });
            this.dao.batchExecute("update answerquestionimage set img=? where id=?", (Object[][]) args2.toArray(new Object[args2.size()]));
        }
        WebSocket.send("转换原题小切图完毕");
        WebSocket.send("开始转换原题答案大图");
        List<Map<String, Object>> list3 = this.dao.queryMapList("SELECT img.id,p.examNum,img.imgtype,img.examPaperNum, img.img,path.location,path.filename,img.page from answerexampaperimage img LEFT JOIN exampaper p on p.examPaperNum =img.examPaperNum LEFT JOIN imgpath path on path.id=img.imgpath where img LIKE '%.png'");
        Map<String, List<Map<String, Object>>> classMap = (Map) list3.stream().sorted((m1, m22) -> {
            return Convert.toInt(m1.get("page")).compareTo(Convert.toInt(m22.get("page")));
        }).collect(Collectors.groupingBy(m3 -> {
            return m3.get("examPaperNum").toString() + "-" + m3.get("imgtype").toString();
        }));
        List<Object[]> delList = new ArrayList<>();
        List<Object[]> updateList = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> item : classMap.entrySet()) {
            String key = item.getKey();
            String str = key.split("-")[0];
            String imgType = key.split("-")[1];
            List<Map<String, Object>> list0 = item.getValue();
            Map<String, Object> m4 = list0.get(0);
            String imgpath = StrUtil.format("{}/{}/datu", new Object[]{m4.get("location"), m4.get("filename")});
            String fileName = StrUtil.format("{}.txt", new Object[]{Long.valueOf(this.dao.getUuid())});
            if (imgType.equals("1")) {
                type = "yuanti";
            } else {
                type = "daan";
            }
            String textFile = StrUtil.format("{}/{}/{}", new Object[]{imgpath, type, fileName});
            String txtPath = StrUtil.format("datu/{}/{}", new Object[]{type, fileName});
            List<String> htmls = new ArrayList<>();
            for (int i = 0; i < list0.size(); i++) {
                Map<String, Object> map = list0.get(i);
                htmls.add(StrUtil.format("<img style='width:98%;height:auto;margin-left: 1%;' @onerror='changeSrc()' src='imageAction!getImage2.action?examNum={}&img={}'/>", new Object[]{map.get(Const.EXPORTREPORT_examNum), map.get("img")}));
                if (i > 0) {
                    delList.add(new Object[]{map.get("id")});
                } else {
                    updateList.add(new Object[]{txtPath, map.get("id")});
                }
            }
            FileUtil.writeString(JSON.toJSONString(htmls), textFile, CharsetUtil.CHARSET_UTF_8);
            if (CollUtil.isNotEmpty(delList)) {
                this.dao.batchExecute("DELETE FROM answerexampaperimage WHERE id=?", (Object[][]) delList.toArray(new Object[delList.size()]));
            }
            if (CollUtil.isNotEmpty(updateList)) {
                this.dao.batchExecute("update answerexampaperimage set img=? where id=?", (Object[][]) updateList.toArray(new Object[updateList.size()]));
            }
        }
        WebSocket.send("转换原题答案大图完毕");
    }
}
