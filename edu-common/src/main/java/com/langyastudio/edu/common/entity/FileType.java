package com.langyastudio.edu.common.entity;

/**
 * 文件类型
 *
 * @author jiangjiaxiong
 * @date 2021年05月13日 10:40
 */
public interface FileType
{
    //-文件类型
    //文件夹
    public final static byte DIRECTORY = 0;
    //视频
    public final static byte VIDEO     = 1;
    //图片
    public final static byte PICTURE   = 2;
    //文本
    public final static byte TEXT      = 3;
    //音频
    public final static byte AUDIO     = 4;
    //其他
    public final static byte OTHER     = 5;
    //压缩文件
    public final static byte RAR       = 100;
}
