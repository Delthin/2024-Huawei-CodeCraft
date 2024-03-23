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
    public static Robot[] robots = new Robot[Cons.MAX_ROBOT];

    static {
        for (int i = 0; i < robots.length; i++) {
            robots[i] = new Robot(i); // 显式初始化
        }
    }

    private static Boat[] boats = new Boat[Cons.MAX_BOAT];

    static {
        for (int i = 0; i < boats.length; i++) {
            boats[i] = new Boat(i); // 显式初始化
        }
    }

    public static Berth[] berths = Main.berths;

    public Frame(int frameNumber, Map map) {
        this.frameNumber = frameNumber;
        this.map = map;
    }

    public Frame(int frameNumber, int money) {
        this.frameNumber = frameNumber;
        this.money = money;
        map = Map.duplicateMap(Main.map);
    }

    public void updateGoods(Goods[] goods) {
        for (int i = 0; i < Frame.goods.size(); i++) {
            if (Frame.goods.get(i).expired(frameNumber)) {
                Frame.goods.get(i).getPos().goods = null;
                Frame.goods.remove(i);
            }
        }
        Frame.goods.addAll(Arrays.asList(goods));
        Pos p;
        for (Goods good : goods) {
            //if(good)
            p = good.getPos();
            //if(frameNumber<500)System.err.println("frame: "+frameNumber +"   goods: "+p +"newGoodsNum"+goods.length);
            Main.mapPos[p.X()][p.Y()].goods = good;
            //if(frameNumber<500)System.err.println("frame: "+frameNumber +"   goods: "+good +"   "+Main.mapPos[p.X()][p.Y()].goods+"   newGoodsNum"+goods.length );

        }

    }

    public void updateBerths() {
        //最后时刻运不走全部设为废弃
        if (frameNumber >= Cons.MAX_FRAME - Berth.maxTransportTime) {
            for (Berth berth : berths) {
                if (berth.getTransportTime() + frameNumber + 3 > Cons.MAX_FRAME) {
                    berth.setDeserted();
                }
            }
        }
//        setMaxAsFinal();
        //为适应map6先去掉
//        setTargetAsFinal();
    }

    boolean flagFirst = true;

    private void setTargetAsFinal() {
        //将船目前的目标设为最终港口

        if (frameNumber > Cons.MAX_FRAME - Berth.maxTransportTime * 4) {
            if (flagFirst) {
                //第一次将所有港口设为废弃
                for (Berth berth : berths) {
                    berth.setDeserted();
                }
                flagFirst = false;
                //为了防止正好全废弃，设定最优港口为不费
                int maxId = Para.guessBestBerthId(berths);
                berths[maxId].setDeserted(false);
            }
            //之后只要有船开往就不设为废弃
            for (Boat boat : boats) {
                if (boat.getTargetBerthId() != -1) {
                    berths[boat.getTargetBerthId()].setDeserted(false);
                }
            }


        }
    }

    private void setMaxAsFinal() {
        //即将到最后时刻全部运往货流量最大的港口（按理说最近的多）
        if (frameNumber > Cons.MAX_FRAME - Berth.maxTransportTime * 2) {
            int maxId = Para.finalBerthId(this);
            int otherId = 0;
            int distance = 0;
            for (Berth berth : berths) {
                berth.setDeserted();
                if (berth.getPos().Mdistance(berths[maxId].getPos()) > distance) {
                    distance = berth.getPos().Mdistance(berths[maxId].getPos());
                    otherId = berth.getId();
                }
            }
            //最远的一个也设为最终港口
            berths[otherId].setDeserted(false);
            berths[maxId].setDeserted(false);
        }
    }

    public void updateRobots(Robot[] robots) {
        for (int i = 0; i < robots.length; i++) {
            Frame.robots[i].setState(robots[i].getState());
            Frame.robots[i].setPos(robots[i].getPos());
            robots[i].getPos().robot=Frame.robots[i];
            Frame.robots[i].setHasGoods(robots[i].isHasGoods());
            Frame.robots[i].clear();
        }
    }

    public void updateBoats(Boat[] boats) {
        for (int i = 0; i < boats.length; i++) {
            Frame.boats[i].setState(boats[i].getState());
            Frame.boats[i].setTargetBerthId(boats[i].getTargetBerthId());
        }
    }

    public void updateMap() {
        for (int i = 0; i < Cons.MAX_ROBOT; i++) {
            if (robots[i] != null) {
                map.setRobot(robots[i].getPos());
            }
        }
        for (int i = 0; i < goods.size(); i++) {
            map.setGoods(goods.get(i).getPos());
        }
    }

    public void updateBlock(Goods[] goods) {
        for (int i = 0; i < Cons.BLOCK_HEIGHT; i++)
            for (int j = 0; j < Cons.BLOCK_WIDTH; j++) {
                Block.blocks[i * Cons.BLOCK_WIDTH + j].newGoods();
            }
        for (Goods good : goods) {
            Block.blocks[Block.getBlockId(good.getPos())].addGoods(good);
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
     *
     * @return
     */
    public Goods[] getGoods() {
        return goods.toArray(new Goods[0]);
    }

    public ArrayList<Goods> getGoodsList() {
        return goods;
    }

    public Map getMap() {
        return map;
    }

    public int getFrameNumber() {
        return this.frameNumber;
    }

    public Berth[] getBerths() {
        return berths;
    }
}



