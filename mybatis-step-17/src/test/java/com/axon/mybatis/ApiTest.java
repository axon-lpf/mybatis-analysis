package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IActivityDao;
import com.axon.mybatis.enties.ActivityDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import ognl.OgnlException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 本章节主要添加了Mybatis中的插件处理
 *  核心代码：
 *  1.1> 添加  Intercepts 、Signature 注解拦截处理
 *  1.2> 创建拦截器InterceptorChain
 *      public class InterceptorChain {
 *
 *
         *     private final List<Interceptor> interceptors = new ArrayList<>();
         *
         *     public Object pluginAll(Object target) {
         *         for (Interceptor interceptor : interceptors) {
         *             target = interceptor.plugin(target);
         *         }
         *         return target;
         *     }
 *
 *              public void addInterceptor(Interceptor interceptor) {
 *                   interceptors.add(interceptor);
 *          }
 *     }
 *
 * 1.3>注册拦截器，在Configuration中，在创建Statement时，创建添加拦截器
 *     public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
 *         StatementHandler preparedStatementHandler = new PreparedStatementHandler(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
 *         // 嵌入插件，代理对象
 *         preparedStatementHandler =(StatementHandler) interceptorChain.pluginAll(preparedStatementHandler);
 *         // 这里创建出来的是一个代理对象
 *         return preparedStatementHandler;
 *     }
 *
 * 1.4> 创建自定义拦截器
 *      @Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
     * public class TestPlugin implements Interceptor {
     *     @Override
     *     public Object intercept(Invocation invocation) throws Throwable {
     *
     *         StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
     *         BoundSql boundSql = statementHandler.getBoundSql();
     *         String sql = boundSql.getSql();
     *         System.out.println("拦截sql" + sql);
     *
     *         return invocation.proceed();
     *     }
     *
     *     @Override
     *     public void setProperties(Properties properties) {
     *         System.out.println("参数输出" + properties.getProperty("test00"));
     *     }
 *      }
 *
 *
 *
 *
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
