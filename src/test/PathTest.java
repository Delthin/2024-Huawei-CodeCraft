package test;

import com.huawei.codecraft.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class PathTest {
    public Frame frameInit(){
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
    @Test
    public void testNextPos(){
        Frame frame = frameInit();
        PlanPath planPath = new PlanPath.greedyPlanPath();
        planPath.plan(frame);
        Robot[] robots = frame.getRobots();
        for (Robot robot : robots) {
            if (robot.getState()==0)continue;
            Pos currentPos = robot.getPos();
            Pos nextPos = robot.getPath();
            Assert.assertEquals(nextPos.Y(), 1);
        }

    }

}
