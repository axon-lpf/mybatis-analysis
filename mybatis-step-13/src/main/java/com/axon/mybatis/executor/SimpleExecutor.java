package com.axon.mybatis.executor;

import com.axon.mybatis.executor.statement.StatementHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.session.ResultHandler;
import com.axon.mybatis.session.RowBounds;
import com.axon.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SimpleExecutor extends BaseExecutor {


    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    /**
     * 具体的查询操作
     *
     * @param ms
     * @param parameter
     * @param rowBounds
     * @param resultHandler
     * @param boundSql
     * @param <E>
     * @return
     */
    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        Statement stmt = null;
        Configuration configuration = ms.getConfiguration();
        StatementHandler statementHandler = configuration.newStatementHandler(this, ms, parameter, rowBounds, resultHandler, boundSql);
        try {
            Connection connection = transaction.getConnection();
            Statement prepare = statementHandler.prepare(connection);
            // 准备语句
            stmt = prepareStatement(statementHandler);
            // 返回结果
            return statementHandler.query(stmt, resultHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeStatement(stmt);
        }
    }

    /**
     * 具体的更新操作
     *
     * @param ms
     * @param parameter
     * @return
     * @throws SQLException
     */
    @Override
    public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            // 新建一个 StatementHandler
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
            // 准备语句
            stmt = prepareStatement(handler);
            // StatementHandler.update
            return handler.update(stmt);
        } finally {
            closeStatement(stmt);
        }

    }


    private Statement prepareStatement(StatementHandler handler) throws SQLException {
        Statement stmt;
        Connection connection = transaction.getConnection();
        // 准备语句
        stmt = handler.prepare(connection);
        handler.parameterize(stmt);
        return stmt;
    }
}
