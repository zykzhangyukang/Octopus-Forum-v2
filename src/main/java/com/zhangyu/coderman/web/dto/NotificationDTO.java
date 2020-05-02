package com.zhangyu.coderman.web.dto;

import com.zhangyu.coderman.web.modal.User;

public class NotificationDTO<T> {

    private Integer id;

    //通知人的姓名
    private User notifier;
    //通知的类型
    private String commentNotificationType;
    //外键信息
    private T item;
    //状态
    private Integer status;

    private String showTime;

    private String statusMsg;

    private String msgTitle;

    private String statusClass;

    public String getShowTime() {
        return showTime;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    private long gmtCreate;

    public long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getNotifier() {
        return notifier;
    }

    public void setNotifier(User notifier) {
        this.notifier = notifier;
    }

    public String getCommentNotificationType() {
        return commentNotificationType;
    }

    public void setCommentNotificationType(String commentNotificationType) {
        this.commentNotificationType = commentNotificationType;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
