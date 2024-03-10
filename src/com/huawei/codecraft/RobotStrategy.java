package com.huawei.codecraft;


/**
 * RobotStrategy
 * 决定机器人策略
 */

public class RobotStrategy {
    /**
     * 机器人策略
     * 1.分配机器人目标 a.如果机器人有货物，将货物送到目的地 b.如果机器人没有货物，选择最近的货物
     * 2.为每个机器人规划路径，可能包含碰撞处理（CBS基于冲突的搜索算法）
     * 3.决定机器人指令 a.当前处在货物且空闲，捡起货物 b.当前处在停泊点且携带货物，放下货物 c.移动到下一个位置 d.可能停在原地规避碰撞
     * 4.将机器人指令写入Robot类，等待输出（可能修改输出部分，输出到输出缓存区）
     * @param frame
     */
    public static void process(Frame frame) {
        AssignTarget assignTarget = new AssignTarget.greedyAssignTarget();
        PlanPath planPath = new PlanPath.greedyPlanPath();
        assignTarget.assign(frame);
        planPath.plan(frame);
        decideInstruction(frame);
    }
    /**
     * 决定机器人指令并且写入Robot类
     * a.当前处在货物且空闲，捡起货物
     * b.当前处在停泊点且携带货物，放下货物
     * c.移动到下一个位置
     * d.可能停在原地规避碰撞
     * 注意：同一个机器人和轮船可以在同一帧内执行多条指令，因此，机器人可以同一帧内移动后立刻取货
     * @param frame
     */
    private static void decideInstruction(Frame frame) {

    }
}
