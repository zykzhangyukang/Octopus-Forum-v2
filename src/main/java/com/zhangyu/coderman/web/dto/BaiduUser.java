package com.zhangyu.coderman.web.dto;

public class BaiduUser {
    private String education;
    private String sex;
    private String blood;
    private String userid;
    private String birthday;
    private String username;
    private String portrait;
    private String headImg;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "BaiduUser{" +
                "education='" + education + '\'' +
                ", sex='" + sex + '\'' +
                ", blood='" + blood + '\'' +
                ", userid='" + userid + '\'' +
                ", birthday='" + birthday + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
