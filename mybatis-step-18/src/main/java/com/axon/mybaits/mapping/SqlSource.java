package com.axon.mybaits.mapping;

import com.axon.mybatis.mapping.BoundSql;

public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);
}
