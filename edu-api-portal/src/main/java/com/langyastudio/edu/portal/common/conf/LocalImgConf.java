package com.langyastudio.edu.portal.common.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 资源配置
 *
 * @author jiangjiaxiong
 */
@Data
@ConfigurationProperties(prefix = "langyastudio.storage.imgs")
public class LocalImgConf
{
    /**
     * 图像目录
     */
    private String root;
    /**
     * windows 图像目录
     */
    private String winRoot;
}
