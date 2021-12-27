package com.langyastudio.edu.portal.common.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 图像服务
 */
public interface ImgService
{
    /**
     * 文件上传到图像服务器
     *
     * @param file 文件
     *
     * @return
     */
    String upload(@NotNull MultipartFile file);

    /**
     * 缩放图片
     *
     * @param request      HttpServletRequest
     * @param response     HttpServletResponse
     * @param relativePath 本地相对路径
     */
    void resize(HttpServletRequest request, HttpServletResponse response, String relativePath, int width, int height);

    /**
     * 图像文件在线裁剪
     *
     * @param imgPath 图像文件全路径
     * @param sx      左上角x坐标
     * @param sy      左上角y坐标
     * @param width   宽度
     * @param height  高度
     *
     * @return
     */
    String crop(String imgPath, Integer sx, Integer sy, Integer width, Integer height);
}
