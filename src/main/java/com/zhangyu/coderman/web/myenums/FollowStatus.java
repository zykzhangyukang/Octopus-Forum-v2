package com.zhangyu.coderman.web.myenums;

public enum FollowStatus {
    FOLLOWED(1,"已关注"),
    UN_FOLLOWED(0,"未关注")
    ;
    private String name;
    private Integer val;


    FollowStatus(Integer val,String name ) {
        this.name = name;
        this.val = val;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
