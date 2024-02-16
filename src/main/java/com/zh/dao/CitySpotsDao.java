package com.zh.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.entity.CitySpots;

@Mapper
public interface CitySpotsDao extends BaseMapper<CitySpots>{

    @Select("select * from city_spots where city_name = #{cityName} order by RAND()")
    List<CitySpots> geCitySpotsByCityName(String cityName);
    
}
