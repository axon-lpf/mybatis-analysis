package com.axon.mybatis.scripting.xmltags;

import com.axon.mybatis.mapping.SqlSource;
import com.axon.mybatis.scripting.LanguageDriver;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Element;

/**'
 *  xml语言驱动器的实现
 *
 */
public class XMLLanguageDriver implements LanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }
}
