package com.axon.mybatis.builder.xmls;

import com.axon.mybatis.builder.BaseBuilder;
import com.axon.mybatis.builder.MapperBuilderAssistant;
import com.axon.mybatis.executor.keygen.Jdbc3KeyGenerator;
import com.axon.mybatis.executor.keygen.KeyGenerator;
import com.axon.mybatis.executor.keygen.NoKeyGenerator;
import com.axon.mybatis.executor.keygen.SelectKeyGenerator;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.mapping.SqlCommandType;
import com.axon.mybatis.mapping.SqlSource;
import com.axon.mybatis.scripting.LanguageDriver;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.List;
import java.util.Locale;


/**
 * 3. XMLStatementBuilder
 * <p>
 * •	职责和作用：
 * •	XMLStatementBuilder 是 MyBatis 中用于解析具体的 SQL 语句（select、insert、update、delete 等）的类，主要负责将 SQL 语句中的配置信息解析成可执行的 MappedStatement 对象。
 * •	主要功能：
 * •	解析 <select>、<insert>、<update>、<delete> 等 SQL 语句，并将它们转换为 MappedStatement 对象，存储 SQL 语句的具体信息，如 SQL 的参数类型、返回值类型、执行类型（查询、更新等）。
 * •	XMLStatementBuilder 主要在 XMLMapperBuilder 中调用，帮助解析 SQL 语句。
 * •	总结：
 * XMLStatementBuilder 负责将每一个 SQL 语句的 XML 标签转换为可执行的 MappedStatement，并将其与 Mapper 关联，供 MyBatis 执行 SQL 使用。
 */
public class XMLStatementBuilder extends BaseBuilder {

    private String currentNamespace;
    private Element element;

    private MapperBuilderAssistant builderAssistant;


    public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, Element element) {
        super(configuration);
        this.element = element;
        this.builderAssistant = builderAssistant;
    }


    /**
     * 解析语句
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

        processSelectKeyNodes(id, parameterTypeClass, langDriver);


        SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);

        // 属性标记【仅对 insert 有用】, MyBatis 会通过 getGeneratedKeys 或者通过 insert 语句的 selectKey 子元素设置它的值 step-15 新增
        String keyProperty = element.attributeValue("keyProperty");
        KeyGenerator keyGenerator = null;
        String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);

        if (configuration.hasKeyGenerator(keyStatementId)) {
            keyGenerator = configuration.getKeyGenerator(keyStatementId);
        } else {
            keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
        }

        // 调用助手类
        builderAssistant.addMappedStatement(id,
                sqlSource,
                sqlCommandType,
                parameterTypeClass,
                resultMap,
                resultTypeClass,
                keyGenerator,
                keyProperty,
                langDriver);

        // 存放键值生成器配置
        //MappedStatement keyStatement = configuration.getMappedStatement(id);

        // 添加解析 SQL
        // configuration.addMappedStatement(mappedStatement);
    }


    private void processSelectKeyNodes(String id, Class<?> parameterTypeClass, LanguageDriver langDriver) {
        List<Element> selectKeyNodes = element.elements("selectKey");
        parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver);
    }

    private void parseSelectKeyNodes(String parentId, List<Element> list, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        for (Element nodeToHandle : list) {
            String id = parentId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
            parseSelectKeyNode(id, nodeToHandle, parameterTypeClass, languageDriver);
        }
    }


    /**
     * <selectKey keyProperty="id" order="AFTER" resultType="long">
     * SELECT LAST_INSERT_ID()
     * </selectKey>
     */
    private void parseSelectKeyNode(String id, Element nodeToHandle, Class<?> parameterTypeClass, LanguageDriver langDriver) {
        String resultType = nodeToHandle.attributeValue("resultType");
        Class<?> resultTypeClass = resolveClass(resultType);
        boolean executeBefore = "BEFORE".equals(nodeToHandle.attributeValue("order", "AFTER"));
        String keyProperty = nodeToHandle.attributeValue("keyProperty");

        // default
        String resultMap = null;
        KeyGenerator keyGenerator = new NoKeyGenerator();

        // 解析成SqlSource，DynamicSqlSource/RawSqlSource
        SqlSource sqlSource = langDriver.createSqlSource(configuration, nodeToHandle, parameterTypeClass);
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;

        // 调用助手类
        builderAssistant.addMappedStatement(id,
                sqlSource,
                sqlCommandType,
                parameterTypeClass,
                resultMap,
                resultTypeClass,
                keyGenerator,
                keyProperty,
                langDriver);

        // 给id加上namespace前缀
        id = builderAssistant.applyCurrentNamespace(id, false);

        // 存放键值生成器配置
        MappedStatement keyStatement = configuration.getMappedStatement(id);
        configuration.addKeyGenerator(id, new SelectKeyGenerator(keyStatement, executeBefore));
    }


}
