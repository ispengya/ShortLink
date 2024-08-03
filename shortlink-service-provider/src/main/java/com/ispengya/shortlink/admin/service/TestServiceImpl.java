package com.ispengya.shortlink.admin.service;

import com.ispengya.shortlink.project.dao.core.ShortLinkDao;
import com.ispengya.shortlink.project.domain.ShortLinkDO;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsParam;
import com.ispengya.shortlink.project.mapper.LinkAccessStatsMapper;
import com.ispengya.travel.frameworks.starter.cache.core.distributed.DistributedCache;
import com.ispengya.travel.frameworks.starter.cache.core.multistage.MultiStageCache;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 韩志鹏
 * @Description
 * @Date 创建于 2024/6/7 16:42
 */
@DubboService
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private MultiStageCache multiStageCache;
    @Autowired
    private DistributedCache distributedCache;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private LinkAccessStatsMapper linkAccessStatsMapper;
    @Autowired
    private ShortLinkDao shortLinkDao;

    @Override
    public void hello() {
        ShortLinkStatsParam shortLinkStatsParam = new ShortLinkStatsParam();
        shortLinkStatsParam.setUsername("133dsfsd!");
        shortLinkStatsParam.setGid("default");
        shortLinkStatsParam.setFullShortUrl("12");
        System.out.println(linkAccessStatsMapper.listStatsByShortLink(shortLinkStatsParam));
//        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
//                .shortUri("12")
//                .domain("12")
//                .clickNum(1)
//                .createdType(1)
//                .fullShortUrl("12")
//                .originUrl("12")
//                .describe("111")
//                .username("133dsfsd!")
//                .build();
//        shortLinkDao.save(shortLinkDO);
    }
}
