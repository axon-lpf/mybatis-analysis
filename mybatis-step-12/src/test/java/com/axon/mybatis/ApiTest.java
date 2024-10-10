package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.datasource.pooled.PooledDataSource;
import com.axon.mybatis.enties.UserDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 本章主要是添加了update、 delete、 insert相关的核心流程
 * 主要核心代码块：
 * 1.1> DefaultSqlSession中添加了以下方法
 *        @Override
 *     public int insert(String statement, Object parameter) {
 *         // 在 Mybatis 中 insert 调用的是 update
 *         return update(statement, parameter);
 *     }
 *
 *     @Override
 *     public Object delete(String statement, Object parameter) {
 *         // 这里也是调用的update方法
 *         return update(statement, parameter);
 *     }
 *
 *     @Override
 *     public int update(String statement, Object parameter) {
 *         MappedStatement ms = configuration.getMappedStatement(statement);
 *         try {
 *             return executor.update(ms, parameter);
 *         } catch (SQLException e) {
 *             throw new RuntimeException("Error updating database.  Cause: " + e);
 *         }
 *     }
 *
 * 1.2>Executor类中添加以下方法
 *     @Override
 *     public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
 *         Statement stmt = null;
 *         try {
 *             Configuration configuration = ms.getConfiguration();
 *             // 新建一个 StatementHandler
 *             StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
 *             // 准备语句
 *             stmt = prepareStatement(handler);
 *             // StatementHandler.update
 *             return handler.update(stmt);
 *         } finally {
 *             closeStatement(stmt);
 *         }
 *     }
 *
 * 1.3>StatementHandler 中也添加 update的方法处理，与query不同的是，query返回的查询结果对象， update是返回的受影响的行数
 *     @Override
 *     public int update(Statement statement) throws SQLException {
 *         PreparedStatement ps = (PreparedStatement) statement;
 *         ps.execute();
 *         return ps.getUpdateCount();
 *     }
 *
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);


    private SqlSession sqlSession;

    @Before
    public void init() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        sqlSession = sqlSessionFactory.openSession();
    }

    /**
     * 测试添加用户
     */
    @Test
    public void test_insertUserInfo() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 2. 测试验证
        UserDO user = new UserDO();
        user.setName("熊猫");
        userDao.insertUserInfo(user);
        logger.info("测试结果：{}", "Insert OK");
        // 3. 提交事务
        sqlSession.commit();
    }

    /**
     * 测试删除用户
     */
    @Test
    public void test_deleteUserInfoByUserId() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 2. 测试验证
        int count = userDao.deleteUserInfoByUserId("1");
        logger.info("测试结果：{}", count == 1);

        // 3. 提交事务
        sqlSession.commit();
    }

    /**
     * 测试更新用户
     */

    @Test
    public void test_updateUserInfo() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 2. 测试验证
        UserDO userDO = new UserDO();
        userDO.setId(2L);
        userDO.setName("风清阳");
        int count = userDao.updateUserInfo(userDO);
        logger.info("测试结果：{}", count);

        // 3. 提交事务
        sqlSession.commit();
    }


    @Test
    public void test_queryUserInfoById() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 2. 测试验证：基本参数
        UserDO user = userDao.queryUserInfoById(3L);
        logger.info("测试结果：{}", JSON.toJSONString(user));
    }

    @Test
    public void test_queryUserInfo() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        UserDO userDO = new UserDO();
        userDO.setId(3L);
        userDO.setName("风清阳");
        userDO = userDao.queryUserInfo(userDO);
        // 2. 测试验证：对象参数
        logger.info("测试结果：{}", JSON.toJSONString(userDO));
    }

    @Test
    public void test_queryUserInfoList() {
        // 1. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 2. 测试验证：对象参数
        List<UserDO> users = userDao.queryUserInfoList();
        logger.info("测试结果：{}", JSON.toJSONString(users));
    }


}
