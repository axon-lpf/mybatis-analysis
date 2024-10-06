package com.axon.mybatis.executor.statement;

import com.axon.mybatis.executor.Executor;
import com.axon.mybatis.executor.parameter.ParameterHandler;
import com.axon.mybatis.executor.resultset.ResultSetHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.session.ResultHandler;
import com.axon.mybatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public abstract class BaseStatementHandler implements StatementHandler {

    protected Configuration configuration = null;

    protected final Executor executor;

    protected final MappedStatement mappedStatement;

    protected final Object parameterObject;

    /**
     *  结果处理器
     */
    protected final ResultSetHandler resultSetHandler;

    /**
     *  参数处理器
     */
    protected final ParameterHandler parameterHandler;

    protected BoundSql boundSql;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.parameterObject = parameterObject;
        if (boundSql == null) {
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }
        this.boundSql = boundSql;
        this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, rowBounds, resultHandler, boundSql);

    }


    @Override
    public Statement prepare(Connection connection) throws SQLException {
        Statement statement = null;
        try {
            statement = instantiateStatement(connection);
            statement.setQueryTimeout(350);
            statement.setFetchSize(10000);
            return statement;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    @Override
    public void parameterize(Statement statement) throws SQLException {

    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        return Collections.emptyList();
    }

    @Override
    public int update(Statement statement) throws SQLException {
        return 0;
    }
}
