package com.axon.mybatis.reflection.invoker;

import java.lang.reflect.Method;

/**
 * 反射调用者
 */
public interface Invoker {

    Object invoke(Object target, Object[] args) throws Exception;

    Class<?> getType();
}


