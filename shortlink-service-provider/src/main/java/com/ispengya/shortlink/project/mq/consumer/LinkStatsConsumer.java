package com.ispengya.shortlink.project.mq.consumer;

import com.ispengya.shortlink.common.constant.MQConstant;
import com.ispengya.shortlink.common.util.JsonUtils;
import com.ispengya.shortlink.project.domain.LinkStatsMQDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author ispengya
 * @date 2023/12/10 17:15
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = MQConstant.SHORT_LINK_STATS_GROUP, topic = MQConstant.SHORT_LINK_STATS_TOPIC
        , messageModel = MessageModel.CLUSTERING)
public class LinkStatsConsumer implements RocketMQListener<LinkStatsMQDTO> {

    @Override
    public void onMessage(LinkStatsMQDTO linkStatsMQDTO) {
        log.info("短链接MQ统计数据：{}", JsonUtils.toStr(linkStatsMQDTO));
    }
}
