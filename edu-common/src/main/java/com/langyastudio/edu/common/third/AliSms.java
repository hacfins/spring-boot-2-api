package com.langyastudio.edu.common.third;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.*;
import com.langyastudio.edu.common.exception.MyException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信服务
 */
@Component
public class AliSms
{
    /**
     * 发送短信
     *
     * @param telPhone        手机号
     * @param accessKeyId     accessKey
     * @param accessKeySecret secretKey
     * @param signName        sign Name
     * @param templateCode    template Name
     * @param mapParam        param
     *
     * @return bool
     *
     * @throws Exception
     */
    public Boolean sendMsg(String telPhone, String accessKeyId, String accessKeySecret,
                           String signName, String templateCode, Map<String, String> mapParam) throws Exception
    {
        //1.0 使用AK&SK初始化账号Client
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);

        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);

        //2.0 设置短信发送参数
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(telPhone)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(JSON.toJSONString(mapParam));

        //3.0 发送短信
        SendSmsResponseBody response = client.sendSms(sendSmsRequest).getBody();

        if ("OK".equals(response.getCode()))
        {
            return true;
        }

        throw new MyException(response.getMessage());
    }
}
