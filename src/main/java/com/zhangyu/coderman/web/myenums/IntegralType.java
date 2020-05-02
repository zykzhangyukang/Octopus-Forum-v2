package com.zhangyu.coderman.web.myenums;

public enum IntegralType {
    SIGN_IN(10,"签到"),
    COMMENT(5,"被评论"),
    LIKE(2,"被点赞" );
    private long val;
    private String message;

    IntegralType(int val, String message) {
        this.val = val;
        this.message = message;
    }

    public long getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
