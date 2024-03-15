package com.huawei.codecraft;

import java.util.ArrayList;
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
    private int[] action = new int[2];
    private Pos targetPos;
    private Pos nextPos;
    private List<Pos> pathList;
    private List<Block> blockList;

    private Block nextBlock;

    public Berth targetBerth;

    public Robot(int id) {
        this.id = id;
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
        this.targetBerth = null;
        this.targetGoods = goods;
        this.targetPos = goods.getPos();
    }

    public void assignTargetBerth(Berth berth) {
        this.targetPos = berth.getPos();
        this.targetBerth = berth;
    }

    public int getId() {
        return id;
    }

    public int getDirection() {
        return direction;
    }

    public int[] getAction() {
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

    public void setPath(Pos nextPos) {
        this.nextPos = nextPos;
    }

    public void setPathList(List path) {
        this.pathList = path;
    }

    public static void setPathList(int id, List path) {
        if (path != null && !path.isEmpty()) {
            Frame.robots[id].setPathList(path);
        }
    }
    public void clearPathList() {
        this.pathList = null;
    }

    public Pos getNextPos() {
        return this.nextPos;
    }

    public List getPathList() {
        return this.pathList;
    }

    public boolean hasPath() {
        return this.pathList != null && !this.pathList.isEmpty();
    }

    public void stepOnce() {
        if (this.pathList == null || this.pathList.isEmpty()) {
            this.nextPos = null;
        } else {
            this.nextPos = this.pathList.get(0);
            this.pathList.remove(0);
        }

    }

    private void setAction(int action) {
        this.action[0] = action;
        this.action[1] = 0;
    }

    public void pickUpGoods(Goods goods) {
        this.action[1] = 1;
    }

    public void putDownGoods() {
        this.action[1] = 2;
    }

    public static void setBlocksList(int id, List blocks) {
        Frame.robots[id].setBlocksList(blocks);
    }

    public void setBlocksList(List blocks) {
        this.blockList = blocks;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public void setHasGoods(boolean hasGoods) {
        this.hasGoods = hasGoods;
    }

    /**
     * 刷新每一帧会变化的属性，比如action，direction
     */
    public void clear() {
        this.action[0] = 0;
        this.action[1] = 0;
        this.direction = -1;
    }
}