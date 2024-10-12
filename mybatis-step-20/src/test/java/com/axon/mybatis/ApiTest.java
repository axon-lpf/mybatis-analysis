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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.Reader;

/**
 * 本章主要整合了Spring，主要代码集成ccom.axon.mybatis.spring下
 *
 * 核心步骤和代码块：
 *  1.1>. 添加SqlSessionFactoryBean，并实现对应的接口  implements FactoryBean<SqlSessionFactory>, InitializingBean
 *     // TODO 核心方法 初始化解析mybatis XML,并构建到缓存中去
 *     @Override
 *     public void afterPropertiesSet() throws Exception {
 *         try (Reader reader = Resources.getResourceAsReader(resource)) {
 *             this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
 *         } catch (Exception e) {
 *             e.printStackTrace();
 *         }
 *     }
 *
 *      springConfig xml 中添加以下配置, 启动时， 则会扫描加载配置， 然后去调用SqlSessionFactoryBean 创建实例、初始化操作
 *       <bean id="sqlSessionFactory" class="com.axon.mybatis.spring.SqlSessionFactoryBean">
 *         <property name="resource" value="mybatis-config-datasource.xml"/>
 *      </bean>
 *
 * 1.2>添加 MapperScannerConfigurer扫描配置操作， 并实现 implements BeanDefinitionRegistryPostProcessor 中方法
 *
 *          public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
 *         try {
 *             String packageSearchPath = "classpath*:" + basePackage.replace('.', '/') + "/** 省略了";
 *             ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
 *             Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
 *
 *             for (Resource resource : resources) {
 *                 MetadataReader metadataReader = new SimpleMetadataReader(resource, ClassUtils.getDefaultClassLoader());
 *
 *                 ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
 *                 String beanName = Introspector.decapitalize(ClassUtils.getShortName(beanDefinition.getBeanClassName()));
 *
 *                 beanDefinition.setResource(resource);
 *                 beanDefinition.setSource(resource);
 *                 beanDefinition.setScope("singleton");
 *                 //TODO 这里也是关键点   设置MapperFactoryBean 中构造函数的第一个参数
 *                 beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName());
 *                 /TODO 这里也是关键点   设置MapperFactoryBean 中构造函数的第二个参数
 *                 beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(sqlSessionFactory);
 *                 //TODO 这里是关键点， 修改其真正的处理对象
 *                 beanDefinition.setBeanClass(MapperFactoryBean.class);
 *                 BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
 *                 registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
 *             }
 *         } catch (IOException e) {
 *             e.printStackTrace();
 *         }
 *     }
 *
 *     spring-config.xml中添加以下配置
 *          <bean class="com.axon.mybatis.spring.MapperScannerConfigurer">
 *         <!-- 注入sqlSessionFactory -->
 *         <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
 *         <!-- 给出需要扫描Dao接口包 -->
 *         <property name="basePackage" value="com.axon.mybatis.dao"/>
 *     </bean>
 *
 * 1.3>添加 MapperFactoryBean去操作 实现 FactoryBean操作
 *     //TODO 这里操作之后， 都会放到bean的缓存中去， 后续中每个dao都会有一个sqlSession 去操作数据库
 *      public class MapperFactoryBean<T> implements FactoryBean<T> {
         *     private Class<T> mapperInterface;
         *     private SqlSessionFactory sqlSessionFactory;
         *
         *     public MapperFactoryBean(Class<T> mapperInterface, SqlSessionFactory sqlSessionFactory) {
         *         this.mapperInterface = mapperInterface;
         *         this.sqlSessionFactory = sqlSessionFactory;
         *     }
         *     @Override
         *     public T getObject() throws Exception {
 *              // TODO 核心点， 这里每次调用都会去创SqlSession， 每次调用则是则创建bean实例的时候，去创建的， 保存到dao对象中去。
 *              // TODO 注意， 在实际sql语句， 每次执行sql语句之前都会创建一个Connection (或者从链接池中拉取一个空闲的Connection 去操作数据中)
 *              // TODO SqlSession 只是保存了一些操作的方法
         *         return sqlSessionFactory.openSession().getMapper(mapperInterface);
         *     }
         *
         *     @Override
         *     public Class<?> getObjectType() {
         *         return mapperInterface;
         *     }
         *
         *     @Override
         *     public boolean isSingleton() {
         *         return true;
         *     }
 * }
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

    @Test
    public void test_ClassPathXmlApplicationContext() {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("spring-config.xml");
        IActivityDao dao = beanFactory.getBean("IActivityDao", IActivityDao.class);
        ActivityDO activityDO = new ActivityDO();
        activityDO.setActivityId(10005L);
        ActivityDO res = dao.queryActivityById(activityDO);
        logger.info("测试结果：{}", JSON.toJSONString(res));

        ActivityDO activityDO1 = new ActivityDO();
        activityDO1.setActivityName("adasdklfj");
        activityDO1.setActivityId(100992L);
        dao.insert(activityDO1);

        IUserDao userDao = beanFactory.getBean("IUserDao", IUserDao.class);

        UserDO userDO = userDao.queryUserInfoById(1L);

        logger.info("测试结果：{}", JSON.toJSONString(userDO));
    }
}
