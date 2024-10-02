package com.axon.mybatis.builder;

import com.axon.mybatis.session.Configuration;


/**
 * 构建器
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
