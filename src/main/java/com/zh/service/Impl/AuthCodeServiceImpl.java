package com.zh.service.Impl;

import com.zh.dao.AuthCodeDao;
import com.zh.domain.AuthCode;
import com.zh.service.AuthCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthCodeServiceImpl implements AuthCodeService {

    @Autowired
    AuthCodeDao authCodeDao;
    @Override
    public void saveAuthCode(AuthCode authCode) {
        String phone = authCode.getPhone();
        if(Objects.isNull(authCodeDao.getByPhone(phone))){
            authCodeDao.insert(authCode);
        }else{
            authCodeDao.update(authCode);
        }
    }
}
