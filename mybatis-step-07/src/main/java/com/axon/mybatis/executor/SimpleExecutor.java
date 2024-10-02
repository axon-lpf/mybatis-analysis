package com.axon.mybatis.executor;

import com.axon.mybatis.executor.statement.StatementHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.session.ResultHandler;
import com.axon.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class SimpleExecutor extends BaseExecutor {


    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        Configuration configuration = ms.getConfiguration();

        StatementHandler statementHandler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
        try {
            Connection connection = transaction.getConnection();
            Statement prepare = statementHandler.prepare(connection);
            statementHandler.parameterize(prepare);
            return statementHandler.query(prepare, resultHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
