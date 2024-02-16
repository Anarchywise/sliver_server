package com.zh.service;

import com.zh.entity.ResponseResult;
import com.zh.entity.User;

public interface LoginService {
    ResponseResult<Object> login (User user);

}
