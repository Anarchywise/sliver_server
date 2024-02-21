package com.zh.handler;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.fasterxml.jackson.core.JsonParseException;
import com.zh.entity.ResponseResult;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseResult<Object> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return new ResponseResult<>(ResponseResult.Error,"文件太大了",null);
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseResult<Object> handleSizeLimitException(SizeLimitExceededException exc) {
        return new ResponseResult<>(ResponseResult.Error,"文件太大了",null);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseResult<Object> handleNullPointerException(NullPointerException exc) {
        return new ResponseResult<>(ResponseResult.Error,"空指针异常,可能是发送的json字段出错",null);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseResult<Object> handleJsonParseException(HttpServletRequest request,JsonParseException exc) {
        System.out.println(exc.getClass().getName()+"被捕获了");
        return new ResponseResult<>(ResponseResult.JsonError);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseResult<Object> handleExpiredJwtException(ExpiredJwtException exc) {
        return new ResponseResult<>(ResponseResult.Error,"token已过期",null);
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseResult<Object> handleSQLSyntaxErrorException(SQLSyntaxErrorException exc) {
        return new ResponseResult<>(ResponseResult.Error,"服务器出错,sql语句问题",null);
    }
    
    @ExceptionHandler(SQLException.class)
    public ResponseResult<Object> handSQLException(SQLException exc) {
        return new ResponseResult<>(ResponseResult.Error,"服务器出错",null);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseResult<Object> handleDataAccessException(DataAccessException exc) {
        exc.printStackTrace();
        return new ResponseResult<>(ResponseResult.Error, "数据库访问异常", null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseResult<Object> handHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exc){
        return new ResponseResult<>(ResponseResult.Error,"访问方式错误,josn访问",null);
    }


    @ExceptionHandler(Exception.class)
    public ResponseResult<Object> handleUnknownException(Exception exc) {
        exc.printStackTrace();
        return new ResponseResult<>(ResponseResult.Error,"未知错误",null);
    }
    
}
