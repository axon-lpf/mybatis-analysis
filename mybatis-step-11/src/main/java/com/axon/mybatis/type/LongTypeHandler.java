package com.axon.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Long类型的处理器
 */
public class LongTypeHandler extends BaseTypeHandler<Long> {
    @Override
    protected void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter);
    }

}
