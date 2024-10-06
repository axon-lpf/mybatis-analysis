package com.axon.mybatis.datasource.unpooled;

import com.axon.mybatis.datasource.DataSourceFactory;
import com.axon.mybatis.datasource.unpooled.UnpooledDataSource;
import com.axon.mybatis.reflection.MetaObject;
import com.axon.mybatis.reflection.SystemMetaObject;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 非池化的数据源工厂
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {


    protected DataSource dataSource;

    public UnpooledDataSourceFactory() {
        //初始化数据源
        this.dataSource = new UnpooledDataSource();
    }


    /**
     * 通过反射，设置数据源对应的属性
     *
     * @param props
     */
    @Override
    public void setProperties(Properties props) {
        // 这里通过反射的方式获取数据源
        MetaObject metaObject = SystemMetaObject.forObject(dataSource);
        for (Object key : props.keySet()) {
            String propertyName = (String) key;
            if (metaObject.hasSetter(propertyName)) {
                String value = (String) props.get(propertyName);

                Object converterValue = convertValue(metaObject, propertyName, value);
                metaObject.setValue(propertyName, converterValue);
            }
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }


    /**
     * 根据setter的类型,将配置文件中的值强转成相应的类型
     */
    private Object convertValue(MetaObject metaObject, String propertyName, String value) {
        Object convertedValue = value;
        Class<?> targetType = metaObject.getSetterType(propertyName);
        if (targetType == Integer.class || targetType == int.class) {
            convertedValue = Integer.valueOf(value);
        } else if (targetType == Long.class || targetType == long.class) {
            convertedValue = Long.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            convertedValue = Boolean.valueOf(value);
        }
        return convertedValue;
    }
}
