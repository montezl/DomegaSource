package com.dmj.cs.bean;

import com.dmj.cs.util.ClipRectType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: ClipRectWrapperCollection.class */
public class ClipRectWrapperCollection {
    private List<ClipRectWrapper> choose;
    private List<ClipRectWrapper> objective;
    private List<ClipRectWrapper> subjective;

    public List<ClipRectWrapper> getChoose() {
        return this.choose;
    }

    public void setChoose(List<ClipRectWrapper> choose) {
        this.choose = choose;
    }

    public List<ClipRectWrapper> getObjective() {
        return this.objective;
    }

    public void setObjective(List<ClipRectWrapper> objective) {
        this.objective = objective;
    }

    public List<ClipRectWrapper> getSubjective() {
        return this.subjective;
    }

    public void setSubjective(List<ClipRectWrapper> subjective) {
        this.subjective = subjective;
    }

    public ClipRectWrapperCollection(List<ClipRectWrapper> choose, List<ClipRectWrapper> objective, List<ClipRectWrapper> subjective) {
        this.choose = new ArrayList();
        this.objective = new ArrayList();
        this.subjective = new ArrayList();
        this.choose = choose;
        this.objective = objective;
        this.subjective = subjective;
    }

    public ClipRectWrapperCollection(Map<String, ClipRect> clipRectMap, Map<String, CsDefine> csDefineMap) {
        this.choose = new ArrayList();
        this.objective = new ArrayList();
        this.subjective = new ArrayList();
        load(clipRectMap, csDefineMap);
    }

    public ClipRectWrapperCollection load(Map<String, ClipRect> clipRectMap, Map<String, CsDefine> csDefineMap) {
        clipRectMap.forEach((key, r) -> {
            if (r.isChoose()) {
                this.choose.add(new ClipRectWrapper(r, (CsDefine) csDefineMap.get(key), this).addScoreIfExist(clipRectMap).addObjectiveOptionIfExist(clipRectMap));
            } else if (r.isSubjective()) {
                this.subjective.add(new ClipRectWrapper(r, (CsDefine) csDefineMap.get(key), this).addScoreIfExist(clipRectMap).addObjectiveOptionIfExist(clipRectMap));
            } else if (r.isObjective()) {
                this.objective.add(new ClipRectWrapper(r, (CsDefine) csDefineMap.get(key), this).addScoreIfExist(clipRectMap).addObjectiveOptionIfExist(clipRectMap));
            }
        });
        return this;
    }

    private boolean skip(String questionkey) {
        return questionkey.equals(ClipRectType.BigImg) || questionkey.equals(ClipRectType.CardNo) || questionkey.equals(ClipRectType.ClipPageMarkSampleImg) || questionkey.equals(ClipRectType.Corner) || questionkey.equals(ClipRectType.Illegal) || questionkey.equals(ClipRectType.PageAb) || questionkey.equals(ClipRectType.SrcBigImg) || questionkey.equals(ClipRectType.Score) || questionkey.equals(ClipRectType.ObjectiveOption);
    }
}
