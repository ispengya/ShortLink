package com.ispengya.shortlink.project.controller;

import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkCreateReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkPageReq;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;
import com.ispengya.shortlink.project.service.ShortLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:21
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/project")
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 新增短链接
     */
    @PostMapping("/auth/link")
    public Result<ShortLinkCreateRespDTO> createLink(@Valid @RequestBody ShortLinkCreateReqDTO shortLinkCreateReqDTO) {
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = shortLinkService.createLink(shortLinkCreateReqDTO);
        return Results.success(shortLinkCreateRespDTO);
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/auth/page/link")
    public Result<List<ShortLinkRespDTO>> pageLink(ShortLinkPageReq shortLinkPageReq) {
        List<ShortLinkRespDTO> list = shortLinkService.pageLink(shortLinkPageReq);
        return Results.success(list);
    }

    /**
     * 查寻分组下的短链接数量
     */
    @GetMapping("/auth/page/linkCount")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupLinkCount(@RequestParam("gid") String[] gid, @RequestParam("username") String username) {
        List<String> list = Arrays.asList(gid);
        List<ShortLinkGroupCountQueryRespDTO> dtoList = shortLinkService.listGroupLinkCount(list, username);
        return Results.success(dtoList);
    }

}
