package com.axon.mybatis.mapping;

import com.axon.mybatis.mapping.BoundSql;

public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);
}
