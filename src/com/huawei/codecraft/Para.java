package com.huawei.codecraft;

public class Para {
    /**
     * 初始化bfs的权重距离
     * @param loadingSpeed
     * @param transportTime
     * @return
     */
    public static int bfsBerthWeight(int loadingSpeed, int transportTime){
        return loadingSpeed * 1000 / transportTime + 1;
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
    public static final int IGNORE_VALUE = 20;
}
