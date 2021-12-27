package com.langyastudio.edu.portal.common.service.impl;

import com.langyastudio.edu.common.third.AliOss;
import com.langyastudio.edu.portal.common.conf.AliOssConf;
import com.langyastudio.edu.portal.common.service.ImgService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 阿里云Oss服务
 */
@Service
@ConditionalOnProperty(value = "langyastudio.storage.imgs.type", havingValue = "aliyun")
public class AliOssImgServiceImpl implements ImgService
{
    @Autowired
    AliOssConf aliOssConf;

    @Autowired
    AliOss aliOss;

    /**
     * 文件上传到OSS
     *
     * @param file 文件
     *
     * @return
     */
    @Override
    public String upload(@NotNull MultipartFile file)
    {
        return aliOss.upload(aliOssConf.getAccessKey(), aliOssConf.getAccessSecret(), aliOssConf.getEndPoint(),
                             aliOssConf.getBucketName(), aliOssConf.getDirPrefix(), file);
    }

    /**
     * 缩放图片
     *
     * @param request      HttpServletRequest
     * @param response     HttpServletResponse
     * @param relativePath 本地相对路径
     */
    @Override
    public void resize(HttpServletRequest request, HttpServletResponse response, String relativePath, int width, int height)
    {

    }

    /**
     * OSS文件在线裁剪
     *
     * @param imgPath oss文件全路径
     * @param sx      左上角x坐标
     * @param sy      左上角y坐标
     * @param width   宽度
     * @param height  高度
     *
     * @return
     */
    @Override
    public String crop(String imgPath, Integer sx, Integer sy, Integer width, Integer height)
    {
        return aliOss.crop(aliOssConf.getAccessKey(), aliOssConf.getAccessSecret(), aliOssConf.getEndPoint(),
                           aliOssConf.getBucketName(), aliOssConf.getDirPrefix(),
                           imgPath, sx, sy, width, height);
    }
}
