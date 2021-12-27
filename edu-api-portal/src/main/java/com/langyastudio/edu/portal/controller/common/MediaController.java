package com.langyastudio.edu.portal.controller.common;

import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.portal.common.service.ImgService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 资源访问
 *
 * @author langyastudio
 */
@Log4j2
@Validated
@RestController
public class MediaController
{
    @Autowired
    ImgService imgService;

    /**
     * 播放-数据
     *
     * @param request  请求
     * @param response 响应
     *
     * http://192.168.123.100:8511/media/2021/09/30/20210930143358019608114.png?x-oss-process=style/resize-400
     */
    @GetMapping(path = "/media/**")
    public void playMedia(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(value = "x-oss-process", required = false) String process)
    {
        String relativePath = request.getRequestURI();
        if (StrUtil.isBlankIfStr(relativePath) || relativePath.length() < 10)
        {
            throw new MyException(EC.ERROR_PARAM_EXCEPTION);
        }

        // /media/**
        relativePath = relativePath.substring(7);

        //resize
        int width = 0;
        if(StrUtil.isNotBlank(process) && process.length() > 13)
        {
            process = process.substring(13);
            try
            {
                width = Integer.parseInt(process);
            }
            catch (NumberFormatException e)
            {

            }
        }

        imgService.resize(request, response, relativePath, width, width);
    }
}
