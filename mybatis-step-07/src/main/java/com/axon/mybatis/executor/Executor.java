package com.axon.mybatis.executor;

import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.session.ResultHandler;
import com.axon.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * 在 MyBatis 中，Executor 是负责执行数据库操作的核心接口，它在 MyBatis 框架中承担着调度 SQL 语句的执行、事务管理、缓存处理等重要职责。Executor 通过整合 MyBatis 的查询、更新、删除等操作，并处理一级缓存、二级缓存和事务提交等功能，确保 SQL 操作能够高效且可靠地执行。
 *
 * 主要功能和作用
 *
 * 	1.	调度 SQL 执行
 * Executor 是 MyBatis 中所有 SQL 执行的核心调度者。它负责通过 StatementHandler 执行 SQL 语句（包括 SELECT、INSERT、UPDATE、DELETE 等操作），并处理 SQL 执行结果。
 * 	2.	事务管理
 * Executor 负责管理数据库事务，包括事务的开启、提交和回滚操作。在进行多个数据库操作时，Executor 会确保这些操作在同一事务中进行，确保操作的一致性和原子性。
 * 	3.	一级缓存（Local Cache）
 * Executor 内部维护一级缓存，也叫本地缓存（Local Cache）。一级缓存是针对当前 SqlSession 的缓存，它的作用是缓存同一 SqlSession 中相同 SQL 和参数的查询结果，避免多次重复查询数据库，提高性能。
 * 	4.	二级缓存（Global Cache）
 * Executor 还支持二级缓存，也叫全局缓存（Global Cache）。二级缓存是跨 SqlSession 的，适用于同一个 Mapper 作用域下的数据共享。Executor 负责管理二级缓存的读取和写入，确保缓存数据的有效性。
 * 	5.	批量更新操作
 * Executor 提供批量执行 SQL 操作的功能，即可以通过一次数据库交互执行多条 INSERT、UPDATE 或 DELETE 操作。这在需要高效处理大量数据更新时尤为有用。
 * 	6.	延迟加载
 * Executor 还支持延迟加载功能，在查询某些关联数据时，可以先返回主表数据，关联数据的查询可以在实际需要时再执行。这种机制可以提升查询性能，避免不必要的查询。
 * 	7.	插件拦截
 * MyBatis 提供了插件机制，Executor 是可以被插件拦截的对象之一。通过拦截 Executor 的方法，开发者可以扩展 MyBatis 的功能，例如记录 SQL 日志、动态修改 SQL 等。
 */
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required);

    void close(boolean forceRollback);


}
