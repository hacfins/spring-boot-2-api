package com.langyastudio.edu.portal.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * jwt 返回对象
 */
@Data
@AllArgsConstructor
public class TokenVO
{
    /**
     * token
     */
    private String token;

    /**
     * token 前缀
     */
    private String tokenHead;
}
