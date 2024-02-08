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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping("/user/details")
    public ResponseResult<Object> getUserDetail(HttpServletRequest request){
        return userService.getUserDetails(request);
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

    @PostMapping("/user/uploadHeadPortrait")
    public ResponseResult<Object> uploadHeadPortrait(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        return userService.uploadHeadPortrait(request,file);
    }

}
