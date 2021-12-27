package com.langyastudio.edu.portal.controller.user;

import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.common.anno.LogField;
import com.langyastudio.edu.portal.common.service.ImgService;
import com.langyastudio.edu.portal.common.service.AliSmsService;
import com.langyastudio.edu.portal.common.service.MailService;
import com.langyastudio.edu.portal.bean.dto.ImgCropParam;
import com.langyastudio.edu.portal.bean.dto.UpdateAuthParam;
import com.langyastudio.edu.portal.bean.dto.UpdatePwdParam;
import com.langyastudio.edu.portal.bean.dto.UserParam;
import com.langyastudio.edu.portal.service.AuthService;
import com.langyastudio.edu.portal.service.UserService;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.ResultInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Objects;

/**
 * 账号设置
 */
@Log4j2
@Validated
@RestController
@RequestMapping("/portal/user/user")
public class UserController
{
    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    AliSmsService aliSmsService;
    @Autowired
    MailService   mailService;
    @Autowired
    ImgService    imgService;

    /**
     * 获取用户菜单列表
     *
     * @param schoolId 机构Id号
     *
     * @return
     */
    @GetMapping("/view_list")
    public ResultInfo getViewList(
            @Valid @RequestParam(value = "school_id", required = false) @Size(min = 32, max = 32) String schoolId)
    {
        return ResultInfo.data(authService.getViewList(schoolId));
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/info")
    public ResultInfo getInfo()
    {
        //1.0 need Token
        String userName = userService.getUserName(true);

        return ResultInfo.data(userService.getInfo(userName));
    }

    /**
     * 修改用户信息
     *
     * @return
     */
    @PostMapping("/modify")
    @LogField("修改用户信息")
    public ResultInfo modify(@Valid @RequestBody UserParam userParam)
    {
        //1.0 need Token
        String userName = userService.getUserName(true);

        userParam.setUserName(userName);
        userService.modify(userParam);
        return ResultInfo.data();
    }

    /**
     * 修改手机号
     *
     * @return
     */
    @PostMapping("/modify_phone")
    @LogField("修改手机号")
    public ResultInfo modifyPhone(@Valid @RequestBody UpdateAuthParam userParam)
    {
        //need Token
        String userName = userService.getUserName(true);

        //1.0 检测校验码是否有效
        if (!aliSmsService.checkValid(userParam.getName(), userParam.getVerifyCode(), Define.VERITY_MODIFY))
        {
            return ResultInfo.data(EC.ERROR_VERIFY_ERROR);
        }

        userParam.setUserName(userName);
        userService.modifyPhone(userParam);

        //3.0 删除校验码
        aliSmsService.del(userParam.getName(), Define.VERITY_MODIFY);

        return ResultInfo.data();
    }

    /**
     * 修改用户密码
     *
     * @param pwdParam 密码参数列表
     *
     * @return
     */
    @PostMapping("/modify_pwd")
    @LogField("修改用户密码")
    public ResultInfo modifyPwd(@Validated @RequestBody UpdatePwdParam pwdParam)
    {
        //1.0 need Token
        String userName = userService.getUserName(true);

        pwdParam.setUserName(userName);
        userService.modifyPassword(pwdParam);
        return ResultInfo.data();
    }

    /**
     * 保存用户头像
     *
     * @return
     */
    @PostMapping("/modify_avator")
    public ResultInfo modifyAvator(@Valid @RequestBody ImgCropParam imgCropParam)
    {
        //1.0 need Token
        String userName = userService.getUserName(true);

        //2.0 进行文件裁剪
        String imgPath = imgCropParam.getImgPath();

        if (Objects.nonNull(imgCropParam.getX()) &&
                Objects.nonNull(imgCropParam.getY()) &&
                Objects.nonNull(imgCropParam.getWidth()) &&
                Objects.nonNull(imgCropParam.getHeight()))
        {
            imgPath = imgService.crop(imgCropParam.getImgPath(), imgCropParam.getX(), imgCropParam.getY(),
                                      imgCropParam.getWidth(), imgCropParam.getHeight());
        }

        userService.modifyAvator(userName, imgPath);
        return ResultInfo.data(Map.of("img_path", imgPath));
    }
}
