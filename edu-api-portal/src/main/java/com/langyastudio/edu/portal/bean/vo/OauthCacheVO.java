package com.langyastudio.edu.portal.bean.vo;

import lombok.Data;

/**
 * 第三方登录的缓存信息
 */
@Data
public class OauthCacheVO
{
    /**
     * 唯一标识符
     */
    private String  key;
    /**
     * 类型，如dingding
     */
    private String  provider;
    /**
     * 是否是登录请求
     */
    private Integer isLogin;
    /**
     * ui回调地址
     */
    private String  uiUrl;
    /**
     * 状态码
     */
    private Integer statue;
    /**
     * 状态消息
     */
    private String  msg;
    /**
     * 登录成功的授权信息
     */
    private TokenVO tokenVO;
    /**
     * 用户名
     */
    private String  userName;

    //------------------------- 第三方服务获取到的信息 ---------------------//
    /**
     * 第三方open id号
     */
    private String openId;
    /**
     * 用户头像地址
     */
    private String avatar;
    /**
     * 用户头像地址
     */
    private String nickName;
}
