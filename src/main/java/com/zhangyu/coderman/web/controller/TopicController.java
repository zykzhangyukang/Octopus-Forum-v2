package com.zhangyu.coderman.web.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.web.dao.QuestionMapper;
import com.zhangyu.coderman.web.dao.TopicFollowMapper;
import com.zhangyu.coderman.web.dao.TopicMapper;
import com.zhangyu.coderman.web.dao.UserMapper;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.dto.TopicQueryDTO;
import com.zhangyu.coderman.web.exception.CustomizeException;
import com.zhangyu.coderman.web.modal.*;
import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.web.service.QuestionService;
import com.zhangyu.coderman.web.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//话题
@Controller
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicMapper topicMapper;
    @Autowired
    private TopicFollowMapper topicFollowMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;
    //话题中心
    @GetMapping("/topic")
    public String topic(Map<String,Object> map){
        List<Topic> topics = topicService.listAllTopic();
        map.put("topics",topics);
        map.put("navLi","topic");
        return "topic";
    }
    //ajax获取用户关注状态
    @GetMapping("/topic/getFollowStatus")
    @ResponseBody
    public ResultTypeDTO getFollowTopicStatusAjax(HttpServletRequest request,
                                                  @RequestParam("id") Integer id,Map<String,Object> map){
        Integer status=0;
        User user = (User) request.getSession().getAttribute("user");
        //该用关注该话题的状态
        TopicFollowExample topicFollowExample = new TopicFollowExample();
        if(user!=null){
            topicFollowExample.createCriteria().andTopicIdEqualTo(id).andUserIdEqualTo(user.getId());
            List<TopicFollow> topicFollows = topicFollowMapper.selectByExample(topicFollowExample);
            if(!topicFollows.isEmpty()){
               status= topicFollows.get(0).getStatus();
            }
        }
        return new ResultTypeDTO().okOf().addMsg("followStatus",status);
    }
    //话题详细
    @GetMapping("/topic/{id}")
    public String findTopic(@PathVariable("id") String id,Map<String,Object> map){
        Topic topic;
        Integer topicId;

        try {
            topicId=Integer.parseInt(id);
            topic = topicMapper.selectByPrimaryKey((long)topicId);
        } catch (Exception e) {
            throw new CustomizeException(CustomizeErrorCode.NOT_FOUND_TOPIC);
        }
        if(topic==null){
            throw new CustomizeException(CustomizeErrorCode.NOT_FOUND_TOPIC);
        }

        map.put("navLi","topic");
        map.put("topic",topic);
      return "topicInfo";
    }
    //加载话题数据
    @GetMapping("/loadTopicInfo")
    @ResponseBody
    public ResultTypeDTO loadTopicInfo(@RequestParam(value = "topicId") String topicId,
                                       @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                       @RequestParam(value = "pageSize",defaultValue = "6") Integer pageSize,HttpServletRequest request,
                                       @RequestParam(value = "sortBy",required = false) String sortBy){

        List<User> userList=null;
//        User user= (User) request.getSession().getAttribute("user");
        //该话题的问题列表
        TopicQueryDTO topicQueryDTO = new TopicQueryDTO();
        topicQueryDTO.setId((long) Integer.parseInt(topicId));
        topicQueryDTO.setPageSize(pageSize);
        topicQueryDTO.setPageNo(pageNo);
        topicQueryDTO.setSortBy(sortBy);
        PageInfo<Question> questions=questionService.findQuestionsWithUserByTopic(topicQueryDTO);
        //关注该话题的用户
        TopicFollowExample example = new TopicFollowExample();
        example.createCriteria().andTopicIdEqualTo(Integer.parseInt(topicId)).andStatusEqualTo(1);
        List<TopicFollow> topicFollows = topicFollowMapper.selectByExample(example);
        if(topicFollows.size()>0){
            userList=new ArrayList<>();
            for (TopicFollow topicFollow : topicFollows) {
                Integer userId = topicFollow.getUserId();
                User follow_user = userMapper.selectByPrimaryKey(userId);
                userList.add(follow_user);
            }
        }

        //相关话题
        List<Topic> topics=topicService.listRelatedTopics(Integer.parseInt(topicId));
//        //关注该话题的人
//        //List<User> userList=topicService.listAllFollowedTopic(id);
//        //map.put("users",userList);
//        map.put("page",questions);
//        map.put("topic",topic);

//        map.put("relatedTopics",topics);
        return new ResultTypeDTO().okOf().addMsg("page",questions).addMsg("relatedTopics",topics).addMsg("userList",userList);
    }


    //关注话题
    @GetMapping("/topic/follow")
    @ResponseBody
    public ResultTypeDTO followTopic(@RequestParam("id") int id,
                                     HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        //用户是否登入
        if(user==null){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }
        Topic topic = topicMapper.selectByPrimaryKey((long) id);
        //先判断该话题是否存在
        if(topic==null){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.NOT_FOUND_TOPIC);
        }else {
            //话题存在判断用户是否已经关注该话题
            TopicFollowExample topicFollowExample = new TopicFollowExample();
            topicFollowExample.createCriteria().andUserIdEqualTo(user.getId()).andTopicIdEqualTo(id);
            List<TopicFollow> topicFollows = topicFollowMapper.selectByExample(topicFollowExample);
            if(!topicFollows.isEmpty()&&topicFollows.get(0).getStatus()==1){
               return new ResultTypeDTO().errorOf(CustomizeErrorCode.TOPIC_IS_FOLLOWED);
            }
            //插入或更新状态
            TopicFollow topicFollow = new TopicFollow();

            topicFollow.setTopicId(id);
            topicFollow.setUserId(user.getId());
            topicFollow.setGmtModified(System.currentTimeMillis());
            topicFollow.setGmtCreate(System.currentTimeMillis());
            if(!topicFollows.isEmpty()){
                TopicFollow db = topicFollows.get(0);
                topicFollow.setId(db.getId());
                topicFollow.setStatus(db.getStatus());
            }
           return  topicService.saveOrUpdate(topicFollow);
        }
    }

    /**
     * 获取话题的分页信息
     * @param pageSize
     * @return
     */
    @GetMapping("/getTopicPage")
    @ResponseBody
    public ResultTypeDTO getTopicPage(@RequestParam("pageSize") int pageSize,@RequestParam("pageNo") int pageNo,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }else {
            PageHelper.startPage(pageNo,pageSize);
            List<Topic> topics = topicMapper.selectByExample(null);
            PageInfo<Topic> topicPageInfo = new PageInfo<>(topics);
            return new ResultTypeDTO().okOf().addMsg("page",topicPageInfo);
        }
    }


    //取消关注话题
    @GetMapping("/topic/unFollowTopic")
    @ResponseBody
    public ResultTypeDTO unFollowTopic(@RequestParam("id") int id,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }else {
            Topic topic = topicMapper.selectByPrimaryKey((long) id);
            if(topic==null){
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.NOT_FOUND_TOPIC);
            }else {
                TopicFollowExample example = new TopicFollowExample();
                example.createCriteria().andUserIdEqualTo(user.getId()).andTopicIdEqualTo(id).andStatusEqualTo(1);
                List<TopicFollow> topicFollows = topicFollowMapper.selectByExample(example);
                if(!topicFollows.isEmpty()){
                    TopicFollow topicFollow = topicFollows.get(0);
                    return topicService.unFollowTopic(topicFollow);
                }
            }
        }
        return new ResultTypeDTO().okOf();
    }
}
