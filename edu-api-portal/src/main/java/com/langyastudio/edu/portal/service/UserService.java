package com.langyastudio.edu.portal.service;

import com.langyastudio.edu.portal.bean.dto.UpdateAuthParam;
import com.langyastudio.edu.portal.bean.dto.UpdatePwdParam;
import com.langyastudio.edu.portal.bean.dto.UserParam;

import java.util.Map;

/**
 * 账号管理
 */
public interface UserService extends BaseService
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // 账号设置
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 获取用户信息
     *
     * @param userName 用户名
     *
     * @return
     */
    Map<String, Object> getInfo(String userName);

    /**
     * 修改用户
     *
     * @param userInfo 用户信息
     *
     * @return
     */
    Integer modify(UserParam userInfo);

    /**
     * 修改用户
     *
     * @param authParam 授权信息
     *
     * @return
     */
    Integer modifyPhone(UpdateAuthParam authParam);

    /**
     * 修改密码
     *
     * @param pwdParam 密码参数
     */
    Integer modifyPassword(UpdatePwdParam pwdParam);

    /**
     * 保存用户头像
     *
     * @param userName 用户名
     * @param imgPath  图像完整路径
     *
     * @return
     */
    Integer modifyAvator(String userName, String imgPath);
}
