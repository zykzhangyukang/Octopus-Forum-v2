package com.zhangyu.coderman.admin.service;

import com.zhangyu.coderman.admin.vo.PageVO;
import com.zhangyu.coderman.admin.vo.UserVO;
import com.zhangyu.coderman.web.modal.User;

/**
 * @Author zhangyukang
 * @Date 2020/3/12 11:06
 * @Version 1.0
 **/
public interface UserManagerService {

    /**
     * 用户管理
     * @param userVO
     * @return
     */
    PageVO<User> list(UserVO userVO);

    /**
     * 删除用户
     * @param id
     */
    void delete(Integer id);

    /**
     * 更新用户等级
     * @param userVO
     */
    void setRank(UserVO userVO);
}
