package com.huawei.codecraft;

/**
 * Constants
 * 存放常量值,如地图大小、最大帧数等。
 */
public class Cons {
    public static final int MAP_SIZE = 200;
    public static final int MAX_FRAME = 15000;
    public static final int MAX_BERTH = 10;
    public static final int MAX_BOAT = 5;
    public static final int MAX_ROBOT = 10;
    public static final int BERTH_WIDTH = 4;
    public static final int BOAT_WIDTH = 2;
    public static final int BOAT_HEIGHT = 4;
    public static final int EXPIRE_TIME = 1000;
    public static final int PRIORITY_QUEUE_SIZE = 10000;
    public static final int BLOCK_WIDTH = 8;
    public static final int BLOCK_HEIGHT = 8;
    public static final int BLOCK_SIZE = MAP_SIZE / BLOCK_WIDTH;
    public static final int DIRECTION_RIGHT = 0;
    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_UP = 2;
    public static final int DIRECTION_DOWN = 3;
    public static final int DIRECTION_STOP = -1;
    public static final int ACTION_MOVE = 0;
    public static final int ACTION_GET = 1;
    public static final int ACTION_PULL = 2;
    public static final int ACTION_SHIP = 1;
    public static final int ACTION_GO = 2;
    public static int dx[] = {0, 0, 1, -1};
    public static int dy[] = {1, -1, 0, 0};
//    public static final int MAX_GOODS_WEIGHT = 100;
//    public static final int MAX_GOODS_VALUE = 100;
//    public static final int MAX_GOODS_VOLUME = 100;
//    public static final int MAX_GOODS_DEST = 10;
//    public static final int MAX_GOODS_DEADLINE = 100;
//    public static final int MAX_GOODS_START = 100;
//    public static final int MAX_GOODS_START_PORT = 10;
//    public static final int MAX_GOODS_DEST_PORT = 10;
//    public static final int MAX_PORT_CAPACITY = 100;
//    public static final int MAX_PORT_UNLOAD = 100;
//    public static final int MAX_PORT_LOAD = 100;
//    public static final int MAX_PORT_GOODS = 100;
//    public static final int MAX_BOAT_CAPACITY = 100;
//    public static final int MAX_BOAT_UNLOAD = 100;
//    public static final int MAX_BOAT_LOAD = 100;
//    public static final int MAX_BOAT_GOODS = 100;
//    public static final int MAX_ROBOT_CAPACITY = 100;
//    public static final int MAX_ROBOT_UNLOAD = 100;
//    public static final int MAX_ROBOT_LOAD = 100;
//    public static final int MAX_ROBOT_GOODS = 100;
//    public static final int MAX_ROBOT_SPEED = 100;
//    public static final int MAX_BOAT_SPEED = 100;
//    public static final int MAX_GOODS_SPEED = 100;
//    public static final int MAX_GOODS_PICKUP = 100;
//    public static final int MAX_GOODS_PUTDOWN = 100;
//    public static final int MAX_GOODS_WAIT = 100;
//    public static final int MAX_GOODS_SAIL = 100;
//    public static final int MAX_GOODS_UNLOAD = 100;
//    public static final int MAX_GOODS_LOAD = 100;
//    public static final int MAX_GOODS_MOVE = 100;
//    public static final int MAX_GOODS_PICKUP_TIME = 100;
//    public static final int MAX_GOODS_PUTDOWN_TIME = 100;
}
