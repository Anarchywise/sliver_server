package com.zh.service;

import com.zh.entity.AuthCode;
import com.zh.entity.ResponseResult;

public interface AuthCodeService {
    void saveAuthCode(AuthCode authCode);

    ResponseResult<Object> verifyAuthCode(AuthCode authCode);

    void saveAuthCode(String phoneNumber,String authCode);
}
