package com.langyastudio.edu.admin.common.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * yml 配置文件-aliyun-sms
 */
@Data
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliSmsConf
{
    private String accessKey;
    private String accessSecret;

    //signName templateCode
    /**
     * 注册
     */
    private Map<String, String> register;
    /**
     * 登录
     */
    private Map<String, String> login;
    /**
     * 找密码
     */
    private Map<String, String> findPwd;
    /**
     * 修改手机号
     */
    private Map<String, String> modifyPhone;
}
