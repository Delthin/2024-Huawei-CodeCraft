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
    private boolean isAssigned = false;
    public Goods(int x, int y, int value, int summonFrame) {
        this.pos = Main.mapPos[x][y];
        this.value = value;
        this.summonFrame = summonFrame;
    }

    public Pos getPos() {
        return pos;
    }
    public int getValue() {
        return value;
    }
    public int getSummonFrame() {
        return summonFrame;
    }
    public boolean isAssigned() {
        return isAssigned;
    }
    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
    public boolean expired(int frameNumber) {
        return frameNumber - summonFrame > Cons.EXPIRE_TIME;
    }
}

