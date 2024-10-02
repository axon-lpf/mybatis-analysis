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

                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).newInstance();

                List<Element> propertyList = dataSourceElement.elements("property");

                Properties properties = new Properties();

                for (Element p : propertyList) {
                    properties.setProperty(p.attributeValue("name"), p.attributeValue("value"));
                }

                dataSourceFactory.setProperties(properties);
                DataSource dataSource = dataSourceFactory.getDataSource();

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
            Reader resourceAsReader = Resources.getResourceAsReader(resource);

            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new InputSource(resourceAsReader));
            Element root = document.getRootElement();

            String namespace = root.attributeValue("namespace");
            List<Element> selectNodes = root.elements("select");
            for (Element selectNode : selectNodes) {
                String id = selectNode.attributeValue("id");
                String parameterType = selectNode.attributeValue("parameterType");
                String resultType = selectNode.attributeValue("resultType");
                String sql = selectNode.getText();

                //解析sql
                Map<Integer, String> parameter = new HashMap<>();
                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                Matcher matcher = pattern.matcher(sql);

                for (int i = 1; matcher.find(); i++) {
                    String group1 = matcher.group(1);
                    String group2 = matcher.group(2);

                    parameter.put(i, group2);
                    sql = sql.replace(group1, "?");
                }

                String msId = namespace + "." + id;

                String nodeName = selectNode.getName();

                SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

                //解析sql
                BoundSql boundSql = new BoundSql(sql, parameter, parameterType, resultType);

                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, boundSql).build();
                // 添加解析 SQL
                configuration.addMappedStatement(mappedStatement);
            }
            configuration.addMapper(Resources.classForName(namespace));
        }
    }
}
