package com.ispengya.shortlink.admin.common.listener;

import cn.hutool.core.util.RandomUtil;
import com.ispengya.shortlink.admin.common.event.UserRegisterEvent;
import com.ispengya.shortlink.admin.dao.GroupDao;
import com.ispengya.shortlink.admin.domain.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

import static com.ispengya.shortlink.admin.service.impl.GroupServiceImpl.GID_LENGTH;
import static com.ispengya.shortlink.admin.service.impl.GroupServiceImpl.SORT_ORDER;

/**
 * @author ispengya
 * @date 2023/11/26 15:39
 */
@Component
public class UserRegisterListener {
    @Autowired
    private GroupDao groupDao;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT,value = UserRegisterEvent.class)
    public void createDefaultGroup(UserRegisterEvent event){
        //判断是否已经存在
        String gid;
        do {
            gid = RandomUtil.randomString(GID_LENGTH);
        } while (!isExit(gid, event.getUsername()));
        Group insert = Group.builder()
                .gid(gid)
                .name("默认分组")
                .sortOrder(SORT_ORDER)
                .username(event.getUsername())
                .build();
        groupDao.save(insert);
    }

    private boolean isExit(String gid, String username) {
        Group group = groupDao.getGroupByGIdAndUserName(username, gid);
        return Objects.isNull(group);
    }
}
