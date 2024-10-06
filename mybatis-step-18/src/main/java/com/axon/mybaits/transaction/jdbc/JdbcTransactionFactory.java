package com.axon.mybaits.transaction.jdbc;


import com.axon.mybatis.session.TransactionIsolationLevel;
import com.axon.mybatis.transaction.Transaction;
import com.axon.mybatis.transaction.TransactionFactory;
import com.axon.mybatis.transaction.jdbc.JdbcTransaction;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * jdbc的事物创建
 */
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }


    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }

}
