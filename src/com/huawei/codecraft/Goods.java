package com.huawei.codecraft;

/**
 * Goods
 * 表示货物对象,包含坐标、价值、生成帧序号属性。
 * 属性:坐标(x,y)、价值(value)、生成帧序号(summonFrame)
 * getter方法
 */

public class Goods {
    private Pos pos;
    private int value;
    /**
     * 生成时的帧数
     */
    private int summonFrame;
    public Goods(int x, int y, int value, int summonFrame) {
        this.pos = new Pos(x, y);
        this.value = value;
        this.summonFrame = summonFrame;
    }

//    public int getX() {
//        return x;
//    }
//
//    public void setX(int x) {
//        this.x = x;
//    }
//
//    public int getY() {
//        return y;
//    }
//
//    public void setY(int y) {
//        this.y = y;
//    }
//
//    public int getValue() {
//        return value;
//    }
//
//    public void setValue(int value) {
//        this.value = value;
//    }
}

