package com.axon.mybatis;

import com.axon.mybatis.executor.statement.StatementHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.plugin.Interceptor;
import com.axon.mybatis.plugin.Intercepts;
import com.axon.mybatis.plugin.Invocation;
import com.axon.mybatis.plugin.Signature;

import java.sql.Connection;
import java.util.Properties;

/**
 * 自定义插件拦截器
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class TestPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        System.out.println("拦截sql" + sql);

        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("参数输出" + properties.getProperty("test00"));
    }
}
