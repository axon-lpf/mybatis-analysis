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
 * 本章节中主要添加了以下功能：
 *  1.StatementHandler    负责生成 Statement 对象（例如 PreparedStatement），以及执行 SQL 语句。
 *      核心方法:
 *          1.	prepare():
         * 	•	创建并初始化 Statement 对象，可能是 PreparedStatement 或 CallableStatement。
         * 	2.	parameterize():
         * 	•	负责为 SQL 语句设置参数。
         * 	3.	query():
         * 	•	执行查询操作，并返回结果集。
         * 	4.	update():
         * 	•	执行更新操作，通常用于 INSERT、UPDATE 和 DELETE 操作。
         * 	5.	batch():
         * 	•	执行批量更新操作。
 *
 * 	2.添加Executor执行器， 主要负责调度整个sql的执行过程
 * 	    核心方法：
 * 	     *  1.	query()
         * 	•	用于执行查询操作。Executor 调用 StatementHandler 来执行查询，并处理结果集。
         * 	•	支持缓存查询结果，如果在同一个 SqlSession 中多次查询相同数据，则会从缓存中获取。
         * 	2.	update()
         * 	•	用于执行更新操作，包括 INSERT、UPDATE 和 DELETE。Executor 负责提交更新操作到数据库。
         * 	3.	commit()
         * 	•	用于提交事务，将所有在事务中执行的更新操作提交到数据库。
         * 	4.	rollback()
         * 	•	用于回滚事务，撤销未提交的数据库操作，确保数据一致性。
         * 	5.	flushStatements()
         * 	•	刷新 Executor 中缓存的批量操作语句并执行它们，返回执行结果。这通常用于批处理操作。
         * 	6.	clearLocalCache()
         * 	•	清空一级缓存，确保后续操作直接从数据库获取最新数据。
         * 	7.	close()
         * 	•	关闭 Executor，释放相关资源，包括关闭数据库连接、清除缓存等。
 *
 *  3. 在Executor中调用的主要方法
 *     核心代码：
 *     @Override
 *     protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
 *         Configuration configuration = ms.getConfiguration();
 *         //获取sql预处理的handler的具体实现
 *         StatementHandler statementHandler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
 *         try {
 *             //获取链接
 *             Connection connection = transaction.getConnection();
 *             Statement prepare = statementHandler.prepare(connection);
 *             //设置参数
 *             statementHandler.parameterize(prepare);
 *             // 执行sql语句，并返回查询结果
 *             return statementHandler.query(prepare, resultHandler);
 *         } catch (Exception e) {
 *             throw new RuntimeException(e);
 *         }
 *     }
 *
 *  4.SqlSession中添加对Executor的调用
 *      核心代码：
 *     @Override
 *     public <T> T selectOne(String statement, Object parameter) {
 *         try {
 *             MappedStatement mappedStatement = configuration.getMappedStatement(statement);
 *             // 使用了Executor的执行器去调用
 *             List<T> objects = executor.query(mappedStatement, parameter, Executor.NO_RESULT_HANDLER, mappedStatement.getBoundSql());
 *             return objects.get(0);
 *         } catch (Exception e) {
 *             throw new RuntimeException(e);
 *         }
 *     }
 *
 *
 *  MyBatis 执行 SQL 的大致流程如下：
 *
 * 	1.	Executor 负责调度整个 SQL 执行过程。
 * 	2.	StatementHandler 负责生成 Statement 对象（例如 PreparedStatement），以及执行 SQL 语句。
 * 	3.	ParameterHandler 负责将用户提供的参数设置到 SQL 语句中。
 * 	4.	ResultSetHandler 负责将 SQL 执行后的结果集处理成用户期望的 Java 对象。
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

            UserDO userDO = userDao.queryUserInfoById(1);
            logger.info("测试结果：{}",JSON.toJSONString(userDO));

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
