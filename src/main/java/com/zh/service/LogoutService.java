package com.zh.service;


import com.zh.entity.ResponseResult;
import com.zh.entity.User;

public interface LogoutService {
    ResponseResult<Object> logout(User user);

    ResponseResult<Object> attemptLogout(User user);

    ResponseResult<Object> logoutWithCode(User user);
}
