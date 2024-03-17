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

    public class bfsAssignTarget implements AssignTarget {
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
            Berth berth = robot.getPos().berth;
//            for (Goods goods : goodsList) {
//                if (!goods.isAssigned() && goods.getPos().berth == berth){
//                    int distance = goods.getPos().bfsWeightsDistance;
//                    double weight = getWeight(goods, distance);
//                    if (weight > maxWeight) {
//                        minDistance = distance;
//                        maxWeight = weight;
//                        closestGoods = goods;
//                    }
//                }
//            }
//            if (closestGoods != null) return closestGoods;
            //第二遍循环找其他港口
            for (Goods goods : goodsList) {
//                if (goods.getValue() < 10) continue;
                if (!goods.isAssigned()) {
                    int distance = getDistance(robot.getPos().Mdistance(goods.getPos()) , goods.getPos().bfsWeightsDistance);
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
            return (double) (goods.getValue() + 200) / (distance);
        }
        private int getDistance(int robotDistance, int BerthDistance) {
            return robotDistance;
        }
    }
}


