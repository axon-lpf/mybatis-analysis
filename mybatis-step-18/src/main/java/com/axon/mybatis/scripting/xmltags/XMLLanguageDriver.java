package com.axon.mybatis.scripting.xmltags;

import com.axon.mybatis.executor.parameter.ParameterHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.mapping.SqlSource;
import com.axon.mybatis.scripting.LanguageDriver;
import com.axon.mybatis.scripting.defaults.DefaultParameterHandler;
import com.axon.mybatis.scripting.defaults.RawSqlSource;
import com.axon.mybatis.scripting.xmltags.XMLScriptBuilder;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * '
 * xml语言驱动器的实现
 */
public class XMLLanguageDriver implements LanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }

    /**
     * 基于注解的sql语句
     *
     * @param configuration
     * @param script
     * @param parameterType
     * @return
     */
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        // 这里暂不解析动态sql
        return new RawSqlSource(configuration, script, parameterType);
    }


    /**
     * 参数处理器
     *
     * @param mappedStatement
     * @param parameterObject
     * @param boundSql
     * @return
     */
    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

}
