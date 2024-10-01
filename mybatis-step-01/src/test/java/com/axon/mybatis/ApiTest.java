package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.Reader;

public class ApiTest {

    @Test
    public void test1() {
        String resource = "mybatis-config-datasource.xml";
        Reader reader;
        try {
            Reader resourceAsReader = Resources.getResourceAsReader(resource);
            DefaultSqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsReader);
            SqlSession sqlSession = build.openSession();
            Object o = sqlSession.selectOne("com.axon.mybatis.dao.IUserDao.queryUserInfoById", 1L);
            System.out.println(JSON.toJSONString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
