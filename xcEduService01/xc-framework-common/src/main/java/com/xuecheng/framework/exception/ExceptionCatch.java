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
 * 统一的异常捕获类
 */
@ControllerAdvice   //控制器增强
public class ExceptionCatch {
    private static  final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //定义一个map，配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //定义map的builder对象，去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();//builder对象用来构建ImmutableMap



    //只要遇到CustomException这类异常，我们就捕获这类异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        //日志记录
        LOGGER.error("catch exception:{}",customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        //日志记录
        LOGGER.error("catch exception:{}",exception.getMessage());
        if (EXCEPTIONS == null){
            EXCEPTIONS = builder.build();//EXCEPTIONS构建成功，并且不能更改
        }
        //从EXCEPTIONS找异常所对应的错误代码，如果找到，则把错误代码响应给用户，如果找不到，则给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if (resultCode != null){
            return new ResponseResult(resultCode);
        }else {
            //返回99999异常
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }


    }


    static {
        //定义了异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }













}
