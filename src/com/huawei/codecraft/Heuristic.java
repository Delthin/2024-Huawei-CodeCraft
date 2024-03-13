package com.huawei.codecraft;

public interface Heuristic {
    public int f(PlanPath.aStarPlanPath.Node node);
    public class Normal implements Heuristic{
        public int f(PlanPath.aStarPlanPath.Node node){
            return node.g + 10 * node.h;
        }
    }
    public class LongDistance implements Heuristic{
        public int f(PlanPath.aStarPlanPath.Node node){
            return node.h;
        }
    }
}
