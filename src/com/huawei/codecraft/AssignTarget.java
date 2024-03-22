package com.huawei.codecraft;

import java.util.*;

public interface AssignTarget {
    /**
     * 分配机器人目标
     * a.如果机器人有货物，将货物送到目的地
     * b.如果机器人没有货物，
     * 1.无目标，选择最近的货物(暂定)
     * 2.有目标，前往目标
     */
    void assign(Frame frame);

    public class greedyAssignTarget implements AssignTarget {
        int frameNumber;

        @Override
        public void assign(Frame frame) {
            frameNumber = frame.getFrameNumber();
            Robot[] robots = frame.getRobots(); // 获取机器人列表
            Goods[] goodsList = frame.getGoods(); // 获取货物列表
            Berth[] berths = frame.getBerth();
            for (Robot robot : robots) {
                if (robot.getState() == 0) continue;
                if (robot.isHasGoods() && robot.targetBerth == null) {
                    Berth closestBerth = findClosestBerth(robot, berths);
                    if (closestBerth != null) {
                        robot.assignTargetBerth(closestBerth);
                    }
                } else {
                    if (robot.hasPath() || robot.getTargetPos() != null) {
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
                if (goods.getValue() < 20) continue;
                if (!goods.isAssigned()) {
                    double distance = robot.getPos().Mdistance(goods.getPos());
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestGoods = goods;
                    }
                }
            }
            if (frameNumber <= 10 && minDistance > Cons.MAX_DISTANCE) return null;
            return closestGoods;
        }


        private Berth findClosestBerth(Robot robot, Berth[] berths) {
            Berth nearestPos = null;
            double minDistance = Double.MAX_VALUE;

            for (Berth berth : berths) {
                double distance = robot.getPos().Mdistance(berth.getPos());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPos = berth;
                }
            }

            return nearestPos;
        }
    }

    public class blockAssignTarget implements AssignTarget {
        /**
         * 物品：对每个Robot，bfs找到在同一area最近（可以考虑价值？）的goods（或由Goods反向遍历可以避免重复分配），记录经历的blocks给机器人存储
         * 泊位：直接bfs找或其他策略
         *
         * @param frame
         */
        //todo:已知缺陷，Block中有隔断无法正确运行
        @Override
        public void assign(Frame frame) {
            for (Robot robot : frame.getRobots()) {
                //恢复中
                if (robot.getState() == 0) continue;
                //有货物
                if (robot.isHasGoods()) {
                    if (robot.targetBerth == null) {
                        robot.clearPathList();
                        Berth closestBerth = findClosestBerth(robot, frame.getBerth(), frame);
                        if (closestBerth != null) {
                            robot.assignTargetBerth(closestBerth);
                            robot.blockOnce();
                        }
                    } else {
                        continue;
                    }
                }
                //没有货物
                else {
                    //没有目标和目标过期（如果分配时间长，可以调整目标过期判断）
                    if (robot.getTargetGoods() == null || robot.getTargetGoods().expired(frame.getFrameNumber())) {
                        robot.clearPathList();
                        Goods closestGoods = findClosestGoods(robot, frame.getGoods(), frame);
                        if (closestGoods != null) {
                            robot.assignTargetGoods(closestGoods);
                            closestGoods.setAssigned(true);
                            robot.blockOnce();
                        }
                    } else {
                        continue;
                    }
                }
            }
        }

        private Goods findClosestGoods(Robot robot, Goods[] goodsList, Frame frame) {
            int areaId = frame.getMap().getAreaId(robot);
            List<Pos> ends = new ArrayList<>();
            for (Goods goods : goodsList) {
                //同一个块，直接分配
                if (Block.getBlockId(goods.getPos()) == Block.getBlockId(robot.getPos())) {
                    return goods;
                }
                if (frame.getMap().getAreaId(goods) == areaId && !goods.isAssigned()) {
                    ends.add(goods.getPos());
                }
            }
            List<Block> blocks = bfsBlock(areaId, robot.getPos(), ends);
            if (blocks == null || blocks.isEmpty()) {
                return null;
            }
            robot.setBlocksList(blocks);
            Block endBlock = blocks.get(blocks.size() - 1);
            for (Goods goods : goodsList) {
                if (frame.getMap().getAreaId(goods) == areaId && Block.getBlockId(goods.getPos()) == endBlock.getId()) {
                    return goods;
                }
            }
            return null;
        }

