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
        return loadingSpeed * loadingSpeedWeight / transportTime + 1;
    }
    public static int loadingSpeedWeight = 300;
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
    public static int IGNORE_VALUE = 10;
    public static double boatAssignWeight(Boat boat, Berth berth){
        boolean isAssigned = berth.isItAssigned();
        int assignWeight = isAssigned ? 1 : 20;
        int goodsNum = berth.getGoodsNum();
        int loadingSpeed = berth.getLoadingSpeed();
        int transportTime = berth.getTransportTime();
        int flow = berth.getFlow();
        return assignWeight * (transportTime / 100) * Math.log(berth.getGoodsFlow()) * goodsNum / Math.pow(loadingSpeed, 2) / Math.sqrt(flow + 1);
    }
    public static double boatFinalWeight(Boat boat, Berth berth){
        //哪里多去哪，优先去不废弃和未分配的
        boolean isAssigned = berth.isItAssigned();
        boolean isDeserted = berth.isDeserted();
        int assignWeight = isAssigned ? 1 : assignW;
        int desertedWeight = isDeserted ? 1 : desertW;
        int goodsNum = berth.getGoodsNum();
        int loadingSpeed = berth.getLoadingSpeed();
        int transportTime = berth.getTransportTime();
        int flow = berth.getFlow();
        return assignWeight * desertedWeight * goodsNum / Math.pow(loadingSpeed, 2);
    }
    public static int assignW = 50;
    public static int desertW = 1300;//best in map5
    public static int guessBestBerthId(Berth[] berths){
        double maxWeight = 0;
        int maxId = 0;
        for (Berth berth : berths){
            int goodsNum = berth.getGoodsNum();
            int loadingSpeed = berth.getLoadingSpeed();
            int transportTime = berth.getTransportTime();
            int goodsFlow = berth.getGoodsFlow();
            double weight = 1000.0 / goodsNum * loadingSpeed * Math.log(goodsFlow) / transportTime;
            if (weight > maxWeight){
                maxWeight = weight;
                maxId = berth.getId();
            }
        }
        return maxId;
    }
    public static double boatRandomWeight(){
        return new Random().nextDouble();
    }
    public static int bfsAssignHeapCapacity = 6;
    public static int bfsMaxdistance = 150;
    public static Comparator<Goods> bfsAssignHeapComparator= Comparator.comparingDouble(Para::calculatePriority);


    private static double calculatePriority(Goods goods) {
        return (double) (goods.getPos().tempg + goods.getPos().bfsRealDistance) / goods.getValue();
    }

    /**
     * 终结时刻泊位选择
     * @param frame
     * @return
     */
    public static int finalBerthId(Frame frame){
        Berth[] berths = frame.getBerths();
        int maxId = 0;
        //只看流量版
//        int maxFlow = 0;
//        for (int i = 0; i < Cons.MAX_BERTH; i++) {
//            if (berths[i].getGoodsFlow() > maxFlow) {
//                maxFlow = berths[i].getGoodsFlow();
//                maxId = i;
//            }
//        }
//        return maxId;
        double maxWeight = 0;
        for (Berth berth : berths){
            int goodsFlow = berth.getGoodsFlow();
            int transportTime = berth.getTransportTime();
            double weight = goodsFlow * 10.0 / Math.pow(transportTime,2);
            if (weight > maxWeight){
                maxWeight = weight;
                maxId = berth.getId();
            }
        }
        return maxId;
    }
}
