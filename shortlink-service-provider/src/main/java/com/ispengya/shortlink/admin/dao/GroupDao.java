package com.ispengya.shortlink.admin.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ispengya.shortlink.admin.domain.Group;
import com.ispengya.shortlink.admin.mapper.GroupMapper;
import com.ispengya.shortlink.common.enums.YesOrNoEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ispengya
 * @date 2023/11/21 15:51
 */
@Repository
public class GroupDao extends ServiceImpl<GroupMapper, Group> {
    public Group getGroupByGIdAndUserName(String username, String gid) {
        return lambdaQuery()
                .eq(Group::getGid,gid)
                .eq(Group::getUsername,username)
                //这里即使删除了也要查询
//                .eq(Group::getDelFlag, YesOrNoEnum.YES)
                .one();
    }

    public List<Group> listGroupByUserName(String username) {
        return lambdaQuery()
                .eq(Group::getUsername,username)
                .eq(Group::getDelFlag, YesOrNoEnum.YES.getCode())
                .orderByDesc(Group::getSortOrder,Group::getUpdateTime)
                .list();
    }

    public void updateGroup(Group update) {
        lambdaUpdate()
                .eq(Group::getGid,update.getGid())
                .eq(Group::getUsername,update.getUsername())
                .eq(Group::getDelFlag,YesOrNoEnum.YES.getCode())
                .update(update);
    }

}
