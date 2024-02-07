package com.zh.controller;

import com.zh.dao.UserDao;
import com.zh.domain.ResponseResult;
import com.zh.domain.User;
import com.zh.service.Impl.UserServiceImpl;
import com.zh.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/user/details")
    public ResponseResult<Object> getUserDetail(@RequestHeader("token") String token){
        return userService.getUserDetails(token);
    }

    @PostMapping("/user/changePassword")
    public ResponseResult<Object> changePassword(HttpServletRequest request){
        return userService.changePassword(request);
    }

    @PostMapping("/user/changeNickname")
    public ResponseResult<Object> changeNickname(HttpServletRequest request){
        return userService.changeNickname(request);
    }

    @PostMapping("/user/feedback")
    public ResponseResult<Object> feedback(HttpServletRequest request){
        return userService.feedback(request);
    }
}
