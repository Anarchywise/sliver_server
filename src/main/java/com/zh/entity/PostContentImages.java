package com.zh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostContentImages {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer userId;
    Integer contentId;
    Integer imageOrder;
    String url;
}
