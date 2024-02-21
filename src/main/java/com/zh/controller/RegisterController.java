package com.zh.controller;

import com.zh.entity.ResponseResult;
import com.zh.entity.User;
import com.zh.service.Impl.RegisterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    @Autowired
    RegisterServiceImpl registerService;

    @PostMapping("/user/attemptRegister")
    public ResponseResult<Object> attemptRegister(@RequestBody User user){
        return registerService.attemptRegister(user);
    }
    @PostMapping("/user/CodeRegister")
    public ResponseResult<Object> registerWithCode(@RequestBody User user){
        return registerService.registerWithCode(user);
    }
}
