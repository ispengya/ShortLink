package com.ispengya.shortlink.admin.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_group")
public class Group implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
    * ID
    */
    private Long id;

    /**
    * 分组标识
    */
    private String gid;

    /**
    * 分组名称
    */
    private String name;

    /**
    * 创建分组用户名
    */
    private String username;

    /**
    * 分组排序
    */
    private Integer sortOrder;

    /**
    * 创建时间
    */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
    * 修改时间
    */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
    * 删除标识 0：未删除 1：已删除
    */
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

}