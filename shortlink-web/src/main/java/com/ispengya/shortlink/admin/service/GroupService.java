package com.ispengya.shortlink.admin.service;

import com.ispengya.shortlink.admin.domain.dto.req.GroupAddReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.GroupSortReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.GroupUpdateReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.GroupListRespDTO;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/21 15:50
 */
public interface GroupService {
    /**
     * 添加短链接分组
     * @param groupAddReqDTO
     */
    void saveGroup(GroupAddReqDTO groupAddReqDTO);

    /**
     * 查询短链接分组
     * @return
     */
    List<GroupListRespDTO> searchGroupList();

    /**
     * 排序分组
     * @param reqDTOList
     */
    void sort(List<GroupSortReqDTO> reqDTOList);

    /**
     * 删除分组
     * @param gid
     */
    void deleteGroup(String gid);

    /**
     * 修改分组名
     * @param requestParam
     */
    void updateGroup(GroupUpdateReqDTO requestParam);
}
