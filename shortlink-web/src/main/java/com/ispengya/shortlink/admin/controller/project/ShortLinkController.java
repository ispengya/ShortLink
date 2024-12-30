package com.ispengya.shortlink.admin.controller.project;

import com.ispengya.shortlink.admin.common.biz.UserContext;
import com.ispengya.shortlink.admin.common.util.EasyExcelWebUtil;
import com.ispengya.shortlink.common.result.PageDTO;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import com.ispengya.shortlink.project.dto.request.ShortLinkBatchCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkCreateParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkPageParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkUpdateParam;
import com.ispengya.shortlink.project.dto.response.*;
import com.ispengya.shortlink.project.service.ShortLinkDubboService;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author ispengya
 * @date 2023/11/25 14:21
 */
@RestController
@RequestMapping("/api/short-link/admin/v1")
public class ShortLinkController {

    @DubboReference(retries = 0)
    private ShortLinkDubboService shortLinkDubboService;



    /**
     * 新增短链接
     */
    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> createLink(@Valid @RequestBody ShortLinkCreateParam shortLinkCreateParam) {
        shortLinkCreateParam.setUsername(UserContext.getUsername());
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = shortLinkDubboService.createLink(shortLinkCreateParam);
        return Results.success(shortLinkCreateRespDTO);
    }

    /**
     * 新增短链接
     */
    @PostMapping("/v1/create")
    public Result<ShortLinkCreateRespDTO> createLinkByLock(@Valid @RequestBody ShortLinkCreateParam shortLinkCreateParam) {
        shortLinkCreateParam.setUsername(UserContext.getUsername());
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = shortLinkDubboService.createShortLinkByLock(shortLinkCreateParam);
        return Results.success(shortLinkCreateRespDTO);
    }

    /**
     * 修改短链接
     */
    @PostMapping("/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateParam shortLinkUpdateParam){
        shortLinkUpdateParam.setUsername(UserContext.getUsername());
        shortLinkDubboService.updateShortLinkV2(shortLinkUpdateParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/page")
    public Result<PageDTO<ShortLinkRespDTO>> pageLink(ShortLinkPageParam shortLinkPageParam) {
        shortLinkPageParam.setUsername(UserContext.getUsername());
        return Results.success(shortLinkDubboService.pageLink(shortLinkPageParam));
    }

    /**
     * 查寻分组下的短链接数量
     */
    @GetMapping("/api/short-link/project/page/linkCount")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupLinkCount(@RequestParam("gid") String[] gid) {
        List<String> list = Arrays.asList(gid);
        List<ShortLinkGroupCountQueryRespDTO> dtoList = shortLinkDubboService.listGroupLinkCount(list, UserContext.getUsername());
        return Results.success(dtoList);
    }

    /**
     * 批量创建短链接
     */
    @SneakyThrows
    @PostMapping("/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateParam requestParam, HttpServletResponse response) {
        ShortLinkBatchCreateRespDTO shortLinkBatchCreateRespDTO =
                shortLinkDubboService.batchCreateShortLink(requestParam);
        if (Objects.nonNull(shortLinkBatchCreateRespDTO)) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTO.getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "批量创建短链接-SaaS短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }

}
