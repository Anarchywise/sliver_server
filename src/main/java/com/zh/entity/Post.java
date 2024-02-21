package com.zh.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer userId;
    String title;
    Timestamp date;
    Integer contentId;
}
