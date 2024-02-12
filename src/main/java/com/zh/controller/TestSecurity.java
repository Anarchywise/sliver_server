package com.zh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestSecurity {
    @RequestMapping("/test")
    @ResponseBody
    public String Test(){
        return "test method";
    }
}
