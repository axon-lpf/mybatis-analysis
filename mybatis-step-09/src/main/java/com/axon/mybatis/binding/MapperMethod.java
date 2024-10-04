package com.axon.mybatis.binding;

import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.mapping.SqlCommandType;
import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.session.SqlSession;

import java.lang.reflect.Method;

public class MapperMethod {


    private final SqlCommand command;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.command = new SqlCommand(configuration, mapperInterface, method);
    }

    /**
     * 执行具体的sql语句
     *
     * @param sqlSession
     * @param args
     * @return
     */
    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        switch (command.getType()) {
            case INSERT:
                break;
            case DELETE:
                break;
            case UPDATE:
                break;
            case SELECT:
                result = sqlSession.selectOne(command.getName(), args);
                break;
            default:
                throw new RuntimeException("Unknown execution method for: " + command.getName());
        }
        return result;
    }


    /**
     * 这里是在处理sql指令
     */
    public static class SqlCommand {

        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            String statementName = mapperInterface.getName() + "." + method.getName();
            MappedStatement ms = configuration.getMappedStatement(statementName);
            name = ms.getId();
            type = ms.getSqlCommandType();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }

}
