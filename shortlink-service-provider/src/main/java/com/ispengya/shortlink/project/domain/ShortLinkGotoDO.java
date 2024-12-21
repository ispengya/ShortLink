package com.ispengya.shortlink.project.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@TableName("t_link_goto")
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class ShortLinkGotoDO implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    private Integer delFlag;
}
