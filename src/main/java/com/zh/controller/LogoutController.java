package com.zh.controller;

import com.zh.entity.ResponseResult;
import com.zh.entity.User;
import com.zh.service.Impl.LogoutServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {

    @Autowired
    LogoutServiceImpl logoutService;

    @PostMapping("/user/attemptLogout")
    public ResponseResult<Object> attemptRegister(@RequestBody User user){
        return logoutService.attemptLogout(user);
    }
    @PostMapping("/user/codeLogout")
    public ResponseResult<Object> registerWithCode(@RequestBody User user){
        return logoutService.logoutWithCode(user);
    }
}
