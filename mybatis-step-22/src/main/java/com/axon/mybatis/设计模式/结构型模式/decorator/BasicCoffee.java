package com.axon.mybatis.设计模式.结构型模式.decorator;

public class BasicCoffee  implements Coffee {

    @Override
    public String getDescription() {
        return "Basic Coffee";
    }

    @Override
    public double cost() {
        return 5.0;
    }


}
