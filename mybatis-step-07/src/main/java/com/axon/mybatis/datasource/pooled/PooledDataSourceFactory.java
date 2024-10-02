package com.axon.mybatis.datasource.pooled;

import com.axon.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

public class PooledDataSourceFactory extends UnpooledDataSourceFactory {


    @Override
    public DataSource getDataSource() {
        PooledDataSource unpooledDataSource = new PooledDataSource();
        unpooledDataSource.setDriver(properties.getProperty("driver"));
        unpooledDataSource.setUrl(properties.getProperty("url"));
        unpooledDataSource.setUserName(properties.getProperty("userName"));
        unpooledDataSource.setPassword(properties.getProperty("password"));
        return unpooledDataSource;
    }
}
