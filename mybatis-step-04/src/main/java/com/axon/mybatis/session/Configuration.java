package com.axon.mybatis.session;

import com.axon.mybatis.binding.MapperRegistry;
import com.axon.mybatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

    /**
     *  映射注册机
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     *  隐射sql语句，存在mapper中
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();


    /**
     *  添加注册机， 通过扫描包添加
     * @param packageName
     */
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    /**
     *  单个添加
     * @param type
     * @param <T>
     */
    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    /**
     *  获取注册机
     * @param type
     * @param sqlSession
     * @return
     * @param <T>
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    /**
     *  判断是否存在
     * @param type
     * @return
     */
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    /**
     *  添加sql语句的解析到缓存中
     * @param ms
     */
    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    /**
     *  获取sql语句的解析
     * @param id
     * @return
     */
    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }


}
