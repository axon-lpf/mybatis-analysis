package com.axon.mybatis.builder.xmls;

import com.axon.mybatis.builder.BaseBuilder;
import com.axon.mybatis.builder.MapperBuilderAssistant;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

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

        // 配置select、 insert、 update、 delete 语句
        buildStatementFromContext(element.elements("select"), element.elements("insert"), element.elements("update"), element.elements("delete"));
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
