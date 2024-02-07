package com.zh.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.domain.Post;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostDao extends BaseMapper<Post>{

    @Select("select * from Post where userid = #{user_id}}")
    List<Post> getPostByUserId(int user_id);


    @Select("SELECT * FROM Post ORDER BY RAND() LIMIT #{num}")
    List<Post> getPostRandom(int num);
}
