package com.zhangyu.coderman.web.myenums;
public enum  CommentStatus {
    READ(0,"已读"),
    UN_READ(1,"未读");

    CommentStatus( Integer code,String status) {
        this.status = status;
        this.code = code;
    }

    private String status;

    private  Integer code;


    public String getDesc() {
        return status;
    }

    public void setDesc(String desc) {
        this.status = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
