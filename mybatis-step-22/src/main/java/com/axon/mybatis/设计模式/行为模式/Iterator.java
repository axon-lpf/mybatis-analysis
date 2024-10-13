package com.axon.mybatis.设计模式.行为模式;

// 迭代器接口
public interface Iterator<T> {
    boolean hasNext();  // 是否还有下一个元素
    T next();          // 获取下一个元素
}