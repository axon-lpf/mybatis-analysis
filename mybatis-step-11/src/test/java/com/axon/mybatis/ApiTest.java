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
 * 本章节主要添加了对ResultSetHandler的处理和实现，即对查询结果的封装和处理
 *   1.ResultSetHandler 由DefaultResultSetHandler的类进行实现，  StatementHandler 中有依赖ResultSetHandler，在处理返回结果的时候调用  ResultSetHandler
 *   核心代码块：
 *     1.1> 初始化完成 默认的ResultSetHandler的赋值
 *                 public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
             *         this.configuration = mappedStatement.getConfiguration();
             *         this.executor = executor;
             *         this.mappedStatement = mappedStatement;
             *         this.parameterObject = parameterObject;
             *         this.boundSql = boundSql;
             *         this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
 *                      // resultSet的赋值
             *         this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, rowBounds, resultHandler, boundSql);
 *
 *          }
 *
 *      1.2> PrepareStatementHandler中
 *             @Override
         *     public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
         *         PreparedStatement ps = (PreparedStatement) statement;
         *         ps.execute();
 *                 //处理返回结果
         *         return resultSetHandler.<E>handleResultSets(ps);
         *     }
 *      1.3> ResulstSetHandler中的处理
 *            public List<Object> handleResultSets(Statement stmt) throws SQLException {
         *         final List<Object> multipleResults = new ArrayList<>();
         *         int resultSetCount = 0;
         *         ResultSetWrapper rsw = new ResultSetWrapper(stmt.getResultSet(), configuration);
         *         List<ResultMap> resultMaps = mappedStatement.getResultMaps();
         *         while (rsw != null && resultMaps.size() > resultSetCount) {
         *             ResultMap resultMap = resultMaps.get(resultSetCount);
         *             handleResultSet(rsw, resultMap, multipleResults, null);
         *             rsw = getNextResultSet(stmt);
         *             resultSetCount++;
         *         }
         *         return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
         *     }
 *
 *
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
            UserDO userDO = userDao.queryUserInfoById(1L);
            logger.info("测试结果：{}", JSON.toJSONString(userDO));
            UserDO queryUser = new UserDO();
            queryUser.setId(1L);
            queryUser.setName("啊稀薄");
             userDO = userDao.queryUserInfo(queryUser);
            logger.info("测试结果：{}", JSON.toJSONString(userDO));

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
