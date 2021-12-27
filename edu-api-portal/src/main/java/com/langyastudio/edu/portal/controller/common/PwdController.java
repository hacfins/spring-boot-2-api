package com.langyastudio.edu.portal.controller.common;

import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.anno.LogField;
import com.langyastudio.edu.portal.bean.dto.*;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.common.service.AliSmsService;
import com.langyastudio.edu.portal.common.service.MailService;
import com.langyastudio.edu.portal.service.PwdService;
import com.langyastudio.edu.portal.bean.vo.TokenVO;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.ResultInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录注册找回密码
 */
@Log4j2
@Validated
@RestController
@RequestMapping("/portal/common/pwd")
public class PwdController
{
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    PwdService pwdService;

    @Autowired
    AliSmsService aliSmsService;
    @Autowired
    MailService   mailService;

    /*-------------------------------------------------------------------------------------------------------------- */
    // 登录注册
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 用户注册
     *
     * @param registerParam 注册参数
     *
     * @return
     */
    @PostMapping("/register")
    public ResultInfo register(@Valid @RequestBody RegisterParam registerParam)
    {
        //1.0 检测校验码是否有效
        if (!aliSmsService.checkValid(registerParam.getName(), registerParam.getVerifyCode(), Define.VERITY_REGISTER))
        {
            return ResultInfo.data(EC.ERROR_VERIFY_ERROR);
        }

        //2.0 用户注册
        pwdService.register(registerParam);

        //3.0 删除校验码
        aliSmsService.del(registerParam.getName(), Define.VERITY_REGISTER);

        return ResultInfo.data();
    }

    /**
     * 用户登录
     *
     * @param loginParam 登录参数
     *
     * @return
     */
    @PostMapping("/login")
    public ResultInfo login(@Validated @RequestBody LoginParam loginParam)
    {
        String token = pwdService.login(loginParam.getUserName(), loginParam.getPwd());
        if (token == null)
        {
            return ResultInfo.data(EC.ERROR_USER_PASSWORD_INCORRECT);
        }

        return ResultInfo.data(new TokenVO(token, tokenHead));
    }

    /**
     * 手机号登录/注册
     *
     * @param verifyParam 注册参数
     *
     * @return
     */
    @PostMapping("/login_sms")
    public ResultInfo loginSms(@Valid @RequestBody LoginVerifyParam verifyParam)
    {
        //1.0 检测校验码是否有效
        if (!aliSmsService.checkValid(verifyParam.getName(), verifyParam.getVerifyCode(), Define.VERITY_LOGIN))
        {
            return ResultInfo.data(EC.ERROR_VERIFY_ERROR);
        }

        //2.0 用户登录
        String token = pwdService.loginSms(verifyParam);
        if (token == null)
        {
            return ResultInfo.data(EC.ERROR_USER_PASSWORD_INCORRECT);
        }

        //3.0 删除校验码
        aliSmsService.del(verifyParam.getName(), Define.VERITY_LOGIN);

        return ResultInfo.data(new TokenVO(token, tokenHead));
    }

    /**
     * 设备登录
     *
     * @param request 登录参数(header 传递)
     *
     * @return
     */
    @PostMapping("/login_device")
    public ResultInfo loginDevice(HttpServletRequest request)
    {
        String signature        = request.getHeader("signature");
        String signatureMethod  = request.getHeader("signaturemethod");
        String signatureNonce   = request.getHeader("signaturenonce");
        String signatureVersion = request.getHeader("signatureversion");
        String accessKey        = request.getHeader("accesskey");
        String timestamp        = request.getHeader("timestamp");
        String format           = request.getHeader("format");

        if (StrUtil.isNullOrUndefined(signature) || StrUtil.isNullOrUndefined(signatureMethod) ||
                StrUtil.isNullOrUndefined(signatureNonce) || StrUtil.isNullOrUndefined(signatureVersion) ||
                StrUtil.isNullOrUndefined(accessKey) || StrUtil.isNullOrUndefined(timestamp) ||
                StrUtil.isNullOrUndefined(format))
        {
            return ResultInfo.data(EC.ERROR_PARAM_NOT_NULL);
        }
        
        LoginDeviceParam deviceParam = new LoginDeviceParam(signature, signatureMethod, signatureNonce,
                                                            signatureVersion, accessKey, timestamp, format);

        String token = pwdService.loginDevice(deviceParam);
        if (token == null)
        {
            return ResultInfo.data(EC.ERROR_USER_PASSWORD_INCORRECT);
        }

        return ResultInfo.data(new TokenVO(token, tokenHead));
    }

    /**
     * 刷新token
     *
     * @param request 请求
     *
     * @return
     */
    @PostMapping("/refresh_token")
    public ResultInfo refreshToken(HttpServletRequest request)
    {
        //1.0 need Token
        String userName = pwdService.getUserName(true);

        String token        = request.getHeader(tokenHeader);
        String refreshToken = pwdService.refreshToken(token);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);

        return ResultInfo.data(tokenMap);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    public ResultInfo logout(HttpServletRequest request)
    {
        //1.0 need Token
        String userName = pwdService.getUserName(true);

        //获取token
        String token = request.getHeader(tokenHeader);

        pwdService.logout(token, userName);
        return ResultInfo.data();
    }

    /**
     * 手机号找回密码
     *
     * @param findPwdParam 注册参数
     *
     * @return
     */
    @PostMapping("/findpwd")
    @LogField("手机号找回密码")
    public ResultInfo findPwd(@Valid @RequestBody FindPwdParam findPwdParam)
    {
        //1.0 检测校验码是否有效
        if (!aliSmsService.checkValid(findPwdParam.getName(), findPwdParam.getVerifyCode(), Define.VERITY_FINDPWD))
        {
            return ResultInfo.data(EC.ERROR_VERIFY_ERROR);
        }

        //2.0 用户注册
        pwdService.findPwd(findPwdParam);

        //3.0 删除校验码
        aliSmsService.del(findPwdParam.getName(), Define.VERITY_FINDPWD);

        return ResultInfo.data();
    }
}
