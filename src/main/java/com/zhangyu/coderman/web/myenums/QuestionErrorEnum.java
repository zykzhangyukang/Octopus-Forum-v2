package com.zhangyu.coderman.web.myenums;

public enum QuestionErrorEnum {

    QUESTION_CANT_EMPTY("4000","问题不能为空"),
    QUESTION_HEAD_CANT_EMPTY("4001","问题标题不能为空"),
    QUESTION_DESC_CANT_EMPTY("4002","问题描述不能为空"),
    QUESTION_TAGS_CANT_EMPTY("4003","问题标签不能为空"),
    QUESTION_NEED_LOGIN("4004","发表问题需要登入哟~~"),
    QUESTION_PUBLISH_SUCCESS("4005","问题发表成功" ),
    QUESTION_UPDATE_SUCCESS("4006","问题更新成功" ),
    QUESTION_DELETE_SUCCESS("4007","问题删除成功"),
    Question_Category_CANT_EMPTY("4008","问题分类不能为空");

    private String msg;
    private String code;

    public String getMsg() {
        return msg;
    }

    QuestionErrorEnum(String code,String msg) {
        this.msg = msg;
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "QuestionErrorEnum{" +
                "msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
