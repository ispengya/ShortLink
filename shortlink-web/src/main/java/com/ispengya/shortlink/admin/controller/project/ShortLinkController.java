package com.ispengya.shortlink.admin.controller.project;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:21
 */
@Tag(name = "链易短-链接核心接口")
@RestController
public class ShortLinkController {

    @DubboReference
    private ShortLinkDubboService shortLinkDubboService;



    /**
     * 新增短链接
     */
    @Operation(description = "新增短链接")
    @PostMapping("/api/short-link/project/link")
    public Result<ShortLinkCreateRespDTO> createLink(@Valid @RequestBody ShortLinkCreateParam shortLinkCreateParam) {
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = shortLinkDubboService.createLink(shortLinkCreateParam);
        return Results.success(shortLinkCreateRespDTO);
    }

    /**
     * 修改短链接
     */
    @Operation(description = "修改短链接")
    @PutMapping("/api/short-link/project/link")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateParam shortLinkUpdateParam){
        shortLinkDubboService.updateShortLink(shortLinkUpdateParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @Operation(description = "分页查询短链接")
    @GetMapping("/api/short-link/project/page/link")
    public Result<List<ShortLinkRespDTO>> pageLink(ShortLinkPageParam shortLinkPageParam) {
        List<ShortLinkRespDTO> list = shortLinkDubboService.pageLink(shortLinkPageParam);
        return Results.success(list);
    }

    /**
     * 查寻分组下的短链接数量
     */
    @Operation(description = "查寻分组下的短链接数量")
    @GetMapping("/api/short-link/project/page/linkCount")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupLinkCount(@Parameter(schema = @Schema(defaultValue = "wvgikz")) @RequestParam("gid") String[] gid, @Parameter(schema = @Schema(defaultValue = "zaizaige1"))@RequestParam("username") String username) {
        List<String> list = Arrays.asList(gid);
        List<ShortLinkGroupCountQueryRespDTO> dtoList = shortLinkDubboService.listGroupLinkCount(list, username);
        return Results.success(dtoList);
    }

}
