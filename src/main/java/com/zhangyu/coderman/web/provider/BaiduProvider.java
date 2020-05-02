package com.zhangyu.coderman.web.provider;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhangyu.coderman.web.dto.BaiduAccessTokenDTO;
import com.zhangyu.coderman.web.dto.BaiduUser;
import com.zhangyu.coderman.web.utils.WebUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
@Component
public class BaiduProvider {
    /**
     *  获取 Bai du的授权码
     * @param :数据传输对象,封装网络传输的数据
     * @return
     */
    public static String getAccessToken(BaiduAccessTokenDTO accessTokenDTO) {
        OkHttpClient client = new OkHttpClient();
        String urlString = "https://openapi.baidu.com/oauth/2.0/token?grant_type=" + accessTokenDTO.getGrant_type() +
                "&code=" + accessTokenDTO.getCode() + "&client_id=" + accessTokenDTO.getClient_id() + "&client_secret=" +
                accessTokenDTO.getClient_secret() + "&redirect_uri=" + accessTokenDTO.getRedirect_uri();
        Request request = new Request.Builder().url(urlString).get().build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            String accessToken = JSON.parseObject(string).getString("access_token");
            return accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取百度用户的信息
     * @param accessToken
     * @return
     */
    public static BaiduUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://openapi.baidu.com/rest/2.0/passport/users/getInfo?access_token=" + accessToken).build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            BaiduUser baiduUser = JSON.parseObject(string, BaiduUser.class);
            baiduUser.setHeadImg(getHeadImg(baiduUser.getPortrait()));
            return baiduUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回百度用户的头像
     * @return
     */
    public static String getHeadImg(String portrait){
          return  " http://tb.himg.baidu.com/sys/portraitn/item/"+portrait;
    }


    /**
     * 返回用户的地址
     */
    public static String getUserLoaction(String accessToken, HttpServletRequest httpServletRequest){
        OkHttpClient client = new OkHttpClient();
        String urlString = "https://openapi.baidu.com/rest/2.0/iplib/query?amp;access_token="+accessToken+"&ip="+ WebUtils.getIpAddr(httpServletRequest)+"";
        Request request = new Request.Builder().url(urlString).get().build();
        try {
            Response response = client.newCall(request).execute();
            String location = response.body().string();
            return location;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
