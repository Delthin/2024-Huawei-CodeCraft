package com.huawei.codecraft;

/**
 * OutputFormatter
 * 负责格式化输出数据,生成控制指令字符串。
 * formatMoveRobot(int robotId, int direction)方法:格式化机器人移动指令
 * formatPickUpGoods(int robotId)方法:格式化捡起货物指令
 * formatPutDownGoods(int robotId)方法:格式化放下货物指令
 * formatMoveBoat(int boatId, int berthId)方法:格式化移动船只指令
 * formatSailBoat(int boatId)方法:格式化船只运输指令
 */
public class OutputFormatter {
    /**
     * 根据当前帧信息，输出机器人与船只指令
     */
    public static void formatOutput(Frame frame) {
        Robot[] robots = frame.getRobots();
        for (Robot robot : robots) {
            int[] act = robot.getAction();
            if (act[0] == 0) {
                if (robot.getDirection() != -1) {
                    System.out.println("move " + robot.getId() + " " + robot.getDirection());
                }
            }
            if (act[1] == 1 ) {
                System.out.println("get " + robot.getId());
            } else if (act[1] == 2) {
                System.out.println("pull " + robot.getId());
            }
        }
        Boat[] boats = frame.getBoats();
        for (Boat boat : boats) {
            int act = boat.getAction();
            if (act == 0) {
                continue;
            } else if (act == 1) {
                System.out.println("ship " + boat.getId() + " " + boat.getTargetBerthId());
            } else if (act == 2) {
                System.out.println("go " + boat.getId());
            }
        }
        System.out.println("OK");
        System.out.flush();
    }
}