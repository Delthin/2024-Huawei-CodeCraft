/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.codecraft;

import java.util.*;
import java.util.logging.Logger;

/**
 * Main
 *
 * @since 2024-02-05
 */

public class Main {
    public static Map map;
    public static Berth[] berths = new Berth[Cons.MAX_BERTH];

    /**
     * 初始化
     */
    private void init() {
        Scanner scanf = new Scanner(System.in);
        String[] mapData = new String[Cons.MAP_SIZE];
        // 读取地图数据
        for (int i = 0; i < Cons.MAP_SIZE; i++) {
            mapData[i] = scanf.nextLine();
        }
        map = new Map(mapData);
        // 读取港口数据
        for (int i = 0; i < Cons.MAX_BERTH; i++) {
            int id = scanf.nextInt();
            int x = scanf.nextInt();
            int y = scanf.nextInt();
            int transportTime = scanf.nextInt();
            int loadingSpeed = scanf.nextInt();
            berths[id] = new Berth(id, x, y, transportTime, loadingSpeed);
        }
        // 读取船只容量
        Boat.setCapacity(scanf.nextInt());
        scanf.nextLine();//去除最后一个int后的line
        // 读取OK
        String okk = scanf.nextLine();
        assert okk.equals("OK");
        initArea();
        initBlock();
        //这里初始化可能添加别的内容
        //输出OK，初始化结束
        System.out.println("OK");
        System.out.flush();
    }

    /**
     * 处理一帧的数据
     *
     * @param frame
     */
    private void processFrame(Frame frame) {
        /**
         * 按以下顺序对机器人和船只分别处理当前已经读取好的帧
         * 机器人策略
         * 1.分配机器人目标 a.如果机器人有货物，将货物送到目的地 b.如果机器人没有货物，选择最近的货物
         * 2.为每个机器人规划路径，可能包含碰撞处理（CBS基于冲突的搜索算法）
         * 3.决定机器人指令 a.当前处在货物且空闲，捡起货物 b.当前处在停泊点且携带货物，放下货物 c.移动到下一个位置 d.可能停在原地规避碰撞
         * 4.将机器人指令写入Robot类，等待输出（可能修改输出部分，输出到输出缓存区）
         * 船只策略
         * 1.在泊位外等待的船只选择停泊点进入
         * 2.在停泊点的船只 a.等待货物装载 b.前往虚拟点出售 c.前往其他停泊点
         * 3.将机器人指令写入Robot类，等待输出（可能修改输出部分，输出到输出缓存区）
         */

        //todo: 目前采用每一帧都重新计算路径的方式，后续如果跳帧可以考虑优化
        RobotStrategy.process(frame);
        BoatStrategy.process(frame);
    }

    private void initArea() {
        boolean visited[][] = new boolean[Cons.MAP_SIZE][Cons.MAP_SIZE];
        int areaId = 0;
        for (int x = 0; x < Cons.MAP_SIZE; x++) {
            for (int y = 0; y < Cons.MAP_SIZE; y++) {
                // 遍历所有地图位置，划分区域
                // 如果已经访问过或者是障碍物，直接跳过
                if (visited[x][y]) {
                    continue;
                }
                if (map.isObstacle(x, y)) {
                    visited[x][y] = true;
                    map.setArea(x, y, -1);
                    continue;
                }
                int areaSize = 0;
                Queue<Pos> queue = new LinkedList<>();
                queue.add(new Pos(x, y));
                visited[x][y] = true;
                map.setArea(x, y, areaId);
                // 一轮BFS确定一个area
                while (!queue.isEmpty()) {
                    Pos pos = queue.poll();
                    areaSize++;
                    for (int i = 0; i < 4; i++) {
                        int nx = pos.X() + Cons.dx[i];
                        int ny = pos.Y() + Cons.dy[i];
                        if (map.isValidXY(nx, ny) && !visited[nx][ny] && !map.isObstacle(nx, ny)) {
                            visited[nx][ny] = true;
                            map.setArea(nx, ny, areaId);
                            queue.add(new Pos(nx, ny));
                        }
                    }
                }
                if (areaSize > 50){
                areaId++;}
            }
        }
        areaId++;//使areaNum比实际多一，为了在initBlock时游离（小于50）的区域不越界，但不影响判断
        //todo:想办法优化这里
        Main.map.setAreaNum(areaId);
    }

