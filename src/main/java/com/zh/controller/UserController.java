package com.zh.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.zh.entity.ResponseResult;
import com.zh.service.Impl.UserServiceImpl;
import com.zh.utils.JsonUtils;
import com.zh.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping("/user/details")
    public ResponseResult<Object> getUserDetail(HttpServletRequest request){
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        String token = request.getHeader("token");
        int userIdSelf = JwtUtils.getUserId(token);
        Integer userId = null;
        try {
            userId = jsonNode.get("userId").asInt();
        }catch (NullPointerException e){
            userId = userIdSelf;
        }
        return userService.getUserDetails(userId,userIdSelf);
    }

    @PostMapping("/user/changePassword")
    public ResponseResult<Object> changePassword(HttpServletRequest request){
        String token = request.getHeader("token");
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        String username = jsonNode.get("username").asText();
        String password = jsonNode.get("password").asText();
        String changedPassword = jsonNode.get("changedPassword").asText();
        int userId = JwtUtils.getUserId(token);
        return userService.changePassword(username,userId,password,changedPassword);
    }

    @PostMapping("/user/changeNickname")
    public ResponseResult<Object> changeNickname(HttpServletRequest request){
        String token = request.getHeader("token");
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        String username = jsonNode.get("username").asText();
        String changedNickname = jsonNode.get("changedNickname").asText();
        int userId = JwtUtils.getUserId(token);
        return userService.changeNickname(username,userId,changedNickname);
    }

    @PostMapping("/user/feedback")
    public ResponseResult<Object> feedback(HttpServletRequest request){
        //获取请求信息
        String token = request.getHeader("token");
        int userId = JwtUtils.getUserId(token);
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        String feedback = jsonNode.get("feedback").asText();
        return userService.feedback(userId,feedback);
    }

     @PostMapping("/user/uploadHeadPortrait")
    public ResponseResult<Object> uploadHeadPortrait(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return new ResponseResult<>(ResponseResult.Error, "上传图片为空", null);
        }
        //获取请求信息
        String token = request.getHeader("token");
        int userId = JwtUtils.getUserId(token);
        return userService.uploadHeadPortrait(userId,file);
    }

}
