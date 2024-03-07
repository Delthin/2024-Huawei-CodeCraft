package com.huawei.model;

/**
 * Map
 * 表示地图对象,包含地图数据和障碍物信息。
 * 属性:地图数据(二维字符数组)
 * 构造方法:Map(String[] mapData)
 * isObstacle(int x, int y)方法:判断指定坐标是否为障碍物
 * isValidCoordinate(int x, int y)方法:判断坐标是否在地图范围内
 */

public class Map {
    private char[][] mapData;

    public Map(String[] mapData) {
        this.mapData = new char[mapData.length][mapData[0].length()];
        for (int i = 0; i < mapData.length; i++) {
            this.mapData[i] = mapData[i].toCharArray();
        }
    }

    public boolean isObstacle(int x, int y) {
        return mapData[x][y] == '#';
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < mapData.length && y >= 0 && y < mapData[0].length;
    }
}
