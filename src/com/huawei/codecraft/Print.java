package com.huawei.codecraft;

public class Print {
    public static void printBerthInfo(Frame frame) {
        Berth[] berths = frame.getBerths();
        for (Berth berth : berths) {
            int id = berth.getId();
            Pos pos = berth.getPos();
            int transportTime = berth.getTransportTime();
            int loadingSpeed = berth.getLoadingSpeed();
            int goodsNum = berth.getGoodsNum();
            int goodsFlow = berth.getGoodsFlow();
            boolean isDeserted = berth.isDeserted();
            System.err.print("id" + id + ":" + goodsNum + ", " + goodsFlow + " ");
//            System.err.println("Berth id: " + id + " goodsNum: " + goodsNum + " goodsFlow: " + goodsFlow +  " transportTime: " + transportTime + " loadingSpeed: " + loadingSpeed + " isDeserted: " + isDeserted);
//            System.err.println("pos: " + pos );
//            System.err.println("transportTime: " + transportTime );
//            System.err.println("loadingSpeed: " + loadingSpeed );
//            System.err.println("goodsNum: " + goodsNum );
        }
        System.err.println();
    }

    public static void printBoatInfo(Frame frame) {
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

    public static void printRobotInfo(Frame frame) {
        Robot[] robots = frame.getRobots();
        if (frame.getFrameNumber() > 10250 && frame.getFrameNumber() < 10500) {
            System.err.println("frameNo: " + frame.getFrameNumber());
        }
        for (Robot robot : robots) {
            int id = robot.getId();
            Pos pos = robot.getPos();
            int state = robot.getState();
            int responsibleBerthId = robot.getResponsibleBerthId();
            if ((id == 1 || id == 5 || id == 3) && frame.getFrameNumber() > 10250 && frame.getFrameNumber() < 10500) {
                System.err.println("Robot id: " + id + " pos: " + pos + " state: " + stateToString(state) + " action: " + robot.getAction() + " nextPos: " + robot.getNextPos() +
                        " pathlist: " + robot.getPathList());
            }
//            System.err.println("Robot id: " + id + " pos: " + pos + " state: " + stateToString(state) + " responsibleBerthId: " + responsibleBerthId);
        }
    }

    public static void printGoodsInfo(Frame frame) {
        Goods[] goods = frame.getGoods();
        for (Goods good : goods) {
            Pos pos = good.getPos();
            int summonFrame = good.getSummonFrame();
            int value = good.getValue();
            int assigned = good.isAssigned() ? 1 : 0;
            System.err.println("Goods pos: " + pos + " summonFrame: " + summonFrame + " value: " + value + " assigned: " + assigned);
        }
    }

    public static void printValue() {
        System.err.println("Total value: " + Main.totalValue + " Get value: " + Main.getValue + " Berth value: " + Main.berthValue);
    }

    public static String stateToString(int state) {
        if (state == 0) {
            return "MOVING";
        } else if (state == 1) {
            return "WAITING";
        } else if (state == 2) {
            return "OUTING";
        }
        return "ERROR";
    }

    public static String actionToString(int action) {
        if (action == Cons.ACTION_SHIP) {
            return "SHIP";
        } else if (action == Cons.ACTION_GO) {
            return "GO";
        } else {
            return "WAITING";
        }
    }

}
