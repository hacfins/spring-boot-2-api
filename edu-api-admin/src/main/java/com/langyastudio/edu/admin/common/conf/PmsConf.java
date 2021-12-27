package com.langyastudio.edu.admin.common.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 资源配置
 *
 * @author jiangjiaxiong
 */
@Data
@ConfigurationProperties(prefix = "langyastudio.disk.pms")
public class PmsConf
{
    /**
     * 根目录
     */
    private String root;
    /**
     * windows 根目录
     */
    private String winRoot;

    /**
     * 文件分片合成目录，需要注意win与linux的切换
     */
    private String rootFile;
    /**
     * 文件转码目录，需要注意win与linux的切换
     */
    private String rootMts;
    /**
     * 文件分片上传临时目录，需要注意win与linux的切换
     */
    private String rootTmp;
    /**
     * 可直接浏览的最大图片大小
     */
    private Long   browseImgMaxSize;
    /**
     * 可转码的最大图片大小
     */
    private Long   cvtImgMaxSize;
}