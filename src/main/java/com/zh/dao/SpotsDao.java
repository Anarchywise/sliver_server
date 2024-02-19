package com.zh.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.zh.entity.Spots;

@Mapper
public interface SpotsDao extends BaseMapper<Spots> {
    
}
