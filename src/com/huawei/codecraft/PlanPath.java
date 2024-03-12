package com.huawei.codecraft;

import java.util.*;

public interface PlanPath {
    /**
     * 为每个机器人规划路径，可能包含碰撞处理（CBS基于冲突的搜索算法）
     */
    void plan(Frame frame);

    public class greedyPlanPath implements PlanPath {
        private static Map map = null;

        private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        @Override
        public void plan(Frame frame) {
            map = frame.getMap();
            Robot[] robots = frame.getRobots();
            Goods[] goods = frame.getGoods();
            List<Pos> initialPositions = new ArrayList<>();
            for (Robot robot : robots) {
                initialPositions.add(robot.getPos());
            }
            List<Pos> goalPositions = new ArrayList<>();
            for (Goods good : goods) {
                goalPositions.add(good.getPos());
            }
            List<List<Pos>> paths = runCBSAlgorithm(initialPositions, goalPositions);
            for (int i = 0; i < Cons.MAX_ROBOT; i++) {

                List<Pos> path = paths.get(i);
                Pos nextpos = new Pos(path.get(1).X(), path.get(1).Y());
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

        public static List<List<Pos>> runCBSAlgorithm(List<Pos> initialPositions, List<Pos> goalPositions) {
            // 初始化路径
            List<List<Pos>> paths = new ArrayList<>();
            for (int i = 0; i < Cons.MAX_ROBOT; i++) {
                int x1 = initialPositions.get(i).X();
                int y1 = initialPositions.get(i).Y();
                int x2 = goalPositions.get(i).X();
                int y2 = goalPositions.get(i).Y();
                List<Pos> path = findPath(x1, y1, x2, y2);
                paths.add(path);
            }

            // 冲突检测和解决
            boolean conflictDetected = true;
            while (conflictDetected) {
                conflictDetected = false;
                Set<Integer> conflictedRobots = new HashSet<>();
                for (int i = 0; i < Cons.MAX_ROBOT; i++) {
                    if (conflictedRobots.contains(i)) {
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
                            replanPath(path1);
                            replanPath(path2);

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

        public static void replanPath(List<Pos> path) {
            // 重新规划路径，考虑障碍物

            Pos start = path.get(0);
            Pos goal = path.get(path.size() - 1);
            path.clear();
            path.add(start);

            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 上下左右四个方向

            boolean[][] visited = new boolean[Cons.MAP_SIZE][Cons.MAP_SIZE];

            replanPathDFS(start.X(), start.Y(), goal, visited, path);
        }

        public static void replanPathDFS(int x, int y, Pos goal, boolean[][] visited, List<Pos> path) {
            if (x == goal.X() && y == goal.Y()) {
                return;
            }

            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 上下左右四个方向

            for (int[] direction : directions) {
                int newX = x + direction[0];
                int newY = y + direction[1];

                if (isValidPosition(newX, newY, visited)) {
                    visited[newX][newY] = true;
                    path.add(new Pos(newX, newY));
                    replanPathDFS(newX, newY, goal, visited, path);

                    if (path.get(path.size() - 1).X() == goal.X() && path.get(path.size() - 1).Y() == goal.Y()) {
                        return;
                    }

                    path.remove(path.size() - 1);
                }
            }
        }

        public static boolean isValidPosition(int x, int y, boolean[][] visited) {
            // 检查位置是否在地图范围内、不在障碍物上且未访问过
            return x >= 0 && x < Cons.MAP_SIZE && y >= 0 && y < Cons.MAP_SIZE && !map.isObstacle(x, y) && !visited[x][y];
        }

        public static boolean isValidPosition(int x, int y) {
            return x >= 0 && x < Cons.MAP_SIZE && y >= 0 && y < Cons.MAP_SIZE && !map.isObstacle(x, y);
        }

        public static List<Pos> findPath(int startX, int startY, int targetX, int targetY) {

            if (!isValidPosition(startX, startY) || !isValidPosition(targetX, targetY)) {
                return null;// 起始坐标或目标坐标无效，返回null表示无法找到路径
            }

            //List<Pos> path = new ArrayList<>();
            PriorityQueue<Pos> openList = new PriorityQueue<>((a, b) -> a.f - b.f);//存储待探索的节点，并按照节点的代价进行排序，优先处理代价最低的节点。
            Set<Pos> closedSet = new HashSet<>();//存储已经完全探索过的节点，避免重复探索

            Pos startPos = new Pos(startX, startY);
            //path.add(startPos);

            startPos.g = 0;
            startPos.h = heuristic(startX, startY, targetX, targetY);
            startPos.f = startPos.g + startPos.h;

            openList.offer(startPos);

            while (!openList.isEmpty()) {
                Pos current = openList.poll();

                if (current.X() == targetX && current.Y() == targetY) {
                    return reconstructPath(current);
                }

                closedSet.add(current);

                for (int[] direction : DIRECTIONS) {
                    int nextX = current.X() + direction[0];
                    int nextY = current.Y() + direction[1];

                    if (isValidPosition(nextX, nextY) && !map.isObstacle(nextX, nextY)) {
                        Pos neighbor = new Pos(nextX, nextY);
                        neighbor.g = current.g + 1;
                        neighbor.h = heuristic(nextX, nextY, targetX, targetY);
                        neighbor.f = neighbor.g + neighbor.h;
                        neighbor.parent = current;// 设置相邻节点的父节点为当前节点

                        if (closedSet.contains(neighbor)) {
                            continue;// 相邻节点已经在已探索集合中，跳过继续处理下一个相邻节点
                        }

                        if (!openList.contains(neighbor)) {
                            openList.offer(neighbor);// 相邻节点不在待探索集合中，加入待探索集合
                        } else {
                            Pos existingNeighbor = getPosFromOpenList(openList, neighbor);
                            if (existingNeighbor != null && neighbor.g < existingNeighbor.g) {
                                existingNeighbor.g = neighbor.g;
                                existingNeighbor.f = neighbor.f;
                                existingNeighbor.parent = current;// 更新已存在的相邻节点的父节点为当前节点
                            }
                        }
                    }
                }
            }

            return null;
        }

        private static boolean isValidCoordinate(int x, int y, int rows, int cols) {
            return x >= 0 && x < rows && y >= 0 && y < cols;
        }

        private static int heuristic(int startX, int startY, int targetX, int targetY) {
            // 曼哈顿距离
            return Math.abs(startX - targetX) + Math.abs(startY - targetY);
        }

        private static List<Pos> reconstructPath(Pos current) {
            List<Pos> path = new ArrayList<>();
            while (current != null) {
                path.add(0, current);
                current = current.parent;
            }
            return path;
        }

        private static Pos getPosFromOpenList(PriorityQueue<Pos> openList, Pos targetPos) {
            for (Pos node : openList) {
                if (node.X() == targetPos.X() && node.Y() == targetPos.Y()) {
                    return node;
                }
            }
            return null;
        }
    }

    public class aStarPlanPath implements PlanPath {
        static final int[] dx = {-1, 0, 1, 0};
        static final int[] dy = {0, 1, 0, -1};

        /**
         * 根据目标货物位置直接寻找路径，不考虑碰撞处理
         *
         * @param frame
         */
        @Override
        public void plan(Frame frame) {

            char[][] mapData = frame.getMap().getMapData();
            for (Robot robot : frame.getRobots()) {
                if (robot.getState() == 0) {
                    continue;
                }
                Pos start = robot.getPos();
                Pos goal = robot.getTargetPos();
                if (robot.getPathList() != null && !robot.getPathList().isEmpty()) {
                    robot.stepOnce();
                }
                //更新路径的情况，分配到港口、分配的货物消失、
                else {
                    if (goal == null) {
                        continue;
                    }
                    List path = findPath(mapData, start, goal);
                    if (path != null) {
                        robot.setPathList(robot.getId(), path);
                        robot.stepOnce();
                    }

                }

            }

        }

        public List findPath(char[][] mapData, Pos start, Pos goal) {
            int m = mapData.length, n = mapData[0].length;
            PriorityQueue<Node> pq = new PriorityQueue<>();
            boolean[][] visited = new boolean[m][n];
            pq.offer(new Node(start, 0, manhattanDistance(start, goal), null));

            Node endNode = null;
            while (!pq.isEmpty() && pq.size() < Cons.PRIORITY_QUEUE_SIZE) {//设置最大优先队列大小防止爆，后面可以考虑提高启发函数权重
                Node cur = pq.poll();
                if (mapData[cur.pos.X()][cur.pos.Y()] == '#' || mapData[cur.pos.X()][cur.pos.Y()] == '*') {
                    continue;
                }
                visited[cur.pos.X()][cur.pos.Y()] = true;

                if (cur.pos.equals(goal)) {
                    endNode = cur;
                    break;
                }

                for (int i = 0; i < 4; i++) {
                    int nx = cur.pos.X() + dx[i], ny = cur.pos.Y() + dy[i];
                    if (nx < 0 || nx >= m || ny < 0 || ny >= n) {
                        continue;
                    }
                    if (!visited[nx][ny] && mapData[nx][ny] != '#' && mapData[nx][ny] != '*') {
                        Pos next = new Pos(nx, ny);
                        pq.offer(new Node(next, cur.g + 1, manhattanDistance(next, goal), cur));
                    }
                }
            }

            if (endNode == null) {
                return null;
            } else {
                List path = getPath(endNode);
                if (path.get(0).equals(start)) {
                    path.remove(0);
                }
                return path;
            }
        }

        private List getPath(Node end) {
            List<Pos> path = new ArrayList<>();
            Node cur = end;
            while (cur != null) {
                path.add(cur.pos);
                cur = cur.parent;
            }
            Collections.reverse(path);
            return path;
        }

        private int manhattanDistance(Pos a, Pos b) {
            return Math.abs(a.X() - b.X()) + Math.abs(a.Y() - b.Y());
        }

        public class Node implements Comparable<Node> {
            Pos pos;
            int g, h;
            Node parent;

            Node(Pos pos, int g, int h, Node parent) {
                this.pos = pos;
                this.g = g;
                this.h = h;
                this.parent = parent;
            }

            public int f() {
                return g + 5 * h;
            }

            @Override
            public int compareTo(Node other) {
                return Integer.compare(this.f(), other.f());
            }
        }

    }
}
