package com.langyastudio.edu.portal.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * 图像裁剪参数
 */
@Data
public class ImgCropParam
{
    /**
     * 头像路径
     */
    @NotBlank
    private String imgPath;

    /**
     * x 坐标
     */
    @PositiveOrZero
    private Integer x;

    /**
     * y 坐标
     */
    @PositiveOrZero
    private Integer y;

    /**
     * 裁剪宽度
     */
    @Positive
    private Integer width;

    /**
     * 裁剪高度
     */
    @Positive
    private Integer height;
}
