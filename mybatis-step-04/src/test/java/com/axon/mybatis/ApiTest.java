package com.axon.mybatis;

import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.Reader;

/**
 *
 *
 * 本章节有以下内容
 *
 *  1.引入了解析xml的相关内容，即XMLConfigBuilder
 *      1.1>.读取了mapper中的xml文件
 *      1.2>.解析xml文件
 *      1.3>.读取相关的属性 namespace、id、parameterType、resultType、 select
 *      1.4>.将这些属性构建到MappedStatement 对象中
 *          核心代码：
 *          MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, parameterType, resultType, sql, parameter).build();
 *      1.5>.将 MappedStatement对象放入到 Configuration 中的mappedStatements中
 *          核心代码：
 *          Map<String, MappedStatement> mappedStatements = new HashMap<>();
 *          注意： 一个 mappedStatement对应一个mapper中的方法
 *      1.6>.以上处理完成之后，在去创建对应的代理对象， 这个代理对象Dao =>对应一个Mapper， 一个mapper中有很多的方法
 *
 *
 *  2.引入了MapperMethod， 对调用方法进行了细化， 并将调用的具体的方法也存入缓存中。多次调用可以从缓存中去获取。
 *      2.1>主要是MapperProxy 中 invoke 处理
 *          核心代码：
 *          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
     *         if (Object.class.equals(method.getDeclaringClass())) {
     *             return method.invoke(this, args);
     *         } else {
     *             final MapperMethod mapperMethod = cachedMapperMethod(method);
     *             return mapperMethod.execute(sqlSession, args);
     *         }
 *          }
 *
 *         private MapperMethod cachedMapperMethod(Method method) {
 *         MapperMethod mapperMethod = methodCache.get(method);
     *         if (mapperMethod == null) {
     *             //找不到才去new， 一个mapperMethod 对应一个命令
     *             mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
     *             methodCache.put(method, mapperMethod);
     *         }
     *         return mapperMethod;
 *          }
 *
 *       2.2> MapperMethod 中包装了SqlCommand命令，  包含了Select、Update、 Insert、Deleted、
 *           即一个MapperMethod 则对应一个命令，
 *
 *
 *
 */
public class ApiTest {

    @Test
    public void test_sqlSessionFactory() {
        String resource = "mybatis-config-datasource.xml";
        try {
            Reader resourceAsReader = Resources.getResourceAsReader(resource);

            //获取sqlSession的factory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsReader);

            SqlSession sqlSession = sqlSessionFactory.openSession();

            //获取代理对象
            IUserDao userDao = sqlSession.getMapper(IUserDao.class);

            String userDO = userDao.queryUserInfoById(1);
            System.out.println(userDO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
