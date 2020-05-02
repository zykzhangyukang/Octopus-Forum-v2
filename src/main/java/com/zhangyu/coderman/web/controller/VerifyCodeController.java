package com.zhangyu.coderman.web.controller;

import com.zhangyu.coderman.web.utils.CreateVerifyCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by zhangyukang on 2019/12/19 17:57
 */
@Slf4j
@Controller
@RequestMapping("/verifyCode")
public class VerifyCodeController {



    /**
     * 生成System登入的验证码
     */
    @RequestMapping(value = "/LoginImageCode",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public void sysLoginImageCode(HttpServletResponse response, HttpSession session) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            CreateVerifyCode createVerifyCode = new CreateVerifyCode();
            String code = createVerifyCode.getCode();
            session.setAttribute("loginCode",code);
            BufferedImage buffImg = createVerifyCode.getBuffImg();
            ImageIO.write(buffImg,"PNG",outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成注册的验证码
     */
    @RequestMapping(value = "/RegImageCode",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public void createRegCode(HttpServletResponse response, HttpSession session) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            CreateVerifyCode createVerifyCode = new CreateVerifyCode();
            String code = createVerifyCode.getCode();
            session.setAttribute("regCode",code);
            BufferedImage buffImg = createVerifyCode.getBuffImg();
            ImageIO.write(buffImg,"PNG",outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 生成后台登入的验证码
     */
    @RequestMapping(value = "/createAdminLoginCode",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public void createAdminLoginCode(HttpServletResponse response, HttpSession session) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            CreateVerifyCode createVerifyCode = new CreateVerifyCode();
            String code = createVerifyCode.getCode();
            session.setAttribute("adminLoginCode",code);
            BufferedImage buffImg = createVerifyCode.getBuffImg();
            ImageIO.write(buffImg,"PNG",outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
