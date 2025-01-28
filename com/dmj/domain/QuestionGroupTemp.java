package com.dmj.domain;

import java.util.List;
import java.util.Map;

/* loaded from: QuestionGroupTemp.class */
public class QuestionGroupTemp {
    private String groupNum;
    private int totalNum;
    private int num;
    private int exampaperNum;
    private String totallogtime;
    private String numtime;
    private String visittime;
    private List avglist;
    private Map groupuserinfo;
    private Map groupavgcount;

    public Map getGroupavgcount() {
        return this.groupavgcount;
    }

    public void setGroupavgcount(Map groupavgcount) {
        this.groupavgcount = groupavgcount;
    }

    public Map getGroupuserinfo() {
        return this.groupuserinfo;
    }

    public void setGroupuserinfo(Map groupuserinfo) {
        this.groupuserinfo = groupuserinfo;
    }

    public String getVisittime() {
        return this.visittime;
    }

    public void setVisittime(String visittime) {
        this.visittime = visittime;
    }

    public int getExampaperNum() {
        return this.exampaperNum;
    }

    public void setExampaperNum(int exampaperNum) {
        this.exampaperNum = exampaperNum;
    }

    public String getGroupNum() {
        return this.groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public int getTotalNum() {
        return this.totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTotallogtime() {
        return this.totallogtime;
    }

    public void setTotallogtime(String totallogtime) {
        this.totallogtime = totallogtime;
    }

    public String getNumtime() {
        return this.numtime;
    }

    public void setNumtime(String numtime) {
        this.numtime = numtime;
    }

    public List getAvglist() {
        return this.avglist;
    }

    public void setAvglist(List avglist) {
        this.avglist = avglist;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.avglist == null ? 0 : this.avglist.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + this.exampaperNum)) + (this.groupNum == null ? 0 : this.groupNum.hashCode()))) + this.num)) + (this.numtime == null ? 0 : this.numtime.hashCode()))) + this.totalNum)) + (this.totallogtime == null ? 0 : this.totallogtime.hashCode()))) + (this.visittime == null ? 0 : this.visittime.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        QuestionGroupTemp other = (QuestionGroupTemp) obj;
        if (this.avglist == null) {
            if (other.avglist != null) {
                return false;
            }
        } else if (!this.avglist.equals(other.avglist)) {
            return false;
        }
        if (this.exampaperNum != other.exampaperNum) {
            return false;
        }
        if (this.groupNum == null) {
            if (other.groupNum != null) {
                return false;
            }
        } else if (!this.groupNum.equals(other.groupNum)) {
            return false;
        }
        if (this.num != other.num) {
            return false;
        }
        if (this.numtime == null) {
            if (other.numtime != null) {
                return false;
            }
        } else if (!this.numtime.equals(other.numtime)) {
            return false;
        }
        if (this.totalNum != other.totalNum) {
            return false;
        }
        if (this.totallogtime == null) {
            if (other.totallogtime != null) {
                return false;
            }
        } else if (!this.totallogtime.equals(other.totallogtime)) {
            return false;
        }
        if (this.visittime == null) {
            if (other.visittime != null) {
                return false;
            }
            return true;
        }
        if (!this.visittime.equals(other.visittime)) {
            return false;
        }
        return true;
    }
}
