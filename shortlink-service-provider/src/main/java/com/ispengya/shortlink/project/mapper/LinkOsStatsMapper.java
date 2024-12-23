package com.ispengya.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ispengya.shortlink.project.domain.LinkOsStatsDO;
import com.ispengya.shortlink.project.dto.request.ShortLinkGroupStatsParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * 操作系统统计访问持久层
 */
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    /**
     * 记录os
     */
    @Insert("INSERT INTO " +
            "t_link_os_stats (full_short_url, date, cnt, os, create_time, update_time, del_flag) " +
            "VALUES( #{linkOsStats.fullShortUrl}, #{linkOsStats.date}, #{linkOsStats.cnt}, #{linkOsStats.os}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkOsStats.cnt};")
    void shortLinkOsState(@Param("linkOsStats") LinkOsStatsDO linkOsStatsDO);

    /**
     * 根据短链接获取指定日期内操作系统监控数据
     */
    List<HashMap<String, Object>> listOsStatsByShortLink(@Param("param") ShortLinkStatsParam requestParam);

    /**
     * 根据分组获取指定日期内操作系统监控数据
     */
    @Select("SELECT " +
            "    tlos.os, " +
            "    SUM(tlos.cnt) AS count " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_os_stats tlos ON tl.full_short_url = tlos.full_short_url " +
            "WHERE " +
            "    tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = '0' " +
            "    AND tlos.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tl.gid, tlos.os;")
    List<HashMap<String, Object>> listOsStatsByGroup(@Param("param") ShortLinkGroupStatsParam requestParam);
}
