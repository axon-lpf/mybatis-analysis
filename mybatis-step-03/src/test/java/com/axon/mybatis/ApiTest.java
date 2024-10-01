package com.axon.mybatis;

import com.axon.mybatis.binding.MapperRegistry;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.Test;


public class ApiTest {


    /**
     * 模拟代理类测试
     */
    @Test
    public void test_MapperProxyFactory() {

        MapperRegistry mapperRegistry = new MapperRegistry();
        mapperRegistry.addMappers("com.axon.mybatis.dao");


        DefaultSqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mapperRegistry);
        SqlSession openSession = sqlSessionFactory.openSession();

        IUserDao userDao = openSession.getMapper(IUserDao.class);

        String result = userDao.queryUserName("10001");

        System.out.println(result);

    }
}
