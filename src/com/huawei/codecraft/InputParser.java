package com.huawei.codecraft;


import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * InputParser
 * 负责解析输入数据,构建相应的对象。
 */
public class InputParser {

    public static Frame parseFrameData() {
        /**
         *
         */
        Scanner scan = new Scanner(System.in);
        if(!scan.hasNext()){
            return null;
        }
        int frameNumber = scan.nextInt();
        int money = scan.nextInt();
        int goodsNumber = scan.nextInt();
        Goods[] goods = new Goods[goodsNumber];
        for (int i = 0; i < goodsNumber; i++) {
            int x = scan.nextInt();
            int y = scan.nextInt();
            int value = scan.nextInt();
            goods[i] = new Goods(x, y, value, frameNumber);
        }
        Robot[] robots = new Robot[Cons.MAX_ROBOT];
        for (int i = 0; i < Cons.MAX_ROBOT; i++){
            int hasGoods = scan.nextInt();
            int x = scan.nextInt();
            int y = scan.nextInt();
            int state = scan.nextInt();
            robots[i] = new Robot(i, hasGoods, x, y, state);
        }
        Boat[] boats = new Boat[Cons.MAX_BOAT];
        for (int i = 0; i < Cons.MAX_BOAT; i++){
            int state = scan.nextInt();
            int targetBerth = scan.nextInt();
            boats[i] = new Boat(i, state, targetBerth);
        }
        Frame frame = new Frame(frameNumber, money);
        frame.updateGoods(goods);
        frame.updateRobots(robots);
        frame.updateBoats(boats);
        frame.updateMap();
//        mapPrint(frame.getMap());
//        if (frameNumber < 2){
//            initResponsibleBerth(frame.getRobots());
//        }
        return frame;
    }
    public static void mapPrint(Map map){
        char[][] mapData = map.getMapData();
        for (int i = 0; i < Cons.MAP_SIZE; i++) {
            for (int j = 0; j < Cons.MAP_SIZE; j++) {
                System.out.print(mapData[i][j]);
            }
            System.out.println();
        }
    }
    public static void initResponsibleBerth(Robot[] robots){
        boolean visited[] = new boolean[Cons.MAX_BERTH];
//        for (Robot robot : robots){
//            robot.setResponsibleBerthId(-1);
//            Berth berth = robot.getPos().berth;
//            if (berth == null){
//                continue;
//            }
//            if (visited[berth.getId()]){
//                continue;
//            }else{
//                visited[berth.getId()] = true;
//                robot.setResponsibleBerthId(berth.getId());
//            }
//        }
        PriorityQueue<Robot> pq = new PriorityQueue<>((robot1, robot2) -> Integer.compare(robot1.getPos().bfsRealDistance, robot2.getPos().bfsRealDistance));
        pq.addAll(Arrays.asList(robots));
        while(pq.size()!=0){
            Robot robot = pq.poll();
            Berth berth = robot.getPos().berth;
            if (berth == null){
                continue;
            }
            if (visited[berth.getId()]){
                continue;
            }else{
                visited[berth.getId()] = true;
                robot.setResponsibleBerthId(berth.getId());
            }
        }//用堆，近的先分配
        for (Robot robot : robots){
            if (robot.getResponsibleBerthId() == -1){
                int minDistance = Integer.MAX_VALUE;
                int minId = -1;
                for (int i = 0; i < Cons.MAX_BERTH; i++){
                    if (!visited[i]){
                        if (robot.getPos().Mdistance(Main.berths[i].getPos()) < minDistance){
                            minDistance = robot.getPos().Mdistance(Main.berths[i].getPos());
                            minId = i;
                        }

                    }
                }
                robot.setResponsibleBerthId(minId);
                visited[minId] = true;
            }
        }
    }
}


