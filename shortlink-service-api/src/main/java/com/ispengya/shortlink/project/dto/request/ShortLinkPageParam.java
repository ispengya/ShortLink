package com.ispengya.shortlink.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ispengya
 * @date 2023/11/25 17:42
 */
@Data
@Schema(name = "分页查询短链接实体")
public class ShortLinkPageParam implements Serializable {
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
     * 排序标识
     */
    private String orderTag;

    /**
     * 当前页
     */
    @Schema(defaultValue = "1")
    private Long current;

    /**
     * 页大小
     */
    @Schema(defaultValue = "10")
    private Long pageSize;
}
