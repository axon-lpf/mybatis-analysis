package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IActivityDao;
import com.axon.mybatis.enties.ActivityDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * 本章主要是添加了二级缓存的处理
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);



    @Test
    public void test_queryActivityById() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        // 2. 请求对象
        ActivityDO req = new ActivityDO();
        req.setActivityId(10004L);

        // 3. 第一组：SqlSession
        // 3.1 开启 Session
        SqlSession sqlSession01 = sqlSessionFactory.openSession();
        // 3.2 获取映射器对象
        IActivityDao dao01 = sqlSession01.getMapper(IActivityDao.class);
        logger.info("测试结果01：{}", JSON.toJSONString(dao01.queryActivityById(req)));
        sqlSession01.close();

        // 4. 第一组：SqlSession
        // 4.1 开启 Session
        SqlSession sqlSession02 = sqlSessionFactory.openSession();
        // 4.2 获取映射器对象
        IActivityDao dao02 = sqlSession02.getMapper(IActivityDao.class);
        logger.info("测试结果02：{}", JSON.toJSONString(dao02.queryActivityById(req)));
        sqlSession02.close();
    }

}
