package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.datasource.pooled.PooledDataSource;
import com.axon.mybatis.enties.UserDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 本章节主要是添加了数据源链接的非池化和池化两种链接模式
 *  1.  UnpooledDataSourceFactory 非池化创建数据源的工厂， UnpooledDataSource 无池化具体的实现， 去实现 dataSource相关接口
 *      核心代码块：
     *     @Override
     *     public Connection getConnection() throws SQLException {
     *         return doGetConnection(userName, password);
     *     }
 *
 *       这块代码，每次则会创建一个新的链接。
 *
 *  2.  PooledDataSourceFactory 池化创建数据源的工厂， PooledDataSource 池化的具体实现 。  PooledDataSource 实现dataSource相关接口
 *      核心代码块：
 *          @Override
     *     public Connection getConnection() throws SQLException {
     *         return popConnection(dataSource.getUserName(), dataSource.getPassword()).getProxyConnection();
     *     }
 *      这块代码，如果连接池中有有存活的链接，则获取， 如果没有，则等待，等上一个链接释放。如果一直获取不到则超时
 *
 *   3.Configuration 初始化时，加入对应的别名处理
 *       核心代码块：
 *              public Configuration() {
         *         typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
         *         typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
         *         typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
         *         typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
 *     }
 *
 *   4. 解析时，获取到对应的数据源策略
 *      核心代码块：
 *           DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).newInstance();
 *           dataSourceElement.attributeValue("type") 获取的是dataSource的属性的值，即别名， 然后从typeAliasRegistry中去获取，并创建对应的实例
 *
 *  3.  具体的使用
 *      核心代码块：
 *              @Override
             *     public <T> T selectOne(String statement, Object parameter) {
             *         MappedStatement mappedStatement = configuration.getMappedStatement(statement);
             *         Environment environment = configuration.getEnvironment();
             *         Connection connection=null;
             *         try {
 *                         //这里获取链接
             *             connection = environment.getDataSource().getConnection();
             *             BoundSql boundSql = mappedStatement.getBoundSql();
             *             PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
             *             preparedStatement.setLong(1, Long.parseLong(((Object[]) parameter)[0].toString()));
             *             ResultSet resultSet = preparedStatement.executeQuery();
             *             List<T> objects = resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
             *             return objects.get(0);
             *         } catch (Exception e) {
             *             throw new RuntimeException(e);
             *         }
             *     }
 *
 *
 *
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

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
            for (int i = 0; i < 50; i++) {
                UserDO userDO = userDao.queryUserInfoById(1);
                logger.info("测试结果：{}",JSON.toJSONString(userDO));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test_pooled() throws SQLException, InterruptedException {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver("com.mysql.jdbc.Driver");
        pooledDataSource.setUrl("jdbc:mysql://192.168.2.104:3306/my_test_db?useUnicode=true&amp;characterEncoding=utf8");
        pooledDataSource.setUserName("root");
        pooledDataSource.setPassword("123456");
        // 持续获得链接
        while (true) {
            Connection connection = pooledDataSource.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            // 注释掉/不注释掉测试
            //connection.close();
        }
    }
}
