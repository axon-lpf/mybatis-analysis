package com.axon.mybatis.mapping;

public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);
}
