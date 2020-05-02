package com.zhangyu.coderman.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by zhangyukang on 2019/11/10 15:10
 */
@Data
public class PageVO<T> {
    private Integer code=0;
    private long count;
    private List<T> data;


    public PageVO(long count, List<T> data) {
        this.count = count;
        this.data = data;
    }

    public PageVO(List<T> data) {
        this.count = 0;
        this.data = data;
    }
}
