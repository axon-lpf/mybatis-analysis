package com.axon.mybatis.builder.xmls;

import com.axon.mybatis.builder.BaseBuilder;
import com.axon.mybatis.builder.MapperBuilderAssistant;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.mapping.SqlCommandType;
import com.axon.mybatis.mapping.SqlSource;
import com.axon.mybatis.scripting.LanguageDriver;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.Locale;

public class XMLStatementBuilder extends BaseBuilder {

    private String currentNamespace;
    private Element element;

    private MapperBuilderAssistant builderAssistant;


    public XMLStatementBuilder(Configuration configuration,MapperBuilderAssistant builderAssistant,  Element element) {
        super(configuration);
        this.element = element;
        this.builderAssistant=builderAssistant;
    }


    /**
     *  解析语句
     */
    public void parseStatementNode() {
        String id = element.attributeValue("id");
        // 参数类型
        String parameterType = element.attributeValue("parameterType");
        Class<?> parameterTypeClass = resolveAlias(parameterType);
        // 结果类型
        String resultType = element.attributeValue("resultType");

        // 外部应用 resultMap
        String resultMap = element.attributeValue("resultMap");

        Class<?> resultTypeClass = resolveAlias(resultType);
        // 获取命令类型(select|insert|update|delete)
        String nodeName = element.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

        // 获取默认语言驱动器
        Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
        LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);

        SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);

        // 调用助手类【本节新添加，便于统一处理参数的包装】
        builderAssistant.addMappedStatement(id,
                sqlSource,
                sqlCommandType,
                parameterTypeClass,
                resultMap,
                resultTypeClass,
                langDriver);

        // 添加解析 SQL
       // configuration.addMappedStatement(mappedStatement);
    }

}
