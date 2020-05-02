package com.zhangyu.coderman.admin.service;

import com.zhangyu.coderman.admin.vo.CommentVO;
import com.zhangyu.coderman.admin.vo.PageVO;
import com.zhangyu.coderman.admin.vo.QuestionVO;
import com.zhangyu.coderman.web.modal.Comment;
import com.zhangyu.coderman.web.modal.Question;

/**
 * @Author zhangyukang
 * @Date 2020/3/12 11:06
 * @Version 1.0
 **/
public interface CommentManagerService {

    /**
     * 评论管理
     * @param commentVO
     * @return
     */
    PageVO<Comment> list(CommentVO commentVO);

    /**
     * 删除评论
     * @param id
     */
    void delete(Integer id);
    
}
