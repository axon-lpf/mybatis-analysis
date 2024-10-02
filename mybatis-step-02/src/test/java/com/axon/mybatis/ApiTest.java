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
