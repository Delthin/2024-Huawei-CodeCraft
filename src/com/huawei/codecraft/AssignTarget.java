package com.huawei.codecraft;

public interface AssignTarget {
    /**
     * 分配机器人目标
     * a.如果机器人有货物，将货物送到目的地
     * b.如果机器人没有货物，选择最近的货物(暂定)
     */
    void assign(Frame frame);
    public class greedyAssignTarget implements AssignTarget {
        @Override
        public void assign(Frame frame) {

        }
    }
}


