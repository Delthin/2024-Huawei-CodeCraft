package com.huawei.codecraft;


import java.util.ArrayList;

/**
 * RobotStrategy
 * 决定机器人策略
 */

public class RobotStrategy {
    /**
     * 机器人策略
     * 1.分配机器人目标 a.如果机器人有货物，将货物送到目的地 b.如果机器人没有货物，选择最近的货物
     * 2.为每个机器人规划路径，可能包含碰撞处理（CBS基于冲突的搜索算法）
     * 3.决定机器人指令 a.当前处在货物且空闲，捡起货物 b.当前处在停泊点且携带货物，放下货物 c.移动到下一个位置 d.可能停在原地规避碰撞
     * 4.将机器人指令写入Robot类，等待输出（可能修改输出部分，输出到输出缓存区）
     *
     * @param frame
     */
    public static void process(Frame frame) {
        AssignTarget assignTarget = new AssignTarget.planPathAssignTarget();
        PlanPath planPath = new PlanPath.BFSPlanPath();
        assignTarget.assign(frame);
        planPath.plan(frame);
        //foolishConflictDetect(frame);
        decideInstruction(frame);
    }

    /**
     * 决定机器人指令并且写入Robot类
     * a.当前处在货物且空闲，捡起货物
     * b.当前处在停泊点且携带货物，放下货物
     * c.移动到下一个位置
     * d.可能停在原地规避碰撞
     * 注意：同一个机器人和轮船可以在同一帧内执行多条指令，因此，机器人可以同一帧内移动后立刻取货
     *
     * @param frame
     */
    private static void decideInstruction(Frame frame) {
        for (Robot robot : frame.getRobots()) {
            if (robot.getState() == 0) continue;

            Pos currentPos = robot.getPos();
            Pos nextPos = robot.getNextPos();

            int direction = getMovementDirection(currentPos, nextPos);
//            if (direction == 100) {
//                //if(frame.getFrameNumber()<3000) {
//                System.err.println(frame.getFrameNumber());
//                System.err.println(robot.getId());
//                System.err.println(robot.getPathList());
//                System.err.println(nextPos);
//                System.err.println(currentPos);
//                System.err.println(robot.getTargetPos());
//
//                //}
//                robot.waitRecover();
//                direction = -1;
//            }
            robot.setDirection(direction);


            Goods currentGoods = robot.getGoods();//机器人当前手上的物品
            Goods target = robot.getTargetGoods();//目标物品
            Map map = frame.getMap();

            if (currentGoods == null && target!=null && nextPos!=null && nextPos.equals(target.getPos()) || currentGoods == null && target!=null && currentPos.equals(target.getPos())) {

            //if (currentGoods == null && nextPos != null && map.isGoods(nextPos) || currentGoods == null && target != null && currentPos.equals(target.getPos())) {
                // 当前处在货物上且空闲，捡起货物
                robot.pickUpGoods(target);// todo:如何得到此地的goods对象
            } else if (robot.isHasGoods() && nextPos != null && map.isBerth(nextPos)) {
                // 当前处在停泊点上且携带货物，放下货物
                robot.putDownGoods(frame.getFrameNumber());
                Berth berth = nextPos.berth;
                berth.addGoodsNum();
            }

        }

    }

    private static int getMovementDirection(Pos currentPos, Pos nextPos) {
        int currentX = currentPos.X();
        int currentY = currentPos.Y();
        if (nextPos == null) {
            return -1; // 停止
        }
        int nextX = nextPos.X();
        int nextY = nextPos.Y();
        if (currentX == nextX - 1 && currentY == nextY) {
            return 3;
        } else if (currentX == 1 + nextX && currentY == nextY) {
            return 2;
        } else if (currentY == nextY - 1 && currentX == nextX) {
            return 0;
        } else if (currentY == nextY + 1 && currentX == nextX) {
            return 1;
        } else if (currentY == nextY && currentX == nextX) {
            return -1;
        } else {
            //return Math.abs(currentX-nextX)+Math.abs(currentY-nextY)+100;
            //System.err.println("FRAME:"+Main.frameNumberLocal+"  curr:"+currentPos+"  next:"+nextPos);
            return -1;
        }

    }

    private static void foolishConflictDetect(Frame frame) {
        Pos nextPoses[] = new Pos[Cons.MAX_ROBOT];
        for (Robot robot : frame.getRobots()) {
            if (robot.getState() == 0) continue;
            nextPoses[robot.getId()] = robot.getNextPos();
        }
        //需要找到nextPos里相同的点，并对每一组冲突点保留第一个，其他退回pathlist里并将robot的nextPos设为null
        for (int i = 0; i < Cons.MAX_ROBOT; i++) {
            if (nextPoses[i] == null) {
                continue;
            }
            for (int j = i + 1; j < Cons.MAX_ROBOT; j++) {
                if (nextPoses[j] == null) {
                    continue;
                }
                if (nextPoses[i].equals(nextPoses[j])) {
                    Robot robot = frame.getRobots()[j];
                    Pos nextPos = nextPoses[j];
                    if (robot.getPathList() == null) {
                        robot.setPathList(new ArrayList<>());
                    }
                    robot.getPathList().add(0, nextPos);
                    robot.setPath(null);
                }
            }
        }
    }
}
