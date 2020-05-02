package com.zhangyu.coderman.web.provider;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhangyu.coderman.web.dto.QQAccessTokenDTO;
import com.zhangyu.coderman.web.dto.QQUser;
import com.zhangyu.coderman.web.dto.QQopenIdObj;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class QQProvider {

    /**
     * 获取access_token
     * @param qqAccessTokenDTO
     * @return
     */
    public static String getAccessToken(QQAccessTokenDTO qqAccessTokenDTO) {
        OkHttpClient client = new OkHttpClient();
        String urlString = "https://graph.qq.com/oauth2.0/token?grant_type=" + qqAccessTokenDTO.getGrant_type() +
                "&code=" + qqAccessTokenDTO.getCode() + "&client_id=" + qqAccessTokenDTO.getClient_id() + "&client_secret=" +
                qqAccessTokenDTO.getClient_secret() + "&redirect_uri=" + qqAccessTokenDTO.getRedirect_uri();
        Request request = new Request.Builder().url(urlString).get().build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            //access_token=8A43F697A44B755E9F812D8BC0C4F2D0&expires_in=7776000&refresh_token=B3AEE7FDE1171B68692AC9B336BBD700
            System.out.println(string);
            return string.split("&")[0];//access_token=8A43F697A44B755E9F812D8BC0C4F2D0
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 通过Access Token获取openid
     * @param accessToken
     * @return
     */
    public String getOpenId(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("  https://graph.qq.com/oauth2.0/me?" + accessToken).build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            QQopenIdObj openIdObj = new QQopenIdObj(string);
            return  openIdObj.getOpenid();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取qq用户信息
     * @param openId
     * @param qq_clientId
     * @param accessToken
     * @return
     */
    public QQUser getUserInfo(String openId, String qq_clientId, String accessToken) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://graph.qq.com/user/get_user_info?"+accessToken+"&oauth_consumer_key="+qq_clientId+"&openid="+openId+"").build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            System.out.println(string);
            QQUser qqUser = JSON.parseObject(string, QQUser.class);
            return qqUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
