package com.axon.mybatis.scripting;

import com.axon.mybatis.mapping.SqlSource;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * 脚本语言驱动
 */
public interface LanguageDriver {

    SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType);
}
