package com.zh.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.zh.entity.ResponseResult;
import com.zh.service.Impl.TravelServiceImpl;
import com.zh.utils.JsonUtils;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
public class TravelControler {
    
    @Autowired
    TravelServiceImpl travelServiceImpl;
    
    @PostMapping("/travel/getRoute")
    public ResponseResult<Object> getTravelRoute(HttpServletRequest request) {
        //TODO: process POST request
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.JsonError);
        try {
            String cityName = jsonNode.get("cityName").asText(); 
            int num = jsonNode.get("num").asInt();
            return travelServiceImpl.getTravelRoute(cityName, num);
        } catch (Exception e) {
            System.out.println("解析错误");
            return new ResponseResult<>(ResponseResult.JsonError);
        }

    }
    
}
