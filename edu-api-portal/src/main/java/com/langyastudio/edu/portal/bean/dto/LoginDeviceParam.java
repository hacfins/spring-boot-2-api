package com.langyastudio.edu.portal.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 设备登录
 *
 * @author jiangjiaxiong
 */
@Data
@AllArgsConstructor
public class LoginDeviceParam
{
    /**
     * 签名后的值
     */
    @NotBlank
    private String signature;

    /**
     * 签名方法 hmac-sha1
     */
    @NotBlank
    private String signatureMethod;
    /**
     * guid 防止截获攻击
     */
    @NotBlank
    private String signatureNonce;
    /**
     * 版本 1.0
     */
    @NotBlank
    private String signatureVersion;
    /**
     * access_key
     */
    @NotBlank
    private String accessKey;
    /**
     * 日期 防止截获攻击
     */
    @NotBlank
    private String timestamp;
    /**
     * 格式 json
     */
    @NotBlank
    private String format;
}
