package com.huawei.codecraft;

import java.util.List;

/**
 * Robot
 * 表示机器人对象,包含机器人ID、坐标、状态(是否携带货物、是否恢复中)、携带的货物(可选)、移动方向、产生行为（移动、拾取、卸货）、目标位置
 */

public class Robot {
    private Goods targetGoods;
    private int id;
    private Pos pos;
    private boolean hasGoods;
    /**
     * 0:恢复中,1:正常
     * 此属性从输入获取
     */
    private int state;
    /**
     * 机器人携带的货物（可选）
     */
    private Goods goods;
    /**
     * 0:右,1:左,2:上,3:下,-1:停止
     * 此属性用于输出处理
     */
    private int direction;
    /**
     * 0:移动（move）,1:拾取（get）,2:卸货（pull）
     * 此属性用于输出处理
     */
    private int action;
    private Pos targetPos;
    private Pos nextPos;
    private List path;

    public Robot(int id){
        this.id=id;
    }
    public Robot(int id, int hasGoods, int x, int y, int state) {
        this.id = id;
        this.pos = new Pos(x, y);
        this.hasGoods = hasGoods == 1;
        this.targetGoods = null;
        this.state = state;
    }

    public Pos getPos() {
        return pos;
    }
    public Goods getGoods() {
        return goods;
    }
    public void assignTargetGoods(Goods goods) {
        this.targetGoods=goods;
        this.targetPos=goods.getPos();
    }
    public void assignTargetBerth(Berth berth) {
        this.targetPos=berth.getPos();
    }
    public int getId() {
        return id;
    }
    public int getDirection() {
        return direction;
    }
    public int getAction() {
        return action;
    }
    public Pos getTargetPos() {
        return targetPos;
    }
    public boolean isHasGoods() {
        return hasGoods;
    }
    public Goods getTargetGoods() {
        return targetGoods;
    }
    public int getState() {
        return state;
    }
    public void setDirection(int direction) {
        this.direction = direction;
        setAction(0);
    }
    public void setPath(Pos nextPos){
        this.nextPos=nextPos;
    }
    public void setPathList(List path){
        this.path=path;
    }
    public Pos getPath(){
        return this.nextPos;
    }
    public List getPathList(){
        return this.path;
    }
    public void stepOnce(){
        if(this.path==null||this.path.size()==0){
            this.nextPos=null;
        }else if(this.path.size()>0){
            this.nextPos=(Pos)this.path.get(0);
            this.path.remove(0);
        }
    }
    private void setAction(int action) {
        this.action = action;
    }
    public void pickUpGoods(Goods goods) {
        setAction(1);
    }
    public void putDownGoods() {
        setAction(2);
    }
}