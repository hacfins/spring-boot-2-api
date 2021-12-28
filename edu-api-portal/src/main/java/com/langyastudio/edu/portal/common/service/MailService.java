package com.langyastudio.edu.portal.common.service;

import com.langyastudio.edu.portal.common.data.Define;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.third.JavaMail;
import com.langyastudio.edu.common.util.RedisT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class MailService
{
    final static String CACHE_PRE = "test_edu_email:";

    @Resource
    private JavaMail emailTool;

    @Autowired
    RedisT redisT;

    /**
     * 发送校验码
     * 1 注册
     * 2 登录
     * 3 找回密码
     * 4 修改邮箱
     */
    public Boolean sendEmail(String email, String type)
    {
        if (StrUtil.isBlank(email))
        {
            throw new MyException(EC.ERROR_PARAM_EMAIL_EMPTY);
        }

        String cacheSmsKey  = CACHE_PRE + "code_" + type + email;
        String cacheTimeKey = CACHE_PRE + "code_time_" + type + email;

        //1.0 频率验证
        long currentNow = System.currentTimeMillis();
        String sendTime = redisT.get(cacheTimeKey);
        if (sendTime != null)
        {
            long pastTime = currentNow - Long.parseLong(sendTime);
            if (pastTime < 1000 * 60)
            {
                throw new MyException(EC.ERROR_ALIYUN_SERVICE_SMS_LIMIT.getCode(),
                                      "每分钟只能发送一封邮件（请" + (60 - pastTime / 1000) + "秒后再尝试）");
            }
        }

        String content = "";
        Map<String, String> params = null;
        switch (type)
        {
            case Define.VERITY_REGISTER:
            {
                content = "注册校证码";
                break;
            }
            case Define.VERITY_MODIFY:
            {
                content = "登录校证码";
                break;
            }
            case Define.VERITY_FINDPWD:
            {
                content = "找回密码校证码";
                break;
            }
            case Define.VERITY_LOGIN:
            {
                content = "修改邮箱校证码";
                break;
            }
            default:
            {

            }
        }

        String  code = RandomUtil.randomNumbers(4);
        Boolean bRtn = emailTool.sendSimpleMail(email, code, content);
        if (bRtn)
        {
            //保存校验码
            //30分钟有效
            redisT.set(cacheSmsKey, code, 60 * 30);

            //最后发送时间
            redisT.set(cacheTimeKey, currentNow, 120);
        }

        return bRtn;
    }

    /**
     * 移除缓存的校验码
     *
     * @param email 手机号
     * @param type     类型
     */
    public void del(String email, String type)
    {
        String cacheSmsKey = CACHE_PRE + "code_" + type + email;

        redisT.del(cacheSmsKey);
    }

    /**
     * 检测校验码是否有效
     *
     * @param email 手机号
     * @param code     校验码
     * @param type     类型
     *
     * @return
     */
    public Boolean checkValid(String email, String code, String type)
    {
        String cacheSmsKey = CACHE_PRE + "code_" + type + email;

        //保存校验码
        //30分钟有效
        String cacheCode = redisT.get(cacheSmsKey);

        return code.equals(cacheCode);
    }
}
