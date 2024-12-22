package com.ispengya.shortlink.project.dao;

import com.ispengya.shortlink.project.domain.dao.LinkStatsTodayDTO;
import com.ispengya.shortlink.project.mapper.LinkStatsTodayMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description: 短链接统计dao
 * @author: hanzhipeng
 * @create: 2024-12-22 11:59
 **/
@Repository
@RequiredArgsConstructor
public class ShortLinkStatsDao {

    private final LinkStatsTodayMapper linkStatsTodayMapper;


    /**
     * 查询today统计数据
     */
    public List<LinkStatsTodayDTO> selectListByShortUrls(List<String> shortUrls) {
        return linkStatsTodayMapper.selectListByShortUrl(shortUrls);
    }


}

