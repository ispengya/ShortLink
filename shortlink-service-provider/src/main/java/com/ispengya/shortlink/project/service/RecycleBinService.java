package com.ispengya.shortlink.project.service;

import com.ispengya.shortlink.project.domain.dto.req.RecycleBinRecoverReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleBinRemoveReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleSaveReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleBinPageReqDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/12/9 16:27
 */

public interface RecycleBinService {
    /**
     * 转移回收站
     * @param reqDTO
     */
    void save(RecycleSaveReqDTO reqDTO);

    /**
     * 查询回收站短链接
     * @param reqDTO
     * @return
     */
    List<ShortLinkRespDTO> pageList(RecycleBinPageReqDTO reqDTO);

    /**
     * 恢复短链接
     * @param reqDTO
     */
    void recover(RecycleBinRecoverReqDTO reqDTO);

    /**
     * 删除回收站
     * @param reqDTO
     */
    void remove(RecycleBinRemoveReqDTO reqDTO);
}
