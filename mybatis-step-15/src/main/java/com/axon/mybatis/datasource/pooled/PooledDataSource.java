package com.axon.mybatis.datasource.pooled;

import com.axon.mybatis.datasource.unpooled.UnpooledDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource {


    private static final Log logger = LogFactory.getLog(PooledDataSource.class);
    /**
     * 池的状态
     */
    private final PoolState state = new PoolState(this);

    private final UnpooledDataSource dataSource;

    /**
     * 活跃链接数
     */
    protected int poolMaximumActiveConnections = 10;
    /**
     * 空闲链接数
     */
    protected int poolMaximumIdleConnections = 5;
    /**
     * 在被强制返回之前,池中连接被检查的时间
     */
    protected int poolMaximumCheckoutTime = 20000;
    /**
     * 这是给连接池一个打印日志状态机会的低层次设置,还有重新尝试获得连接, 这些情况下往往需要很长时间 为了避免连接池没有配置时静默失败)。
     */
    protected int poolTimeToWait = 20000;
    /**
     * 发送到数据的侦测查询,用来验证连接是否正常工作,并且准备 接受请求。默认是“NO PING QUERY SET” ,这会引起许多数据库驱动连接由一 个错误信息而导致失败
     */
    protected String poolPingQuery = "NO PING QUERY SET";
    /**
     * 开启或禁用侦测查询
     */
    protected boolean poolPingEnabled = false;
    /**
     * 用来配置 poolPingQuery 多次时间被用一次
     */
    protected int poolPingConnectionsNotUsedFor = 0;

    private int expectedConnectionTypeCode;


    public PooledDataSource() {
        this.dataSource = new UnpooledDataSource();
    }

    /**
     * 回收链接
     *
     * 通过 pushConnection 方法将连接从活跃状态转移到空闲状态，并根据空闲连接的数量决定是否复用连接或者直接关闭连接。
     *
     * @param connection 需要回收的连接
     */
    protected void pushConnection(PooledConnection connection) throws SQLException {
        synchronized (state) {
            // 从活跃连接池中移除当前连接，表示该连接不再使用
            state.activeConnections.remove(connection);

            // 判断连接是否仍然有效
            if (connection.isValid()) {
                // 判断空闲连接池中的连接数量是否小于最大空闲连接数，且连接类型是否匹配
                if (state.idleConnections.size() < poolMaximumIdleConnections && connection.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    // 累加连接的使用时间，用于记录连接的总使用时长
                    state.accumulatedCheckoutTime += connection.getCheckoutTime();

                    // 如果连接的自动提交关闭，则回滚事务，保证连接状态一致性
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }

                    // 创建一个新的连接对象，但复用原有的真实连接
                    PooledConnection newPooledConnection = new PooledConnection(connection.getRealConnection(), this);

                    // 将新连接加入空闲连接池
                    state.idleConnections.add(newPooledConnection);

                    // 保留旧连接的创建时间和最后使用时间
                    newPooledConnection.setCreatedTimestamp(connection.getCreatedTimestamp());
                    newPooledConnection.setLastUsedTimestamp(connection.getLastUsedTimestamp());

                    // 将旧的连接标记为无效，防止再次使用
                    connection.invalidate();
                    logger.info(" Returned Connection " + newPooledConnection.getRealHashCode() + " to pool");

                    // 通知其他等待线程连接池状态发生变化，其他线程可以尝试获取连接
                    state.notifyAll();
                } else {
                    // 如果空闲连接池已满，直接关闭连接，释放资源
                    state.accumulatedCheckoutTime += connection.getCheckoutTimestamp();

                    // 回滚事务，保证连接一致性
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }

                    // 关闭真实数据库连接
                    connection.getRealConnection().close();
                    logger.info(" Closed Connection " + connection.getRealHashCode() + " to pool");
                    connection.invalidate(); // 将连接标记为无效
                }
            } else {
                logger.info(" A bad Connection " + connection.getRealHashCode() + " to pool");
                // 如果连接无效，增加坏连接计数器
                state.badConnectionCount++;
            }
        }
    }

    /**
     * 获取链接
     *
     * @param userName 数据库用户名
     * @param password 数据库密码
     * @return PooledConnection 对象
     */
    private PooledConnection popConnection(String userName, String password) throws SQLException {
        boolean countedWait = false; // 用于跟踪是否已经等待过
        PooledConnection conn = null; // 初始化连接为空
        long t = System.currentTimeMillis(); // 获取当前时间，用于计算等待时间
        int localBadConnectionCount = 0; // 记录当前循环中的坏连接数量

        while (conn == null) {
            synchronized (state) {
                // 如果空闲连接池不为空，从空闲连接池获取连接
                if (!state.idleConnections.isEmpty()) {
                    conn = state.idleConnections.remove(0);
                    logger.info(" checked out Connection " + conn.getRealHashCode() + " from pool");
                }
                // 如果没有空闲连接
                else {
                    // 如果活跃连接数小于最大活跃连接数，创建新连接
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {
                        conn = new PooledConnection(dataSource.getConnection(), this);
                        logger.info(" create Connection " + conn.getRealHashCode() + ".");
                    }
                    // 如果活跃连接池已满
                    else {
                        // 获取活跃连接池中最老的连接
                        PooledConnection oldPooledConnection = state.activeConnections.get(0);
                        long checkoutTime = oldPooledConnection.getCheckoutTime();

                        // 如果最老的连接使用时间超过最大允许使用时间，回收该连接
                        if (checkoutTime - t > poolMaximumCheckoutTime) {
                            state.claimedOverdueConnectionCount++; // 过期连接计数器加1
                            state.accumulatedCheckoutTimeOfOverdueConnections++; // 累加过期连接的使用时间
                            state.accumulatedCheckoutTime += checkoutTime;

                            // 从活跃连接池中移除该连接
                            state.activeConnections.remove(oldPooledConnection);

                            // 回滚事务，确保状态一致性
                            if (!oldPooledConnection.getRealConnection().getAutoCommit()) {
                                oldPooledConnection.getRealConnection().rollback();
                            }

                            // 创建新的连接复用旧的真实连接
                            conn = new PooledConnection(oldPooledConnection.getRealConnection(), this);
                            oldPooledConnection.invalidate(); // 将旧连接标记为无效
                            logger.info(" Claimed overdue Connection " + conn.getRealHashCode() + "...");
                        }
                        // 如果最老连接未超时，则等待连接可用
                        else {
                            try {
                                // 如果还没有记录等待时间，增加等待计数器
                                if (!countedWait) {
                                    state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                logger.info(" waiting as  long as " +poolTimeToWait + " milliseconds for connection...");

                                // 进入等待，直到有连接可用或者超时
                                state.wait(poolTimeToWait);

                                state.accumulatedWaitTime += System.currentTimeMillis() - t; // 累加等待时间
                            } catch (Exception e) {
                                break; // 捕获异常，退出等待
                            }
                        }
                    }
                }

                // 如果成功获取连接，检查连接是否有效
                if (conn != null) {
                    if (conn.isValid()) {
                        // 回滚事务，确保状态一致性
                        if (!conn.getRealConnection().getAutoCommit()) {
                            conn.getRealConnection().rollback();
                        }

                        // 设置连接的类型编码，用于标识连接的数据库来源
                        conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), userName, password));

                        // 记录连接的获取时间和最后使用时间
                        conn.setCheckoutTimestamp(System.currentTimeMillis());
                        conn.setLastUsedTimestamp(System.currentTimeMillis());

                        // 将连接加入活跃连接池
                        state.activeConnections.add(conn);

                        // 增加连接请求计数
                        state.requestCount++;
                        // 记录连接请求的总时间
                        state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    } else {

                        logger.info("a bad Connection " + conn.getRealHashCode() + "...");

                        // 如果连接无效，增加坏连接计数，并将连接置为空以重新获取
                        state.badConnectionCount++;
                        localBadConnectionCount++;
                        conn = null;

                        // 如果连续获取坏连接次数超过最大空闲连接数 + 3，抛出异常
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            throw new RuntimeException("获取数据库链接异常");
                        }
                    }
                }
            }
        }
        return conn; // 返回获取到的有效连接
    }

    /**
     * 测试连接的有效性
     *
     * @param conn 需要测试的连接
     * @return boolean 表示连接是否有效
     */
    protected boolean pingConnection(PooledConnection conn) {
        boolean result = true;

        try {
            // 检查连接是否已关闭，如果关闭则返回 false
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException e) {
            result = false;
        }

        // 如果连接仍然有效且启用了 ping 功能
        if (result) {
            if (poolPingEnabled) {
                // 如果连接未使用的时间超过指定的阈值，进行 ping 操作
                if (poolPingConnectionsNotUsedFor >= 0 && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                    try {
                        // 打印日志，表示正在测试该连接
                        logger.info("Testing connection " + conn.getRealHashCode() + " ...");

                        // 执行 ping 查询，确保连接正常
                        Connection realConn = conn.getRealConnection();
                        Statement statement = realConn.createStatement();
                        ResultSet resultSet = statement.executeQuery(poolPingQuery);
                        resultSet.close();

                        // 回滚事务，确保状态一致性
                        if (!realConn.getAutoCommit()) {
                            realConn.rollback();
                        }

                        // 记录连接正常的日志
                        result = true;
                        logger.info("Connection " + conn.getRealHashCode() + " is GOOD!");
                    } catch (Exception e) {
                        // 如果 ping 查询失败，关闭连接并记录日志
                        logger.info("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                        try {
                            conn.getRealConnection().close();
                        } catch (SQLException ignore) {
                        }
                        result = false; // 将连接标记为无效
                        logger.info("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
                    }
                }
            }
        }

        return result; // 返回连接是否有效
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUserName(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public void setLoginTimeout(int loginTimeout) throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUserName(String userName) {
        dataSource.setUserName(userName);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }


    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        dataSource.setAutoCommit(defaultAutoCommit);
        forceCloseAll();
    }

    public int getPoolMaximumActiveConnections() {
        return poolMaximumActiveConnections;
    }

    public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
        this.poolMaximumActiveConnections = poolMaximumActiveConnections;
    }

    public int getPoolMaximumIdleConnections() {
        return poolMaximumIdleConnections;
    }

    public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
        this.poolMaximumIdleConnections = poolMaximumIdleConnections;
    }

    public int getPoolMaximumCheckoutTime() {
        return poolMaximumCheckoutTime;
    }

    public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
        this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
    }

    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
    }

    public String getPoolPingQuery() {
        return poolPingQuery;
    }

    public void setPoolPingQuery(String poolPingQuery) {
        this.poolPingQuery = poolPingQuery;
    }

    public boolean isPoolPingEnabled() {
        return poolPingEnabled;
    }

    public void setPoolPingEnabled(boolean poolPingEnabled) {
        this.poolPingEnabled = poolPingEnabled;
    }

    public int getPoolPingConnectionsNotUsedFor() {
        return poolPingConnectionsNotUsedFor;
    }

    public void setPoolPingConnectionsNotUsedFor(int poolPingConnectionsNotUsedFor) {
        this.poolPingConnectionsNotUsedFor = poolPingConnectionsNotUsedFor;
    }

    public int getExpectedConnectionTypeCode() {
        return expectedConnectionTypeCode;
    }

    public void setExpectedConnectionTypeCode(int expectedConnectionTypeCode) {
        this.expectedConnectionTypeCode = expectedConnectionTypeCode;
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }

    public void forceCloseAll() {
        synchronized (state) {
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUserName(), dataSource.getPassword());
            // 关闭活跃链接
            for (int i = state.activeConnections.size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.activeConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception ignore) {

                }
            }
            // 关闭空闲链接
            for (int i = state.idleConnections.size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.idleConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                } catch (Exception ignore) {

                }
            }
            logger.info("PooledDataSource forcefully closed/removed all connections.");
        }
    }


}
