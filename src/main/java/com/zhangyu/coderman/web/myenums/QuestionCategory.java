package com.zhangyu.coderman.web.myenums;

/**
 * 问题所属类型
 */
public enum  QuestionCategory {
    Put_Questions(1,"提问"),
    Share(2,"分享"),
    Discuss(3,"讨论"),
    Advise(4,"建议"),
    Bug(5,"Bug"),
    FOR_JOB(6,"求职"),
    NOTICE(7,"公告"),
    TEACH(8,"教程"),
    INTERVIEW(9,"面试");
    private Integer value;
    private  String name;

    QuestionCategory(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static   String getnameByVal(Integer value){
        QuestionCategory[] values = QuestionCategory.values();
        for (Integer i = 0; i < value; i++) {
            if(values[i].getValue()==value){
                return values[i].name;
            }
        }
        return "";
    }
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
