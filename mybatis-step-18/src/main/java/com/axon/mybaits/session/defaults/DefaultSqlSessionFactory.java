package com.axon.mybaits.session.defaults;

import com.axon.mybatis.executor.Executor;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.TransactionIsolationLevel;
import com.axon.mybatis.transaction.Transaction;
import com.axon.mybatis.transaction.TransactionFactory;

/**
 * 创建工厂类
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        Transaction tx = null;
        try {
            Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, false);
            Executor executor = configuration.newExecutor(tx);
            return new DefaultSqlSession(configuration, executor);

        } catch (Exception e) {
            try {
                assert tx != null;
                tx.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        throw new RuntimeException("Can't open session");
    }
}
