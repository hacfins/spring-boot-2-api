package com.langyastudio.edu.admin.controller.monitor;

import com.langyastudio.edu.admin.service.MonitorService;
import com.langyastudio.edu.common.data.ResultInfo;
import com.langyastudio.edu.common.util.Tool;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


/**
 * 日志管理
 */
@Log4j2
@RestController
@RequestMapping("/admin/monitor/log")
public class LogController
{
    @Autowired
    MonitorService monitorService;

    /**
     * 登录日志
     *
     * @param offset   offset
     * @param pageSize pageSize
     *
     * @return
     */
    @GetMapping("/login")
    public ResultInfo getLoginList(@Valid @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                   @Valid @RequestParam(value = "page_size", defaultValue = "20") @Min(1) @Max(500) Integer pageSize)
    {
        //登录验证
        String loginUserName = monitorService.getUserName(true);

        return ResultInfo.data(monitorService.getLogLoginList(loginUserName, offset, pageSize));
    }

    /**
     * 系统操作日志
     *
     * @param userNameKey 用户名检索关键字
     * @param phoneKey    电话号码检索关键字
     * @param fullNameKey 姓名检索关键字
     * @param startTime   操作的开始时间
     * @param endTime     操作的结束时间
     * @param offset      offset
     * @param pageSize    pageSize
     *
     * @return
     */
    @GetMapping("/system")
    public ResultInfo getSystemList(@Valid @RequestParam(value = "user_name_key", required = false) String userNameKey,
                                    @Valid @RequestParam(value = "phone_key", required = false) String phoneKey,
                                    @Valid @RequestParam(value = "full_name_key", required = false) String fullNameKey,
                                    @Valid @RequestParam(value = "start_time", required = false) String startTime,
                                    @Valid @RequestParam(value = "end_time", required = false) String endTime,
                                    @Valid @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                    @Valid @RequestParam(value = "page_size", defaultValue = "20") @Min(1) @Max(500) Integer pageSize)
    {
        //登录验证
        String loginUserName = monitorService.getUserName(true);

        return ResultInfo.data(monitorService.getLogSystemList(userNameKey, fullNameKey, phoneKey,
                                                               Tool.dateTimeToEntiry(startTime),
                                                               Tool.dateTimeToEntiry(endTime),
                                                               offset, pageSize));
    }

    /**
     * 老师活跃度
     *
     * @param schoolId
     * @param startTime
     * @param endTime
     * @param offset
     * @param pageSize
     *
     * @return
     */
    @GetMapping("/get_list")
    public ResultInfo getList(@Valid @RequestParam(value = "school_id", required = false) @Size(min = 32, max = 32) String schoolId,
                              @Valid @RequestParam(value = "start_time", required = false) String startTime,
                              @Valid @RequestParam(value = "end_time", required = false) String endTime,
                              @Valid @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                              @Valid @RequestParam(value = "page_size", defaultValue = "20") @Min(1) @Max(500) Integer pageSize)
    {
        //登录验证
        String loginUserName = monitorService.getUserName(true);

        return ResultInfo.data(monitorService.getLogUserList(schoolId, Tool.dateTimeToEntiry(startTime),
                                                             Tool.dateTimeToEntiry(endTime),
                                                             offset, pageSize));
    }
}
