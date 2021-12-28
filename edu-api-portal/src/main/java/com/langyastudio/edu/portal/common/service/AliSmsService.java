package com.langyastudio.edu.portal.common.service;

import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.common.conf.AliSmsConf;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.third.AliSms;
import cn.hutool.core.util.RandomUtil;
import com.langyastudio.edu.common.util.RedisT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信发送与校验服务
 */
@Component
public class AliSmsService
{
    final static String CACHE_PRE = "test_edu_sms:";

    @Autowired
    AliSmsConf aliSmsConf;

    @Autowired
    AliSms aliSms;

    @Autowired
    RedisT redisT;

    /**
     * 发送校验码
     * 1 注册
     * 2 登录
     * 3 找回密码
     * 4 修改手机号
     */
    public Boolean sendSms(String telPhone, String type) throws Exception
    {
        String cacheSmsKey  = CACHE_PRE + "aliyun_" + type + telPhone;
        String cacheTimeKey = CACHE_PRE + "aliyun_time_" + type + telPhone;

        //1.0 频率验证
        long   currentNow = System.currentTimeMillis();
        String smsTime    = redisT.get(cacheTimeKey);
        if (smsTime != null)
        {
            long pastTime = currentNow - Long.parseLong(smsTime);
            if (pastTime < 1000 * 60)
            {
                throw new MyException(EC.ERROR_ALIYUN_SERVICE_SMS_LIMIT.getCode(),
                                      "每分钟只能发送一条短信（请" + (60 - pastTime / 1000) + "秒后再尝试）");
            }
        }

        Map<String, String> params = null;
        switch (type)
        {
            case Define.VERITY_REGISTER:
            {
                params = aliSmsConf.getRegister();
                break;
            }
            case Define.VERITY_MODIFY:
            {
                params = aliSmsConf.getModifyPhone();
                break;
            }
            case Define.VERITY_FINDPWD:
            {
                params = aliSmsConf.getFindPwd();
                break;
            }
            case Define.VERITY_LOGIN:
            {
                params = aliSmsConf.getLogin();
                break;
            }
            default:
            {

            }
        }

        String              code     = RandomUtil.randomNumbers(4);
        Map<String, String> mapParam = new HashMap<>();
        mapParam.put("code", code);

        Boolean bRtn = aliSms.sendMsg(telPhone, aliSmsConf.getAccessKey(), aliSmsConf.getAccessSecret(),
                                      params.get("signName"), params.get("templateCode"), mapParam);
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
     * @param telPhone 手机号
     * @param type     类型
     */
    public void del(String telPhone, String type)
    {
        String cacheSmsKey = CACHE_PRE + "aliyun_" + type + telPhone;

        redisT.del(cacheSmsKey);
    }

    /**
     * 检测校验码是否有效
     *
     * @param telPhone 手机号
     * @param code     校验码
     * @param type     类型
     *
     * @return
     */
    public Boolean checkValid(String telPhone, String code, String type)
    {
        String cacheSmsKey = CACHE_PRE + "aliyun_" + type + telPhone;

        //保存校验码
        //30分钟有效
        String cacheCode = redisT.get(cacheSmsKey);

        return code.equals(cacheCode);
    }
}
