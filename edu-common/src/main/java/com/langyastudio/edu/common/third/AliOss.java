package com.langyastudio.edu.common.third;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.ProcessObjectRequest;
import cn.hutool.core.io.FileUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.Tool;
import lombok.Cleanup;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Objects;

/**
 * 阿里云 Oss
 */
@Component
public class AliOss
{
    /**
     * 上传
     *
     * @param file MultipartFile
     *
     * @return objectName
     */
    public String upload(String accessKeyId, String accessKeySecret, String endPoint, String bucketName,
                         String dirPrefix,
                         @NotNull MultipartFile file)
    {
        if (file.isEmpty())
        {
            throw new MyException(EC.ERROR_PARAM_FILE_EMPTY);
        }

        //1.1 文件名称
        String objectName = this.getObjectName(dirPrefix, file.getOriginalFilename());

        //2.0 将上传文件保存到OSS
        OSS ossClient = null;
        try
        {
            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build("http://" + endPoint, accessKeyId, accessKeySecret);

            @Cleanup InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucketName, objectName, inputStream);
        }
        catch (IOException e)
        {
            throw new MyException(e.getMessage());
        }
        finally
        {
            // 关闭OSSClient
            if (Objects.nonNull(ossClient))
            {
                ossClient.shutdown();
            }
        }

        return this.getFileUrl(endPoint, bucketName, objectName);
    }

    /**
     * OSS图像在线裁剪
     *
     * @param accessKeyId     accesskey
     * @param accessKeySecret accesssecret
     * @param endPoint        oss-cn-hangzhou.aliyuncs.com
     * @param bucketName      edu-imgs-hacfin
     * @param imgPath         如https://[bucketName].[endPoint]/[dirPrefix]2021/04/20210430125938734160.png
     * @param sx              图像左上角x坐标
     * @param sy              图像左上角y坐标
     * @param width           图像宽度
     * @param height          图像高度
     *
     * @return
     */
    public String crop(String accessKeyId, String accessKeySecret, String endPoint, String bucketName, String dirPrefix,
                       String imgPath, Integer sx, Integer sy, Integer width, Integer height)
    {
        String targetImage = null;
        OSS    ossClient   = null;
        try
        {
            //1.0 创建OSSClient实例
            ossClient = new OSSClientBuilder().build("http://" + endPoint, accessKeyId, accessKeySecret);

            //2.1 根据imgPath获取ObjectPath
            String[] strPaths = imgPath.split(endPoint + "/");
            if (Objects.isNull(strPaths) || strPaths.length != 2)
            {
                throw new MyException(EC.ERROR_FILE_PATH_ERROR);
            }
            String objectPath = strPaths[1];

            //2.2 生成的图片路径 - 保存到当前Bucket
            targetImage = this.getObjectName(dirPrefix, FileUtil.getName(objectPath));

            //2.2 从坐标（x,y）开始，将图片裁剪为宽高width height px
            StringBuilder processStyle   = new StringBuilder();
            Formatter     styleFormatter = new Formatter(processStyle);
            String        styleType      = String.format("image/crop,w_%d,h_%d,x_%d,y_%d", width, height, sx, sy);
            styleFormatter.format("%s|sys/saveas,o_%s,b_%s", styleType,
                                  BinaryUtil.toBase64String(targetImage.getBytes()),
                                  BinaryUtil.toBase64String(bucketName.getBytes()));

            ProcessObjectRequest request = new ProcessObjectRequest(bucketName, objectPath, processStyle.toString());
            ossClient.processObject(request);
        }
        catch (Throwable e)
        {
            throw new MyException(e.getMessage());
        }
        finally
        {
            // 关闭OSSClient
            if (Objects.nonNull(ossClient))
            {
                ossClient.shutdown();
            }
        }

        return this.getFileUrl(endPoint, bucketName, targetImage);
    }

    /**
     * 获取下载url
     *
     * @param objectName 文件路径, 例如abc/efg/123.jpg
     *
     * @return Url
     */
    public String getFileUrl(String endPoint, String bucketName, String objectName)
    {
        return "https://" + bucketName + "." + endPoint + "/" + objectName;
    }

    /**
     * 下载
     *
     * @param objectName   从OSS下载文件时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
     * @param downloadName 下载名称
     */
    public void download(String accessKeyId, String accessKeySecret, String endPoint, String bucketName,
                         String objectName, String downloadName)
    {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build("http://" + endPoint, accessKeyId, accessKeySecret);

        // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);

        // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
        InputStream content = ossObject.getObjectContent();
        if (content != null)
        {
            HttpServletResponse response = Tool.getResponse();
            if (null != response)
            {
                // 设置强制下载不打开
                response.setContentType("application/force-download");

                // 设置文件名
                String dlName =
                        URLEncoder.encode(downloadName, StandardCharsets.UTF_8) + "." + FileUtil.extName(objectName);
                response.addHeader("Content-Disposition",
                                   "attachment;filename=" + dlName + ";filename*=UTF-8''" + dlName);

                byte[] buffer = new byte[1024];
                try
                {
                    OutputStream        outputStream        = response.getOutputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(content);

                    int i = bufferedInputStream.read(buffer);
                    while (i != -1)
                    {
                        outputStream.write(buffer, 0, i);
                        i = bufferedInputStream.read(buffer);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
                try
                {
                    content.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    private String getObjectName(String dirPrefix, String sorFileName)
    {
        String extName = FileUtil.extName(sorFileName);
        if (!StrUtil.isEmptyIfStr(extName))
        {
            extName = "." + extName;
        }

        //年月日时分秒+6位随机码
        String fileName =
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + RandomUtil.randomNumbers(6) + extName;

        //1.2 文件相对路径+名称
        String path = new SimpleDateFormat("yyyy/MM/").format(new Date());
        return dirPrefix + path + fileName;
    }
}
