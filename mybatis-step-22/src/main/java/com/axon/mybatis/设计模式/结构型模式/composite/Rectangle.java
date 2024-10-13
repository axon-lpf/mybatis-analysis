package com.axon.mybatis.设计模式.结构型模式.composite;

public class Rectangle implements Graphic {
    @Override
    public void draw() {
        System.out.println("Drawing a Rectangle");
    }
}