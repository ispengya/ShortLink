package com.ispengya.shortlink.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * @author ispengya
 * @date 2023/11/20 16:46
 */
@Configuration
public class UserMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"createTime",Date::new,Date.class);
        this.strictInsertFill(metaObject,"updateTime",Date::new,Date.class);
        this.strictInsertFill(metaObject,"delFlag", () -> 0,Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"updateTime",Date::new,Date.class);
    }
}