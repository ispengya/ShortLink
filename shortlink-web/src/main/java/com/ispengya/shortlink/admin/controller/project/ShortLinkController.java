package com.ispengya.shortlink.admin.controller.project;

import com.ispengya.shortlink.admin.common.biz.UserContext;
import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.dto.request.ShortLinkCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkPageParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkUpdateParam;
import com.ispengya.shortlink.project.dto.response.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.dto.response.ShortLinkRespDTO;
import com.ispengya.shortlink.project.service.ShortLinkDubboService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:21
 */
@Tag(name = "链易短-链接核心接口")
@RestController
@RequestMapping("/api/short-link/admin/v1")
public class ShortLinkController {

    @DubboReference(retries = 0)
    private ShortLinkDubboService shortLinkDubboService;



    /**
     * 新增短链接
     */
    @Operation(description = "新增短链接")
    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> createLink(@Valid @RequestBody ShortLinkCreateParam shortLinkCreateParam) {
        shortLinkCreateParam.setUsername(UserContext.getUsername());
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = shortLinkDubboService.createLink(shortLinkCreateParam);
        return Results.success(shortLinkCreateRespDTO);
    }

    /**
     * 修改短链接
     */
    @Operation(description = "修改短链接")
    @PutMapping("/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateParam shortLinkUpdateParam){
        shortLinkUpdateParam.setUsername(UserContext.getUsername());
        shortLinkDubboService.updateShortLink(shortLinkUpdateParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @Operation(description = "分页查询短链接")
    @GetMapping("/page")
    public Result<PageDTO<ShortLinkRespDTO>> pageLink(ShortLinkPageParam shortLinkPageParam) {
        shortLinkPageParam.setUsername(UserContext.getUsername());
        return Results.success(shortLinkDubboService.pageLink(shortLinkPageParam));
    }

    /**
     * 查寻分组下的短链接数量
     */
    @Operation(description = "查寻分组下的短链接数量")
    @GetMapping("/api/short-link/project/page/linkCount")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupLinkCount(@RequestParam("gid") String[] gid) {
        List<String> list = Arrays.asList(gid);
        List<ShortLinkGroupCountQueryRespDTO> dtoList = shortLinkDubboService.listGroupLinkCount(list, UserContext.getUsername());
        return Results.success(dtoList);
    }

}
