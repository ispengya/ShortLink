package com.ispengya.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.domain.eneity.ShortLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShortLinkMapper extends BaseMapper<ShortLink> {
    List<ShortLinkGroupCountQueryRespDTO> getGroupLinkCount(@Param("gidList") List<String> gidList,@Param("username") String username);
}
