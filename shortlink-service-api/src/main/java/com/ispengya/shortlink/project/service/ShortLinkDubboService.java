package com.ispengya.shortlink.project.service;

import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.project.dto.request.ShortLinkBatchCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkPageParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkUpdateParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkBatchCreateRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkRespDTO;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:19
 */
public interface ShortLinkDubboService {

    /**
     * 短链接跳转 V1版本 多级缓存
     * @param shortUri
     * @param request
     * @param response
     * @throws IOException
     */
    void jumpUrlV1(String shortUri, ServletRequest request, ServletResponse response) throws IOException;


//    /**
//     * 短链接跳转 V2版本引入jdhotkey
//     * @param shortUri
//     * @param request
//     * @param response
//     */
//    void jumpUrlV2(String shortUri, ServletRequest request, ServletResponse response) throws IOException;

    /**
     * 创建短链接
     * @param shortLinkCreateParam
     * @return
     */
    ShortLinkCreateRespDTO createLink(ShortLinkCreateParam shortLinkCreateParam);

    /**
     * 根据分布式锁创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建信息
     */
    ShortLinkCreateRespDTO createShortLinkByLock(ShortLinkCreateParam requestParam);

    /**
     * 修改短链接
     * @param shortLinkUpdateParam
     */
    void updateShortLink(ShortLinkUpdateParam shortLinkUpdateParam);

    /**
     * 分页查询短链接
     * @param shortLinkPageParam
     * @return
     */
    PageDTO<ShortLinkRespDTO> pageLink(ShortLinkPageParam shortLinkPageParam);

    /**
     * 查询分组下的连接数量
     * @param requestParam
     * @param username
     * @return
     */
    List<ShortLinkGroupCountQueryRespDTO> listGroupLinkCount(List<String> requestParam, String username);

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 批量创建短链接返回参数
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateParam requestParam);
}
