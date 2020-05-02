package com.zhangyu.coderman.web.controller;

import com.zhangyu.coderman.web.dao.*;
import com.zhangyu.coderman.web.dto.CommentDTO;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.exception.CustomizeException;
import com.zhangyu.coderman.web.modal.*;
import com.zhangyu.coderman.web.myenums.*;
import com.zhangyu.coderman.web.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class QuestionController {

    protected static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private QuestionZanMapper questionZanMapper;

    @Autowired
    private IntegralMapper integralMapper;

    @Autowired
    private UserExtMapper userExtMapper;


    /**
     * 问题详情
     * @param idstr
     * @param map
     * @return
     */
    @GetMapping("/question/{id}")
    public String question(@PathVariable("id") String idstr, Map<String, Object> map,HttpServletRequest request) {

        User currentUser = (User) request.getSession().getAttribute("user");
        Question question;
        try {
            int i = Integer.parseInt(idstr);
            question = questionService.findQuestionById(i);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        //相关问题
        List<Question> relatedQuestions = questionService.relatedQuestions(question);
        //浏览数增加
        if(currentUser!=null){
            logger.info(currentUser.getName()+"查看问题:"+question.getId()+new Date());
        }
        questionService.incViewCount(question);
        //评论信息
        List<CommentDTO> commentDTOS = questionService.findQuestionComments(question.getId());
        //收藏该问题的人
        List<User> collectUsers=questionService.findCollectUsers(question.getId());
        Long integral = userExtMapper.getIntegral(question.getCreator());
        if(integral!=null){
            map.put("integral",integral);
        }else {
            map.put("integral",0);
        }
        map.put("comments", commentDTOS);
        map.put("question", question);
        map.put("collect_users",collectUsers);
        map.put("relatedQuestions", relatedQuestions);
        return "question";
    }

    /**
     * 删除问题
     *
     * @param id
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/deleteQuestion")
    public ResultTypeDTO deleteQuestion(@RequestParam("id") Integer id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.USER_NO_LOGIN);
        }
        Question dbQuestion = questionMapper.selectByPrimaryKey(id);
        if (dbQuestion.getCreator() != user.getId()) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_IS_YOURS);
        } else {
            questionMapper.deleteByPrimaryKey(id);
            //删除通知中关联的问题
            NotificationExample example = new NotificationExample();
            NotificationExample.Criteria criteria = example.createCriteria();
            criteria.andOutterIdEqualTo(dbQuestion.getId());
            notificationMapper.deleteByExample(example);
//            //删除关联的评论(删除一级评论)
//            CommentExample example1 = new CommentExample();
//            CommentExample.Criteria criteria1 = example1.createCriteria();
//            criteria1.andParentIdEqualTo(dbQuestion.getId());
//            criteria1.andTypeEqualTo(CommentType.COMMENT_ONE.getVal());
//            commentMapper.deleteByExample(example1);
        }
        //return "redirect:/profile/questions";
        return new ResultTypeDTO().okOf().addMsg("result", QuestionErrorEnum.QUESTION_DELETE_SUCCESS.getMsg());
    }


    private ArrayList<Integer> likeQuestionIds=new ArrayList<>();

    //点赞问题
    @Transactional
    @ResponseBody
    @GetMapping("/likeQuestion")
    public ResultTypeDTO likeQuestion(@RequestParam("id") int id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
          return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }
        QuestionZanExample example = new QuestionZanExample();
        QuestionZanExample.Criteria criteria = example.createCriteria();
        criteria.andQuestionIdEqualTo((long)id);
        criteria.andUserIdEqualTo(user.getId());
        List<QuestionZan> questionZans = questionZanMapper.selectByExample(example);
        if(questionZans.size()>0){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.CANT_LIKE_QUESTION_TWICE);
        }
        Question dbQuestion = questionMapper.selectByPrimaryKey(id);
        if (dbQuestion == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }else {
            QuestionZan questionZan = new QuestionZan();
            questionZan.setGmtCreate(System.currentTimeMillis());
            questionZan.setGmtModified(System.currentTimeMillis());
            questionZan.setUserId(user.getId());
            questionZan.setQuestionId((long)id);
            questionZanMapper.insert(questionZan);
            questionExtMapper.incLikeCount(id);
        }

        if (!user.getId().equals(dbQuestion.getCreator())) {
            //通知
            Notification notification = new Notification();
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setType(CommentNotificationType.LIKE_QUESTION.getCode());
            notification.setOutterId(id);
            notification.setNotifier((long) user.getId());
            notification.setReceiver((long) dbQuestion.getCreator());
            notification.setStatus(CommentStatus.UN_READ.getCode());
            notificationMapper.insertSelective(notification);
            //被点赞的人可以获得2积分
            IntegralExample example1 = new IntegralExample();
            IntegralExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andUserIdEqualTo(dbQuestion.getCreator());
            List<Integral> integrals = integralMapper.selectByExample(example1);
            if(integrals.size()>0){
                Integral integral = integrals.get(0);
                integral.setIntegral(integral.getIntegral()+IntegralType.LIKE.getVal());
                integralMapper.updateByPrimaryKeySelective(integral);
            }else{
                Integral integral = new Integral();
                integral.setIntegral(IntegralType.LIKE.getVal());
                integral.setUserId(dbQuestion.getCreator());
                integral.setGmtModified(System.currentTimeMillis());
                integral.setGmtCreate(System.currentTimeMillis());
                integralMapper.insert(integral);
            }
        }
        return new ResultTypeDTO().okOf().addMsg("likequestioncount",dbQuestion.getLikeCount()+1);
    }
    //收藏问题
    @GetMapping("/doCollect")
    @ResponseBody
    public ResultTypeDTO doCollect(@RequestParam("id") Integer id,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }else {
            //判断是否有该问题
            Question dbQuestion = questionMapper.selectByPrimaryKey(id);
            if(dbQuestion==null){
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //判断是否已经收藏
            CollectExample collectExample = new CollectExample();
            CollectExample.Criteria criteria = collectExample.createCriteria();
            criteria.andUserIdEqualTo(user.getId());
            criteria.andQuestionIdEqualTo(id);
            int i = collectMapper.countByExample(collectExample);
            if(i>0){
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.YOU_COLLECTED_QUESTION);
            }else {
                //收藏
                Collect collect = new Collect();
                collect.setGmtCreate(System.currentTimeMillis());
                collect.setGmtModified(System.currentTimeMillis());
                collect.setQuestionId(id);
                collect.setUserId(user.getId());
                collectMapper.insert(collect);
                //被收藏的人添加积分
                IntegralExample example = new IntegralExample();
                IntegralExample.Criteria criteria1 = example.createCriteria();
                criteria1.andUserIdEqualTo(dbQuestion.getCreator());
                List<Integral> integrals = integralMapper.selectByExample(example);
                if(integrals.size()>0){
                    Integral integral = integrals.get(0);
                    integral.setIntegral(integral.getIntegral()+ IntegralType.COMMENT.getVal());
                    integralMapper.updateByPrimaryKeySelective(integral);
                }else {
                    Integral integral = new Integral();
                    integral.setUserId(dbQuestion.getCreator());
                    integral.setIntegral(IntegralType.COMMENT.getVal());
                    integral.setGmtCreate(System.currentTimeMillis());
                    integral.setGmtModified(System.currentTimeMillis());
                    integralMapper.insert(integral);
                }
            }
        }
        return new ResultTypeDTO().okOf();
    }
    //取消收藏
    @ResponseBody
    @GetMapping("/deleteCollect")
    public ResultTypeDTO deleteCollect(@RequestParam("id") Integer id,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }else{
            //判读是否存在该question
            Question dbQuestion = questionMapper.selectByPrimaryKey(id);
            if(dbQuestion==null){
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }else {
                //删除关注
                CollectExample collectExample = new CollectExample();
                CollectExample.Criteria criteria = collectExample.createCriteria();
                criteria.andUserIdEqualTo(user.getId());
                criteria.andQuestionIdEqualTo(id);
                collectMapper.deleteByExample(collectExample);
            }
        }
        return new ResultTypeDTO().okOf();
    }

}
