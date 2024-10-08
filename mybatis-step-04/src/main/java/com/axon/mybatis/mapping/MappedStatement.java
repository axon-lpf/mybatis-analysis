package com.axon.mybatis.mapping;

import com.axon.mybatis.session.Configuration;

import java.util.Map;


/**
 * MappedStatement 的主要作用和功能：
 *
 * 	1.	保存 SQL 语句及其相关配置：
 * MappedStatement 对应每一条具体的 SQL 语句。它保存了 SQL 语句本身，以及该语句的类型（SELECT、INSERT、UPDATE、DELETE）、参数类型、返回值类型等信息。
 * 	2.	存储和维护 SQL 的执行元数据：
 * 每个 SQL 语句在 MyBatis 中执行时都伴随着一些元数据，例如参数映射、结果映射、缓存信息等。MappedStatement 会保存这些元数据信息，以便在运行时执行这些 SQL 语句。
 * 	3.	管理 SQL 语句的执行模式：
 * MappedStatement 还会管理 SQL 的执行模式，比如是否开启了缓存、SQL 的执行超时、是否支持自动生成的主键等。这些信息对于 MyBatis 的执行器（Executor）和语句处理器（StatementHandler）非常重要。
 * 	4.	与 Mapper 方法关联：
 * 每个 MappedStatement 通常是通过 MyBatis 的 Mapper 接口方法与具体的 SQL 语句相对应的。当你调用某个 Mapper 方法时，MyBatis 会找到与这个方法相对应的 MappedStatement，然后根据该 MappedStatement 中保存的信息来执行 SQL 语句。
 * 	5.	缓存控制：
 * MappedStatement 也保存了缓存相关的信息，比如是否启用了一级缓存（会话缓存）或二级缓存（跨会话的缓存），以及缓存策略的具体配置。这样 MyBatis 可以根据配置决定是否启用缓存，以及如何管理缓存。
 */
public class MappedStatement {


    /**
     * 禁止使用构造器
     */
    private MappedStatement() {

    }

    /**
     * 建造之模式
     */
    public static class Builder {

        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, String parameterType, String resultType, String sql, Map<Integer, String> parameter) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.parameterType = parameterType;
            mappedStatement.resultType = resultType;
            mappedStatement.sql = sql;
            mappedStatement.parameter = parameter;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }

    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<Integer, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<Integer, String> parameter) {
        this.parameter = parameter;
    }

    private Configuration configuration;
    private String id;
    private SqlCommandType sqlCommandType;

    private String parameterType;
    private String resultType;
    private String sql;
    private Map<Integer, String> parameter;


}
