package com.huawei.model;

/**
 * Robot
 * 表示机器人对象,包含机器人ID、坐标、状态(是否携带货物、是否恢复中)等信息。
 * 属性:ID、坐标(x,y)、状态(是否携带货物、是否恢复中)、携带的货物(可选)
 * 构造方法:Robot(int id)
 * moveTo(int x, int y)方法:移动机器人到指定坐标
 * pickUpGoods(Goods Goods)方法:捡起货物
 * putDownGoods()方法:放下货物
 * isRecovering()方法:判断机器人是否处于恢复状态
 * recover()方法:让机器人进入恢复状态
 * getter和setter方法
 */

public class Robot {
    private int id;
    private int x;
    private int y;
    private boolean hasGoods;
    private boolean isRecovering;
    private Goods goods;

    public Robot(int id) {
        this.id = id;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void pickUpGoods(Goods goods) {
        this.goods = goods;
        this.hasGoods = true;
    }

    public void putDownGoods() {
        this.goods = null;
        this.hasGoods = false;
    }

    public boolean isRecovering() {
        return isRecovering;
    }

    public void recover() {
        isRecovering = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isHasGoods() {
        return hasGoods;
    }

    public void setHasGoods(boolean hasGoods) {
        this.hasGoods = hasGoods;
    }


    public void setRecovering(boolean recovering) {
        isRecovering = recovering;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}