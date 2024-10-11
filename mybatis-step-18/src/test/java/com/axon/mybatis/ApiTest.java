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

/**
 *
 * 在 MyBatis 中，缓存机制是为了提高查询性能，通过减少重复的数据库查询操作。MyBatis 提供了两级缓存机制，分别是 一级缓存 和 二级缓存。你提到的 SESSION 和 STATEMENT 是针对一级缓存的生效范围的配置。
 *
 * 1. 一级缓存 (Local Cache)
 *
 * 一级缓存是 MyBatis 的默认缓存机制，作用范围为 SQL 会话（Session），即一个 SqlSession 对象内部使用的缓存，通常称为 PerpetualCache，存储在 SqlSession 中。
 *
 * 一级缓存的两个常用生效范围是 SESSION 和 STATEMENT，控制的是一级缓存的存储周期：
 *
 * 1.1 SESSION 级别缓存
 *
 * 	•	作用范围：当前 SqlSession 生命周期内共享缓存。
 * 	•	生效范围：在同一个 SqlSession 里，多次执行同一个 SQL 语句时，如果 SQL 语句相同，查询的参数相同，MyBatis 会直接从缓存中取数据，而不是再次查询数据库。
 * 	•	特点：在 SqlSession 生命周期结束（如执行 commit 或 close 方法）时，一级缓存将被清除。
 * 	•	适用场景：适用于同一个会话（事务）中多次重复查询的情况，例如同一个事务中反复查询同样的数据，可以提升性能。
 *
 * 1.2 STATEMENT 级别缓存
 *
 * 	•	作用范围：仅限于当前 SQL 语句的执行过程，SQL 语句执行完之后，缓存立即被清空。
 * 	•	生效范围：仅在当前 SQL 语句执行期间有效，MyBatis 会在执行完 SQL 后立即清除缓存。
 * 	•	特点：每次执行 SQL 语句后缓存都会被清除，即便是在同一个 SqlSession 中，下一次执行相同 SQL 也需要重新查询数据库。
 * 	•	适用场景：适用于需要确保每次查询都是最新结果的情况，通常对数据变动较大的操作会使用 STATEMENT 级别缓存。
 *
 * 一级缓存的局限：
 *
 * 	•	一级缓存是基于 SqlSession 的，也就是说缓存仅在当前 SqlSession 有效，如果多个会话或者在不同的 SqlSession 中，是无法共享一级缓存的。
 * 	•	只适用于读取相同的查询参数以及 SQL 语句，不适用于复杂查询或者需要最新数据的场景。
 *
 * 2. 二级缓存 (Global Cache)
 *
 * 	•	作用范围：二级缓存是跨 SqlSession 的，通常是 Mapper 级别（即某个 Mapper 的缓存），不同 SqlSession 间可以共享缓存数据。
 * 	•	存储位置：二级缓存的数据会在某个缓存提供者（如内存、磁盘、集群等）中持久化存储。
 * 	•	配置方式：需要在 mybatis-config.xml 文件中显式开启，并且在具体的 Mapper 文件中通过 <cache> 标签来启用。
 * 	•	适用场景：二级缓存适合在读多写少、数据更新频率较低的场景下使用。
 *
 *
 * 本章节主要添加了一级缓存的处理， 主要是在同一个会话即Session中去加入一级缓存
 *  核心步骤和代码块
 *  1.1>定义了 ICache 接口， 这个接口中包含了 增加了对缓存增、删、改、查的操作,具体的实现类是 PerpetualCache
 *  1.2>BaseExecutor 加入对对缓存的依赖    PerpetualCache
 *  1.3> 程序启动是，在XMLConfigBuilder中加入对缓存配置解析
 *      private void settingsElement(Element context) {
 *         if (context == null) return;
 *         List<Element> elements = context.elements();
 *         Properties props = new Properties();
 *         for (Element element : elements) {
 *             props.setProperty(element.attributeValue("name"), element.attributeValue("value"));
 *         }
 *         configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope")));
 *     }
 *  1.4>修改BaseExecuot中的Query和 update、Cmomit的相关逻辑
 *      1.3.1>
 *     @Override
 *     public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
 *         BoundSql boundSql = ms.getBoundSql(parameter);
 *         // TODO 创建缓存Key
 *         CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
 *         return query(ms, parameter, rowBounds, resultHandler,key, boundSql);
 *     }
 *
 *         @Override
 *     public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler,CacheKey key, BoundSql boundSql) {
 *         if (closed) {
 *             throw new RuntimeException("Executor was closed.");
 *         }
 *         // 清理局部缓存，查询堆栈为0则清理。queryStack 避免递归调用清理
 *         if (queryStack == 0 && ms.isFlushCacheRequired()) {
 *              // TODO  清除缓存
 *             clearLocalCache();
 *         }
 *         List<E> list;
 *         try {
 *             queryStack++;
 *             // TODO 根据cacheKey从localCache中查询数据, 先从缓存中去找， 若是找不到，则去数据库找
 *             list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
 *             if (list == null) {
 *                 //TODO 查询数据库
 *                 list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
 *             }
 *         } catch (SQLException e) {
 *             throw new RuntimeException(e);
 *         } finally {
 *             queryStack--;
 *         }
 *         if (queryStack == 0) {
 *             if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
 *                 //TODO 清除缓存
 *                 clearLocalCache();
 *             }
 *         }
 *         return list;
 *     }
 *
 * 1.5> 提交之前， 更新都需要清除缓存
 *       @Override
 *     public void commit(boolean required) throws SQLException {
 *         if (closed) {
 *             throw new RuntimeException("Cannot commit, transaction is already closed");
 *         }
 *         //TODO 清除缓存
 *         clearLocalCache();
 *         if (required) {
 *             transaction.commit();
 *         }
 *     }
 *
 *     @Override
 *     public void rollback(boolean required) throws SQLException {
 *         if (!closed) {
 *             try {
 *             //TODO  清除缓存
 *                 clearLocalCache();
 *             } finally {
 *                 if (required) {
 *                     transaction.rollback();
 *                 }
 *             }
 *         }
 *     }
 *
 * 1.6> 生成key的核心代码
 *     @Override
 *     public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
 *         if (closed) {
 *             throw new RuntimeException("Executor was closed.");
 *         }
 *         //TODO   生成key的核心代码块
 *         CacheKey cacheKey = new CacheKey();
 *         cacheKey.update(ms.getId());
 *         cacheKey.update(rowBounds.getOffset());
 *         cacheKey.update(rowBounds.getLimit());
 *         cacheKey.update(boundSql.getSql());
 *         List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
 *         TypeHandlerRegistry typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
 *         for (ParameterMapping parameterMapping : parameterMappings) {
 *             Object value;
 *             String propertyName = parameterMapping.getProperty();
 *             if (boundSql.hasAdditionalParameter(propertyName)) {
 *                 value = boundSql.getAdditionalParameter(propertyName);
 *             } else if (parameterObject == null) {
 *                 value = null;
 *             } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
 *                 value = parameterObject;
 *             } else {
 *                 MetaObject metaObject = configuration.newMetaObject(parameterObject);
 *                 value = metaObject.getValue(propertyName);
 *             }
 *             cacheKey.update(value);
 *         }
 *         if (configuration.getEnvironment() != null) {
 *             cacheKey.update(configuration.getEnvironment().getId());
 *         }
 *         return cacheKey;
 *     }
 *
 *
 *
 *
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);


    @Test
    public void test_queryActivityById() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IActivityDao dao = sqlSession.getMapper(IActivityDao.class);

        // 3. 测试验证
        ActivityDO req = new ActivityDO();
        req.setActivityId(10004L);

        logger.info("测试结果：{}", JSON.toJSONString(dao.queryActivityById(req)));

        // 测试时，可以分别开启对应的注释，验证功能逻辑
         sqlSession.commit();
        // sqlSession.clearCache();
        // sqlSession.close();

        logger.info("测试结果：{}", JSON.toJSONString(dao.queryActivityById(req)));
    }


}
