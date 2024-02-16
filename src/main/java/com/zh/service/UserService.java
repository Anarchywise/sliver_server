package com.zh.service;

import org.springframework.web.multipart.MultipartFile;

import com.zh.entity.ResponseResult;

public interface UserService {

    ResponseResult<Object> changePassword(String username, int userId, String password, String changedPassword);

    ResponseResult<Object> getUserDetails(Integer userId, Integer userIdSelf);


    ResponseResult<Object> changeNickname(String username, int user_id, String changedNickname);

    ResponseResult<Object> feedback(int userId, String feedback);


    ResponseResult<Object> uploadHeadPortrait(int userId, MultipartFile file);
}
