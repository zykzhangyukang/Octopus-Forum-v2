package com.zhangyu.coderman.web.exception;

import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;

public class CustomizeException extends RuntimeException {

    @Override
    public String getMessage() {
        return super.getMessage();
    }
    public CustomizeException(CustomizeErrorCode customizeErrorCode) {
        super(customizeErrorCode.getMessage());
    }
}
