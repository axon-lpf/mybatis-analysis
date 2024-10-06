package com.axon.mybatis.plugin;

import com.axon.mybatis.plugin.Invocation;
import com.axon.mybatis.plugin.Plugin;

import java.util.Properties;

/**
 * 添加拦截器接口
 */
public interface Interceptor {

    // 拦截，使用方实现
    Object intercept(Invocation invocation) throws Throwable;

    // 代理
    default Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    // 设置属性
    default void setProperties(Properties properties) {

    }
}
