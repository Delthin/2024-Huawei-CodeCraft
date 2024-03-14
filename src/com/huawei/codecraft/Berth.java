package com.huawei.codecraft;

import java.util.LinkedList;
import java.util.Queue;

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
    private int transportTime;
    private int loadingSpeed;
<<<<<<< HEAD
    private Queue<Boat> waitingBoats= new LinkedList<>();//维护一个等待队列

=======
    private Queue<Boat> waitingBoats=new LinkedList<Boat>();//维护一个等待队列
>>>>>>> 0e9b26d4caec1ef2a5a11a8e745575a3dff30d48
    public Berth(int id, int x, int y, int transportTime, int loadingSpeed) {
        this.id = id;
        this.pos = new Pos(x, y);
        this.transportTime = transportTime;
        this.loadingSpeed = loadingSpeed;
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
    public void offerWaitingBoats(Boat boat){
<<<<<<< HEAD
        this.waitingBoats.offer(boat);//实现队尾添加元素
    }
    public Boat deleteFirstWaitingBoat(){
        return this.waitingBoats.poll();//删除并返回队列的第一个元素,没用到这个函数 导致等待队列没有更新
    }
    public Boat getFirstWaitingBoat(){
        return this.waitingBoats.peek();//返回队列的第一个元素
    }
    public int waitingLength(){
        return this.waitingBoats.size();
=======
        waitingBoats.offer(boat);//实现队尾添加元素
    }
    public Boat deleteFirstWaitingBoat(){
        return waitingBoats.poll();//删除并返回队列的第一个元素
    }
    public Boat getFirstWaitingBoat(){
        return waitingBoats.peek();//返回队列的第一个元素
    }
    public int waitingLength(){
        return waitingBoats.size();
>>>>>>> 0e9b26d4caec1ef2a5a11a8e745575a3dff30d48
    }
}