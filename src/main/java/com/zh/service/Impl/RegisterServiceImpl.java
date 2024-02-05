package com.zh.service.Impl;

import com.zh.dao.AuthCodeDao;
import com.zh.dao.UserDao;
import com.zh.domain.AuthCode;
import com.zh.domain.ResponseResult;
import com.zh.domain.User;
import com.zh.service.RegisterService;
import com.zh.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDao userDao;

    @Autowired
    AuthCodeDao authCodeDao;

    @Autowired
    UserTokenService userTokenService;

    @Autowired
    AuthCodeServiceImpl authCodeService;

    private static final long EXPIRATION_TIME =  10*60*1000; //验证码10分钟过期

    private final static int PhoneNumLength = 11;

    private final static int PasswordMaxLength = 20;

    @Override
    public ResponseResult<Object> register(User user) {
        //没有用户名但是有电话号码
        if (Objects.isNull(user.getUsername()) && !Objects.isNull(user.getPhone())) {
            //将用户名设置位手机号码
            user.setUsername(user.getPhone());
            //把用户存入用户表
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userDao.insert(user);
            //获取token
            int userid = userDao.getByUsername(user.getUsername()).getId();
            String token = userTokenService.saveUserToken(userid);
            //将验证码删除
            authCodeDao.deleteByPhoneNum(new AuthCode(user.getPhone(), null,null ));

            //返回登录结果
            return new ResponseResult<>(ResponseResult.RegisterOk, "注册成功", token);

        }
        return null;
    }

    @Override
    public ResponseResult<Object> attemptRegister(User user) {
        //接受手机号
        //检查用户传入的号码是否正确
        //1.检查手机号码是否为11位
        if (user.getPhone().length() != PhoneNumLength) {
            return new ResponseResult<>(ResponseResult.IllegalPhoneNum, "手机号错误", null);
        }
        //2.检查手机号码是否已被注册
        if(!Objects.isNull(userDao.getByUsername(user.getPhone()))){
            return new ResponseResult<>(ResponseResult.IllegalPhoneNum, "手机号已被注册", null);
        }
        //检查密码的合法性
        //1.检查是否包含空字符
        if (user.getPassword().contains(" ")) {
            return new ResponseResult<>(ResponseResult.IllegalPassword, "密码不符合规定", null);
        }
        //2.检查字符是否超过20位
        if (user.getPassword().length() > PasswordMaxLength) {
            return new ResponseResult<>(ResponseResult.IllegalPassword, "密码不符合规定", null);
        }
        String phone = user.getPhone();
        //向手机号码发送验证码AuthCode
        String authCode = "111";
        //把验证码存入数据库
        long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
        authCodeService.saveAuthCode(new AuthCode(phone,authCode,Long.toString(expirationTime)));
        return new ResponseResult<>(ResponseResult.WaitForAuthCode,"等待验证码",null);
    }

    @Override
    public ResponseResult<Object> registerWithCode(User user) {
        //验证用户携带的authCode和数据库中的是否一致
        String phone = user.getPhone();
        AuthCode authCode = authCodeDao.getByPhone(phone);
        if(Objects.isNull(authCode)){
            return new ResponseResult<>(ResponseResult.IllegalAction,"你在干嘛?",null);
        }
        if(!Objects.equals(authCode.getAuthCode(), user.getAuthCode())){
            return new ResponseResult<>(ResponseResult.WrongAuthCode,"验证码错误",null);
        }
        //是否过期
         if(new Date(Long.parseLong(authCode.getExpirationTime())).before(new Date())){
             return new ResponseResult<>(ResponseResult.AuthCodeOutdated,"验证码超时",null);
         }
        //注册
        return register(user);
    }
}
