package com.zh.service;

import com.zh.domain.ResponseResult;
import com.zh.domain.User;

public interface LoginService {
    ResponseResult<Object> login (User user);

}
