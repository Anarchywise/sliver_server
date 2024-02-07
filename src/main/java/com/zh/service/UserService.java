package com.zh.service;

import com.zh.domain.ResponseResult;
import com.zh.domain.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    ResponseResult<Object> getUserDetails(String token);

    ResponseResult<Object> changePassword(HttpServletRequest request);

    ResponseResult<Object> changeNickname(HttpServletRequest request);

    ResponseResult<Object> feedback(HttpServletRequest request);
}
