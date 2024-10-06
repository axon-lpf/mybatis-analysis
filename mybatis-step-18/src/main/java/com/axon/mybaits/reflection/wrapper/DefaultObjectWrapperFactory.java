package com.axon.mybaits.reflection.wrapper;

import com.axon.mybatis.reflection.MetaObject;
import com.axon.mybatis.reflection.wrapper.ObjectWrapper;
import com.axon.mybatis.reflection.wrapper.ObjectWrapperFactory;


/**
 * 默认的对象构建工厂
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {


    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        throw new RuntimeException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }
}
