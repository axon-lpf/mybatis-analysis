package com.axon.mybaits.reflection.wrapper;


import com.axon.mybatis.reflection.MetaObject;
import com.axon.mybatis.reflection.factory.ObjectFactory;
import com.axon.mybatis.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * 对象包装器
 */
public interface ObjectWrapper {

    Object get(PropertyTokenizer prop);

    void set(PropertyTokenizer prop, Object value);


    /**
     * 查找属性
     *
     * @param name
     * @param useCamelCaseMapping
     * @return
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 获取get的名字列表
     *
     * @return
     */
    String[] getGetterNames();

    /**
     * 获取setter的名字列表
     *
     * @return
     */
    String[] getSetterNames();

    /**
     * 获取setter的类型
     *
     * @param name
     * @return
     */
    Class<?> getSetterType(String name);


    /**
     * 获取getter的类型
     *
     * @param name
     * @return
     */
    Class<?> getGetterType(String name);


    /**
     * 是否有指定的setter
     *
     * @param name
     * @return
     */
    boolean hasSetter(String name);

    /**
     * 是否有指定的getter
     *
     * @param name
     * @return
     */
    boolean hasGetter(String name);

    /**
     * 实例化属性
     *
     * @param name
     * @param prop
     * @param objectFactory
     * @return
     */
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    /**
     * 是否是集合
     *
     * @return
     */
    boolean isCollection();

    /**
     * 添加属性
     *
     * @param element
     */
    void add(Object element);

    /**
     * 添加属性
     *
     * @param element
     * @param <E>
     */
    <E> void addAll(List<E> element);

}
