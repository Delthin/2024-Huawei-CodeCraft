package com.huawei.codecraft;

/**
 * Map
 * 表示地图对象,包含地图数据和障碍物信息。
 * 属性:地图数据(二维字符数组)
 */

public class Map {
    //todo: 对每一帧的暂时地图信息（包括机器人位置，货物位置，船只位置等）在char中选字符表示，并添加getter和setter
    private char[][] mapData;

    public Map() {
        this.mapData = new char[Cons.MAP_SIZE][Cons.MAP_SIZE];
    }
    public Map(String[] mapData) {
        this.mapData = new char[mapData.length][mapData[0].length()];
        for (int i = 0; i < mapData.length; i++) {
            this.mapData[i] = mapData[i].toCharArray();
        }
    }

    /**
     * 复制地图从src到dst
     * @param src
     */
    public static Map duplicateMap(Map src){
        Map dst = new Map();
        for (int i = 0; i < Cons.MAP_SIZE; i++) {
            System.arraycopy(src.mapData[i], 0, dst.mapData[i], 0, Cons.MAP_SIZE);
        }
        return dst;
    }
//    public boolean isObstacle(int x, int y) {
//        return mapData[x][y] == '#';
//    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < mapData.length && y >= 0 && y < mapData[0].length;
    }
}
