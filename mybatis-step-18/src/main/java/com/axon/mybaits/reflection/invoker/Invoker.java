package com.axon.mybaits.reflection.invoker;

/**
 * 反射调用者
 */
public interface Invoker {

    Object invoke(Object target, Object[] args) throws Exception;

    Class<?> getType();
}


