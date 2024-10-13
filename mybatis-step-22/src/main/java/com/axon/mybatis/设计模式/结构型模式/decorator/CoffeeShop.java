package com.axon.mybatis.设计模式.结构型模式.decorator;

/**
 * 装饰器模式的适用场景
 *
 * 	1.	扩展类的功能：装饰器模式适合在不修改现有类的情况下，动态地为对象添加新功能。
 * 	2.	职责动态变化：当需要在运行时根据具体情况给对象附加不同的职责时，装饰器模式非常适合。
 * 	3.	避免子类过多：如果直接通过继承来扩展类的功能，可能会导致大量子类的产生。装饰器模式通过组合而非继承，避免了类的爆炸性增长。
 *
 * 常见使用案例
 *
 * 	1.	Java I/O 类：BufferedReader, InputStreamReader, FileReader等是经典的装饰器模式的应用。通过对原始输入流的包装，可以为其添加缓冲、字符转换等功能。
 *      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("file.txt")));
 *
 * .	图形界面：在GUI应用中，装饰器模式常用于为组件动态添加功能，比如为按钮添加边框、颜色、阴影等。
 * 	3.	HTTP请求和响应：在Web应用中，通过装饰器模式对请求和响应对象进行增强，如对响应进行压缩、对请求进行参数过滤等。
 * 	4.	邮件系统：邮件系统中可以使用装饰器模式来给邮件添加功能，比如加密邮件、添加附件、设置优先级等。
 *
 * 总结
 * 装饰器模式是一种灵活、强大的设计模式，可以在不修改原有类的情况下，动态地增强对象的功能。它的核心思想是通过组合的方式而不是继承来扩展类的行为，适用于当职责需要动态变化或避免子类爆炸的场景。
 *
 */
public class CoffeeShop {

    public static void main(String[] args) {
        // 购买一杯基础咖啡
        Coffee coffee = new BasicCoffee();
        System.out.println(coffee.getDescription() + " $" + coffee.cost());

        // 给咖啡加牛奶
        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.cost());

        // 再加糖
        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.cost());
    }
}