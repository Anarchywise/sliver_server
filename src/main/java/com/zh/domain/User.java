package com.zh.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private int id;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String phone;
    private String authCode;
}
