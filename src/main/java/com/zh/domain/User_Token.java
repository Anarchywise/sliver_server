package com.zh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User_Token {
    int user_id;
    String token;
}
