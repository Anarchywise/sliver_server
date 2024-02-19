package com.zh.service;

import com.zh.entity.ResponseResult;

public interface TravelService {
    ResponseResult<Object> getTravelRoute(String cityName, int num);

    ResponseResult<Object> getSpotsIntroduction(String spotsName);
}
