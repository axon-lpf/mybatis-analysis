package com.axon.mybatis.builder;

import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.type.TypeAliasRegistry;


/**
 * 构建器
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;

    protected final TypeAliasRegistry typeAliasRegistry;


    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
