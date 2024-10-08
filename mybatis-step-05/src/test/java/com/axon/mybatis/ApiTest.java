package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.enties.UserDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.Reader;

/**
 *
 *  本章主要添加了一下功能
 *  1.抽取sql解析
 *      1.1. 对 MappedStatement 类进行了修改， 将其中mapper中每个方法的属性进行了提取，即 id、parameterType、resultType、 select、sql 进行提取， 提取封装到BoundSql中。
 *      1.2. 然后 MappedStatement 中加入 BoundSql 的 set、get即可。
 *      1.3. 在解析xml时的处理。在构建MappedStatement 前一步加入  BoundSql 的构建
 *          核心代码：
 *          //解析sql
 *          BoundSql boundSql = new BoundSql(sql, parameter, parameterType, resultType);
 *          MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, boundSql).build();
 * 2.添加了DataSource 和 dataSourceFactory的创建
 *     2.1>dataSource 对应的是DruidDataSource ， 通过 dataSourceFactory去创建
 *     2.2> configuration初始化时，加入对应的工厂，即对应的缓存中去。
 *        核心底代码：
 *           public Configuration() {
     *         typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
     *         typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
 *          }
 *
 *
 * 3.添加Transaction 和 TransactionFactory的创建
 *     3.1>Transaction 对应的是JdbcTransaction的事物 ，通过 TransactionFactory 创建
 *     3.2>configuration初始化时，加入对应的工厂，即对应的缓存中去。
 *      核心代码：
 *        public Configuration() {
 *      *         typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
 *      *         typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
 *  *          }
 *
 * 4.Configuration 中添加了 Environment 环境配置的属性
 *      2和3步骤处理完后，在解析的时候，将解析到对应的属性赋值到对应的工厂中 ，即 XMLConfigBuilder类中处理
 *      核心代码：
 *            //获取事务管理器
 *                 TransactionFactory o = (TransactionFactory) typeAliasRegistry.resolveAlias(e.element("transactionManager").attributeValue("type")).newInstance();
 *                 Element dataSourceElement = e.element("dataSource");
 *                 DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).newInstance();
 *                 List<Element> propertyList = dataSourceElement.elements("property");
 *                 Properties properties = new Properties();
 *                 for (Element p : propertyList) {
 *                     properties.setProperty(p.attributeValue("name"), p.attributeValue("value"));
 *                 }
 *                 dataSourceFactory.setProperties(properties);
 *                 DataSource dataSource = dataSourceFactory.getDataSource();
 *                 Environment.Builder builder = new Environment.Builder(id).transactionFactory(o).dataSource(dataSource);
 *                 configuration.setEnvironment(builder.build());
 *
 *
 *     Environment 中包含了transactionFactory、dataSource 的属性
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

            UserDO userDO = userDao.queryUserInfoById(1);
            System.out.println(JSON.toJSONString(userDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
