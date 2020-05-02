package com.zhangyu.coderman.web.controller;

import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.modal.User;
import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SigInController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 判断用户是否已经签到
     * @return
     */
    @ResponseBody
    @GetMapping("/sigIned")
    public ResultTypeDTO isSigined(HttpServletRequest request){
        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return new ResultTypeDTO().okOf().addMsg("sigined","0");
        }else {
           boolean sigined= userService.isSigined(user.getId());
           if(sigined){
               return new ResultTypeDTO().okOf().addMsg("sigined","1");
           }
        }
        return new ResultTypeDTO().okOf().addMsg("sigined","0");
    }

    /**
     * 用户签到
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/sigIn")
    public ResultTypeDTO sigIn(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }
        ResultTypeDTO resultTypeDTO=userService.signIn(user.getId());
        return resultTypeDTO;
    }
}
