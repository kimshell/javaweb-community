package io.javaweb.community.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by KevinBlandy on 2017/10/30 14:49
 */
@ConfigurationProperties(prefix = "datasource")
public class DynamicDataSourceConfig {


    //dynamic datasource

    private List<String> url = new ArrayList<>();

    private List<String> name = new ArrayList<>();

    private List<String> pass = new ArrayList<>();

    private List<Boolean> master = new ArrayList<>();

    //datasource common config

    private String type;

    private String driverClassName;

    private Integer initialSize;

    private Integer minIdle;

    private Integer maxActive;

    private Integer maxWait;

    private Integer timeBetweenEvictionRunsMillis;

    private Integer minEvictableIdleTimeMillis;

    private String validationQuery;

    private Boolean testWhileIdle;

    private Boolean testOnBorrow;

    private Boolean testOnReturn;

    private Boolean poolPreparedStatements;

    private Integer maxPoolPreparedStatementPerConnectionSize;

    private String filters;

    private Properties connectionProperties;

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getPass() {
        return pass;
    }

    public void setPass(List<String> pass) {
        this.pass = pass;
    }

    public List<Boolean> getMaster() {
        return master;
    }

    public void setMaster(List<Boolean> master) {
        this.master = master;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public Integer getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public Boolean getPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(Integer maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    @Override
    public String toString() {
        return "DynamicDataSourceConfig{" +
                "url=" + url +
                ", name=" + name +
                ", pass=" + pass +
                ", master=" + master +
                ", type='" + type + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", initialSize='" + initialSize + '\'' +
                ", minIdle='" + minIdle + '\'' +
                ", maxActive='" + maxActive + '\'' +
                ", maxWait='" + maxWait + '\'' +
                ", timeBetweenEvictionRunsMillis='" + timeBetweenEvictionRunsMillis + '\'' +
                ", minEvictableIdleTimeMillis='" + minEvictableIdleTimeMillis + '\'' +
                ", validationQuery='" + validationQuery + '\'' +
                ", testWhileIdle='" + testWhileIdle + '\'' +
                ", testOnBorrow='" + testOnBorrow + '\'' +
                ", testOnReturn='" + testOnReturn + '\'' +
                ", poolPreparedStatements='" + poolPreparedStatements + '\'' +
                ", maxPoolPreparedStatementPerConnectionSize='" + maxPoolPreparedStatementPerConnectionSize + '\'' +
                ", filters='" + filters + '\'' +
                ", connectionProperties='" + connectionProperties + '\'' +
                '}';
    }
}
