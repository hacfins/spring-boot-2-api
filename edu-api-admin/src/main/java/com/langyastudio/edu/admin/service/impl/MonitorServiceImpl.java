package com.langyastudio.edu.admin.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.langyastudio.edu.admin.common.data.Define;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.PageIn;
import com.langyastudio.edu.common.data.PageInfo;
import com.langyastudio.edu.common.data.ResultInfo;
import com.langyastudio.edu.common.entity.UserAgentData;
import com.langyastudio.edu.common.entity.WebLog;
import com.langyastudio.edu.common.util.Tool;
import com.langyastudio.edu.db.mapper.UmsLogsMapper;
import com.langyastudio.edu.db.mapper.UmsUserLoginLogsMapper;
import com.langyastudio.edu.admin.service.MonitorService;
import com.langyastudio.edu.common.service.LogService;
import com.langyastudio.edu.db.model.UmsLogs;
import com.langyastudio.edu.db.model.UmsUser;
import com.langyastudio.edu.db.model.UmsUserLoginLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 日志、监控、系统信息等
 */
@Service
public class MonitorServiceImpl extends BaseServiceImpl implements MonitorService, LogService
{
    @Autowired
    UmsUserLoginLogsMapper umsUserLoginLogsMapper;

    @Autowired
    UmsLogsMapper logsMapper;

    /*-------------------------------------------------------------------------------------------------------------- */
    // 日志
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 获取登录日志信息
     *
     * @param userName 用户名
     * @param offSet   offSet
     * @param pageSize pageSize
     *
     * @return
     */
    @Override
    public PageInfo<UmsUserLoginLogs> getLogLoginList(String userName, Integer offSet, Integer pageSize)
    {
        //获取原始数据
        PageIn<UmsUserLoginLogs> pageIns = new PageIn<>(offSet, pageSize);
        umsUserLoginLogsMapper.getListByUserName(userName, pageIns);

        PageInfo<UmsUserLoginLogs> pageInfos = new PageInfo<>();
        pageInfos.setTotal(pageIns.getTotal());
        pageInfos.setList(pageIns.getRecords());

        return pageInfos;
    }

    /**
     * 获取操作日志列表
     *
     * @param schoolId    机构Id号
     * @param userNameKey 用户名关键字
     * @param fullNameKey 姓名关键字
     * @param phoneKey    电话号码关键字
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param offSet      offset
     * @param pageSize    pagesize
     *
     * @return
     */
    private PageInfo<Map<String, Object>> getLogSchoolList(String schoolId, String loginUserName, Integer sgId,
                                                          String userNameKey, String fullNameKey, String phoneKey,
                                                          LocalDateTime startTime, LocalDateTime endTime,
                                                          Integer offSet, Integer pageSize)
    {
        //分校path
        List<String> sgPaths = null;
        if (schoolId != null && !schoolId.equals(Define.SHOOL_SYSTEM_ID))
        {
            //机构存在检查
        }

        //获取原始数据
        PageIn<UmsLogs> pageIns = new PageIn<>(offSet, pageSize);
        logsMapper.getListBySchoolId(schoolId, sgPaths, startTime, endTime, userNameKey, fullNameKey, phoneKey,
                                     UmsUser.USER_TYPE_USER, pageIns);

        List<Map<String, Object>> result = new ArrayList<>();
        if (pageIns.getTotal() > 0)
        {
            Map<String, Object> item    = null;
            UmsUser             umsUser = null;
            for (UmsLogs umsLogs : pageIns.getRecords())
            {
                item = Tool.beanToMap(umsLogs);

                String userName = (String) item.get("user_name");
                umsUser = umsUserMapper.getInfoByUserName(userName);
                if (Objects.nonNull(umsUser))
                {
                    item.put("full_name", umsUser.getFullName());
                    item.put("nick_name", umsUser.getNickName());
                }

                result.add(item);
            }
        }

        return new PageInfo<>(pageIns.getTotal(), result);
    }

