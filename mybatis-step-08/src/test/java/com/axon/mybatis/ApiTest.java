package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.datasource.pooled.PooledDataSource;
import com.axon.mybatis.enties.UserDO;
import com.axon.mybatis.io.Resources;
import com.axon.mybatis.reflection.MetaObject;
import com.axon.mybatis.reflection.SystemMetaObject;
import com.axon.mybatis.session.SqlSession;
import com.axon.mybatis.session.SqlSessionFactory;
import com.axon.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 本章节主要是对反射的相关功能进行了封装，包装成元数据对象
 *  MetaObject 是 MyBatis 提供的一个非常实用的工具类，它极大简化了对 Java 对象属性的操作，特别是当我们需要处理复杂的嵌套对象、集合或动态生成 SQL 时，它的作用尤为显著。
 * 它通过封装反射和动态代理，提供了一种简洁、安全且高效的对象操作方式。在插件开发、动态 SQL 处理和对象属性访问等场景中，MetaObject 被广泛使用。
 *
 * 1. MeatObject的相关核心方法
 *      MetaObject 提供了一系列方法，用于对象属性的访问和操作：
 *
         * 	1.	getValue(String name)
         * 	•	读取对象的属性值。可以支持嵌套属性，如 person.address.street。
         * 	•	例子：metaObject.getValue("person.address.street")
         * 	2.	setValue(String name, Object value)
         * 	•	设置对象的属性值。支持嵌套属性修改。
         * 	•	例子：metaObject.setValue("person.address.street", "Main St")
         * 	3.	hasGetter(String name) 和 hasSetter(String name)
         * 	•	检查对象是否有指定属性的 getter 或 setter 方法。
         * 	•	例子：metaObject.hasSetter("person.age")
         * 	4.	findProperty(String name, boolean useCamelCaseMapping)
         * 	•	查找对象的属性，支持驼峰命名法的自动转换。
         * 	•	例子：metaObject.findProperty("personAge", true)
         * 	5.	getGetterNames() 和 getSetterNames()
         * 	•	返回对象所有可读（getter）或可写（setter）属性的名称列表。
         * 	6.	isCollection()
         * 	•	判断当前对象是否为集合类型（如数组、List 或 Map）。
         * 	•	例子：metaObject.isCollection()
         * 	7.	add(Object element) 和 addAll(List<?> elements)
         * 	•	用于往集合类型的属性中添加元素。
 *
 * 	2. 通过反射对属性设置的案例， 例如在构造数据源的时候
 * 	   核心代码块：
 * 	   @Override
 *     public void setProperties(Properties props) {
 *         // 这里通过反射的方式获取数据源
 *         MetaObject metaObject = SystemMetaObject.forObject(dataSource);
 *         for (Object key : props.keySet()) {
 *             String propertyName = (String) key;
 *             if (metaObject.hasSetter(propertyName)) {
 *                 String value = (String) props.get(propertyName);
 *
 *                 Object converterValue = convertValue(metaObject, propertyName, value);
 *                 metaObject.setValue(propertyName, converterValue);
 *             }
 *         }
 *     }
 *
 *
 *
 *
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_sqlSessionFactory() {
        String resource = "mybatis-config-datasource.xml";
        try {
            Reader resourceAsReader = Resources.getResourceAsReader(resource);

            //获取sqlSession的factory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsReader);

            SqlSession sqlSession = sqlSessionFactory.openSession();
            //获取代理对象
            IUserDao userDao = sqlSession.getMapper(IUserDao.class);

            UserDO userDO = userDao.queryUserInfoById(1);
            logger.info("测试结果：{}",JSON.toJSONString(userDO));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  反射的测试
     */
    @Test
    public void test_reflection() {
        ReflectionTest.Teacher teacher = new ReflectionTest.Teacher();
        List<ReflectionTest.Teacher.Student> list = new ArrayList<>();
        list.add(new ReflectionTest.Teacher.Student());
        teacher.setName("小傅哥");
        teacher.setStudents(list);

        MetaObject metaObject = SystemMetaObject.forObject(teacher);

        logger.info("getGetterNames：{}", JSON.toJSONString(metaObject.getGetterNames()));
        logger.info("getSetterNames：{}", JSON.toJSONString(metaObject.getSetterNames()));
        logger.info("name的get方法返回值：{}", JSON.toJSONString(metaObject.getGetterType("name")));
        logger.info("students的set方法参数值：{}", JSON.toJSONString(metaObject.getGetterType("students")));
        logger.info("name的hasGetter：{}", metaObject.hasGetter("name"));
        logger.info("student.id（属性为对象）的hasGetter：{}", metaObject.hasGetter("student.id"));
        logger.info("获取name的属性值：{}", metaObject.getValue("name"));
        // 重新设置属性值
        metaObject.setValue("name", "小白");
        logger.info("设置name的属性值：{}", metaObject.getValue("name"));
        // 设置属性（集合）的元素值
        metaObject.setValue("students[0].id", "001");
        logger.info("获取students集合的第一个元素的属性值：{}", JSON.toJSONString(metaObject.getValue("students[0].id")));
        logger.info("对象的序列化：{}", JSON.toJSONString(teacher));
    }

    static class Teacher {

        private String name;

        private double price;

        private List<ReflectionTest.Teacher.Student> students;

        private ReflectionTest.Teacher.Student student;

        public static class Student {

            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public List<ReflectionTest.Teacher.Student> getStudents() {
            return students;
        }

        public void setStudents(List<ReflectionTest.Teacher.Student> students) {
            this.students = students;
        }

        public ReflectionTest.Teacher.Student getStudent() {
            return student;
        }

        public void setStudent(ReflectionTest.Teacher.Student student) {
            this.student = student;
        }
    }
}
