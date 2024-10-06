package com.axon.mybaits.executor.keygen;

import com.axon.mybatis.executor.Executor;
import com.axon.mybatis.mapping.MappedStatement;

import java.sql.Statement;

/**
 *  不使用主键生成器
 */
public class NoKeyGenerator implements KeyGenerator {
    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }
}