    /**
     * 用户活跃度
     *
     * @param schoolId
     * @param startTime
     * @param endTime
     * @param offSet
     * @param pageSize
     *
     * @return
     */
    @Override
    public PageInfo<Map<String, Object>> getLogUserList(String schoolId, LocalDateTime startTime, LocalDateTime endTime,
                                                        Integer offSet, Integer pageSize)
    {
        if (schoolId != null && !schoolId.equals(Define.SHOOL_SYSTEM_ID))
        {

        }

        //获取原始数据
        PageIn<Map<String, Object>> pageIns = new PageIn<>(offSet, pageSize);
        umsUserLoginLogsMapper.getListByActive(schoolId, startTime, endTime, pageIns);

        List<Map<String, Object>> result = new ArrayList<>();
        if (pageIns.getTotal() > 0)
        {
            UmsUser umsUser = null;
            for (Map<String, Object> item : pageIns.getRecords())
            {
                String userName = (String) item.get("user_name");
                umsUser = umsUserMapper.getInfoByUserName(userName);
                if (Objects.nonNull(umsUser))
                {
                    item.put("full_name", umsUser.getFullName());
                }

                result.add(item);
            }
        }

        return new PageInfo<>(pageIns.getTotal(), result);
    }

    /**
     * 获取系统日志列表
     *
     * @param userNameKey 用户名关键字
     * @param fullNameKey 姓名关键字
     * @param phoneKey    电话号码关键字
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param offSet      offset
     * @param pageSize    pagesize
     *
     * @return
     */
    @Override
    public PageInfo<Map<String, Object>> getLogSystemList(String userNameKey, String fullNameKey, String phoneKey,
                                                          LocalDateTime startTime, LocalDateTime endTime,
                                                          Integer offSet,
                                                          Integer pageSize)
    {
        return this.getLogSchoolList(Define.SHOOL_SYSTEM_ID, null, null, userNameKey, fullNameKey, phoneKey,
                                     startTime, endTime, offSet, pageSize);
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // 写入日志
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 写入系统日志!!!
     *
     * @param webLog WebLog
     */
    @Override
    public void addLog(WebLog webLog)
    {
        //spring security 设置了用户名
        String userName = webLog.getUserName();

        //1.0 userName
        if (StrUtil.isNotBlank(userName))
        {
            UmsLogs umsLogs = new UmsLogs();
            umsLogs.setUserName(userName);

            //2.0 operator info
            Map<String, Object> paramsMap = webLog.getParameter();
            Map<String, Object> desParams = new HashMap<>();

            umsLogs.setSchoolId(Define.SHOOL_SYSTEM_ID);
            if (MapUtil.isNotEmpty(paramsMap))
            {
                //schoolId
                if (StrUtil.isNotBlank(webLog.getSchoolId()))
                {
                    umsLogs.setSchoolId(webLog.getSchoolId());
                }

                //移除空值
                for (String key : paramsMap.keySet())
                {
                    Object obj = paramsMap.get(key);
                    if (Objects.nonNull(obj) && !(obj instanceof MultipartFile))
                    {
                        desParams.put(key, obj);
                    }
                }
            }
            umsLogs.setOpId(IdUtil.simpleUUID());
            umsLogs.setOpUrl(webLog.getUri());
            umsLogs.setOpComment(webLog.getDescription());
            umsLogs.setOpParams(JSON.toJSONString(desParams));
            umsLogs.setUseTime(webLog.getSpendTime());

            //请求结果
            umsLogs.setOpResult(EC.ERROR.getCode());
            if (webLog.getResult() instanceof ResultInfo)
            {
                umsLogs.setOpResult(((ResultInfo) webLog.getResult()).getCode());
            }

            //3.0 agent info
            UserAgentData userAgent = webLog.getUserAgent();
            umsLogs.setLocation(userAgent.getLocation());
            umsLogs.setOsName(userAgent.getOsName());
            umsLogs.setIp(userAgent.getIp());
            umsLogs.setBrowseName(userAgent.getBrowseName());

            logsMapper.insertLog(umsLogs);
        }
    }
}
