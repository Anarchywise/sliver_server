package com.zh.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zh.dao.UserDao;
import com.zh.domain.ResponseResult;
import com.zh.domain.User;
import com.zh.service.UserService;
import com.zh.utils.JsonUtils;
import com.zh.utils.JwtUtils;
import com.zh.utils.LegalUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.Objects;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult<Object> changePassword(HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String changedPassword = request.getParameter("changedPassword");
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
    public ResponseResult<Object> getUserDetails(String token) {
        Claims claims = JwtUtils.parseJwtToken(token);
        String user_id = claims.getId();
        User user = userDao.getById(Integer.parseInt(user_id));
        //BCryptPasswordEncoder不能被解密,看吧
        return new ResponseResult<>(ResponseResult.AccessOk, "获得用户信息", user);
    }

    @Override
    public ResponseResult<Object> changeNickname(HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = request.getParameter("username");
        String changedNickname = request.getParameter("changedNickname");
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
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "服务器出错", null);
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
}
