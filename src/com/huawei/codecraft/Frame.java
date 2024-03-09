package com.huawei.codecraft;

/**
 * Frame
 * 表示一帧的状态,包含帧序号、货物列表、机器人列表、船只列表等信息。
 * 属性:帧序号、当前金钱数、新增货物列表、机器人列表、船只列表
 * 构造方法:Frame(int frameNumber, int money)
 * addNewGoods(Goods goods)方法:添加新生成的货物
 * getRobots()方法:获取机器人列表
 * getBoats()方法:获取船只列表
 */

public class Frame {
    //todo: 添加必要的当前帧信息和获取方法
    private Map map;
    private int frameNumber;
    private int money;
    private Goods[] Goods;
    private Robot[] robots;
    private Boat[] boats;

    public Frame(int frameNumber, int money) {
        this.frameNumber = frameNumber;
        this.money = money;
        map = Map.duplicateMap(Main.map);
    }

    public void addNewGoods(Goods goods) {

    }

    public Robot[] getRobots() {
        return robots;
    }

    public Boat[] getBoats() {
        return boats;
    }

    public Goods[] getGoods() {return Goods;
    }
}



