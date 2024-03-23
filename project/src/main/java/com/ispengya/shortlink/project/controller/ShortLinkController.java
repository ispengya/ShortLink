package com.ispengya.shortlink.project.controller;

import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkCreateReqDTO;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkPageReq;
import com.ispengya.shortlink.project.domain.dto.req.ShortLinkUpdateReqDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkCreateRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.ispengya.shortlink.project.domain.dto.resp.ShortLinkRespDTO;
import com.ispengya.shortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/25 14:21
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;


    /**
     * 短链接跳转原始链接
     */
    @GetMapping("/{short-uri}")
    @SneakyThrows
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) {
        shortLinkService.jumpUrlV1(shortUri, request, response);
    }

    /**
     * 新增短链接
     */
    @PostMapping("/api/short-link/project/link")
    public Result<ShortLinkCreateRespDTO> createLink(@Valid @RequestBody ShortLinkCreateReqDTO shortLinkCreateReqDTO) {
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = shortLinkService.createLink(shortLinkCreateReqDTO);
        return Results.success(shortLinkCreateRespDTO);
    }

    /**
     * 修改短链接
     */
    @PutMapping("/api/short-link/project/link")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO shortLinkUpdateReqDTO){
        shortLinkService.updateShortLink(shortLinkUpdateReqDTO);
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/project/page/link")
    public Result<List<ShortLinkRespDTO>> pageLink(ShortLinkPageReq shortLinkPageReq) {
        List<ShortLinkRespDTO> list = shortLinkService.pageLink(shortLinkPageReq);
        return Results.success(list);
    }

    /**
     * 查寻分组下的短链接数量
     */
    @GetMapping("/api/short-link/project/page/linkCount")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupLinkCount(@RequestParam("gid") String[] gid, @RequestParam("username") String username) {
        List<String> list = Arrays.asList(gid);
        List<ShortLinkGroupCountQueryRespDTO> dtoList = shortLinkService.listGroupLinkCount(list, username);
        return Results.success(dtoList);
    }

}
