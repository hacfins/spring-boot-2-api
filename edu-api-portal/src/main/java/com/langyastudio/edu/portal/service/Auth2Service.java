package com.langyastudio.edu.portal.service;

import com.langyastudio.edu.portal.bean.vo.JsSDKSignVO;
import com.langyastudio.edu.portal.bean.vo.OauthCacheVO;
import me.zhyd.oauth.model.AuthCallback;

import java.util.Map;

/**
 * 第三方登录服务
 */
public interface Auth2Service extends BaseService
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // JsSDK
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 获取JS-SDK sign 签名信息
     *
     * @param url 要分享的URL地址
     *
     * @return
     */
    JsSDKSignVO jsSDKSign(String url);

    /*-------------------------------------------------------------------------------------------------------------- */
    // 扫码登录
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 检测授权状态
     *
     * @param key 存储与statue中，类似于唯一标识符
     *
     * @return
     */
    OauthCacheVO checkStatus(String key);

    /**
     * 获取授权地址
     *
     * @param provider 第三方类型
     * @param uiUrl    ui URL地址
     * @param isLogin  是否登录
     *
     * @return Map
     */
    Map<String, String> getAuthUrl(String userName, String provider, String uiUrl, Integer isLogin);

    /**
     * 回调接口
     *
     * @param provider     第三方类型
     * @param authCallback callback返回参数，如code、statue等
     *
     * @return 重定向地址
     */
    String callback(String provider, AuthCallback authCallback);

    /*-------------------------------------------------------------------------------------------------------------- */
    // 绑定与解绑
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 是否绑定
     *
     * @param userName 用户名
     * @param provider 第三方类型
     *
     * @return YES or NO
     */
    Integer isBind(String userName, String provider);

    /**
     * 删除绑定
     *
     * @param userName 用户名
     * @param provider 第三方类型
     *
     * @return YES or NO
     */
    Integer delBind(String userName, String provider);

    /**
     * 登录绑定
     *
     * @param key  唯一标识符
     * @param name 用户名or手机号
     * @param pwd  密码
     *
     * @return
     */
    Integer loginBind(String key, String name, String pwd);

    /**
     * 自动绑定
     *
     * @param key 唯一标识符
     *
     * @return
     */
    Integer autoBind(String key);
}
