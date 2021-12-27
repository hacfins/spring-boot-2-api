package com.langyastudio.edu.common.data;

/**
 * 常用API操作码
 */
public enum EC implements IErrorCode{

    /* -----------------成功---------------------------- */
    SUCCESS(111111, "请求成功")
    ,ERROR(111112, "请求异常")


    /* ------------------999999 系统错误--------------------------- */
    , ERROR_SYSTEM_EXCEPTION(999999, "系统异常")


    /* ------------------101xxx 请求错误--------------------------- */
    , ERROR_REQUEST_METHOD_NOT_SUPPORT(101001, "请求方法不支持")
    , ERROR_REQUEST_METHOD_NOT_EXIST(101002, "请求方法不存在")
    , ERROR_REQUEST_API_SIGNATURE_ERROR(101003, "API应用接入签名有误")

    //权限
    , ERROR_USER_UNAUTHORIZED(101101, "暂未登录或已经过期")
    , ERROR_USER_FORBIDDEN(101102, "没有相关权限")
    , ERROR_VERIFY_ERROR(101103, "校验码不正确")


    /* ------------------102xxx 参数错误--------------------------- */
    , ERROR_PARAM_NOT_NULL(102001, "参数不能为空")
    , ERROR_PARAM_EXCEPTION(102002, "参数异常")
    , ERROR_PARAM_ILLEGAL(102003, "参数非法")

    , ERROR_PARAM_EMAIL_EMPTY(102031, "邮箱地址不能为空")
    , ERROR_PARAM_FILE_EMPTY(102032, "上传文件不能为空")
    , ERROR_PARAM_FILE_NUM_ERROR(102033, "只能上传单文件")
    , ERROR_PARAM_CONF_ERROR(102034, "配置项不存在")


    /* ------------------103xxx 数据存储错误--------------------------- */
    , ERROR_DATA_ERROR(103000, "数据操作异常")
    , ERROR_DATA_SAVE_FAILURE(103001, "数据保存失败")
    , ERROR_DATA_UPDATE_FAILURE(103002, "数据修改失败")
    , ERROR_DATA_DELETE_FAILURE(103003, "数据删除失败")
    , ERROR_DATA_GET_FAILURE(103004, "数据获取失败")

    , ERROR_FILE_SIZE_ERROR(103101, "文件大小超标")
    , ERROR_FILE_UPLOAD_ERROR(103102, "文件上传失败")
    , ERROR_FILE_SAVE_ERROR(103103, "文件存储失败")
    , ERROR_FILE_PATH_ERROR(103104, "文件路径不合法")
    , ERROR_FILE_PART_NOTEXIST(103105, "分片数据未找到")
    , ERROR_FILE_NOPERMISSON(103106, "无法创建文件夹")
    , ERROR_FILE_DOWNLOAD_ERROR(103107, "文件下载失败")


    /* ------------------104xxx 逻辑层相关的错误--------------------------- */
    //通用
    , ERROR_COMMON_RECORD_NOT_EXIST(104000, "数据不存在")


    //用户
    , ERROR_USER_NAME_EXIST(104101, "用户已存在")
    , ERROR_USER_NOT_EXIST(104102, "用户不存在")
    , ERROR_USER_PASSWORD_INCORRECT(104003, "用户名或密码不正确")
    , ERROR_USER_LOCKED(104104, "用户被锁定")
    , ERROR_USER_EXPIRE(104105, "用户已过期")
    , ERROR_USER_ARREARS(104106, "用户已欠费")
    , ERROR_USER_PHONE_EXIST(104107, "手机号已存在")
    , ERROR_USER_ENABLED(104108, "用户已被禁用")
    , ERROR_USER_PWD_SAME(104109, "新旧密码相同")
    , ERROR_USER_PASSWORD_ERROR(104110, "旧密码不正确")
    , ERROR_USER_LEAVE_ERROR(104111, "用户已离职")
    , ERROR_USER_PARAM_ERROR(104112, "用户名非法")
    , ERROR_USER_PHONE_PARAM_ERROR(104112, "手机号非法")
    , ERROR_USER_FULLNAME_ERROR(104113, "姓名非法")


    //角色
    , ERROR_ROLE_NAME_RE(104131, "角色重名")
    , ERROR_ROLE_NOT_EXIST(104132, "角色不存在")
    , ERROR_ROLE_CANT_DEL(104133, "内置角色不能删除")
    , ERROR_ROLE_USER_EXIST(104134, "角色下存在用户")


    //文件转码
    , ERROR_FILE_BROWSE_IMG_SIZE(104251, "图片过大，不支持在线浏览")
    , ERROR_FILE_TASK_STATUE(104252, "转码失败")
    , ERROR_FILE_TASK_STATUE_RUNNING(104253, "正在转码")
    , ERROR_FILE_TASK_CVT_NOTEXIST(104254, "转码后的文件已丢失")
    , ERROR_FILE_DOWNLOAD_SIZE(104255, "文件打包下载错误")
    , ERROR_FILE_DOWNLOAD_SINGLE_SIZE(104256, "单文件不得超过100M")
    , ERROR_FILE_NOTIMAGE(104257, "文件类型不是图片")
    , ERROR_FILE_NOTEXIST_IMAGE(104258, "文件不存在或不是图片")
    , ERROR_FILE_NOTEXIST_PERMISSION(104259, "文件不存在或无读取权限")
    , ERROR_FILE_IMAGE_LOAD(104260, "图片文件加载失败")


    /* ------------------106xxx 服务调用相关的错误--------------------------- */
    , ERROR_SERVICE_CALL_EXCEPTION(106001, "服务调用异常")
    , ERROR_SERVICE_RESPONSE_EXCEPTION(106002, "服务响应异常")

    //阿里云
    , ERROR_ALIYUN_SERVICE_SMS_LIMIT(106101, "每分钟只能发送一条短信")


    //微信
    , ERROR_WX_ACCESSTOKEN_FAILD_ERROR(106301, "获取的AccessToken失败")
    , ERROR_WX_TICKET_JSAPI_FAILD_ERROR(106302, "获取的jsapi ticket失败")



    ;

    /**
     * 返回码
     */
    private final int code;

    /**
     * 提示信息
     */
    private final String msg;

    private EC(int code, String msg)
    {
        this.code = code;
        this.msg  = msg;
    }

    @Override
    public Integer getCode()
    {
        return code;
    }

    @Override
    public String getMsg()
    {
        return msg;
    }
}