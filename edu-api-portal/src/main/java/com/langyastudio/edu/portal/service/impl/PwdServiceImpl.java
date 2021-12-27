package com.langyastudio.edu.portal.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.PageInfo;
import com.langyastudio.edu.db.mapper.*;
import com.langyastudio.edu.db.model.UmsDivision;
import com.langyastudio.edu.db.model.UmsUser;
import com.langyastudio.edu.db.model.UmsUserAuth;
import com.langyastudio.edu.portal.bean.dto.LoginDeviceParam;
import com.langyastudio.edu.portal.bean.vo.UmsDivisionVO;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.bean.dto.FindPwdParam;
import com.langyastudio.edu.portal.bean.dto.LoginVerifyParam;
import com.langyastudio.edu.portal.bean.dto.RegisterParam;
import com.langyastudio.edu.portal.service.PwdService;
import com.langyastudio.edu.security.util.JwtTokenUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 登录注册
 */
@Log4j2
@Service
public class PwdServiceImpl extends BaseServiceImpl implements PwdService
{
    @Autowired
    private JwtTokenUtil    jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UmsUserMapper      umsUserMapper;
    @Autowired
    UmsUserAuthMapper  umsUserAuthMapper;
    @Autowired
    UmsUserRolesMapper umsUserRolesMapper;
    @Autowired
    UmsRoleApisMapper umsRoleApisMapper;
    @Autowired
    UmsApiMapper      umsApiMapper;
    @Autowired
    UmsRoleMapper     umsRoleMapper;
    @Autowired
    UmsDivisionMapper  umsDivisionMapper;

    /*-------------------------------------------------------------------------------------------------------------- */
    // 登录注册
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 用户名是否存在
     *
     * @param userName 用户名
     *
     * @return
     */
    @Override
    public Map<String, Object> existName(String userName)
    {
        Map<String, Object> rst = new HashMap<>(Map.of("exist", Define.NOI,
                                                       "user_name", userName,
                                                       "full_name", "",
                                                       "phone", ""));

        //获取用户信息
        UmsUser umsUserInfo = umsUserMapper.getInfoByUserName(userName);
        if(Objects.nonNull(umsUserInfo))
        {
            rst.put("exist", Define.YESI);
            rst.put("full_name", umsUserInfo.getFullName());

            UmsUserAuth auth = umsUserAuthMapper.getInfoByUserName(userName);
            if(Objects.nonNull(auth))
            {
                rst.put("phone", auth.getPhone());
            }
        }

        return rst;
    }

    /**
     * 手机号是否存在
     *
     * @param phone  手机号
     * @param except 是否排除当前登录用户
     *
     * @return
     */
    @Override
    public Map<String, Object> existPhone(String phone, String except)
    {
        Map<String, Object> rst = new HashMap<>(Map.of("exist", Define.NOI,
                                                       "user_name", "",
                                                       "full_name", "",
                                                       "phone", phone));
        String userName = umsUserAuthMapper.getUserNameByPhone(phone);

        if (Objects.nonNull(userName))
        {
            UmsUserAuth auth = umsUserAuthMapper.getInfoByUserName(userName);
            if(Objects.nonNull(auth))
            {
                Byte enable = auth.getEnabled();
                if (Convert.toInt(enable).equals(Define.NOI))
                {
                    throw new MyException(EC.ERROR_USER_ENABLED);
                }
            }

            rst.put("exist", Define.YESI);
            if (StrUtil.isNotBlank(except) && userName.equals(except))
            {
                rst.put("exist", Define.NOI);
            }

            rst.put("user_name", userName);
            UmsUser umsUserInfo = umsUserMapper.getInfoByUserName(userName);
            if(Objects.nonNull(umsUserInfo))
            {
                rst.put("full_name", umsUserInfo.getFullName());
            }
        }

        return rst;
    }

    /**
     * 注册功能
     *
     * @param param
     */
    @Override
    public Map<String, Object> register(RegisterParam param)
    {
        return this.addSystemUser(param.getUserName(), param.getPwd(), null, param.getName(), UmsUser.USER_SEX_UNKNOWN_I);
    }

