package com.zh.dao;

import com.zh.domain.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UserDao {

    @Select("select * from User where id =#{id}")
    User getById(int id);

    @Select("select * from User where username =#{username}")
    User getByUsername(String username);

    @Select("select * from User where nickname =#{nickname}")
    User getByNickname(String nickname);

    @Delete("delete from User where id =#{id}")
    void deleteById(int id);

    @Delete("delete from User where username =#{username}")
    void deleteByUsername(String username);

    @Insert("insert into User (username, nickname, password, email, phone) values (#{username},#{nickname},#{password},#{email},#{phone})" )
    void insert(User user);
}
