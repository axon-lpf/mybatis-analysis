package com.axon.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型处理器
 */
public interface TypeHandler<T> {

    /**
     * 设置查询参数
     *
     * @param ps
     * @param i
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     */
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;


    /**
     * 获取结果
     */
    T getResult(ResultSet rs, String columnName) throws SQLException;

}
