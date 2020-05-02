package com.zhangyu.coderman.web.service;

import com.zhangyu.coderman.web.dto.NewUserDTO;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.modal.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    void save(User user);

    User findUserByToken(String token);

    void SaveOrUpdate(User user);

    List<NewUserDTO> findNewsUsers(Integer top);

    List<User> getFollowList( User user);

    List<User> getFansList(User user);

    ResultTypeDTO signIn(Integer id);

    boolean isSigined(Integer id);
}
