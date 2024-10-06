package com.axon.mybaits.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transaction {

    /**
     * 获取链接
     *
     * @return
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交事物
     *
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * 回滚事务
     *
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * 关闭
     *
     * @throws SQLException
     */
    void close() throws SQLException;
}
