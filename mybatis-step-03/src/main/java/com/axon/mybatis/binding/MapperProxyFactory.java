package com.axon.mybatis.binding;


import com.axon.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 代理工厂类
 * <p>
 * 使用了简单工厂区包装代理类
 *
 * @param <T>
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;


    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * 创建新的代理类
     *
     * @param sqlSession
     * @return
     */
    public T newTnstance(SqlSession sqlSession) {

        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

}
