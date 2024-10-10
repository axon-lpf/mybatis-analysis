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
        activity.setActivityId(10006L);
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
