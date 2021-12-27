package com.langyastudio.edu.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.langyastudio.edu.common.exception.MyException;

/**
 * 加密解密
 */
public class CryptoT
{
    //随机生成密钥: Base64.encode(SecureUtil.generateKey(SymmetricAlgorithm.DESede.getValue()).getEncoded());
    private static final String KEY_STR = "sxUWiV56j4UyLPF5MgFk4zJ/+xrmzfe6";

    public static String encryptHex(String content)
    {
        byte[]          key = Base64.decode(KEY_STR);
        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.DESede, key);

        //加密为16进制字符串（Hex表示）
        return des.encryptHex(content);
    }

    public static String decryptStr(String encryptHex) throws MyException
    {
        byte[]          key = Base64.decode(KEY_STR);
        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.DESede, key);

        //解密为字符串
        String decryptStr;
        try
        {
            decryptStr = des.decryptStr(encryptHex);
        }
        catch (Exception e)
        {
            throw new MyException("解密失败:" + e.getMessage());
        }

        if (StrUtil.isNotBlank(encryptHex) && StrUtil.isBlank(decryptStr))
        {
            throw new MyException("解密失败");
        }

        return decryptStr;
    }
}
