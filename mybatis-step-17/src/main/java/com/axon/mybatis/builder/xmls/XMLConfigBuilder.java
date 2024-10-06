package com.axon.mybatis.builder.xmls;

import com.axon.mybatis.builder.BaseBuilder;
import com.axon.mybatis.datasource.DataSourceFactory;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.plugin.Interceptor;
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
            //解析插件的拦截器链
            pluginElement(root.element("plugins"));
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


    /**
     * 解析插件拦截器
     *
     * @param parent
     * @throws Exception
     */
    private void pluginElement(Element parent) throws Exception {
        if (parent == null) return;
        List<Element> elements = parent.elements();
        for (Element element : elements) {
            String interceptor = element.attributeValue("interceptor");
            // 参数配置
            Properties properties = new Properties();
            List<Element> propertyElementList = element.elements("property");
            for (Element property : propertyElementList) {
                properties.setProperty(property.attributeValue("name"), property.attributeValue("value"));
            }
            // 获取插件实现类并实例化：cn.bugstack.mybatis.test.plugin.TestPlugin
            Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
            interceptorInstance.setProperties(properties);
            configuration.addInterceptor(interceptorInstance);
        }
    }
}
