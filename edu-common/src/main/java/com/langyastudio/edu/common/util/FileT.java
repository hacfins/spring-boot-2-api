package com.langyastudio.edu.common.util;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.entity.FileType;
import com.langyastudio.edu.common.exception.MyException;
import lombok.Cleanup;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FileT
{
    /**
     * 文件上传
     *
     * @param file     上传的文件信息
     * @param saveDir  存储的文件路径
     * @param fileName 存储的文件名称
     * @param isUseYmd 是否使用年月日子目录
     *
     * @return 相对路径-相对于上传根路径的完整路径及文件名
     *
     * @throws MyException
     */
    public static String upload(@NotNull MultipartFile file, String saveDir,
                                String fileName, boolean isUseYmd) throws MyException
    {
        if (file.isEmpty() || file.getSize() <= 0)
        {
            throw new MyException(EC.ERROR_PARAM_FILE_EMPTY);
        }

        //1.0 文件名
        //扩展名
        String extName = FileUtil.extName(file.getOriginalFilename());
        if (!StrUtil.isEmptyIfStr(extName))
        {
            extName = "." + extName;
        }

        if (StrUtil.isEmptyIfStr(fileName))
        {
            //文件名称 年月日时分秒毫秒+6位随机码
            fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) +
                    RandomUtil.randomNumbers(6) + extName;
        }

        //2.0 文件相对路径+名称
        String filePath = fileName;
        if (isUseYmd)
        {
            filePath = new SimpleDateFormat("yyyy/MM/dd/").format(new Date()) + fileName;
        }

        File fileFullPath = new File(saveDir, filePath);

        //将上传文件保存到目标文件目录
        // 判断路径是否存在，不存在则新创建一个
        try
        {
            if (!fileFullPath.getParentFile().exists())
            {
                fileFullPath.getParentFile().mkdirs();
            }
        }
        catch (SecurityException securityException)
        {
            throw new MyException(EC.ERROR_FILE_NOPERMISSON);
        }

        try
        {
            file.transferTo(fileFullPath);
        }
        catch (IOException | IllegalStateException e)
        {
            throw new MyException(EC.ERROR_FILE_SAVE_ERROR);
        }

        return filePath;
    }

    public static void download(HttpServletRequest request, HttpServletResponse response,
                                InputStream inputStream, String fileName, int expire, boolean isDownload)
    {
        if (Objects.isNull(inputStream))
        {
            throw new MyException(EC.ERROR_COMMON_RECORD_NOT_EXIST);
        }

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Description", "File Transfer");
        response.setHeader("Accept-Ranges", "bytes");

        //cors
        String uiDomain = request.getHeader("origin");
        if(StrUtil.isNotBlank(uiDomain))
        {
            response.addHeader("Access-Control-Allow-Origin", uiDomain);
        }

        if (isDownload)
        {
            response.setHeader("Content-Disposition", "attachment;fileName=" +
                    java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        }

        byte[] buffer = new byte[1024 * 8];
        try
        {
            @Cleanup BufferedInputStream bufferedInputStream = null;

            //输出流
            @Cleanup OutputStream outputStream = null;

            outputStream = response.getOutputStream();

            bufferedInputStream = new BufferedInputStream(inputStream);
            int len = bufferedInputStream.read(buffer);
            while (len != -1)
            {
                outputStream.write(buffer);
                response.flushBuffer();

                len = bufferedInputStream.read(buffer);
            }
        }
        catch (Exception e)
        {
            throw new MyException(e.getMessage());
        }
    }

    /**
     * 浏览器下载文件
     *
     * @param response 响应
     * @param filePath 全路径
     * @param fileName 文件名
     */
    public static void download(HttpServletRequest request, HttpServletResponse response,
                                String filePath, String fileName, int expire, boolean isDownload)
    {
        File file = new File(filePath);
        if (!file.exists())
        {
            throw new MyException(EC.ERROR_COMMON_RECORD_NOT_EXIST);
        }

        long   fileSize    = FileUtil.size(file);
        String contentType = getContentType(filePath);
        if (!StrUtil.isBlankIfStr(contentType))
        {
            response.setContentType(contentType);
        }
        else
        {
            response.setContentType("application/octet-stream");
        }
        response.setCharacterEncoding("UTF-8");

        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Description", "File Transfer");
        response.setHeader("Content-Length", Long.toString(fileSize));
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Accept-Length", Long.toString(fileSize));

        //set cache and expire
        response.setHeader("Cache-Control",
                           "public, must-revalidate, post-check=0, pre-check=0" + ",max-age=" + expire);

        LocalDateTime dateTime = LocalDateTimeUtil.of(file.lastModified());
        response.setDateHeader("Last-Modified", dateTime.toEpochSecond(ZoneOffset.ofHours(8)) * 1000);
        response.setDateHeader("Expires", dateTime.plusSeconds(expire).toEpochSecond(ZoneOffset.ofHours(8)) * 1000);

        //cors
        String uiDomain = request.getHeader("origin");
        if(StrUtil.isNotBlank(uiDomain))
        {
            response.addHeader("Access-Control-Allow-Origin", uiDomain);
        }

        if (isDownload)
        {
            response.setHeader("Content-Disposition", "attachment;fileName=" +
                    java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        }

        byte[] buffer = new byte[1024 * 8];
        try
        {
            @Cleanup FileInputStream     fileInputStream     = null;
            @Cleanup BufferedInputStream bufferedInputStream = null;

            //输出流
            @Cleanup OutputStream outputStream = null;

            outputStream = response.getOutputStream();

            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            int len = bufferedInputStream.read(buffer);
            while (len != -1)
            {
                outputStream.write(buffer);
                response.flushBuffer();

                len = bufferedInputStream.read(buffer);
            }
        }
        catch (Exception e)
        {
            throw new MyException(e.getMessage());
        }
    }

    /**
     * 获取文件类型
     *
     * @param filePath 文件的完整路径
     *
     * @return Integer FileType
     */
    public static Byte getFileType(String filePath)
    {
        String extName = FileUtil.extName(filePath);

        if (StrUtil.isEmptyIfStr(extName))
        {
            return FileType.OTHER;
        }
        extName = extName.toLowerCase();

        //视频
        List<String> videoTypes = Arrays.asList("mov",
                                                "mp4",
                                                "mpe",
                                                "mpeg",
                                                "mpg",
                                                "m4v",
                                                "mkv",
                                                "m4d",
                                                "3gp",
                                                "avi",
                                                "flv",
                                                "wmv",
                                                "asf",
                                                "rmvb",
                                                "rm",
                                                "ogv",
                                                "mts",
                                                "m2ts",
                                                "ts",

                                                "asx",
                                                "dav",
                                                "dv",
                                                "mxf",
                                                "vob",
                                                "vp6",
                                                "webm");
        if (videoTypes.contains(extName))
        {
            return FileType.VIDEO;
        }

        //图片
        List<String> picTypes = Arrays.asList(
                "jpeg",
                "jpg",
                "jpe",
                "bmp",
                "png",
                "gif",
                "tif",
                "tiff",
                "ico",
                "tga",
                "pcx",
                "exif",
                "xpm",
                "jif"
        );
        if (picTypes.contains(extName))
        {
            return FileType.PICTURE;
        }

        //文档
        List<String> dockTypes = Arrays.asList(
                "doc",
                "docx",
                "ppt",
                "pptx",
                "xls",
                "xlsx",
                "rtf",
                "odt",
                "ods",
                "pdf",
                "txt",
                "wps"
        );
        if (dockTypes.contains(extName))
        {
            return FileType.TEXT;
        }

        //音频
        List<String> audioTypes = Arrays.asList(
                "mp3",
                "aac",
                "aif",
                "aiff",
                "oga",
                "ogg",
                "wav",
                "wma",
                "m4a",
                "flac",
                "ac3",

                "amr",
                "ape",
                "au",
                "mmf",
                "mp2",
                "tak",
                "tta",
                "wv"
        );
        if (audioTypes.contains(extName))
        {
            return FileType.AUDIO;
        }

        return FileType.OTHER;
    }

    /**
     * 计算文件32位md5值
     *
     * @param fis 文件InputStream
     * @param len 文件前多少字节
     *
     * @return md5
     *
     * @throws Exception
     */
    public static String md5HashCode32(InputStream fis, int len) throws Exception
    {
        if (len > 1024 * 1024 * 5)
        {
            throw new Exception("检验大小不能超过5M");
        }

        StringBuilder hexValue;
        try
        {
            long size = fis.available();
            if (len > size)
            {

                len = Math.toIntExact(size);
            }

            //拿到一个MD5转换器,如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256
            MessageDigest md;

            md = MessageDigest.getInstance("MD5");

            //分多次将一个文件读入，对于大型文件而言，比较推荐这种方式，占用内存比较少。
            byte[] buffer = new byte[len];

            fis.read(buffer, 0, len);

            md.update(buffer, 0, len);

            fis.close();

            //转换并返回包含16个元素字节数组,返回数值范围为-128到127
            byte[] md5Bytes = md.digest();

            hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes)
            {
                int val = ((int) md5Byte) & 0xff;//解释参见最下方
                if (val < 16)
                {
                    /*
                      如果小于16，那么val值的16进制形式必然为一位，
                      因为十进制0,1...9,10,11,12,13,14,15 对应的 16进制为 0,1...9,a,b,c,d,e,f;
                      此处高位补0。
                     */
                    hexValue.append("0");
                }

                //这里借助了Integer类的方法实现16进制的转换
                hexValue.append(Integer.toHexString(val));
            }

        }
        catch (NoSuchAlgorithmException | IOException e)
        {
            throw new Exception(e.getMessage());
        }

        return hexValue.toString();
    }

    public static String getContentType(String filename)
    {
        String type = null;
        Path   path = Paths.get(filename);
        try
        {
            type = Files.probeContentType(path);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return type;
    }
}


