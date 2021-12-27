package com.langyastudio.edu.db.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsUserAuth;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * 用户账号密钥
 * 逻辑Id号 - user_name
 *
 * 可以使用用户名或手机号登录
 */
@CacheConfig(cacheNames = "db" + ":umsuserauthmapper")
public interface UmsUserAuthMapper extends Mapper<UmsUserAuth>
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // cache
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 插入用户账号
     *
     * @param umsUserAuth Bean
     *
     * @return
     */
    Integer insertUserAuth(UmsUserAuth umsUserAuth);

    /**
     * 根据用户名获取用户信息
     *
     * @param userName 用户名
     *
     * @return
     */
    @Cacheable(key = "#userName", unless = "#result == null")
    UmsUserAuth getInfoByUserName(@Param("userName") String userName);

    /**
     * 更新用户账号
     * @param record bean
     * @return
     */
    @CacheEvict(key = "#record.userName", beforeInvocation=false)
    Integer updateByUserName(UmsUserAuth record);

    /*-------------------------------------------------------------------------------------------------------------- */
    // other
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 根据用户名获取用户密码信息
     *
     * ！！这个函数较为特殊，没有使用缓存功能
     *
     * @param userName 用户名
     * @return
     */
    UmsUserAuth getInfoByUserNameEx(@Param("userName") String userName);

    /**
     * 根据手机号获取用户名
     *
     * @param phone 手机号
     *
     * @return
     */
    String getUserNameByPhone(@Param("phone") String phone);
}