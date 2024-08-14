package com.ispengya.shortlink.admin.controller.admin;

import com.ispengya.shortlink.admin.dto.request.GroupAddParam;
import com.ispengya.shortlink.admin.dto.request.GroupSortParam;
import com.ispengya.shortlink.admin.dto.request.GroupUpdateParam;
import com.ispengya.shortlink.admin.dto.response.GroupListRespDTO;
import com.ispengya.shortlink.admin.service.GroupDubboService;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/21 16:40
 */
@Tag(name = "链易短-链接分组接口")
@RestController
@RequestMapping("/api/short-link/admin/v1")
public class GroupController {
    @DubboReference(retries = 0)
    private GroupDubboService groupDubboService;

    /**
     * 添加分组
     */
    @PostMapping("/group")
    @Operation(description = "添加分组")
    public Result<Void> save(@Valid @RequestBody GroupAddParam param){
        groupDubboService.saveGroup(param);
        return Results.success();
    }

    /**
     * 查询短链接分组集合
     */
    @Operation(description = "查询短链接分组集合")
    @GetMapping("/group")
    public Result<List<GroupListRespDTO>> list(){
        return Results.success(groupDubboService.searchGroupList());
    }

    /**
     * 排序分组
     */
    @Operation(description = "排序分组")
    @PutMapping("/group/sort")
    public Result<Void> sort(@RequestBody List<GroupSortParam> reqDTOList){
//        groupService.sort(reqDTOList);
        return Results.success();
    }

    /**
     * 修改短链接分组名称
     */
    @Operation(description = "修改短链接分组名称")
    @PutMapping("/group")
    public Result<Void> updateGroup(@RequestBody GroupUpdateParam requestParam) {
        groupDubboService.updateGroup(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     */
    @Operation(description = "删除短链接分组")
    @DeleteMapping("/group")
    public Result<Void> deleteGroup(@RequestParam String gid) {
        groupDubboService.deleteGroup(gid);
        return Results.success();
    }
}
