package test;

import com.huawei.codecraft.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class PathTest {
    public static Frame frameSimpleInit(){
        Map map = MapUtils.mapInit();
        Frame frame = new Frame(1, map);
        Goods[] goods = new Goods[10];
        for (int i = 0; i < 10; i++) {
            goods[i] = new Goods(i, 0, 100, 1);
        }
        Robot[] robots = new Robot[Cons.MAX_ROBOT];
        for (int i = 0; i < Cons.MAX_ROBOT; i++) {
            robots[i] = new Robot(i,0,i,5,1);
            robots[i].assignTargetGoods(goods[i]);
        }
        frame.updateRobots(robots);
        frame.updateGoods(goods);
        return frame;
    }
    public static void printPosOfRobotsAndGoods(Frame frame){
        Robot[] robots = frame.getRobots();
        Goods[] goods = frame.getGoods();
        for (Robot robot : robots) {
            int id = robot.getId();
            System.out.println("Robot "+id+" is at "+robot.getPos().X()+","+robot.getPos().Y());
            System.out.println("next pos is "+robot.getNextPos().X()+","+robot.getNextPos().Y());
        }
        for (Goods good : goods) {
            int value = good.getValue();
            System.out.println("Goods "+value+" is at "+good.getPos().X()+","+good.getPos().Y());
        }
    }
    public static void printPath(Robot robot){
        System.out.println("Robot "+robot.getId()+" path is " + robot.getPathList());
    }
    @Test
    public void testNextPos(){
        Frame frame = frameSimpleInit();
        PlanPath planPath = new PlanPath.aStarPlanPath();
        planPath.plan(frame);
        Robot[] robots = frame.getRobots();
        printPosOfRobotsAndGoods(frame);
        for (Robot robot : robots) {
            if (robot.getState()==0)continue;
            Pos currentPos = robot.getPos();
            Pos nextPos = robot.getNextPos();
            Assert.assertEquals(nextPos.Y(), 4);
        }

    }
    public static Frame frameObstacleInit(){
        Map map = MapUtils.mapInit();
        MapUtils.mapSetObstacle(map, new Pos(0, 1));
        MapUtils.mapSetObstacle(map, new Pos(1, 1));
        MapUtils.mapSetObstacle(map, new Pos(2, 1));
        MapUtils.mapSetObstacle(map, new Pos(3, 1));
        MapUtils.mapSetObstacle(map, new Pos(4, 1));
        MapUtils.mapSetObstacle(map, new Pos(5, 1));
        MapUtils.mapSetObstacle(map, new Pos(6, 1));
        MapUtils.mapSetObstacle(map, new Pos(7, 1));
        MapUtils.mapSetObstacle(map, new Pos(8, 1));
        MapUtils.mapSetObstacle(map, new Pos(9, 1));
        Frame frame = new Frame(1, map);
        Goods[] goods = new Goods[10];
        for (int i = 0; i < 10; i++) {
            goods[i] = new Goods(i, 0, 100, 1);
        }
        Robot[] robots = new Robot[Cons.MAX_ROBOT];
        for (int i = 0; i < Cons.MAX_ROBOT; i++) {
            robots[i] = new Robot(i,0,i,2,1);
            robots[i].assignTargetGoods(goods[i]);
        }
        frame.updateRobots(robots);
        frame.updateGoods(goods);
        frame.updateMap();
        return frame;
    }
    @Test
    public void testObstacle(){
        Frame frame = frameObstacleInit();
        PlanPath planPath = new PlanPath.BidirectionalAStar();
        planPath.plan(frame);
        Robot[] robots = frame.getRobots();
        MapUtils.mapPrint(frame.getMap());
        printPosOfRobotsAndGoods(frame);
        for (Robot robot : robots) {
            if (robot.getState()==0)continue;
            System.out.print("Robot "+robot.getId()+" path is " + robot.getPathList());
            while(!robot.getPathList().isEmpty()){
                System.out.print("Step once, next pos is "+robot.getNextPos());
                robot.stepOnce();
                System.out.println();
            }
            System.out.println("Robot "+robot.getId()+" path is " + robot.getPathList());
            Pos currentPos = robot.getPos();
            Pos nextPos = robot.getNextPos();
            Assert.assertEquals(19, nextPos.Y());
        }
    }
    public static Frame frameBlockInit(){
        Map map = MapUtils.mapInit();
        for (int i = 0; i < Cons.MAP_SIZE; i++){
            for (int j = 0; j < Cons.MAP_SIZE; j++){
                map.setArea(i, j, 0);
            }
        }
        Frame frame = new Frame(1, map);
        Goods[] goods = new Goods[10];
        for (int i = 0; i < 10; i++) {
            goods[i] = new Goods(i, 0, 100, 1);
        }
        Robot[] robots = new Robot[Cons.MAX_ROBOT];
        for (int i = 0; i < Cons.MAX_ROBOT; i++) {
            robots[i] = new Robot(i,0,i,25,1);
            robots[i].assignTargetGoods(goods[i]);
        }
        frame.updateRobots(robots);
        frame.updateGoods(goods);
        Block.blocks[0] = new Block(0);
        Block.blocks[1] = new Block(1);
        ArrayList neighbours0 = new ArrayList();
        ArrayList n0 = new ArrayList();
        n0.add(Block.blocks[1]);
        neighbours0.add(n0);
        Block.blocks[0].setNeighbours(neighbours0);
        ArrayList neighbours1 = new ArrayList();
        ArrayList n1 = new ArrayList();
        n1.add(Block.blocks[0]);
        neighbours1.add(n1);
        Block.blocks[1].setNeighbours(neighbours1);
        ArrayList borders0 = new ArrayList<>();
        ArrayList b0 = new ArrayList<>();
        for (int i = 0; i < Cons.BLOCK_SIZE; i++) {
            b0.add(new Pos(0,24));
        }
        borders0.add(b0);
        Block.blocks[0].setBordersRight(borders0);
        ArrayList borders1 = new ArrayList<>();
        ArrayList b1 = new ArrayList<>();
        for (int i = 0; i < Cons.BLOCK_SIZE; i++) {
            b1.add(new Pos(0,25));
        }
        borders1.add(b1);
        Block.blocks[1].setBordersLeft(borders1);
        return frame;
    }
    @Test
    public void testBlock(){
        Frame frame = frameBlockInit();
        AssignTarget assignTarget = new AssignTarget.blockAssignTarget();
        PlanPath planPath = new PlanPath.blockPlanPath();
        assignTarget.assign(frame);
        planPath.plan(frame);
        Robot[] robots = frame.getRobots();
        printPosOfRobotsAndGoods(frame);
        for (Robot robot : robots) {
            printPath(robot);
            Pos pos = robot.getPos();
            int direction = Cons.DIRECTION_LEFT;
            if (Block.atBorder(pos, direction)) {
                if (robot.getBlocksList().isEmpty()) {
                    robot.setNextBlock(null);
                } else {
                    robot.setNextBlock((Block) robot.getBlocksList().remove(0));
                }
                robot.setPathList(null);
                Pos nextPos = new Pos(pos.X() + Cons.dx[direction], pos.Y() + Cons.dy[direction]);
                robot.setPath(nextPos);
            }
            robot.setPos(robot.getNextPos());
        }
        assignTarget.assign(frame);
        planPath.plan(frame);
        printPosOfRobotsAndGoods(frame);
        for (Robot robot : robots) {
            printPath(robot);

        }
    }
}
