package com.huawei.codecraft;


import java.util.List;
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
}


