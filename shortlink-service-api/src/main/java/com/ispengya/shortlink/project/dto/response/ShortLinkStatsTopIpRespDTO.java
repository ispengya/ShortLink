package com.ispengya.shortlink.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsTopIpRespDTO implements Serializable {

    /**
     * 统计
     */
    private Integer cnt;

    /**
     * IP
     */
    private String ip;
}