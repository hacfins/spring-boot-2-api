package com.langyastudio.edu.admin.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 基础数据类
 *
 * @author Jiaju Zhuang
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserImportParam implements Serializable
{
    /**
     * 用户名
     */
    @ExcelProperty("用户名(必填)")
    private String userName;
    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String fullName;
    /**
     * 手机号
     */
    @ExcelProperty("手机号")
    private String phoneNumber;
    /**
     * 分校列表
     */
    @ExcelProperty("学校名称")
    private String pgNames;
    /**
     * 班级Id号
     */
    @ExcelProperty("班级ID")
    private String classesId;
    /**
     * 执行结果
     */
    @ExcelProperty("结果")
    private String result;
    /**
     * 结果信息
     */
    @ExcelProperty("结果信息")
    private String resultMsg;
}
