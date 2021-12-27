package com.langyastudio.edu.portal.common.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * aliyun oss 配置
 */
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliOssConf
{
    private String accessKey;
    private String accessSecret;

    /**
     * oss对外服务的访问域名
     */
    private String endPoint;
    /**
     * oss的存储空间
     */
    private String bucketName;
    /**
     * 存储路径前缀
     */
    private String dirPrefix;
    /**
     * 上传文件大小(M)
     */
    private String maxSize;
    /**
     * 签名有效期(S)
     */
    private String policyExpire;
    /**
     * 文件上传成功后的回调地址
     */
    private String callback;
}
