package com.langyastudio.edu.portal.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.entity.UserAgentData;
import com.langyastudio.edu.db.mapper.*;
import com.langyastudio.edu.db.model.*;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.service.BaseService;
import com.langyastudio.edu.portal.service.SecurityService;
import com.langyastudio.edu.security.util.JwtTokenUtil;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.Tool;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 基类服务层
 */
@Log4j2
public class BaseServiceImpl implements BaseService
{
    @Autowired
    SecurityService securityService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UmsUserMapper umsUserMapper;

    @Autowired
    UmsUserAuthMapper umsUserAuthMapper;

    @Autowired
    UmsUserRolesMapper umsUserRolesMapper;

    @Autowired
    UmsRoleApisMapper umsRoleApisMapper;
    @Autowired
    UmsApiMapper      umsApiMapper;
    @Autowired
    UmsRoleMapper     umsRoleMapper;
    @Autowired
    UmsUserLoginLogsMapper umsUserLoginLogsMapper;

    /*-------------------------------------------------------------------------------------------------------------- */
    // 登录认证
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 身份识别
     * 也可以在Controller层注入Principal
     *
     * @param bNeedLogin 是否需要登录
     *
     * @return 用户名
     */
    @Override
    public String getUserName(boolean bNeedLogin)
    {
        String userName = null;

        // 获取用户认证信息对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 认证信息可能为空，因此需要进行判断
        if (Objects.nonNull(authentication))
        {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails)
            {
                userName = ((UserDetails) principal).getUsername();
            }
        }

        if (bNeedLogin && StrUtil.isBlankIfStr(userName))
        {
            throw new MyException(EC.ERROR_USER_UNAUTHORIZED);
        }

        return userName;
    }

    /**
     * 是否是admin
     *
     * @param userName 用户名
     *
     * @return bool
     */
    @Override
    public Boolean isAdmin(String userName)
    {
        return UmsUser.USER_ADMIN_NAME.equals(userName);
    }

    /**
     * 登录功能
     *
     * @param userName 用户名
     * @param pwd      密码
     *
     * @return 生成的JWT的token
     */
    @Override
    public String login(String userName, String pwd)
    {
        String token = null;

        //密码需要客户端加密后传递
        try
        {
            UserDetails userDetails = securityService.loadUserByUsername(userName);

            //账号密码登录时，必须传入!!!
            //此时要求慎重密码的传入行为
            if (!StrUtil.isEmptyIfStr(pwd))
            {
                if (!passwordEncoder.matches(pwd, userDetails.getPassword()))
                {
                    throw new MyException(EC.ERROR_USER_PASSWORD_INCORRECT);
                }
            }

            if (!userDetails.isEnabled())
            {
                throw new MyException(EC.ERROR_USER_ENABLED);
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            token = jwtTokenUtil.generateToken(userDetails);

            //登录日志
            UmsUserLoginLogs umsUserLoginLogs = new UmsUserLoginLogs();
            umsUserLoginLogs.setUserName(userName);

            //agent info
            UserAgentData userAgent = Tool.getUserAgentInfo(Tool.getRequest());
            umsUserLoginLogs.setLocation(userAgent.getLocation());
            umsUserLoginLogs.setOsName(userAgent.getOsName());
            umsUserLoginLogs.setIp(userAgent.getIp());
            umsUserLoginLogs.setBrowseName(userAgent.getBrowseName());

            umsUserLoginLogsMapper.insertUserLoginLogs(umsUserLoginLogs);
        }
        catch (AuthenticationException e)
        {
            log.warn("登录异常:{}", e.getMessage());
        }

        return token;
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // 用户操作
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 添加系统用户
     *
     * @param userName 用户名
     * @param fullName 姓名
     * @param phone    手机号
     * @param sex      性别
     *
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> addSystemUser(String userName, String pwd,
                                             String fullName, String phone, Integer sex)
    {
        //1.0 生成自定义用户名
        if (StrUtil.isEmptyIfStr(userName))
        {
            //手机号后四位
            String end = RandomUtil.randomNumbers(4);
            if (!StrUtil.isEmptyIfStr(phone))
            {
                end = phone.substring(phone.length() - 4, phone.length());
            }

            //取一个未用的用户名
            userName = "a" + end;
            int count = 10;
            while ((count-- > 0) && null != umsUserMapper.getInfoByUserName(userName))
            {
                userName = "a" + RandomUtil.randomNumbers(2) + end;
            }
        }

        //1.1 用户名是否存在
        if (null != umsUserMapper.getInfoByUserName(userName))
        {
            throw new MyException(EC.ERROR_USER_NAME_EXIST);
        }

        //1.2 手机号是否存在
        if (!StrUtil.isEmptyIfStr(phone) && null != umsUserAuthMapper.getUserNameByPhone(phone))
        {
            throw new MyException(EC.ERROR_USER_PHONE_EXIST);
        }

        if (StrUtil.isEmptyIfStr(pwd))
        {
            pwd = UmsUserAuth.USER_AUTH_PWD;
        }

        //2.1 写入数据
        //用户
        UmsUser umsUserBean = new UmsUser();
        String  strName     = StrUtil.isEmptyIfStr(fullName) ? userName : fullName;
        umsUserBean.setUserName(userName);
        umsUserBean.setSex(Convert.toByte(sex));
        umsUserBean.setFullName(strName);
        umsUserBean.setNickName(strName);
        umsUserBean.setRegIp(Tool.getClientIpLong(Tool.getRequest()));
        umsUserMapper.insertUser(umsUserBean);

        //用户auth
        UmsUserAuth umsUserAuth = new UmsUserAuth();
        umsUserAuth.setUserName(userName);
        umsUserAuth.setPhone(phone);
        umsUserAuth.setPwd(passwordEncoder.encode(pwd));
        umsUserAuthMapper.insertUserAuth(umsUserAuth);

        //用户角色
        List<UmsUserRoles> umsUserRoles = new ArrayList<>();
        umsUserRoles.add(new UmsUserRoles(null, userName, UmsRole.ROLE_USER_ID, Define.SHOOL_SYSTEM_ID, null, null
                , null));
        umsUserRolesMapper.batchInsertUserRoles(umsUserRoles);

        //3.1 返回数据
        Map<String, Object> result      = new HashMap<>();
        UmsUser             umsUserInfo = umsUserMapper.getInfoByUserName(userName);
        if (Objects.isNull(umsUserInfo))
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }

        //2.1 基本信息
        result = Tool.beanToMap(umsUserInfo);
        result.remove("description");

        //2.2 user auth
        UmsUserAuth umsUserAuthInfo = umsUserAuthMapper.getInfoByUserName(userName);
        result.putAll(Tool.beanToMap(umsUserAuthInfo));

        return result;
    }
}
