package com.zh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScenicSpots {

    @TableId(type = IdType.AUTO)
    Integer id;
    String positionName;
    Float longitude;
    Float latitude;
    
}
