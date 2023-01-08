package com.hg.common.exception;

import com.hg.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author hougen
 * @program syt_parent
 * @description 全局异常处理
 * @create 2023-01-09 02:34
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( Exception.class )
    public Result error( Exception e ) {
        e.printStackTrace();
        return Result.fail( e.getMessage() );
    }

    @ExceptionHandler( SytException.class )
    public Result error( SytException e ) {
        e.printStackTrace();
        return Result.fail( e.getMessage() );
    }

}


