package com.axon.mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import com.axon.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 提供报路径的扫描和映射器代理类注册服务，完成接口对象的代理类注册
 */
public class MapperRegistry {

    /**
     * 将已添加的引射器代理添加到HasMap的缓存中
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();


    /**
     * 获取对应的代理对象
     *
     * @param type
     * @param sqlSession
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("MapperProxyFactory not found for type " + type);
        }

        return mapperProxyFactory.newTnstance(sqlSession);
    }


    /**
     * 添加代理对象
     */

    public <T> void addMapper(Class<T> type) {
        //Mapper必须是接口才能够进行注册
        if (type.isInterface()) {
            if (knownMappers.containsKey(type)) {
                throw new RuntimeException("代理对象已存在");
            }
            knownMappers.put(type, new MapperProxyFactory<>(type));
        }

    }

    public void addMappers(String packageName) {
        Set<Class<?>> classes = ClassScanner.scanPackage(packageName);
        for (Class<?> clazz : classes) {
            addMapper(clazz);
        }
    }
}
