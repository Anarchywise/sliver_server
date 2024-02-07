package com.zh.domain;

import lombok.Data;

@Data
public class Content_Images {
    int id;
    int user_id;
    int content_id;
    int image_order;
    String url;
}
