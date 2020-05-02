package com.zhangyu.coderman.web.intercepter;

import com.zhangyu.coderman.admin.intercepter.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Autowired
    private PublishInterceptor publishInterceptor;

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**").order(2);

        registry.addInterceptor(publishInterceptor)
                .addPathPatterns("/publish/*").order(3);

        //后台登入拦截器
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/doLogin").order(1);


    }
}
