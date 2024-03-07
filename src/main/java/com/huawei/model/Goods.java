package com.huawei.model;

/**
 * Goods
 * 表示货物对象,包含坐标和价值等属性。
 * 属性:坐标(x,y)、价值
 * 构造方法:Cargo(int x, int y, int value)
 * getter和setter方法
 */

public class Goods {
    private int x;
    private int y;
    private int value;

    public Goods(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

