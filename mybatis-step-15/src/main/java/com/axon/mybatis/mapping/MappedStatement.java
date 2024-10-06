package com.axon.mybatis.mapping;

import com.axon.mybatis.executor.keygen.Jdbc3KeyGenerator;
import com.axon.mybatis.executor.keygen.KeyGenerator;
import com.axon.mybatis.executor.keygen.NoKeyGenerator;
import com.axon.mybatis.scripting.LanguageDriver;
import com.axon.mybatis.session.Configuration;

import java.util.List;


public class MappedStatement {

    /**
     * 禁止使用构造器
     */
    private MappedStatement() {

    }

    /**
     * 配置新消息
     */
    private Configuration configuration;
    /**
     * id
     */
    private String id;
    private SqlCommandType sqlCommandType;
    private SqlSource sqlSource;
    Class<?> resultType;
    private LanguageDriver lang;
    private List<ResultMap> resultMaps;

    //主键生成接口
    private KeyGenerator keyGenerator;
    // 主键生成配置
    private String[] keyProperties;

    private String[] keyColumns;

    private String resource;


    /**
     * 建造之模式
     */
    public static class Builder {

        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, SqlSource sqlSource, Class<?> resultType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.resultType = resultType;
            mappedStatement.keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
            mappedStatement.lang = configuration.getDefaultScriptingLanguageInstance();

        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }

        public String id() {
            return mappedStatement.id;
        }

        public Builder resultMaps(List<ResultMap> resultMaps) {
            mappedStatement.resultMaps = resultMaps;
            return this;
        }


        public Builder resource(String resource) {
            mappedStatement.resource = resource;
            return this;
        }

        public Builder keyGenerator(KeyGenerator keyGenerator) {
            mappedStatement.keyGenerator = keyGenerator;
            return this;
        }

        public Builder keyProperty(String keyProperty) {
            mappedStatement.keyProperties = delimitedStringToArray(keyProperty);
            return this;
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


    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }


    public void setBoundSql(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    private BoundSql boundSql;


    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public Class<?> getResultType() {
        return resultType;
    }


    public LanguageDriver getLang() {
        return lang;
    }


    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }

    public String[] getKeyProperties() {
        return keyProperties;
    }

    public String[] getKeyColumns() {
        return keyColumns;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    private static String[] delimitedStringToArray(String in) {
        if (in == null || in.trim().length() == 0) {
            return null;
        } else {
            return in.split(",");
        }
    }

}
