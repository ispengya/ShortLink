package com.ispengya.shortlink.admin.service;

import com.ispengya.shortlink.admin.dto.request.GroupAddParam;
import com.ispengya.shortlink.admin.dto.request.GroupSortParam;
import com.ispengya.shortlink.admin.dto.request.GroupUpdateParam;
import com.ispengya.shortlink.admin.dto.response.GroupListRespDTO;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/21 15:50
 */
public interface GroupDubboService {
    /**
     * 添加短链接分组
     * @param groupAddParam
     */
    void saveGroup(GroupAddParam groupAddParam);

    /**
     * 查询短链接分组
     * @return
     */
    List<GroupListRespDTO> searchGroupList();

    /**
     * 排序分组
     * @param paramList
     */
    void sort(List<GroupSortParam> paramList);

    /**
     * 删除分组
     * @param gid
     */
    void deleteGroup(String gid);

    /**
     * 修改分组名
     * @param requestParam
     */
    void updateGroup(GroupUpdateParam requestParam);
}
