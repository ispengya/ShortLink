package com.ispengya.shortlink.admin.controller;

import com.ispengya.shortlink.admin.domain.dto.req.GroupAddReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.GroupSortReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.GroupUpdateReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.GroupListRespDTO;
import com.ispengya.shortlink.admin.service.GroupService;
import com.ispengya.shortlink.common.result.Result;
import com.ispengya.shortlink.common.result.Results;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/21 16:40
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin")
public class GroupController {
    private final GroupService groupService;

    /**
     * 添加分组
     */
    @PostMapping("/auth/group")
    public Result<Void> save(@Valid @RequestBody GroupAddReqDTO groupAddReqDTO){
        groupService.saveGroup(groupAddReqDTO);
        return Results.success();
    }

    /**
     * 查询短链接分组集合
     */
    @GetMapping("/auth/group")
    public Result<List<GroupListRespDTO>> list(){
        return Results.success(groupService.searchGroupList());
    }

    /**
     * 排序分组
     */
    @PutMapping("/auth/group/sort")
    public Result<Void> sort(@RequestBody List<GroupSortReqDTO> reqDTOList){
        groupService.sort(reqDTOList);
        return Results.success();
    }

    /**
     * 修改短链接分组名称
     */
    @PutMapping("/auth/group")
    public Result<Void> updateGroup(@RequestBody GroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     */
    @DeleteMapping("/auth/group")
    public Result<Void> updateGroup(@RequestParam String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }
}
