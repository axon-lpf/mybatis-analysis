package com.axon.mybaits.type;

import com.axon.mybatis.io.Resources;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TypeAliasRegistry {


    private final Map<String, Class<?>> TYPE_ALIASES = new HashMap<>();


    public TypeAliasRegistry() {
        // 在构造函数中注册系统内置的类型别名
        registerAlias("string", String.class);
        registerAlias("int", Integer.class);
        registerAlias("long", Long.class);
        registerAlias("float", Float.class);
        registerAlias("double", Double.class);
        registerAlias("boolean", Boolean.class);
        registerAlias("byte", Byte.class);
        registerAlias("short", Short.class);
        registerAlias("char", Character.class);
    }

    public void registerAlias(String alias, Class<?> value) {
        String key = alias.toLowerCase(Locale.ENGLISH);
        TYPE_ALIASES.put(key, value);
    }

    /**
     * 这里需要做优化
     * @param string
     * @param <T>
     * @return
     */
    public <T> Class<T> resolveAlias(String string) {
        try {
            if (string == null) {
                return null;
            }
            String key = string.toLowerCase(Locale.ENGLISH);
            Class<T> value;
            if (TYPE_ALIASES.containsKey(key)) {
                value = (Class<T>) TYPE_ALIASES.get(key);
            } else {
                value = (Class<T>) Resources.classForName(string);
            }
            return value;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not resolve type alias '" + string + "'.  Cause: " + e, e);
        }
    }
}
