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
            Berth[] berths=frame.getBerth();
            for (Robot robot :robots) {
                if (robot.getState() == 0) continue;
                if (robot.isHasGoods() && robot.targetBerth==null) {
                    Berth closestBerth = findClosestBerth(robot, berths);
                    if (closestBerth != null) {
                        robot.assignTargetBerth(closestBerth);
                    }
                } else {
                    if(robot.hasPath() || robot.getTargetPos()!=null){
                        continue;
                    }
                    Goods closestGoods = findClosestGoods(robot, goodsList);
                    if (closestGoods != null) {
                        robot.assignTargetGoods(closestGoods);
                        closestGoods.setAssigned(true);
                    }
                }
            }
            
//            for (Robot robot : robots) {
//                if (robot.getState()==0)continue;
//                if (!robot.isHasGoods() && (robot.getTargetGoods()==null || robot.getTargetGoods().expired(frame.getFrameNumber()))) {
//                    Goods closestGoods = findClosestGoods(robot, goodsList);
//                    if (closestGoods != null) {
//                        robot.assignTargetGoods(closestGoods);
//                        closestGoods.setAssigned(true);
//                    }
//                } else if (robot.isHasGoods()) {
//                    Berth closestBerth = findClosestBerth(robot, berths);
//                    if (closestBerth != null) {
//                        robot.assignTargetBerth(closestBerth);
//                    }
//                }
//            }
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



        private Berth findClosestBerth(Robot robot, Berth[] berths) {
            Berth nearestPos = null;
            double minDistance = Double.MAX_VALUE;

            for (Berth berth : berths) {
                double distance = robot.getPos().distance(berth.getPos());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPos = berth;
                }
            }

            return nearestPos;
        }
    }
}


