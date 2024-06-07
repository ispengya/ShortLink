package com.ispengya.shortlink.project.controller;

import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.domain.dto.req.RecycleBinRecoverReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleBinRemoveReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleSaveReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.RecycleBinPageReqDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;
import com.ispengya.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/12/9 16:25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/recycle-bin")
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 移至回收站
     */
    @PostMapping("/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleSaveReqDTO reqDTO) {
        recycleBinService.save(reqDTO);
        return Results.success();
    }

    /**
     * 分页查询回收站链接
     */
    @GetMapping("/page")
    public Result<List<ShortLinkRespDTO>> pageList(RecycleBinPageReqDTO reqDTO) {
        List<ShortLinkRespDTO> list = recycleBinService.pageList(reqDTO);
        return Results.success(list);
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/recover")
    public Result<Void> recover(@RequestBody RecycleBinRecoverReqDTO reqDTO){
        recycleBinService.recover(reqDTO);
        return Results.success();
    }

    /**
     * 删除回收站
     */
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody RecycleBinRemoveReqDTO reqDTO){
        recycleBinService.remove(reqDTO);
        return Results.success();
    }
}
