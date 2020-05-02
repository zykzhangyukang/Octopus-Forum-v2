package com.zhangyu.coderman.web.service;

import com.zhangyu.coderman.web.dto.CommentDTO;
import com.zhangyu.coderman.web.modal.Comment;

import java.util.List;

public interface CommentService {

    void doComment(Comment comment);

    List<CommentDTO> findSecondComments(Integer id);
}
