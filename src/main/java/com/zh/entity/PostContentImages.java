package com.zh.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostContentImages {
    Integer id;
    Integer userId;
    Integer contentId;
    Integer imageOrder;
    String url;
}
