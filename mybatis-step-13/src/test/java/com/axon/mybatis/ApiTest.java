package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IUserDao;
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
import java.util.List;

/**
 * 本章节主要添加了基于注解的增删改查的主要流程
 * 主要核心代码块
 * 1.1>创建对应的anntation的注解  @select  @update  @delete @insert
 * 1.2>XMLConfigBuilder中加入注解解析的逻辑
 *       private void mapperElement(Element mappers) throws IOException, DocumentException, ClassNotFoundException {
 *         List<Element> mapperList = mappers.elements("mapper");
 *         for (Element e : mapperList) {
 *             String resource = e.attributeValue("resource");
 *             String mapperClass = e.attributeValue("class");
 *             // 这里是一个xml的解析
 *             if (resource != null && mapperClass == null) {
 *                 InputStream inputStream = Resources.getResourceAsStream(resource);
 *                 XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
 *                 mapperParser.parse();
 *             }
 *             //TODO   这里是注解解析的逻辑   xml配置           <mapper class="com.axon.mybatis.dao.IUserDao"/>
 *             else if (resource == null && mapperClass != null) {
 *                 Class<?> aClass = Resources.classForName(mapperClass);
 *                 configuration.addMapper(aClass);
 *             }
 *         }
 *     }
 *
 * 1.3> MapperRegistry中对addMpper进行修改
 *          public <T> void addMapper(Class<T> type) {
 *         //Mapper必须是接口才能够进行注册
 *         if (type.isInterface()) {
 *             if (knownMappers.containsKey(type)) {
 *                 throw new RuntimeException("代理对象已存在");
 *             }
 *             // 注册映射器代理工厂
 *             knownMappers.put(type, new MapperProxyFactory<>(type));
 *
 *             // TODO  加入对注解解析的功能
 *             MapperAnnotationBuilder parse = new MapperAnnotationBuilder(config, type);
 *             parse.parse();
 *         }
 *     }
 *
 * 1.4> MapperAnnotationBuilder中的核心方法
 *       public void parse() {
 *         String resource = type.toString();
 *         if (!configuration.isResourceLoaded(resource)) {
 *             assistant.setCurrentNamespace(type.getName());
 *
 *             Method[] methods = type.getMethods();
 *             for (Method method : methods) {
 *                 if (!method.isBridge()) {
 *                     // 解析语句
 *                     parseStatement(method);
 *                 }
 *             }
 *         }
 *     }
 *
 *     private void parseStatement(Method method) {
 *         Class<?> parameterTypeClass = getParameterType(method);
 *         LanguageDriver languageDriver = getLanguageDriver(method);
 *         SqlSource sqlSource = getSqlSourceFromAnnotations(method, parameterTypeClass, languageDriver);
 *
 *         if (sqlSource != null) {
 *             final String mappedStatementId = type.getName() + "." + method.getName();
 *             SqlCommandType sqlCommandType = getSqlCommandType(method);
 *             boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
 *
 *             String resultMapId = null;
 *             if (isSelect) {
 *                 resultMapId = parseResultMap(method);
 *             }
 *
 *             // TODO 解析完成之后加入 MappedStatement中去， 用于后续的调用
 *             assistant.addMappedStatement(
 *                     mappedStatementId,
 *                     sqlSource,
 *                     sqlCommandType,
 *                     parameterTypeClass,
 *                     resultMapId,
 *                     getReturnType(method),
 *                     languageDriver
 *             );
 *         }
 *     }
 *
 *  1.5>在执行sql的时候从MappedStatement 中获取 ， 根据mappedStatementId
 *          @Override
 *     public <E> List<E> selectList(String statement, Object parameter) {
 *         logger.info("执行查询 statement：{} parameter：{}", statement, JSON.toJSONString(parameter));
 *         // TODO 获取操作
 *         MappedStatement ms = configuration.getMappedStatement(statement);
 *         return executor.query(ms, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER, ms.getSqlSource().getBoundSql(parameter));
 *     }
 *
 *
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
        user.setName("周瑜");
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
        int count = userDao.deleteUserInfoByUserId("2");
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
        userDO.setId(3L);
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
