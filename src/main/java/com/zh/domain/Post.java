package com.zh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    Integer id;
    Integer userId;
    String title;
    Timestamp date;
    Integer contentId;
}
