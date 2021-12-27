package com.langyastudio.edu.portal.service;

import com.langyastudio.edu.common.data.PageInfo;
import com.langyastudio.edu.portal.bean.dto.FindPwdParam;
import com.langyastudio.edu.portal.bean.dto.LoginDeviceParam;
import com.langyastudio.edu.portal.bean.dto.LoginVerifyParam;
import com.langyastudio.edu.portal.bean.dto.RegisterParam;
import com.langyastudio.edu.portal.bean.vo.UmsDivisionVO;

import java.util.Map;

/**
 * 密码服务
 */
public interface PwdService extends BaseService
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // 登录注册
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 用户名是否存在
     */
    Map<String, Object> existName(String userName);

    /**
     * 手机号是否存在
     */
    Map<String, Object> existPhone(String phone, String except);

    /**
     * 注册功能
     */
    Map<String, Object> register(RegisterParam param);

    /**
     * 手机号一键登录
     *
     * @param verifyParam 校验码参数
     *
     * @return
     */
    String loginSms(LoginVerifyParam verifyParam);

    /**
     * 设备加密登录
     *
     * @param deviceParam 登录参数
     *
     * @return
     */
    String loginDevice(LoginDeviceParam deviceParam);

    /**
     * 退出登录
     *
     * @param userName 用户名
     *
     * @return
     */
    Integer logout(String userName, String token);

    /**
     * 刷新token的功能
     *
     * @param oldToken 旧的token
     */
    String refreshToken(String oldToken);

    /**
     * 手机号找回密码
     *
     * @param param 找回密码参数
     *
     * @return
     */
    Integer findPwd(FindPwdParam param);

    /**
     * 获取省市区列表
     *
     * @param parentId 父节点
     * @param lever    层级
     *
     * @return
     */
    PageInfo<UmsDivisionVO> getListAddress(Integer parentId, Integer lever);
}

