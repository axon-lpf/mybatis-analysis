package com.axon.mybaits.session;

import com.axon.mybatis.session.ResultContext;

/**
 * 结果处理器
 */
public interface ResultHandler {

    void handleResult(ResultContext resultContext);

}
