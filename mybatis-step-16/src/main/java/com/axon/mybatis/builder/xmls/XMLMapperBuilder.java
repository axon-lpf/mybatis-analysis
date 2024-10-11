package com.axon.mybatis.builder.xmls;

import com.axon.mybatis.builder.BaseBuilder;
import com.axon.mybatis.builder.MapperBuilderAssistant;
import com.axon.mybatis.builder.ResultMapResolver;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.mapping.ResultFlag;
import com.axon.mybatis.mapping.ResultMap;
import com.axon.mybatis.mapping.ResultMapping;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 2. XMLMapperBuilder
 *
 * 	•	职责和作用：
 * 	•	XMLMapperBuilder 是 MyBatis 中用于解析映射文件（Mapper.xml）的类，负责加载和解析每个映射文件中的 SQL 语句定义、结果映射等信息。
 * 	•	主要功能：
 * 	•	解析 <mapper> 标签：处理 Mapper 映射文件中的 SQL 语句定义、<resultMap>、<parameterMap>、<sql> 等标签。
 * 	•	将解析到的 SQL 语句（如 select、insert、update、delete）注册到 Configuration 对象中，以便在执行时可以找到对应的 SQL 语句。
 * 	•	总结：
 * XMLMapperBuilder 负责解析每个 Mapper.xml 文件中的 SQL 定义、结果映射等信息，并将它们加载到 MyBatis 的 Configuration 中，用于后续 SQL 执行时的映射和操作。
 */
public class XMLMapperBuilder extends BaseBuilder {

    private Element element;
    private String resource;
    private String currentNamespace;

    // 映射器构建助手
    private MapperBuilderAssistant builderAssistant;

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) throws DocumentException {
        this(new SAXReader().read(inputStream), configuration, resource);
    }

    private XMLMapperBuilder(Document document, Configuration configuration, String resource) {
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
        this.element = document.getRootElement();
        this.resource = resource;
    }

    /**
     * 解析
     */
    public void parse() throws ClassNotFoundException {
        //确认当前没有加载过资源，防止重复加载
        if (!configuration.isResourceLoaded(resource)) {
            configurationElement(element);
            configuration.addLoadedResource(resource);
            configuration.addMapper(Resources.classForName(builderAssistant.getCurrentNamespace()));

        }
    }

    /**
     * 这里是在配置mapper元素
     *
     * @param element
     */
    private void configurationElement(Element element) {

        currentNamespace = element.attributeValue("namespace");
        if (currentNamespace.equals("")) {
            throw new RuntimeException("namespace attribute is empty");
        }

        builderAssistant.setCurrentNamespace(currentNamespace);
        //2.解析ResultMap
        resultMapElements(element.elements("resultMap"));

        // 配置select、 insert、 update、 delete 语句
        buildStatementFromContext(element.elements("select"), element.elements("insert"), element.elements("update"), element.elements("delete"));
    }


    private void resultMapElements(List<Element> list) {
        for (Element element : list) {
            try {
                resultMapElement(element, Collections.emptyList());
            } catch (Exception ignore) {
            }
        }
    }


    /**
     * <resultMap id="activityMap" type="cn.bugstack.mybatis.test.po.Activity">
     * <id column="id" property="id"/>
     * <result column="activity_id" property="activityId"/>
     * <result column="activity_name" property="activityName"/>
     * <result column="activity_desc" property="activityDesc"/>
     * <result column="create_time" property="createTime"/>
     * <result column="update_time" property="updateTime"/>
     * </resultMap>
     */
    private ResultMap resultMapElement(Element resultMapNode, List<ResultMapping> additionalResultMappings) throws Exception {
        String id = resultMapNode.attributeValue("id");
        String type = resultMapNode.attributeValue("type");
        Class<?> typeClass = resolveClass(type);

        List<ResultMapping> resultMappings = new ArrayList<>();
        resultMappings.addAll(additionalResultMappings);

        List<Element> resultChildren = resultMapNode.elements();
        for (Element resultChild : resultChildren) {
            List<ResultFlag> flags = new ArrayList<>();
            if ("id".equals(resultChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            // 构建 ResultMapping
            resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
        }

        // 创建结果映射解析器
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, resultMappings);
        return resultMapResolver.resolve();
    }

    /**
     * <id column="id" property="id"/>
     * <result column="activity_id" property="activityId"/>
     */
    private ResultMapping buildResultMappingFromContext(Element context, Class<?> resultType, List<ResultFlag> flags) throws Exception {
        String property = context.attributeValue("property");
        String column = context.attributeValue("column");
        return builderAssistant.buildResultMapping(resultType, property, column, flags);
    }


    private void buildStatementFromContext(List<Element>... lists) {
        for (List<Element> list : lists) {
            for (Element element : list) {
                final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, element);
                statementParser.parseStatementNode();
            }
        }

    }
}
