package com.langyastudio.edu.common.util;

import cn.hutool.core.util.StrUtil;
import lombok.Cleanup;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件压缩工具
 */
public class ZipT
{
    /**
     * 批量文件压缩下载
     *
     * @param urlList      需要批量下载文件的链接地址列表
     * @param fileNameList 需要批量下载文件名列表
     * @param zipName      输出的压缩包名称(无后缀)
     * @param response     HttpServletResponse
     * @param request      HttpServletRequest
     */
    public static void downZip(List<String> urlList, List<String> fileNameList, String zipName,
                               HttpServletRequest request, HttpServletResponse response)
    {
        //响应头的设置
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Description", "File Transfer");
        response.setContentType("multipart/form-data");

        String downloadName = zipName + ".zip";

        //返回客户端浏览器的版本号、类型
        String agent = request.getHeader("USER-AGENT");
        try
        {
            //针对IE或者以IE为内核的浏览器：
            if (agent.contains("MSIE") || agent.contains("Trident"))
            {
                downloadName = java.net.URLEncoder.encode(downloadName, StandardCharsets.UTF_8);
            }
            else
            {
                //非IE浏览器的处理：
                downloadName = new String(downloadName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment;fileName=\"" + downloadName + "\"");

        //cors
        String uiDomain = request.getHeader("origin");
        if (StrUtil.isNotBlank(uiDomain))
        {
            response.addHeader("Access-Control-Allow-Origin", uiDomain);
        }

        //设置压缩流：直接写入response，实现边压缩边下载!!!
        ZipOutputStream zipOutputStream = null;
        try
        {
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));

            //设置压缩方法
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //循环将文件写入压缩流
        byte[] buffer = new byte[1024 * 8];
        int    len    = 0;
        for (int ix = 0; ix < urlList.size(); ix++)
        {
            try
            {
                String filename = fileNameList.get(ix);

                //添加ZipEntry，并ZipEntry中写入文件流
                zipOutputStream.putNextEntry(new ZipEntry(filename));

                @Cleanup FileInputStream     fileInputStream     = new FileInputStream(urlList.get(ix));
                @Cleanup BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                while ((len = bufferedInputStream.read(buffer)) != -1)
                {
                    zipOutputStream.write(buffer, 0, len);
                }
                zipOutputStream.closeEntry();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        //关闭流
        try
        {
            if (Objects.nonNull(zipOutputStream))
            {
                zipOutputStream.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
