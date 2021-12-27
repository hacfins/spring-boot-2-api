package com.langyastudio.edu.common.exception;

import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.ResultInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.*;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 异常处理
 */
@ControllerAdvice
@Log4j2
public class ExceptionHandle
{
    /**
     * 系统异常 10001
     * 业务默认异常 1
     * 业务异常 11001--11999
     * 参数异常 3
     * 登录异常 2
     */

    //上传文件的最大体积
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    @Value("${spring.profiles.active}")
    private String env;

    //------------------------------------------------------- 参数异常 --------------------------------------------------
    // 处理 form data方式调用接口校验失败抛出的异常
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public ResultInfo bindExceptionHandler(BindException e)
    {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResultInfo.data(EC.ERROR_PARAM_EXCEPTION, collect);
    }

    // 处理 json 请求体调用接口校验失败抛出的异常
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultInfo methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e)
    {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = new ArrayList<>();
        for(FieldError fieldError :fieldErrors)
        {
            collect.add(fieldError.getField() + fieldError.getDefaultMessage());
        }
        return ResultInfo.data(EC.ERROR_PARAM_EXCEPTION, collect);
    }

    // 处理单个参数校验失败抛出的异常
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultInfo constraintViolationExceptionHandler(ConstraintViolationException e)
    {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return ResultInfo.data(EC.ERROR_PARAM_EXCEPTION, collect);
    }

    /**
     * 参数绑定异常
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResultInfo exceptionHandlerParamJson(HttpMessageConversionException e)
    {
        String msg = "【解析失败，请检查参数是否合法】" + e.getMessage();
        return ResultInfo.data(EC.ERROR_PARAM_EXCEPTION, msg);
    }

    /**
     * 参数绑定异常
     */
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResultInfo exceptionHandlerParamJson(HttpMediaTypeException e)
    {
        String msg = "【解析失败，该API不支持此ContentType】" + e.getMessage();

        return ResultInfo.data(EC.ERROR_PARAM_EXCEPTION, msg);
    }

    /**
     * 参数绑定异常
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)
    public ResultInfo exceptionHandlerParamJson(MethodArgumentConversionNotSupportedException e)
    {
        String msg = "【解析失败，请检查参数是否合法】" + e.getMessage();
        return ResultInfo.data(EC.ERROR_PARAM_EXCEPTION, msg);
    }

    //------------------------------------------------------- 业务异常 --------------------------------------------------

    /**
     * API不存在异常
     */
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseBody
    public ResultInfo noHandlerFoundException(HttpServletRequest req, Exception e)
    {
        return ResultInfo.data(EC.ERROR_REQUEST_METHOD_NOT_EXIST, "API不存在");
    }

    /**
     * 文件过大异常
     */
    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    @ResponseBody
    public ResultInfo maxUploadSizeExceededException(MaxUploadSizeExceededException e)
    {
        return ResultInfo.data(EC.ERROR_FILE_SIZE_ERROR, "上传的文件不能超过" + maxFileSize);
    }

    /**
     * 业务异常
     */
    @ResponseBody
    @ExceptionHandler(value = MyException.class)
    public ResultInfo exceptionHandlerMyException(MyException e)
    {
        return this.handlerException(e);
    }

    /**
     * 所有异常
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultInfo handlerException(Exception e)
    {
        String msg  = "";
        Object data = null;
        if ("dev".equals(env))
        {
            msg = e.getMessage();
            msg = msg != null ? msg : e.toString();

            ArrayList<String> ste = new ArrayList<>();
            for (StackTraceElement stackTraceElement : e.getStackTrace())
            {
                String str = stackTraceElement.toString();
                if (!str.contains("langyastudio"))
                {
                    continue;
                }

                ste.add(str);
            }
            data = ste;
        }
        else
        {
            msg  = e.getMessage();
            data = null;
        }

        String value = StrUtil.isBlank(msg) ? "系统异常" : msg;

        ResultInfo rtn;
        if(e instanceof MyException)
        {
            rtn = ResultInfo.data(((MyException) e).getCode(), value, data);
        }
        else
        {
            rtn = ResultInfo.data(EC.ERROR_SYSTEM_EXCEPTION.getCode(), value, data);
        }

        logThrowable(e);

        return rtn;
    }

    //------------------------------------------------------- 数据异常 --------------------------------------------------

    /**
     * 重复数据异常
     */
    @ExceptionHandler(value = {DuplicateKeyException.class})
    @ResponseBody
    public ResultInfo sqlIntegrityConstraintViolationException(DuplicateKeyException e)
    {
        return this.getSqlError(e, "有不允许重复的数据");
    }

    /**
     * 数据时违反了完整性
     */
    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseBody
    public ResultInfo dataIntegrityViolationException(DataIntegrityViolationException e)
    {
        return this.getSqlError(e, "数据时违反了完整性");
    }

    /**
     * BadSqlGrammarException
     */
    @ExceptionHandler(value = {BadSqlGrammarException.class})
    @ResponseBody
    public ResultInfo badSqlGrammarException(BadSqlGrammarException e)
    {
        return this.getSqlError(e, "数据库操作失败");
    }

    /**
     * SQLSyntaxErrorException
     */
    @ExceptionHandler(value = {SQLSyntaxErrorException.class})
    @ResponseBody
    public ResultInfo sqlSyntaxErrorException(SQLSyntaxErrorException e)
    {
        return this.getSqlError(e, "数据库操作失败");
    }

    /**
     * 数据访问资源彻底失败
     */
    @ExceptionHandler(value = {DataAccessResourceFailureException.class})
    @ResponseBody
    public ResultInfo dataAccessResourceFailureException(DataAccessResourceFailureException e)
    {
        return this.getSqlError(e, "数据库操作失败（数据访问资源彻底失败）");
    }

    /**
     * 当前的操作因为死锁而失败
     */
    @ExceptionHandler(value = {DeadlockLoserDataAccessException.class})
    @ResponseBody
    public ResultInfo deadlockLoserDataAccessException(DeadlockLoserDataAccessException e)
    {
        return this.getSqlError(e, "当前的操作因为死锁而失败");
    }

    /**
     * Java类型和数据类型不匹配
     */
    @ExceptionHandler(value = {TypeMismatchDataAccessException.class})
    @ResponseBody
    public ResultInfo typemismatchDataAccessException(TypeMismatchDataAccessException e)
    {
        return this.getSqlError(e, "Java类型和数据类型不匹配");
    }

    /**
     * 未知异数据库常
     */
    @ExceptionHandler(value = {UncategorizedDataAccessException.class})
    @ResponseBody
    public ResultInfo uncategorizedDataAccessException(UncategorizedDataAccessException e)
    {
        return this.getSqlError(e, "数据库操作失败（UncategorizedDataAccessException）");
    }

    private ResultInfo getSqlError(Exception e, String msg)
    {
        ArrayList<String> data;
        String[] err = Objects.requireNonNull(e.getMessage())
                .replace("###", "")
                .replace("\r", "")
                .replace("  ", "")
                .replace("\t", "")
                .split("\n");

        ArrayList<String> strings = new ArrayList<>(Arrays.asList(err));
        strings.removeIf(s -> !s.contains("SQL:") && !s.contains(" Cause: "));

        data = strings;
        if (data.size() < 1)
        {
            data.add(e.getMessage());
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace)
            {
                data.add(stackTraceElement.toString());
            }
        }
        log.error(msg + "\r\n\t" + data);

        if ("prod".equals(env))
        {
            data = null;
        }
        return ResultInfo.data(EC.ERROR_DATA_ERROR.getCode(), msg, data);
    }

    /**
     * 打印异常
     */
    public static void logThrowable(Exception e)
    {
        StackTraceElement[] stackTrace = e.getStackTrace();

        log.error(e + "\r\n\t" + e.getMessage());

        StringBuilder stackTraceStr = new StringBuilder();
        for (StackTraceElement stackTraceElement : stackTrace)
        {
            String str = stackTraceElement.toString();
            if (!str.contains("langyastudio"))
            {
                continue;
            }

            stackTraceStr.append("\r\n\t").append(str);

        }
        stackTraceStr.append("\r\n");
        log.warn(stackTraceStr.toString());
    }
}