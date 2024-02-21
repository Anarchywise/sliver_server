package com.zh.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.zh.entity.ResponseResult;
import com.zh.entity.SpotsIntroduction;
import com.zh.service.Impl.TravelServiceImpl;
import com.zh.utils.JsonUtils;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class TravelControler {
    
    @Autowired
    TravelServiceImpl travelServiceImpl;
    
    @PostMapping("/travel/getRoute")
    public ResponseResult<Object> getTravelRoute(HttpServletRequest request) {
        //TODO: process POST request
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        String cityName = jsonNode.get("cityName").asText(); 
        int num = jsonNode.get("num").asInt();
        return travelServiceImpl.getTravelRoute(cityName, num);
    }

    @PostMapping("/travel/getSpotsIntroduction")
    public ResponseResult<Object> getSpotsIntroduction(@RequestBody SpotsIntroduction spotsIntroduction) {
        //TODO: process POST request
        return travelServiceImpl.getSpotsIntroduction(spotsIntroduction.getSpotsName());
    }
    
    
    
}
