package com.huawei.codecraft;

/**
 * Robot
 * 表示机器人对象,包含机器人ID、坐标、状态(是否携带货物、是否恢复中)、携带的货物(可选)、移动方向、产生行为（移动、拾取、卸货）、目标位置
 */

public class Robot {
    //todo: 添加机器人的其他属性和方法
    private int id;
    private Pos pos;
    private boolean hasGoods;
    private boolean isRecovering;
    private Goods goods;
    /**
     * 0:右,1:左,2:上,3:下,-1:停止
     */
    private int direction;
    /**
     * 0:移动,1:拾取,2:卸货
     */
    private int action;
    private Pos targetPos;

    public Robot(int id) {
        this.id = id;
    }

//    public void moveTo(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }
//
//    public void pickUpGoods(Goods goods) {
//        this.goods = goods;
//        this.hasGoods = true;
//    }
//
//    public void putDownGoods() {
//        this.goods = null;
//        this.hasGoods = false;
//    }
//
//    public boolean isRecovering() {
//        return isRecovering;
//    }
//
//    public void recover() {
//        isRecovering = true;
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
//    public boolean isHasGoods() {
//        return hasGoods;
//    }
//
//    public void setHasGoods(boolean hasGoods) {
//        this.hasGoods = hasGoods;
//    }
//
//
//    public void setRecovering(boolean recovering) {
//        isRecovering = recovering;
//    }
//
//    public Goods getGoods() {
//        return goods;
//    }
//
//    public void setGoods(Goods goods) {
//        this.goods = goods;
//    }
}