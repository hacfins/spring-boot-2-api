package com.langyastudio.edu.portal.service.impl;

import com.langyastudio.edu.db.mapper.*;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.bean.dto.UpdateAuthParam;
import com.langyastudio.edu.portal.bean.dto.UpdatePwdParam;
import com.langyastudio.edu.db.model.UmsUser;
import com.langyastudio.edu.db.model.UmsUserAuth;
import com.langyastudio.edu.portal.bean.dto.UserParam;
import com.langyastudio.edu.portal.service.UserService;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.Tool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * 用户服务
 */
@Service
public class UserServiceImpl extends BaseServiceImpl implements UserService
{
    @Autowired
    PasswordEncoder    passwordEncoder;
    @Autowired
    UmsUserMapper      umsUserMapper;
    @Autowired
    UmsUserAuthMapper  umsUserAuthMapper;
    @Autowired
    UmsUserRolesMapper umsUserRolesMapper;
    @Autowired
    UmsRoleApisMapper  umsRoleApisMapper;
    @Autowired
    UmsApiMapper       umsApiMapper;
    @Autowired
    UmsRoleMapper      umsRoleMapper;

    @Autowired
    UmsDivisionMapper umsDivisionMapper;

    /**
     * 获取用户信息
     *
     * @param userName 用户名
     *
     * @return
     */
    @Override
    public Map<String, Object> getInfo(String userName)
    {
        //1.0 基本信息
        UmsUser umsUserInfo = umsUserMapper.getInfoByUserName(userName);
        if (null == umsUserInfo)
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }
        Map<String, Object> resultInfo = Tool.beanToMap(umsUserInfo);

        //2.0 账户信息
        UmsUserAuth umsUserAuthInfo = umsUserAuthMapper.getInfoByUserName(userName);
        if (null == umsUserAuthInfo)
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }
        resultInfo.putAll(Tool.beanToMap(umsUserAuthInfo));

        return resultInfo;
    }

    /**
     * 修改用户
     *
     * @param userParam 用户信息
     *
     * @return
     */
    @Override
    public Integer modify(UserParam userParam)
    {
        //验证行政区划是否正确
        Integer pcdId = userParam.getPcdId();
        if (pcdId != 0)
        {
            if (Objects.isNull(umsDivisionMapper.getByPcdId(pcdId)))
            {
                throw new MyException(EC.ERROR_PARAM_ILLEGAL);
            }
        }

        //更新用户信息
        UmsUser umsUserInfo = new UmsUser();
        BeanUtils.copyProperties(userParam, umsUserInfo);
        umsUserMapper.updateByUserName(umsUserInfo);

        return Define.YESI;
    }

    /**
     * 修改用户
     *
     * @param authParam 用户信息
     *
     * @return
     */
    @Override
    public Integer modifyPhone(UpdateAuthParam authParam)
    {
        //判断需要修改的手机号是否已经存在
        String userName = umsUserAuthMapper.getUserNameByPhone(authParam.getName());
        if (Objects.nonNull(userName) && !userName.equals(authParam.getUserName()))
        {
            throw new MyException(EC.ERROR_USER_PHONE_EXIST);
        }

        UmsUserAuth umsUserAuth = new UmsUserAuth();
        umsUserAuth.setUserName(authParam.getUserName());
        umsUserAuth.setPhone(authParam.getName());
        umsUserAuthMapper.updateByUserName(umsUserAuth);

        return Define.YESI;
    }

    /**
     * 修改密码
     *
     * @param pwdParam 密码参数
     */
    @Override
    public Integer modifyPassword(UpdatePwdParam pwdParam)
    {
        //1.0 用户是否存在
        UmsUserAuth umsUserAuthInfo = umsUserAuthMapper.getInfoByUserNameEx(pwdParam.getUserName());
        if (Objects.isNull(umsUserAuthInfo))
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }

        //密码不正确
        if (!passwordEncoder.matches(pwdParam.getOldPwd(), umsUserAuthInfo.getPwd()))
        {
            throw new MyException(EC.ERROR_USER_PASSWORD_ERROR);
        }

        //新旧密码相同
        if (passwordEncoder.matches(pwdParam.getNewPwd(), umsUserAuthInfo.getPwd()))
        {
            throw new MyException(EC.ERROR_USER_PWD_SAME);
        }

        //2.0 修改密码
        UmsUserAuth desUserInfo = new UmsUserAuth();
        desUserInfo.setUserName(pwdParam.getUserName());
        desUserInfo.setPwd(passwordEncoder.encode(pwdParam.getNewPwd()));
        umsUserAuthMapper.updateByUserName(desUserInfo);

        return Define.YESI;
    }

    /**
     * 保存用户头像
     *
     * @param userName 用户名
     * @param imgPath  图像完整路径
     *
     * @return
     */
    @Override
    public Integer modifyAvator(String userName, String imgPath)
    {
        //更新用户信息
        UmsUser umsUserInfo = new UmsUser();
        umsUserInfo.setUserName(userName);
        umsUserInfo.setAvator(imgPath);
        umsUserMapper.updateByUserName(umsUserInfo);

        return Define.YESI;
    }
}
