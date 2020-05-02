package com.zhangyu.coderman.web.service;

import com.zhangyu.coderman.web.dao.*;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.modal.*;
import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private TopicFollowMapper topicFollowMapper;

    public List<Topic> listAllTopic(){
        return topicMapper.selectByExample(null);
    }

    public List<Topic> listRelatedTopics(int id) {

        List<Question> list = questionExtMapper.listQuestionByTopic(id);

        if(list.size()==0){
            return new ArrayList<>();
        }
        List<Long> topicIds = new ArrayList<>();
        for (Question question : list) {
            List<Question> relatedQuestions = questionService.relatedQuestions(question);
            if(relatedQuestions.size()>0){
                for (Question relatedQuestion : relatedQuestions) {
                   if(relatedQuestion!=null&&relatedQuestion.getTopic()!=null){
                       topicIds.add((long)relatedQuestion.getTopic());
                   }
                }
            }
        }
        if(topicIds.size()!=0){
            TopicExample topicExample = new TopicExample();
            TopicExample.Criteria criteria = topicExample.createCriteria();
            criteria.andIdIn(topicIds);
            criteria.andIdNotEqualTo((long)id);
          return topicMapper.selectByExample(topicExample);
        }
        return null;
    }
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;
    public List<User> listAllFollowedTopic(Integer id) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andTopicEqualTo(id);
        List<Question> questions = questionMapper.selectByExample(questionExample);
        List<User> userlist=new ArrayList<>();
        if(questions!=null&&questions.size()>0){
            userlist=new ArrayList<>();
            for (Question question : questions) {
                Integer creator = question.getCreator();
                User user = userMapper.selectByPrimaryKey(creator);
                if(!userlist.contains(user)){
                    userlist.add(user);
                }
            }
        }
        return userlist;
    }

    public ResultTypeDTO followTopic(TopicFollow topicFollow) {
        try {
            //话题表关注数增加
            topicMapper.invTopicFollowCount(topicFollow.getTopicId());
            //插入记录到topic_follow
            topicFollowMapper.insertSelective(topicFollow);
        } catch (Exception e) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.NOT_FOUND_TOPIC);
        }
        return new ResultTypeDTO().okOf();
    }

    public ResultTypeDTO unFollowTopic(TopicFollow topicFollow) {
        try {
            //话题表关注数减少
            topicMapper.unInCTopicFollowCount(topicFollow.getTopicId());
            //改变用户关注的状态
            topicFollow.setStatus(0);
           topicFollowMapper.updateByPrimaryKeySelective(topicFollow);
        } catch (Exception e) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.NOT_FOUND_TOPIC);
        }
        return new ResultTypeDTO().okOf().addMsg("followCount",topicExtMapper.getTopicFollowCountById(topicFollow.getTopicId()));
    }

    @Autowired
    private TopicExtMapper topicExtMapper;
    public ResultTypeDTO saveOrUpdate(TopicFollow topicFollow) {
        Integer status = topicFollow.getStatus();
        if(status==null){
            topicFollowMapper.insertSelective(topicFollow);
        }else if (status==0){
            topicFollow.setStatus(1);
            topicFollowMapper.updateByPrimaryKeySelective(topicFollow);
        }
        //插入
        topicMapper.invTopicFollowCount(topicFollow.getTopicId());
        Integer count=topicExtMapper.getTopicFollowCountById(topicFollow.getTopicId());
        return new ResultTypeDTO().okOf().addMsg("followCount",count);
    }
}
