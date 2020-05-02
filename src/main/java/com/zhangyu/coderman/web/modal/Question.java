package com.zhangyu.coderman.web.modal;

import lombok.Data;

@Data
public class Question {
    private Integer id;

    private String title;

    private Long gmtCreate;

    private Long gmtModified;

    private Integer commentCount;

    private Integer viewCount;

    private Integer likeCount;

    private String tag;

    private Integer creator;

    private User user;

    private Integer category;

    private Integer topic;

    private Integer top;

    private String description;

    private String showTime;

    private String typeName;


}