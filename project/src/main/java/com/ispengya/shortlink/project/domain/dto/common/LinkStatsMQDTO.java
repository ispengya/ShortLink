package com.ispengya.shortlink.project.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ispengya
 * @date 2023/12/10 17:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkStatsMQDTO {
    private String fullShortUrl;
    private String username;
}
