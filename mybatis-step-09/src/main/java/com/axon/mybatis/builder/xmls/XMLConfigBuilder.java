package com.axon.mybatis.builder.xmls;

import com.axon.mybatis.builder.BaseBuilder;
import com.axon.mybatis.datasource.DataSourceFactory;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.mapping.BoundSql;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.mapping.MappedStatement;
import com.axon.mybatis.mapping.SqlCommandType;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            InputStream inputStream = Resources.getResourceAsStream(resource);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
            mapperParser.parse();
        }
    }
}
