package com.axon.mybatis.设计模式.结构型模式.composite;

import java.util.ArrayList;
import java.util.List;

// 组合图形类，表示图形的组合
class CompositeGraphic implements Graphic {
    // 存储子图形的列表
    private List<Graphic> childGraphics = new ArrayList<>();

    // 添加子图形
    public void add(Graphic graphic) {
        childGraphics.add(graphic);
    }

    // 移除子图形
    public void remove(Graphic graphic) {
        childGraphics.remove(graphic);
    }

    // 绘制组合中的所有图形
    @Override
    public void draw() {
        for (Graphic graphic : childGraphics) {
            graphic.draw();  // 递归调用子图形的draw方法
        }
    }
}