package com.ispengya.shortlink.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ispengya
 * @date 2023/11/25 14:26
 */
@Data
@Schema(name = "短链接创建实体")
public class ShortLinkCreateParam implements Serializable {
    /**
     * 域名,默认或者自己的域名
     */
    @Schema(defaultValue = "cn.zaizaige.top")
    private String domain;

    /**
     * 原始链接
     */
    @Schema(defaultValue = "chat.zaizaige.top")
    private String originUrl;

    /**
     * 分组标识
     */
    @Schema(defaultValue = "wvgikz")
    private String gid;

    /**
     * 用户名
     */
    @Schema(defaultValue = "zaizaige1")
    private String username;

    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    @Schema(defaultValue = "0")
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    @Schema(defaultValue = "0")
    private Integer validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    /**
     * 描述
     */
    private String describe;
}
