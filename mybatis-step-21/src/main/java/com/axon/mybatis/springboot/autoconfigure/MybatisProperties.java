package com.axon.mybatis.springboot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @ConfigurationProperties 注解的作用是 将配置文件中的属性值注入到类的字段中。在这个例子中，它指定了 prefix = MybatisProperties.MYBATIS_PREFIX，这意味着 Spring Boot 会从配置文件（如 application.properties 或 application.yml）中读取以 mybatis 为前缀的属性，并将其注入到 MybatisProperties 类中。
 * 假设 MYBATIS_PREFIX 的值为 "mybatis"，那么 Spring Boot 会将 mybatis.* 相关的配置注入到这个类中。
 */
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class MybatisProperties {

    public static final String MYBATIS_PREFIX = "mybatis.datasource";

    private String driver;              // com.mysql.jdbc.Driver
    private String url;                 // jdbc:mysql://127.0.0.1:3306/my_test_db?useUnicode=true
    private String userName;            // root
    private String password;            // 123456
    private String mapperLocations;     // classpath*:mapper/*.xml
    private String baseDaoPackage;      // 扫描报的路径

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getBaseDaoPackage() {
        return baseDaoPackage;
    }

    public void setBaseDaoPackage(String baseDaoPackage) {
        this.baseDaoPackage = baseDaoPackage;
    }

}