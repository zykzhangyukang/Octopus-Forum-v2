package com.zhangyu.coderman.web.provider;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.*;
import com.zhangyu.coderman.web.dto.AccessTokenDTO;
import com.zhangyu.coderman.web.dto.GithubUser;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    /**
     *  获取 Github的授权码
     * @param accessTokenDTO:数据传输对象,封装网络传输的数据
     * @return
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO));
            Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token").post(body).build();
            try {
                Response response = client.newCall(request).execute();
                String string = response.body().string();
                return string;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }
    /**
     * 获取Github的用户信息
     * @param accessToken:传入的授权码
     * @return
     */
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?"+accessToken)
                .build();
        try {
        Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
