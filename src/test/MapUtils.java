package test;

import com.huawei.codecraft.Cons;
import com.huawei.codecraft.Map;
import com.huawei.codecraft.Pos;

public class MapUtils {
    public static Map mapInit(){
        Map map = new Map();
        char[][] mapData = new char[Cons.MAP_SIZE][Cons.MAP_SIZE];
        for (int i = 0; i < Cons.MAP_SIZE; i++) {
            for (int j = 0; j < Cons.MAP_SIZE; j++) {
                mapData[i][j] = '.';
            }
        }
        map.setMapData(mapData);
        return map;
    }
    public static void mapSetObstacle(Map map, Pos pos){
        map.getMapData()[pos.X()][pos.Y()] = '#';
    }
    public static void mapSetRobot(Map map, Pos pos){
        map.getMapData()[pos.X()][pos.Y()] = 'R';
    }
    public static void mapSetGoods(Map map, Pos pos){
        map.getMapData()[pos.X()][pos.Y()] = 'G';
    }
    public static void mapSetBerth(Map map, Pos pos){
        map.getMapData()[pos.X()][pos.Y()] = 'B';
    }
    public static void mapSetOcean(Map map, Pos pos){
        map.getMapData()[pos.X()][pos.Y()] = '*';
    }
    public static void mapSetStart(Map map, Pos pos){
        map.getMapData()[pos.X()][pos.Y()] = 'A';
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
