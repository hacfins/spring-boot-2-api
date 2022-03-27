package com.langyastudio.edu.admin.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserManagerParam implements Serializable
{
    /**
     * 用户名
     */
    @Size(min = 2, max = 20)
    private String userName;

    /**
     * 状态
     */
    @Min(1) @Max(2)
    private Integer enabled;

    /**
     * 机构id
     */
    @Size(min = 32, max = 32)
    private String schoolId;

    /**
     * 角色id
     */
    @Size(min = 32, max = 500)
    private String roleIds;

}
