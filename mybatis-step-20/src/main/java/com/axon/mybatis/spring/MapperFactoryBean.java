package com.axon.mybatis.spring;

import com.axon.mybatis.session.SqlSessionFactory;

import org.springframework.beans.factory.FactoryBean;


public class MapperFactoryBean<T> implements FactoryBean<T> {


    private Class<T> mapperInterface;
    private SqlSessionFactory sqlSessionFactory;

    public MapperFactoryBean(Class<T> mapperInterface, SqlSessionFactory sqlSessionFactory) {
        this.mapperInterface = mapperInterface;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     *  返回实际的dao对象
     * @return
     * @throws Exception
     */
    @Override
    public T getObject() throws Exception {
        return sqlSessionFactory.openSession().getMapper(mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
