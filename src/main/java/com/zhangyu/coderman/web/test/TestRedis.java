package com.zhangyu.coderman.web.test;

import com.zhangyu.coderman.web.utils.RedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestRedis {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testTOlist(){
        String listStr="[java, spring, 测试, 闲聊, css, springboot, 灌水, 更新, bug, 生活, linux, html, 公告, redis, Java, 其他, 注水, 分享, 建议, js, 问题, 通知, c#, 数据库, Html, Spring , java , 账号, 美女, 窝窝头]";
        String[] substring = listStr.split(",");
        List<String> strings = Arrays.asList(substring);
        System.out.println(strings.size());
    }
    @Test
    public void testredis(){
        System.out.println("redis");
        RedisTemplate redisTemplate=new RedisTemplate();
        redisTemplate.opsForValue().set("name","zhangyukang");
    }
    @Test
    public void testsetKey(){
        boolean set = redisUtils.set("name", "zhangyukang");
        System.out.println(set);
    }
    @Test
    public void saveList(){
        stringRedisTemplate.opsForValue().set("name","zhangyukang");
        //stringRedisTemplate.opsForList().leftPush()
    }

}
