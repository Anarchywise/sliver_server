package com.zh.utils;

import com.zh.domain.ResponseResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegalUtils {

    private final static int PhoneNumLength = 11;
    private static final String PHONE_NUMBER_REGEX = "^1[3456789]\\d{9}$";

    private final static int PasswordMaxLength = 20;
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
        if (password.length() > PasswordMaxLength) {
            return new ResponseResult<>(ResponseResult.IllegalPassword, "密码不符合规定", null);
        }

        return null;
    }

}
