package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybaits.MapperProxyFactory;
import com.axon.mybatis.dao.IUserDao;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ApiTest {


    /**
     * 模拟代理类测试
     *
     *   MapperProxyFactory: 创建代理类的工厂， 创建的代理类，代替IDao接口去操作数据库
     *   MapperProxy: 具体的实际代理类， 实现了InvocationHandler 接口， 调用invoke
     *
     *   MapperProxyFactory 通过操作 MapperProxy 创建实际IDao的操作
     *   核心代码：
     *        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
     *       return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
     *
     *
     *
     */
    @Test
    public void test_MapperProxyFactory() {

        MapperProxyFactory<IUserDao> factory = new MapperProxyFactory<>(IUserDao.class);
        Map<String, String> sqlSession = new HashMap<>();

        sqlSession.put("com.axon.mybatis.dao.IUserDao.queryUserName", "模拟执行xml中的sql语句queryUserName");
        sqlSession.put("com.axon.mybatis.dao.IUserDao.queryUserInfoById", "模拟执行xml中的sql语句queryUserInfoById");

        IUserDao iUserDao = factory.newTnstance(sqlSession);

        String userDO = iUserDao.queryUserInfoById(1);
        System.out.println(JSON.toJSONString(userDO));

        String result = iUserDao.queryUserName("10001");

        System.out.println(result);

    }
}
