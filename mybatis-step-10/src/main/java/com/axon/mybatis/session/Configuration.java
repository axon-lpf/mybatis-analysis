package com.axon.mybatis.session;

import com.axon.mybatis.binding.MapperRegistry;
import com.axon.mybatis.datasource.druid.DruidDataSourceFactory;
import com.axon.mybatis.datasource.pooled.PooledDataSourceFactory;
import com.axon.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import com.axon.mybatis.executor.Executor;
import com.axon.mybatis.executor.SimpleExecutor;
import com.axon.mybatis.executor.parameter.ParameterHandler;
import com.axon.mybatis.executor.resultset.DefaultResultSetHandler;
import com.axon.mybatis.executor.resultset.ResultSetHandler;
import com.axon.mybatis.executor.statement.PreparedStatementHandler;
import com.axon.mybatis.executor.statement.StatementHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.reflection.MetaObject;
import com.axon.mybatis.reflection.factory.DefaultObjectFactory;
import com.axon.mybatis.reflection.factory.ObjectFactory;
import com.axon.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import com.axon.mybatis.reflection.wrapper.ObjectWrapperFactory;
import com.axon.mybatis.scripting.LanguageDriver;
import com.axon.mybatis.scripting.LanguageDriverRegistry;
import com.axon.mybatis.scripting.xmltags.XMLLanguageDriver;
import com.axon.mybatis.transaction.Transaction;
import com.axon.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.axon.mybatis.type.TypeAliasRegistry;
import com.axon.mybatis.type.TypeHandler;
import com.axon.mybatis.type.TypeHandlerRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Configuration {

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected Environment environment;


    // 对象工厂和对象包装器工厂
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    protected final Set<String> loadedResources = new HashSet<>();

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    protected String databaseId;


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
     * xml解析注册机
     */
    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();


    /**
     * 类型处理器注册机
     */
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();


    /**
     * 初始化时，将对应的数据源工厂的映射放到缓存中去
     */
    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);

        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);

    }


    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }


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
     * 参数处理器
     *
     * @param mappedStatement
     * @param parameterObject
     * @param boundSql
     * @return
     */
    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        // 创建参数处理器
        ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
        // 插件的一些参数，也是在这里处理，暂时不添加这部分内容 interceptorChain.pluginAll(parameterHandler);
        return parameterHandler;
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


    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public LanguageDriverRegistry getLanguageRegistry() {
        return languageRegistry;
    }


    // 创建元对象
    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory);
    }

    /**
     * 获取类型注册机
     *
     * @return
     */
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }


    /**
     * 获取对应的脚本语言驱动
     *
     * @return
     */
    public LanguageDriver getDefaultScriptingLanguageInstance() {
        return languageRegistry.getDefaultDriver();
    }


}
