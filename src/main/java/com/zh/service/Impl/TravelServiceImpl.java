package com.zh.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ldap.Rdn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zh.dao.CitySpotsDao;
import com.zh.dao.SpotsDao;
import com.zh.dao.SpotsIntroductionDao;
import com.zh.entity.CitySpots;
import com.zh.entity.ResponseResult;
import com.zh.entity.Spots;
import com.zh.entity.SpotsIntroduction;
import com.zh.service.TravelService;

@Service
public class TravelServiceImpl implements TravelService{

    @Autowired
    CitySpotsDao citySpotsDao;

    @Autowired
    SpotsDao scenicSpotsDao;

    @Autowired
    SpotsIntroductionDao spotsIntroductionDao;

    @Override
    public ResponseResult<Object> getTravelRoute(String cityName, int num) {

        //先查citySpots表,获取num个景点id
        List<CitySpots> citySpotsList = citySpotsDao.geCitySpotsByCityName(cityName);
        List<CitySpots> citySpotsNumList = citySpotsList.subList(0, Math.min(num, citySpotsList.size()));

        //查出ScenicSpot中的景点数据
        List<Spots> ScenicSpotsList = new ArrayList<>();
        for(CitySpots citySpots : citySpotsNumList){
            Spots scenicSpots = new Spots();
            scenicSpots = scenicSpotsDao.selectById(citySpots.getSpotsId());
            ScenicSpotsList.add(scenicSpots);
        }
        //重构返回的景点数据(景点的线路)
        int index = 1;
        for(Spots scenicSpots: ScenicSpotsList){
            scenicSpots.setId(index);
            scenicSpots.setPictureUrl("/spotsPicture/"+scenicSpots.getSpotsName()+".jpg");
            index++;
        }

       return new ResponseResult<>(ResponseResult.AccessOk,"获取旅游路线成功",ScenicSpotsList);
    }

    @Override
    public ResponseResult<Object> getSpotsIntroduction(String spotsName) {
        // TODO Auto-generated method stub
        List<SpotsIntroduction> spotsIntroductionList = new ArrayList<>();
        Map<String,Object> nameMap = new HashMap<>();
        nameMap.put("spots_name", spotsName);
        spotsIntroductionList = spotsIntroductionDao.selectByMap(nameMap);
        return new ResponseResult<>(ResponseResult.AccessOk,"获取景点介绍成功",spotsIntroductionList);
    }
    
    
}
