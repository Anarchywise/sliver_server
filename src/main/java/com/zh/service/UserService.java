package com.zh.service;

import com.zh.domain.ResponseResult;
import com.zh.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    ResponseResult<Object> changePassword(HttpServletRequest request);

    ResponseResult<Object> getUserDetails(HttpServletRequest request);

    ResponseResult<Object> changeNickname(HttpServletRequest request);

    ResponseResult<Object> feedback(HttpServletRequest request);

    ResponseResult<Object> uploadHeadPortrait(HttpServletRequest request, @RequestParam("file") MultipartFile file);
}
