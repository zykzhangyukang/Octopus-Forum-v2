package com.zhangyu.coderman.admin.vo;

import com.zhangyu.coderman.web.modal.User;
import lombok.Data;

/**
 * @Author zhangyukang
 * @Date 2020/3/12 11:07
 * @Version 1.0
 **/
@Data
public class UserVO extends User {
    private int page;
    private int limit;
}
