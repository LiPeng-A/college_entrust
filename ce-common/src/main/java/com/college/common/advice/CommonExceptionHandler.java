package com.college.common.advice;

import com.college.common.Exception.EntrustException;
import com.college.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 通用异常处理类
 */

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(EntrustException.class)
    public ResponseEntity<ExceptionResult> handlerException(EntrustException e){

        return ResponseEntity.status
                (e.getExceptionEnum().getStatus())
                .body(new ExceptionResult(e.getExceptionEnum()));
    }
}
