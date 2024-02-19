package com.zh.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ReplyPost {
    Integer PostId;
    String title;
    String type;
    Integer userId;
    Integer likes;
    String userNickname;
    String userHeadPortraitUrl;
    String contentText;
    List<String> ImagesUrls;
    Timestamp date;

}
