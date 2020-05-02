package com.zhangyu.coderman.web.dao;
import com.zhangyu.coderman.web.modal.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
@Mapper
public interface UserExtMapper {

    @Select(value = "select * from user order by gmt_modified desc limit 0,#{top}")
    List<User> findNewUserList(Integer top);

    @Select(value = " select count(*) from follow where followed_user=#{id} and status=1")
    Integer getFansCount(@Param("id") Integer id);

    @Update("update notification set status=0 where receiver=#{id} ")
    void readAllNotification(@Param("id") Integer id);

    @Select(value = "select count(*) from follow where user_id=#{id} and status=1")
    Integer getFollowCount(Integer id);

    @Select(value = "select count(*) from question where creator=#{id}")
    Integer getQuestionCount(Integer id);

    @Select(value = " select user_id from follow where followed_user=#{id} and status=1")
    List<Integer> getFansIds(Integer id);

    @Select(value = "select count(*) from collect where user_id=#{id}")
    Integer getCollectCount(Integer id);

    @Select(value = "select integral from user_integral where user_id=#{id}")
    Long getIntegral(Integer id);
}
