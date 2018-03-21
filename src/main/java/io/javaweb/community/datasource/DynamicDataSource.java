package io.javaweb.community.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by KevinBlandy on 2017/10/30 14:05
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);

    // 轮询计数
    private AtomicInteger counter = new AtomicInteger(-1);

    // 记录读库的key(配置在ioc中)
    private List<Object> slaveDataSources = new ArrayList<>(0);

    @Override
    protected Object determineCurrentLookupKey() {
        Object key = null;
        if (DynamicDataSourceHolder.isMaster() || this.slaveDataSources.isEmpty()) {
            key = DynamicDataSourceHolder.MASTER;
        } else {
            key = this.getSlaveKey();
        }
        LOGGER.info("动态数据源 dataSourceKey = {}", key);
        return key;
    }

    /**
     * 属性注入后,反射读取配置的数据源信息
     */
    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Field field = ReflectionUtils.findField(AbstractRoutingDataSource.class, "resolvedDataSources");
        field.setAccessible(true); // 暴力访问
        try {
            Map<Object, DataSource> resolvedDataSources = (Map<Object, DataSource>) field.get(this);
            for (Map.Entry<Object, DataSource> entry : resolvedDataSources.entrySet()) {
                if (DynamicDataSourceHolder.MASTER.equals(entry.getKey())) {
                    continue;
                }
                //从(读)库key
                slaveDataSources.add(entry.getKey());
            }
        } catch (Exception e) {
            LOGGER.error("afterPropertiesSet error! ", e);
        }
    }

    /**
     * 轮询
     * @return
     */
    public Object getSlaveKey() {
        Integer index = counter.incrementAndGet() % this.slaveDataSources.size();
        if (counter.get() > 9999) {
            counter.set(-1);
        }
        return slaveDataSources.get(index);
    }
}
