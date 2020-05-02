package com.zhangyu.coderman.admin.vo;

import com.zhangyu.coderman.web.modal.Comment;
import lombok.Data;

/**
 * @Author zhangyukang
 * @Date 2020/3/14 09:19
 * @Version 1.0
 **/
@Data
public class CommentVO extends Comment {
    private int page;
    private int limit;
}
