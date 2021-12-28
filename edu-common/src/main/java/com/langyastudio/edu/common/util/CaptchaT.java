package com.langyastudio.edu.common.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.exception.MyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 验证码
 */
@Component
public class CaptchaT
{
    final static String CACHE_PRE = "test_edu_captchat:";

    @Resource
    private RedisT redis;

    /**
     * 显示图片验证码
     */
    public void showImg(String sessionId) throws MyException
    {
        HttpServletRequest  req = Tool.getRequest();
        HttpServletResponse res = Tool.getResponse();

        if (req == null || res == null)
        {
            throw new MyException("未获取到请求和响应的信息");
        }

        try
        {
            // 自定义纯数字的验证码（随机4位数字，可重复）
            RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
            ShearCaptcha    captcha      = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);
            captcha .setGenerator(randomGenerator);
            //captcha.setGenerator(new MathGenerator());

            //验证码字符串保存到session
            String createText = captcha.getCode();
            if (StrUtil.isNotBlank(sessionId))
            {
                if (sessionId.length() < 32)
                {
                    throw new MyException("SessionId 不能少于32位");
                }

                redis.set(CACHE_PRE + "sessionid_" + sessionId, createText, 60 * 10);
            }
            else
            {
                HttpSession session = req.getSession();
                session.setAttribute("captcha", createText);
            }

            res.setHeader("Cache-Control", "no-store");
            res.setHeader("Pragma", "no-cache");
            res.setDateHeader("Expires", 0);

            ServletOutputStream responseOutputStream = res.getOutputStream();
            captcha.write(responseOutputStream);

            //关闭句柄
            responseOutputStream.flush();
            responseOutputStream.close();

        }
        catch (IllegalArgumentException | IOException e)
        {
            throw new MyException(e.getMessage());
        }
    }

    /**
     * 检查图片验证码是否正确
     */
    public void check(String verifyCode, String sessionId) throws MyException
    {
        HttpServletRequest  httpServletRequest  = Tool.getRequest();
        HttpServletResponse httpServletResponse = Tool.getResponse();

        if (httpServletRequest == null || httpServletResponse == null)
        {
            throw new MyException("未获取到请求和响应的信息");
        }

        String captchaId;
        if (StrUtil.isNotBlank(sessionId))
        {
            captchaId = (String)redis.get(CACHE_PRE + "sessionid_" + sessionId);
            if (StrUtil.isBlank(captchaId))
            {
                throw new MyException("未找到服务器保存的图片验证码，请刷新图片验证码");
            }
        }
        else
        {
            HttpSession session = httpServletRequest.getSession();
            captchaId = (String) session.getAttribute("verifyCode");
            session.setAttribute("captcha", null);
        }

        if (!StrUtil.equals(captchaId, verifyCode))
        {
            throw new MyException("图片验证码错误");
        }
    }
}

