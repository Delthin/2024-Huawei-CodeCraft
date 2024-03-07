package com.huawei.strategy;

import com.huawei.model.Boat;
import com.huawei.model.Frame;

/**
 * BoatStrategy
 * 这也是一个抽象类或接口,定义了船舶行为的算法接口
 * 需要实现的方法:
 * decideMoveTo(Boat boat, Frame frame): 根据船舶当前状态和场景信息,决定船舶下一步移动的目标港口
 * decideSail(Boat boat, Frame frame): 根据船舶当前状态和场景信息,决定是否驶离港口运输货物
 * 你可以实现不同的具体策略类,如:
 * RandomShipStrategy: 随机移动和操作
 * GreedyShipStrategy: 贪心策略,优先选择最近的空闲港口或最快装卸的港口
 * ValueMaxShipStrategy: 优先选择能产生最高价值的运输路线
 */
public abstract class BoatStrategy {
    public abstract void decideMoveTo(Boat boat, Frame frame);
    public abstract void decideSail(Boat boat, Frame frame);
}
