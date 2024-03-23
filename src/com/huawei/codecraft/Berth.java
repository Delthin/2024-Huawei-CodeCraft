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
    private int goodsNum;
    private boolean isAssigned = false;
    private boolean deserted = false;
    public static int maxTransportTime = 0;
    public static int minTransportId = -1;
    //船流量（可改为货物）
    private int flow = 0;
    private int goodsFlow = 0;

    public Berth(int id, int x, int y, int transportTime, int loadingSpeed) {
        this.id = id;
        this.pos = Main.mapPos[x][y];
        this.transportTime = transportTime;
        this.loadingSpeed = loadingSpeed;
        this.bfsWeight= Para.bfsBerthWeight(loadingSpeed, transportTime) ;//todo:调参
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
    public int getGoodsNum() {
        return goodsNum;
    }
    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }
    public void addGoodsNum(int goodsNum) {
        this.goodsNum += goodsNum;
    }
    public void addGoodsNum() {
        this.goodsNum += 1;
        this.goodsFlow += 1;
    }
    public void subGoodsNum(int goodsNum) {
        this.goodsNum -= goodsNum;
    }
    public boolean isItAssigned() {
        return isAssigned;
    }
    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
    public void setDeserted(){
        this.deserted = true;
    }
    public void setDeserted(boolean deserted){
        this.deserted = deserted;
    }
    public boolean isDeserted(){
        return this.deserted;
    }
    public void clearGoodsNum() {
        this.goodsNum = 0;
    }
    public int getFlow() {
        return flow;
    }
    public void addFlow(int flow) {
        this.flow += flow;
    }
    public int getGoodsFlow() {
        return goodsFlow;
    }
}