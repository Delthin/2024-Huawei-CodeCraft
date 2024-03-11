package com.huawei.codecraft;

public interface PlanPath {
    /**
     * 为每个机器人规划路径，可能包含碰撞处理（CBS基于冲突的搜索算法）
     */
    void plan(Frame frame);
    public class greedyPlanPath implements PlanPath {
        @Override
        public void plan(Frame frame) {
            Robot[] robots = frame.getRobots();

            for (Robot robot : robots) {
                Pos currentPos = robot.getPos();
                Pos targetPos = robot.getTargetPos();
                Pos nextPos=null;
                robot.setPath(null);
                if (!currentPos.equals(targetPos)) {
                    // 根据贪心策略选择下一个最近的位置
                    nextPos= getNextPos(currentPos, targetPos);
                    //path.add(nextPos);
                    //currentPos = nextPos;
                }
                if(nextPos!=null)robot.setPath(nextPos);
            }
        }

        private Pos getNextPos(Pos currentPos, Pos targetPos) {
            // 实现贪心策略，选择下一个最近的位置
            // 这里可以使用启发式函数或简单的距离计算来确定下一个位置

            // 示例：假设机器人只能水平或垂直移动
            int currentX = currentPos.X();
            int currentY = currentPos.Y();
            int targetX = targetPos.X();
            int targetY = targetPos.Y();

            if (currentX < targetX) {
                return new Pos(currentX + 1, currentY);
            } else if (currentX > targetX) {
                return new Pos(currentX - 1, currentY);
            } else if (currentY < targetY) {
                return new Pos(currentX, currentY + 1);
            } else {
                return new Pos(currentX, currentY - 1);
            }
        }

    }
}
