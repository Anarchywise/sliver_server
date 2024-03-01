package com.zh.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zh.entity.ResponseResult;

@Component
public class LegalUtils {

    private static final String PHONE_NUMBER_REGEX = "^1[3456789]\\d{9}$";

    private static int passwordMaxLength;
    private static int nicknameMaxLength;

    @Value("${custom.password.passwordMaxLength}")
    public void setPasswordMaxLength(int passwordMaxLength) {
        LegalUtils.passwordMaxLength = passwordMaxLength;
    }

    @Value("${custom.nickName.nicknameMaxLength}")
    public void setNicknameMaxLength(int nicknameMaxLength) {
        LegalUtils.nicknameMaxLength = nicknameMaxLength;
    }


    public static ResponseResult<Object> verifyPhone(String phoneNumber){
        //检查用户传入的号码的合法性
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return new ResponseResult<>(ResponseResult.IllegalPhoneNum, "手机号为空", null);
        }

        // 使用正则表达式进行匹配
        Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);

        if(matcher.matches()){
            return null;
        }

        return new ResponseResult<>(ResponseResult.IllegalPhoneNum, "手机号错误", null);
    }

    public static ResponseResult<Object> verifyPassword(String password){
        //1.检查是否包含空字符
        if (password.contains(" ")) {
            return new ResponseResult<>(ResponseResult.IllegalPassword, "密码不符合规定", null);
        }
        //2.检查字符是否超过20位
        if (password.length() > passwordMaxLength) {
            return new ResponseResult<>(ResponseResult.IllegalPassword, "密码不符合规定", null);
        }

        return null;
    }

    public static ResponseResult<Object> verifyNickname(String nickname){
        if(nickname.length()>nicknameMaxLength) return new ResponseResult<>(ResponseResult.IllegalNickname, "昵称不符合规定", null);
        return null;
    }

    public static ResponseResult<Object> isImageFileName(String fileName) {
        // 定义常见图片格式的正则表达式
        System.out.println(fileName);
        String imageFileRegex = "\\.(?i)(jpg|jpeg|png|gif|bmp)$";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(imageFileRegex);


        // 匹配文件名
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            System.out.println("Matched!");
            return null;
        } else {
            System.out.println("Not matched.");
            return new ResponseResult<>(ResponseResult.Error,"图片格式不支持",null);
        }
        
    }

    public static String buildAccessPath(long userId, String originalFilename) {
        // 构建日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String formattedDate = dateFormat.format(new Date());

        // 移除文件名中的非法字符
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");

        // 构建路径
        String accessPath = "__" + userId + "__" + "$" + formattedDate + "__" + sanitizedFilename;
        return accessPath;
    }

}
