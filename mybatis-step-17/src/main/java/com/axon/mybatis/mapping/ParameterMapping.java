package com.axon.mybatis.mapping;

import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.type.JdbcType;
import com.axon.mybatis.type.TypeHandler;
import com.axon.mybatis.type.TypeHandlerRegistry;


/**
 * 参数映射处理
 */
public class ParameterMapping {

    private Configuration configuration;

    /**
     * 属性值
     */
    private String property;

    /**
     * 对应的java类型
     */
    private Class<?> javaType = Object.class;
    /**
     * 对应的jdbc的类型
     */
    private JdbcType jdbcType;


    private TypeHandler<?> typeHandler;

    private ParameterMapping() {
    }

    public static class Builder {

        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
        }

        public Builder javaType(Class<?> javaType) {
            parameterMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            parameterMapping.jdbcType = jdbcType;
            return this;
        }

        public ParameterMapping build() {
            if (parameterMapping.typeHandler == null && parameterMapping.javaType != null) {
                Configuration configuration = parameterMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                // 根据不同的类型，获取对应的typeHandler
                parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType, parameterMapping.jdbcType);
            }

            return parameterMapping;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }


    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }
}