    /**
     * 手机号一键登录
     *
     * @param verifyParam 校验码参数
     *
     * @return
     */
    @Override
    @Transactional
    public String loginSms(LoginVerifyParam verifyParam)
    {
        //1.0 判断手机号是否存在系统中
        String userName = umsUserAuthMapper.getUserNameByPhone(verifyParam.getName());

        //此时需要注册用户
        if (Objects.isNull(userName))
        {
            Map<String, Object> newInfo = this.addSystemUser(null, null, null, verifyParam.getName(), UmsUser.USER_SEX_UNKNOWN_I);
            userName = (String) newInfo.get("user_name");
        }

        return this.login(userName, null);
    }

    /**
     * 设备加密登录
     *
     * @param deviceParam 登录参数
     *
     * @return
     */
    @Override
    @Transactional
    public String loginDevice(LoginDeviceParam deviceParam)
    {
        //1.0 设备是否存在
        String  accessKey = deviceParam.getAccessKey();
        UmsUser umsUser   = umsUserMapper.getInfoByUserName(accessKey);
        if (Objects.isNull(umsUser))
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }
        if (umsUser.getUserType() != UmsUser.USER_TYPE_ROOM)
        {
            throw new MyException(EC.ERROR_PARAM_ILLEGAL);
        }

        //check key


        //2.0 计算签名
        String signature = deviceParam.getSignature();
        String secretKey = "a0f12b45968549d085220f75a68bc668";

        //使用排序后的map
        Map<String, String> signatureParams = new TreeMap<>(String::compareTo);
        signatureParams.put("signaturemethod", deviceParam.getSignatureMethod());
        signatureParams.put("signaturenonce", deviceParam.getSignatureNonce());
        signatureParams.put("signatureversion", deviceParam.getSignatureVersion());
        signatureParams.put("accesskey", deviceParam.getAccessKey());
        signatureParams.put("timestamp", deviceParam.getTimestamp());
        signatureParams.put("format", deviceParam.getFormat());

        // merge param
        StringBuilder sortedQueryStringTmp = new StringBuilder();
        for (Map.Entry<String, String> entry : signatureParams.entrySet())
        {
            sortedQueryStringTmp.append("&")
                    .append(this.safeEncode(entry.getKey()))
                    .append("=")
                    .append(this.safeEncode(entry.getValue()));
        }

        // sign
        String stringToSign   = "GET&%2F&" + this.safeEncode(sortedQueryStringTmp.substring(1));
        String sign           = Base64.getEncoder().encodeToString(this.hamcsha1(stringToSign, secretKey + "&"));
        String signatureCheck = this.safeEncode(sign);
        if (!signatureCheck.equals(signature))
        {
            throw new MyException(EC.ERROR_REQUEST_API_SIGNATURE_ERROR);
        }

