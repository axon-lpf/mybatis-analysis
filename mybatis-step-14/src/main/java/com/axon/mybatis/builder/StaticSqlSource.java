package com.axon.mybatis.builder;

import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.ParameterMapping;
import com.axon.mybatis.mapping.SqlSource;
import com.axon.mybatis.session.Configuration;

import java.util.List;


/**
 *  静态sql的处理
 *
 */
public class StaticSqlSource implements SqlSource {


    private String sql;
    private List<ParameterMapping> parameterMappings;
    private Configuration configuration;

    public StaticSqlSource(Configuration configuration, String sql) {
        this(configuration, sql, null);
    }

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }
}
