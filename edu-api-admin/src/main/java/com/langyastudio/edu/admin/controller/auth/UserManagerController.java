package com.langyastudio.edu.admin.controller.auth;

import com.langyastudio.edu.admin.bean.dto.UserManagerParam;
import com.langyastudio.edu.admin.common.data.Define;
import com.langyastudio.edu.common.anno.InValue;
import com.langyastudio.edu.common.anno.LogField;
import com.langyastudio.edu.common.anno.PhoneOrEmpty;
import com.langyastudio.edu.db.model.UmsUser;
import com.langyastudio.edu.admin.service.AuthService;
import com.langyastudio.edu.common.data.ResultInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * 用户管理
 */
@Log4j2
@Validated
@RestController
@RequestMapping("/admin/auth/user")
public class UserManagerController
{
    @Autowired
    AuthService authService;

    /**
     * 获取授权用户列表
     *
     * @param roleId      角色Id号
     * @param userNameKey 用户名检索关键字
     * @param phoneKey    电话号码检索关键字
     * @param fullNameKey 姓名检索关键字
     * @param isAsc       排序方式
     * @param offset      offset
     * @param pageSize    分页大小
     *
     * @return
     */
    @GetMapping("/get_list")
    public ResultInfo getUserList(@Valid @RequestParam(value = "role_id", required = false) @Size(min = 32, max = 32) String roleId,
                                  @Valid @RequestParam(value = "user_name_key", required = false) String userNameKey,
                                  @Valid @RequestParam(value = "phone_key", required = false) String phoneKey,
                                  @Valid @RequestParam(value = "full_name_key", required = false) String fullNameKey,
                                  @Valid @RequestParam(value = "is_asc", defaultValue = Define.YES) Integer isAsc,
                                  @Valid @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                  @Valid @RequestParam(value = "page_size", defaultValue = "20") @Min(1) @Max(500) Integer pageSize)
    {
        //登录验证
        String loginUserName = authService.getUserName(true);

        return ResultInfo.data(authService.getUserList(roleId, userNameKey, fullNameKey, phoneKey, isAsc,
                                                       offset, pageSize));
    }

    /**
     * 设置用户系统角色
     *
     *
     * @return
     */
    @PostMapping("/set_roles")
    @LogField("设置用户系统角色")
    public ResultInfo setUserRoles(@Valid @RequestBody UserManagerParam param)
    {
        //登录验证
        String loginUserName = authService.getUserName(true);

        authService.setUserRoles(param.getUserName(), param.getRoleIds(), null);
        return ResultInfo.data();
    }

    /**
     * 设置用户机构角色
     *
     *
     * @return
     */
    @PostMapping("/set_school_roles")
    @LogField("设置用户机构角色")
    public ResultInfo setSchoolUserRoles(@Valid @RequestBody UserManagerParam param)
    {
        //登录验证
        String loginUserName = authService.getUserName(true);

        authService.setUserRoles(param.getUserName(), param.getRoleIds(), param.getSchoolId());
        return ResultInfo.data();
    }

    /**
     * 添加用户
     *
     * @param userName 用户名
     * @param fullName 姓名
     * @param phone    电话号码
     * @param sex      性别（1 男 2 女 3 未知）
     *
     * @return
     */
    @GetMapping("/add")
    @LogField("添加用户")
    public ResultInfo addUser(@Valid @RequestParam(value = "user_name") @Size(min = 2, max = 20) String userName,
                              @Valid @RequestParam(value = "full_name", required = false) @Size(min = 2, max = 20) String fullName,
                              @Valid @RequestParam(value = "phone", required = false) @PhoneOrEmpty String phone,
                              @Valid @RequestParam(value = "sex", defaultValue = UmsUser.USER_SEX_UNKNOWN) @InValue(value = {"1", "2", "3"}) Integer sex)
    {
        //登录验证
        String loginUserName = authService.getUserName(true);

        return ResultInfo.data(authService.addSystemUser(userName, null, fullName, phone, sex, null, Define.YESI));
    }

    /**
     * 修改系统用户
     *
     * @param userName 用户名
     * @param fullName 姓名
     * @param phone    手机号
     * @param sex      性别
     *
     * @return
     */
    @GetMapping("/modify")
    @LogField("修改系统用户")
    public ResultInfo modifyUser(@Valid @RequestParam(value = "user_name") @Size(min = 2, max = 20) String userName,
                                 @Valid @RequestParam(value = "full_name", required = false) @Size(min = 2, max = 20) String fullName,
                                 @Valid @RequestParam(value = "phone", required = false) @PhoneOrEmpty String phone,
                                 @Valid @RequestParam(value = "sex", required = false) @InValue(value = {"1", "2", "3"
                                 }) Integer sex)
    {
        //登录验证
        String loginUserName = authService.getUserName(true);

        authService.modifyUser(userName, fullName, phone, sex);
        return ResultInfo.data();
    }

    /**
     * 重置密码
     *
     * @param userName 用户名
     *
     * @return
     */
    @GetMapping("/reset_pwd")
    @LogField("重置密码")
    public ResultInfo resetPwd(@Valid @RequestParam(value = "user_name") @Size(min = 2, max = 20) String userName)
    {
        //登录验证
        String loginUserName = authService.getUserName(true);

        authService.resetPwd(userName);
        return ResultInfo.data();
    }

    /**
     * 启用禁用用户
     *
     *
     * @return
     */
    @PostMapping("/enabled")
    @LogField("启用禁用用户")
    public ResultInfo enableUser(@Valid @RequestBody UserManagerParam param)
    {
        //登录验证
        String loginUserName = authService.getUserName(true);

        authService.enableUser(param.getUserName(), param.getEnabled());
        return ResultInfo.data();
    }
}
