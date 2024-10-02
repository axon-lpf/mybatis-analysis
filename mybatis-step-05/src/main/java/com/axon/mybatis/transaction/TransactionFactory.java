package com.axon.mybatis.transaction;

import com.axon.mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 创建事务的工厂
 */
public interface TransactionFactory {


    Transaction newTransaction(Connection connection);


    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel transactionLevel, boolean autoCommit);
}
