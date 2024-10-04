package com.axon.mybatis.scripting;

import com.axon.mybatis.executor.parameter.ParameterHandler;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.mapping.SqlSource;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * 脚本语言驱动
 */
public interface LanguageDriver {

    /**
     *  sql源码处理器
     * @param configuration
     * @param script
     * @param parameterType
     * @return
     */
    SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType);


    /**
     * 创建参数处理器
     */
    ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

}
