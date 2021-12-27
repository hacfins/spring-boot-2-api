package com.langyastudio.edu.portal.common.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 第三方登录配置
 */
@Data
@ConfigurationProperties(prefix = "oauth")
public class OauthConf
{
    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 第三方授权列表
     */
    private Map<String, Object> provider;

    /**
     * 授权登录URL前缀
     * /auth2/authorization
     */
    private String authLoginUrlPrefix;

    /**
     * 重定向前缀
     * /portal/auth2/callback
     */
    private String redirectUrlPrefix;

    /**
     * api 域名地址
     * http://account-test.bogo365.net
     */
    private String domain;
}
