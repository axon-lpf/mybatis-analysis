package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.enties.UserDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.Reader;

/**
 *  本章主要是添加了sql的调用，以及事务的相关处理， 数据源，事务处理
 */
public class ApiTest {

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
            System.out.println(JSON.toJSONString(userDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
