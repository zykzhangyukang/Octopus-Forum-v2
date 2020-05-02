package com.zhangyu.coderman.web.controller;

import com.zhangyu.coderman.web.dto.FileDTO;
import com.zhangyu.coderman.web.provider.OssUploadImgProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileController {

    @Autowired
    private OssUploadImgProvider ossUploadImgProvider;

    /**
     * 上传图片，返回图片链接
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/question/fileupload",method = RequestMethod.POST)
    public FileDTO uploadImg(HttpServletRequest request) throws IOException {
        MultipartRequest multipartRequest= (MultipartRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        String url=ossUploadImgProvider.UploadFile(file.getInputStream(),file.getContentType(),file.getOriginalFilename());
        FileDTO fileDTO = new FileDTO();
        fileDTO.setMessage("上传成功");
        fileDTO.setSuccess(1);
        fileDTO.setUrl(url);
        return fileDTO;
    }


}
