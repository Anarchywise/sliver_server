package com.zh.service;

import com.zh.entity.ResponseResult;
import com.zh.entity.User;

public interface RegisterService {
    ResponseResult<Object> register(User user);

    ResponseResult<Object> attemptRegister(User user);

    ResponseResult<Object> registerWithCode(User user);
}
