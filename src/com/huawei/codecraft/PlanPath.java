package com.huawei.codecraft;

import java.util.*;


public interface PlanPath {
    /**
     * 为每个机器人规划路径，可能包含碰撞处理（CBS基于冲突的搜索算法）
     */
    void plan(Frame frame);

    public class CBSPlanPath_aborted implements PlanPath {

        private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        private static class Node {
            int x;
            int y;
            int g;  // 从起点到当前节点的实际代价
            int h;  // 从当前节点到终点的估计代价
            Node parent;

            Node(int x, int y) {
                this.x = x;
                this.y = y;
                this.g = 0;
                this.h = 0;
                this.parent = null;
            }

            int getF() {
                return g + h;
            }
        }
        private static HashSet[][] visited = Main.visited;
        private static char[][] grid;  // 地图网格
        private static int gridSizeX;  // 网格大小X
        private static int gridSizeY;  // 网格大小Y

        private static PriorityQueue<Node> openSetForward;  // 前向搜索的开放集合
        private static PriorityQueue<Node> openSetBackward;  // 后向搜索的开放集合
        //private static Set<Node> closedSetForward;  // 前向搜索的关闭集合
        //private static Set<Node> closedSetBackward;  // 后向搜索的关闭集合
        Robot[] robots;
        static int frameNumber;
        @Override
        public void plan(Frame frame) {
            frameNumber=frame.getFrameNumber();
            grid = frame.getMap().getMapData();
            gridSizeX = Cons.MAP_SIZE;
            gridSizeY = Cons.MAP_SIZE;

            robots = frame.getRobots();
            for (Robot robot : robots) {
                Pos start = robot.getPos();
                visited[start.X()][start.Y()].add(frameNumber);
            }
            for (Robot robot : robots) {
                if (robot.getState() == 0) {
                    continue;
                }
                Pos start = robot.getPos();
                Pos goal = robot.getTargetPos();

                if (robot.getPathList() != null && !robot.getPathList().isEmpty()) {
                    robot.stepOnce();
                } else {
                    //更新路径的情况，分配到港口、分配的货物消失、
                    if (goal == null) {
                        continue;
                    }
                    Node startNode = new Node(start.X(), start.Y());
                    Node endNode = new Node(goal.X(), goal.Y());

                    // 执行双向A*搜索
                    Node[] intersectionNode = bidirectionalAStar(startNode, endNode);

                    // 输出最短路径
                    List<Pos> path = null;
                    if (intersectionNode != null) {
                        path = reconstructPath(intersectionNode);
                    }
                    if (path != null) {
                        robot.setPathList(robot.getId(), path);
                        robot.stepOnce();
                    }

                }

            }


        }


        private static Node[] bidirectionalAStar(Node startNode, Node endNode) {
            openSetForward = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
            openSetBackward = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
            //closedSetForward = new HashSet<>();
            //closedSetBackward = new HashSet<>();
            boolean[][] visitedStart = new boolean[gridSizeX][gridSizeY];
            //boolean[][] visitedBack = new boolean[gridSizeX][gridSizeY];
            Node[][] visitedBack = new Node[gridSizeX][gridSizeY];
            startNode.g = 0;
            startNode.h = calculateHeuristic(startNode, endNode);
            openSetForward.add(startNode);

            endNode.g = 0;
            endNode.h = calculateHeuristic(endNode, startNode);
            openSetBackward.add(endNode);

            while (!openSetForward.isEmpty() && !openSetBackward.isEmpty() && openSetForward.size() < Cons.PRIORITY_QUEUE_SIZE / 2) {
                Node currentForward = openSetForward.poll();
                //closedSetForward.add(currentForward);
                visitedStart[currentForward.x][currentForward.y] = true;


                Node currentBackward = openSetBackward.poll();
                //closedSetBackward.add(currentBackward);
                visitedBack[currentBackward.x][currentBackward.y] = currentBackward;

                if (visitedBack[currentForward.x][currentForward.y] != null) {
                    return new Node[]{currentForward, visitedBack[currentForward.x][currentForward.y]};  // 找到相交节点
                }

//                if (visitedStart[currentBackward.x][currentBackward.y]) {
//                    return currentBackward;  // 找到相交节点
//                }

                exploreNeighbors(currentForward, openSetForward, visitedStart, endNode);
                exploreNeighbors(currentBackward, openSetBackward, visitedBack, startNode);
            }

            return null;  // 没有找到路径
        }

