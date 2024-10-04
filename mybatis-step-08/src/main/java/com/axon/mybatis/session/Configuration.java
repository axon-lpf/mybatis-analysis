package com.axon.mybatis.session;

import com.axon.mybatis.binding.MapperRegistry;
import com.axon.mybatis.datasource.druid.DruidDataSourceFactory;
import com.axon.mybatis.datasource.pooled.PooledDataSourceFactory;
import com.axon.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import com.axon.mybatis.executor.Executor;
import com.axon.mybatis.executor.SimpleExecutor;
import com.axon.mybatis.executor.resultset.DefaultResultSetHandler;
import com.axon.mybatis.executor.resultset.ResultSetHandler;
import com.axon.mybatis.executor.statement.PreparedStatementHandler;
import com.axon.mybatis.executor.statement.StatementHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.transaction.Transaction;
import com.axon.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.axon.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected Environment environment;

    /**
     * 初始化时，将对应的数据源工厂的映射放到缓存中去
     */
    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);


    }


    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    /**
     * 类型别名注册机
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    /**
     * 映射注册机
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * 隐射sql语句，存在mapper中
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();


    /**
     * 添加注册机， 通过扫描包添加
     *
     * @param packageName
     */
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    /**
     * 单个添加
     *
     * @param type
     * @param <T>
     */
    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    /**
     * 获取注册机
     *
     * @param type
     * @param sqlSession
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    /**
     * 判断是否存在
     *
     * @param type
     * @return
     */
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    /**
     * 添加sql语句的解析到缓存中
     *
     * @param ms
     */
    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    /**
     * 获取sql语句的解析
     *
     * @param id
     * @return
     */
    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }


    /**
     * 创建结果集处理器
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    /**
     * 生产执行器
     */
    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    /**
     * 创建语句处理器
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler, boundSql);
    }

}
