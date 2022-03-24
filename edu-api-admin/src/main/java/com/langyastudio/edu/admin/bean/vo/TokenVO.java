package com.langyastudio.edu.admin.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * jwt 返回对象
 */
@Data
@AllArgsConstructor
public class TokenVO implements Serializable
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
