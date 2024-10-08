package com.axon.mybatis;

import com.axon.mybatis.binding.MapperRegistry;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.Test;


public class ApiTest {

    /**
     *   本章节中，主要添加了MapperRegistry
     *   MapperRegistry的作用主要是管理 创建代理类的工厂, 使用了HashMap去做存储， 用于缓存， 后续使用，直接从这个缓存中取出即可。
     *   核心代码块：
     *       Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();
     *
     *   对这个map 进行添加和获取的操作。
     *   在启动的时候就去扫描对应dao包，然后批量创建存储到这个mapper中。
     *
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
