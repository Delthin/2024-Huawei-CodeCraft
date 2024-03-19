package com.huawei.codecraft;


import java.util.Comparator;
import java.util.Random;

public class Para {
    /**
     * 初始化bfs的权重距离
     * @param loadingSpeed
     * @param transportTime
     * @return
     */
    public static int bfsBerthWeight(int loadingSpeed, int transportTime){
        return loadingSpeed * 300 / transportTime + 1;
    }

    /**
     * 分配货物时的权重计算
     * @param robot
     * @param goods
     * @return
     */
    public static double goodsAssignWeight(Robot robot, Goods goods){
        int distance = 2*robot.getPos().Mdistance(goods.getPos())+goods.getPos().bfsWeightsDistance;
        double weight = (double) goods.getValue() /distance;
        return weight;
    }

    /**
     * 分配货物时忽视的小货物
     */
    public static final int IGNORE_VALUE = 10;
    public static double boatAssignWeight(Boat boat, Berth berth){
        boolean isAssigned = berth.isItAssigned();
        int assignWeight = isAssigned ? 1 : 5;
        int goodsNum = berth.getGoodsNum();
        int loadingSpeed = berth.getLoadingSpeed();
        int transportTime = berth.getTransportTime();
        int flow = berth.getFlow();
        return assignWeight * (transportTime / 100) * Math.log(berth.getGoodsFlow()) * goodsNum / Math.pow(loadingSpeed, 2) / Math.sqrt(flow + 1);
    }
    public static double boatRandomWeight(){
        return new Random().nextDouble();
    }
    public static int bfsAssignHeapCapacity = 5;
    public static int bfsMaxdistance = 150;
    public static Comparator<Goods> bfsAssignHeapComparator= Comparator.comparingInt(Para::calculatePriority);


    private static int calculatePriority(Goods goods) {
        return (goods.getPos().tempg + goods.getPos().bfsRealDistance) / goods.getValue();
    }



}
