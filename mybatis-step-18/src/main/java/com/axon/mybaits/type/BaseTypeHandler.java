package com.axon.mybaits.type;

import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.type.JdbcType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型处理器的抽象方法，具体的实现，由其子类实现
 *
 * @param <T>
 */
public abstract class BaseTypeHandler<T> implements TypeHandler<T> {

    protected Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {

        setNonNullParameter(ps, i, parameter, jdbcType);
    }


    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        return getNullableResult(rs, columnName);

    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getNullableResult(rs, columnIndex);
    }

    /**
     * 由具体的子类进行实现
     *
     * @param ps
     * @param i
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     */
    protected abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;


    /**
     * 结果集的处理
     * @param rs
     * @param columnName
     * @return
     * @throws SQLException
     */
    protected abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;



    protected abstract T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

}
