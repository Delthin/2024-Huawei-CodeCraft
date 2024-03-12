package com.huawei.codecraft;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Frame
 * 表示一帧的状态,包含帧序号、货物列表、机器人列表、船只列表等信息。
 * 属性:帧序号、当前金钱数、新增货物列表、机器人列表、船只列表
 * 构造方法:Frame(int frameNumber, int money)
 * addNewGoods(Goods goods)方法:添加新生成的货物
 * getRobots()方法:获取机器人列表
 * getBoats()方法:获取船只列表
 * getGoods()方法:获取货物列表
 * getMap()方法:获取地图
 * updateRobots(Robot[] robots)方法:更新机器人列表
 * updateBoats(Boat[] boats)方法:更新船只列表
 * updateMap()方法:更新地图
 */

public class Frame {
    private Map map;
    private int frameNumber;
    private int money;
    public static ArrayList<Goods> goods = new ArrayList<>();
    private Robot[] robots = new Robot[Cons.MAX_ROBOT];
    private static Boat[] boats = new Boat[Cons.MAX_BOAT];
    static {
        for (int i = 0; i < boats.length; i++) {
            boats[i] = new Boat(i); // 显式初始化
        }
    }
    public static Berth[] berths = Main.berths;
    public Frame(int frameNumber, Map map){
        this.frameNumber = frameNumber;
        this.map = map;
    }
    public Frame(int frameNumber, int money) {
        this.frameNumber = frameNumber;
        this.money = money;
        map = Map.duplicateMap(Main.map);
    }

    public void updateGoods (Goods[] goods) {
        Frame.goods.addAll(Arrays.asList(goods));
        for (int i = 0; i < Frame.goods.size(); i++) {
            if (Frame.goods.get(i).expired(frameNumber)) {
                Frame.goods.remove(i);
            }
        }
    }
    public void updateRobots (Robot[] robots) {
        this.robots = robots;
    }
    public void updateBoats (Boat[] boats) {
        for (int i = 0; i < boats.length; i++) {
            Frame.boats[i].setState(boats[i].getState());
            Frame.boats[i].setTargetBerthId(boats[i].getTargetBerthId());
        }
    }
    public void updateMap () {
        for (int i = 0; i < Cons.MAX_ROBOT; i++) {
            if (robots[i] != null) {
                map.setRobot(robots[i].getPos());
            }
        }
        for (int i = 0; i < goods.size(); i++) {
            map.setGoods(goods.get(i).getPos());
        }
    }
    public Robot[] getRobots() {
        return robots;
    }

    public Boat[] getBoats() {
        return boats;
    }
    public Berth[] getBerth() {
        return berths;
    }//todo:如何获得港口list

    /**
     * 获取货物列表,使用时需要获取数组长度（变长）
     * @return
     */
    public Goods[] getGoods() {return goods.toArray(new Goods[0]);}
    public Map getMap() {return map;}
    public int getFrameNumber(){
        return this.frameNumber;
    }
}