        //3.0 授权登录
        return this.login(accessKey, null);
    }

    /**
     * 参数编码
     */
    private String safeEncode(String str)
    {
        str = URLEncoder.encode(str, StandardCharsets.UTF_8);

        str = str.replace("+", "%20");
        str = str.replace("*", "%2A");
        str = str.replace("%7E", "~");

        return str;
    }

    /**
     * hamcsha1 加密
     *
     * @param data      data
     * @param secretKey 密钥
     *
     * @return
     */
    private byte[] hamcsha1(String data, String secretKey)
    {
        try
        {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            Mac           mac        = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        }
        catch (Throwable e)
        {
            //Todo: 写入日志
        }

        return null;
    }

    /**
     * 退出登录
     *
     * @param userName 用户名
     * @param token    token
     *
     * @return
     */
    @Override
    public Integer logout(String userName, String token)
    {
        //Todo:标记该token无效


        return Define.YESI;
    }

    /**
     * 刷新token的功能
     *
     * @param oldToken 旧的token
     */
    @Override
    public String refreshToken(String oldToken)
    {
        String token = jwtTokenUtil.refreshHeadToken(oldToken);
        if (Objects.isNull(token))
        {
            throw new MyException(EC.ERROR_USER_EXPIRE);
        }

        return token;
    }

    /**
     * 手机号找回密码
     *
     * @param param 找回密码参数
     *
     * @return
     */
    @Override
    @Transactional
    public Integer findPwd(FindPwdParam param)
    {
        String phone = param.getName();

        //1.0 校验用户名是否存在
        String userName = umsUserAuthMapper.getUserNameByPhone(phone);
        if (Objects.isNull(userName))
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }

        //2.0 修改密码
        UmsUserAuth umsUserAuth = new UmsUserAuth();
        umsUserAuth.setPwd(passwordEncoder.encode(param.getNewPwd()));
        umsUserAuth.setUserName(userName);

        umsUserAuthMapper.updateByUserName(umsUserAuth);

        return Define.YESI;
    }

    /**
     * 获取省市区列表
     *
     * @param parentId 父节点
     * @param lever    层级
     *
     * @return
     */
    @Override
    public PageInfo<UmsDivisionVO> getListAddress(Integer parentId, Integer lever)
    {
        String  path = null;
        Integer self = 0;
        Integer sgId = 0;
        if (!parentId.equals(UmsDivision.TOP_NODE_ID))
        {
            UmsDivision sgInfo = umsDivisionMapper.getByPcdId(parentId);
            if (null == sgInfo)
            {
                throw new MyException(EC.ERROR_COMMON_RECORD_NOT_EXIST);
            }

            self = sgInfo.getLevel();
            path = sgInfo.getPcdPath();
        }

        //level范围
        Integer min = self + 1;
        Integer max = self + lever;
        List<String> branchIds = umsDivisionMapper.getPcdIdBySchoolIdAndSgId(parentId, lever, self, path,
                                                                             min, max);
        List<UmsDivisionVO> nodes = new ArrayList<>();
        if (null != branchIds)
        {
            UmsDivisionVO branchInfo = null;
            for (String branchId : branchIds)
            {
                UmsDivision umsDivision = umsDivisionMapper.getByPcdId(Convert.toInt(branchId));

                //分校详情
                branchInfo = new UmsDivisionVO();
                branchInfo.setPcdPath(umsDivision.getPcdPath());
                branchInfo.setPcdName(umsDivision.getPcdName());
                branchInfo.setPcdId(umsDivision.getPcdId());
                branchInfo.setParentId(umsDivision.getParentId());
                branchInfo.setLevel(umsDivision.getLevel());
                branchInfo.setSort(umsDivision.getSort());
                branchInfo.setCreateTime(umsDivision.getCreateTime());
                branchInfo.setDeleteTime(umsDivision.getDeleteTime());

                nodes.add(branchInfo);
            }
        }

        List<UmsDivisionVO> rtnList = buildDeptTree(nodes, parentId);
        if(Objects.isNull(rtnList))
        {
            return null;
        }

        return new PageInfo<>((long)rtnList.size(), rtnList);
    }


    /**
     * 生成树
     *
     * @param nodes 所有节点列表
     *
     * @return
     */
    public List<UmsDivisionVO> buildDeptTree(List<UmsDivisionVO> nodes, int inPid)
    {
        if (nodes == null)
        {
            return null;
        }
        List<UmsDivisionVO> result = new ArrayList<>();

        nodes.forEach(children -> {
            Integer pid = children.getParentId();

            if (Objects.isNull(pid) || inPid == pid)
            {
                result.add(children);
                return;
            }

            for (UmsDivisionVO node : nodes)
            {
                Integer id = node.getPcdId();
                if (id != null && id.equals(pid))
                {
                    if (Objects.isNull(node.getChildren()))
                    {
                        node.initChildren();
                    }

                    node.getChildren().add(children);
                    children.setHasParent(true);
                    node.setHasChild(true);

                    return;
                }
            }
        });

        return result;
    }
}
