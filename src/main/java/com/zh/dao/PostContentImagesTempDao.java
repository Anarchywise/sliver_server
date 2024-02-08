package com.zh.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.domain.PostContentImagesTemp;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface PostContentImagesTempDao extends BaseMapper<PostContentImagesTemp> {

    @Select("select * from Content_Images_Temp where user_id = #{user_id} order by image_order desc")
    ArrayList<PostContentImagesTemp> selectByUserIdOrderByImage_orderDescInt(Integer user_id);

    @Select("select * from Content_Images_Temp where user_id = #{user_id}")
    ArrayList<PostContentImagesTemp> selectByUserId(int user_id);

    @Delete("DELETE FROM Content_Images_Temp WHERE user_id = #{userId} And image_Order = #{imageOrder}")
    void deleteByUserIdAndImageOrder(int userId,int imageOrder);
}
