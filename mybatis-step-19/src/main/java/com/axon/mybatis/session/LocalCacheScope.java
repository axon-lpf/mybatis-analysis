package com.axon.mybatis.session;

public enum LocalCacheScope {

    /**
     * 默认值，缓存一个会话中执行的所有查询
     */
    SESSION,
    /**
     * 本地会话仅用在语句执行上，对相同 SqlSession 的不同调用将不做数据共享
     */
    STATEMENT
}