        private static void exploreNeighbors(Node current, PriorityQueue<Node> openSet, boolean[][] visited, Node endNode) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direction : directions) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                if (isValidPosition(neighborX, neighborY, current.g+1)) {
                    Node neighbor = new Node(neighborX, neighborY);
                    neighbor.parent = current;
                    neighbor.g = current.g + 1;
                    neighbor.h = calculateHeuristic(neighbor, endNode);

                    if (visited[neighborX][neighborY]) {
                        continue;  // 已在关闭集合中，跳过
                    }

                    Node existingNode = findNodeInOpenSet(neighbor, openSet);
                    if (existingNode == null) {
                        openSet.add(neighbor);  // 添加到开放集合
                    } else {
                        if (neighbor.g < existingNode.g) {
                            existingNode.parent = current;
                            existingNode.g = neighbor.g;
                        }
                    }
                }
            }
        }

        private static void exploreNeighbors(Node current, PriorityQueue<Node> openSet, Node[][] visited, Node endNode) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direction : directions) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                if (isValidPosition(neighborX, neighborY,current.g+1)) {
                    Node neighbor = new Node(neighborX, neighborY);
                    neighbor.parent = current;
                    neighbor.g = current.g + 1;
                    neighbor.h = calculateHeuristic(neighbor, endNode);

                    if (visited[neighborX][neighborY] != null) {
                        continue;  // 已在关闭集合中，跳过
                    }

                    Node existingNode = findNodeInOpenSet(neighbor, openSet);
                    if (existingNode == null) {
                        openSet.add(neighbor);  // 添加到开放集合
                    } else {
                        if (neighbor.g < existingNode.g) {
                            existingNode.parent = current;
                            existingNode.g = neighbor.g;
                        }
                    }
                }
            }
        }

        private static Node findNodeInOpenSet(Node node, PriorityQueue<Node> openSet) {
            for (Node n : openSet) {
                if (n.x == node.x && n.y == node.y) {
                    return n;
                }
            }
            return null;
        }

        private static List<Pos> reconstructPath(Node[] intersectionNode) {
            List<Pos> path = new ArrayList<>();
            Node currentNode = intersectionNode[0];
            int g1=currentNode.g;
            //Pos cur = new Pos(currentNode.x,currentNode.y);
            while (currentNode != null && currentNode.parent != null) {
                visited[currentNode.x][currentNode.y].add(frameNumber+currentNode.g);
                path.add(new Pos(currentNode.x, currentNode.y));
                currentNode = currentNode.parent;
            }

            // 反转路径，使其从起点到相交节点
            Collections.reverse(path);

            // 继续从相交节点回溯到终点
            currentNode = intersectionNode[1];
            int g2=currentNode.g;
            while (currentNode != null && currentNode.parent != null) {
                visited[currentNode.x][currentNode.y].add(frameNumber+g1+g2-currentNode.g);

                currentNode = currentNode.parent;
                path.add(new Pos(currentNode.x, currentNode.y));
            }

            return path;
        }


        private static int calculateHeuristic(Node node, Node targetNode) {
            // 使用曼哈顿距离作为估计代价
            return Math.abs(node.x - targetNode.x) + Math.abs(node.y - targetNode.y);
        }

        private static boolean isValidPosition(int x, int y ,int g) {
            if(y==2 && x==1 || y==2 &&x==2){
                int a=1;
            }
            return x >= 0 && x < gridSizeX && y >= 0 && y < gridSizeY && grid[x][y] != '#' && grid[x][y] != '*' && !visited[x][y].contains(frameNumber+g) && !visited[x][y].contains(frameNumber+g-1);
        }


