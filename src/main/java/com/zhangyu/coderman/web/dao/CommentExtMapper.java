package com.zhangyu.coderman.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommentExtMapper {


    @Update(value = "update comment set comment_count=comment_count+1 where id=#{parentId}")
    void incCommentCount(Integer parentId);

    @Update(value = "update comment set like_count=like_count+1 where id=#{id}")
    void incLikeCount(Integer id);
}
