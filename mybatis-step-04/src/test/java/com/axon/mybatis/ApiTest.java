package com.axon.mybatis;

import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.Reader;

/**
 * 本章主要引入了SqlSessionFactoryBuilder的执行 过程，包括XML文件的解析，以及Configuration的类的处理，
 * 从而使DefaultSqlSession更加灵活的获取对对应的信息
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

            String userDO = userDao.queryUserInfoById(1);
            System.out.println(userDO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
