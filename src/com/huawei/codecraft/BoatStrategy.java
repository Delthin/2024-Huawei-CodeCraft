package com.huawei.codecraft;


/**
 * BoatStrategy
 * 决定船只策略
 */
public class BoatStrategy {
    /**
     * 船只策略
     * 1.在泊位外等待的船只选择停泊点进入
     * 2.在停泊点的船只 a.等待货物装载 b.前往虚拟点出售 c.前往其他停泊点
     * 3.将船只指令写入Boat类，等待输出（可能修改输出部分，输出到输出缓存区）
     * @param frame
     */
    public static void process(Frame frame) {
        AssignTargetBoat assignTarget = new AssignTargetBoat.greedyAssignTarget();
        assignTarget.assign(frame);
        decideInstruction(frame);
    }
    /**
     * 争取进入复赛用到这个方法
     */
    private static void planPath(Frame frame) {

    }

    /**
     * 将船只指令写入Boat类，等待输出（可能修改输出部分，输出到输出缓存区）
     * 注意：同一个机器人和轮船可以在同一帧内执行多条指令，同一船只可以到达泊位的同一帧立刻装货。
     * @param frame
     */
    private static void decideInstruction(Frame frame) {

    }
}