//        public List<List<Pos>> runCBSAlgorithm() {
//            // 冲突检测和解决
//            boolean conflictDetected = true;
//            while (conflictDetected) {
//                conflictDetected = false;
//                Set<Integer> conflictedRobots = new HashSet<>();
//                for (int i = 0; i < Cons.MAX_ROBOT; i++) {
//                    if (conflictedRobots.contains(i)) {
//                        continue;
//                    }
//
//                    for (int j = i + 1; j < Cons.MAX_ROBOT; j++) {
//                        if (conflictedRobots.contains(j)) {
//                            continue;
//                        }
//
//                        List<Pos> path1 = paths.get(i);
//                        List<Pos> path2 = paths.get(j);
//                        if (checkCollision(path1, path2)) {
//                            // 发生冲突，进行解决
//                            if (robots[i].isHasGoods() && robots[j].isHasGoods() || !robots[i].isHasGoods() && !robots[j].isHasGoods()) {
//                                // 路径交换
//                                swapGoals(path1, path2);
//                            }
//                            // 路径重规划
//                            replanPath(path1);
//                            replanPath(path2);
//
//                            conflictDetected = true;
//                            conflictedRobots.add(i);
//                            conflictedRobots.add(j);
//                        }
//                    }
//                }
//            }
//
//            return paths;
//        }
//
//        public static boolean checkCollision(List<Pos> path1, List<Pos> path2) {
//            // 检查两个路径是否有重叠的位置
//            for (Pos position1 : path1) {
//                for (Pos position2 : path2) {
//                    if (position1.X() == position2.X() && position1.Y() == position2.Y()) {
//                        return true; // 发生碰撞冲突
//                    }
//                }
//            }
//            return false;
//        }
//
//        public static void swapPaths(List<Pos> path1, List<Pos> path2) {
//            // 交换两个路径
//            List<Pos> temp = new ArrayList<>(path1);
//            path1.clear();
//            path1.addAll(path2);
//            path2.clear();
//            path2.addAll(temp);
//        }
//
//        public static void replanPath(List<Pos> path) {
//            // 重新规划路径，考虑障碍物
//
//            Pos start = path.get(0);
//            Pos goal = path.get(path.size() - 1);
//            path.clear();
//            path.add(start);
//
//            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 上下左右四个方向
//
//            boolean[][] visited = new boolean[Cons.MAP_SIZE][Cons.MAP_SIZE];
//
//            replanPathDFS(start.X(), start.Y(), goal, visited, path);
//        }
//
//        public static void replanPathDFS(int x, int y, Pos goal, boolean[][] visited, List<Pos> path) {
//            if (x == goal.X() && y == goal.Y()) {
//                return;
//            }
//
//            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 上下左右四个方向
//
//            for (int[] direction : directions) {
//                int newX = x + direction[0];
//                int newY = y + direction[1];
//
//                if (isValidPosition(newX, newY, visited)) {
//                    visited[newX][newY] = true;
//                    path.add(new Pos(newX, newY));
//                    replanPathDFS(newX, newY, goal, visited, path);
//
//                    if (path.get(path.size() - 1).X() == goal.X() && path.get(path.size() - 1).Y() == goal.Y()) {
//                        return;
//                    }
//
//                    path.remove(path.size() - 1);
//                }
//            }
//        }


    }
    public class CBSPlanPath implements PlanPath {

        private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        private static class Node {
            int x;
            int y;
            int g;  // 从起点到当前节点的实际代价
            int h;  // 从当前节点到终点的估计代价
            Node parent;

            Node(int x, int y) {
                this.x = x;
                this.y = y;
                this.g = 0;
                this.h = 0;
                this.parent = null;
            }

            int getF() {
                return g + h;
            }
        }
        private static HashSet[][] visited = Main.visited;
        private static char[][] grid;  // 地图网格
        private static int gridSizeX;  // 网格大小X
        private static int gridSizeY;  // 网格大小Y

        private static PriorityQueue<Node> openSetForward;  // 前向搜索的开放集合
        private static PriorityQueue<Node> openSetBackward;  // 后向搜索的开放集合
        //private static Set<Node> closedSetForward;  // 前向搜索的关闭集合
        //private static Set<Node> closedSetBackward;  // 后向搜索的关闭集合
        Robot[] robots;
        static int frameNumber;
        static Pos[][] mapPos;
        private static int plan;
        @Override
        public void plan(Frame frame) {
            frameNumber=frame.getFrameNumber();
            grid = frame.getMap().getMapData();
            gridSizeX = Cons.MAP_SIZE;
            gridSizeY = Cons.MAP_SIZE;
            mapPos=Main.mapPos;
            robots = frame.getRobots();
            plan=0;
            for (Robot robot : robots) {
                Pos start = robot.getPos();
                visited[start.X()][start.Y()].add(frameNumber);
                if(robot.getState()==0){
                    if(robot.hasPath()){
                        System.err.println(frameNumber);
                        System.err.println(visited[start.X()][start.Y()]);
                        System.err.println(visited[robot.nextPos.X()][robot.nextPos.Y()]);

                    }
                    robot.setPathList(robot.getId(), null);
                    for(int i=1;i<20;i++){
                        visited[start.X()][start.Y()].add(frameNumber+i);//todo:撞傻的机器人应该add不止此帧
                    }
                }
            }
            for (Robot robot : robots) {
                if(plan>=500)break;
                if (robot.getState() == 0) {
                    continue;
                }
                Pos start = robot.getPos();
                Pos goal = robot.getTargetPos();

                if (robot.getPathList() != null && !robot.getPathList().isEmpty()) {
                    if(robot.getState()==0){
                        System.err.println(start);
                        System.err.println(frameNumber);
                    }
                    robot.stepOnce();//todo
                } else {
                    //更新路径的情况，分配到港口、分配的货物消失、
                    if (goal == null || robot.isHasGoods()) {//先分配去拿货的机器人
                        continue;
                    }

                    Node startNode = new Node(start.X(), start.Y());
                    Node endNode = new Node(goal.X(), goal.Y());

                    // 执行A*搜索
                    Node intersectionNode = AStar(startNode, endNode);

                    // 输出最短路径
                    List<Pos> path = null;
                    if (intersectionNode != null) {
                        path = reconstructPath(intersectionNode,startNode);
                    }
                    if (path != null) {
                        robot.setPathList(robot.getId(), path);
                        robot.stepOnce();
                    }else{
                        robot.setTargetPos(null);
                    }

                }

            }
            for (Robot robot : robots) {
                if (robot.isHasGoods()) {//前往港口
                    int minDistance=Integer.MAX_VALUE;
                    Pos start = robot.getPos();
                    Pos next = start;
                    for (int[] direction : Cons.DIRECTIONS) {
                        int neighborX = start.X() + direction[0];
                        int neighborY = start.Y() + direction[1];

                        if (isValidPosition(neighborX, neighborY, 1)) {
                            if(mapPos[neighborX][neighborY].bfsWeightsDistance<minDistance){
                                minDistance = mapPos[neighborX][neighborY].bfsWeightsDistance;
                                next=mapPos[neighborX][neighborY];
                            }
                        }
                    }
                    robot.nextPos=next;
                }
                if(robot.nextPos==null){
                    Pos start = robot.getPos();
                    if(visited[start.X()][start.Y()].contains(frameNumber+1)){
                        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                        for (int[] direction : directions) {
                            int neighborX = start.X() + direction[0];
                            int neighborY = start.Y() + direction[1];

                            if (isValidPosition(neighborX, neighborY, 1)) {
                                robot.nextPos=mapPos[neighborX][neighborY];
                            }
                        }
                    }

                }

            }

        }


        private static Node AStar(Node startNode, Node endNode) {
            openSetForward = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
            //openSetBackward = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
            //closedSetForward = new HashSet<>();
            //closedSetBackward = new HashSet<>();
            boolean[][] visitedStart = new boolean[gridSizeX][gridSizeY];

            startNode.g = 0;
            startNode.h = calculateHeuristic(startNode, endNode);
            openSetForward.add(startNode);
            //int step=0;
            while (!openSetForward.isEmpty()  ) {
                //step+=1;
                plan+=1;
                Node currentForward = openSetForward.poll();
                //closedSetForward.add(currentForward);
                if((currentForward.g>startNode.h*5 ) && grid[endNode.x][endNode.y]!='B')return null;//
                if(visitedStart[currentForward.x][currentForward.y])continue;
                visitedStart[currentForward.x][currentForward.y] = true;

                if (currentForward.x==endNode.x && currentForward.y==endNode.y) {
                    return currentForward;  // 找到相交节点
                }


                exploreNeighbors(currentForward, openSetForward, visitedStart, endNode);
            }

            return null;  // 没有找到路径
        }

        private static void exploreNeighbors(Node current, PriorityQueue<Node> openSet, boolean[][] visited, Node endNode) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direction : directions) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                if (isValidPosition(neighborX, neighborY, current.g+1)) {
                    Node neighbor = new Node(neighborX, neighborY);
                    neighbor.parent = current;
                    neighbor.g = current.g + 1;
                    neighbor.h = calculateHeuristic(neighbor, endNode);

                    if (visited[neighborX][neighborY]) {
                        continue;  // 已在关闭集合中，跳过
                    }

                    Node existingNode = findNodeInOpenSet(neighbor, openSet);
                    if (existingNode == null) {
                        openSet.add(neighbor);  // 添加到开放集合
                    } else {
                        if (neighbor.g < existingNode.g) {
                            existingNode.parent = current;
                            existingNode.g = neighbor.g;
                        }
                    }
                }
            }
        }

        private static void exploreNeighbors(Node current, PriorityQueue<Node> openSet, Node[][] visited, Node endNode) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direction : directions) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                if (isValidPosition(neighborX, neighborY,current.g+1)) {
                    Node neighbor = new Node(neighborX, neighborY);
                    neighbor.parent = current;
                    neighbor.g = current.g + 1;
                    neighbor.h = calculateHeuristic(neighbor, endNode);

                    if (visited[neighborX][neighborY] != null) {
                        continue;  // 已在关闭集合中，跳过
                    }

                    Node existingNode = findNodeInOpenSet(neighbor, openSet);
                    if (existingNode == null) {
                        openSet.add(neighbor);  // 添加到开放集合
                    } else {
                        if (neighbor.g < existingNode.g) {
                            existingNode.parent = current;
                            existingNode.g = neighbor.g;
                        }
                    }
                }
            }
        }

        private static Node findNodeInOpenSet(Node node, PriorityQueue<Node> openSet) {
            for (Node n : openSet) {
                if (n.x == node.x && n.y == node.y) {
                    return n;
                }
            }
            return null;
        }

        private static List<Pos> reconstructPath(Node currentNode, Node startNode) {
            List<Pos> path = new ArrayList<>();
            visited[currentNode.x][currentNode.y].add(frameNumber+currentNode.g+1);
            //Pos cur = new Pos(currentNode.x,currentNode.y);
            while (currentNode != null && currentNode.parent != null) {
                visited[currentNode.x][currentNode.y].add(frameNumber+currentNode.g);

                path.add(mapPos[currentNode.x][currentNode.y]);
                currentNode = currentNode.parent;
            }
            // 反转路径，使其从起点到相交节点
            Collections.reverse(path);
            return path;
        }


        private static int calculateHeuristic(Node node, Node targetNode) {
            // 使用曼哈顿距离作为估计代价
            return Math.abs(node.x - targetNode.x) + Math.abs(node.y - targetNode.y);
        }

        private static boolean isValidPosition(int x, int y ,int g) {

            return x >= 0 && x < gridSizeX && y >= 0 && y < gridSizeY && grid[x][y] != '#'&& grid[x][y] != 'R' && grid[x][y] != '*' && !visited[x][y].contains(frameNumber+g) && !visited[x][y].contains(frameNumber+g-1) && !visited[x][y].contains(frameNumber+g+1)&& !visited[x][y].contains(frameNumber+g-2) && !visited[x][y].contains(frameNumber+g+2);
        }


