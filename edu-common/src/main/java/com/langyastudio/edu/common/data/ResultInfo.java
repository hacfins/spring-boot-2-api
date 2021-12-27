package com.langyastudio.edu.common.data;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * 返回值类
 */
@Data
@Log4j2
public class ResultInfo
{
    private Integer code;
    private String  msg;
    private Long    time;
    private Object  result;

    private ResultInfo(Integer code, String msg, Object result)
    {
        this.setCode(code);
        this.setResult(result);
        this.setMsg(msg);
        this.setTime(System.currentTimeMillis());
    }

    /**
     * 返回正确提示
     *
     * @return Rv
     */
    public static ResultInfo data()
    {
        return data(EC.SUCCESS.getCode(), EC.SUCCESS.getMsg(), null);
    }

    /**
     * 返回正确后的数据
     *
     * @param data 数据
     *
     * @return Rv
     */
    public static ResultInfo data(Object data)
    {
        return data(EC.SUCCESS.getCode(), EC.SUCCESS.getMsg(), data);
    }

    /**
     * 返回状态码和信息
     *
     * @param code 状态码
     * @param msg  信息
     *
     * @return Rv
     */
    public static ResultInfo data(Integer code, String msg)
    {
        return data(code, msg, null);
    }

    /**
     * 返回状态码，信息，数据
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     *
     * @return Rv
     */
    public static ResultInfo data(Integer code, String msg, Object data)
    {
        return new ResultInfo(code, msg, data);
    }

    /**
     * 返回错误提示
     *
     * @param ec EC
     *
     * @return
     */
    public static ResultInfo data(IErrorCode ec)
    {
        return data(ec.getCode(), ec.getMsg(), null);
    }

    /**
     * 返回错误提示与数据
     *
     * @param ec EC
     *
     * @return
     */
    public static ResultInfo data(IErrorCode ec, String msg)
    {
        return data(ec.getCode(), msg);
    }

    /**
     * 返回错误提示与数据
     *
     * @param ec EC
     *
     * @return
     */
    public static ResultInfo data(IErrorCode ec, Object data)
    {
        return data(ec.getCode(), ec.getMsg(), data);
    }
}
