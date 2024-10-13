package com.axon.mybatis.设计模式.结构型模式.composite;

/**
 * 适用场景：
 *
 * 	1.	表示树形结构的数据：如果你的对象具有“部分-整体”的层次结构，比如文件系统（文件和文件夹）、公司组织结构（部门和员工），组合模式非常适合。
 * 	2.	希望客户端统一处理个体和组合对象：在某些情况下，你希望客户端能以相同的方式处理单个对象和组合对象。组合模式允许你在处理对象集合时，忽略它们是单个对象还是一个组合对象。
 * 	3.	图形或用户界面系统：类似于这个示例，图形界面系统通常由单个控件和复合控件（如按钮、窗口、面板等）组成，组合模式可以用于这样的场景。
 *
 * 案例应用场景：
 *
 * 	•	文件系统：文件夹可以包含文件，也可以包含其他文件夹，文件夹和文件的操作（如显示文件名）可以统一进行。
 * 	•	图形绘制系统：单个图形元素（如圆形、矩形）和图形的组合可以统一处理。
 * 	•	用户界面组件：按钮、文本框和窗口等UI组件可以进行组合使用，并统一处理。
 * 	•	公司组织结构：公司中的部门可以包含员工，也可以包含其他部门，操作可以统一执行。
 */
public class CompositePatternDemo {
    public static void main(String[] args) {
        // 创建单个图形对象
        Graphic circle1 = new Circle();
        Graphic rectangle1 = new Rectangle();

        // 创建组合图形
        CompositeGraphic composite1 = new CompositeGraphic();
        composite1.add(circle1);
        composite1.add(rectangle1);

        // 再次组合，创建嵌套组合
        CompositeGraphic composite2 = new CompositeGraphic();
        composite2.add(composite1);  // 添加组合图形
        composite2.add(new Circle());  // 添加新的单个图形

        // 绘制所有图形
        System.out.println("Drawing composite2:");
        composite2.draw();
    }
}