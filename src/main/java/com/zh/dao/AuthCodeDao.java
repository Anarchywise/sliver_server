package com.zh.dao;

import com.zh.domain.AuthCode;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AuthCodeDao {
    @Insert("insert into AuthCode (phone, authCode, expirationTime) values (#{phone},#{authCode},#{expirationTime})")
    void insert(AuthCode authCode);

    @Update("update AuthCode set authCode = #{authCode}, expirationTime = #{expirationTime} where phone = #{phone}")
    void update(AuthCode authCode);

    @Select("select * from AuthCode where phone = #{phone} LIMIT 1")
    AuthCode getByPhone(String phone);

    @Delete("delete from AuthCode where phone = #{phone}")
    void deleteByPhoneNum(AuthCode authCode);
}
