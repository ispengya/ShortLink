package com.ispengya.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ispengya.shortlink.project.domain.LinkAccessStatsDO;
import com.ispengya.shortlink.project.dto.request.ShortLinkGroupStatsParam;
import com.ispengya.shortlink.project.dto.request.ShortLinkStatsParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 短链接基础访问监控持久层
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    /**
     * 记录基础访问监控数据
     */
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);

    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param") ShortLinkStatsParam requestParam);

    /**
     * 根据分组获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    tlas.date, " +
            "    SUM(tlas.pv) AS pv, " +
            "    SUM(tlas.uv) AS uv, " +
            "    SUM(tlas.uip) AS uip " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = '0' " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tl.gid, tlas.date;")
    List<LinkAccessStatsDO> listStatsByGroup(@Param("param") ShortLinkGroupStatsParam requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param") ShortLinkStatsParam requestParam);

    /**
     * 根据分组获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    tlas.hour, " +
            "    SUM(tlas.pv) AS pv " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = '0' " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tl.gid, tlas.hour;")
    List<LinkAccessStatsDO> listHourStatsByGroup(@Param("param") ShortLinkGroupStatsParam requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsParam requestParam);

    /**
     * 根据分组获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    tlas.weekday, " +
            "    SUM(tlas.pv) AS pv " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = '0' " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tl.gid, tlas.weekday;")
    List<LinkAccessStatsDO> listWeekdayStatsByGroup(@Param("param") ShortLinkGroupStatsParam requestParam);
}
