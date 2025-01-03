package com.ispengya.shortlink.admin.controller.project;

import com.ispengya.shortlink.admin.common.biz.UserContext;
import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.dto.request.RecycleBinPageParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRecoverParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRemoveParam;
import com.ispengya.shortlink.project.dto.request.RecycleSaveParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkRespDTO;
import com.ispengya.shortlink.project.service.RecycleBinDubboService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author ispengya
 * @date 2023/12/9 16:25
 */
@RestController
@RequestMapping("/api/short-link/admin/v1")
public class RecycleBinController {

    @DubboReference(retries = 0)
    private RecycleBinDubboService recycleBinService;

    /**
     * 移至回收站
     */
    @PostMapping("/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleSaveParam reqDTO) {
        reqDTO.setUsername(UserContext.getUsername());
        recycleBinService.save(reqDTO);
        return Results.success();
    }

    /**
     * 分页查询回收站链接
     */
    @GetMapping("/recycle-bin/page")
    public Result<PageDTO<ShortLinkRespDTO>> pageList(RecycleBinPageParam reqDTO) {
        reqDTO.setUsername(UserContext.getUsername());
        return Results.success( recycleBinService.pageList(reqDTO));
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/recycle-bin/recover")
    public Result<Void> recover(@RequestBody RecycleBinRecoverParam reqDTO){
        reqDTO.setUsername(UserContext.getUsername());
        recycleBinService.recover(reqDTO);
        return Results.success();
    }

    /**
     * 删除回收站
     */
    @PostMapping("/recycle-bin/remove")
    public Result<Void> delete(@RequestBody RecycleBinRemoveParam reqDTO){
        reqDTO.setUsername(UserContext.getUsername());
        recycleBinService.remove(reqDTO);
        return Results.success();
    }
}
