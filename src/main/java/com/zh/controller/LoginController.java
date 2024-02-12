package com.zh.controller;

import com.zh.domain.ResponseResult;
import com.zh.domain.User;
import com.zh.service.Impl.LoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    @Autowired
    LoginServiceImpl loginService;

    @PostMapping("/user/login")
    public ResponseResult<Object> login(@RequestBody User user){
        System.out.println("/user/login 被访问");
        return loginService.login(user);
    }

}
