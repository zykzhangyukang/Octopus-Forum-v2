package com.zhangyu.coderman.web.utils;

import javax.servlet.http.Cookie;

public class CookieUtils {
    public static Cookie findCookieByName( Cookie[] c, String jsessionid) {
        if(c!=null&&c.length>0){
            for (Cookie cookie : c) {
                if(jsessionid.equals(cookie.getName())){
                    return cookie;
                }
            }
        }
        return null;
    }
}
