package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.builder.xmls.XMLConfigBuilder;
import com.axon.mybatis.dao.IActivityDao;
import com.axon.mybatis.enties.ActivityDO;
import com.axon.mybatis.executor.Executor;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.mapping.Environment;
import com.axon.mybatis.session.*;
import com.axon.mybatis.session.defaults.DefaultSqlSession;
import com.axon.mybatis.transaction.Transaction;
import com.axon.mybatis.transaction.TransactionFactory;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * 本章主要添加了对动态sql的解析
 *
 * 核心代码块：
 * 1.1> 添加对应的动态sql解析处理器handler 如： ifHandler、TrimHandler
 * 1.2> XMLScriptBuilder 实例化时，将解析器加入到缓存中。
 *          public XMLScriptBuilder(Configuration configuration, Element element, Class<?> parameterType) {
 *         super(configuration);
 *         this.element = element;
 *         this.parameterType = parameterType;
 *         //TODO 初始化解析器
 *         initNodeHandlerMap();
 *     }
 *     // 初始化解析器方法
 *     private void initNodeHandlerMap() {
 *         // 9种，实现其中2种 trim/where/set/foreach/if/choose/when/otherwise/bind
 *         nodeHandlerMap.put("trim", new TrimHandler(configuration, this));
 *         nodeHandlerMap.put("if", new IfHandler(configuration, this));
 *     }
 *
 * 1.3> XMLScriptBuilder核心的解析步骤
 *     public SqlSource parseScriptNode() {
 *          //TODO 核心的解析步骤
 *         List<SqlNode> contents = parseDynamicTags(element);
 *         MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
 *         SqlSource sqlSource = null;
 *         // 判断是否是是动态sql  TODO 核心点判断是动态合适静态， 动态则走动态解析的步骤
 *         if (isDynamic) {
 *             sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
 *         } else {
 *             sqlSource = new RawSqlSource(configuration, rootSqlNode, parameterType);
 *         }
 *         return sqlSource;
 *     }
 *·    //TODO 解析步骤的具体详情方法
 *     public List<SqlNode> parseDynamicTags(Element element) {
 *         List<SqlNode> contents = new ArrayList<>();
 *         List<Node> children = element.content();
 *         for (Node child : children) {
 *             if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
 *                 String data = child.getText();
 *                 TextSqlNode textSqlNode = new TextSqlNode(data);
 *                 if (textSqlNode.isDynamic()) {
 *                     contents.add(textSqlNode);
 *                     isDynamic = true;
 *                 } else {
 *                     contents.add(new StaticTextSqlNode(data));
 *                 }
 *             } else if (child.getNodeType() == Node.ELEMENT_NODE) {
 *                 String nodeName = child.getName();
 *                 NodeHandler handler = nodeHandlerMap.get(nodeName);
 *                 if (handler == null) {
 *                     throw new RuntimeException("Unknown element <" + nodeName + "> in SQL statement.");
 *                 }
 *                 handler.handleNode(element.element(child.getName()), contents);
 *                 isDynamic = true;
 *             }
 *         }
 *         return contents;
 *     }
 *
 *
 *
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);


    private SqlSession sqlSession;

    @Test
    public void test_queryActivityById() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 2. 获取映射器对象
        IActivityDao dao = sqlSession.getMapper(IActivityDao.class);
        // 3. 测试验证
        ActivityDO req = new ActivityDO();
        req.setActivityId(10005L);
        ActivityDO res = dao.queryActivityById(req);
        logger.info("测试结果：{}", JSON.toJSONString(res));
    }

    @Test
    public void test_ognl() throws OgnlException {
      /*  ActivityDO req = new ActivityDO();
        req.setActivityId(1L);
        req.setActivityName("测试活动");
        req.setActivityDesc("阿西吧");

        OgnlContext context = new OgnlContext();
        context.setRoot(req);
        Object root = context.getRoot();

        Object activityName = Ognl.getValue("activityName", context, root);
        Object activityDesc = Ognl.getValue("activityDesc", context, root);
        Object value = Ognl.getValue("activityDesc.length()", context, root);

        System.out.println(activityName + "\t" + activityDesc + " length：" + value);*/
    }


}
