package com.zh.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class Post {
    int id;
    int user_id;
    String title;
    Date date;
    int content_id;
}
