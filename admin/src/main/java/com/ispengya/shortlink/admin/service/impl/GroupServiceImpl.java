package com.ispengya.shortlink.admin.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.ispengya.shortlink.admin.common.user.UserContext;
import com.ispengya.shortlink.admin.dao.GroupDao;
import com.ispengya.shortlink.admin.domain.dto.req.GroupAddReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.GroupSortReqDTO;
import com.ispengya.shortlink.admin.domain.dto.req.GroupUpdateReqDTO;
import com.ispengya.shortlink.admin.domain.dto.resp.GroupListRespDTO;
import com.ispengya.shortlink.admin.domain.entity.Group;
import com.ispengya.shortlink.admin.service.GroupService;
import com.ispengya.shortlink.admin.service.converter.BeanConverter;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.common.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ispengya
 * @date 2023/11/21 15:50
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    public static final int GID_LENGTH = 6;
    public static final int SORT_ORDER = 0;
    private final GroupDao groupDao;


    @Override
    public void saveGroup(GroupAddReqDTO groupAddReqDTO) {
        String username = UserContext.getUsername();
        //判断是否已经存在
        String gid;
        do {
            gid = RandomUtil.randomString(GID_LENGTH);
        } while (!isExit(gid, username, groupAddReqDTO.getName()));
        Group insert = Group.builder()
                .gid(gid)
                .name(groupAddReqDTO.getName())
                .sortOrder(SORT_ORDER)
                .username(username)
                .build();
        groupDao.save(insert);
    }

    @Override
    public List<GroupListRespDTO> searchGroupList() {
        String username = UserContext.getUsername();
        List<Group> groupList = groupDao.listGroupByUserName(username);
        return groupList.stream()
                //TODO 查询短链接数目
                .map(group -> BeanConverter.CONVERTER.converterGroup1(group)).collect(Collectors.toList());
    }

    @Override
    public void sort(List<GroupSortReqDTO> reqDTOList) {
        reqDTOList.forEach(groupSortReqDTO -> {
            Group update = Group.builder()
                    .sortOrder(groupSortReqDTO.getSortOrder())
                    .gid(groupSortReqDTO.getGid())
                    .username(UserContext.getUsername())
                    .build();
            groupDao.updateGroup(update);
        });
    }

    @Override
    public void deleteGroup(String gid) {
        String username = UserContext.getUsername();
        Group oldGroup = groupDao.getGroupByGIdAndUserName(username, gid);
        oldGroup.setDelFlag(YesOrNoEnum.NO.getCode());
        groupDao.updateGroup(oldGroup);
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {
        String username = UserContext.getUsername();
        Group oldGroup = groupDao.getGroupByGIdAndUserName(username, requestParam.getGid());
        AssertUtil.notNull(oldGroup, "该分组不存在");
        oldGroup.setName(requestParam.getName());
        groupDao.updateGroup(oldGroup);
    }

    private boolean isExit(String gid, String username, String name) {
        Group group = groupDao.getGroupByGIdAndUserName(username, gid);
        return Objects.isNull(group);
    }
}
