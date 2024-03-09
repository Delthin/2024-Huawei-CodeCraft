package com.huawei.codecraft;

/**
 * Boat
 * 表示船只对象,包含船只ID、容量、状态(正常运行、移动中、等待中)、目标泊位ID、携带的货物数量等信息。
 */

public class Boat {
    public static int capacity;

    private int id;

    /**
     * 0:移动中，1:等待中，2:泊位外等待，
     */
    private int status;
    /**
     * -1:前往虚拟点，
     */
    private int targetBerthId;

    public Boat(int id) {
        this.id = id;
        this.status = 1;
        this.targetBerthId = -1;
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int capa) {
        capacity = capa;
    }

//    public void moveTo(int berthId) {
//        this.targetBerthId = berthId;
//        this.status = 1;
//    }
//
//    public void dock() {
//        this.status = 2;
//    }
//
//    public void undock() {
//        this.status = 1;
//    }
//
//    public void load(Goods goods) {
//        this.capacity -= goods.getValue();
//    }
//
//    public void setWaitingState() {
//        this.status = 0;
//    }
//
//    public boolean isAvailable() {
//        return this.status == 0;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public int getTargetBerthId() {
//        return targetBerthId;
//    }
//
//    public void setTargetBerthId(int targetBerthId) {
//        this.targetBerthId = targetBerthId;
//    }
}



