package com.zh.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.zh.domain.ResponseResult;
import com.zh.service.Impl.PostServiceImpl;
import com.zh.utils.JsonUtils;
import com.zh.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PostController {

    @Autowired
    PostServiceImpl postService;

    @PostMapping("/post/uploadImages")
    ResponseResult<Object> uploadPostImages(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        return postService.uploadPostContentImages(request,file);
    }

    @PostMapping("/post/uploadPost")
    ResponseResult<Object> uploadPost(HttpServletRequest request){
        return postService.acceptPost(request);
    }

    @GetMapping("/post/getUserPost")
    ResponseResult<Object> getUserPost(@RequestHeader("token") String token){
        //获取请求信息
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        int userId = Integer.parseInt(claims.getId());
        return postService.getPostByUserId(userId);
    }

    @PostMapping("/post/deleteUploadedImage")
    ResponseResult<Object> deleteUploadedImage(HttpServletRequest request,@RequestHeader("token") String token){
        //解析token
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        int userId = Integer.parseInt(claims.getId());
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        int imageOrder = Integer.parseInt(jsonNode.get("imageOrder").asText());
        return postService.deleteUploadedImage(imageOrder,userId);
    }
}
