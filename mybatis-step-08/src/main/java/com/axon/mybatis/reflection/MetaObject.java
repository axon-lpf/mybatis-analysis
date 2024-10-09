package com.axon.mybatis.reflection;

import com.axon.mybatis.reflection.factory.ObjectFactory;
import com.axon.mybatis.reflection.property.PropertyTokenizer;
import com.axon.mybatis.reflection.wrapper.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 主要功能和作用
 *
 * 	1.	简化对象属性的读写操作
 * MetaObject 提供了一组方法来读取和修改对象的属性，无论对象是普通的 JavaBean，Map 还是集合类。它能自动处理属性的获取和设置，避免繁琐的手工编写反射代码，提高了代码的可读性和可维护性。
 * 	2.	支持嵌套属性操作
 * MetaObject 支持对嵌套属性进行操作，例如 person.address.street，你可以通过一行代码访问和修改嵌套的属性值，而不必手动逐层获取对象。
 * 	3.	处理集合和数组类型的属性
 * 对于数组、List、Map 等集合类型的数据，MetaObject 提供了便利的接口来获取和修改它们的内容。它支持索引访问、动态添加元素等功能。
 * 	4.	反射包装
 * MetaObject 是对 Java 反射 API 的封装。它隐藏了 Java 反射的复杂性，提供了一种更加简洁和安全的方式来操作对象属性。比如，处理私有字段或属性时，MetaObject 能轻松绕过访问权限限制。
 * 	5.	辅助动态 SQL 生成
 * 在 MyBatis 中，MetaObject 常被用于动态 SQL 的生成中，特别是在 OGNL 表达式或者类似操作中，通过 MetaObject 可以快速操作对象属性，帮助动态构建查询条件。
 * 	6.	插件和拦截器中的常用工具
 * MetaObject 在 MyBatis 插件（Interceptor）中非常常用。例如，拦截器可以通过 MetaObject 对 MyBatis 内部对象（如 MappedStatement、BoundSql）进行修改，动态改变 SQL 的执行方式。
 */
public class MetaObject {

    /**
     *  原始的对象
     */
    private Object originalObject;
    /**
     *  对象包装器
     */
    private ObjectWrapper objectWrapper;

    /**
     *  对象工厂
     */
    private ObjectFactory objectFactory;

    /**
     *  对象包装工厂
     */
    private ObjectWrapperFactory objectWrapperFactory;

    private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory) {
        this.originalObject = object;
        this.objectFactory = objectFactory;
        this.objectWrapperFactory = objectWrapperFactory;

        if (object instanceof ObjectWrapper) {
            // 如果对象本身已经是ObjectWrapper型，则直接赋给objectWrapper
            this.objectWrapper = (ObjectWrapper) object;
        } else if (objectWrapperFactory.hasWrapperFor(object)) {
            // 如果有包装器,调用ObjectWrapperFactory.getWrapperFor
            this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
        } else if (object instanceof Map) {
            // 如果是Map型，返回MapWrapper
            this.objectWrapper = new MapWrapper(this, (Map) object);
        } else if (object instanceof Collection) {
            // 如果是Collection型，返回CollectionWrapper
            this.objectWrapper = new CollectionWrapper(this, (Collection) object);
        } else {
            // 除此以外，返回BeanWrapper
            this.objectWrapper = new BeanWrapper(this, object);
        }
    }

    public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory) {
        if (object == null) {
            // 处理一下null,将null包装起来
            return SystemMetaObject.NULL_META_OBJECT;
        } else {
            return new MetaObject(object, objectFactory, objectWrapperFactory);
        }
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    /* --------以下方法都是委派给 ObjectWrapper------ */

    /**
     *  查找属性
     * @param propName
     * @param useCamelCaseMapping
     * @return
     */
    public String findProperty(String propName, boolean useCamelCaseMapping) {
        return objectWrapper.findProperty(propName, useCamelCaseMapping);
    }

    /**
     *  获取得getter的名字列表
     * @return
     */
    public String[] getGetterNames() {
        return objectWrapper.getGetterNames();
    }

    /**
     * 获得setter的名字列表
     * @return
     */
    public String[] getSetterNames() {
        return objectWrapper.getSetterNames();
    }

    /**
     * 取得setter的类型列表
     * @param name
     * @return
     */
    public Class<?> getSetterType(String name) {
        return objectWrapper.getSetterType(name);
    }

    /**
     * 取得getter的类型列表
     * @param name
     * @return
     */
    public Class<?> getGetterType(String name) {
        return objectWrapper.getGetterType(name);
    }

    /**
     * 是否有指定的setter
     * @param name
     * @return
     */
    public boolean hasSetter(String name) {
        return objectWrapper.hasSetter(name);
    }

    /**
     * 是否有指定的getter
     * @param name
     * @return
     */
    public boolean hasGetter(String name) {
        return objectWrapper.hasGetter(name);
    }


    /**
     * 获取value
     *
     * @param name
     * @return
     */
    public Object getValue(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                // 如果上层就是null了，那就结束，返回null
                return null;
            } else {
                // 否则继续看下一层，递归调用getValue
                return metaValue.getValue(prop.getChildren());
            }
        } else {
            return objectWrapper.get(prop);
        }
    }


    /**
     * 设置value
     *
     * @param name
     * @param value
     */
    public void setValue(String name, Object value) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                if (value == null && prop.getChildren() != null) {
                    // don't instantiate child path if value is null
                    // 如果上层就是 null 了，还得看有没有儿子，没有那就结束
                    return;
                } else {
                    // 否则还得 new 一个，委派给 ObjectWrapper.instantiatePropertyValue
                    metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
                }
            }
            // 递归调用setValue
            metaValue.setValue(prop.getChildren(), value);
        } else {
            // 到了最后一层了，所以委派给 ObjectWrapper.set
            objectWrapper.set(prop, value);
        }
    }

    /**
     *  为属性生成元对象
     * @param name
     * @return
     */
    public MetaObject metaObjectForProperty(String name) {
        // 实际是递归调用
        Object value = getValue(name);
        return MetaObject.forObject(value, objectFactory, objectWrapperFactory);
    }

    public ObjectWrapper getObjectWrapper() {
        return objectWrapper;
    }

    /**
     *  判断是否是集合
     * @return
     */
    public boolean isCollection() {
        return objectWrapper.isCollection();
    }

    /**
     *  添加属性
     * @param element
     */
    public void add(Object element) {
        objectWrapper.add(element);
    }

    /**
     *  添加属性集合
     * @param list
     * @param <E>
     */
    public <E> void addAll(List<E> list) {
        objectWrapper.addAll(list);
    }

}
