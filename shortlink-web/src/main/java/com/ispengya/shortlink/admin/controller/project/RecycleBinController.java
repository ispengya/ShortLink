package com.ispengya.shortlink.admin.controller.project;

import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.dto.request.RecycleBinPageParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRecoverParam;
import com.ispengya.shortlink.project.dto.request.RecycleBinRemoveParam;
import com.ispengya.shortlink.project.dto.request.RecycleSaveParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkRespDTO;
import com.ispengya.shortlink.project.service.RecycleBinDubboService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/12/9 16:25
 */
@RestController
@RequestMapping("/api/short-link/recycle-bin")
public class RecycleBinController {

    @DubboReference
    private RecycleBinDubboService recycleBinService;

    /**
     * 移至回收站
     */
    @PostMapping("/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleSaveParam reqDTO) {
        recycleBinService.save(reqDTO);
        return Results.success();
    }

    /**
     * 分页查询回收站链接
     */
    @GetMapping("/page")
    public Result<List<ShortLinkRespDTO>> pageList(RecycleBinPageParam reqDTO) {
        List<ShortLinkRespDTO> list = recycleBinService.pageList(reqDTO);
        return Results.success(list);
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/recover")
    public Result<Void> recover(@RequestBody RecycleBinRecoverParam reqDTO){
        recycleBinService.recover(reqDTO);
        return Results.success();
    }

    /**
     * 删除回收站
     */
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody RecycleBinRemoveParam reqDTO){
        recycleBinService.remove(reqDTO);
        return Results.success();
    }
}
