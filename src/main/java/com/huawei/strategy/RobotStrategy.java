package com.huawei.strategy;

import com.huawei.model.Frame;
import com.huawei.model.Robot;

/**
 * RobotStrategy
 * 这是一个抽象类或接口,定义了机器人行为的算法接口
 * 需要实现的方法:
 * decideMove(Robot robot, Frame frame): 根据机器人当前状态和场景信息,决定机器人下一步的移动方向
 * decidePickUp(Robot robot, Frame frame): 根据机器人当前状态和场景信息,决定是否捡起货物
 * decidePutDown(Robot robot, Frame frame): 根据机器人当前状态和场景信息,决定是否在港口放下货物
 * 你可以实现不同的具体策略类,如:
 * RandomRobotStrategy: 随机移动和操作
 * GreedyRobotStrategy: 贪心策略,优先移动到最近的货物或港口
 * HeuristicRobotStrategy: 使用启发式算法进行路径规划和决策
 */

public abstract class RobotStrategy {
    public abstract void decideMove(Robot robot, Frame frame);
    public abstract void decidePickUp(Robot robot, Frame frame);
    public abstract void decidePutDown(Robot robot, Frame frame);
}
