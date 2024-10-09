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
 * 本章主要添加了LanguageDriver 是一个接口，主要负责解析和处理 SQL 语句，特别是动态 SQL 的生成。MyBatis 支持通过 LanguageDriver 进行 SQL 语句的定制化生成和解析，允许开发者灵活地定义 SQL 语法风格和处理方式。
 * 1. LanguageDriver
 *    主要实现有 XMLLanguageDriver的处理。 又依赖 XMLScriptBuilder sql语言构建器
 *    核心代码块：
 *      1.1> Configuration中  LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();  设置语言脚本解析注册器
 *      1.2> Configuration中实例化时，注册默认的语言脚本解析器
 *         public Configuration() {
     *         typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
     *         typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
     *         typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
     *         typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
     *         // 语言脚本解析器
     *         languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
 *
 *       }
 *
 *     1.3>解析xml时，去构建sqlSource, 在XMLStatementBuilder中
 *            public void parseStatementNode() {
         *         String id = element.attributeValue("id");
         *         // 参数类型
         *         String parameterType = element.attributeValue("parameterType");
         *         Class<?> parameterTypeClass = resolveAlias(parameterType);
         *         // 结果类型
         *         String resultType = element.attributeValue("resultType");
         *         Class<?> resultTypeClass = resolveAlias(resultType);
         *         // 获取命令类型(select|insert|update|delete)
         *         String nodeName = element.getName();
         *         SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
         *
         *         // 获取默认语言驱动器
         *         Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
         *         LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);
 *                 // 这里去解析sql脚本
         *         SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);
 *
         *         MappedStatement mappedStatement = new MappedStatement.Builder(configuration, currentNamespace + "." + id, sqlCommandType, sqlSource, resultTypeClass).build();
         *         // sql脚本解析完成后，添加到  MappedStatement 缓存中去
         *         configuration.addMappedStatement(mappedStatement);
 *     }
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
