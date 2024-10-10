package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IActivityDao;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.enties.ActivityDO;
import com.axon.mybatis.enties.UserDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 本章节主要添加了对ResultMap的解析和处理
 * 核心代码块：
 *
 *  1.1>XMLMapperBuilder 添加对resultMap中的属性解析
 *          private void configurationElement(Element element) {
     *         currentNamespace = element.attributeValue("namespace");
     *         if (currentNamespace.equals("")) {
     *             throw new RuntimeException("namespace attribute is empty");
     *         }
     *
     *         builderAssistant.setCurrentNamespace(currentNamespace);
     *         //2.解析ResultMap
     *         resultMapElements(element.elements("resultMap"));
     *
     *         // 配置select、 insert、 update、 delete 语句
     *         buildStatementFromContext(element.elements("select"), element.elements("insert"), element.elements("update"), element.elements("delete"));
 *     }
 *
 *  1.2> 解析并添加对应的resultMappings的缓存中去
 *          private ResultMap resultMapElement(Element resultMapNode, List<ResultMapping> additionalResultMappings) throws Exception {
     *         String id = resultMapNode.attributeValue("id");
     *         String type = resultMapNode.attributeValue("type");
     *         Class<?> typeClass = resolveClass(type);
     *
     *         List<ResultMapping> resultMappings = new ArrayList<>();
     *         resultMappings.addAll(additionalResultMappings);
     *
     *         List<Element> resultChildren = resultMapNode.elements();
     *         for (Element resultChild : resultChildren) {
     *             List<ResultFlag> flags = new ArrayList<>();
     *             if ("id".equals(resultChild.getName())) {
     *                 flags.add(ResultFlag.ID);
     *             }
     *             // 构建 ResultMapping
     *             resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
     *         }
     *
     *         // 创建结果映射解析器
     *         ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, resultMappings);
     *         return resultMapResolver.resolve();
 *     }
 *
 *  1.3> XMLStatementBuilder 中添加对resultMap的解析
 *          public void parseStatementNode() {
 *         String id = element.attributeValue("id");
 *         // 参数类型
 *         String parameterType = element.attributeValue("parameterType");
 *         Class<?> parameterTypeClass = resolveAlias(parameterType);
 *         // 结果类型
 *         String resultType = element.attributeValue("resultType");
 *
 *         //TODO 获取resultMap所对应的值
 *         String resultMap = element.attributeValue("resultMap");
 *
 *         Class<?> resultTypeClass = resolveAlias(resultType);
 *         // 获取命令类型(select|insert|update|delete)
 *         String nodeName = element.getName();
 *         SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
 *
 *         // 获取默认语言驱动器
 *         Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
 *         LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);
 *
 *         SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);
 *
 *         // 调用助手类【本节新添加，便于统一处理参数的包装】
 *         builderAssistant.addMappedStatement(id,
 *                 sqlSource,
 *                 sqlCommandType,
 *                 parameterTypeClass,
 *                 resultMap,
 *                 resultTypeClass,
 *                 langDriver);
 *     }
 *
 * 1.4>  builderAssistant添加对resultMap的解析
 *      public MappedStatement addMappedStatement(
 *             String id,
 *             SqlSource sqlSource,
 *             SqlCommandType sqlCommandType,
 *             Class<?> parameterType,
 *             String resultMap,
 *             Class<?> resultType,
 *             LanguageDriver lang
 *     ) {
 *         // 给id加上namespace前缀：com.axon..mybatis.dao.IUserDao.queryUserInfoById
 *         id = applyCurrentNamespace(id, false);
 *         MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlCommandType, sqlSource, resultType);
 *         // TODO 放入到statementBuilder 中去
 *         setStatementResultMap(resultMap, resultType, statementBuilder);
 *         MappedStatement statement = statementBuilder.build();
 *         // 映射语句信息，建造完存放到配置项中
 *         configuration.addMappedStatement(statement);
 *         return statement;
 *     }
 *
 *1.5> 添加设置  TODO 放入到statementBuilder 中去
 *     private void setStatementResultMap(
 *             String resultMap,
 *             Class<?> resultType,
 *             MappedStatement.Builder statementBuilder) {
 *         // 因为暂时还没有在 Mapper XML 中配置 Map 返回结果，所以这里返回的是 null
 *         resultMap = applyCurrentNamespace(resultMap, true);
 *
 *         List<ResultMap> resultMaps = new ArrayList<>();
 *
 *         if (resultMap != null) {
 *             String[] resultMapNames = resultMap.split(",");
 *             for (String resultMapName : resultMapNames) {
 *                 resultMaps.add(configuration.getResultMap(resultMapName.trim()));
 *             }
 *         }
 *         else if (resultType != null) {
 *             ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(
 *                     configuration,
 *                     statementBuilder.id() + "-Inline",
 *                     resultType,
 *                     new ArrayList<>());
 *             resultMaps.add(inlineResultMapBuilder.build());
 *         }
 *         statementBuilder.resultMaps(resultMaps);
 *     }
 *
 *
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);


    private SqlSession sqlSession;

    @Before
    public void init() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        sqlSession = sqlSessionFactory.openSession();
    }

    @Test
    public void test_queryActivityById() {
        // 1. 获取映射器对象
        IActivityDao dao = sqlSession.getMapper(IActivityDao.class);
        // 2. 测试验证
        ActivityDO res = dao.queryActivityById(100001L);
        logger.info("测试结果：{}", JSON.toJSONString(res));
    }


}
