package com.langyastudio.edu.portal.controller.common;

import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.common.service.ImgService;
import com.langyastudio.edu.portal.common.service.AliSmsService;
import com.langyastudio.edu.portal.common.service.MailService;
import com.langyastudio.edu.portal.service.PwdService;
import com.langyastudio.edu.common.anno.InValue;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.ResultInfo;
import com.langyastudio.edu.common.exception.MyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Objects;

/**
 * 通用请求
 */
@Log4j2
@Validated
@RestController
@RequestMapping("/portal/common/common")
public class CommonController
{
    @Autowired
    ImgService imgService;

    @Autowired
    PwdService pwdService;

    @Autowired
    AliSmsService aliSmsService;
    @Autowired
    MailService   mailService;

    /*-------------------------------------------------------------------------------------------------------------- */
    // 用户名手机号是否存在
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 用户名是否存在
     *
     * @param userName 用户名
     *
     * @return
     */
    @GetMapping("/exist_name")
    public ResultInfo existName(@Valid @RequestParam(value = "user_name") @Size(min = 2, max = 20) String userName)
    {
        return ResultInfo.data(pwdService.existName(userName));
    }

    /**
     * 手机号是否存在
     *
     * @param phone  手机号
     * @param except 是否排除当前登录用户
     *
     * @return
     */
    @GetMapping("/exist_phone")
    public ResultInfo existPhone(@Valid @RequestParam(value = "phone") @Size(min = 11, max = 11) String phone,
                                 @Valid @RequestParam(value = "except", defaultValue = "") @Size(min = 0,
                                         max = 20) String except)
    {
        return ResultInfo.data(pwdService.existPhone(phone, except));
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // 文件操作
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 上传图片
     */
    @PostMapping("/upload_img")
    public ResultInfo uploadImg(@RequestParam("file") MultipartFile[] file) throws MyException
    {
        //need token
        String userName = pwdService.getUserName(true);

        //只能上传单文件
        if (Objects.isNull(file) || file.length < 1)
        {
            throw new MyException(EC.ERROR_PARAM_FILE_EMPTY);
        }
        if (file.length > 1)
        {
            throw new MyException(EC.ERROR_PARAM_FILE_NUM_ERROR);
        }

        //上传文件
        return ResultInfo.data(Map.of("img_path", imgService.upload(file[0])));
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // 校验码
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param type  校验码类型
     *              1 注册
     *              2 修改手机号
     *              3 找回密码
     *              4 登录
     *
     * @return
     *
     * @throws Exception
     */
    @GetMapping("/send_sms")
    public ResultInfo sengSms(@Valid @RequestParam(value = "phone") String phone,
                              @Valid @RequestParam(value = "type") @InValue({Define.VERITY_REGISTER,
                                      Define.VERITY_MODIFY, Define.VERITY_FINDPWD, Define.VERITY_LOGIN}) String type) throws Exception
    {
        //1.0 need Token
        if (Define.VERITY_MODIFY.equals(type))
        {
            String userName = pwdService.getUserName(true);
        }

        aliSmsService.sendSms(phone, type);
        return ResultInfo.data();
    }

    /**
     * 发送邮件验证码
     *
     * @param email 邮箱
     * @param type  类型
     *              1 注册
     *              2 修改邮箱
     *              3 找回密码
     *              4 登录
     *
     * @return
     *
     * @throws MyException
     */
    @GetMapping("/send_email")
    public ResultInfo sengEmail(@Valid @RequestParam(value = "phone") @Email String email,
                                @Valid @RequestParam(value = "type") @InValue({Define.VERITY_REGISTER,
                                        Define.VERITY_MODIFY, Define.VERITY_FINDPWD, Define.VERITY_LOGIN}) String type) throws MyException
    {
        mailService.sendEmail(email, type);
        return ResultInfo.data();
    }

    /**
     * 地址列表
     *
     * @param parentId 父节点
     * @param level    层级数目
     *
     * @return
     */
    @RequestMapping("/get_list")
    public ResultInfo getList(@Valid @RequestParam(value = "parent_id", defaultValue = "0") @Min(0) Integer parentId,
                              @Valid @RequestParam(value = "level", defaultValue = Define.YES) Integer level)
    {
        return ResultInfo.data(pwdService.getListAddress(parentId, level));
    }
}