        private Berth findClosestBerth(Robot robot, Berth[] berths, Frame frame) {
            int areaId = frame.getMap().getAreaId(robot);
            List<Pos> ends = new ArrayList<>();
            for (Berth berth : berths) {
                if (Block.getBlockId(berth.getPos()) == Block.getBlockId(robot.getPos())) {
                    return berth;
                }
                if (frame.getMap().getAreaId(berth) == areaId) {
                    ends.add(berth.getPos());
                }
            }
            List<Block> blocks = bfsBlock(areaId, robot.getPos(), ends);
            if (blocks == null || blocks.isEmpty()) {
                return null;
            }
            robot.setBlocksList(blocks);
            Block endBlock = blocks.get(blocks.size() - 1);
            for (Berth berth : berths) {
                if (frame.getMap().getAreaId(berth) == areaId && Block.getBlockId(berth.getPos()) == endBlock.getId()) {
                    return berth;
                }
            }
            return null;
        }

        /**
         * bfs从start到end在areaId里经过的block，不含含start所在块本身
         *
         * @param areaId
         * @param start
         * @param ends
         * @return
         */
        private List<Block> bfsBlock(int areaId, Pos start, List<Pos> ends) {
            List<Block> blocks = new ArrayList<>();
            int startId = Block.getBlockId(start);
            List<Integer> endIds = new ArrayList<>();
            for (Pos end : ends) {
                endIds.add(Block.getBlockId(end));
            }
            boolean find = false;
            boolean[] visited = new boolean[Cons.BLOCK_HEIGHT * Cons.BLOCK_WIDTH];
            int[] cameFrom = new int[Cons.BLOCK_HEIGHT * Cons.BLOCK_WIDTH];
            Queue<Block> queue = new LinkedList<>();
            queue.add(Block.blocks[startId]);
            visited[startId] = true;
            cameFrom[startId] = -1;
            while (!queue.isEmpty()) {
                Block block = queue.poll();
                if (endIds.contains(block.getId())) {
                    find = true;
                    int id = block.getId();
                    while (id != -1) {
                        blocks.add(Block.blocks[id]);
                        id = cameFrom[id];
                    }
                    Collections.reverse(blocks);
                    if (blocks.get(0) == Block.blocks[startId]) {
                        blocks.remove(0);
                    }
                    break;
                }
                for (Block nextBlock : block.getNeighbours(areaId)) {
                    if (!visited[nextBlock.getId()]) {
                        queue.add(nextBlock);
                        visited[nextBlock.getId()] = true;
                        cameFrom[nextBlock.getId()] = block.getId();
                    }
                }
            }
            if (!find) {
                return null;
            }
            return blocks;
        }
    }

    public class r1b1AssignTarget implements AssignTarget {
        int frameNumber;

