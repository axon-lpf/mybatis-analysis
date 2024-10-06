package com.axon.mybatis.session;

import com.axon.mybatis.session.ResultContext;

/**
 * 结果处理器
 */
public interface ResultHandler {

    void handleResult(ResultContext resultContext);

}
