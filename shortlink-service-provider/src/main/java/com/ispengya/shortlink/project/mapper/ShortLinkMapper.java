package com.ispengya.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ispengya.shortlink.project.domain.ShortLink;
import com.ispengya.shortlink.project.dto.response.ShortLinkGroupCountQueryRespDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShortLinkMapper extends BaseMapper<ShortLink> {
    List<ShortLinkGroupCountQueryRespDTO> getGroupLinkCount(@Param("gidList") List<String> gidList, @Param("username") String username);
}
