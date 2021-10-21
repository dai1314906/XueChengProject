package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常捕获类
 */
@ControllerAdvice//控制器增强
public class ExceptionCatch {


    //定义日志
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);
    //定义map配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //定义构建map的builder对象去构建ImmutableMap对象
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    //捕获此类异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        customException.printStackTrace();
        //记录日志
        LOGGER.error("catch exception:{}",customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }

    //捕获此类异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        exception.printStackTrace();
        //记录日志
        LOGGER.error("catch exception:{}",exception.getMessage());

        if (EXCEPTIONS==null){
             EXCEPTIONS=builder.build();//构建EXCEPTIONS
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，没找到则返回9999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if (resultCode!=null){
            return new ResponseResult(resultCode);
        }else {
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }

    }
    static {
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }
}
