package com.axon.mybatis.设计模式.行为模式;

import java.util.ArrayList;
import java.util.List;

// 具体容器
public class NameRepository implements Container<String> {
    private List<String> names;

    public NameRepository() {
        names = new ArrayList<>();
        addName("Alice");
        addName("Bob");
        addName("Charlie");
    }

    // 添加名称
    public void addName(String name) {
        names.add(name);
    }

    // 实现getIterator方法，返回具体迭代器
    @Override
    public Iterator<String> getIterator() {
        return new NameIterator();
    }

    // 具体迭代器
    private class NameIterator implements Iterator<String> {
        private int index;

        @Override
        public boolean hasNext() {
            return index < names.size();
        }

        @Override
        public String next() {
            if (this.hasNext()) {
                return names.get(index++);
            }
            return null;
        }
    }
}