package com.huawei.codecraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface PlanPath {
    /**
     * 为每个机器人规划路径，可能包含碰撞处理（CBS基于冲突的搜索算法）
     */
    void plan(Frame frame);
    public class greedyPlanPath implements PlanPath {
        @Override
        public void plan(Frame frame) {
            Map map = frame.getMap();
            Robot[] robots = frame.getRobots();
            Goods[] goods = frame.getGoods();
            List<Pos> initialPositions = new ArrayList<>();
            for (Robot robot : robots) {
                 initialPositions.add(robot.getPos() );
            }
            List<Pos> goalPositions = new ArrayList<>();
            for (Goods good : goods) {
                goalPositions.add(good.getPos() );
            }
            List<List<Pos>> paths = runCBSAlgorithm(map, initialPositions, goalPositions);
            for (int i = 0; i < Cons.MAX_ROBOT; i++) {

                List<Pos> path = paths.get(i);
                Pos nextpos = new Pos(path.get(0).X(),path.get(0).Y());
                robots[i].setPath(nextpos);

            }

//            for (Robot robot : robots) {
//                Pos currentPos = robot.getPos();
//                Pos targetPos = robot.getTargetPos();
//                Pos nextPos=null;
//                robot.setPath(null);
//                if (!currentPos.equals(targetPos)) {
//                    // 根据贪心策略选择下一个最近的位置
//                    nextPos= getNextPos(currentPos, targetPos);
//                    //path.add(nextPos);
//                    //currentPos = nextPos;
//                }
//                if(nextPos!=null)robot.setPath(nextPos);
//            }
        }

        private Pos getNearestPos(Pos currentPos, Pos targetPos) {
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

        public static List<List<Pos>> runCBSAlgorithm(Map map, List<Pos> initialPositions, List<Pos> goalPositions) {
            // 初始化路径
            List<List<Pos>> paths = new ArrayList<>();
            for (int i = 0; i < Cons.MAX_ROBOT; i++) {
                List<Pos> path = new ArrayList<>();
                path.add(initialPositions.get(i));
                paths.add(path);
            }

            // 冲突检测和解决
            boolean conflictDetected = true;
            while (conflictDetected) {
                conflictDetected = false;
                Set<Integer> conflictedRobots = new HashSet<>();
                for (int i = 0; i < Cons.MAX_ROBOT; i++) {
                    if (conflictedRobots.contains(i) ) {
                        continue;
                    }

                    for (int j = i + 1; j < Cons.MAX_ROBOT; j++) {
                        if (conflictedRobots.contains(j)) {
                            continue;
                        }

                        List<Pos> path1 = paths.get(i);
                        List<Pos> path2 = paths.get(j);
                        if (checkCollision(path1, path2)) {
                            // 发生冲突，进行解决

                            // 路径交换
                            swapPaths(path1, path2);

                            // 路径重规划
                            replanPath(path1, map);
                            replanPath(path2, map);

                            conflictDetected = true;
                            conflictedRobots.add(i);
                            conflictedRobots.add(j);
                        }
                    }
                }
            }

            return paths;
        }

        public static boolean checkCollision(List<Pos> path1, List<Pos> path2) {
            // 检查两个路径是否有重叠的位置
            for (Pos position1 : path1) {
                for (Pos position2 : path2) {
                    if (position1.X() == position2.X() && position1.Y() == position2.Y()) {
                        return true; // 发生碰撞冲突
                    }
                }
            }
            return false;
        }

        public static void swapPaths(List<Pos> path1, List<Pos> path2) {
            // 交换两个路径
            List<Pos> temp = new ArrayList<>(path1);
            path1.clear();
            path1.addAll(path2);
            path2.clear();
            path2.addAll(temp);
        }

        public static void replanPath(List<Pos> path,Map map) {
            // 重新规划路径，考虑障碍物

            Pos start = path.get(0);
            Pos goal = path.get(path.size() - 1);
            path.clear();
            path.add(start);

            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 上下左右四个方向

            boolean[][] visited = new boolean[Cons.MAP_SIZE][Cons.MAP_SIZE];

            replanPathDFS(start.X(), start.Y(), map, goal, visited, path);
        }

        public static void replanPathDFS(int x, int y, Map map, Pos goal, boolean[][] visited, List<Pos> path) {
            if (x == goal.X() && y == goal.Y()) {
                return;
            }

            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 上下左右四个方向

            for (int[] direction : directions) {
                int newX = x + direction[0];
                int newY = y + direction[1];

                if (isValidPosition(newX, newY, map, visited)) {
                    visited[newX][newY] = true;
                    path.add(new Pos(newX, newY));
                    replanPathDFS(newX, newY, map, goal, visited, path);

                    if (path.get(path.size() - 1).X() == goal.X() && path.get(path.size() - 1).Y() == goal.Y()) {
                        return;
                    }

                    path.remove(path.size() - 1);
                }
            }
        }

        public static boolean isValidPosition(int x, int y, Map map, boolean[][] visited) {
            // 检查位置是否在地图范围内、不在障碍物上且未访问过
            return x >= 0 && x < Cons.MAP_SIZE && y >= 0 && y < Cons.MAP_SIZE && !map.isObstacle(x,y) && !visited[x][y];
        }
    }

}
