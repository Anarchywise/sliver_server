package com.zh.service;

import com.zh.domain.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    ResponseResult<Object> uploadPostContentImages(HttpServletRequest request, MultipartFile file);
    ResponseResult<Object> acceptPost(HttpServletRequest request);

    ResponseResult<Object> getPostByUserId(int userId);

    ResponseResult<Object> deleteUploadedImage(int imageOrder, int userId);

    ResponseResult<Object> getUploadedContentImages(String token);

    ResponseResult<Object> postLike(HttpServletRequest request, String token);

    ResponseResult<Object> postNoLike(HttpServletRequest request, String token);

    ResponseResult<Object> remark(HttpServletRequest request, String token);
}
