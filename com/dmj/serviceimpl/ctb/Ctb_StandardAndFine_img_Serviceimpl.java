package com.dmj.serviceimpl.ctb;

import cn.hutool.core.io.FileUtil;
import com.dmj.daoimpl.base.BaseDaoImpl2;
import com.dmj.daoimpl.examManagement.ExamDAOImpl;
import com.dmj.domain.AjaxData;
import com.dmj.domain.AnswerQuestionImage;
import com.dmj.domain.Answerexampaperimage;
import com.dmj.domain.ExampaperQuestionimage;
import com.dmj.domain.vo.Imgpath;
import com.dmj.service.ctb.Ctb_StandardAndFine_img;
import com.dmj.service.historyTable.HistoryTableService;
import com.dmj.service.systemManagement.SystemService;
import com.dmj.serviceimpl.historyTable.HistoryTableServiceImpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.Const;
import com.dmj.util.ImageUtil;
import com.zht.db.ServiceFactory;
import com.zht.db.StreamMap;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

/* loaded from: Ctb_StandardAndFine_img_Serviceimpl.class */
public class Ctb_StandardAndFine_img_Serviceimpl implements Ctb_StandardAndFine_img {
    Logger log = Logger.getLogger(getClass());
    ExamDAOImpl edao = new ExamDAOImpl();
    BaseDaoImpl2<?, ?, ?> dao2 = new BaseDaoImpl2<>();
    private HistoryTableService hts = (HistoryTableService) ServiceFactory.getObject(new HistoryTableServiceImpl());
    private SystemService cis = (SystemService) ServiceFactory.getObject(new SystemServiceImpl());
    private ExamDAOImpl dao = new ExamDAOImpl();

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] rightQuestionImg(String exampaperNum, String questionNum, String id) {
        String idsql = "";
        if (id != null) {
            idsql = idsql + "  and id={id}   ";
        }
        String sql = "select s.img tableimg,ip.location locationurl  from  (SELECT imgpath,img FROM answerquestionimage WHERE exampaperNum={exampaperNum} " + idsql + "AND questionNum={questionNum} )s  left join (select id,location from imgpath )ip on s.imgpath=ip.id";
        Map args = StreamMap.create().put("id", (Object) id).put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum);
        Imgpath ip = (Imgpath) this.dao2._queryBean(sql, Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        ServletContext sc = ServletActionContext.getServletContext();
        String realPath = sc.getRealPath("/");
        String file = realPath + "/common/image/weiluru.png";
        return FileUtil.readBytes(file);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] questionOrAnswerBigImg(String id) {
        Map args = StreamMap.create().put("id", (Object) id);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (SELECT imgpath,img FROM answerexampaperimage WHERE id={id})s  left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        ServletContext sc = ServletActionContext.getServletContext();
        String realPath = sc.getRealPath("/");
        String file = realPath + "/common/image/bucunzai.png";
        return FileUtil.readBytes(file);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] QuestionImage(String examPaperNum, String questionNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (SELECT imgpath,img FROM exampaperquestionimage WHERE examPaperNum={examPaperNum} AND questionNum={questionNum})s  left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        ServletContext sc = ServletActionContext.getServletContext();
        String realPath = sc.getRealPath("/");
        String file = realPath + "/common/image/yuantiweiluru.png";
        return FileUtil.readBytes(file);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] rightQuestionImgGrade(String exampaperNum, String questionNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum);
        return this.dao2._queryBlob("SELECT img FROM scoreStandard  WHERE examPaperNum={exampaperNum} AND questionNum={questionNum} ", args);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public List getStdAnswerImageSize(String examNum, String subjectNum, String gradeNum, String questionNum, String examPaperNum) {
        if (examPaperNum == null || "".equals(examPaperNum)) {
            examPaperNum = this.edao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        return this.dao2._queryBeanList("SELECT id,examPaperNum,page,questionNum,width,height   FROM answerquestionimage   WHERE examPaperNum={examPaperNum} AND questionNum={questionNum}  LIMIT 0,1 ", AnswerQuestionImage.class, args);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public List getExamPaperQuesImageSize(String examNum, String subjectNum, String gradeNum, String questionNum, String examPaperNum) {
        if (examPaperNum == null || "".equals(examPaperNum)) {
            examPaperNum = this.edao.getExampaperNumBySubjectAndGradeAndExam(examNum, subjectNum, gradeNum);
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("questionNum", questionNum);
        return this.dao2._queryBeanList("SELECT id,examPaperNum,page,questionNum,width,height   FROM exampaperquestionimage   WHERE examPaperNum={examPaperNum} AND questionNum={questionNum}  LIMIT 0,1 ", ExampaperQuestionimage.class, args);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] getillimg(String regId) {
        Map args = new HashMap();
        args.put("regId", regId);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (SELECT imgpath,img FROM illegalimage  where regId ={regId})s   left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.dao.splitimgurl2(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] getExamineeImage(String regId) {
        Map args = StreamMap.create().put("regId", (Object) regId);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from (SELECT imgpath,img FROM examinationNumImg  where regId = {regId})s  left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] getScoreImage(String scoreId) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.scoreimg tableimg,ip.location locationurl  from (select  imgpath,scoreimg   from scoreimage  WHERE scoreId={scoreId})s left join (select id,location from imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] history_getQuesionImage(String scoreId) {
        Map args = StreamMap.create().put("scoreId", (Object) scoreId);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img   from his_questionImage  WHERE scoreId={scoreId})s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        this.log.info("get one Quesion Image  sql: select s.img tableimg,ip.location locationurl  from  (select  imgpath,img   from his_questionImage  WHERE scoreId={scoreId})s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id");
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] history_getQuesionImage(Integer examPaperNum, String studentID, String questionNum, String scoreId) {
        String historytableNum = this.hts.tableNum(String.valueOf(studentID), "0");
        if (null == scoreId) {
            return null;
        }
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("scoreId", scoreId);
        args.put("questionNum", questionNum);
        args.put("studentID", studentID);
        if (null == questionNum || questionNum.equals("-1")) {
            String getsql = "SELECT examPaperNum,studentId,page,questionNum FROM " + historytableNum + " WHERE ";
            if (null != examPaperNum && !examPaperNum.equals("")) {
                getsql = getsql + " exampaperNum = {examPaperNum} and ";
            }
            Object[] obj = this.dao2._queryArray(getsql + "id={scoreId} ", args);
            examPaperNum = Integer.valueOf(null == obj[0] ? -1 : Integer.parseInt(obj[0].toString()));
            studentID = null == obj[1] ? "-1" : obj[1].toString();
            String obj2 = null == obj[2] ? "-1" : obj[2].toString();
        }
        String questionType = "0";
        String cross_page = "F";
        Object[] obj3 = this.dao2._queryArray("SELECT (select questiontype from  his_define where id = {questionNum} ) questiontype ,(select cross_page from  his_defineInfo where defineId = {questionNum} ) cross_page", args);
        if (null != obj3) {
            questionType = null == obj3[0] ? "0" : obj3[0].toString();
            cross_page = null == obj3[0] ? "F" : obj3[1].toString();
        }
        if (null != questionType && questionType.equals("0")) {
            Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img     from  his_questionImage  WHERE scoreId={scoreId})s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (null != ip) {
                return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
            }
            return null;
        }
        if (null != cross_page && cross_page.equals("F")) {
            Imgpath ip2 = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img     from  his_questionImage  WHERE scoreId={scoreId})s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (null != ip2) {
                return this.cis.splitimgurl(ip2.getLocationurl(), ip2.getTableimg());
            }
            return null;
        }
        if (null == studentID || "-1".equals(studentID)) {
            String getsql2 = "SELECT examPaperNum,studentId,page,questionNum FROM  " + historytableNum + " WHERE ";
            if (null != examPaperNum && !examPaperNum.equals("")) {
                getsql2 = getsql2 + " exampaperNum = {examPaperNum} and ";
            }
            Object[] obj22 = this.dao2._queryArray(getsql2 + "id={scoreId} ", args);
            String obj4 = null == obj22[1] ? "0" : obj22[1].toString();
        }
        String getScoreInfoSql = "SELECT id FROM " + historytableNum + " WHERE examPaperNum={examPaperNum} AND studentId={studentID} AND questionNum={questionNum} order by page asc";
        List<Object> list = this.dao2._queryColList(getScoreInfoSql, args);
        if (null == list || list.size() <= 1) {
            Imgpath ip3 = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img     from  his_questionImage  WHERE scoreId={scoreId})s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (null != ip3) {
                return this.cis.splitimgurl(ip3.getLocationurl(), ip3.getTableimg());
            }
            return null;
        }
        List<byte[]> imgs = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            scoreId = String.valueOf(list.get(i));
            Imgpath ip4 = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img     from  his_questionImage  WHERE scoreId={scoreId} )s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
            if (null != ip4) {
                imgs.add(this.cis.splitimgurl(ip4.getLocationurl(), ip4.getTableimg()));
            }
        }
        return commbinImage(scoreId, imgs);
    }

    public byte[] commbinImage(String scoreId, List<byte[]> list) {
        List<BufferedImage> imgs = new ArrayList<>();
        while (0 < list.size()) {
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(list.get(0));
                list.remove(0);
                imgs.add(ImageIO.read(in));
            } catch (Exception e) {
                this.log.error("拼图：", e);
                e.printStackTrace();
                return null;
            }
        }
        return toCommbin2(scoreId, imgs);
    }

    public byte[] toCommbin2(String scoreId, List<BufferedImage> imgs) throws Exception {
        if (null == imgs || imgs.size() <= 0) {
            return new byte[0];
        }
        int width = imgs.get(0).getWidth();
        int imageNum = imgs.size();
        Integer[] hts = new Integer[imgs.size()];
        int totalHt = 0;
        for (int i = 0; i < imageNum; i++) {
            int ht = imgs.get(i).getHeight();
            totalHt += ht;
            hts[i] = Integer.valueOf(ht);
        }
        BufferedImage outImage = new BufferedImage(width, totalHt, 1);
        Graphics2D graphics = outImage.getGraphics();
        Graphics2D g2d = graphics;
        g2d.setComposite(AlphaComposite.Src);
        for (int i2 = 0; i2 < imageNum; i2++) {
            int ht2 = 0;
            for (int j = 0; j < i2; j++) {
                ht2 += hts[j].intValue();
            }
            g2d.drawImage(imgs.get(i2), 0, ht2, (ImageObserver) null);
        }
        if (null != graphics) {
            graphics.dispose();
        }
        if (null != g2d) {
            g2d.dispose();
        }
        byte[] bytes = ImageUtil.bufferedImageTobytes(outImage, 0.1f);
        outImage.flush();
        return bytes;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public List<String> history_getQuesionImage_scoreIdList(String examPaperNum, String studentID, String questionNum, String scoreId, String cross_page) {
        String historytableNum = this.hts.tableNum(studentID, "0");
        if (null == scoreId) {
            return null;
        }
        List<String> list = new ArrayList<>();
        Map args = new HashMap();
        args.put("examPaperNum", examPaperNum);
        args.put("scoreId", scoreId);
        args.put("questionNum", questionNum);
        args.put("studentID", studentID);
        if (null == questionNum || questionNum.equals("")) {
            String getsql = "SELECT examPaperNum,studentId,page,questionNum FROM " + historytableNum + " WHERE ";
            if (null != examPaperNum && !examPaperNum.equals("")) {
                getsql = getsql + " exampaperNum = {examPaperNum} and ";
            }
            this.dao2._queryArray(getsql + "id={scoreId} ", args);
        }
        String questionType = "0";
        Object[] obj = this.dao2._queryArray("SELECT (select questiontype from his_define where id = {questionNum}) questiontype ,(select cross_page from his_defineInfo where defineId = {questionNum}) cross_page", args);
        if (null != obj) {
            questionType = null == obj[0] ? "0" : obj[0].toString();
            String obj2 = null == obj[0] ? "F" : obj[1].toString();
        }
        if (null != questionType && questionType.equals("0")) {
            list.add(scoreId);
            return list;
        }
        if (null != cross_page && cross_page.equals("F")) {
            list.add(scoreId);
            return list;
        }
        if (null == studentID || studentID.equals("")) {
            String getsql2 = "SELECT examPaperNum,studentId,page,questionNum FROM " + historytableNum + " WHERE ";
            if (null != examPaperNum && !examPaperNum.equals("")) {
                getsql2 = getsql2 + " exampaperNum = {examPaperNum} and ";
            }
            Object[] obj22 = this.dao2._queryArray(getsql2 + "id={scoreId} ", args);
            String obj3 = null == obj22[1] ? "" : obj22[1].toString();
        }
        String getScoreInfoSql = "SELECT id FROM " + historytableNum + " WHERE examPaperNum={examPaperNum} AND studentId={studentID} AND questionNum={questionNum} order by page asc";
        return this.dao2._queryColList(getScoreInfoSql, String.class, args);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] history_getBigImage(String regId) {
        Map args = StreamMap.create().put("regId", (Object) regId);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (select  imgpath,img     from his_studentpaperimage  WHERE regId={regId} )s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] history_QuestionImage(String examPaperNum, String questionNum) {
        Map args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (SELECT imgpath,img FROM his_exampaperquestionimage WHERE examPaperNum={examPaperNum} AND questionNum={questionNum})s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public String isZhuTi(String examPaperNum, String questionNum) {
        String questionNum2;
        this.dao2._queryStr("select distinct examNum from exampaper where examPaperNum={examPaperNum}", StreamMap.create().put("examPaperNum", (Object) examPaperNum));
        Map<String, String> args = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum);
        String isZhuTi = this.dao2._queryStr("select isParent from define where examPaperNum={examPaperNum} and id={questionNum}", args);
        if ("1".equals(isZhuTi)) {
            questionNum2 = this.dao2._queryStr("select id from subdefine where pid={questionNum} order by id  limit 1", StreamMap.create().put("questionNum", (Object) questionNum));
        } else {
            questionNum2 = questionNum;
        }
        return questionNum2;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public String getImgPath(String examPaperNum, String questionNum) {
        String examNum = this.dao2._queryStr("select distinct examNum from exampaper where examPaperNum={examPaperNum}", StreamMap.create().put("examPaperNum", (Object) examPaperNum));
        Imgpath ip = (Imgpath) this.dao2._queryBean("select location locationurl,filename tableimg from imgpath where examNum={examNum} and selected='1'", Imgpath.class, StreamMap.create().put(Const.EXPORTREPORT_examNum, (Object) examNum));
        if (ip == null) {
            return null;
        }
        return ip.getLocationurl() + "/" + ip.getTableimg();
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public ExampaperQuestionimage getExampaperQuestionImagePath(String examPaperNum, String questionNum) {
        new StreamMap();
        Map args_fuexampaperNumSql = StreamMap.create().put("examPaperNum", (Object) examPaperNum);
        String pexampaperNum = this.dao2._queryStr("select pexamPaperNum from exampaper where exampaperNum={examPaperNum} ", args_fuexampaperNumSql);
        new StreamMap();
        Map args_sql = StreamMap.create().put("pexampaperNum", (Object) pexampaperNum).put("questionNum", (Object) questionNum);
        return (ExampaperQuestionimage) this.dao2._queryBean("select * from exampaperquestionimage where exampaperNUm={pexampaperNum} and questionNum={questionNum}", ExampaperQuestionimage.class, args_sql);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public AnswerQuestionImage getAnswerQuestionImageImagePath(String examPaperNum, String questionNum) {
        new StreamMap();
        Map args_sql = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("questionNum", (Object) questionNum);
        return (AnswerQuestionImage) this.dao2._queryBean("select * from answerquestionimage where exampaperNUm={examPaperNum} and questionNum={questionNum}", AnswerQuestionImage.class, args_sql);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public Answerexampaperimage getAnswerexampaperimagePath(String examPaperNum, String queOrAns) {
        new StreamMap();
        Map args_sql = StreamMap.create().put("examPaperNum", (Object) examPaperNum).put("queOrAns", (Object) queOrAns);
        return (Answerexampaperimage) this.dao2._queryBean("select * from answerexampaperimage where exampaperNum={examPaperNum} and imgType={queOrAns}", Answerexampaperimage.class, args_sql);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] history_rightQuestionImg(String exampaperNum, String questionNum) {
        Map args = StreamMap.create().put("exampaperNum", (Object) exampaperNum).put("questionNum", (Object) questionNum);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (SELECT imgpath,img FROM his_answerquestionimage WHERE exampaperNum={exampaperNum} AND questionNum={questionNum})s  left join (select id,location from his_imgpath )ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] abimage(String illid, String regId) {
        Map args = StreamMap.create().put("regId", (Object) regId);
        Imgpath ip = (Imgpath) this.dao2._queryBean("select s.img tableimg,ip.location locationurl  from  (SELECT imgpath,img  FROM exampapertypeimage WHERE  regId={regId} )s  left join (select id,location from imgpath UNION ALL  select id,location from his_imgpath)ip on s.imgpath=ip.id", Imgpath.class, args);
        if (null != ip) {
            return this.cis.splitimgurl(ip.getLocationurl(), ip.getTableimg());
        }
        return null;
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public byte[] imageC(String url, String img) {
        Map args = new HashMap();
        args.put("url", url);
        String url2 = this.dao2._queryStr("SELECT DISTINCT  ip.location ext1 FROM scan_test sc LEFT JOIN  (select id,location from imgpath UNION ALL select id,location from his_imgpath)ip ON sc.imgpath = ip.id WHERE sc.imgpath={url} ", args);
        return this.cis.splitimgurl(url2, img);
    }

    @Override // com.dmj.service.ctb.Ctb_StandardAndFine_img
    public List getimgurl(String url, String img) {
        Map args = new HashMap();
        args.put("url", url);
        args.put("img", img);
        return this.dao2._queryBeanList("SELECT DISTINCT  ip.location ext1,sc.img ext2 FROM scan_test sc LEFT JOIN  (select id,location from imgpath UNION ALL select id,location from his_imgpath)ip ON sc.imgpath = ip.id WHERE sc.imgpath={url} AND sc.img={img} ", AjaxData.class, args);
    }
}
