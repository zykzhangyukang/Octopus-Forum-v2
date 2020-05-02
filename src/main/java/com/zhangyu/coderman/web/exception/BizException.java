package com.zhangyu.coderman.web.exception;

import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;

public class BizException extends  RuntimeException{
    @Override
    public String getMessage() {
        return super.getMessage();
    }
    public BizException(CustomizeErrorCode customizeErrorCode) {
        super(customizeErrorCode.getMessage());
    }
}