    private void initBlock() {
        Block[] blocks = Block.blocks;
        // 第一轮遍历，新建blocks
        for (int i = 0; i < Cons.BLOCK_WIDTH; i++) {
            for (int j = 0; j < Cons.BLOCK_WIDTH; j++) {
                blocks[i * Cons.BLOCK_WIDTH + j] = new Block(i * Cons.BLOCK_WIDTH + j);
            }
        }
        // 第二轮遍历，确定每个block的邻居和边界可用点
        for (int i = 0; i < Cons.BLOCK_WIDTH; i++) {
            for (int j = 0; j < Cons.BLOCK_WIDTH; j++) {
                Block block = blocks[i * Cons.BLOCK_WIDTH + j];
                List<List<Block>> neighbours = new ArrayList<>();
                List<List<Pos>> bordersLeft = new ArrayList<>();
                List<List<Pos>> bordersRight = new ArrayList<>();
                List<List<Pos>> bordersUp = new ArrayList<>();
                List<List<Pos>> bordersDown = new ArrayList<>();
                for (int areaId = 0; areaId < Main.map.getAreaNum(); areaId++) {
                    neighbours.add(new ArrayList<>());
                    bordersLeft.add(new ArrayList<>());
                    bordersRight.add(new ArrayList<>());
                    bordersUp.add(new ArrayList<>());
                    bordersDown.add(new ArrayList<>());
                }// 四个方向分别遍历此areaId下的边界，如果边界存在，则添加neighbour
                for (int x = i * Cons.BLOCK_SIZE; x < (i + 1) * Cons.BLOCK_SIZE; x++) {
                    for (int y = j * Cons.BLOCK_SIZE; y < (j + 1) * Cons.BLOCK_SIZE; y++) {
                        if (!map.isObstacle(x, y) && Block.isConnected(x, y)) {
                            if (block.atBorder(x, y, Cons.DIRECTION_RIGHT)) {
                                int areaId = Main.map.getAreaId(x, y);
                                bordersRight.get(areaId).add(new Pos(x, y));
                            } else if (block.atBorder(x, y, Cons.DIRECTION_LEFT)) {
                                int areaId = Main.map.getAreaId(x, y);
                                bordersLeft.get(areaId).add(new Pos(x, y));
                            } else if (block.atBorder(x, y, Cons.DIRECTION_UP)) {
                                int areaId = Main.map.getAreaId(x, y);
                                bordersUp.get(areaId).add(new Pos(x, y));
                            } else if (block.atBorder(x, y, Cons.DIRECTION_DOWN)) {
                                int areaId = Main.map.getAreaId(x, y);
                                bordersDown.get(areaId).add(new Pos(x, y));
                            }
                        }
                    }
                }
                // 根据边界是否存在添加neighbours
                for (int areaId = 0; areaId < Main.map.getAreaNum(); areaId++) {
                    if (bordersRight.get(areaId).size() > 0) {
                        neighbours.get(areaId).add(blocks[i * Cons.BLOCK_WIDTH + j + 1]);
                    }if (bordersLeft.get(areaId).size() > 0) {
                        neighbours.get(areaId).add(blocks[i * Cons.BLOCK_WIDTH + j - 1]);
                    }if (bordersUp.get(areaId).size() > 0) {
                        neighbours.get(areaId).add(blocks[(i - 1) * Cons.BLOCK_WIDTH + j]);
                    }if (bordersDown.get(areaId).size() > 0) {
                        neighbours.get(areaId).add(blocks[(i + 1) * Cons.BLOCK_WIDTH + j]);
                    }
                }
                block.setNeighbours(neighbours);
                block.setBordersLeft(bordersLeft);
                block.setBordersRight(bordersRight);
                block.setBordersUp(bordersUp);
                block.setBordersDown(bordersDown);
            }
        }
    }


