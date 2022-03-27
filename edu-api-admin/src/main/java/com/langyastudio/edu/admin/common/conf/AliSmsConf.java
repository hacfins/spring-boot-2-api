package com.langyastudio.edu.admin.common.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * yml 配置文件-aliyun-sms
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliSmsConf implements Serializable
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
