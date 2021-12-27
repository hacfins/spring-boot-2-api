package com.langyastudio.edu.db.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsUser;
import com.langyastudio.edu.common.data.PageIn;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * 用户表
 * 逻辑Id号 - user_name
 */
@CacheConfig(cacheNames = "db" + ":umsusermapper")
public interface UmsUserMapper extends Mapper<UmsUser>
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // cache
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 插入用户
     *
     * @param umsUser 用户bean
     *
     * @return
     */
    Integer insertUser(UmsUser umsUser);

    /**
     * 获取用户信息
     *
     * @param userName 用户名
     *
     * @return
     */
    @Cacheable(key = "#userName", unless = "#result == null")
    UmsUser getInfoByUserName(@Param("userName") String userName);

    /**
     * 根据user_name更新bean
     *
     * @param record UmsUser
     *
     * @return
     */
    @CacheEvict(key = "#record.userName", beforeInvocation=false)
    Integer updateByUserName(UmsUser record);

    /*-------------------------------------------------------------------------------------------------------------- */
    // other
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 获取系统用户列表
     *
     * @param roleId      角色Id号
     * @param userNameKey 用户检索关键字
     * @param fullNameKey 姓名检索关键字
     * @param phoneKey    电话号码检索关键字
     * @param isAsc       是否升序
     * @param pageIn      分页
     *
     * @return
     */
    PageIn<String> getUserNameList(@Param("roleId") String roleId,
                                   @Param("userNameKey") String userNameKey, @Param("fullNameKey") String fullNameKey,
                                   @Param("phoneKey") String phoneKey,
                                   @Param("userType") Byte userType,
                                   @Param("isAsc") Integer isAsc, PageIn<?> pageIn);
}