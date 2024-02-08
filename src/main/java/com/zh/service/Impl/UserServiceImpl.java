package com.zh.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.zh.dao.UserDao;
import com.zh.dao.UserHeadPortraitDao;
import com.zh.domain.ResponseResult;
import com.zh.domain.User;
import com.zh.domain.UserHeadPortrait;
import com.zh.service.UserService;
import com.zh.utils.JsonUtils;
import com.zh.utils.JwtUtils;
import com.zh.utils.LegalUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import java.nio.file.Paths;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    UserHeadPortraitDao userHeadPortraitDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult<Object> changePassword(HttpServletRequest request) {
        String token = request.getHeader("token");
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        String username = jsonNode.get("username").asText();
        String password = jsonNode.get("password").asText();
        String changedPassword = jsonNode.get("changedPassword").asText();
        User user = userDao.getByUsername(username);
        //判断用户是否存在
        if (Objects.isNull(user)) return new ResponseResult<>(ResponseResult.IllegalAction, "用户不存在", null);
        Claims claims = JwtUtils.parseJwtToken(token);
        String user_id = claims.getId();
        //用户信息是否和token一致
        if (Integer.parseInt(user_id) != user.getId())
            return new ResponseResult<>(ResponseResult.IllegalAction, "用户名和token数据不匹配", null);
        //判断密码的正确性
        if (!passwordEncoder.matches(password, user.getPassword()))
            return new ResponseResult<>(ResponseResult.WrongUserOrPassword, "密码错误", null);
        //修改密码
        user.setPassword(passwordEncoder.encode(changedPassword));
        userDao.updatePassword(user);
        return new ResponseResult<>(ResponseResult.AccessOk, "修改密码成功", user);
    }

    @Override
    public ResponseResult<Object> getUserDetails(HttpServletRequest request) {
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        String userId = jsonNode.get("userId").asText();
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseJwtToken(token);
        String userId2 = claims.getId();
        if(userId==null){//没有查询其他用户,返回本人的信息
            User user = userDao.getById(Integer.parseInt(userId2));
            if(!userHeadPortraitDao.selectByUser_idUserHeadPortraitList(user.getId()).isEmpty()){
                user.setHeadPortraitUrl(userHeadPortraitDao
                        .selectByUser_idUserHeadPortraitList(user.getId()).get(0).getUrl());
            }
            return new ResponseResult<>(ResponseResult.AccessOk, "获得用户信息", user);
        }else{
            User user = userDao.getById(Integer.parseInt(userId));
            user.setPhone(null);
            user.setUsername(null);
            if(!userHeadPortraitDao.selectByUser_idUserHeadPortraitList(user.getId()).isEmpty()){
                user.setHeadPortraitUrl(userHeadPortraitDao
                        .selectByUser_idUserHeadPortraitList(user.getId()).get(0).getUrl());
            }

            //BCryptPasswordEncoder不能被解密,看吧
            return new ResponseResult<>(ResponseResult.AccessOk, "获得用户信息", user);
        }
    }

    @Override
    public ResponseResult<Object> changeNickname(HttpServletRequest request) {
        String token = request.getHeader("token");
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        String username = jsonNode.get("username").asText();
        String changedNickname = jsonNode.get("changedNickname").asText();
        User user = userDao.getByUsername(username);
        //判断用户是否存在
        if (Objects.isNull(user)) return new ResponseResult<>(ResponseResult.IllegalAction, "用户不存在", null);
        Claims claims = JwtUtils.parseJwtToken(token);
        String user_id = claims.getId();
        //用户信息是否和token一致
        if (Integer.parseInt(user_id) != user.getId())
            return new ResponseResult<>(ResponseResult.IllegalAction, "用户名和token数据不匹配", null);
        //修改昵称
        ResponseResult<Object> result = null;
        result = LegalUtils.verifyNickname(changedNickname);
        if (result != null) return result;
        user.setNickname(changedNickname);
        try {
            userDao.updatePassword(user);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult<>(ResponseResult.Error, "服务器出错", user);
        }

        return new ResponseResult<>(ResponseResult.AccessOk, "修改昵称成功", user);
    }

    @Override
    public ResponseResult<Object> feedback(HttpServletRequest request) {
        //获取请求信息
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseJwtToken(token);
        String user_id = claims.getId();
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        String feedback = null;
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        feedback = jsonNode.get("feedback").asText();
        //构建文件路径
        String userDir = System.getProperty("user.dir"); // 获取当前工作目录
        String relativePath = "data/" + user_id + "feedback.txt";// 构建相对路径，不包含 JAR 文件的信息
        String absolutePath = Paths.get(userDir, relativePath).toString(); // 将当前工作目录与相对路径结合，创建绝对路径
        System.out.println("File Absolute Path: " + absolutePath);
        try {
            // 使用绝对路径
            File file = new File(absolutePath);

            // 检查文件是否存在，如果不存在则创建
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("File created successfully.");
                } else {
                    System.out.println("Failed to create file.");
                }
            }

            // 使用 BufferedWriter 写入数据
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {//第二个参数为true表示追加写入
                // 将数据写入文件
                writer.write(new Date() + "\n" + feedback + "\n");
                writer.flush();  // 刷新缓冲区，确保数据被写入文件
                System.out.println("Data has been written to the file.");

            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseResult<>(ResponseResult.Error, "服务器出错", feedback);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseResult<>(ResponseResult.Error, "服务器出错", feedback);
        }


        return new ResponseResult<>(ResponseResult.AccessOk, "反馈成功", feedback);
    }

    @Override
    public ResponseResult<Object> uploadHeadPortrait(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return new ResponseResult<>(ResponseResult.Error, "上传图片为空", null);
        }
        //获取请求信息
        String token = request.getHeader("token");
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        String userId = claims.getId();

        try {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            //判断是否合法
            if(LegalUtils.isImageFileName(originalFilename)!=null) return LegalUtils.isImageFileName(originalFilename);
            //构建文件路径
            String userDir = System.getProperty("user.dir"); // 获取当前工作目录
            String accessPath = null;//构建能够访问的路径
            if (originalFilename != null) {
                accessPath = "/user/headPortrait/" + LegalUtils.buildAccessPath(Integer.parseInt(userId),originalFilename);
            }
            String relativePath = null;// 构建相对路径，不包含 JAR 文件的信息
            if (originalFilename != null) {
                relativePath = "data/user/headPortrait/" + LegalUtils.buildAccessPath(Integer.parseInt(userId),originalFilename);
            }
            String absolutePath = Paths.get(userDir, relativePath).toString(); // 将当前工作目录与相对路径结合，创建绝对路径
            System.out.println("userPortraitUploadFile Absolute Path: " + absolutePath);
            System.out.println("userPortraitAccessPath: " + accessPath);

            // 保存文件到服务器
            // 检查文件是否存在，如果不存在则创建
            File savedfile = new File(absolutePath);
            if (!savedfile.exists()) {
                if (savedfile.createNewFile()) {
                    System.out.println("File created successfully.");
                } else {
                    System.out.println("Failed to create file.");
                }
            }
            file.transferTo(savedfile);
            //保存
            UserHeadPortrait userHeadPortrait = new UserHeadPortrait();
            userHeadPortrait.setUser_id(Integer.parseInt(userId));// userId
            userHeadPortrait.setUrl(accessPath);// url
            List<UserHeadPortrait> userHeadPortraits = userHeadPortraitDao
                    .selectByUser_idUserHeadPortraitList(Integer.parseInt(userId));
            if(userHeadPortraits.isEmpty()) {
                userHeadPortraitDao.insert(userHeadPortrait);
            }else{
                userHeadPortrait.setId(userHeadPortraits.get(0).getId());
                userHeadPortraitDao.updateById(userHeadPortrait);
            }


            return new ResponseResult<>(ResponseResult.AccessOk,"上传成功",null);
        }catch (IOException e) {
            e.printStackTrace();
            return new ResponseResult<>(ResponseResult.Error, "服务器出错", null);
        }
    }
}
