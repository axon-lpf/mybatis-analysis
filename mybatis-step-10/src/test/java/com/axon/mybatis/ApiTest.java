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
 * 本章节主要添加了ParameterHandler的处理
 *      1.主要接口 ParameterHandler，由  DefaultParameterHandler 具体的实现类去实现,  ParameterHandler功能作用去处理参数的
 *        StatementHandler 由依赖  ParameterHandler
 *       核心代码块：
 *       1.1> Configuration中创建一个方法
 *          public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
     *         // 创建参数处理器
     *         ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
     *         // 插件的一些参数，也是在这里处理，暂时不添加这部分内容 interceptorChain.pluginAll(parameterHandler);
     *         return parameterHandler;
 *      }
 *      1.2>在BaseStatementHandler构造函数是实例化默认的参数处理器
 *             public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
         *         this.configuration = mappedStatement.getConfiguration();
         *         this.executor = executor;
         *         this.mappedStatement = mappedStatement;
         *         this.parameterObject = parameterObject;
         *         this.boundSql = boundSql;
 *                 // 默认的参数处理器
         *         this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
         *         this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, boundSql);
 *     }
 *      1.3> 在PreParedStatementHandler中实现对 parameterHandler的调用
 *              @Override
         *     public void parameterize(Statement statement) throws SQLException {
         *         parameterHandler.setParameters((PreparedStatement) statement);
         *
         *     }
 *      1.4>对sql语句的参数设置  DefaultParameterHandler
 *           @Override
         *     public void setParameters(PreparedStatement ps) throws SQLException {
         *         List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
         *         if (null != parameterMappings) {
         *             for (int i = 0; i < parameterMappings.size(); i++) {
         *                 ParameterMapping parameterMapping = parameterMappings.get(i);
         *                 String propertyName = parameterMapping.getProperty();
         *                 Object value;
         *                 if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
         *                     value = parameterObject;
         *                 } else {
         *                     // 通过 MetaObject.getValue 反射取得值设进去
         *                     MetaObject metaObject = configuration.newMetaObject(parameterObject);
         *                     value = metaObject.getValue(propertyName);
         *                 }
         *                 JdbcType jdbcType = parameterMapping.getJdbcType();
         *
         *                 // 设置参数
         *                 logger.info("根据每个ParameterMapping中的TypeHandler设置对应的参数信息 value：{}", JSON.toJSONString(value));
         *                 TypeHandler typeHandler = parameterMapping.getTypeHandler();
         *                 //将对应的参数值，设置的到sql语句中去
         *                 typeHandler.setParameter(ps, i + 1, value, jdbcType);
         *             }
         *         }
         *     }
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
