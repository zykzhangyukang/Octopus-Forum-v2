package com.zhangyu.coderman.web.controller;

import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.web.dao.UserExtMapper;
import com.zhangyu.coderman.web.dao.UserMapper;
import com.zhangyu.coderman.web.exception.CustomizeException;
import com.zhangyu.coderman.web.modal.*;
import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.web.service.QuestionService;
import com.zhangyu.coderman.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class PeopleController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserExtMapper userExtMapper;

    @Autowired
    private UserService userService;


    @GetMapping("/people")
    public String people(@RequestParam("id") String id,Map<String,Object> map,
                         @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                         @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,HttpServletRequest request
                         ){
        User loginUser = (User) request.getSession().getAttribute("user");
        Integer i;
        try {
            i = Integer.parseInt(id);
        } catch (NumberFormatException e) {
           throw  new CustomizeException(CustomizeErrorCode.PEOPLE_DOT_HAVE);
        }
        if(loginUser!=null&&loginUser.getId().toString().equals(id)){
         return "redirect:/profile";
        }
        //他的问题
        PageInfo<Question> myquestionPageInfo=null;
        User user = userMapper.selectByPrimaryKey(i);
        if(user!=null&&user.getId()!=null){
            myquestionPageInfo=questionService.findQuestionsByUserId(pageNo,pageSize,user.getId());
        }else {
            throw new CustomizeException(CustomizeErrorCode.PEOPLE_DOT_HAVE);
        }
        //他关注的人
        List<User> followList = userService.getFollowList(user);
        //他的积分
        Long integral = userExtMapper.getIntegral(Integer.parseInt(id));
        if(integral==null){
            integral=(long)0;
        }
        //他的粉丝
       List<User> fansList=userService.getFansList(user);
        map.put("userList",followList);
        map.put("fansList",fansList);
        map.put("id",i);
        map.put("integral",integral);
        map.put("people",user);
        map.put("page",myquestionPageInfo);
        return "people";
    }




}
