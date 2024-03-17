package com.huawei.codecraft;

public class Print {
    public static void printBerthInfo(Frame frame){
        Berth[] berths = frame.getBerths();
        for (Berth berth : berths) {
            int id = berth.getId();
            Pos pos = berth.getPos();
            int transportTime = berth.getTransportTime();
            int loadingSpeed = berth.getLoadingSpeed();
            int goodsNum = berth.getGoodsNum();
            System.err.println("Berth id: " + id + " goodsNum: " + goodsNum + " isAssigned: " + berth.isItAssigned() + " pos: " + pos + " transportTime: " + transportTime + " loadingSpeed: " + loadingSpeed);
//            System.err.println("pos: " + pos );
//            System.err.println("transportTime: " + transportTime );
//            System.err.println("loadingSpeed: " + loadingSpeed );
//            System.err.println("goodsNum: " + goodsNum );
        }
    }
    public static void printBoatInfo(Frame frame){
        Boat[] boats = frame.getBoats();
        for (Boat boat : boats) {
            int id = boat.getId();
            int state = boat.getState();
            int targetBerthId = boat.getTargetBerthId();
            int action = boat.getAction();
            int goodsSize = boat.getGoodsSize();
            System.err.println("Boat id: " + id + " state: " + stateToString(state) + " target: " + targetBerthId + " action: " + actionToString(action) + " goods: " + goodsSize);
        }
    }
    public static String stateToString(int state){
        if (state == 0){
            return "MOVING";
        }else if (state == 1){
            return "WAITING";
        }else if (state == 2){
            return "OUTING";
        }
        return "ERROR";
    }
    public static String actionToString(int action){
        if (action == Cons.ACTION_SHIP){
            return "SHIP";
        }else if (action == Cons.ACTION_GO){
            return "GO";
        }else {
            return "WAITING";
        }
    }
}
