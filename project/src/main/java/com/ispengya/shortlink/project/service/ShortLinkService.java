package com.ispengya.shortlink.project.service;

import com.ispengya.shortlink.project.domain.dto.req.ShortLinkCreateReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkPageReq;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:19
 */
public interface ShortLinkService {
    /**
     * 创建短链接
     * @param shortLinkCreateReqDTO
     * @return
     */
    ShortLinkCreateRespDTO createLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO);

    /**
     * 分页查询短链接
     * @param shortLinkPageReq
     * @return
     */
    List<ShortLinkRespDTO> pageLink(ShortLinkPageReq shortLinkPageReq);

    /**
     * 查询分组下的连接数量
     * @param requestParam
     * @param username
     * @return
     */
    List<ShortLinkGroupCountQueryRespDTO> listGroupLinkCount(List<String> requestParam, String username);
}
