package com.zhangyu.coderman.admin.controller;

import com.zhangyu.coderman.admin.service.QuestionManagerService;
import com.zhangyu.coderman.admin.vo.PageVO;
import com.zhangyu.coderman.admin.vo.QuestionVO;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.modal.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zhangyukang
 * @Date 2020/3/12 12:28
 * @Version 1.0
 **/
@Controller
@RequestMapping("/admin/questionManager")
public class QuestionManagerController {

    @Autowired
    private QuestionManagerService questionManagerService;

    /**
     * 跳转到用户管理页面
     * @return
     */
    @RequestMapping(value = "/questions",method = RequestMethod.GET)
    public String users(){
        return "admin/manager/questions";
    }

    /**
     * 用户列表
     * @param questionVO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public PageVO<Question> list(QuestionVO questionVO){
        PageVO<Question> page=questionManagerService.list(questionVO);
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
            questionManagerService.delete(id);
            return new ResultTypeDTO().okOf();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultTypeDTO().errorOf("删除帖子失败");
        }
    }

    /**
     * 置顶帖子
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateTop",method = RequestMethod.GET)
    public ResultTypeDTO updateTop(QuestionVO questionVO){
        try {
            questionManagerService.updateTop(questionVO);
            return new ResultTypeDTO().okOf();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultTypeDTO().errorOf("帖子置顶失败");
        }
    }

}
