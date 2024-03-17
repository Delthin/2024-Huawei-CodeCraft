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
    /**
     * 左上角坐标
     */
    private Pos pos;
    private int transportTime;//1-1000
    private int loadingSpeed;//1-5
    public int bfsWeight;

    public Berth(int id, int x, int y, int transportTime, int loadingSpeed) {
        this.id = id;
        this.pos = Main.mapPos[x][y];
        this.transportTime = transportTime;
        this.loadingSpeed = loadingSpeed;
        this.bfsWeight= loadingSpeed * 10 / ((transportTime+1000) / 100) + 1 ;//todo:调参
    }

    public int getId() {
        return id;
    }

    public Pos getPos(){
        return pos;
    }

    public int getTransportTime() {
        return transportTime;
    }

    public int getLoadingSpeed() {
        return loadingSpeed;
    }
}