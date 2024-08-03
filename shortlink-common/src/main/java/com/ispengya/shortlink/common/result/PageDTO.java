package com.ispengya.shortlink.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ispengya
 * @date 2024/8/3 11:59
 */
@Data
public class PageDTO<T> implements Serializable {
    private List<T> records;
    private long total;
    private long size;
    private long current;
    private long pages;
}
