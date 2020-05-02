package com.zhangyu.coderman.web.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.web.dao.*;
import com.zhangyu.coderman.web.dto.*;
import com.zhangyu.coderman.web.exception.CustomizeException;
import com.zhangyu.coderman.web.modal.*;
import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.web.myenums.QuestionCategory;
import com.zhangyu.coderman.web.myenums.QuestionErrorEnum;
import com.zhangyu.coderman.web.myenums.QuestionSortType;
import com.zhangyu.coderman.web.service.QuestionService;
import com.zhangyu.coderman.web.utils.DateFormateUtil;
import com.zhangyu.coderman.web.utils.DatesUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TopicExtMapper topicExtMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    private  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//    @Autowired
//    private HotTagCache hotTagCache;

    @Autowired
    private CollectExtMapper collectExtMapper;

    @Override
    public void doPublish(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public PageInfo<Question> getPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<Question> list = questionExtMapper.listQuestionWithUser();
        PageInfo<Question> questionPageInfo = new PageInfo<>(list);
        questionPageInfo.setNavigatePages(5);
        return questionPageInfo;
    }

    @Override
    public PageInfo<Question> findQuestionsByUserId(Integer pageNo, Integer pageSize, Integer id) {
        PageHelper.startPage(pageNo,pageSize);
        List<Question> list = questionExtMapper.listQuestionWithUserByUserId(id);
        BuildQuestionTime(list);
        PageInfo<Question> questionPageInfo = new PageInfo<>(list);
        questionPageInfo.setNavigatePages(3);
        return questionPageInfo;
    }

    @Override
    public Question findQuestionById(Integer id) {
        Question question=questionExtMapper.findQuestionWithUserById(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        return question;
    }

    @Override
    public void updateQuestion(Question question) {
        questionMapper.updateByPrimaryKeySelective(question);
    }

    @Override
    public ResultTypeDTO saveOrUpdate(Question question) {
        if(question.getId()==null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            question.setTop(0);
            //用户是否加入或者创建了话题
            if(question.getTopic()!=0){
                //加入话题,讨论数增加
                topicExtMapper.incTalkCount(question.getTopic());
                //创建话题，插入一天话题
            }
            questionMapper.insert(question);
            return new ResultTypeDTO().okOf().addMsg("result", QuestionErrorEnum.QUESTION_PUBLISH_SUCCESS.getMsg());
        }else{
            Question dbQuestion = questionExtMapper.findQuestionWithUserById(question.getId());
            question.setGmtModified(question.getGmtCreate());
            if(dbQuestion!=null&& !dbQuestion.getCreator().equals(question.getUser().getId())){
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.QUESTION_NOT_IS_YOURS);
            }
            questionMapper.updateByPrimaryKeySelective(question);
            return new ResultTypeDTO().okOf().addMsg("result", QuestionErrorEnum.QUESTION_UPDATE_SUCCESS.getMsg());
        }
    }

    @Override
    public List<Question> relatedQuestions(Question question) {
        List<Question> relatedList=null;
        String tags = question.getTag();
       if(!StringUtils.isEmpty(tags)){
           String sqlRegex = tags.replaceAll(",", "|");
          relatedList =questionExtMapper.selectRelated(sqlRegex,question.getId());
       }
       if(relatedList.size()>18){
           return relatedList.subList(0,18);
       }else {
           return relatedList;
       }
    }

    @Override
    public void incViewCount(Question question) {
        questionExtMapper.inCViewCount(question);
    }

    @Override
    public List<CommentDTO> findQuestionComments(Integer id) {
        CommentExample commentExample = new CommentExample();
        commentExample.setOrderByClause("gmt_create desc");
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andParentIdEqualTo(id);
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        List<CommentDTO> commentlist=null;
        if(comments.size()>0){
           commentlist= new ArrayList<>();
            for (Comment comment : comments) {
                CommentDTO commentDTO = new CommentDTO();
                BeanUtils.copyProperties(comment,commentDTO);
                User user = userMapper.selectByPrimaryKey(comment.getCommentor());
                commentDTO.setUser(user);
                String dateString = simpleDateFormat.format(new Date(comment.getGmtCreate()));
                String time = DateFormateUtil.getTime(dateString);
                commentDTO.setShowTime(time);
                if(user!=null){
                    commentlist.add(commentDTO);
                }
            }
        }
        return commentlist;
    }

    @Override
    public PageInfo<Question> getPageBySearch(QuestionQueryDTO questionQueryDTO) {
        List<Question> list=new ArrayList<>();
        PageInfo<Question> questionPageInfo;
        PageHelper.startPage(questionQueryDTO.getPageNo(),questionQueryDTO.getPageSize());
        String sortType=questionQueryDTO.getSort();

        if ("ALL".equals(sortType)){
            //全部
            list= questionExtMapper.listQuestionWithUserBySearch(questionQueryDTO);
       }else if (QuestionSortType.WEEK_HOT.name().equals(sortType)){
           //本周最热
            long startweektime = DatesUtil.getBeginDayOfWeek().getTime();
            long endweetime=DatesUtil.getEndDayOfWeek().getTime();
            questionQueryDTO.setBeginTime(startweektime);
            questionQueryDTO.setEndTime(endweetime);
            list=questionExtMapper.listQuestionHotByTime(questionQueryDTO);
        }else if (QuestionSortType.MONTH_HOT.name().equals(sortType)){
            //月最热
            long monthStartTime = DatesUtil.getBeginDayOfMonth().getTime();
            long montthEndTime=DatesUtil.getEndDayOfMonth().getTime();
            questionQueryDTO.setBeginTime(monthStartTime);
            questionQueryDTO.setEndTime(montthEndTime);
            list=questionExtMapper.listQuestionHotByTime(questionQueryDTO);
        }else if(QuestionSortType.WAIT_COMMENT.name().equals(sortType)){
            //0评论
            list=questionExtMapper.listQuestionZeroHot(questionQueryDTO.getTag(),questionQueryDTO.getCategory());
        }else if(QuestionSortType.LIKE_HOT.name().equals(questionQueryDTO.getSort())){
            //赞最多
            list=questionExtMapper.listQuestionMostLike(questionQueryDTO);
        }else  if(QuestionSortType.COMMENT_HOT.name().equals(sortType)){
            list=questionExtMapper.listQuestionMostComment(questionQueryDTO);
        }else if(QuestionSortType.VIEW_HOT.name().equals(sortType)){
            list=questionExtMapper.listQuestionViewHot(questionQueryDTO);
        }
        //时间格式化,typename
        BuildQuestionTime(list);
        questionPageInfo = new PageInfo<>(list);
        questionPageInfo.setNavigatePages(5);
        return questionPageInfo;
    }

    @Override
    public List<QuestionDTO> findNewQuestion(int i) {
        List<QuestionDTO> questions=questionExtMapper.findNewQuestion(i);
        return questions;
    }

    @Override
    public PageInfo<Question> findQuestionsByCategory(Integer pageNo, Integer pageSize, Integer categoryVal) {
        PageHelper.startPage(pageNo,pageSize);
        List<Question> questions = questionExtMapper.listQuestionWithUserBycategory(categoryVal);
        BuildQuestionTime(questions);
        PageInfo<Question> questionPageInfo = new PageInfo<>(questions);
        return questionPageInfo;
    }

    @Override
    public List<QuestionDTO> findRecommendQuestions(int pageno, int pagesize) {
        PageHelper.startPage(pageno,pagesize);
        List<QuestionDTO> questionDTOS= questionExtMapper.findRecommendQuestions();
        return questionDTOS;
    }

    @Override
    public PageInfo<Question> getCollectPage(Integer pageNo, Integer pageSize, Integer userId) {

        List<Integer> questionIds=collectExtMapper.findQuestionById(userId);
        if(questionIds.size()==0){
            return null;
        }
        PageHelper.startPage(pageNo,pageSize);
        List<Question> questions=questionExtMapper.listQuestionCollectedWithUser(questionIds);
        if(questionIds!=null&&questions.size()>0){
            BuildQuestionTime(questions);
        }
        return new PageInfo<>(questions);
    }

    @Override
    public List<User> findCollectUsers(Integer id) {
        CollectExample example = new CollectExample();
        CollectExample.Criteria criteria = example.createCriteria();
        criteria.andQuestionIdEqualTo(id);
        List<Collect> collects = collectMapper.selectByExample(example);
        if(collects!=null){
            ArrayList<User> objects = new ArrayList<>();
            for (Collect collect : collects) {
                User user = userMapper.selectByPrimaryKey(collect.getUserId());
                if(user!=null){
                    objects.add(user);
                }
            }
            return objects;
        }
        return null;
    }

    @Override
    public PageInfo<Question> findQuestionsWithUserByTopic(TopicQueryDTO topicQueryDTO) {
        PageHelper.startPage(topicQueryDTO.getPageNo(),topicQueryDTO.getPageSize());
        List<Question> questionsWithUserByTopic=null;
        switch (topicQueryDTO.getSortBy()){
            case "all": questionsWithUserByTopic= questionExtMapper.findQuestionsWithUserByTopicAll(topicQueryDTO);
                break;
            case "jh": questionsWithUserByTopic= questionExtMapper.findQuestionsWithUserByTopicJH(topicQueryDTO);
                break;
            case "tj": questionsWithUserByTopic= questionExtMapper.findQuestionsWithUserByTopicTJ(topicQueryDTO);
                break;
            case "wt": questionsWithUserByTopic= questionExtMapper.findQuestionsWithUserByTopicWT(topicQueryDTO);
                break;
        }
       if(questionsWithUserByTopic!=null&&questionsWithUserByTopic.size()>0){
           //时间格式化,typename
           BuildQuestionTime(questionsWithUserByTopic);
           return new PageInfo<>(questionsWithUserByTopic);
       }
       return null;
    }

    private void BuildQuestionTime(List<Question> questions) {
        for (Question question : questions) {
            Date date = new Date(question.getGmtCreate());
            String dateString = simpleDateFormat.format(date);
            if(dateString!=null&&!"".equals(dateString)){
                String time = DateFormateUtil.getTime(dateString);
                question.setShowTime(time);
            }
            //
            Integer category = question.getCategory();
            String typename = QuestionCategory.getnameByVal(category);
            question.setTypeName(typename);
        }
    }


}
