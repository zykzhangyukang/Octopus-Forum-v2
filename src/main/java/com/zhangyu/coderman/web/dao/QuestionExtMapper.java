package com.zhangyu.coderman.web.dao;
import com.zhangyu.coderman.web.dto.QuestionDTO;
import com.zhangyu.coderman.web.dto.QuestionQueryDTO;
import com.zhangyu.coderman.web.dto.TopicQueryDTO;
import com.zhangyu.coderman.web.modal.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QuestionExtMapper {

    Question findQuestionWithUserById(Integer id);

    List<Question> listQuestionWithUserByUserId(Integer id);

    List<Question> listQuestionWithUser();

    @Select(value = "select * from question where tag regexp #{sqlRegex} and id!=#{id} order by gmt_create desc")
    List<Question> selectRelated(@Param("sqlRegex") String sqlRegex, @Param("id") Integer id);

    @Select(value = "update question set view_count=view_count +1 where id=#{id}")
    void inCViewCount(Question question);

    @Update("update question set comment_count=comment_count+1 where id=#{id}")
    void incCommentCount(@Param("id") Integer parentId);

    List<Question> listQuestionWithUserBySearch(QuestionQueryDTO questionQueryDTO);

    @Update("update question set like_count=like_count+1 where id=#{id}")
    void incLikeCount(Integer id);

    @Select(value = "select * from question order by gmt_create desc limit 0,#{i}")
    List<QuestionDTO> findNewQuestion(int i);

    List<Question> listQuestionHotByTime(QuestionQueryDTO questionQueryDTO);

    List<Question> listQuestionZeroHot(@Param("tag") String tag, @Param("category") Integer category);

    List<Question> listQuestionWithUserBycategory(Integer categoryVal);

    @Select("select * from question order by comment_count desc,like_count desc,view_count desc ")
    List<QuestionDTO> findRecommendQuestions();

    List<Question> listQuestionMostLike(QuestionQueryDTO questionQueryDTO);

    List<Question> listQuestionMostComment(QuestionQueryDTO questionQueryDTO);


    List<Question> listQuestionCollectedWithUser(List<Integer> groups);

    List<Question> findQuestionsWithUserByTopicAll(TopicQueryDTO topicQueryDTO);

    @Select(value = "select id,tag from question where topic=#{id}")
    List<Question> listQuestionByTopic(@Param("id") int id);

    List<Question> findQuestionsWithUserByTopicJH(TopicQueryDTO topicQueryDTO);

    List<Question> findQuestionsWithUserByTopicTJ(TopicQueryDTO topicQueryDTO);

    List<Question> findQuestionsWithUserByTopicWT(TopicQueryDTO topicQueryDTO);

    List<Question> listQuestionViewHot(QuestionQueryDTO questionQueryDTO);

    @Update("update question set top=#{top} where id=#{id}")
    void updateTop(@Param("id") Integer id,@Param("top") Integer top);
//    @Select(value = "select * from question limit #{offset},#{limit}")
//    List<Question> findQuestionPage(@Param("offset") int offset,@Param("limit") int limit);
}
