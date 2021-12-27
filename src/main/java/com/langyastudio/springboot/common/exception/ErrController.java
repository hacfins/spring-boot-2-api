package com.langyastudio.springboot.common.exception;

import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpStatus;
import com.langyastudio.springboot.common.data.EC;
import com.langyastudio.springboot.common.data.ResultInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义异常类
 */
@RestController
public class ErrController extends AbstractErrorController
{
    public static final String ERROR_PATH = "/error";
    @Value("${spring.profiles.active}")
    private             String env;

    public ErrController(ErrorAttributes errorAttributes)
    {
        super(errorAttributes);
    }

    @RequestMapping(ERROR_PATH)
    public ResultInfo handleError(HttpServletRequest request)
    {
        WebRequest webRequest = new ServletWebRequest(request);

        String msg;
        Object data = null;

        int       code = EC.ERROR.getCode();
        Throwable e    = (Throwable) webRequest.getAttribute("javax.servlet.error.exception", 0);
        if (e == null)
        {
            Map<String, Object> errorAttributes = getErrorAttributes(request,
                                                                     ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE));

            Object status = errorAttributes.get("status");
            if(Objects.nonNull(status) && (Integer)status == HttpStatus.HTTP_NOT_FOUND)
            {
                msg = "请求地址不存在";
            }
            else
            {
                msg = "【" + errorAttributes.get("error") + "】" + errorAttributes.get("message");
            }

            if ("dev".equals(env) || "test".equals(env))
            {
                String trace = (String) errorAttributes.get("trace");
                if (trace == null)
                {
                    trace = "";
                }
                data = trace
                        .replace("\r\n", "\n")
                        .replace("\r", "\n")
                        .replace("\t", "")
                        .split("\n");
            }
        }
        else
        {
            if ("dev".equals(env) || "test".equals(env))
            {
                msg = e.getMessage();
                msg = msg != null ? msg : e.toString();

                Integer codeE = (Convert.convert(MyException.class, e)).getCode();
                if (codeE != null)
                {
                    code = codeE;
                }
                if (!(code >= 11000 && code < 11999))
                {
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

            }
            else
            {
                msg = e.getMessage();
                data = null;
            }
        }

        return ResultInfo.data(code, msg, data);
    }
}
