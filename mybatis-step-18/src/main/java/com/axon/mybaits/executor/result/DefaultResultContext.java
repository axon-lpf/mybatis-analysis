package com.axon.mybaits.executor.result;

import com.axon.mybatis.session.ResultContext;


/**
 * 获取结果
 */
public class DefaultResultContext implements ResultContext {


    private Object resultObject;
    private int resultCount;

    public DefaultResultContext() {
        this.resultObject = null;
        this.resultCount = 0;
    }

    @Override
    public Object getResultObject() {
        return resultObject;
    }

    @Override
    public int getResultCount() {
        return resultCount;
    }

    public void nextResultObject(Object resultObject) {
        resultCount++;
        this.resultObject = resultObject;
    }
}
