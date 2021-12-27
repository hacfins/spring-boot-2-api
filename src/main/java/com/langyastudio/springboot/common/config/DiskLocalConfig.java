package com.langyastudio.springboot.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Disk 配置项
 */
@Data
@ConfigurationProperties(prefix = "langyastudio.disk.local")
public class DiskLocalConfig
{
    private String       root;
    private String       winRoot;
    private String       rootFile;
    private String       rootMts;
    private String       rootTmp;
    private Integer      browseImgMaxSize;
    private Integer      cvtImgMaxSize;
    private Integer      maxSize;
    private Boolean      allowEmpty;
    private List<String> allowTypes;
}
