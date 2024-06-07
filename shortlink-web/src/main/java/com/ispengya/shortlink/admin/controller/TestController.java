package com.ispengya.shortlink.admin.controller;

import com.ispengya.shortlink.admin.dao.GroupDao;
import com.ispengya.shortlink.admin.domain.entity.Group;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author ispengya
 * @date 2023/11/15 18:01
 */
@RestController
public class TestController {
    @Autowired
    GroupDao groupDao;

    @GetMapping("/test")
    public Result<Group> test(){
        Group groupByGIdAndUserName = groupDao.getGroupByGIdAndUserName("ispengya", "ukyu2h");
        return Results.success(groupByGIdAndUserName);
    }
}
