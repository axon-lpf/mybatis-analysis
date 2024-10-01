package com.axon.mybatis;

/**
 * 实现sqlSession的创建
 */
public interface SqlSessionFactory {


    SqlSession openSession();


}
