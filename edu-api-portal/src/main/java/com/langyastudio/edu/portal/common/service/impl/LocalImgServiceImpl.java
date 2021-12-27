package com.langyastudio.edu.portal.common.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.FileT;
import com.langyastudio.edu.common.util.Tool;
import com.langyastudio.edu.portal.common.conf.LocalImgConf;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.common.service.ImgService;
import net.coobird.thumbnailator.Thumbnails;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 本地图像服务
 */
@Service
@ConditionalOnProperty(value = "langyastudio.storage.imgs.type", havingValue = "local", matchIfMissing = true)
public class LocalImgServiceImpl implements ImgService
{
    @Autowired
    LocalImgConf imgConf;

    /**
     * 文件上传到本地
     *
     * @param file 文件
     *
     * @return
     */
    @Override
    public String upload(@NotNull MultipartFile file)
    {
        //存储目录
        String rootDir = imgConf.getRoot();
        if (Tool.isWinOs())
        {
            rootDir = rootDir.replace(imgConf.getRoot(), imgConf.getWinRoot());
        }

        //上传分片文件
        return this.getPlayUrl(FileT.upload(file, rootDir, null, true));
    }

    /**
     * 文件在线裁剪
     *
     * @param imgPath 文件全路径 http://192.168.123.100:8511/media/2021/10/09/20211009110803495884687.png
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
        //1.0 根据imgPath获取fullPath
        String[] strPaths = imgPath.split(Define.RESOURCE_MEDIA_NAME + "/");
        if (Objects.isNull(strPaths) || strPaths.length != 2)
        {
            throw new MyException(EC.ERROR_FILE_PATH_ERROR);
        }
        String fullPath = strPaths[1];
        //windows Os 运行需要进行路径转换
        if (Tool.isWinOs())
        {
            fullPath = imgConf.getWinRoot() + fullPath;
        }
        else
        {
            fullPath = imgConf.getRoot() + fullPath;
        }
        if (!FileUtil.isFile(fullPath))
        {
            throw new MyException(EC.ERROR_FILE_NOTEXIST_PERMISSION);
        }

        //2.0 生成裁剪图片
        String cropPath = FileUtil.getParent(fullPath, 1) + File.separator;

        //年月日时分秒毫秒+6位随机码
        StringBuilder fileName = new StringBuilder();
        fileName.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()))
                .append(RandomUtil.randomNumbers(6))
                .append(".")
                .append(FileUtil.extName(fullPath));

        cropPath += fileName.toString();
        try
        {
            Thumbnails.of(fullPath)
                    .scale(1)
                    .sourceRegion(sx, sy, width, height)
                    .toFile(cropPath);
        }
        catch (IOException e)
        {
            throw new MyException(EC.ERROR_FILE_NOTEXIST_PERMISSION);
        }

        return this.getPlayUrl(cropPath);
    }

    /**
     * 缩放图片
     *
     * @param request      HttpServletRequest
     * @param response     HttpServletResponse
     * @param relativePath 本地相对路径
     */
    @Override
    public void resize(HttpServletRequest request, HttpServletResponse response, String relativePath, int width,
                       int height)
    {
        //1.0 源文件路径
        String fullPath = relativePath;
        //windows Os 运行需要进行路径转换
        if (Tool.isWinOs())
        {
            fullPath = imgConf.getWinRoot() + relativePath;
        }
        else
        {
            fullPath = imgConf.getRoot() + relativePath;
        }
        if (!FileUtil.isFile(fullPath))
        {
            throw new MyException(EC.ERROR_FILE_NOTEXIST_PERMISSION);
        }

        //2.0 缩放路径
        //xxxx-width.xxx
        if (width > 0)
        {
            StringBuilder resizePath = new StringBuilder(FileUtil.getParent(fullPath, 1));
            resizePath.append(File.separator)
                    .append(FileUtil.mainName(fullPath))
                    .append("_")
                    .append(width)
                    .append(".")
                    .append(FileUtil.extName(fullPath));

            //生成缩放图片
            if (!FileUtil.isFile(resizePath.toString()))
            {
                try
                {
                    Thumbnails.of(fullPath)
                            .size(width, width)
                            .toFile(resizePath.toString());
                }
                catch (IOException e)
                {
                    throw new MyException(EC.ERROR_FILE_NOTEXIST_PERMISSION);
                }
            }

            fullPath = resizePath.toString();
        }

        FileT.download(request, response, fullPath, FileUtil.getName(fullPath), 2592000, false);
    }

    /**
     * 获取访问文件的http地址
     *
     * @param filePath 全路径
     *
     * @return
     */
    private String getPlayUrl(String filePath)
    {
        //Todo: 使用mts自部署的服务
        HttpServletRequest request = Tool.getRequest();
        StringBuilder domain = new StringBuilder();
        domain.append(request.getScheme())
                .append("://" )
                .append(request.getServerName())
                .append( ":" )
                .append(request.getServerPort())
                .append("/")
                .append(Define.RESOURCE_MEDIA_NAME)
                .append("/");

        filePath = filePath.replace("\\", "/");
        if (Tool.isWinOs())
        {
            return domain + filePath.replace(imgConf.getWinRoot(), "");
        }
        return domain + filePath.replace(imgConf.getRoot(), "");
    }
}
