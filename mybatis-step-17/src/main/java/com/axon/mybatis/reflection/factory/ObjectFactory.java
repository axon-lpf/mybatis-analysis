package com.axon.mybatis.reflection.factory;

import java.util.List;
import java.util.Properties;

public interface ObjectFactory {

    /**
     *  设置属性
     * @param properties
     */
    void setProperties(Properties properties);


    /**
     *  创建生产对象
     * @param type
     * @return
     * @param <T>
     */
    <T> T create(Class<T> type);


    /**
     *  使用指定的构造函数和参数
     * @param type
     * @param constructorArgTypes
     * @param constructorArgs
     * @return
     * @param <T>
     */
    <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);


    /**
     *  返回这个对象是否是集合
     * @param type
     * @return
     * @param <T>
     */
    <T> boolean isCollection(Class<T> type);


}
