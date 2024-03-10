package com.huawei.codecraft;

public interface AssignTarget {
    /**
     * 分配机器人目标
     * a.如果机器人有货物，将货物送到目的地
     * b.如果机器人没有货物，
     *      1.无目标，选择最近的货物(暂定)
     *      2.有目标，前往目标
     */
    void assign(Frame frame);
    public class greedyAssignTarget implements AssignTarget {
        @Override
        public void assign(Frame frame) {
            Robot[] robots = frame.getRobots(); // 获取机器人列表
            Goods[] goodsList = frame.getGoods(); // 获取货物列表

            for (Robot robot : robots) {
                if (!robot.isHasGoods() && robot.getTarget()!=null) {
                    Goods closestGoods = findClosestGoods(robot, goodsList);
                    if (closestGoods != null) {
                        assignTarget(robot, closestGoods);
                    }
                }
            }
        }
        private Goods findClosestGoods(Robot robot, Goods[] goodsList) {
            Goods closestGoods = null;
            double minDistance = Double.MAX_VALUE;

            for (Goods goods : goodsList) {
                if (!goods.isAssigned()) {
                    double distance = robot.getPos().distance(goods.getPos());
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestGoods = goods;
                    }
                }
            }

            return closestGoods;
        }
        private void assignTarget(Robot robot, Goods goods) {
            // 将机器人分配给最近的货物
            // 实现具体的分配逻辑
            robot.assignTarget(goods);
            goods.setAssigned(true);
        }

    }
}


