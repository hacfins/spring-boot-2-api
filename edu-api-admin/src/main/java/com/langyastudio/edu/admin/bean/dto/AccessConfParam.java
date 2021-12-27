package com.langyastudio.edu.admin.bean.dto;

import com.langyastudio.edu.admin.common.data.Define;
import com.langyastudio.edu.common.anno.InValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问配置参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessConfParam
{
    /**
     * 注册
     */
    @InValue
    private Integer register;

    /**
     * 微信登录
     */
    @InValue
    private Integer weixin;

    /**
     * 钉钉登录
     */
    @InValue
    private Integer dingding;

    /**
     * 默认登录方式
     */
    @InValue(value = {Define.YES, Define.NO, "3"})
    private Integer loginType;

    /**
     * 单点登录
     */
    @InValue
    private Integer singleLogin;

    /**
     * 请求频率限制
     */
    @InValue
    private Integer requestLimit;

    /**
     * 敏感词过滤
     */
    @InValue
    private Integer sensitive;
}