//        public List<List<Pos>> runCBSAlgorithm() {
//            // 冲突检测和解决
//            boolean conflictDetected = true;
//            while (conflictDetected) {
//                conflictDetected = false;
//                Set<Integer> conflictedRobots = new HashSet<>();
//                for (int i = 0; i < Cons.MAX_ROBOT; i++) {
//                    if (conflictedRobots.contains(i)) {
//                        continue;
//                    }
//
//                    for (int j = i + 1; j < Cons.MAX_ROBOT; j++) {
//                        if (conflictedRobots.contains(j)) {
//                            continue;
//                        }
//
//                        List<Pos> path1 = paths.get(i);
//                        List<Pos> path2 = paths.get(j);
//                        if (checkCollision(path1, path2)) {
//                            // 发生冲突，进行解决
//                            if (robots[i].isHasGoods() && robots[j].isHasGoods() || !robots[i].isHasGoods() && !robots[j].isHasGoods()) {
//                                // 路径交换
//                                swapGoals(path1, path2);
//                            }
//                            // 路径重规划
//                            replanPath(path1);
//                            replanPath(path2);
//
//                            conflictDetected = true;
//                            conflictedRobots.add(i);
//                            conflictedRobots.add(j);
//                        }
//                    }
//                }
//            }
//
//            return paths;
//        }
//
//        public static boolean checkCollision(List<Pos> path1, List<Pos> path2) {
//            // 检查两个路径是否有重叠的位置
//            for (Pos position1 : path1) {
//                for (Pos position2 : path2) {
//                    if (position1.X() == position2.X() && position1.Y() == position2.Y()) {
//                        return true; // 发生碰撞冲突
//                    }
//                }
//            }
//            return false;
//        }
//
//        public static void swapPaths(List<Pos> path1, List<Pos> path2) {
//            // 交换两个路径
//            List<Pos> temp = new ArrayList<>(path1);
//            path1.clear();
//            path1.addAll(path2);
//            path2.clear();
//            path2.addAll(temp);
//        }
//
//        public static void replanPath(List<Pos> path) {
//            // 重新规划路径，考虑障碍物
//
//            Pos start = path.get(0);
//            Pos goal = path.get(path.size() - 1);
//            path.clear();
//            path.add(start);
//
//            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 上下左右四个方向
//
//            boolean[][] visited = new boolean[Cons.MAP_SIZE][Cons.MAP_SIZE];
//
//            replanPathDFS(start.X(), start.Y(), goal, visited, path);
//        }
//
//        public static void replanPathDFS(int x, int y, Pos goal, boolean[][] visited, List<Pos> path) {
//            if (x == goal.X() && y == goal.Y()) {
//                return;
//            }
//
//            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 上下左右四个方向
//
//            for (int[] direction : directions) {
//                int newX = x + direction[0];
//                int newY = y + direction[1];
//
//                if (isValidPosition(newX, newY, visited)) {
//                    visited[newX][newY] = true;
//                    path.add(new Pos(newX, newY));
//                    replanPathDFS(newX, newY, goal, visited, path);
//
//                    if (path.get(path.size() - 1).X() == goal.X() && path.get(path.size() - 1).Y() == goal.Y()) {
//                        return;
//                    }
//
//                    path.remove(path.size() - 1);
//                }
//            }
//        }


    }
    public class aStarPlanPath implements PlanPath {
        static final int[] dx = {-1, 0, 1, 0};
        static final int[] dy = {0, 1, 0, -1};
        private static HashSet[][] visitedFrame = Main.visited;
        Robot[] robots;
        static int frameNumber;

        /**
         * 根据目标货物位置直接寻找路径，不考虑碰撞处理
         *
         * @param frame
         */
        @Override
        public void plan(Frame frame) {

            char[][] mapData = frame.getMap().getMapData();
            robots = frame.getRobots();
            frameNumber=frame.getFrameNumber();
            for (Robot robot : robots) {
                Pos start = robot.getPos();
                visitedFrame[start.X()][start.Y()].add(frameNumber);
            }
            for (Robot robot : robots) {
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
                    List path = findPath(mapData, start, goal, robot);
                    if (path != null) {
                        robot.setPathList(robot.getId(), path);
                        robot.stepOnce();
                    }else if(!robot.isHasGoods()){
                        robot.setTargetPos(null);

                    }

                }

            }

        }

        public List findPath(char[][] mapData, Pos start, Pos goal, Robot robot) {
            int m = mapData.length, n = mapData[0].length;
            PriorityQueue<Node> pq = new PriorityQueue<>();
            boolean[][] visited = new boolean[m][n];
            Heuristic func = new Heuristic.Normal();
            if (manhattanDistance(start, goal) > 33) {
                func = new Heuristic.LongDistance();
            }
            pq.offer(new Node(start, 0, manhattanDistance(start, goal), null, func));

            Node endNode = null;
            while (!pq.isEmpty() && pq.size() < Cons.PRIORITY_QUEUE_SIZE/2) {//设置最大优先队列大小防止爆，后面可以考虑提高启发函数权重
                Node cur = pq.poll();
                if (mapData[cur.pos.X()][cur.pos.Y()] == '#' || mapData[cur.pos.X()][cur.pos.Y()] == '*') {
                    continue;
                }
                if (visited[cur.pos.X()][cur.pos.Y()]) {
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
                    if (!visited[nx][ny] && mapData[nx][ny] != '#' && mapData[nx][ny] != '*' && !visitedFrame[nx][ny].contains(frameNumber+cur.g+1) && !visitedFrame[nx][ny].contains(frameNumber+cur.g) && !isCollidingWithOtherRobots(nx, ny, robot.getId())) {

                        Pos next = new Pos(nx, ny);
                        pq.offer(new Node(next, cur.g + 1, manhattanDistance(next, goal), cur, func));
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
        // 寻找可行的邻居节点

        private List getPath(Node end) {
            List<Pos> path = new ArrayList<>();
            Node cur = end;
            while (cur != null) {
                visitedFrame[cur.pos.X()][cur.pos.Y()].add(frameNumber+cur.g);
                path.add(cur.pos);
                cur = cur.parent;
            }
            Collections.reverse(path);
            return path;
        }

        private int manhattanDistance(Pos a, Pos b) {
            return Math.abs(a.X() - b.X()) + Math.abs(a.Y() - b.Y());
        }

        private boolean isCollidingWithOtherRobots(int x, int y, int id) {
            // 检查坐标是否与其他机器人冲突
            for (Robot robot : robots) {
                if (robot.getId() == id || robot.getNextPos() == null) continue;
                if (robot.getNextPos().X() == x && robot.getNextPos().Y() == y) {
                    return true;
                }
            }
            return false;
        }

        public class Node implements Comparable<Node> {
            Pos pos;
            int g, h;
            Node parent;

            Heuristic func = new Heuristic.Normal();

            Node(Pos pos, int g, int h, Node parent) {
                this.pos = pos;
                this.g = g;
                this.h = h;
                this.parent = parent;
            }

            Node(Pos pos, int g, int h, Node parent, Heuristic func) {
                this.pos = pos;
                this.g = g;
                this.h = h;
                this.parent = parent;
                this.func = func;
            }

            public int f() {
                return func.f(this);
            }

            @Override
            public int compareTo(Node other) {
                return Integer.compare(this.f(), other.f());
            }


        }


    }

    public class BidirectionalAStar implements PlanPath {

        private static class Node {
            int x;
            int y;
            int g;  // 从起点到当前节点的实际代价
            int h;  // 从当前节点到终点的估计代价
            Node parent;

            Node(int x, int y) {
                this.x = x;
                this.y = y;
                this.g = 0;
                this.h = 0;
                this.parent = null;
            }

            int getF() {
                return g + h;
            }
        }

        private static char[][] grid;  // 地图网格
        private static int gridSizeX;  // 网格大小X
        private static int gridSizeY;  // 网格大小Y

        private static PriorityQueue<Node> openSetForward;  // 前向搜索的开放集合
        private static PriorityQueue<Node> openSetBackward;  // 后向搜索的开放集合
        //private static Set<Node> closedSetForward;  // 前向搜索的关闭集合
        //private static Set<Node> closedSetBackward;  // 后向搜索的关闭集合
        Robot[] robots;

        @Override
        public void plan(Frame frame) {

            grid = frame.getMap().getMapData();
            gridSizeX = Cons.MAP_SIZE;
            gridSizeY = Cons.MAP_SIZE;

            robots = frame.getRobots();
            for (Robot robot : robots) {
                if (robot.getState() == 0) {
                    continue;
                }
                Pos start = robot.getPos();
                Pos goal = robot.getTargetPos();

                if (robot.getPathList() != null && !robot.getPathList().isEmpty()) {
                    robot.stepOnce();
                } else {
                    //更新路径的情况，分配到港口、分配的货物消失、
                    if (goal == null) {
                        continue;
                    }
                    Node startNode = new Node(start.X(), start.Y());
                    Node endNode = new Node(goal.X(), goal.Y());

                    // 执行双向A*搜索
                    Node[] intersectionNode = bidirectionalAStar(startNode, endNode);

                    // 输出最短路径
                    List<Pos> path = null;
                    if (intersectionNode != null) {
                        path = reconstructPath(intersectionNode);
                    }
                    if (path != null) {
                        robot.setPathList(robot.getId(), path);
                        robot.stepOnce();
                    }

                }

            }

        }


        private static Node[] bidirectionalAStar(Node startNode, Node endNode) {
            openSetForward = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
            openSetBackward = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
            //closedSetForward = new HashSet<>();
            //closedSetBackward = new HashSet<>();
            boolean[][] visitedStart = new boolean[gridSizeX][gridSizeY];
            //boolean[][] visitedBack = new boolean[gridSizeX][gridSizeY];
            Node[][] visitedBack = new Node[gridSizeX][gridSizeY];
            startNode.g = 0;
            startNode.h = calculateHeuristic(startNode, endNode);
            openSetForward.add(startNode);

            endNode.g = 0;
            endNode.h = calculateHeuristic(endNode, startNode);
            openSetBackward.add(endNode);

            while (!openSetForward.isEmpty() && !openSetBackward.isEmpty() && openSetForward.size() < Cons.PRIORITY_QUEUE_SIZE / 2) {
                Node currentForward = openSetForward.poll();
                //closedSetForward.add(currentForward);
                visitedStart[currentForward.x][currentForward.y] = true;


                Node currentBackward = openSetBackward.poll();
                //closedSetBackward.add(currentBackward);
                visitedBack[currentBackward.x][currentBackward.y] = currentBackward;

                if (visitedBack[currentForward.x][currentForward.y] != null) {
                    return new Node[]{currentForward, visitedBack[currentForward.x][currentForward.y]};  // 找到相交节点
                }

//                if (visitedStart[currentBackward.x][currentBackward.y]) {
//                    return currentBackward;  // 找到相交节点
//                }

                exploreNeighbors(currentForward, openSetForward, visitedStart, endNode);
                exploreNeighbors(currentBackward, openSetBackward, visitedBack, startNode);
            }

            return null;  // 没有找到路径
        }

        private static void exploreNeighbors(Node current, PriorityQueue<Node> openSet, boolean[][] visited, Node endNode) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direction : directions) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                if (isValidPosition(neighborX, neighborY)) {
                    Node neighbor = new Node(neighborX, neighborY);
                    neighbor.parent = current;
                    neighbor.g = current.g + 1;
                    neighbor.h = calculateHeuristic(neighbor, endNode);

                    if (visited[neighborX][neighborY]) {
                        continue;  // 已在关闭集合中，跳过
                    }

                    Node existingNode = findNodeInOpenSet(neighbor, openSet);
                    if (existingNode == null) {
                        openSet.add(neighbor);  // 添加到开放集合
                    } else {
                        if (neighbor.g < existingNode.g) {
                            existingNode.parent = current;
                            existingNode.g = neighbor.g;
                        }
                    }
                }
            }
        }

        private static void exploreNeighbors(Node current, PriorityQueue<Node> openSet, Node[][] visited, Node endNode) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direction : directions) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                if (isValidPosition(neighborX, neighborY)) {
                    Node neighbor = new Node(neighborX, neighborY);
                    neighbor.parent = current;
                    neighbor.g = current.g + 1;
                    neighbor.h = calculateHeuristic(neighbor, endNode);

                    if (visited[neighborX][neighborY] != null) {
                        continue;  // 已在关闭集合中，跳过
                    }

                    Node existingNode = findNodeInOpenSet(neighbor, openSet);
                    if (existingNode == null) {
                        openSet.add(neighbor);  // 添加到开放集合
                    } else {
                        if (neighbor.g < existingNode.g) {
                            existingNode.parent = current;
                            existingNode.g = neighbor.g;
                        }
                    }
                }
            }
        }

        private static Node findNodeInOpenSet(Node node, PriorityQueue<Node> openSet) {
            for (Node n : openSet) {
                if (n.x == node.x && n.y == node.y) {
                    return n;
                }
            }
            return null;
        }

        private static List<Pos> reconstructPath(Node[] intersectionNode) {
            List<Pos> path = new ArrayList<>();
            Node currentNode = intersectionNode[0];
            //Pos cur = new Pos(currentNode.x,currentNode.y);
            while (currentNode != null && currentNode.parent != null) {
                path.add(new Pos(currentNode.x, currentNode.y));
                currentNode = currentNode.parent;
            }

            // 反转路径，使其从起点到相交节点
            Collections.reverse(path);

            // 继续从相交节点回溯到终点
            currentNode = intersectionNode[1];
            while (currentNode != null && currentNode.parent != null) {
                currentNode = currentNode.parent;
                path.add(new Pos(currentNode.x, currentNode.y));
            }

            return path;
        }


        private static int calculateHeuristic(Node node, Node targetNode) {
            // 使用曼哈顿距离作为估计代价
            return Math.abs(node.x - targetNode.x) + Math.abs(node.y - targetNode.y);
        }

        private static boolean isValidPosition(int x, int y) {
            return x >= 0 && x < gridSizeX && y >= 0 && y < gridSizeY && grid[x][y] != '#' && grid[x][y] != '*';
        }
    }

    public class blockPlanPath implements PlanPath {

        /**
         * - 根据blocks路径，只在每个block内对borders的位置进行a*或直接bfs（最多25*25次操作，可以接受，应该不会跳帧？）
         * - path不为空的机器人继续行进
         * - 进入另一个block后重新规划路径，这里需要判断目标货物消失则重新分配目标
         * - 在泊位放下后重新分配目标
         * - 对于同一个block内的机器人考虑冲突，每一步（一个新的g）对同一个block的已存在的robotpath进行遍历，将同一时间经过的同一个位置避开
         *
         * @param frame
         */
        @Override
        public void plan(Frame frame) {
            Robot[] robots = frame.getRobots();
            for (Robot robot : robots) {
                // 恢复中
                if (robot.getState() == 0) {
                    continue;
                }
                if (robot.getTargetPos() == null) {
                    continue;
                }
                //已有路径直接继续
                if (robot.getPathList() != null && !robot.getPathList().isEmpty()) {
                    robot.stepOnce();
                } else {
                    // 需要计算路径的情况
                    Pos pos = robot.getPos();
                    int thisBlockId = Block.getBlockId(pos);
                    Block thisBlock = Block.blocks[thisBlockId];
                    //目标就在本块内
                    if (Block.getBlockId(robot.getTargetPos()) == thisBlockId) {
                        List ends = new ArrayList<>();
                        //todo:ends加入泊位的4*4范围
                        ends.add(robot.getTargetPos());
                        List<Pos> path = findPathInBlock(robot, ends, frame);
                        if (path != null) {
                            robot.setPathList(path);
                            robot.stepOnce();
                        } else {
                            //todo:没有找到路径，直接使用A*寻整个路，但无法考虑碰撞
                            continue;
                        }
                    } else {
                        int nextBlockId;
                        if (robot.getNextBlock() == null) {
                            nextBlockId = -1000;
                        } else {
                            nextBlockId = robot.getNextBlock().getId();
                        }

                        int direction = Block.getDirection(thisBlockId, nextBlockId);
                        if (direction != -1) {
                            //处在边界，直接走一步进入另一个block
                            //todo:考虑另一个块机器人对冲带来的碰撞
                            if (Block.atBorder(pos, direction)) {
                                if (robot.getBlocksList().isEmpty()) {
                                    robot.setNextBlock(null);
                                } else {
                                    robot.setNextBlock((Block) robot.getBlocksList().remove(0));
                                }
                                robot.setPathList(null);
                                Pos nextPos = new Pos(pos.X() + Cons.dx[direction], pos.Y() + Cons.dy[direction]);
                                robot.setPath(nextPos);
                                continue;
                            }
                            //在本块内移动到对应方向的边界
                            else {
                                List borders = thisBlock.getBorders(frame.getMap().getAreaId(robot), direction);
                                List<Pos> path = findPathInBlock(robot, borders, frame);
                                if (path != null) {
                                    robot.setPathList(path);
                                    robot.stepOnce();
                                } else {
                                    //todo:没有找到路径，直接使用A*寻整个路，但无法考虑碰撞
                                }
                            }
                        }
                    }
                }
            }

        }

        private List<Pos> findPathInBlock(Robot robot, List<Pos> ends, Frame frame) {
            int areaId = frame.getMap().getAreaId(robot);
            List<Pos> path;
            boolean visited[][] = new boolean[Cons.BLOCK_SIZE][Cons.BLOCK_SIZE];
            PriorityQueue<Node> pq = new PriorityQueue<>();
            Pos start = robot.getPos();
            pq.offer(new Node(start, 0, heuristic(start, ends.get(0)), null));
            visited[start.X() % Cons.BLOCK_SIZE][start.Y() % Cons.BLOCK_SIZE] = true;
            //左闭右开
            int minX = Block.getBlockId(start) / Cons.BLOCK_HEIGHT * Cons.BLOCK_SIZE;
            int minY = Block.getBlockId(start) % Cons.BLOCK_WIDTH * Cons.BLOCK_SIZE;
            int maxX = minX + Cons.BLOCK_SIZE;
            int maxY = minY + Cons.BLOCK_SIZE;
            while (!pq.isEmpty() && pq.size() < Cons.BLOCK_SIZE * Cons.BLOCK_SIZE) {
                Node cur = pq.poll();
                for (int i = 0; i < ends.size(); i++) {
                    if (ends.get(i).equals(cur.pos)) {
                        path = getPath(cur);
                        if (path.get(0).equals(start)) {
                            path.remove(0);
                        }
                        return path;
                    }
                }
                for (int i = 0; i < 4; i++) {
                    int nx = cur.pos.X() + Cons.dx[i], ny = cur.pos.Y() + Cons.dy[i];
                    if (nx < minX || nx >= maxX || ny < minY || ny >= maxY) {
                        continue;
                    }
                    if (!visited[nx % Cons.BLOCK_SIZE][ny % Cons.BLOCK_SIZE] && !frame.getMap().isObstacle(nx, ny)) {
                        Pos next = new Pos(nx, ny);
                        //在加入新一个节点时，需要考虑是否会与本块内的其他机器人冲突、
                        //g==1即为第一步
                        if (checkCollision(next, cur.g + 1, frame)) {
                            continue;
                        }
                        pq.offer(new Node(next, cur.g + 1, heuristic(next, ends.get(0)), cur));
                        visited[nx % Cons.BLOCK_SIZE][ny % Cons.BLOCK_SIZE] = true;
                    }
                }
            }
            return null;
        }

        private boolean checkCollision(Pos pos, int step, Frame frame) {
            int blockId = Block.getBlockId(pos);
            for (Robot robot : frame.getRobots()) {
                if (Block.getBlockId(robot.getPos()) == blockId && robot.getPathList() != null && !robot.getPathList().isEmpty()) {
                    if (robot.getPathList().size() <= step) {
                        continue;
                    }
                    if (robot.getPathList().get(step).equals(pos)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private List<Pos> getPath(Node end) {
            List<Pos> path = new ArrayList<>();
            Node cur = end;
            while (cur != null) {
                path.add(cur.pos);
                cur = cur.parent;
            }
            Collections.reverse(path);
            return path;
        }

        class Node implements Comparable<Node> {
            Pos pos;
            /**
             * 深度，步时
             */
            int g;
            /**
             * 启发式函数值
             */
            int h;
            Node parent;

            Node(int x, int y) {
                this.pos = new Pos(x, y);
                this.g = 0;
                this.h = 0;
                this.parent = null;
            }

            Node(Pos pos) {
                this.pos = pos;
                this.g = 0;
                this.h = 0;
                this.parent = null;
            }

            Node(Pos pos, int g, int h, Node parent) {
                this.pos = pos;
                this.g = g;
                this.h = h;
                this.parent = parent;
            }

            int f() {
                //启发式函数
                return g + 5 * h;
            }

            @Override
            public int compareTo(Node o) {
                return Integer.compare(this.f(), o.f());
            }
        }

        private int heuristic(Pos start, Pos end) {
            return Math.abs(start.X() - end.X()) + Math.abs(start.Y() - end.Y());
        }
    }
}
