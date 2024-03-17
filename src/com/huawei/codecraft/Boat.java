package com.huawei.codecraft;

/**
 * Boat
 * 表示船只对象,包含船只ID、容量、状态(正常运行、移动中、等待中)、目标泊位ID、携带的货物数量等信息。
 */

public class Boat {
    public static int capacity;

    private int goodsSize = 0;
    private int id;

    /**
     * 0:移动中，1:等待中，2:泊位外等待
     * 此属性从输入获取
     */
    private int state;
    /**
     * -1:前往虚拟点，
     * 此属性从输入获取
     */
    private int targetBerthId;
    /**
     * 此属性用于输出的ship
     */
    private int shipTarget;
    /**
     * 0:等待，1；移动（ship），2；驶向虚拟点（go）
     */
    private int action = 0;
    public Boat(){}
    public Boat(int id){
        this.id = id;
    }

    public Boat(int id, int state, int targetBerthId) {
        this.id = id;
        this.state = state;
        this.targetBerthId = targetBerthId;
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int capa) {
        capacity = capa;
    }

    public int getId() {
        return id;
    }

    public int getState() {
        return state;
    }

    public int getTargetBerthId() {
        return targetBerthId;
    }

    public int getAction() {
        return action;
    }

    public void setState(int state) {
        this.state = state;
    }
    public void setAction(int action) {
        this.action = action;
    }
    public void shipTo(int targetBerthId) {
        this.targetBerthId = targetBerthId;
        this.action = 1;
    }
    public void go() {
        this.action = 2;
    }
    public void waitting() {
        this.action = 0;
    }
    public void load(){
        this.goodsSize += 1;
    }
    public void load(int loadingSpeed){
        this.goodsSize += loadingSpeed;
    }
    public int getVacancy() {
        return capacity - goodsSize;
    }
    public int getGoodsSize() {
        return goodsSize;
    }
    public void clearGoods(){
        goodsSize = 0;
    }

    public void setTargetBerthId(int targetBerthId) {
        this.targetBerthId = targetBerthId;
    }
    public void setShipTarget(int shipTarget){
        this.shipTarget = shipTarget;
    }
    public int getShipTarget(){
        return this.shipTarget;
    }

}



