package test;

import com.huawei.codecraft.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

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
            System.out.println("next pos is "+robot.getPath().X()+","+robot.getPath().Y());
        }
        for (Goods good : goods) {
            int value = good.getValue();
            System.out.println("Goods "+value+" is at "+good.getPos().X()+","+good.getPos().Y());
        }
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
            Pos nextPos = robot.getPath();
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
        return frame;
    }
    @Test
    public void testObstacle(){
        Frame frame = frameObstacleInit();
        PlanPath planPath = new PlanPath.aStarPlanPath();
        planPath.plan(frame);
        Robot[] robots = frame.getRobots();
        printPosOfRobotsAndGoods(frame);
        for (Robot robot : robots) {
            if (robot.getState()==0)continue;
            Pos currentPos = robot.getPos();
            Pos nextPos = robot.getPath();
            Assert.assertEquals(2, nextPos.Y());
        }
    }
}
