package com.ispengya.shortlink.common.algorithm.sharding;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Preconditions;
import com.ispengya.shortlink.common.exception.ServiceException;
import com.ispengya.shortlink.project.util.HashUtil;
import lombok.Getter;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;

/**
 * @author ispengya
 * @date 2023/11/25 15:17
 */
public class LinkCommonTableComplexAlgorithm implements ComplexKeysShardingAlgorithm {

    @Getter
    private Properties props;

    private int shardingCount;
    private int tableShardingCount;

    private static final String SHARDING_COUNT_KEY = "sharding-count";
    private static final String DATABASE_COUNT_KEY = "database-count";
    private static final String TABLE_SHARDING_COUNT_KEY = "table-sharding-count";

    @Override
    public Collection<String> doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {
        Map<String, Collection<Comparable<?>>> columnNameAndShardingValuesMap =
                shardingValue.getColumnNameAndShardingValuesMap();
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        //获取username的分片值
        Collection<Comparable<?>> usernameCollection = columnNameAndShardingValuesMap.get("username");
        if (CollUtil.isNotEmpty(usernameCollection)) {
            Comparable<?> comparable = usernameCollection.stream().findFirst().get();
            String actualUserName = comparable.toString();
            //计算数据库后缀
            String databaseSuffix =
                    String.valueOf(hashShardingValue(actualUserName) % shardingCount / tableShardingCount);
            //计算表后缀
//            int tableIndex = HashUtil.hashAndMapToRange(actualUserName, shardingCount,databaseSuffix);
            result.add(shardingValue.getLogicTableName() + "_" + databaseSuffix);
        } else {
            throw new ServiceException("分片键username不能为空");
        }
        return result;
    }

    @Override
    public void init(Properties props) {
        this.props = props;
        shardingCount = getShardingCount(props);
        tableShardingCount=getTableShardingCount(props);
    }

    private int getShardingCount(final Properties props) {
        Preconditions.checkArgument(props.containsKey(SHARDING_COUNT_KEY), "sharding-count cannot be null.");
        return Integer.parseInt(props.getProperty(SHARDING_COUNT_KEY));
    }

    private int getTableShardingCount(final Properties props) {
        Preconditions.checkArgument(props.containsKey(DATABASE_COUNT_KEY), "table-sharding-count cannot be null.");
        return Integer.parseInt(props.getProperty(TABLE_SHARDING_COUNT_KEY));
    }

    private long hashShardingValue(final Comparable<?> shardingValue) {
        return Math.abs((long) shardingValue.hashCode());
    }

}
