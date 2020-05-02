package com.zhangyu.coderman.web.controller;

import com.zhangyu.coderman.web.dao.FollowMapper;
import com.zhangyu.coderman.web.dao.NotificationMapper;
import com.zhangyu.coderman.web.dao.UserMapper;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.modal.Follow;
import com.zhangyu.coderman.web.modal.FollowExample;
import com.zhangyu.coderman.web.modal.Notification;
import com.zhangyu.coderman.web.modal.User;
import com.zhangyu.coderman.web.myenums.CommentNotificationType;
import com.zhangyu.coderman.web.myenums.CommentStatus;
import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.web.myenums.FollowStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FollowController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    //验证是否已关注
    @ResponseBody
    @GetMapping("/isFollowed")
    public ResultTypeDTO idFollowed(@RequestParam("id") Integer id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.FOLLOW_NEED_LOGIN);
        } else {
            FollowExample example = new FollowExample();
            FollowExample.Criteria criteria = example.createCriteria();
            criteria.andUserIdEqualTo(user.getId());
            criteria.andFollowedUserEqualTo(id);
            List<Follow> follows = followMapper.selectByExample(example);
            if (follows.size() > 0) {
                Follow follow = follows.get(0);
                return new ResultTypeDTO().okOf().addMsg("status", follow.getStatus());
            }
        }
        return new ResultTypeDTO().okOf();
    }

    //关注
    @ResponseBody
    @GetMapping("/follow")
    public ResultTypeDTO follow(@RequestParam("id") Integer id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.FOLLOW_NEED_LOGIN);
        }
        User dbUser = userMapper.selectByPrimaryKey(id);
        if (dbUser == null) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NOT_FOUND);
        } else {
            //先查看数据库中是否取消关注的记录
            FollowExample example = new FollowExample();
            FollowExample.Criteria criteria = example.createCriteria();
            criteria.andUserIdEqualTo(user.getId());
            criteria.andFollowedUserEqualTo(id);
            List<Follow> follows = followMapper.selectByExample(example);

            if (follows.size() > 0) {
                Follow follow = follows.get(0);
                if (follow.getStatus() == FollowStatus.UN_FOLLOWED.getVal()) {
                    //已有记录
                    follow.setStatus(FollowStatus.FOLLOWED.getVal());
                    followMapper.updateByPrimaryKeySelective(follow);
                    return new ResultTypeDTO().okOf();
                }
            } else {
                //插入新记录
                Follow nfollow = new Follow();
                nfollow.setUserId(user.getId());
                nfollow.setFollowedUser(id);
                nfollow.setGmtCreate(System.currentTimeMillis());
                nfollow.setStatus(FollowStatus.FOLLOWED.getVal());
                followMapper.insert(nfollow);
                //发送通知
                Notification notification = new Notification();
                notification.setStatus(CommentStatus.UN_READ.getCode());
                notification.setNotifier((long) user.getId());
                notification.setReceiver((long) id);
                notification.setType(CommentNotificationType.FOLLOWING.getCode());
                notification.setGmtCreate(System.currentTimeMillis());
                notification.setOutterId(user.getId());
                notificationMapper.insertSelective(notification);
            }

        }
        return new ResultTypeDTO().okOf();
    }

    //取消关注
    @ResponseBody
    @GetMapping("/deleteFollow")
    public ResultTypeDTO deleteFollow(@RequestParam("id") Integer id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }
        User dbUser = userMapper.selectByPrimaryKey(id);
        if (dbUser == null) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NOT_FOUND);
        } else {
            FollowExample example = new FollowExample();
            FollowExample.Criteria criteria = example.createCriteria();
            criteria.andUserIdEqualTo(user.getId());
            criteria.andStatusEqualTo(FollowStatus.FOLLOWED.getVal());
            criteria.andFollowedUserEqualTo(id);
            List<Follow> follows = followMapper.selectByExample(example);
            if (follows.size() > 0) {
                Follow follow = follows.get(0);
                follow.setStatus(FollowStatus.UN_FOLLOWED.getVal());
                followMapper.updateByPrimaryKeySelective(follow);
            }
        }
        return new ResultTypeDTO().okOf();
    }

    //我的关注的人
    @ResponseBody
    @GetMapping("/findMyFollow")
    public ResultTypeDTO findMyFollow(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<User> userList = new ArrayList<>();
        if (user != null) {

            FollowExample example = new FollowExample();
            FollowExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo(FollowStatus.FOLLOWED.getVal());
            criteria.andUserIdEqualTo(user.getId());
            List<Follow> follows = followMapper.selectByExample(example);
            if (follows != null && follows.size() > 0) {
                userList = new ArrayList<>();
                for (Follow follow : follows) {
                    Integer followedUser = follow.getFollowedUser();
                    User fuser = userMapper.selectByPrimaryKey(followedUser);
                    userList.add(fuser);
                }
            }
        }
        return new ResultTypeDTO().okOf().addMsg("follows", userList);
    }
}
