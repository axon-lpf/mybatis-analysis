package com.axon.mybatis.binding;


import com.axon.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理工厂类
 * <p>
 * 使用了简单工厂区包装代理类
 *
 * @param <T>
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    public Map<Method, MapperMethod> getMethodCache() {
        return methodCache;
    }

    private Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();


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

        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

}
