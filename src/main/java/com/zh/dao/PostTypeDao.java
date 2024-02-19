package com.zh.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.entity.PostType;

@Mapper
public interface PostTypeDao extends BaseMapper<PostType>{
    
}
