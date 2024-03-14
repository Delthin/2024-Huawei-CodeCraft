package com.huawei.codecraft;

/**
 * Map
 * 表示地图对象,包含地图数据和障碍物信息。
 * 属性:地图数据(二维字符数组)
 */

//‘.’ ： 空地
//‘*’ ： 海洋
//‘#’ ： 障碍
//‘A’ ： 机器人起始位置，总共 10 个。
//‘B’ ： 大小为 4*4，表示泊位的位置,泊位标号在后泊位处初始化。

//'R' ： 表示机器人目前位置，总共 10 个。
//‘G’ ： 表示有货物的位置
public class Map {
    private char[][] mapData;
    private int[][] area;
    private int areaNum = 0;

    public Map() {
        this.mapData = new char[Cons.MAP_SIZE][Cons.MAP_SIZE];
        this.area = new int[Cons.MAP_SIZE][Cons.MAP_SIZE];
    }

    public Map(String[] mapData) {
        this.mapData = new char[mapData.length][mapData[0].length()];
        for (int i = 0; i < mapData.length; i++) {
            this.mapData[i] = mapData[i].toCharArray();
        }
        this.area = new int[Cons.MAP_SIZE][Cons.MAP_SIZE];
    }

    /**
     * 复制地图从src到dst
     *
     * @param src
     */
    public static Map duplicateMap(Map src) {
        Map dst = new Map();
        for (int i = 0; i < Cons.MAP_SIZE; i++) {
            System.arraycopy(src.mapData[i], 0, dst.mapData[i], 0, Cons.MAP_SIZE);
//            System.arraycopy(src.area[i], 0, dst.area[i], 0, Cons.MAP_SIZE);
        }
        return dst;
    }
    public char[][] getMapData(){
        return this.mapData;
    }
    public boolean isObstacle(Pos pos) {
        return mapData[pos.X()][pos.Y()] == '#' || mapData[pos.X()][pos.Y()] == '*';
    }
    public boolean isObstacle(int x,int y) {
        return mapData[x][y] == '#' || mapData[x][y] == '*';
    }

    public boolean isRobot(Pos pos) {
        return mapData[pos.X()][pos.Y()] == 'R';
    }

    public boolean isGoods(Pos pos) {
        return mapData[pos.X()][pos.Y()] == 'G';
    }

    public boolean isBerth(Pos pos) {
        return mapData[pos.X()][pos.Y()] == 'B';
    }
    public boolean canMove(Pos pos) {
        return isGoods(pos) || isBerth(pos) || mapData[pos.X()][pos.Y()] == '.' || mapData[pos.X()][pos.Y()] == 'A';
    }
    public char getChar(Pos pos) {
        return mapData[pos.X()][pos.Y()];
    }
    public void setRobot(Pos pos) {
        mapData[pos.X()][pos.Y()] = 'R';
    }
    public void setGoods(Pos pos) {
        mapData[pos.X()][pos.Y()] = 'G';
    }

    public void setMapData(char[][] mapData) {
        this.mapData = mapData;
    }
    public static boolean isValidXY(int x, int y) {
        return x >= 0 && x < Cons.MAP_SIZE && y >= 0 && y < Cons.MAP_SIZE;
    }

    public void setArea(int x, int y, int i) {
        area[x][y] = i;
    }
    public int getAreaId(int x, int y) {
        return area[x][y];
    }
    public int getAreaId(Pos pos){
        return area[pos.X()][pos.Y()];
    }
    public void setAreaNum(int areaNum) {
        this.areaNum = areaNum;
    }
    public int getAreaNum() {
        return areaNum;
    }
    public static boolean isSameArea(Pos a, Pos b) {
        return Main.map.getAreaId(a) == Main.map.getAreaId(b);
    }
}
