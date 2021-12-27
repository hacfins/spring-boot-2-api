package com.langyastudio.edu.admin.controller.auth;

import com.langyastudio.edu.admin.service.AuthService;
import com.langyastudio.edu.common.anno.LogField;
import com.langyastudio.edu.common.data.ResultInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * 权限管理
 */
@Log4j2
@Validated
@RestController
@RequestMapping("/admin/auth/auth")
public class AuthController
{
    @Autowired
    AuthService authService;

    /**
     * 获取权限列表
     *
     * @param roleId 权限Id号
     *
     * @return
     */
    @GetMapping("/get_list")
    public ResultInfo getList(@Valid @RequestParam(value = "role_id") @Size(min = 32, max = 32) String roleId)
    {
        //登录验证
        String userName = authService.getUserName(true);

        return ResultInfo.data(authService.getAuthListByRoleId(roleId));
    }

    /**
     * 修改角色权限
     *
     * @param roleApis 参数
     *                 role_id 角色id号
     *                 api_ids 权限列表(多个使用竖杠分割)
     *
     * @return
     */
    @PostMapping("/set_rules")
    @LogField("修改角色权限")
    public ResultInfo setRules(@Valid @RequestBody Map<String, String> roleApis)
    {
        //登录验证
        String userName = authService.getUserName(true);

        authService.setAuthByRoleId(roleApis.get("role_id"), roleApis.get("api_ids"));
        return ResultInfo.data();
    }
}
