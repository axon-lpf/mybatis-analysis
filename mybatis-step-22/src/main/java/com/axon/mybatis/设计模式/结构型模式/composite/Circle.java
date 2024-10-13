package com.axon.mybatis.设计模式.结构型模式.composite;

public // 单个图形：圆形
class Circle implements Graphic {
    @Override
    public void draw() {
        System.out.println("Drawing a Circle");
    }
}