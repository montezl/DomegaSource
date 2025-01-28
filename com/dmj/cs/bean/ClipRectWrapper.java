package com.dmj.cs.bean;

import cn.hutool.core.bean.BeanUtil;
import com.dmj.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* loaded from: ClipRectWrapper.class */
public class ClipRectWrapper {
    private ClipRect rect;
    private CsDefine define;
    private List<ClipRectWrapper> childrens = new ArrayList();
    private ClipRectWrapperCollection clipRectWrapperCollection;

    public ClipRect getRect() {
        return this.rect;
    }

    public void setRect(ClipRect rect) {
        this.rect = rect;
    }

    public CsDefine getDefine() {
        return this.define;
    }

    public void setDefine(CsDefine define) {
        this.define = define;
    }

    public List<ClipRectWrapper> getChildrens() {
        return this.childrens;
    }

    public void setChildrens(List<ClipRectWrapper> childrens) {
        this.childrens = childrens;
    }

    public ClipRectWrapperCollection getClipRectWrapperCollection() {
        return this.clipRectWrapperCollection;
    }

    public void setClipRectWrapperCollection(ClipRectWrapperCollection clipRectWrapperCollection) {
        this.clipRectWrapperCollection = clipRectWrapperCollection;
    }

    public ClipRectWrapper(ClipRect rect, CsDefine define, ClipRectWrapperCollection clipRectWrapperCollection) {
        this.rect = rect;
        this.define = define;
        this.clipRectWrapperCollection = clipRectWrapperCollection;
    }

    public ClipRectWrapper() {
    }

    public ClipRectWrapper addScoreIfExist(Map<String, ClipRect> map) {
        if (this.define.ifSubjective()) {
            this.childrens = (List) map.values().stream().filter(r -> {
                return r.isScore() && this.rect.getQuestionNum().equals(r.getParentQuestionNum());
            }).collect(ArrayList::new, (list1, rect) -> {
                list1.add(new ClipRectWrapper(rect, this.define, this.clipRectWrapperCollection));
            }, (list12, list2) -> {
                list12.addAll(list2);
            });
        }
        return this;
    }

    public ClipRectWrapper addObjectiveOptionIfExist(Map<String, ClipRect> map) {
        if (this.define.ifObjective() && this.define.ifMultiple()) {
            this.childrens = (List) map.values().stream().filter(r -> {
                return r.isObjectiveOption() && this.rect.getQuestionNum().equals(r.getParentQuestionNum());
            }).collect(ArrayList::new, (list1, rect) -> {
                list1.add(new ClipRectWrapper(rect, this.define, this.clipRectWrapperCollection));
            }, (list12, list2) -> {
                list12.addAll(list2);
            });
        }
        return this;
    }

    public String selectDefineId() {
        String parentQuestionNum;
        if (this.define.ifChooseSon()) {
            parentQuestionNum = this.define.getParent().getQuestionNum();
        } else if (this.define.ifChooseGrandson()) {
            parentQuestionNum = this.define.getParent().getParent().getQuestionNum();
        } else {
            return null;
        }
        String str = parentQuestionNum;
        List<ClipRectWrapper> parentClipRectWrappers = (List) this.clipRectWrapperCollection.getChoose().stream().filter(w -> {
            return w.rect.getQuestionNum().equals(str);
        }).collect(Collectors.toList());
        if (parentClipRectWrappers != null && parentClipRectWrappers.size() > 0) {
            int selectIndex = parentClipRectWrappers.get(0).getRect().getAnswerIndex();
            CsDefine parent = parentClipRectWrappers.get(0).getDefine();
            List<CsDefine> childrens = parent.getChildrens();
            return String.valueOf(childrens.get(selectIndex).getId());
        }
        return null;
    }

    public double objectiveScore(String abValue) {
        Map<String, Object> map = BeanUtil.beanToMap(this.define, new String[0]);
        ConvertMap(map, abValue);
        int optionCount = this.define.getOptionCount();
        String answer = this.rect.getAnswer();
        if (optionCount == 2) {
            answer = answer.replace('A', 'T').replace('B', 'F');
        }
        double regScore = Util.suitAllObjSingleJudge(answer, map);
        return regScore;
    }

    private void ConvertMap(Map<String, Object> map, String AB) {
        if ("B".equals(AB.toUpperCase())) {
            for (int i = 1; i < 16; i++) {
                if (map.containsKey("one" + i + "b") && map.containsKey("one" + i)) {
                    map.put("one" + i, map.get("one" + i + "b"));
                }
            }
            map.put("answer", map.get("answer_b"));
        }
    }
}
