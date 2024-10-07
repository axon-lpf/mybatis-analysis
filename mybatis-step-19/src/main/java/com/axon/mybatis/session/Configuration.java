package com.axon.mybatis.session;

import com.axon.mybatis.binding.MapperRegistry;
import com.axon.mybatis.cache.Cache;
import com.axon.mybatis.cache.decorators.FifoCache;
import com.axon.mybatis.cache.impl.PerpetualCache;
import com.axon.mybatis.datasource.druid.DruidDataSourceFactory;
import com.axon.mybatis.datasource.pooled.PooledDataSourceFactory;
import com.axon.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import com.axon.mybatis.executor.CachingExecutor;
import com.axon.mybatis.executor.Executor;
import com.axon.mybatis.executor.SimpleExecutor;
import com.axon.mybatis.executor.keygen.KeyGenerator;
import com.axon.mybatis.executor.parameter.ParameterHandler;
import com.axon.mybatis.executor.resultset.DefaultResultSetHandler;
import com.axon.mybatis.executor.resultset.ResultSetHandler;
import com.axon.mybatis.executor.statement.PreparedStatementHandler;
import com.axon.mybatis.executor.statement.StatementHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.mapping.ResultMap;
import com.axon.mybatis.plugin.Interceptor;
import com.axon.mybatis.plugin.InterceptorChain;
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

    /**
     * 主键生成的配置
     */
    private Boolean useGeneratedKeys = false;


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
     * 这里映射结果，存在map中
     */
    protected final Map<String, ResultMap> resultMaps = new HashMap<>();


    /**
     * 主键生成策略
     */
    protected final Map<String, KeyGenerator> keyGenerators = new HashMap<>();


    /**
     * 插件的拦截器链
     */
    protected final InterceptorChain interceptorChain = new InterceptorChain();


    /**
     * 缓存机制，默认不配置的情况是 SESSION
     */
    protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;


    // 默认启用缓存，cacheEnabled = true/false
    protected boolean cacheEnabled = true;

    /**
     * 缓存,存在Map里
     */
    protected final Map<String, Cache> caches = new HashMap<>();

    /**
     * 初始化时，将对应的数据源工厂的映射放到缓存中去
     */
    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);

        typeAliasRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
        typeAliasRegistry.registerAlias("FIFO", FifoCache.class);


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
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, resultHandler, rowBounds, boundSql);
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
        Executor executor = new SimpleExecutor(this, transaction);
        // 配置开启缓存， 创建CachingExecutor(默认有缓存)  这里是使用了装饰器模式？  不是代理模式？
        if (cacheEnabled) {
            executor = new CachingExecutor(executor);
        }
        return executor;
    }

    /**
     * 创建语句处理器
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler preparedStatementHandler = new PreparedStatementHandler(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
        // 嵌入插件，代理对象
        preparedStatementHandler = (StatementHandler) interceptorChain.pluginAll(preparedStatementHandler);
        // 这里创建出来的是一个代理对象
        return preparedStatementHandler;

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


    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }


    /**
     * 获取隐射结果
     *
     * @param id
     * @return
     */
    public ResultMap getResultMap(String id) {
        return resultMaps.get(id);
    }


    /**
     * 添加隐射结果
     *
     * @param resultMap
     */
    public void addResultMap(ResultMap resultMap) {
        resultMaps.put(resultMap.getId(), resultMap);
    }


    /**
     * 主键生成配置
     *
     * @return
     */
    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }


    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public boolean hasKeyGenerator(String id) {
        return keyGenerators.containsKey(id);
    }

    public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
        keyGenerators.put(id, keyGenerator);
    }

    public KeyGenerator getKeyGenerator(String id) {
        return keyGenerators.get(id);
    }


    public void addInterceptor(Interceptor interceptorInstance) {
        interceptorChain.addInterceptor(interceptorInstance);
    }

    /**
     * 获取缓存策略
     *
     * @return
     */
    public LocalCacheScope getLocalCacheScope() {
        return localCacheScope;
    }

    /**
     * 设置缓存策略
     *
     * @param localCacheScope
     */
    public void setLocalCacheScope(LocalCacheScope localCacheScope) {
        this.localCacheScope = localCacheScope;
    }

    /**
     * 设置是否启用二级缓存
     *
     * @param cacheEnabled
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * 获取启用二级缓存的状态
     *
     * @return
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void addCache(Cache cache) {
        caches.put(cache.getId(), cache);
    }

    public Cache getCache(String id) {
        return caches.get(id);
    }
}
