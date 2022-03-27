package com.langyastudio.edu.admin.common.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 资源配置
 *
 * @author jiangjiaxiong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "langyastudio.disk.pms")
public class PmsConf implements Serializable
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
