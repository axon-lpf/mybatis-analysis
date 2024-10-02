package com.axon.mybatis.session.defaults;

import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 定义执行sql语句执行的标准
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你的操作被代理了！" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        Environment environment = configuration.getEnvironment();

        try {
            Connection connection = environment.getDataSource().getConnection();
            BoundSql boundSql = mappedStatement.getBoundSql();
            PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
            preparedStatement.setLong(1, Long.parseLong(((Object[]) parameter)[0].toString()));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> objects = resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
            return objects.get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }


    /**
     * 对类型的转换操作
     *
     * @param resultSet
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> list = new ArrayList<>();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                T obj = (T) clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    String columnName = metaData.getColumnName(i);
                    String setMethodName = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method;
                    // 数据库中是BigInteger,这里需要强制转换
                    if (value instanceof BigInteger) {
                        method = clazz.getMethod(setMethodName, Long.class); // 处理Long类型的setter方法
                        value = ((BigInteger) value).longValue(); // 将BigInteger转换为Long
                    } else if (value instanceof Timestamp) {
                        method = clazz.getMethod(setMethodName, Date.class); // 处理时间类型
                    } else {
                        method = clazz.getMethod(setMethodName, value.getClass()); // 处理其他类型
                    }
                    method.invoke(obj, value);
                }
                list.add(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
