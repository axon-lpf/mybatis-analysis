package com.axon.mybatis.builder.xmls;

import com.axon.mybatis.builder.BaseBuilder;
import com.axon.mybatis.datasource.DataSourceFactory;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.session.Configuration;
import com.axon.mybatis.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

/**
 * 1. XMLConfigBuilder
 *
 * 	•	职责和作用：
 * 	•	XMLConfigBuilder 是 MyBatis 中用于解析主配置文件（mybatis-config.xml）的类。它主要负责加载和解析 MyBatis 的核心配置文件。
 * 	•	主要功能：
 * 	•	解析全局配置：如 environment、settings、typeAliases、mappers 等。
 * 	•	初始化 MyBatis 的 Configuration 对象，读取配置文件中的信息并将其设置到 Configuration 对象中。
 * 	•	解析和加载 typeAlias（类型别名）、typeHandlers（类型处理器）、mappers（映射器）等全局配置信息。
 * 	•	总结：
 * XMLConfigBuilder 的主要作用是将 MyBatis 核心配置文件（mybatis-config.xml）中的内容解析并初始化到 Configuration 对象中，为 MyBatis 的运行提供必要的配置信息。
 */
public class XMLConfigBuilder extends BaseBuilder {

    private Element root;

    /**
     * 构造函数，读取xml文件信息
     *
     * @param reader
     */
    public XMLConfigBuilder(Reader reader) {
        //调用父类初始化Configuration
        super(new Configuration());

        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析进行转换操作
     *
     * @return
     */
    public Configuration parse() {

        try {
            //设置环境
            environmentsElement(root.element("environments"));

            //解析映射器
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configuration;
    }

    private void environmentsElement(Element context) throws InstantiationException, IllegalAccessException {
        String environment = context.attributeValue("default");
        List<Element> environmentList = context.elements("environment");

        for (Element e : environmentList) {
            String id = e.attributeValue("id");
            if (environment.equals(id)) {
                //获取事务管理器
                TransactionFactory o = (TransactionFactory) typeAliasRegistry.resolveAlias(e.element("transactionManager").attributeValue("type")).newInstance();

                Element dataSourceElement = e.element("dataSource");

                //从typeAliasRegistry 找到数据源的类型， 并转换成对以后的工厂
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).newInstance();

                List<Element> propertyList = dataSourceElement.elements("property");

                Properties properties = new Properties();

                for (Element p : propertyList) {
                    properties.setProperty(p.attributeValue("name"), p.attributeValue("value"));
                }
                //初始化dataSource, 即设置dataSource
                dataSourceFactory.setProperties(properties);
                DataSource dataSource = dataSourceFactory.getDataSource();

                //对运行的环境填充数据源
                Environment.Builder builder = new Environment.Builder(id).transactionFactory(o).dataSource(dataSource);
                configuration.setEnvironment(builder.build());
            }
        }
    }

    /**
     * 解析
     *
     * @param mappers
     * @throws IOException
     * @throws DocumentException
     */
    private void mapperElement(Element mappers) throws IOException, DocumentException, ClassNotFoundException {

        List<Element> mapperList = mappers.elements("mapper");

        for (Element e : mapperList) {

            String resource = e.attributeValue("resource");
            String mapperClass = e.attributeValue("class");

            // 这里是一个xml的解析
            if (resource != null && mapperClass == null) {
                InputStream inputStream = Resources.getResourceAsStream(resource);
                XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
                mapperParser.parse();
            }
            //注解解析
            else if (resource == null && mapperClass != null) {
                Class<?> aClass = Resources.classForName(mapperClass);
                configuration.addMapper(aClass);
            }

        }
    }
}