        @Override
        public void assign(Frame frame) {
            frameNumber = frame.getFrameNumber();
            Robot[] robots = frame.getRobots(); // 获取机器人列表
            Goods[] goodsList = frame.getGoods(); // 获取货物列表
            assignRecover(robots);
            assignBerth(robots);
            assignByRobot(robots, goodsList);
//            assignByGoods(robots, goodsList);
        }
        private void assignRecover(Robot[] robots) {
            for (Robot robot : robots) {
                if (robot.getState() == 0) {
                    robot.waitRecover();
                }
            }
        }
        private void assignByGoods(Robot[] robots, Goods[] goodsList) {
            //垃圾策略，别用
            Arrays.sort(goodsList, new Comparator<Goods>() {
                @Override
                public int compare(Goods o1, Goods o2) {
                    return o2.getValue() - o1.getValue();
                }
            });
            for (Goods goods : goodsList) {
                if (goods.isAssigned()) continue;
                int minDistance = Integer.MAX_VALUE;
                Robot closestRobot = null;
                for (Robot robot : robots) {
                    if (robot.getState() == 0 || robot.hasPath() || robot.isHasGoods() || robot.getTargetPos() != null) continue;
                    int distance = getDistance(robot.getPos().Mdistance(goods.getPos()) , goods.getPos().bfsWeightsDistance);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestRobot = robot;
                    }
                }
                if (closestRobot != null) {
                    closestRobot.assignTargetGoods(goods);
                    goods.setAssigned(true);
                }
            }
        }
        private void assignBerth(Robot[] robots) {
            for (Robot robot : robots) {
                if (robot.getState() == 0) continue;
                Berth berth = robot.getPos().berth;
                if (robot.isHasGoods() && robot.targetBerth == null) {
                    if (berth != null) {
                        robot.assignTargetBerth(berth);
                    }
                }
            }
        }
        private void assignByRobot(Robot[] robots, Goods[] goodsList) {
            for (Robot robot : robots) {
                if (robot.isHasGoods()){
                } else {
                    if (robot.hasPath() || robot.getTargetPos() != null) {
                        continue;
                    }
                    Goods bestGoods = findBestGoods(robot, goodsList);
                    if (bestGoods != null) {
                        robot.assignTargetGoods(bestGoods);
                        bestGoods.setAssigned(true);
                    }
                }
            }
        }
        private Goods findBestGoods(Robot robot, Goods[] goodsList) {
            Goods closestGoods = null;
            int minDistance = Integer.MAX_VALUE;
            double maxWeight = Double.MIN_VALUE;
            //第一遍循环找同港口
            Berth berth = Main.berths[robot.getResponsibleBerthId()];
            for (Goods goods : goodsList) {
                if (!goods.isAssigned() && goods.getPos().berth == berth){
                    int distance = goods.getPos().bfsWeightsDistance;
                    //if(distance>200)continue;//todo:调参

                    double weight = getWeight(goods, distance);
                    if (weight > maxWeight) {
                        minDistance = distance;
                        maxWeight = weight;
                        closestGoods = goods;
                    }
                }
            }
            if (closestGoods != null) return closestGoods;

            //第二遍循环找其他港口
            for (Goods goods : goodsList) {
//                if (goods.getValue() < 10) continue;
                if (!goods.isAssigned()) {
                    int distance = getDistance(robot.getPos().Mdistance(goods.getPos()) , goods.getPos().bfsWeightsDistance);
                    //if(distance>350)continue;//todo:调参
                    double weight = getWeight(goods, distance);
                    if (weight > maxWeight) {
                        minDistance = distance;
                        maxWeight = weight;
                        closestGoods = goods;
                    }
                }
            }
//            if (minDistance > Cons.MAX_DISTANCE) return null;
            return closestGoods;
        }
        private double getWeight(Goods goods, int distance) {
            return (double) (goods.getValue() ) / (distance);
        }
        private int getDistance(int robotDistance, int BerthDistance) {
            return robotDistance + BerthDistance;
        }
        private Goods findClosestGoods(Robot robot, Goods[] goodsList) {
            Goods closestGoods = null;
            double minDistance = Double.MAX_VALUE;

            for (Goods goods : goodsList) {
                if(goods.getValue() < 20)continue;
                if (!goods.isAssigned()) {
                    double distance = robot.getPos().Mdistance(goods.getPos());
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestGoods = goods;
                    }
                }
            }
            if(frameNumber<=10 && minDistance>Cons.MAX_DISTANCE)return null;
            return closestGoods;
        }

    }
    public class planPathAssignTarget implements AssignTarget{
        static int frameNumber;
        static int frameNumberReal;
        Berth[] berths;
        Pos[][] mapPos;
        private static final HashSet<Integer>[][] visitedRecord = Main.visitedRecord;
        static char[][] mapData;
        Frame frame0;
        Goods[] goodsList;
        @Override
        public void assign(Frame frame) {
            frame0=frame;
            frameNumber = Main.frameNumberLocal;
            frameNumberReal = frame.getFrameNumber();
            Robot[] robots = frame.getRobots(); // 获取机器人列表
            goodsList = frame.getGoods(); // 获取货物列表
            berths = frame.getBerth();
            mapPos=Main.mapPos;
            mapData=frame.getMap().getMapData();
            //存下当前位置，防撞
            for (Robot robot : robots) {
                Pos start = robot.getPos();
                visitedRecord[start.X()][start.Y()].add(frameNumber);
                if (robot.getState() == 0) {
                    robot.setPathList(null);
                    for (int i = 1; i < 20; i++) {
                        visitedRecord[start.X()][start.Y()].add(frameNumber + i);//todo:撞傻的机器人应该add不止此帧
                    }
                }
            }
            for (Robot robot : robots) {
                if (robot.hasPath()) {
                    continue;
                }
                if (robot.getState() == 0 && robot.getPos().bfsWeightsDistance == Integer.MAX_VALUE) continue;
                Berth berth = robot.getPos().berth;

                if (robot.isHasGoods()) {//todo:可以简化
                    robot.isFromDesertedArea = false;
                    if (berth != null && berth.isDeserted()) {
                        berth = findClosestBerth(robot);
                        robot.isFromDesertedArea = true;
                    }
                    if (berth != null) {
                        robot.assignTargetBerth(berth);
                    }
                }
            }

            for (Robot robot : robots) {
                //前往港口，下降
                if (robot.isHasGoods() && !robot.isFromDesertedArea && !robot.hasPath() && robot.getPos().bfsWeightsDistance != Integer.MAX_VALUE) {//前往港口
                    Pos start = robot.getPos();
                    Pos next = start;
                    List<Pos> path = new ArrayList<>();
                    int g = 1;
                    while (next.bfsWeightsDistance != 0) {
                        int minDistance = Integer.MAX_VALUE;
                        if (isValidPosition(start.X(), start.Y(), g)) minDistance = start.bfsWeightsDistance;

                        for (int[] direction : Cons.DIRECTIONS) {
                            int neighborX = start.X() + direction[0];
                            int neighborY = start.Y() + direction[1];

                            if (isValidPosition(neighborX, neighborY, g)) {
                                if (mapPos[neighborX][neighborY].bfsWeightsDistance <= minDistance) {
                                    minDistance = mapPos[neighborX][neighborY].bfsWeightsDistance;
                                    next = mapPos[neighborX][neighborY];
                                }
                            }
                        }
                        g++;
                        //if (frameNumber < 200) System.err.println("frame" + (frameNumber) + next);
                        path.add(next);
                        start = next;
                    }
                    g = 1;
                    for (Pos pos : path) {
                        visitedRecord[pos.X()][pos.Y()].add(frameNumber + g);
                        g += 1;
                    }
                    robot.setPathList(path);
                }
            }
            for (Robot robot : robots) {
                if(!robot.isHasGoods() && !robot.hasPath() && robot.getState() != 0 && robot.getPos().bfsWeightsDistance != Integer.MAX_VALUE){
                    //Goods bestGoods = findClosestGoods(robot, goodsList);
                    Goods bestGoods = bfsfindBestGoods(robot);//todo:调参 k
                    if (bestGoods != null) {
                        robot.assignTargetGoods(bestGoods);
                        bestGoods.setAssigned(true);
                        robot.setPathList(reconstructPath(bestGoods.getPos()));
                    }
                }
            }
        }
        private Goods bfsfindBestGoods(Robot robot) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // 上下左右四个方向
            boolean[][] visited = new boolean[Cons.MAP_SIZE][Cons.MAP_SIZE];
            Pos start = robot.getPos();
            int start_x = start.X();
            int start_y = start.Y();
            Queue<Pos> queue = new LinkedList<>();
            PriorityQueue<Goods> targetGoodsPQ = new PriorityQueue<>(Para.bfsAssignHeapComparator);//todo
            Pos curr=mapPos[start_x][start_y];
            curr.tempg=0;
            curr.tempParent =null;
            queue.offer(curr);
            visited[start_x][start_y] = true;
            boolean isStart = true;
            while (!queue.isEmpty() && targetGoodsPQ.size() < Para.bfsAssignHeapCapacity  && curr.tempg<Para.bfsMaxdistance) {
                //if(frameNumber<100 && frameNumber>50)System.err.println("frame: "+frameNumber + "   R id: "+robot.getId() + "queueNum"+ queue.size());

                curr = queue.poll();
                int x = curr.X();
                int y = curr.Y();
                if (!isValidPosition(x,y, curr.tempg) && !isStart) continue;
                isStart=false;
                //if(curr.goods!=null && frameNumber<500)System.err.println("frame: "+frameNumber + "   R id: "+robot.getId()+"   goods: "+curr.goods.getPos());
                if (curr.goods!=null && !curr.goods.isAssigned() && curr.goods.getSummonFrame()+1000>frameNumberReal + curr.tempg && curr.goods.getValue() > Para.IGNORE_VALUE){
                    //if(frameNumber<500)System.err.println("frame: "+frameNumber + "   R id: "+robot.getId()+"   goods: "+curr.goods.getPos());
                    targetGoodsPQ.add(curr.goods);
                }
                for (int[] dir : directions) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];
                    if (isValidPosition(nx,ny,curr.tempg+1) && !visited[nx][ny]) {
                        Pos next = mapPos[nx][ny];
                        next.tempParent=curr;
                        next.tempg= curr.tempg+1;
                        queue.offer(next);
                        visited[nx][ny] = true;
                    }
                }
            }
            Goods targetGoods = null;
            if(!targetGoodsPQ.isEmpty()){
                targetGoods=targetGoodsPQ.poll();
                //if(frameNumber<13500 &&frameNumber>12500)System.err.println ("frame: "+frameNumber + "   goods: "+targetGoods.getValue()+"  d "+(targetGoods.getPos().tempg+targetGoods.getPos().bfsRealDistance) +"remainT: "+(targetGoods.getSummonFrame()+1000-frameNumberReal));
            }
            return targetGoods;
        }
        private static boolean isValidPosition(int x, int y ,int g) {

            return x >= 0 && x < Cons.MAP_SIZE && y >= 0 && y < Cons.MAP_SIZE && mapData[x][y] != '#' && mapData[x][y] != '*' && !visitedRecord[x][y].contains(frameNumber+g) && !visitedRecord[x][y].contains(frameNumber+g-1) ;
        }
        private static List<Pos> reconstructPath(Pos currentNode ) {
            List<Pos> path = new ArrayList<>();
            visitedRecord[currentNode.X()][currentNode.Y()].add(frameNumber+currentNode.tempg+1);
            //Pos cur = new Pos(currentNode.x,currentNode.y);
            while (currentNode != null && currentNode.tempParent != null) {
                visitedRecord[currentNode.X()][currentNode.Y()].add(frameNumber+currentNode.tempg);
                path.add(currentNode);
                currentNode = currentNode.tempParent;
            }
            // 反转路径，使其从起点到相交节点
            Collections.reverse(path);
            return path;
        }
        private Berth findClosestBerth(Robot robot) {
            Berth nearestPos = null;
            double minDistance = Double.MAX_VALUE;

            for (Berth berth : berths) {
                if(berth.isDeserted())continue;
                double distance = robot.getPos().Mdistance(berth.getPos());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPos = berth;
                }
            }

            return nearestPos;
        }
    }
    public class bfsAssignTarget implements AssignTarget{
        int frameNumber;
        Berth[] berths;
        @Override
        public void assign(Frame frame) {
            frameNumber = frame.getFrameNumber();
            Robot[] robots = frame.getRobots(); // 获取机器人列表
            Goods[] goodsList = frame.getGoods(); // 获取货物列表
            berths = frame.getBerth();
            for (Robot robot : robots) {
                if (robot.getState() == 0) continue;
                Berth berth = robot.getPos().berth;
                if (robot.isHasGoods() && robot.targetBerth == null) {
                    if(berth!=null && berth.isDeserted()){
                        berth =findClosestBerth(robot);
                    }
                    if (berth != null) {
                        robot.assignTargetBerth(berth);//此处目标泊位仍是推荐泊位，有无遗弃在planpath判断
                    }
                } else {
                    if (robot.hasPath() || robot.getTargetPos() != null) {
                        continue;
                    }
                    //Goods bestGoods = findClosestGoods(robot, goodsList);
                    Goods bestGoods = findBestGoods(robot, goodsList);
                    if (bestGoods != null) {
                        robot.assignTargetGoods(bestGoods);
                        bestGoods.setAssigned(true);
                    }
                }
            }
        }
        private Goods findBestGoods(Robot robot, Goods[] goodsList) {
            Goods closestGoods = null;
//            int minDistance=Integer.MAX_VALUE;
            double maxWeight = Integer.MIN_VALUE;

            for (Goods goods : goodsList) {
                if(goods.getValue() < Para.IGNORE_VALUE){continue;}
                if (!Map.isSameArea(robot.getPos(), goods.getPos())){
                    continue;
                }
                if (!goods.isAssigned()) {
                    double weight = Para.goodsAssignWeight(robot, goods);
                    if (weight > maxWeight) {
//                        minDistance = distance;
                        maxWeight = weight;
                        closestGoods = goods;
                    }
                }
            }
//            if(frameNumber<=10 && minDistance>2*Cons.MAX_DISTANCE)return null;//TODO:暂时性前期防跳帧
            return closestGoods;
        }
        private Goods findClosestGoods(Robot robot, Goods[] goodsList) {
            Goods closestGoods = null;
            double minDistance = Double.MAX_VALUE;

            for (Goods goods : goodsList) {
                if(goods.getValue() < Para.IGNORE_VALUE)continue;
                if (!goods.isAssigned()) {
                    double distance = robot.getPos().Mdistance(goods.getPos());
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestGoods = goods;
                    }
                }
            }
//            if(frameNumber<=10 && minDistance>Cons.MAX_DISTANCE)return null;
            return closestGoods;
        }
        private Berth findClosestBerth(Robot robot) {
            Berth nearestPos = null;
            double minDistance = Double.MAX_VALUE;

            for (Berth berth : berths) {
                if(berth.isDeserted())continue;
                if(Map.isSameArea(robot.getPos(),berth.getPos())){
                    continue;
                }
                double distance = robot.getPos().Mdistance(berth.getPos());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPos = berth;
                }
            }

            return nearestPos;
        }
    }
}


