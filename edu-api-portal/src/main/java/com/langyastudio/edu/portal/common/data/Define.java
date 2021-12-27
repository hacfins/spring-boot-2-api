package com.langyastudio.edu.portal.common.data;

import com.langyastudio.edu.common.data.BasDefine;

/**
 * 常量与配置文件
 * <p>
 * 注意：表字段的常量值定义到ORM
 */
public class Define extends BasDefine
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // 常用常量定义
    /*-------------------------------------------------------------------------------------------------------------- */
    public final static Integer SUCESSS = 1;

    //是 or 否
    public final static String  YES  = "1";
    public final static String  NO   = "2";
    public final static Integer YESI = 1;
    public final static Integer NOI  = 2;

    //校验码
    public final static String VERITY_REGISTER = "1";
    public final static String VERITY_MODIFY   = "2";
    public final static String VERITY_FINDPWD  = "3";
    public final static String VERITY_LOGIN    = "4";

    //默认分页的起始位置
    public final static Integer PAGE_OFFSET   = 0;
    //默认每页最大数量
    public final static Integer PAGE_MAX_SIZE = 500;


    /*-------------------------------------------------------------------------------------------------------------- */
    // 资源
    /*-------------------------------------------------------------------------------------------------------------- */
    //-超过2M再计算hash
    public final static Integer FILE_HASH_MIN_SIZE = 2097152;

    public final static String RESOURCE_MEDIA_NAME = "media";
}