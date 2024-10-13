package com.axon.mybatis.设计模式.创建型模式;


/**
 *  建造者模式使用案例
 *  适用场景
 *
 * 	1.	构造复杂对象时，参数多且可选参数较多：
 * 当对象的构造涉及很多可选参数时，直接使用构造函数容易导致大量重载方法，使代码难以维护。使用建造者模式可以有效解决这一问题。例如，创建一台电脑时，有些组件（如硬盘、显卡）可以有多种配置，但它们并非必需。建造者模式可以灵活地为不同客户创建不同配置的电脑。
 * 	2.	对象的构建步骤需要可控和分步骤进行：
 * 当对象构造过程需要分多个步骤进行，每个步骤都可能改变对象状态，使用建造者模式可以更好地控制构建顺序。例如，建造房子涉及多个步骤（如打地基、建墙、安装屋顶等），每个步骤都是对象的一部分，最后构成整体对象。
 * 	3.	需要确保对象的不可变性：
 * 在对象创建后，不允许修改其内部状态。使用建造者模式可以确保对象在构造过程中是可变的，而一旦构造完成，就成为不可变的对象。例如，像StringBuilder这种对象在生成String时就不再允许修改。
 * 	4.	需要生成复杂对象家族：
 * 当需要根据一组标准生成不同的对象时，建造者模式可以通过不同的建造者生成不同的对象。例如，在游戏开发中，可能需要根据不同的角色生成不同的装备配置，建造者模式可以帮助我们简化这个过程。
 *
 *
 * 其他适用场景：
 * 	•	报表生成器：创建不同类型的报表，用户可以选择不同的数据格式（如JSON、XML）、不同的输出格式（如PDF、HTML）。
 * 	•	配置文件生成：系统中有大量复杂的配置项，建造者模式可以简化生成不同配置的对象。
 * 	•	数据库查询构建器：构建复杂的SQL查询，使用建造者模式可以逐步构建不同的查询条件（如SELECT、WHERE、ORDER BY）。
 *
 *
 *
 */
public class ComputerBuilder {
    // 必要属性
    private String CPU;
    private String RAM;

    // 可选属性
    private String storage;
    private String GPU;
    private String keyboard;
    private String display;

    // 私有构造方法，使用 Builder 创建
    private ComputerBuilder(Builder builder) {
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        this.storage = builder.storage;
        this.GPU = builder.GPU;
        this.keyboard = builder.keyboard;
        this.display = builder.display;
    }

    @Override
    public String toString() {
        return "Computer{" +
                "CPU='" + CPU + '\'' +
                ", RAM='" + RAM + '\'' +
                ", storage='" + storage + '\'' +
                ", GPU='" + GPU + '\'' +
                ", keyboard='" + keyboard + '\'' +
                ", display='" + display + '\'' +
                '}';
    }

    // 静态内部类 Builder，负责构造 Computer
    public static class Builder {
        // 必要属性
        private String CPU;
        private String RAM;

        // 可选属性
        private String storage;
        private String GPU;
        private String keyboard;
        private String display;

        // 构造器中要求传递必要属性
        public Builder(String CPU, String RAM) {
            this.CPU = CPU;
            this.RAM = RAM;
        }

        // 设置可选属性的方法，返回 Builder 自身
        public Builder setStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public Builder setGPU(String GPU) {
            this.GPU = GPU;
            return this;
        }

        public Builder setKeyboard(String keyboard) {
            this.keyboard = keyboard;
            return this;
        }

        public Builder setDisplay(String display) {
            this.display = display;
            return this;
        }

        // 构建最终的 Computer 对象
        public ComputerBuilder build() {
            return new ComputerBuilder(this);
        }
    }

    public static void main(String[] args) {

        // 使用 Builder 模式创建 Computer 对象
        ComputerBuilder gamingPC = new ComputerBuilder.Builder("Intel i9", "32GB")
                .setStorage("1TB SSD")
                .setGPU("NVIDIA RTX 3080")
                .setKeyboard("Mechanical Keyboard")
                .setDisplay("4K Display")
                .build();

        ComputerBuilder officePC = new ComputerBuilder.Builder("Intel i5", "16GB")
                .setStorage("512GB SSD")
                .setDisplay("1080p Display")
                .build();

        System.out.println(gamingPC);
        System.out.println(officePC);
    }
}


