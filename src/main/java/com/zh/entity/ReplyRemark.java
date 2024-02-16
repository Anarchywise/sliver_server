package com.zh.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyRemark {
    Integer id;
    Integer userId;
    String userNickname;
    String userHeadPortraitUrl;
    String contentText;
    Timestamp date;

}
