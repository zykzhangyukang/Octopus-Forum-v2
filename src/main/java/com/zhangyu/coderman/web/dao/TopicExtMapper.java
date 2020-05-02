package com.zhangyu.coderman.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TopicExtMapper {

    @Update("update topic set talk_count =talk_count +1 where id=#{topicId}")
    void incTalkCount(@Param("topicId") Integer topicId);

    @Select("select follow_count from topic where id=#{id}")
    Integer getTopicFollowCountById(Integer id);
}
