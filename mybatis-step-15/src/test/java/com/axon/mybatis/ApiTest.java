package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.builder.xmls.XMLConfigBuilder;
import com.axon.mybatis.dao.IActivityDao;
import com.axon.mybatis.enties.ActivityDO;
import com.axon.mybatis.executor.Executor;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.session.*;
import com.axon.mybatis.session.defaults.DefaultSqlSession;
import com.axon.mybatis.transaction.Transaction;
import com.axon.mybatis.transaction.TransactionFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * 本章节主要添加对insert后，返回主键id的处理
 * 核心代码块：
 *  1.1> 解析xml中 key， 在 XMLStatementBuilder 中进行解析
 *       public void parseStatementNode() {
 *         String id = element.attributeValue("id");
 *         // 参数类型
 *         String parameterType = element.attributeValue("parameterType");
 *         Class<?> parameterTypeClass = resolveAlias(parameterType);
 *         // 结果类型
 *         String resultType = element.attributeValue("resultType");
 *
 *         // 外部应用 resultMap
 *         String resultMap = element.attributeValue("resultMap");
 *
 *         Class<?> resultTypeClass = resolveAlias(resultType);
 *         // 获取命令类型(select|insert|update|delete)
 *         String nodeName = element.getName();
 *         SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
 *
 *         // 获取默认语言驱动器
 *         Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
 *         LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);
 *
 *         // TODO 核心代码解析 在这一步，如果是instert 中包含了SelectKey这个节点，则解析完成，否则不会去解析，也不会加入到缓存中去， 若解析到则已经加入到Configuration中KeyGenter的缓存中去了，
 *         processSelectKeyNodes(id, parameterTypeClass, langDriver);
 *
 *         SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);
 *
 *         // 属性标记【仅对 insert 有用】, MyBatis 会通过 getGeneratedKeys 或者通过 insert 语句的 selectKey 子元素设置它的值 step-15 新增
 *         String keyProperty = element.attributeValue("keyProperty");
 *         KeyGenerator keyGenerator = null;
 *         String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
 *         keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);
 *
 *         if (configuration.hasKeyGenerator(keyStatementId)) {
 *             //TODO 获取对应的key的实现， 存储到MappedStatement 中的缓存中去， 方便后续使用过
 *             keyGenerator = configuration.getKeyGenerator(keyStatementId);
 *         } else {
 *             // TODO 缓存中没有，则走该步骤
 *             keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
 *         }
 *         // 调用助手类
 *         builderAssistant.addMappedStatement(id,
 *                 sqlSource,
 *                 sqlCommandType,
 *                 parameterTypeClass,
 *                 resultMap,
 *                 resultTypeClass,
 *                 keyGenerator,
 *                 keyProperty,
 *                 langDriver);
 *     }
 *
 * 1.2> 解析加入到Configuration中的缓存中去
 *      TODO 如果包含了SelectKey这个节点，才能进入彩发发加入缓存
 *      private void parseSelectKeyNode(String id, Element nodeToHandle, Class<?> parameterTypeClass, LanguageDriver langDriver) {
 *         String resultType = nodeToHandle.attributeValue("resultType");
 *         Class<?> resultTypeClass = resolveClass(resultType);
 *         boolean executeBefore = "BEFORE".equals(nodeToHandle.attributeValue("order", "AFTER"));
 *         String keyProperty = nodeToHandle.attributeValue("keyProperty");
 *
 *         // default
 *         String resultMap = null;
 *         KeyGenerator keyGenerator = new NoKeyGenerator();
 *
 *         // 解析成SqlSource，DynamicSqlSource/RawSqlSource
 *         SqlSource sqlSource = langDriver.createSqlSource(configuration, nodeToHandle, parameterTypeClass);
 *         SqlCommandType sqlCommandType = SqlCommandType.SELECT;
 *
 *         // 调用助手类
 *         builderAssistant.addMappedStatement(id,
 *                 sqlSource,
 *                 sqlCommandType,
 *                 parameterTypeClass,
 *                 resultMap,
 *                 resultTypeClass,
 *                 keyGenerator,
 *                 keyProperty,
 *                 langDriver);
 *
 *         // 给id加上namespace前缀
 *         id = builderAssistant.applyCurrentNamespace(id, false);
 *         // 存放键值生成器配置
 *         MappedStatement keyStatement = configuration.getMappedStatement(id);
 *         //TODO 将SelectKeyGenerator 加入到缓存中去
 *         configuration.addKeyGenerator(id, new SelectKeyGenerator(keyStatement, executeBefore));
 *     }
 *
 *1.3> 具体的使用方法
 *     @Override
 *     public int update(Statement statement) throws SQLException {
 *         PreparedStatement ps = (PreparedStatement) statement;
 *         ps.execute();
 *
 *         Object parameterObject = boundSql.getParameterObject();
 *         //TODO 从 mappedStatement 中获取对应KeyGenerator 的实现调用对应的方法
 *         KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
 *         keyGenerator.processAfter(executor, mappedStatement, ps, parameterObject);
 *         return ps.getUpdateCount();
 *     }
 *
 * 1.4> key 的主要实现步骤
 *       private void processGeneratedKeys(Executor executor, MappedStatement ms, Object parameter) {
 *         try {
 *             if (parameter != null && keyStatement != null && keyStatement.getKeyProperties() != null) {
 *                 String[] keyProperties = keyStatement.getKeyProperties();
 *                 final Configuration configuration = ms.getConfiguration();
 *                 final MetaObject metaParam = configuration.newMetaObject(parameter);
 *                 if (keyProperties != null) {
 *                     Executor keyExecutor = configuration.newExecutor(executor.getTransaction());
 *                     List<Object> values = keyExecutor.query(keyStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
 *                     if (values.size() == 0) {
 *                         throw new RuntimeException("SelectKey returned no data.");
 *                     } else if (values.size() > 1) {
 *                         throw new RuntimeException("SelectKey returned more than one value.");
 *                     } else {
 *                         MetaObject metaResult = configuration.newMetaObject(values.get(0));
 *                         if (keyProperties.length == 1) {
 *                             if (metaResult.hasGetter(keyProperties[0])) {
 *                                 setValue(metaParam, keyProperties[0], metaResult.getValue(keyProperties[0]));
 *                             } else {
 *                                 setValue(metaParam, keyProperties[0], values.get(0));
 *                             }
 *                         } else {
 *                             handleMultipleProperties(keyProperties, metaParam, metaResult);
 *                         }
 *                     }
 *                 }
 *             }
 *         } catch (Exception e) {
 *             throw new RuntimeException("Error selecting key or setting result to parameter object. Cause: " + e);
 *         }
 *     }
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

    @Test
    public void test_queryActivityById() {
        // 1. 获取映射器对象
        IActivityDao dao = sqlSession.getMapper(IActivityDao.class);
        // 2. 测试验证
        ActivityDO res = dao.queryActivityById(100001L);
        logger.info("测试结果：{}", JSON.toJSONString(res));
    }


    @Test
    public void test_insert() {
        // 1. 获取映射器对象
        IActivityDao dao = sqlSession.getMapper(IActivityDao.class);

        ActivityDO activity = new ActivityDO();
        activity.setActivityId(10009L);
        activity.setActivityName("降龙十九掌");
        activity.setActivityDesc("测试数据插入");
        activity.setCreator("降龙十八掌");

        // 2. 测试验证
        Integer res = dao.insert(activity);
        sqlSession.commit();

        logger.info("测试结果：count：{} idx：{}", res, JSON.toJSONString(activity.getId()));
    }

    @Test
    public void test_insert_select() throws IOException {
        // 解析 XML
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();

        // 获取 DefaultSqlSession
        final Environment environment = configuration.getEnvironment();
        TransactionFactory transactionFactory = environment.getTransactionFactory();
        Transaction tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, false);

        // 创建执行器
        final Executor executor = configuration.newExecutor(tx);
        SqlSession sqlSession = new DefaultSqlSession(configuration, executor);

        // 执行查询：默认是一个集合参数
        ActivityDO activity = new ActivityDO();
        activity.setActivityId(10004L);
        activity.setActivityName("测试活动");
        activity.setActivityDesc("测试数据插入");
        activity.setCreator("张三丰");
        int res = sqlSession.insert("cn.bugstack.mybatis.test.dao.IActivityDao.insert", activity);

        Object obj = sqlSession.selectOne("com.axon.mybatis.dao.IActivityDao.insert!selectKey",null);
        logger.info("测试结果：count：{} idx：{}", res, JSON.toJSONString(obj));

        sqlSession.commit();
    }



}
