package com.zhangyu.coderman.web.dao;

import com.zhangyu.coderman.web.modal.TopicFollow;
import com.zhangyu.coderman.web.modal.TopicFollowExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TopicFollowMapper {
    int countByExample(TopicFollowExample example);

    int deleteByExample(TopicFollowExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TopicFollow record);

    int insertSelective(TopicFollow record);

    List<TopicFollow> selectByExample(TopicFollowExample example);

    TopicFollow selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TopicFollow record, @Param("example") TopicFollowExample example);

    int updateByExample(@Param("record") TopicFollow record, @Param("example") TopicFollowExample example);

    int updateByPrimaryKeySelective(TopicFollow record);

    int updateByPrimaryKey(TopicFollow record);
}