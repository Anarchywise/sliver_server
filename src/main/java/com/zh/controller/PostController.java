package com.zh.controller;

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
    ResponseResult<Object> uploadPost(HttpServletRequest request) {
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        String title = null;
        String contentText = null;
        String type = null;
        try {
            title = jsonNode.get("title").asText();
            contentText = jsonNode.get("contentText").asText();
            type = jsonNode.get("type").asText();
            //获取请求信息
            String token = request.getHeader("token");
            int userId = JwtUtils.getUserId(token);
            return postService.acceptPost(userId, title, contentText,type);
        } catch (Exception e) {
            return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        }        
    }

    @PostMapping("/post/getUserPost")
    ResponseResult<Object> getUserPost(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        try {
            int userId = jsonNode.get("userId").asInt();
            return postService.getPostByUserId(userId);
        } catch (Exception ignored) {}
        int userId = JwtUtils.getUserId(token);
        return postService.getPostByUserId(userId);
    }

    @PostMapping("/post/deleteUploadedImage")
    ResponseResult<Object> deleteUploadedImage(HttpServletRequest request, @RequestHeader("token") String token) {
        int userId = JwtUtils.getUserId(token);
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        int imageOrder;
        try {
            imageOrder = jsonNode.get("imageOrder").asInt();
        }catch (Exception e){
            return new ResponseResult<>(ResponseResult.JsonError);
        }
        return postService.deleteUploadedImage(imageOrder, userId);
    }

    @PostMapping("/post/uploadRemark")
    ResponseResult<Object> uploadRemark(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        int postId;
        String contentText;
        try {
            postId = Integer.parseInt(jsonNode.get("postId").asText());
            contentText = jsonNode.get("contentText").asText();
        }catch (Exception e){
            return new ResponseResult<>(ResponseResult.JsonError);
        }
        int userId = JwtUtils.getUserId(token);
        return postService.remark(postId, contentText, userId);
    }

    @PostMapping("/post/getRemark")
    ResponseResult<Object> getRemark(HttpServletRequest request) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        int postId;
        try {
            postId = Integer.parseInt(jsonNode.get("postId").asText());
        }catch (Exception e){
            return new ResponseResult<>(ResponseResult.JsonError);
        }
        return postService.getPostRemark(postId);
    }

    @PostMapping("/post/like")
    ResponseResult<Object> postLike(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        int postId;
        try {
            postId = Integer.parseInt(jsonNode.get("postId").asText());
        }catch (Exception e){
            return new ResponseResult<>(ResponseResult.JsonError);
        }
        int userId = JwtUtils.getUserId(token);
        return postService.postLike(userId, postId);
    }

    @PostMapping("/post/noLike")
    ResponseResult<Object> postNoLike(HttpServletRequest request, @RequestHeader("token") String token) {
        //获取json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        int postId;
        try {
            postId = Integer.parseInt(jsonNode.get("postId").asText());
        }catch (Exception e){
            return new ResponseResult<>(ResponseResult.JsonError);
        }
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
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        int postId;
        try {
            postId = jsonNode.get("postId").asInt();
            int userId = JwtUtils.getUserId(token);
        return postService.postCollect(postId, userId);
        }catch (Exception e){
            return new ResponseResult<>(ResponseResult.JsonError);
        }
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
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        Integer current;
        Integer size; 
        try {
            current = jsonNode.get("current").asInt();
            size = jsonNode.get("size").asInt();
            return postService.getPostByPage(current, size);
        }catch (Exception e){
            return new ResponseResult<>(ResponseResult.JsonError);
        }
    }
    
}
