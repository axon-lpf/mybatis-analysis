package com.axon.mybatis.springboot.autoconfigure;

import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import com.axon.mybatis.springboot.spring.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class})  //如果 SqlSessionFactory 类不存在，那么这个 MybatisAutoConfiguration 配置类将不会被加载。  前提是必须被加载之后，才能执行该类型
@EnableConfigurationProperties(MybatisProperties.class)  //解的作用是 启用某个 @ConfigurationProperties 类的功能，并将其注入到 Spring 容器中。它告诉 Spring Boot 自动将 MybatisProperties 类的配置绑定到配置文件中的对应属性，并将该类的实例作为 Bean 注入到 Spring 容器中。
public class MybatisAutoConfiguration implements InitializingBean {

    @Bean
    @ConditionalOnMissingBean  // 用于防止重复创建 Bean，确保在容器中没有同类型的 Bean 时，才注册默认的 Bean。它使得 Spring Boot 的自动配置更加灵活，允许开发者自定义覆盖框架的默认行为。
    public SqlSessionFactory sqlSessionFactory(MybatisProperties mybatisProperties) throws Exception {

        Document document = DocumentHelper.createDocument();

        Element configuration = document.addElement("configuration");

        Element environments = configuration.addElement("environments");
        environments.addAttribute("default", "development");

        Element environment = environments.addElement("environment");
        environment.addAttribute("id", "development");
        environment.addElement("transactionManager").addAttribute("type", "JDBC");

        Element dataSource = environment.addElement("dataSource");
        dataSource.addAttribute("type", "POOLED");

        dataSource.addElement("property").addAttribute("name", "driver").addAttribute("value", mybatisProperties.getDriver());
        dataSource.addElement("property").addAttribute("name", "url").addAttribute("value", mybatisProperties.getUrl());
        dataSource.addElement("property").addAttribute("name", "userName").addAttribute("value", mybatisProperties.getUserName());
        dataSource.addElement("property").addAttribute("name", "password").addAttribute("value", mybatisProperties.getPassword());

        Element mappers = configuration.addElement("mappers");
        mappers.addElement("mapper").addAttribute("resource", mybatisProperties.getMapperLocations());

        return new SqlSessionFactoryBuilder().build(document);
    }

    /**
     * 这个类实现了两个接口 EnvironmentAware 和 ImportBeanDefinitionRegistrar，它的作用是 将 MyBatis 的 MapperScannerConfigurer 注册到 Spring 容器中。
     */
    public static class AutoConfiguredMapperScannerRegistrar implements EnvironmentAware, ImportBeanDefinitionRegistrar {

        private String basePackage;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
            builder.addPropertyValue("basePackage", basePackage);
            ///它的作用是 将 MyBatis 的 MapperScannerConfigurer的定义注册到 Spring 容器中。
            registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
        }

        @Override
        public void setEnvironment(Environment environment) {
            //在这里，setEnvironment() 方法从环境变量中读取了 mybatis.datasource.base-dao-package 属性，并将它赋值给 basePackage 变量。这个变量将用于指定要扫描的 DAO 接口所在的包路径。
            //Environment 是 Spring 的配置环境对象，可以通过它获取应用中的配置信息，比如 application.properties 或 application.yml
            this.basePackage = environment.getProperty("mybatis.datasource.base-dao-package");
        }
    }

    /**
     *  备用配置，用于在特定条件下（当 MapperFactoryBean 和 MapperScannerConfigurer 不存在时）进行额外的配置。
     *
     *  这个注解表示 只有当 Spring 容器中没有 MapperFactoryBean 和 MapperScannerConfigurer 这两个 Bean 的定义时，才会加载 MapperScannerRegistrarNotFoundConfiguration。
     * 	•	目的：通过这种方式，Spring Boot 可以确保在没有手动配置 MyBatis 相关 Bean 时，自动进行必要的配置，避免 Bean 冲突或重复定义。
     */
    @Configuration
    @Import(AutoConfiguredMapperScannerRegistrar.class)
    @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

        @Override
        public void afterPropertiesSet() {
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

}
