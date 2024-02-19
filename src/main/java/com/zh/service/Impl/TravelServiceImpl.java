package com.zh.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zh.dao.CitySpotsDao;
import com.zh.dao.ScenicSpotsDao;
import com.zh.entity.CitySpots;
import com.zh.entity.ResponseResult;
import com.zh.entity.ScenicSpots;
import com.zh.service.TravelService;

@Service
public class TravelServiceImpl implements TravelService{

    @Autowired
    CitySpotsDao citySpotsDao;

    @Autowired
    ScenicSpotsDao scenicSpotsDao;

    @Override
    public ResponseResult<Object> getTravelRoute(String cityName, int num) {

        //先查citySpots表,获取num个景点id
        List<CitySpots> citySpotsList = citySpotsDao.geCitySpotsByCityName(cityName);
        List<CitySpots> citySpotsNumList = citySpotsList.subList(0, Math.min(num, citySpotsList.size()));

        //查出ScenicSpot中的景点数据
        List<ScenicSpots> ScenicSpotsList = new ArrayList<>();
        for(CitySpots citySpots : citySpotsNumList){
            ScenicSpots scenicSpots = new ScenicSpots();
            scenicSpots = scenicSpotsDao.selectById(citySpots.getSpotsId());
            ScenicSpotsList.add(scenicSpots);
        }
        //重构返回的景点数据(景点的线路)
        int index = 1;
        for(ScenicSpots scenicSpots: ScenicSpotsList){
            scenicSpots.setId(index);
            scenicSpots.setPictureUrl("/spotsPicture/"+scenicSpots.getPositionName()+".jpg");
            index++;
        }

       return new ResponseResult<>(ResponseResult.AccessOk,"获取旅游路线成功",ScenicSpotsList);
    }
    
    
}
