package com.axon.mybaits.reflection.wrapper;

import com.axon.mybatis.reflection.MetaObject;

public interface ObjectWrapperFactory {

    /**
     * 判断有没有包装器
     */
    boolean hasWrapperFor(Object object);

    /**
     * 获取包装器
     */
    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);
}
