package com.axon.mybatis.executor;

import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.session.ResultHandler;
import com.axon.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required);

    void close(boolean forceRollback);


}
