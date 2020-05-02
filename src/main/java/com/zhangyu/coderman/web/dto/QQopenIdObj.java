package com.zhangyu.coderman.web.dto;

import com.alibaba.fastjson.JSON;

public class QQopenIdObj {


    private String client_id;

    private String openid;

    private String callBackStr;

    public QQopenIdObj(String callBackStr) {
        this.callBackStr = callBackStr;
    }

    public String getCallBackStr() {
        return callBackStr;
    }

    public void setCallBackStr(String callBackStr) {
        this.callBackStr = callBackStr;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getOpenid() {
        String id = callBackStr.split("\\(")[1];
        String s = id.split("\\)")[0];
        return  JSON.parseObject(s, QQopenIdObj.class).getIdOpen();
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "QQopenIdObj{" +
                "client_id='" + client_id + '\'' +
                ", openid='" + openid + '\'' +
                ", callBackStr='" + callBackStr + '\'' +
                '}';
    }

    public QQopenIdObj() {

    }
    private String getIdOpen(){
        return this.openid;
    }
}
