package test;

import com.huawei.codecraft.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class PathTest {
    public static Frame frameInit(){
        Frame frame = new Frame(1);
        Robot[] robots = new Robot[Cons.MAX_ROBOT];
        for (int i = 0; i < Cons.MAX_ROBOT; i++) {
            robots[i] = new Robot(i,0,i,0,1);
        }
        Goods[] goods = new Goods[5];
        for (int i = 0; i < 5; i++) {
            goods[i] = new Goods(i, 5, 100, 1);
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
        }
        for (Goods good : goods) {
            int value = good.getValue();
            System.out.println("Goods "+value+" is at "+good.getPos().X()+","+good.getPos().Y());
        }
    }
    @Test
    public void testNextPos(){
        Frame frame = frameInit();
        PlanPath planPath = new PlanPath.greedyPlanPath();
        planPath.plan(frame);
        Robot[] robots = frame.getRobots();
        printPosOfRobotsAndGoods(frame);
        for (Robot robot : robots) {
            if (robot.getState()==0)continue;
            Pos currentPos = robot.getPos();
            Pos nextPos = robot.getPath();
            Assert.assertEquals(nextPos.Y(), 1);
        }

    }

}
