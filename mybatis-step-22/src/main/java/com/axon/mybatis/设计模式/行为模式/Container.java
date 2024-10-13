package com.axon.mybatis.设计模式.行为模式;

// 容器接口
public interface Container<T> {
    Iterator<T> getIterator();  // 返回迭代器
}