    public static void main(String[] args) {
        Main mainInstance = new Main();
        mainInstance.init();
        for (int zhen = 1; zhen <= Cons.MAX_FRAME; zhen++) {
            // 读取每一帧的输入数据
            Frame frame = InputParser.parseFrameData();
            mainInstance.processFrame(frame);
            // 根据当前Frame信息输出格式化
            OutputFormatter.formatOutput(frame);
        }
    }
}

//official sdk
//public class Main {
//    private static final int n = 200;
//    private static final int robot_num = 10;
//    private static final int berth_num = 10;
//    private static final int N = 210;
//
//    private int money, boat_capacity, id;
//    private String[] ch = new String[N];
//    private int[][] gds = new int[N][N];
//
//    private Robot[] robot = new Robot[robot_num + 10];
//    private Berth[] berth = new Berth[berth_num + 10];
//    private Boat[] boat = new Boat[10];
//
//    private void init() {
//        Scanner scanf = new Scanner(System.in);
//        for(int i = 1; i <= n; i++) {
//            ch[i] = scanf.nextLine();
//        }
//        for (int i = 0; i < berth_num; i++) {
//            int id = scanf.nextInt();
//            berth[id] = new Berth();
//            berth[id].x = scanf.nextInt();
//            berth[id].y = scanf.nextInt();
//            berth[id].transport_time = scanf.nextInt();
//            berth[id].loading_speed = scanf.nextInt();
//        }
//        this.boat_capacity = scanf.nextInt();
//        String okk = scanf.nextLine();
//        System.out.println("OK");
//        System.out.flush();
//    }
//
//    private int input() {
//        Scanner scanf = new Scanner(System.in);
//        this.id = scanf.nextInt();
//        this.money = scanf.nextInt();
//        int num = scanf.nextInt();
//        for (int i = 1; i <= num; i++) {
//            int x = scanf.nextInt();
//            int y = scanf.nextInt();
//            int val = scanf.nextInt();
//        }
//        for(int i = 0; i < robot_num; i++) {
//            robot[i] = new Robot();
//            robot[i].goods = scanf.nextInt();
//            robot[i].x = scanf.nextInt();
//            robot[i].y = scanf.nextInt();
//            int sts = scanf.nextInt();
//        }
//        for(int i = 0; i < 5; i ++) {
//            boat[i] = new Boat();
//            boat[i].status = scanf.nextInt();
//            boat[i].pos = scanf.nextInt();
//        }
//        String okk = scanf.nextLine();
//        return id;
//    }
//
//    public static void main(String[] args) {
//        Main mainInstance = new Main();
//        mainInstance.init();
//        for(int zhen = 1; zhen <= 15000; zhen ++) {
//            int id = mainInstance.input();
//            Random rand = new Random();
//            for(int i = 0; i < robot_num; i ++)
//                System.out.printf("move %d %d" + System.lineSeparator(), i, rand.nextInt(4) % 4);
//            System.out.println("OK");
//            System.out.flush();
//        }
//    }
//
//    class Robot {
//        int x, y, goods;
//        int status;
//        int mbx, mby;
//
//        public Robot() {}
//
//        public Robot(int startX, int startY) {
//            this.x = startX;
//            this.y = startY;
//        }
//    }
//
//    class Berth {
//        int x;
//        int y;
//        int transport_time;
//        int loading_speed;
//        public Berth(){}
//        public Berth(int x, int y, int transport_time, int loading_speed) {
//            this.x = x;
//            this.y = y;
//            this.transport_time = transport_time;
//            this.loading_speed = loading_speed;
//        }
//    }
//
//    class Boat {
//        int num;
//        int pos;
//        int status;
//    }
//}
