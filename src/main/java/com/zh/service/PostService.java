package com.zh.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.zh.entity.ResponseResult;

public interface PostService {

    ResponseResult<Object> uploadPostContentImages(int userId, @RequestParam("file") MultipartFile file);

    public ResponseResult<Object> acceptPost(int userId, String title, String contentText,String type);

    ResponseResult<Object> getPostByUserId(int userId);

    ResponseResult<Object> deleteUploadedImage(int imageOrder, int userId);


    ResponseResult<Object> getUploadedContentImages(int userId);

    ResponseResult<Object> postLike(int userId, int postId);


    ResponseResult<Object> postNoLike(int userId, int postId);

    ResponseResult<Object> remark(int postId, String contentText, int userId);

    ResponseResult<Object> getPostRemark(int postId);

    ResponseResult<Object> postCollect(int postId, int userId);

    ResponseResult<Object> getUserPostCollects(int userId);

    ResponseResult<Object> getPostByPage(int current,int size);
}
