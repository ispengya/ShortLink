package com.ispengya.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ispengya.shortlink.project.domain.ShortLinkDO;
import com.ispengya.shortlink.project.dto.response.ShortLinkGroupCountQueryRespDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {
    /**
     * 短链接访问统计自增
     */
    void incrementStats(@Param("username") String username,
                        @Param("shortUri") String shortUri,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalUip") Integer totalUip);
    List<ShortLinkGroupCountQueryRespDTO> getGroupLinkCount(@Param("gidList") List<String> gidList, @Param("username") String username);
}
