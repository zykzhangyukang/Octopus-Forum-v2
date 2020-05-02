package com.zhangyu.coderman.web.service.impl;

import com.zhangyu.coderman.web.dao.*;
import com.zhangyu.coderman.web.dto.NewUserDTO;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.modal.*;
import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.web.myenums.FollowStatus;
import com.zhangyu.coderman.web.myenums.IntegralType;
import com.zhangyu.coderman.web.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserExtMapper userExtMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private IntegralMapper integralMapper;

    @Override
    public void save(User user) {
        userMapper.insert(user);
    }


    @Override
    public User findUserByToken(String token) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andTokenEqualTo(token);
        List<User> users = userMapper.selectByExample(userExample);
        User user = null;
        if (users.size() > 0) {
            user = users.get(0);
        }
        return user;
    }

    @Override
    public void SaveOrUpdate(User user) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        User dbUser = null;
        if (users.size() > 0) {
            dbUser = users.get(0);
        }
        if (dbUser == null) {
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(System.currentTimeMillis());
            //userMapper.save(user);
            userMapper.insertSelective(user);
        } else {
            //更新用户(修改时间)
            user.setGmtCreate(dbUser.getGmtCreate());
            user.setGmtModified(System.currentTimeMillis());
            //userMapper.UpdateUser(user);
            user.setId(dbUser.getId());
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    @Override
    public List<NewUserDTO> findNewsUsers(Integer top) {
        List<User> userList = userExtMapper.findNewUserList(top);
        List<NewUserDTO> userDTOS = new ArrayList<>();

        if (userList != null && userList.size() > 0) {
            for (User user : userList) {
                if (user.getName() == null || user.getName() == "") {
                    user.setName("无名氏");
                }
            }
            for (User user : userList) {
                NewUserDTO newUserDTO = new NewUserDTO();
                newUserDTO.setFansCount(userExtMapper.getFansCount(user.getId()));
                BeanUtils.copyProperties(user, newUserDTO);
                QuestionExample example = new QuestionExample();
                QuestionExample.Criteria criteria = example.createCriteria();
                criteria.andCreatorEqualTo(user.getId());
                newUserDTO.setQuestionCount(questionMapper.countByExample(example));
                userDTOS.add(newUserDTO);
            }
        }
        return userDTOS;
    }

    @Autowired
    private FollowMapper followMapper;

    @Override
    public List<User> getFollowList(User user) {
        List<User> userList = new ArrayList<>();
        FollowExample example = new FollowExample();
        FollowExample.Criteria c = example.createCriteria();
        c.andStatusEqualTo(FollowStatus.FOLLOWED.getVal());
        c.andUserIdEqualTo(user.getId());
        List<Follow> follows = followMapper.selectByExample(example);
        if (follows.size() > 0) {
            userList = new ArrayList<>();
            for (Follow follow : follows) {
                Integer followedUser = follow.getFollowedUser();
                User fuser = userMapper.selectByPrimaryKey(followedUser);
                userList.add(fuser);
            }
        }
        return userList;
    }

    @Override
    public List<User> getFansList(User user) {
        if (user != null) {
            //所有粉丝的id
            List<Integer> fanIds = userExtMapper.getFansIds(user.getId());
            if (fanIds.size() > 0) {
                UserExample example = new UserExample();
                example.createCriteria().andIdIn(fanIds);
                List<User> fans = userMapper.selectByExample(example);
                return fans;
            }
        }
        return null;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 签到得积分
     * @param id
     * @return
     */
    @Transactional
    @Override
    public ResultTypeDTO signIn(Integer id) {
        if (id == null) {
            new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_ID_EMPTY);
        }
        String day = simpleDateFormat.format(new Date());
        String key = "signin:" + day;
        Boolean sismember = redisTemplate.opsForSet().isMember(key, id.toString());
        if(sismember){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.ALREADY_SIGIN);
        }else{
            redisTemplate.opsForSet().add(key,id.toString());
            this.redisTemplate.expire(key,getRefreshTime(), TimeUnit.SECONDS);
            addPointsRecord(id, IntegralType.SIGN_IN);//给用户增加积分
        }
        return new ResultTypeDTO().okOf();
    }

    /**
     * 今日是否签到
     * @param id
     * @return
     */
    @Override
    public boolean isSigined(Integer id) {
        String day = simpleDateFormat.format(new Date());
        String key = "signin:" + day;
        Boolean sismember = redisTemplate.opsForSet().isMember(key, id.toString());
        return sismember;
    }

    /**
     * 签到得积分
     * @param id
     * @param type
     */
    private void addPointsRecord(Integer id, IntegralType type) {
        IntegralExample example = new IntegralExample();
        example.createCriteria().andUserIdEqualTo(id);
        List<Integral> integrals = integralMapper.selectByExample(example);
        if(integrals.size()>0){
            Integral integral = integrals.get(0);
            integral.setIntegral(integral.getIntegral()+type.getVal());
            integralMapper.updateByPrimaryKeySelective(integral);
        }else {
            Integral integral = new Integral();
            integral.setIntegral(type.getVal());
            integral.setUserId(id);
            integral.setGmtCreate(System.currentTimeMillis());
            integral.setGmtModified(System.currentTimeMillis());
            integralMapper.insert(integral);
        }
    }

    /**
     * 获取当前时间离明天凌晨还有多少小时
     * @return
     */
    public static int getRefreshTime(){
        Calendar calendar = Calendar.getInstance();
        int now = (int) (calendar.getTimeInMillis()/1000);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY , 0);
        return (int) (calendar.getTimeInMillis()/1000-now);
    }

}
