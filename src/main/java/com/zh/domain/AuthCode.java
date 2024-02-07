package com.zh.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("AuthCode")
public class AuthCode {
    String phone;
    String authCode;
    String expirationTime;
}
