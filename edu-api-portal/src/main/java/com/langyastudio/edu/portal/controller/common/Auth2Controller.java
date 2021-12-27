package com.langyastudio.edu.portal.controller.common;

import com.langyastudio.edu.common.anno.LogField;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.common.anno.InValue;
import com.langyastudio.edu.portal.service.Auth2Service;
import com.langyastudio.edu.common.data.ResultInfo;
import lombok.extern.log4j.Log4j2;
import me.zhyd.oauth.model.AuthCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.Map;

/**
 * 第三方登录
 */
@Log4j2
@Validated
@RestController
@RequestMapping("/portal/auth2")
public class Auth2Controller
{
    @Autowired
    Auth2Service auth2Service;

    /*-------------------------------------------------------------------------------------------------------------- */
    // JsSDK
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 检测授权状态
     *
     * @param url 需要分享的url地址
     *
     * @return
     */
    @GetMapping("/jssdk_sign")
    public ResultInfo JsSDKSign(@Valid @Size(min = 6) String url)
    {
        return ResultInfo.data(auth2Service.jsSDKSign(url));
    }

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
    @GetMapping("/checkstatus")
    public ResultInfo checkStatus(@Valid @Size(min = 32, max = 32) String key)
    {
        return ResultInfo.data(auth2Service.checkStatus(key));
    }

    /**
     * 获取授权链接
     *
     * @param provider 服务商，如weixin、dingding
     * @param authUrl  ui回调地址
     * @param isLogin  是否是登录
     *
     * @return
     */
    @GetMapping("/url")
    public ResultInfo getAuthUrl(@Valid @InValue({"dingding", "weixin"}) String provider,
                                 @Valid @RequestParam(value = "ui_url") String authUrl,
                                 @Valid @RequestParam(value = "is_login", defaultValue = Define.YES) @InValue Integer isLogin)
    {
        //个人中心绑定第三方登录
        String userName = null;
        if (Define.NOI.equals(isLogin))
        {
            userName = auth2Service.getUserName(true);
        }

        return ResultInfo.data(auth2Service.getAuthUrl(userName, provider, authUrl, isLogin));
    }

    /**
     * 用户在确认第三方平台授权（登录）后， 第三方平台会重定向到该地址，并携带code、state等参数
     *
     * @param callback 第三方回调时的入参
     */
    @GetMapping("/callback/{provider}")
    public void callback(@PathVariable String provider, AuthCallback callback, HttpServletResponse response) throws IOException
    {
        String redirectUrl = auth2Service.callback(provider, callback);
        response.sendRedirect(redirectUrl);
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // 绑定与解绑
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 是否绑定
     *
     * @param provider 第三方类型
     *
     * @return YES or NO
     */
    @GetMapping("/is_bind")
    public ResultInfo isBind(@Valid @InValue({"dingding", "weixin"}) String provider)
    {
        //need login
        String userName = auth2Service.getUserName(true);

        Integer isBind = auth2Service.isBind(userName, provider);
        return ResultInfo.data(Map.of("exist", isBind));
    }

    /**
     * 删除绑定
     *
     * @param provider 第三方类型
     *
     * @return YES or NO
     */
    @GetMapping("/del_bind")
    @LogField("删除登录绑定")
    public ResultInfo delBind(@Valid @InValue({"dingding", "weixin"}) String provider)
    {
        //need login
        String userName = auth2Service.getUserName(true);

        auth2Service.delBind(userName, provider);
        return ResultInfo.data();
    }

    /**
     * 登录绑定
     *
     * @param key  唯一标识符
     * @param name 用户名or手机号
     * @param pwd  密码
     */
    @GetMapping("/login_bind")
    public ResultInfo loginBind(@Valid @Size(min = 32, max = 32) String key,
                                @Valid @Size(min = 2, max = 20) String name,
                                @Valid @Size(min = 6, max = 40) String pwd)
    {
        auth2Service.loginBind(key, name, pwd);
        return ResultInfo.data();
    }

    /**
     * 自动注册并绑定
     *
     * @param key 唯一标识符
     */
    @GetMapping("/auto_bind")
    public ResultInfo autoBind(@Valid @Size(min = 32, max = 32) String key)
    {
        auth2Service.autoBind(key);
        return ResultInfo.data();
    }
}
