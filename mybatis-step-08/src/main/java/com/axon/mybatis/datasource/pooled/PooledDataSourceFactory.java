package com.axon.mybatis.datasource.pooled;

import com.axon.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

public class PooledDataSourceFactory extends UnpooledDataSourceFactory {


    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
