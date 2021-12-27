package com.langyastudio.edu.db.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsUserOauths;
import org.apache.ibatis.annotations.Param;

/**
 * 第三方授权信息表
 * 逻辑Id号 - oauthId or userName + oAuthType
 */
public interface UmsUserOauthsMapper extends Mapper<UmsUserOauths>
{
    /**
     * 插入数据
     *
     * @param record username+oauthtype
     * @return
     */
    int insertUserOauth(UmsUserOauths record);

    /**
     * 软删除记录
     *
     * @param userName  用户名
     * @param oAuthType 第三方类型
     *
     * @return int
     */
    int deleteByUserName(@Param("userName") String userName, @Param("oAuthType") Byte oAuthType);

    /**
     * 根据用户名获取第三方授权信息
     *
     * @param userName  用户名
     * @param oAuthType 第三方类型
     *
     * @return UmsUserOauths
     */
    UmsUserOauths getByUserName(@Param("userName") String userName, @Param("oAuthType") Byte oAuthType);

    /**
     * 根据openid获取第三方授权信息
     * @param oauthId openid
     * @return
     */
    String getUserNameByOauthId(@Param("oauthId")String oauthId);
}