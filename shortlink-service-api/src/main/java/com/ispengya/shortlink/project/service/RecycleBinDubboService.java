package com.ispengya.shortlink.project.service;

import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.project.dto.request.RecycleBinPageParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRecoverParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRemoveParam;
import com.ispengya.shortlink.project.dto.request.RecycleSaveParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkRespDTO;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/12/9 16:27
 */

public interface RecycleBinDubboService {
    /**
     * 转移回收站
     * @param reqDTO
     */
    void save(RecycleSaveParam reqDTO);

    /**
     * 查询回收站短链接
     * @param reqDTO
     * @return
     */
    PageDTO<ShortLinkRespDTO> pageList(RecycleBinPageParam reqDTO);

    /**
     * 恢复短链接
     * @param reqDTO
     */
    void recover(RecycleBinRecoverParam reqDTO);

    /**
     * 删除回收站
     * @param reqDTO
     */
    void remove(RecycleBinRemoveParam reqDTO);
}
