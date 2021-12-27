package com.langyastudio.edu.portal.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.ResultInfo;
import com.langyastudio.edu.common.entity.UserAgentData;
import com.langyastudio.edu.common.entity.WebLog;
import com.langyastudio.edu.common.service.LogService;
import com.langyastudio.edu.db.mapper.UmsLogsMapper;
import com.langyastudio.edu.db.model.UmsLogs;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 监控服务
 */
@Service
public class MonitorServiceImpl extends BaseServiceImpl implements MonitorService, LogService
{
    @Autowired
    UmsLogsMapper logsMapper;

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
