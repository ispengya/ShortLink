package com.ispengya.shortlink.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispengya.shortlink.admin.serializer.PhoneSerializer;
import com.ispengya.shortlink.admin.serializer.RealNameSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/16 16:55
 */
@Data
public class UserInfoRespDTO implements Serializable {
    /**
     * ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 用户名
     */
    private String username;


    /**
     * 真实姓名
     */
//    @JsonSerialize(using = RealNameSerializer.class)
    private String realName;

    /**
     * 手机号
     */
//    @JsonSerialize(using = PhoneSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;


}
