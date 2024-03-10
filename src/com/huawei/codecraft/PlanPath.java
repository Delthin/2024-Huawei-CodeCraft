package com.huawei.codecraft;

public interface PlanPath {
    /**
     * 为每个机器人规划路径，可能包含碰撞处理（CBS基于冲突的搜索算法）
     */
    void plan(Frame frame);
    public class greedyPlanPath implements PlanPath {
        @Override
        public void plan(Frame frame) {

        }
    }
}
