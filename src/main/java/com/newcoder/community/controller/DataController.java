package com.newcoder.community.controller;

import com.newcoder.community.service.DataStatisticsService;
import com.newcoder.community.util.CommonUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author 86156
 */
@Controller
public class DataController {
    @Autowired
    DataStatisticsService dataStatisticsService;
    @RequestMapping(value = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String toData(){
        return "/site/admin/data";
    }
    @RequestMapping(value = "/uv",method = RequestMethod.POST)
    @ResponseBody
    public String statisticsUV(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        Long aLong = dataStatisticsService.statisticsUvUnion(start, end);
        return CommonUtil.getJsonObj(0,aLong.toString());
    }
    @RequestMapping(value = "/dau",method = RequestMethod.POST)
    @ResponseBody
    public String statisticsDAU(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        Long aLong = dataStatisticsService.staticDauUnion(start, end);
        return CommonUtil.getJsonObj(0,aLong.toString());
    }



}
