package com.zh.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.zh.entity.ResponseResult;
import com.zh.service.Impl.PostServiceImpl;
import com.zh.utils.JsonUtils;
import com.zh.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class PostController {

    @Autowired
    PostServiceImpl postService;

    @PostMapping("/post/uploadImages")
    ResponseResult<Object> uploadPostImages(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseResult<>(ResponseResult.Error, "上传图片为空", null);
        }
        //获取请求信息
        String token = request.getHeader("token");
        int userId = JwtUtils.getUserId(token);
        return postService.uploadPostContentImages(userId, file);
    }

    @PostMapping("/post/uploadPost")
    ResponseResult<Object> uploadPost(HttpServletRequest request) throws JsonParseException{
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        String title = jsonNode.get("title").asText();
        String contentText = jsonNode.get("contentText").asText();
        String type = jsonNode.get("type").asText();
        //获取请求信息
        String token = request.getHeader("token");
        int userId = JwtUtils.getUserId(token);
        return postService.acceptPost(userId, title, contentText,type); 
    }

    @PostMapping("/post/getUserPost")
    ResponseResult<Object> getUserPost(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        Integer userId = null;
        try{
            userId = jsonNode.get("userId").asInt();
        }catch(NullPointerException e){
            userId = JwtUtils.getUserId(token);
        }
        
        return postService.getPostByUserId(userId);
    }

    @PostMapping("/post/deleteUploadedImage")
    ResponseResult<Object> deleteUploadedImage(HttpServletRequest request, @RequestHeader("token") String token) {
        int userId = JwtUtils.getUserId(token);
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        int imageOrder = jsonNode.get("imageOrder").asInt();
        return postService.deleteUploadedImage(imageOrder, userId);
    }

    @PostMapping("/post/uploadRemark")
    ResponseResult<Object> uploadRemark(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        int postId = Integer.parseInt(jsonNode.get("postId").asText());
        String contentText = jsonNode.get("contentText").asText();
        int userId = JwtUtils.getUserId(token);
        return postService.remark(postId, contentText, userId);
    }

    @PostMapping("/post/getRemark")
    ResponseResult<Object> getRemark(HttpServletRequest request) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        int postId = Integer.parseInt(jsonNode.get("postId").asText());
        return postService.getPostRemark(postId);
    }

    @PostMapping("/post/like")
    ResponseResult<Object> postLike(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        int postId = Integer.parseInt(jsonNode.get("postId").asText());
        int userId = JwtUtils.getUserId(token);
        return postService.postLike(userId, postId);
    }

    @PostMapping("/post/noLike")
    ResponseResult<Object> postNoLike(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        int postId = Integer.parseInt(jsonNode.get("postId").asText());
        int userId = JwtUtils.getUserId(token);
        return postService.postNoLike(userId, postId);
    }

    @GetMapping("/post/uploadedImages")
    ResponseResult<Object> getUploadedImages(@RequestHeader("token") String token) {
        int userId = JwtUtils.getUserId(token);
        return postService.getUploadedContentImages(userId);
    }

    @PostMapping("/post/userCollect")
    ResponseResult<Object> userCollect(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        int postId = jsonNode.get("postId").asInt();
        int userId = JwtUtils.getUserId(token);
        return postService.postCollect(postId, userId);
    }

    @GetMapping("/post/getUserCollect")
    ResponseResult<Object> getUserCollect(@RequestHeader("token") String token) {
        int userId = JwtUtils.getUserId(token);
        return postService.getUserPostCollects(userId);
    }

    @PostMapping("/post/getPostByPage")
    public ResponseResult<Object> getPostByPage(HttpServletRequest request) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        int current = jsonNode.get("current").asInt();
        int size = jsonNode.get("size").asInt();
        return postService.getPostByPage(current, size);
    }
    
}
