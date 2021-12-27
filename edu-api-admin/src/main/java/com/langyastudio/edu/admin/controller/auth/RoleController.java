package com.langyastudio.edu.admin.controller.auth;

import com.langyastudio.edu.admin.common.data.Define;
import com.langyastudio.edu.common.anno.InValue;
import com.langyastudio.edu.common.anno.LogField;
import com.langyastudio.edu.db.model.UmsRole;
import com.langyastudio.edu.admin.service.RoleService;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.ResultInfo;
import com.langyastudio.edu.common.exception.MyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * 角色管理
 */
@Log4j2
@Validated
@RestController
@RequestMapping("/admin/auth/role")
public class RoleController
{
    @Autowired
    RoleService roleService;

    /**
     * 角色信息
     *
     * @param roleId 角色Id号
     */
    @GetMapping("/info")
    public ResultInfo info(@Valid @RequestParam(value = "role_id") @Size(min = 32, max = 32) String roleId)
    {
        //登录验证
        String userName = roleService.getUserName(true);

        UmsRole umsRoleInfo = roleService.getInfo(roleId);
        if (null != umsRoleInfo)
        {
            return ResultInfo.data(umsRoleInfo);
        }

        return ResultInfo.data(EC.ERROR_ROLE_NOT_EXIST);
    }

    /**
     * 角色列表
     *
     * @param isSystem 是否是系统角色
     * @param isAsc    是否升序
     * @param offset   offset
     * @param pageSize pageSize
     */
    @GetMapping("/get_list")
    public ResultInfo getList(@Valid @RequestParam(value = "is_system") @InValue Integer isSystem,
                              @Valid @RequestParam(value = "is_asc", defaultValue = Define.YES) Integer isAsc,
                              @Valid @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                              @Valid @RequestParam(value = "page_size", defaultValue = "20") @Min(1) @Max(500) Integer pageSize)
    {
        //登录验证
        String userName = roleService.getUserName(true);

        return ResultInfo.data(roleService.getList(isSystem, isAsc, offset, pageSize));
    }

    /**
     * 添加角色
     *
     * @param roleName    角色名称
     * @param viewSystem  访问系统后台
     * @param viewSchool  访问机构后台
     * @param isSystem    是否是系统角色
     * @param description 描述信息
     *
     * @return
     *
     * @throws MyException
     */
    @GetMapping("/add")
    @LogField("添加角色")
    public ResultInfo add(@Valid @RequestParam(value = "role_name") @Size(min = 2, max = 20) String roleName,
                          @Valid @RequestParam(value = "view_system", defaultValue = Define.NO) @InValue Integer viewSystem,
                          @Valid @RequestParam(value = "view_school", defaultValue = Define.NO) @InValue Integer viewSchool,
                          @Valid @RequestParam(value = "is_system") @Min(1) @Max(2) Integer isSystem,
                          @Valid @RequestParam(value = "description", defaultValue = "") @Size(min = 0, max = 128) String description) throws MyException
    {
        //登录验证
        String userName = roleService.getUserName(true);

        UmsRole umsRole = roleService.add(roleName, viewSystem, viewSchool, isSystem, description);
        return ResultInfo.data(umsRole);
    }

    /**
     * 修改角色
     *
     * @param roleId     角色id号
     * @param roleName   角色名称
     * @param viewSystem 访问系统后台
     * @param viewSchool 访问机构后台
     *
     * @return
     *
     * @throws MyException
     */
    @GetMapping("/modify")
    @LogField("修改角色")
    public ResultInfo modify(@Valid @RequestParam(value = "role_id") @Size(min = 32, max = 32) String roleId,
                             @Valid @RequestParam(value = "role_name", required = false) @Size(min = 2, max = 20) String roleName,
                             @Valid @RequestParam(value = "view_system", required = false) @InValue Integer viewSystem,
                             @Valid @RequestParam(value = "view_school", required = false) @InValue Integer viewSchool,
                             @Valid @RequestParam(value = "description", required = false) @Size(min = 0, max = 128) String description) throws MyException
    {
        //登录验证
        String userName = roleService.getUserName(true);

        roleService.modify(roleId, roleName, viewSystem, viewSchool, description);
        return ResultInfo.data();
    }

    /**
     * 移动角色
     *
     * @param roleId 原始角色Id号
     * @param desId  目的角色Id号
     *
     * @return
     */
    @GetMapping("/move_to")
    public ResultInfo moveTo(@Valid @RequestParam(value = "role_id") @Size(min = 32, max = 32) String roleId,
                             @Valid @RequestParam(value = "des_id") @Size(min = 32, max = 32) String desId)
    {
        //登录验证
        String userName = roleService.getUserName(true);

        roleService.moveTo(roleId, desId);
        return ResultInfo.data();
    }

    /**
     * 角色是否存在
     *
     * @param roleName 角色名称
     * @param isSystem 是否是系统角色
     *
     * @return
     */
    @GetMapping("/exist_name")
    public ResultInfo existName(@Valid @RequestParam(value = "role_name") @Size(min = 2, max = 20) String roleName,
                                @Valid @RequestParam(value = "is_system", defaultValue = Define.NO) @Min(1) @Max(2) Integer isSystem)
    {
        //登录验证
        String userName = roleService.getUserName(true);

        Integer iExist = roleService.existName(roleName, isSystem);
        return ResultInfo.data(Map.of("exist", iExist));
    }

    /**
     * 删除角色
     *
     * @param roleId 角色id号
     *
     * @return
     */
    @GetMapping("/del")
    @LogField("删除角色")
    public ResultInfo del(@Valid @RequestParam(value = "role_id") @Size(min = 32, max = 32) String roleId)
    {
        //登录验证
        String userName = roleService.getUserName(true);

        roleService.del(roleId);
        return ResultInfo.data();
    }
}
