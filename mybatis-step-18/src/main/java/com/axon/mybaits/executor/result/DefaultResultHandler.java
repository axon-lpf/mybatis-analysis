package com.axon.mybaits.executor.result;

import com.axon.mybatis.reflection.factory.ObjectFactory;
import com.axon.mybatis.session.ResultContext;
import com.axon.mybatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * 处理结果
 */
public class DefaultResultHandler implements ResultHandler {
    private final List<Object> list;

    public DefaultResultHandler() {
        this.list = new ArrayList<>();
    }

    /**
     * 通过 ObjectFactory 反射工具类，产生特定的 List
     */
    @SuppressWarnings("unchecked")
    public DefaultResultHandler(ObjectFactory objectFactory) {
        this.list = objectFactory.create(List.class);
    }

    /**
     *  将结果集添加到集合中，然后再返回对应的结果
     * @param context
     */
    @Override
    public void handleResult(ResultContext context) {
        list.add(context.getResultObject());
    }

    /**
     *  返回结果集
     * @return
     */
    public List<Object> getResultList() {
        return list;
    }
}