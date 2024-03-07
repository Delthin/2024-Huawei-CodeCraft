package com.huawei.codecraft;

/**
 * Berth
 * 表示港口对象,包含港口坐标、效率、船只到达虚拟点的时间等信息。
 * 属性:ID、坐标(x,y)、装卸效率、到达虚拟点时间
 * 构造方法:Berth(int id, int x, int y, int time, int velocity)
 * getter和setter方法
 */

public class Berth {
    private int id;
    private int x;
    private int y;
    private int transportTime;
    private int loadingSpeed;

    public Berth(int id, int x, int y, int transportTime, int loadingSpeed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.transportTime = transportTime;
        this.loadingSpeed = loadingSpeed;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTransportTime() {
        return transportTime;
    }

    public int getLoadingSpeed() {
        return loadingSpeed;
    }
}