package com.zhangyu.coderman.admin.controller;

import com.zhangyu.coderman.admin.service.CommentManagerService;
import com.zhangyu.coderman.admin.vo.CommentVO;
import com.zhangyu.coderman.admin.vo.PageVO;
import com.zhangyu.coderman.admin.vo.QuestionVO;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.modal.Comment;
import com.zhangyu.coderman.web.modal.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zhangyukang
 * @Date 2020/3/14 09:15
 * @Version 1.0
 **/
@Controller
@RequestMapping("/admin/commentManager")
public class CommentManagerController {

    @Autowired
    private CommentManagerService commentManagerService;
    
    /**
     * 跳转到评论管理页面
     * @return
     */
    @GetMapping("/comments")
    public String comments(){
        return "admin/manager/comments";
    }


    /**
     * 评论列表
     * @param commentVO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public PageVO<Comment> list(CommentVO commentVO){
        PageVO<Comment> page=commentManagerService.list(commentVO);
        return page;
    }

    /**
     * 删除评论
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public ResultTypeDTO delete(@RequestParam("id") Integer id){
        try {
            commentManagerService.delete(id);
            return new ResultTypeDTO().okOf();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultTypeDTO().errorOf("删除评论失败");
        }
    }

}
