package com.zhangyu.coderman.web.service;

import com.zhangyu.coderman.web.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> list(Integer pageNo, Integer pageSize, Integer id);
}
