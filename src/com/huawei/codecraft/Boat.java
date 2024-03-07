package com.huawei.codecraft;

/**
 * Boat
 * 表示船只对象,包含船只ID、容量、状态(正常运行、移动中、等待中)、目标港口等信息。
 * 属性:ID、容量、状态(正常运行、移动中、等待中)、目标港口ID
 * 构造方法:Boat(int id, int capacity)
 * moveTo(int berthId)方法:移动船只到指定港口
 * dock()方法:船只进入港口
 * undock()方法:船只离开港口
 * load(List<Goods> Goodss)方法:装载货物
 * setWaitingState()方法:设置船只为等待状态
 * isAvailable()方法:判断船只是否可用
 * getter和setter方法
 */

public class Boat {
    private int id;
    private int capacity;
    private int status;
    private int targetBerthId;

    public Boat(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.status = 0;
    }

    public void moveTo(int berthId) {
        this.targetBerthId = berthId;
        this.status = 1;
    }

    public void dock() {
        this.status = 2;
    }

    public void undock() {
        this.status = 1;
    }

    public void load(Goods goods) {
        this.capacity -= goods.getValue();
    }

    public void setWaitingState() {
        this.status = 0;
    }

    public boolean isAvailable() {
        return this.status == 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTargetBerthId() {
        return targetBerthId;
    }

    public void setTargetBerthId(int targetBerthId) {
        this.targetBerthId = targetBerthId;
    }
}



