package com.zhangyu.coderman.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.admin.service.UserManagerService;
import com.zhangyu.coderman.admin.vo.PageVO;
import com.zhangyu.coderman.admin.vo.UserVO;
import com.zhangyu.coderman.web.dao.UserMapper;
import com.zhangyu.coderman.web.modal.User;
import com.zhangyu.coderman.web.modal.UserExample;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/12 11:06
 * @Version 1.0
 **/
@Service
public class UserManagerServiceImpl implements UserManagerService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageVO<User> list(UserVO userVO) {
        PageHelper.startPage(userVO.getPage(),userVO.getLimit());
        UserExample example = new UserExample();

        example.setOrderByClause("gmt_create desc");
        if(userVO.getId()!=null&&!"".equals(userVO.getId())){
            example.createCriteria().andIdEqualTo(userVO.getId());
        }
        if(userVO.getName()!=null&&!"".equalsIgnoreCase(userVO.getName())){
            example.createCriteria().andNameLike("%"+userVO.getName()+"%");
        }
        if(userVO.getAccountId()!=null&&!"".equalsIgnoreCase(userVO.getAccountId())){
            example.createCriteria().andAccountIdLike("%"+userVO.getAccountId()+"%");
        }
        if(userVO.getRank()!=null){
            example.createCriteria().andRankEqualTo(userVO.getRank());
        }

        List<User> users = userMapper.selectByExample(example);

        PageInfo<User> pageInfo = new PageInfo<>(users);
        return new PageVO<>(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public void delete(Integer id) {
        userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void setRank(UserVO userVO) {
        User user = new User();
        BeanUtils.copyProperties(userVO,user);
        userMapper.updateByPrimaryKeySelective(user);
    }
}
