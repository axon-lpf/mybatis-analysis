package com.axon.mybatis.type;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TypeAliasRegistry {

    private final Map<String, Class<?>> TYP_ALIASES = new HashMap<>();

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
        TYP_ALIASES.put(key, value);
    }

    public <T> Class<T> resolveAlias(String string) {
        String key = string.toLowerCase(Locale.ENGLISH);
        return (Class<T>) TYP_ALIASES.get(key);
    }
}
