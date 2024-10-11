package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IActivityDao;
import com.axon.mybatis.enties.ActivityDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * 本章主要是添加了二级缓存的处理
 *
 *  * 1. 二级缓存 (Global Cache)
 *  *
 *  * 	•	作用范围：二级缓存是跨 SqlSession 的，通常是 Mapper 级别（即某个 Mapper 的缓存），不同 SqlSession 间可以共享缓存数据。
 *  * 	•	存储位置：二级缓存的数据会在某个缓存提供者（如内存、磁盘、集群等）中持久化存储。
 *  * 	•	配置方式：需要在 mybatis-config.xml 文件中显式开启，并且在具体的 Mapper 文件中通过 <cache> 标签来启用。
 *  * 	•	适用场景：二级缓存适合在读多写少、数据更新频率较低的场景下使用。
 *  *
 *
 * 本章节主要添加事务缓存管理器， 二级缓存的处理
 *  1.1> TransactionalCache 实现ICache 实现对应的事物缓存
 *  1.2>添加 TransactionalCacheManager 事务缓存管理器， 对  TransactionalCache 进行管理
 *  1.3> CachingExecutor 实现  Executor 执行器的相关方法。  使用了装饰器模式实现对二级缓存的扩展
 *      @Override
 *     public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) {
 *         //TODO 这一句在判断当前的sql是否配置了二级缓存， 如果配置了，在之前的解析中已经赋值到MappedStatement  这个对象中去了。 如果开启了，那就是不为空，走二级缓存的策略。
 *         Cache cache = ms.getCache();
 *         if (cache != null) {
 *             flushCacheIfRequired(ms);
 *             if (ms.isUseCache() && resultHandler == null) {
 *                 @SuppressWarnings("unchecked")
 *                 List<E> list = (List<E>) tcm.getObject(cache, key);
 *                 if (list == null) {
 *                     TODO 这里是原来的执行器，调用原来的方法
 *                     list = delegate.<E>query(ms, parameter, rowBounds, resultHandler, key, boundSql);
 *                     // cache：缓存队列实现类，FIFO
 *                     // key：哈希值 [mappedStatementId + offset + limit + SQL + queryParams + environment]
 *                     // list：查询的数据
 *                     tcm.putObject(cache, key, list);
 *                 }
 *                 // 打印调试日志，记录二级缓存获取数据
 *                 if (logger.isDebugEnabled() && cache.getSize() > 0) {
 *                     logger.debug("二级缓存：{}", JSON.toJSONString(list));
 *                 }
 *                 return list;
 *             }
 *         }
 *         return delegate.<E>query(ms, parameter, rowBounds, resultHandler, key, boundSql);
 *     }
 *
 *1.4> 在Configuration中创建执行器时，核心方法
 *
 *      public Executor newExecutor(Transaction transaction) {
 *         Executor executor = new SimpleExecutor(this, transaction);
 *         // 配置开启缓存， 创建CachingExecutor(默认有缓存)  这里是使用了装饰器模式？  不是代理模式？
 *         //TODO 判断是否开启了二级缓存
 *         if (cacheEnabled) {
 *             executor = new CachingExecutor(executor);
 *         }
 *         return executor;
 *     }
 *
 * 1.5> 在XMLConfigBuilder中加入对  cacheEnabled的解析
 *          private void settingsElement(Element context) {
     *         if (context == null) return;
     *         List<Element> elements = context.elements();
     *         Properties props = new Properties();
     *         for (Element element : elements) {
     *             props.setProperty(element.attributeValue("name"), element.attributeValue("value"));
     *         }
 *             //TODO 解析赋值操作
     *         configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
     *         configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope")));
 *     }
 *
 * 1.6> XMLMapperBuilder中加入对二级缓存的配置解析
 *
 *        private void cacheElement(Element context) {
     *         if (context == null) return;
     *         // 基础配置信息
     *         String type = context.attributeValue("type", "PERPETUAL");
     *         Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
     *         // 缓存队列 FIFO
     *         String eviction = context.attributeValue("eviction", "FIFO");
     *         Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
     *         Long flushInterval = Long.valueOf(context.attributeValue("flushInterval"));
     *         Integer size = Integer.valueOf(context.attributeValue("size"));
     *         boolean readWrite = !Boolean.parseBoolean(context.attributeValue("readOnly", "false"));
     *         boolean blocking = !Boolean.parseBoolean(context.attributeValue("blocking", "false"));
     *
     *         // 解析额外属性信息；<property name="cacheFile" value="/tmp/xxx-cache.tmp"/>
     *         List<Element> elements = context.elements();
     *         Properties props = new Properties();
     *         for (Element element : elements) {
     *             props.setProperty(element.attributeValue("name"), element.attributeValue("value"));
     *         }
     *         //TODO 加入到Configuration缓存中去,方便在使用的时候，错那个缓存中取， 解析中已经赋值到MappedStatement 这个对象中去
     *         builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
 *     }
 *
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);



    @Test
    public void test_queryActivityById() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        // 2. 请求对象
        ActivityDO req = new ActivityDO();
        req.setActivityId(10004L);

        // 3. 第一组：SqlSession
        // 3.1 开启 Session
        SqlSession sqlSession01 = sqlSessionFactory.openSession();
        // 3.2 获取映射器对象
        IActivityDao dao01 = sqlSession01.getMapper(IActivityDao.class);
        logger.info("测试结果01：{}", JSON.toJSONString(dao01.queryActivityById(req)));
        sqlSession01.close();

        // 4. 第一组：SqlSession
        // 4.1 开启 Session
        SqlSession sqlSession02 = sqlSessionFactory.openSession();
        // 4.2 获取映射器对象
        IActivityDao dao02 = sqlSession02.getMapper(IActivityDao.class);
        logger.info("测试结果02：{}", JSON.toJSONString(dao02.queryActivityById(req)));
        sqlSession02.close();
    }

}
