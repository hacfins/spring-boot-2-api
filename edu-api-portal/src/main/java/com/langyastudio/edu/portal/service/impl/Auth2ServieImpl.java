package com.langyastudio.edu.portal.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.langyastudio.edu.portal.bean.vo.JsSDKSignVO;
import com.langyastudio.edu.portal.common.data.Define;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.langyastudio.edu.portal.common.conf.OauthConf;
import com.langyastudio.edu.portal.bean.bo.AuthStateRedisCache;
import com.langyastudio.edu.db.mapper.UmsUserAuthMapper;
import com.langyastudio.edu.db.mapper.UmsUserOauthsMapper;
import com.langyastudio.edu.db.model.UmsUserAuth;
import com.langyastudio.edu.db.model.UmsUserOauths;
import com.langyastudio.edu.portal.service.Auth2Service;
import com.langyastudio.edu.portal.bean.vo.OauthCacheVO;
import com.langyastudio.edu.portal.bean.vo.TokenVO;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.RedisT;
import com.langyastudio.edu.common.util.Tool;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthDingTalkAccountRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWeChatMpRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class Auth2ServieImpl extends BaseServiceImpl implements Auth2Service
{
    private final static String CACHE_KEY          = "test_edu_auth2:key_";
    private final static String CACHE_TICKET_JSAPI = "test_edu_auth2:ticket_jsapi_";
    private final static String CACHE_ACCESS_TOKEN = "test_edu_auth2:access_token_";

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    // ??????
    // - ??????????????????
    public final static int WX_LOGIN_STATUE_QRCODE             = 1;
    // - ?????????AccessToken??????
    public final static int WX_LOGIN_STATUE_ACCESSTOKEN_FAILD  = 2;
    // - ????????????????????????
    public final static int WX_LOGIN_STATUE_USER_REMOVE        = 3;
    // - ????????????
    public final static int WX_LOGIN_STATUE_LOGIN_SUCESS       = 4;
    // - ?????????????????????????????????????????????/????????????
    public final static int WX_LOGIN_STATUE_OAUTH_SUCCESS      = 5;
    // - ???????????????????????????????????????
    public final static int WX_LOGIN_STATUE_USER_EXIST         = 6;
    // - ????????????
    public final static int WX_LOGIN_STATUE_BLIND_SUCESS       = 7;
    // - ????????????????????????
    public final static int WX_LOGIN_STATUE_USER_ENABLED       = 8;
    // - ?????????????????????
    public final static int WX_LOGIN_STATUE_LOGIN_BLIND_SUCESS = 9;

    @Autowired
    private OauthConf oauthConf;

    @Autowired
    private UmsUserOauthsMapper umsUserOauthsMapper;
    @Autowired
    private UmsUserAuthMapper   umsUserAuthMapper;

    @Autowired
    private RedisT redisT;

    @Autowired
    private AuthStateRedisCache authStateRedisCache;


    /*-------------------------------------------------------------------------------------------------------------- */
    // JsSDK
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * ??????JS-SDK sign ????????????
     *
     * @param url ????????????URL??????
     *
     * @return
     */
    @Override
    public JsSDKSignVO jsSDKSign(String url)
    {
        JsSDKSignVO sdkSignVO = new JsSDKSignVO();

        //1.0 ??????
        Map<String, Object> providers = oauthConf.getProvider();
        Object              provider  = providers.get("weixin");
        if (Objects.isNull(provider))
        {
            throw new MyException(EC.ERROR_PARAM_CONF_ERROR);
        }
        Map<String, String> items    = (Map<String, String>) provider;
        String              clientId = items.get("client-id");
        String              secret   = items.get("client-secret");

        try
        {
            //????????????#???????????????
            url = url.split("#")[0];

            String nonceStr  = UUID.randomUUID().toString();
            String timestamp = Long.toString(System.currentTimeMillis() / 1000);

            //jsapi Ticket
            String jsapiTicket = getTicket(clientId, secret);

            // ?????????????????????????????????????????????????????????
            String signature = sha1ToHex("jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonceStr
                                                 + "&timestamp=" + timestamp + "&url=" + url);

            sdkSignVO.setUrl(url);
            sdkSignVO.setAppId(clientId);
            sdkSignVO.setTimestamp(timestamp);
            sdkSignVO.setNonceStr(nonceStr);
            sdkSignVO.setSignature(signature);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return sdkSignVO;
    }

    /**
     * ??????jsapi_ticket??????
     *
     * @param appid  appid
     * @param secret secret
     *
     * @return
     */
    private String getTicket(String appid, String secret)
    {
        String key    = CACHE_TICKET_JSAPI + appid;
        String ticket = redisT.get(key);
        if (StrUtil.isBlankIfStr(ticket))
        {
            String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token="
                    + getAccessToken(appid, secret);
            String backData = getRequest(url);

            JSONObject jsonObject = JSONObject.parseObject(backData);
            if (null != jsonObject)
            {
                ticket = (String) jsonObject.get("ticket");
                if (null == ticket)
                {
                    throw new MyException(EC.ERROR_WX_TICKET_JSAPI_FAILD_ERROR);
                }

                redisT.set(key, ticket, 5000);
            }
        }

        return ticket;
    }

    private String getAccessToken(String appid, String secret)
    {
        String key   = CACHE_ACCESS_TOKEN + appid;
        String token = redisT.get(key);
        if (StrUtil.isBlankIfStr(token))
        {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid +
                    "&secret=" + secret;
            String backData = getRequest(url);

            JSONObject jsonObject = JSONObject.parseObject(backData);
            if (null != jsonObject)
            {
                token = (String) jsonObject.get("access_token");
                if (null == token)
                {
                    throw new MyException(EC.ERROR_WX_ACCESSTOKEN_FAILD_ERROR);
                }

                redisT.set(key, token, 7000);
            }
        }

        return token;
    }

    private String getRequest(String url)
    {
        return HttpRequest.get(url)
                .charset(StandardCharsets.UTF_8)
                .timeout(5000)
                .execute()
                .body();
    }

    private String sha1ToHex(String sorString) throws NoSuchAlgorithmException
    {
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(sorString.getBytes(StandardCharsets.UTF_8));

        return byteToHex(crypt.digest());
    }

    private String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // ????????????
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * ??????????????????
     *
     * @param key ?????????statue??????????????????????????????
     *
     * @return
     */
    @Override
    public OauthCacheVO checkStatus(String key)
    {
        OauthCacheVO oauthCacheVO = this.getCache(key);
        if (Objects.isNull(oauthCacheVO))
        {
            throw new MyException(EC.ERROR_COMMON_RECORD_NOT_EXIST);
        }

        return oauthCacheVO;
    }

    /**
     * ??????????????????
     *
     * @param userName ?????????
     * @param provider ???????????????
     * @param uiUrl    ui URL??????
     * @param isLogin  ????????????
     *
     * @return
     */
    @Override
    public Map<String, String> getAuthUrl(String userName, String provider, String uiUrl, Integer isLogin)
    {
        //1.0 ?????????????????????key
        AuthRequest authRequest = getAuthRequest(provider);
        String      statue      = AuthStateUtils.createState();

        String url = authRequest.authorize(statue);

        //?????????getAuthRequest??????????????????????????????!!
        //??????????????????????????????????????????
        if ("dingding".equals(provider))
        {
            url = url.replace("scope=snsapi_login", "scope=snsapi_auth");
        }

        //2.0 ????????????
        this.setCache(statue, WX_LOGIN_STATUE_QRCODE, "??????????????????", null, userName,
                      null, null, null, provider, isLogin, uiUrl);

        //3.0 ????????????
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("key", statue);
        return result;
    }

    /**
     * ????????????
     *
     * @param provider     ???????????????
     * @param authCallback callback??????????????????code???statue???
     *
     * @return
     */
    @Override
    @Transactional
    public String callback(String provider, AuthCallback authCallback)
    {
        //????????????Android??????????????????????????????????????? - ??????????????????

        AuthRequest  authRequest = getAuthRequest(provider);
        AuthResponse response    = authRequest.login(authCallback);

        String key = authCallback.getState();
        do
        {
            //1.1 access token ????????????
            if (Objects.isNull(response))
            {
                this.setCache(key, WX_LOGIN_STATUE_ACCESSTOKEN_FAILD, "?????????AccessToken??????");
                break;
            }

            //1.2 ????????????
            Map<String, Object> result = Tool.beanToMap(response.getData());
            if (Objects.isNull(result))
            {
                this.setCache(key, WX_LOGIN_STATUE_ACCESSTOKEN_FAILD, "?????????AccessToken??????");
                break;
            }
            String openId   = (String) result.get("uuid");
            String avatar   = (String) result.get("avatar");
            String nickName = (String) result.get("nickname");
            this.setCache(key, WX_LOGIN_STATUE_OAUTH_SUCCESS, "????????????/????????????", null, null, openId, avatar, nickName, null,
                          null, null);

            //2.0 ????????????????????????
            OauthCacheVO oauthCacheVO  = this.getCache(key);
            Integer      isLogin       = oauthCacheVO.getIsLogin();
            String       cacheUserName = oauthCacheVO.getUserName();

            String bindUserName = umsUserOauthsMapper.getUserNameByOauthId(openId);

            //2.1 ????????????
            if (!StrUtil.isEmptyIfStr(bindUserName))
            {
                UmsUserAuth umsUserAuth = umsUserAuthMapper.getInfoByUserName(bindUserName);

                // ????????????????????????
                if (Objects.isNull(umsUserAuth))
                {
                    this.setCache(key, WX_LOGIN_STATUE_USER_REMOVE, "????????????????????????");
                    break;
                }

                // ????????????????????????
                if (Define.NOI.compareTo(Convert.toInt(umsUserAuth.getEnabled())) == 0)
                {
                    this.setCache(key, WX_LOGIN_STATUE_USER_ENABLED, "????????????????????????");
                    break;
                }

                //?????????????????????????????????????????????
                if (Define.NOI.equals(isLogin))
                {
                    this.setCache(key, WX_LOGIN_STATUE_USER_EXIST, "???????????????????????????????????????");
                    break;
                }

                //??????????????????????????????
                String  token   = this.login(bindUserName, null);
                TokenVO tokenVO = new TokenVO(token, tokenHead);
                this.setCache(key, WX_LOGIN_STATUE_LOGIN_SUCESS, "????????????", tokenVO, null, null, null, null, null, null,
                              null);
            }
            //2.2 ????????????
            else
            {
                //?????????????????????????????????????????????
                if (Define.NOI.equals(isLogin))
                {
                    UmsUserOauths umsUserOauths = new UmsUserOauths(null, openId, cacheUserName,
                                                                    this.providerToByte(provider),
                                                                    null, null, null);
                    umsUserOauthsMapper.insertUserOauth(umsUserOauths);

                    this.setCache(key, WX_LOGIN_STATUE_BLIND_SUCESS, "????????????");
                }
            }

        }
        while (false);

        return this.getRedirctUrl(key);
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // ???????????????
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * ????????????
     *
     * @param userName ?????????
     * @param provider ???????????????
     *
     * @return
     */
    @Override
    public Integer isBind(String userName, String provider)
    {
        UmsUserOauths umsUserOauths = umsUserOauthsMapper.getByUserName(userName, this.providerToByte(provider));
        if (Objects.isNull(umsUserOauths))
        {
            return Define.NOI;
        }

        return Define.YESI;
    }

    /**
     * ????????????
     *
     * @param userName ?????????
     * @param provider ???????????????
     *
     * @return
     */
    @Override
    public Integer delBind(String userName, String provider)
    {
        //??????????????????
        UmsUserOauths umsUserOauths = umsUserOauthsMapper.getByUserName(userName, this.providerToByte(provider));
        if (Objects.isNull(umsUserOauths))
        {
            throw new MyException(EC.ERROR_COMMON_RECORD_NOT_EXIST);
        }

        umsUserOauthsMapper.deleteByUserName(userName, this.providerToByte(provider));
        return Define.YESI;
    }

    /**
     * ????????????
     *
     * @param key  ???????????????
     * @param name ?????????or?????????
     * @param pwd  ??????
     *
     * @return
     */
    @Override
    public Integer loginBind(String key, String name, String pwd)
    {
        OauthCacheVO oauthCacheVO = this.getCache(key);
        if (Objects.isNull(oauthCacheVO))
        {
            throw new MyException(EC.ERROR_COMMON_RECORD_NOT_EXIST);
        }

        String openId   = oauthCacheVO.getOpenId();
        String provider = oauthCacheVO.getProvider();

        //1.1 ?????????????????????
        String bindUserName = umsUserOauthsMapper.getUserNameByOauthId(openId);
        if (!StrUtil.isEmptyIfStr(bindUserName))
        {
            throw new MyException(EC.ERROR_USER_NAME_EXIST);
        }

        //2.1 ??????
        this.bind(name, key, openId, provider);

        return Define.YESI;
    }

    /**
     * ????????????
     *
     * @param key ???????????????
     *
     * @return
     */
    @Override
    @Transactional
    public Integer autoBind(String key)
    {
        OauthCacheVO oauthCacheVO = this.getCache(key);
        if (Objects.isNull(oauthCacheVO))
        {
            throw new MyException(EC.ERROR_COMMON_RECORD_NOT_EXIST);
        }

        String openId   = oauthCacheVO.getOpenId();
        String provider = oauthCacheVO.getProvider();
        String nickName = oauthCacheVO.getNickName();

        //1.1 ?????????????????????
        String bindUserName = umsUserOauthsMapper.getUserNameByOauthId(openId);
        if (!StrUtil.isEmptyIfStr(bindUserName))
        {
            throw new MyException(EC.ERROR_USER_NAME_EXIST);
        }

        //2.1 ????????????
        String              userName = null;
        Map<String, Object> userMap  = this.addSystemUser(null, null, null, null, null);
        if (Objects.isNull(userMap))
        {
            throw new MyException(EC.ERROR_DATA_ERROR);
        }

        //2.2 ??????
        this.bind((String) userMap.get("user_name"), key, openId, provider);

        return Define.YESI;
    }

    private void bind(String userName, String key, String openId, String provider)
    {
        String token = this.login(userName, null);

        //2.2 ????????????
        UmsUserOauths umsUserOauths = new UmsUserOauths(null, openId, userName, this.providerToByte(provider),
                                                        null, null, null);
        umsUserOauthsMapper.insertUserOauth(umsUserOauths);

        //????????????
        TokenVO tokenVO = new TokenVO(token, tokenHead);
        this.setCache(key, WX_LOGIN_STATUE_LOGIN_BLIND_SUCESS, "?????????????????????", tokenVO, null, null, null, null, null, null,
                      null);
    }

    /**
     * ????????????Request
     *
     * @param providerId ??????????????????????????????dingding weixin
     *
     * @return AuthRequest
     */
    private AuthRequest getAuthRequest(String providerId)
    {
        Map<String, Object> providers = oauthConf.getProvider();
        Object              provider  = providers.get(providerId);
        if (Objects.isNull(provider))
        {
            throw new MyException(EC.ERROR_PARAM_CONF_ERROR);
        }

        Map<String, String> items = (Map<String, String>) provider;

        String clientId    = items.get("client-id");
        String security    = items.get("client-secret");
        String callBackUrl = oauthConf.getDomain() + oauthConf.getRedirectUrlPrefix() + "/" + providerId;

        AuthConfig authConfig = AuthConfig.builder()
                .clientId(clientId)
                .clientSecret(security)
                .redirectUri(URLUtil.encode(callBackUrl))
                .build();

        if ("dingding".equals(providerId))
        {
            return new AuthDingTalkAccountRequest(authConfig, authStateRedisCache);
        }

        return new AuthWeChatMpRequest(authConfig, authStateRedisCache);
    }

    /**
     * ??????????????????????????????Byte
     *
     * @param provider ???weixin???dingding
     *
     * @return Byte
     */
    private Byte providerToByte(String provider)
    {
        //??????
        if ("weixin".equals(provider))
        {
            return 1;
        }
        //??????
        return 2;
    }


    /**
     * ??????UI???????????????
     *
     * @param key ???????????????
     *
     * @return url
     */
    private String getRedirctUrl(String key)
    {
        OauthCacheVO oauthCacheVO = this.getCache(key);

        return oauthCacheVO.getUiUrl()
                + "?key=" + oauthCacheVO.getKey()
                + "&statue=" + oauthCacheVO.getStatue()
                + "&msg=" + oauthCacheVO.getMsg()
                + "&provider=" + oauthCacheVO.getProvider();
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // ??????
    /*-------------------------------------------------------------------------------------------------------------- */
    private OauthCacheVO getCache(String key)
    {
        return redisT.get(CACHE_KEY + key, OauthCacheVO.class);
    }

    private void setCache(String key,
                          Integer statue, String msg)
    {
        this.setCache(key, statue, msg, null, null, null, null, null, null, null, null);
    }

    private void setCache(String key,
                          Integer statue, String msg, TokenVO tokenVO, String userName,
                          String openId, String avatar, String nickName,
                          String provider, Integer isLogin, String uiUrl)
    {
        OauthCacheVO oauthCacheVO = redisT.get(CACHE_KEY + key, OauthCacheVO.class);
        if (Objects.isNull(oauthCacheVO))
        {
            oauthCacheVO = new OauthCacheVO();
        }

        if (!StrUtil.isEmptyIfStr(key))
        {
            oauthCacheVO.setKey(key);
        }
        if (!StrUtil.isEmptyIfStr(provider))
        {
            oauthCacheVO.setProvider(provider);
        }
        if (!Objects.isNull(isLogin))
        {
            oauthCacheVO.setIsLogin(isLogin);
        }
        if (!StrUtil.isEmptyIfStr(uiUrl))
        {
            oauthCacheVO.setUiUrl(uiUrl);
        }
        if (!Objects.isNull(statue))
        {
            oauthCacheVO.setStatue(statue);
        }
        if (!StrUtil.isEmptyIfStr(msg))
        {
            oauthCacheVO.setMsg(msg);
        }
        if (!Objects.isNull(tokenVO))
        {
            oauthCacheVO.setTokenVO(tokenVO);
        }
        if (!StrUtil.isEmptyIfStr(userName))
        {
            oauthCacheVO.setUserName(userName);
        }

        if (!StrUtil.isEmptyIfStr(openId))
        {
            oauthCacheVO.setOpenId(openId);
        }
        if (!StrUtil.isEmptyIfStr(avatar))
        {
            oauthCacheVO.setAvatar(avatar);
        }
        if (!StrUtil.isEmptyIfStr(nickName))
        {
            oauthCacheVO.setNickName(nickName);
        }

        redisT.set(CACHE_KEY + key, oauthCacheVO, 1800);
    }
}
