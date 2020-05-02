package com.zhangyu.coderman.web.dao;

import com.zhangyu.coderman.web.modal.Topic;
import com.zhangyu.coderman.web.modal.TopicExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TopicMapper {
    int countByExample(TopicExample example);

    int deleteByExample(TopicExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Topic record);

    int insertSelective(Topic record);

    List<Topic> selectByExample(TopicExample example);

    Topic selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Topic record, @Param("example") TopicExample example);

    int updateByExample(@Param("record") Topic record, @Param("example") TopicExample example);

    int updateByPrimaryKeySelective(Topic record);

    int updateByPrimaryKey(Topic record);

    @Update(value = "update topic set follow_count =follow_count +1 where  id=#{id}")
    void invTopicFollowCount(@Param("id") int id);

    @Update(value = "update topic set follow_count =follow_count -1 where id=#{id}")
    void unInCTopicFollowCount(@Param("id")  int id);
}