package com.axon.mybatis.executor.statement;

import com.axon.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


/**
 * 在 MyBatis 中，StatementHandler 是负责处理 SQL 语句的核心组件之一。它的主要功能和作用是负责生成和执行 SQL 语句，以及处理查询结果。StatementHandler 是 MyBatis 中负责与数据库进行交互的重要接口之一，它在 MyBatis 框架中的 SQL 执行流程中起着至关重要的作用。
 *
 * 主要功能
 *
 * 	1.	准备 SQL 语句
 * StatementHandler 的主要功能之一是通过处理 MyBatis 配置的 SQL 映射语句，将用户提供的参数动态地绑定到 SQL 中，并生成最终执行的 SQL 语句。
 * 	2.	执行 SQL
 * 它负责执行生成的 SQL 语句。无论是 INSERT、UPDATE、DELETE 还是 SELECT 类型的 SQL 语句，StatementHandler 都会根据不同的情况执行相应的操作。
 * 	3.	设置参数
 * 在执行 SQL 语句之前，StatementHandler 会将参数设置到 PreparedStatement 或 CallableStatement 中。它会处理参数的类型，确保 SQL 语句能够正确执行。
 * 	4.	处理返回结果
 * 当执行 SQL 语句后，StatementHandler 会处理返回的 ResultSet。它会根据用户的映射配置将数据库中的结果集转换成对应的对象。
 * 	5.	分页处理
 * 对于需要分页的查询操作，StatementHandler 也会进行分页 SQL 的处理，如 LIMIT 子句的加入，确保分页查询的高效执行。
 * 	6.	SQL 拦截
 * StatementHandler 是 MyBatis 插件机制中的拦截对象之一，MyBatis 允许通过拦截器对 StatementHandler 进行自定义扩展，拦截 SQL 的生成、执行等操作，从而实现动态 SQL 修改、监控等功能。
 */
public interface StatementHandler {

    /**
     * 准备语句
     */
    Statement prepare(Connection connection) throws SQLException;

    /**
     * 参数化
     */
    void parameterize(Statement statement) throws SQLException;

    /**
     * 执行查询
     */
    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;
}
