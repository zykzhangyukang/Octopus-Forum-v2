package com.zhangyu.coderman.admin.vo;

import com.zhangyu.coderman.web.modal.Question;
import lombok.Data;

/**
 * @Author zhangyukang
 * @Date 2020/3/12 12:30
 * @Version 1.0
 **/
@Data
public class QuestionVO extends Question {
    private int page;
    private int limit;
}
