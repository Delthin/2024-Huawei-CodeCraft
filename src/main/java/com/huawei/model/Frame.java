package com.huawei.model;

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
    private int frameNumber;
    private int money;
    private Goods[] newGoods;
    private Robot[] robots;
    private Boat[] boats;

    public Frame(int frameNumber, int money) {
        this.frameNumber = frameNumber;
        this.money = money;
    }

    public void addNewGoods(Goods goods) {

    }

    public Robot[] getRobots() {
        return robots;
    }

    public Boat[] getBoats() {
        return boats;
    }

}



