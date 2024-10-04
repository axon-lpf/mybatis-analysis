package com.axon.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * String 类型的处理器
 */
public class StringTypeHandler implements TypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }
}
