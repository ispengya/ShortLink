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
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RestController
public class ShortLinkController {

    @DubboReference
    private ShortLinkDubboService shortLinkDubboService;


    /**
     * 短链接跳转原始链接
     */
    @GetMapping("/{short-uri}")
    @SneakyThrows
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) {
        shortLinkDubboService.jumpUrlV1(shortUri, request, response);
    }

    /**
     * 新增短链接
     */
    @PostMapping("/api/short-link/project/link")
    public Result<ShortLinkCreateRespDTO> createLink(@Valid @RequestBody ShortLinkCreateParam shortLinkCreateParam) {
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = shortLinkDubboService.createLink(shortLinkCreateParam);
        return Results.success(shortLinkCreateRespDTO);
    }

    /**
     * 修改短链接
     */
    @PutMapping("/api/short-link/project/link")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateParam shortLinkUpdateParam){
        shortLinkDubboService.updateShortLink(shortLinkUpdateParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/project/page/link")
    public Result<List<ShortLinkRespDTO>> pageLink(ShortLinkPageParam shortLinkPageParam) {
        List<ShortLinkRespDTO> list = shortLinkDubboService.pageLink(shortLinkPageParam);
        return Results.success(list);
    }

    /**
     * 查寻分组下的短链接数量
     */
    @GetMapping("/api/short-link/project/page/linkCount")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupLinkCount(@RequestParam("gid") String[] gid, @RequestParam("username") String username) {
        List<String> list = Arrays.asList(gid);
        List<ShortLinkGroupCountQueryRespDTO> dtoList = shortLinkDubboService.listGroupLinkCount(list, username);
        return Results.success(dtoList);
    }

}
