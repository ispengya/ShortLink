package com.ispengya.shortlink.project.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * MyBatis-Plus 原数据自动填充类
 */
@Configuration
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"createTime",Date::new,Date.class);
        this.strictInsertFill(metaObject,"updateTime",Date::new,Date.class);
        this.strictInsertFill(metaObject,"delFlag", () -> 0,Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject,"updateTime",Date::new,Date.class);
    }
}
