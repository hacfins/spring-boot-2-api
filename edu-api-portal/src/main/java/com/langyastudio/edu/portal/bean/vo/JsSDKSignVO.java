package com.langyastudio.edu.portal.bean.vo;

import lombok.Data;

/**
 * weixin jssdk 签名
 *
 * @author langyastudio
 */
@Data
public class JsSDKSignVO
{
    /**
     * 返回url地址
     */
    private String url;
    /**
     * 公众号的唯一标识
     */
    private String appId;

    /**
     * 生成签名的时间戳
     */
    private String nonceStr;

    /**
     * 生成签名的随机串
     */
    private String timestamp;

    /**
     * 签名
     */
    private String signature;
}
