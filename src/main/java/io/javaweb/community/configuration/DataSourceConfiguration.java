package io.javaweb.community.configuration;

import com.alibaba.druid.pool.DruidDataSource;

import io.javaweb.community.datasource.DynamicDataSource;
import io.javaweb.community.datasource.DynamicDataSourceHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KevinBlandy on 2017/10/30 14:29
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceConfig.class)
public class DataSourceConfiguration {

    @Autowired
    private DynamicDataSourceConfig dynamicDataSourceConfig;

    /**
     * 数据源
     * @return
     * @throws SQLException
     */
    @Bean(name = "dataSource")
    //@ConfigurationProperties(value = "datasource")
    public DataSource dataSource() throws SQLException {

        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        Map<Object,Object> dataSources = new HashMap<>();

        List<String> urls = dynamicDataSourceConfig.getUrl();
        List<String> names = dynamicDataSourceConfig.getName();
        List<String> passes = dynamicDataSourceConfig.getPass();
        List<Boolean> flags = dynamicDataSourceConfig.getMaster();

        int dataSourceCount = urls.size();

        DruidDataSource druidDataSource = null;
        for (int x = 0;x < dataSourceCount ; x++){

            druidDataSource = new DruidDataSource();

            //基本连接属性
            druidDataSource.setUrl(urls.get(x));
            druidDataSource.setUsername(names.get(x));
            druidDataSource.setPassword(passes.get(x));

            //公共属性
            druidDataSource.setDbType(dynamicDataSourceConfig.getType());
            druidDataSource.setDriverClassName(dynamicDataSourceConfig.getDriverClassName());

            druidDataSource.setInitialSize(dynamicDataSourceConfig.getInitialSize());
            druidDataSource.setMinIdle(dynamicDataSourceConfig.getMinIdle());
            druidDataSource.setMaxActive(dynamicDataSourceConfig.getMaxActive());
            druidDataSource.setMaxWait(dynamicDataSourceConfig.getMaxWait());
            druidDataSource.setTimeBetweenEvictionRunsMillis(dynamicDataSourceConfig.getTimeBetweenEvictionRunsMillis());
            druidDataSource.setMinEvictableIdleTimeMillis(dynamicDataSourceConfig.getMinEvictableIdleTimeMillis());
            druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(dynamicDataSourceConfig.getMaxPoolPreparedStatementPerConnectionSize());

            druidDataSource.setValidationQuery(dynamicDataSourceConfig.getValidationQuery());
            druidDataSource.setTestWhileIdle(dynamicDataSourceConfig.getTestWhileIdle());
            druidDataSource.setTestOnBorrow(dynamicDataSourceConfig.getTestOnBorrow());
            druidDataSource.setTestOnReturn(dynamicDataSourceConfig.getTestOnReturn());
            druidDataSource.setPoolPreparedStatements(dynamicDataSourceConfig.getPoolPreparedStatements());
            druidDataSource.setFilters(dynamicDataSourceConfig.getFilters());

            druidDataSource.setConnectProperties(dynamicDataSourceConfig.getConnectionProperties());

            //TODO DB集群情况下,每个节点性能不一样,可能会针对于单个节点配置连接池属性

            if(flags.get(x)){
                //master库
                dataSources.put(DynamicDataSourceHolder.MASTER,druidDataSource);
                dynamicDataSource.setDefaultTargetDataSource(druidDataSource);
            }else{
                //slave库
                dataSources.put("slave" + x,druidDataSource);
            }

            //手动初始化
            druidDataSource.init();
        }

        dynamicDataSource.setTargetDataSources(dataSources);

        return dynamicDataSource;
    }
}
