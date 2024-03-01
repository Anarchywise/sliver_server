package com.zh.service.Impl;

import com.zh.dao.UserDao;
import com.zh.dao.UserHeadPortraitDao;
import com.zh.entity.ResponseResult;
import com.zh.entity.User;
import com.zh.entity.UserHeadPortrait;
import com.zh.service.UserService;
import com.zh.utils.LegalUtils;
import com.zh.utils.PictureUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.nio.file.Files;
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
    public ResponseResult<Object> changePassword(String username, int userId, String password, String changedPassword) {
        User user = userDao.getByUsername(username);
        //判断用户是否存在
        if (Objects.isNull(user)) return new ResponseResult<>(ResponseResult.IllegalAction, "用户不存在", null);
        //用户信息是否和token一致
        if (userId!= user.getId())
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
    public ResponseResult<Object> getUserDetails(Integer userId, Integer userIdSelf) {

        if(userId==userIdSelf){//如果是本人,返回本人的信息,不处理
            User user = userDao.getById(userId);
            if(!userHeadPortraitDao.selectByUser_idUserHeadPortraitList(user.getId()).isEmpty()){
                user.setHeadPortraitUrl(userHeadPortraitDao
                        .selectByUser_idUserHeadPortraitList(user.getId()).get(0).getUrl());
            }
            return new ResponseResult<>(ResponseResult.AccessOk, "获得用户信息", user);
        }else{
            User user = userDao.getById(userId);
            if(Objects.isNull(user)) return new ResponseResult<>(ResponseResult.IllegalAction,"用户不存在",null);
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
    public ResponseResult<Object> changeNickname(String username, int userId, String changedNickname) {
        User user = userDao.getByUsername(username);
        //判断用户是否存在
        if (Objects.isNull(user)) return new ResponseResult<>(ResponseResult.IllegalAction, "用户不存在", null);
        //用户信息是否和token一致
        if (userId != user.getId())
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
    public ResponseResult<Object> feedback(int userId, String feedback) {

        //构建文件路径
        String userDir = System.getProperty("user.dir"); // 获取当前工作目录
        String relativePath = "data/" + userId + "feedback.txt";// 构建相对路径，不包含 JAR 文件的信息
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
    public ResponseResult<Object> uploadHeadPortrait(int userId, MultipartFile getFile){

        
            // 获取文件名
            String originalFilename = getFile.getOriginalFilename();

            //判断是否合法
            if(LegalUtils.isImageFileName(originalFilename)!=null) return LegalUtils.isImageFileName(originalFilename);

            //压缩图片
            byte[] compressedImage;
            try {
                compressedImage = PictureUtils.compressImage(getFile.getBytes());
            } catch (IOException e) {
                System.out.println("压缩图片失败");
                return new ResponseResult<>(ResponseResult.Error, "压缩图片失败", null);
            }
            //构建文件路径
            String userDir = System.getProperty("user.dir"); // 获取当前工作目录
            String accessPath = "http://39.101.67.214:8080/user/headPortrait/" + LegalUtils.buildAccessPath(userId,originalFilename);//构建能够访问的路径
            String relativePath = "data/user/headPortrait/" + LegalUtils.buildAccessPath(userId,originalFilename);// 构建相对路径，不包含 JAR 文件的信息
            String absolutePath = Paths.get(userDir, relativePath).toString(); // 将当前工作目录与相对路径结合，创建绝对路径
            System.out.println("userPortraitUploadFile Absolute Path: " + absolutePath);
            System.out.println("userPortraitAccessPath: " + accessPath);

            // 保存文件到服务器
            try {
                Files.createDirectories(Paths.get(absolutePath).getParent());
                Files.write(Paths.get(absolutePath), compressedImage);
                System.out.println("File saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save file.");
                return new ResponseResult<>(ResponseResult.Error, "保存文件失败", null);
            }

            //保存到数据库
            UserHeadPortrait userHeadPortrait = new UserHeadPortrait();
            userHeadPortrait.setUser_id(userId);// userId
            userHeadPortrait.setUrl(accessPath);// url
            List<UserHeadPortrait> userHeadPortraits = userHeadPortraitDao
                    .selectByUser_idUserHeadPortraitList(userId);
            if(userHeadPortraits.isEmpty()) {
                userHeadPortraitDao.insert(userHeadPortrait);
            }else{
                userHeadPortrait.setId(userHeadPortraits.get(0).getId());
                userHeadPortraitDao.updateById(userHeadPortrait);
            }

            return new ResponseResult<>(ResponseResult.AccessOk,"上传成功",null);
    }
}
