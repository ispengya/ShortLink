package com.ispengya.shortlink.project.service;

import com.ispengya.shortlink.project.domain.dto.req.ShortLinkCreateReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkPageReq;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkUpdateReqDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:19
 */
public interface ShortLinkService {

    /**
     * 短链接跳转 V1版本 多级缓存
     * @param shortUri
     * @param request
     * @param response
     * @throws IOException
     */
    void jumpUrlV1(String shortUri, ServletRequest request, ServletResponse response) throws IOException;


    /**
     * 短链接跳转 V2版本引入jdhotkey
     * @param shortUri
     * @param request
     * @param response
     */
    void jumpUrlV2(String shortUri, ServletRequest request, ServletResponse response) throws IOException;

    /**
     * 创建短链接
     * @param shortLinkCreateReqDTO
     * @return
     */
    ShortLinkCreateRespDTO createLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO);

    /**
     * 修改短链接
     * @param shortLinkUpdateReqDTO
     */
    void updateShortLink(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO);

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
