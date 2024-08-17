package com.ispengya.shortlink.admin.service;

import cn.hutool.core.util.RandomUtil;
import com.ispengya.shortlink.admin.dao.GroupDao;
import com.ispengya.shortlink.admin.domain.Group;
import com.ispengya.shortlink.admin.dto.request.GroupAddParam;
import com.ispengya.shortlink.admin.dto.request.GroupSortParam;
import com.ispengya.shortlink.admin.dto.request.GroupUpdateParam;
import com.ispengya.shortlink.admin.dto.response.GroupListRespDTO;
import com.ispengya.shortlink.common.biz.UserContext;
import com.ispengya.shortlink.common.converter.BeanConverter;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import com.ispengya.shortlink.common.util.AssertUtil;
import com.ispengya.shortlink.project.dao.core.ShortLinkDao;
import com.ispengya.shortlink.project.dto.response.ShortLinkGroupCountQueryRespDTO;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ispengya
 * @date 2023/11/21 15:50
 */
@Service
@DubboService
@RequiredArgsConstructor
public class GroupDubboServiceImpl implements GroupDubboService {
    public static final int GID_LENGTH = 6;
    public static final int SORT_ORDER = 0;
    private final GroupDao groupDao;
    private final ShortLinkDao shortLinkDao;


    @Override
    public void saveGroup(GroupAddParam groupAddParam) {
        String username = UserContext.getUsername();
        //判断是否已经存在
        String gid;
        do {
            gid = RandomUtil.randomString(GID_LENGTH);
        } while (!isExit(gid, username, groupAddParam.getName()));
        Group insert = Group.builder()
                .gid(gid)
                .name(groupAddParam.getName())
                .sortOrder(SORT_ORDER)
                .username(username)
                .build();
        groupDao.save(insert);
    }

    @Override
    public List<GroupListRespDTO> searchGroupList() {
        String username = UserContext.getUsername();
        List<Group> groupList = groupDao.listGroupByUserName(username);
        List<String> groupIds = groupList.stream().map(Group::getGid).collect(Collectors.toList());
        List<ShortLinkGroupCountQueryRespDTO> groupLinkCount = shortLinkDao.getGroupLinkCount(groupIds, username);
        Map<String, Integer> gidCount =
                groupLinkCount.stream().collect(Collectors.toMap(ShortLinkGroupCountQueryRespDTO::getGid,
                        ShortLinkGroupCountQueryRespDTO::getShortLinkCount));
        List<GroupListRespDTO> collect = groupList.stream()
                //TODO 查询短链接数目
                .map(group -> {
                    GroupListRespDTO groupListRespDTO = BeanConverter.CONVERTER.converterGroup1(group);
                    groupListRespDTO.setShortLinkCount(gidCount.get(group.getGid()));
                    return groupListRespDTO;
                }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void sort(List<GroupSortParam> reqDTOList) {
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
    public void updateGroup(GroupUpdateParam requestParam) {
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
