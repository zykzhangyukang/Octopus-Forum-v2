package com.zhangyu.coderman.admin.controller;

import com.zhangyu.coderman.admin.service.UserManagerService;
import com.zhangyu.coderman.admin.vo.PageVO;
import com.zhangyu.coderman.admin.vo.UserVO;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.modal.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台用户管理接口
 * @Author zhangyukang
 * @Date 2020/3/12 10:49
 * @Version 1.0
 **/
@Controller
@RequestMapping("/admin/userManager")
public class UserManagerController {


    @Autowired
    private UserManagerService userManagerService;

    /**
     * 跳转到用户管理页面
     * @return
     */
    @RequestMapping(value = "/users",method = RequestMethod.GET)
    public String users(){
        return "admin/manager/users";
    }

    /**
     * 用户列表
     * @param userVO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public PageVO<User> list(UserVO userVO){
        PageVO<User> page=userManagerService.list(userVO);
        return page;
    }

    /**
     * 删除用户
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public ResultTypeDTO delete(@RequestParam("id") Integer id){
        try {
            userManagerService.delete(id);
            return new ResultTypeDTO().okOf();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultTypeDTO().errorOf("删除用户失败");
        }
    }

    /**
     * 更新等级
     * @param userVO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setRank",method = RequestMethod.POST)
    public ResultTypeDTO setRank(UserVO userVO){
        try {
            userManagerService.setRank(userVO);
            return new ResultTypeDTO().okOf();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultTypeDTO().errorOf("等级更新失败");
        }
    }

}
